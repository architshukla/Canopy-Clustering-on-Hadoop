/**
  * @author Archit Shukla
  * @version 1.0
  * Package to compute new Cluster Centers
  */
package ClusterCenter;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.conf.Configuration;

import DataPoint.TemperatureDataPoint;

/**
  * Driver class for the package. Initializes the MapReduce job to find Cluster Centers.
  */
public class ClusterCenterDriver
{
	/**
	  * ArrayLists holding k-Means Centriods before and after an iteration. Used to check convergence.
	  */
	public static ArrayList<TemperatureDataPoint> oldKCentroids, newKCentroids;

	/**
	  * Name of the output part file.
	  */
	public static final String partFile = "/part-r-00000";


	/**
	  * Checks if k-Means Centroids have converged
	  * Parameters: String oldKCentroidsFile, String path to the old k-Means Centroid File
	  * 			String newKCentroidsFile, String path to the new k-Means Centroid File
	  * Returns:	Nothing
	  *
	  * Converts the String file names to Path objects.
	  * Then, the files are read from the FileSystem and parsed.
	  * The oldKCentroids and newKCentroids are populated and checked if they are within the defined thresholds.
	  */
	public static boolean hasConverged(String oldKCentroidsFile, String newKCentroidsFile)
		throws Exception
	{
		// String to hold lines read from files
		String line;

		// Allocate memory for oldKCentroids and newKCentroids
		oldKCentroids = new ArrayList<TemperatureDataPoint>();
		newKCentroids = new ArrayList<TemperatureDataPoint>();

		// Create a new Configuration and obtain a handle on the FileSystem
		Configuration configuration = new Configuration();
		FileSystem filesystem = FileSystem.get(configuration);

		// Read the Old k-Means Centroid File
		if(TemperatureDataPoint.NUM_ITERATIONS == 0)
		{
			// If this is the first iteration, the centroids lie in the k-Means Centroid input file
			BufferedReader oldReader = new BufferedReader(new InputStreamReader(filesystem.open
				(new Path(configuration.get("fs.default.name") + oldKCentroidsFile))));

			line = oldReader.readLine();
			while(line != null)
			{
				oldKCentroids.add(new TemperatureDataPoint(line));
				line = oldReader.readLine();
			}
		}
		else
		{
			// If this is not the first iteration, the centroids lie in a part file
			BufferedReader oldReader = new BufferedReader(new InputStreamReader(filesystem.open
				(new Path(configuration.get("fs.default.name") + oldKCentroidsFile + partFile))));
			
			line = oldReader.readLine();
			while(line != null)
			{
				// Since the output is a (key, value) pair, the key is ignored
				int tabPosition = line.indexOf("\t");
				oldKCentroids.add(new TemperatureDataPoint(line.substring(tabPosition + 1)));
				line = oldReader.readLine();
			}
		}

		// Read the New k-Means Centroid File
		BufferedReader newReader = new BufferedReader(new InputStreamReader(filesystem.open(
			new Path(configuration.get("fs.default.name") + newKCentroidsFile + partFile))));
		line = newReader.readLine();
		while(line != null)
		{
			// Since the file contains a (key, value) pair, the key is ignored
			int tabPosition = line.indexOf("\t");
			newKCentroids.add(new TemperatureDataPoint(line.substring(tabPosition + 1)));
			line = newReader.readLine();
		}

		// Check if the corresponding Old and New Centroids have converged
		for(int i = 0; i < oldKCentroids.size(); i++)
		{
			if(oldKCentroids.get(i).complexDistance(newKCentroids.get(i)) > TemperatureDataPoint.CONVERGENCE_THRESHOLD)
				return false;
		}
		return true;
	}

	/**
	  * Copies the final k-Means Centroids file to a the folder given by the parameter
	  * Parameters:	String outputFolderName, name of the output folder
	  * Returns:	Nothing
	  * 
	  * Uses the the copy method of FileUtil class to copy the converged centroids into the output path given.
	  */
	public static void copyFinalCentroidsFile(String outputFolderName)
		throws Exception
	{
		// Create a new Configuration and get a handle on the File System
		Configuration configuration = new Configuration();
		FileSystem filesystem = FileSystem.get(configuration);

		// Use the copy method of FileUtil to copy the final k-Means Centroid file to a known output file
		FileUtil.copy(filesystem, new Path(configuration.get("fs.default.name") + outputFolderName + "_" + (TemperatureDataPoint.NUM_ITERATIONS-1)), filesystem, 
			new Path(configuration.get("fs.default.name") + outputFolderName), false, true, configuration);
	}

	public static void main(String[] args)
		throws Exception
	{
		// Check if a sufficient number of arguments are provided
		// args[0] = Path to file containing the pairs (Cluster Center, Data Point), output of ClusterAssign package
		// args[1] = Path to file containing a list of canopy centers produced by the CanopyCenter package
		// args[2] = Path to file containing intital k Centroids
		// args[3] = Path to output file
		if(args.length != 4)
		{
			System.out.println("Usage: ClusterCenterDriver <input path> <canopy centers file> <k centroids file> <output path>");
			System.exit(-1);
		}

		while(true)
		{
			// Add parameter to the configuration - Path to Cluster Centers and k Centroid files
			Configuration configuration = new Configuration();
			configuration.set("canopyCentersFile", args[1]);
			if(TemperatureDataPoint.NUM_ITERATIONS == 0)
				configuration.set("kCentroidsFile", args[2]);
			else
				configuration.set("kCentroidsFile", args[3] + "_" + (TemperatureDataPoint.NUM_ITERATIONS-1) + partFile);
			configuration.set("NUM_ITERATIONS",TemperatureDataPoint.NUM_ITERATIONS + "");

			System.out.println("Iteration: " + TemperatureDataPoint.NUM_ITERATIONS);

			// Set up the job with the configuration defined above
			Job job = new Job(configuration);
			job.setJarByClass(ClusterCenterDriver.class);
			job.setJobName("Maximum Temperature - Cluster Centers");
		
			// Set paths for input and output files
			FileInputFormat.setInputPaths(job, new Path(args[0]));
			FileOutputFormat.setOutputPath(job, new Path(args[3] + "_" + TemperatureDataPoint.NUM_ITERATIONS));
		
			// Set the Mapper and Reducer class
			job.setMapperClass(ClusterCenterMapper.class);
			job.setReducerClass(ClusterCenterReducer.class);
		
			// Specify the class types of the key and value produced by the mapper
			job.setMapOutputKeyClass(TemperatureDataPoint.class);
			job.setMapOutputValueClass(TemperatureDataPoint.class);
		
			// Specify the class types of the key and value produced by the reducer
			job.setOutputKeyClass(IntWritable.class);
			job.setOutputValueClass(TemperatureDataPoint.class);
	
			job.waitForCompletion(true);

			// Check if the k-Means Centroids have converged
			if(TemperatureDataPoint.NUM_ITERATIONS == 0)
			{
				if(hasConverged(args[2], args[3] + "_" + TemperatureDataPoint.NUM_ITERATIONS))
					break;
			}
			else
			{
				if(hasConverged(args[3] + "_" + (TemperatureDataPoint.NUM_ITERATIONS - 1), args[3] + "_" + TemperatureDataPoint.NUM_ITERATIONS))
					break;
			}

			TemperatureDataPoint.NUM_ITERATIONS++;
		}

		// Copy the final k-Means Centroids File to a fixed location
		copyFinalCentroidsFile(args[3]);
	}
}
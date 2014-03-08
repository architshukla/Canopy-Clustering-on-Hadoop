/**
  * @author Archit Shukla
  * @version 1.0
  * Package to assign Data Points to k-Means Centroids
  */
package ClusterAssign;

import java.io.IOException;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.conf.Configuration;

import DataPoint.TemperatureDataPoint;

/**
  * Driver class for the package. Initializes the MapReduce job to assign Data Points to k-Means Centroids.
  */
public class ClusterAssignDriver
{
	public static void main(String args[])
		throws Exception
	{
		// Check if a sufficient number of arguments are provided.
		// args[0] = Path to file containing the Data Points
		// args[1] = Path to file containing the k-Means Centroids
		// args[2] = Path to output file
		if(args.length != 3)
		{
			System.out.println("Usage: ClusterAssignDriver <input path> <output path>");
			System.exit(-1);
		}

		// Create a new configuration and add the path to the k-Means Centroid file as a parameter
		Configuration configuration = new Configuration();
		configuration.set("kCentroidsFile", args[1]);

		// Set up the job
		Job job = new Job(configuration);
		job.setJarByClass(ClusterAssignDriver.class);
		job.setJobName("Maximum Temperature - Cluster Assignment");

		// Set the Mapper and Reducer class
		job.setMapperClass(ClusterAssignMapper.class);
		job.setReducerClass(ClusterAssignReducer.class);

		// Set paths for input and output files
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[2]));

		// Specify the class types of the key and value produced by the mapper and reducer
		job.setOutputKeyClass(TemperatureDataPoint.class);
		job.setOutputValueClass(TemperatureDataPoint.class);

		System.exit(job.waitForCompletion(true)?0:1);
	}
}
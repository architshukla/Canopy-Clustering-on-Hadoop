/**
  * @author Archit Shukla
  * @version 1.0
  * Package to assign Data Points to Canopy Centers
  */
package CanopyAssign;

import java.io.IOException;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.conf.Configuration;

import DataPoint.TemperatureDataPoint;

/**
  * Driver class for the package. Initializes the MapReduce job to assign Data Points to Canopy Centers.
  */
public class CanopyAssignDriver
{
	/**
	  * <b>Main function of CanopyAssignDriver. </b><br>
	  * <b>Parameters:</b>	Strings args[], arguments passed to this class.
	  * 					args[0] = Path to file containing the input.
	  * 					args[1] = Path to file containing the Canopy Centers.
	  * 					args[2] = Path to output file. <br>
	  * <b>Returns:</b>		Nothing <br><br>
	  *
	  * Initializes and runs the job to find Canopy Centers.
	  */
	public static void main(String[] args)
		throws Exception
	{
		// Check if a sufficient number of arguments are provided.
		// args[0] = Path to file containing the data points
		// args[1] = Path to file containing a list of canopy centers produced by the CanopyCenter package
		// args[2] = Path to output file
		if(args.length < 3)
		{
			System.out.println("Usage: CanopyAssignDriver <input path> <canopy centers file> <output path>");
			System.exit(-1);
		}

		// Add a parameter to the configuration - Path to Cluster Centroids File
		Configuration configuration = new Configuration();
		configuration.set("canopyCentersFile", args[1]);

		// Set up the job with the configuration defined above
		Job job = new Job(configuration);
		job.setJarByClass(CanopyAssignDriver.class);
		job.setJobName("Maximum Temperature - Canopy Center Assignment");

		// Set the Mapper and Reducer class
		job.setMapperClass(CanopyAssignMapper.class);
		job.setReducerClass(CanopyAssignReducer.class);

		// Set paths for input and output files
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[2]));

		// Specify the class types of the key and value produced by the mapper and reducer
		job.setOutputKeyClass(TemperatureDataPoint.class);
		job.setOutputValueClass(TemperatureDataPoint.class);

		System.exit(job.waitForCompletion(true)?0:1);
	}
}

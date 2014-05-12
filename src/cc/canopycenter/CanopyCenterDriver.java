/**
  * @author Archit Shukla
  * @version 1.0
  * Package to compute Canopy Centers for a gievn Data Set.
  */
package cc.canopycenter;

import java.io.IOException;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import cc.dataset.DataPoint;

/**
  * Driver class for the package. Initializes the MapReduce job to find Canopy Centers.
  */
public class CanopyCenterDriver
{
	/**
	  * <b>Main function of CanopyCenterDriver. </b><br>
	  * <b>Parameters:</b>	Strings args[], arguments passed to this class.
	  * 					args[0] = Path to file containing the input.
	  * 					args[1] = Path to output file. <br>
	  * <b>Returns:</b>		Nothing <br><br>
	  *
	  * Initializes and runs the job to find Canopy Centers.
	  */
	public static void main(String args[])
		throws IOException, InterruptedException, ClassNotFoundException
	{
		// Check if a sufficient number of arguments are provided
		// args[0] = Path to file containing the input
		// args[1] = Path to output file
		if(args.length < 2)
		{
			System.out.println("Usage: CanopyCenterDriver <input path> <output path>");
			System.exit(-1);
		}

		// Set up the job
		Job job = new Job();
		job.setJarByClass(CanopyCenterDriver.class);
		job.setJobName("Maximum Temperature - Cluster Center Selection");

		// Set input and output paths for the program
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		// Set the class for the output key and value types of the Reducer
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(DataPoint.class);

		// Set the class for the output key and value types of the Mapper
		job.setMapperClass(CanopyCenterMapper.class);
		job.setReducerClass(CanopyCenterReducer.class);

		System.exit(job.waitForCompletion(true)?0:1);
	}
}
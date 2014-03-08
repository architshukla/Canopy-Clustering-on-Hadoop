package ClusterAssign;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;

import DataPoint.TemperatureDataPoint;

/**
  * Mapper class for the Clluster Assign step
  */
public class ClusterAssignMapper extends Mapper<LongWritable, Text, TemperatureDataPoint, TemperatureDataPoint>
{
	/**
	  * ArrayList holding the k-Means Centroids read from a file
	  */
	public static ArrayList<TemperatureDataPoint> kCentroids;

	/**
	  * Overridden setup method of Mapper class
	  * Parameters:	Context context
	  * Returns:	Nothing
	  * 
	  * Reads the file containing k-Means Centroids, parses it and loads the Centroids into the ArrayList kCentroids
	  */
	@Override
	public void setup(Context context)
		throws IOException, InterruptedException
	{
		// Call setup of super class
		super.setup(context);

		// Allocate memory for kCentroids
		kCentroids = new ArrayList<TemperatureDataPoint>();

		// Get the context's configuration
		Configuration configuration = context.getConfiguration();

		// Open the file for reading
		Path path = new Path(configuration.get("fs.default.name") + configuration.get("kCentroidsFile"));
		FileSystem filesystem = FileSystem.get(configuration);
		BufferedReader reader = new BufferedReader(new InputStreamReader(filesystem.open(path)));

		String line = reader.readLine();
		while(line != null)
		{
			// The first character is 1, the second is \t, All the other characters are parsed to an object of TemperatureDataPoint
			TemperatureDataPoint centroid =  new TemperatureDataPoint(line.substring(2));
			kCentroids.add(centroid);
			line = reader.readLine();
		}
	}

	/**
	  * Overridden map function of Mapper Class
	  * Parameters:	LongWritable key, an offset in the file
	  * 			Text value, Data Points in a string format
	  *				Context context
	  * Returns:	(key, value) pairs where
	  *				key is a Canopy Center associated with the current Data Point
	  *				value is the Data Point being considered
	  * 
	  * The function receives a (key, value) pair and nparses it.
	  * For each k-Means Centroid in kCentroids, it calculates the centroid with the minimum distance.
	  * It outputs the pair (K-Centroid with minimum distance, Data Point)
	  */
	@Override 
	public void map(LongWritable key, Text value, Context context)
		throws IOException, InterruptedException
	{
		// Parse the Data Point
		TemperatureDataPoint dataPoint = new TemperatureDataPoint(value.toString());

		// Set up variables to find the centroid with minimum distance
		double minDistance = Double.MAX_VALUE;
		double distance;
		int offset = -1;

		// Find centroid with minimum distance
		for(int i = 0; i < kCentroids.size(); i++)
		{
			distance = dataPoint.complexDistance(kCentroids.get(i));
			if(distance < minDistance)
			{
				minDistance = distance;
				offset = i;
			}
		}

		// Write the pair (Centroid, Data Point)
		context.write(kCentroids.get(offset), dataPoint);
	}
}
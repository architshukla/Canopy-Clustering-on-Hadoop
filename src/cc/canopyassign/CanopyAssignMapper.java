package cc.canopyassign;

import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import cc.dataset.DataPoint;

/**
  * Mapper class for the Canopy Assign step.
  */
public class CanopyAssignMapper extends Mapper<LongWritable, Text, DataPoint, DataPoint>
{
	/**
	  * ArrayList holding the Canopy Centers read from a file.
	  */
	public static ArrayList<DataPoint> canopyCenters;

	/**
	  * Overridden setup method of Mapper class.
	  * Reads the file containing canopy centers, parses it and loads the Canopy Centers into the ArrayList canopyCenters.
	  *
	  * @param context Context object.
	  */
	@Override
	public void setup(Context context)
		throws IOException, InterruptedException
	{
		// Call setup of super class
		super.setup(context);

		// Allocate memory for canopyCenters
		canopyCenters = new ArrayList<DataPoint>();

		// Get the context's configuration
		Configuration configuration = context.getConfiguration();

		// Open the file for reading
		Path path = new Path(configuration.get("fs.default.name") + configuration.get("canopyCentersFile"));
		FileSystem filesystem = FileSystem.get(configuration);
		BufferedReader reader = new BufferedReader(new InputStreamReader(filesystem.open(path)));

		String line = reader.readLine();
		while(line != null)
		{
			// The first character is 1, the second is \t, All the other characters are parsed to an object of DataPoint
			DataPoint canopyCenter =  new DataPoint(line.substring(2));
			canopyCenters.add(canopyCenter);
			line = reader.readLine();
		}
	}

	/**
	  * Overridden map function of Mapper Class.
	  * The function receives a (key, value) pair, parses it and checks if any Canopy Center is within T1 distance of this Data Point.
	  * For every such point, it outputs (key, value) pairs where, 
	  *	key is a Canopy Center associated with the current Data Point, 
	  *	value is the Data Point being considered.
	  *
	  * @param key An offset in the input file.
	  * @param value DataPoint objects in a string format.
	  *	@param context Context object.
	  */
	@Override 
	public void map(LongWritable key, Text value, Context context)
		throws IOException, InterruptedException
	{
		// Convert the value to an object of Data Point
		DataPoint dataPoint = new DataPoint(value.toString());

		// For each Canopy Center, check if it is within T1 distance of Canopy Center
		for(DataPoint canopyCenter : canopyCenters)
		{
			// If the Data Point and a Canopy Center are within T1 distance of each other, write the pair (Canopy Center, Data Point)
			if(dataPoint.withinT1(canopyCenter))
				context.write(canopyCenter, dataPoint);
		}
	}
}
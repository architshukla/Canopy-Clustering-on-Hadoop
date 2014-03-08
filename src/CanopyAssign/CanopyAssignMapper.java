package CanopyAssign;

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

import DataPoint.TemperatureDataPoint;

/**
  * Mapper class for the Canopy Assign step.
  */
public class CanopyAssignMapper extends Mapper<LongWritable, Text, TemperatureDataPoint, TemperatureDataPoint>
{
	/**
	  * ArrayList holding the Canopy Centers read from a file.
	  */
	public static ArrayList<TemperatureDataPoint> canopyCenters;

	/**
	  * <b>Overridden setup method of Mapper class. </b><br>
	  * <b>Parameters:</b>	Context context <br>
	  * <b>Returns:</b>		Nothing <br><br>
	  * 
	  * Reads the file containing canopy centers, parses it and loads the Canopy Centers into the ArrayList canopyCenters.
	  */
	@Override
	public void setup(Context context)
		throws IOException, InterruptedException
	{
		// Call setup of super class
		super.setup(context);

		// Allocate memory for canopyCenters
		canopyCenters = new ArrayList<TemperatureDataPoint>();

		// Get the context's configuration
		Configuration configuration = context.getConfiguration();

		// Open the file for reading
		Path path = new Path(configuration.get("fs.default.name") + configuration.get("canopyCentersFile"));
		FileSystem filesystem = FileSystem.get(configuration);
		BufferedReader reader = new BufferedReader(new InputStreamReader(filesystem.open(path)));

		String line = reader.readLine();
		while(line != null)
		{
			// The first character is 1, the second is \t, All the other characters are parsed to an object of TemperatureDataPoint
			TemperatureDataPoint canopyCenter =  new TemperatureDataPoint(line.substring(2));
			canopyCenters.add(canopyCenter);
			line = reader.readLine();
		}
	}

	/**
	  * <b>Overridden map function of Mapper Class. </b><br>
	  * <b>Parameters:</b>	LongWritable key, an offset in the file. <br>
	  * 			Text value, Data Points in a string format.
	  *				Context context. <br>
	  * <b>Returns:</b>	(key, value) pairs where, 
	  *				key is a Canopy Center associated with the current Data Point, 
	  *				value is the Data Point being considered. <br><br>
	  * 
	  * The function receives a (key, value) pair, parses it and checks if any Canopy Center is within T1 distance of this Data Point.
	  * For every such point, it outputs the pair (Canopy Center, Data Point).
	  */
	@Override 
	public void map(LongWritable key, Text value, Context context)
		throws IOException, InterruptedException
	{
		// Convert the value to an object of Data Point
		TemperatureDataPoint dataPoint = new TemperatureDataPoint(value.toString());

		// For each Canopy Center, check if it is within T1 distance of Canopy Center
		for(TemperatureDataPoint canopyCenter : canopyCenters)
		{
			// If the Data Point and a Canopy Center are within T1 distance of each other, write the pair (Canopy Center, Data Point)
			if(dataPoint.withinT1(canopyCenter))
				context.write(canopyCenter, dataPoint);
		}
	}
}
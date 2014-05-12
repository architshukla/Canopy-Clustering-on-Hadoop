package cc.canopycenter;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import cc.dataset.DataPoint;

/**
  * Mapper class for the Canopy Center finding step.
  */
public class CanopyCenterMapper
	extends Mapper<LongWritable, Text, IntWritable, DataPoint>
{
	/**
	  * ArrayList containing the Canopy Centers found so far.
	  */
	public static ArrayList<DataPoint> canopyCenters;
	
	/**
	  * <b>Overridden setup method of Mapper class. </b><br>
	  * <b>Parameters:</b>	Context context <br>
	  * <b>Returns:</b>		Nothing <br><br>
	  * 
	  * Calls the super class setup method and allocates memory for the canopyCenters ArrayList.
	  */
	@Override
	public void setup(Context context)
		throws IOException, InterruptedException
	{
		// Call the setup method of super class
		super.setup(context);

		// Allocate memory for the canopyCenters ArrayList
		canopyCenters = new ArrayList<DataPoint>();
	}

	/**
	  * <b>Overridden map function of Mapper Class. </b><br>
	  * <b>Parameters:</b>	LongWritable key, an offset in the file. 
	  * 					Text value, A Data Point String. 
	  *						Context context. <br>
	  * <b>Returns:</b>		(key, value) pairs where, 
	  *						key is a IntWritable with value 1, 
	  *						value is the Data Point being considered if it is a local Canopy Center. <br><br>
	  * 
	  * Receives a (key, value) pair, parses it into the Data Point.
	  * If this point is within T2 distance of any canopy center found so far, it is ignored.
	  * Otherwise, it is added into the canopyCenters ArrayList and written to the output as the pair (1, Canopy Center).
	  */
	@Override
	public void map(LongWritable key, Text value, Context context)
		throws IOException, InterruptedException
	{
		// Create a new Data Point object for the parameter named value
		DataPoint dataPoint = new DataPoint(value.toString());
		
		// If canopyCenters is empty
		if(canopyCenters.size() == 0)
		{
			// Add the Data Point at the end of the list
			canopyCenters.add(dataPoint);

			// Write (1, Data Point) as output
			context.write(new IntWritable(1), dataPoint);
		}
		else
		{
			boolean insert = true;

			// Iterate through all Canopy Centers found so far
			for(DataPoint center : canopyCenters)
			{
				// If any Canopy Center is within T2 distnace of this Data Point
				if(dataPoint.withinT2(center))
				{
					// It cannot be a Canopy Center
					insert = false;
					break;
				}	
			}

			// If the Data Point is a Canopy Center
			if(insert)
			{
				// Add the Data Point to the list of Canopy Centers
				canopyCenters.add(dataPoint);

				// Write (1, Data Point) as output
				context.write(new IntWritable(1), dataPoint);
			}
		}
	}
}
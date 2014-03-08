package CanopyAssign;

import java.io.IOException;

import org.apache.hadoop.mapreduce.Reducer;

import DataPoint.TemperatureDataPoint;

/**
  * Reducer class for the Canopy Assign step.
  */
public class CanopyAssignReducer extends Reducer<TemperatureDataPoint, TemperatureDataPoint, TemperatureDataPoint, TemperatureDataPoint>
{
	/**
	  * <b>Overridden reduce method of the Reduce class. </b><br>
	  * <b>Parameters:</b>	TemperatureDataPoint key, a Canopy Center.
	  * 					Iterable<TemperatureDataPoint> value, A list of Data Points associated to this Canopy Center.
	  *						Context context. <br>
	  * <b>Returns:</b>		(key, value) pairs where, 
	  *						key is a Canopy Center associated with the current Data Point, 
	  *						value is one of the Data Points in the Iterable list. <br><br>
	  * 
	  * The function receives a (key, value) pair, and for every point in the Iterable list, it outputs the pair (Canopy Center, Data Point).
	  */
	@Override
	public void reduce(TemperatureDataPoint key, Iterable<TemperatureDataPoint> values, Context context)
		throws IOException, InterruptedException
	{
		// For each Data Point in this Canopy Center, write the pair (Canopy Center, Data Point) to output
		for(TemperatureDataPoint dataPoint : values)
			context.write(key, dataPoint);
	}
}
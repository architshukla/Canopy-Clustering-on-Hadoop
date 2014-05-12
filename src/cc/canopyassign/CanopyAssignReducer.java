package cc.canopyassign;

import java.io.IOException;

import org.apache.hadoop.mapreduce.Reducer;

import cc.dataset.DataPoint;

/**
  * Reducer class for the Canopy Assign step.
  */
public class CanopyAssignReducer extends Reducer<DataPoint, DataPoint, DataPoint, DataPoint>
{
	/**
	  * Overridden reduce method of the Reduce class.
	  * The function receives a (key, value) pair, and for every point in the Iterable list
	  * It outputs (key, value) pairs where, 
	  *	key is a Canopy Center associated with the current Data Point, 
	  *	value is one of the Data Points in the Iterable list.
	  *
	  * @param key A Canopy Center.
	  * @param value A list of Data Points associated to this Canopy Center.
	  *	@param context Context object. 
	  */
	@Override
	public void reduce(DataPoint key, Iterable<DataPoint> values, Context context)
		throws IOException, InterruptedException
	{
		// For each Data Point in this Canopy Center, write the pair (Canopy Center, Data Point) to output
		for(DataPoint dataPoint : values)
			context.write(key, dataPoint);
	}
}
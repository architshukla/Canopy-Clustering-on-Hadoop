package ClusterAssign;

import java.io.IOException;

import org.apache.hadoop.mapreduce.Reducer;

import DataPoint.TemperatureDataPoint;

/**
  * Reducer class for the Cluster Assign step
  */
public class ClusterAssignReducer extends Reducer<TemperatureDataPoint, TemperatureDataPoint, TemperatureDataPoint, TemperatureDataPoint>
{
	/**
	  * Overridden reduce method of the Reduce class
	  * Parameters:	TemperatureDataPoint key, a k-Means Centroid
	  * 			Iterable<TemperatureDataPoint> value, A list of Data Points associated to this Centriod
	  *				Context context
	  * Returns:	(key, value) pairs where
	  *				key is a k-Means Centroid associated with the current Data Point
	  *				value is one of the Data Points in the Iterable list
	  * 
	  * The function receives a (key, value) pair.
	  * For every point in the Iterable list, it outputs the pair (k_Means Centroid, Data Point).
	  */
	@Override
	public void reduce(TemperatureDataPoint key, Iterable<TemperatureDataPoint> values, Context context)
		throws IOException, InterruptedException
	{
		for(TemperatureDataPoint dataPoint : values)
			context.write(key, dataPoint);
	}
}
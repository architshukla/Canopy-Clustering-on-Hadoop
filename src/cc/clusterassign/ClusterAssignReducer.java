package cc.clusterassign;

import java.io.IOException;

import org.apache.hadoop.mapreduce.Reducer;

import cc.dataset.DataPoint;

/**
  * Reducer class for the Cluster Assign step
  */
public class ClusterAssignReducer extends Reducer<DataPoint, DataPoint, DataPoint, DataPoint>
{
	/**
	  * Overridden reduce method of the Reduce class
	  * Parameters:	DataPoint key, a k-Means Centroid
	  * 			Iterable<DataPoint> value, A list of Data Points associated to this Centriod
	  *				Context context
	  * Returns:	(key, value) pairs where
	  *				key is a k-Means Centroid associated with the current Data Point
	  *				value is one of the Data Points in the Iterable list
	  * 
	  * The function receives a (key, value) pair.
	  * For every point in the Iterable list, it outputs the pair (k_Means Centroid, Data Point).
	  */
	@Override
	public void reduce(DataPoint key, Iterable<DataPoint> values, Context context)
		throws IOException, InterruptedException
	{
		for(DataPoint dataPoint : values)
			context.write(key, dataPoint);
	}
}
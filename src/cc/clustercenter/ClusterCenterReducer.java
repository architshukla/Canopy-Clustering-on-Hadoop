package cc.clustercenter;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

import cc.dataset.DataPoint;

/**
  * Reducer class for the Cluster Center iteration step.
  */
public class ClusterCenterReducer extends Reducer<DataPoint, DataPoint, IntWritable, DataPoint>
{
	/**
	 * Output key for the k-Means Centroid
	 */
	public static int CENTROID_KEY = 1;
	/**
	  * Overridden reduce method of the Reduce class
	  * Parameters:	DataPoint key, a Canopy Center
	  * 			Iterable<DataPoint> value, A list of Data Points associated to this Canopy Center
	  *				Context context
	  * Returns:	(key, value) pairs where
	  *				key is a k-Means Cluster Centroid
	  *				value is one of the Data Points in the Iterable list
	  * 
	  * The function receives a (key, value) pair, where 
	  * the key is a k-Means Cluster Centroid
	  * the value is a Iterable list of Data Points in this Cluster
	  * It calculates the average of all the Data Points as the new Cluster Centroid
	  * It outputs the pair (1, New Cluster Centroid, that is Average of all Data Points)
	  */
	@Override
	public void reduce(DataPoint key, Iterable<DataPoint> values, Context context)
		throws IOException, InterruptedException
	{
		// Find average Data Point and output it
		DataPoint dataPoint = DataPoint.getAverageDataPoint(values);
		context.write(new IntWritable(ClusterCenterReducer.CENTROID_KEY), dataPoint);

		// Increment the centroid key
		ClusterCenterReducer.CENTROID_KEY++;
	}
}
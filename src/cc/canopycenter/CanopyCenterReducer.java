package cc.canopycenter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import cc.dataset.DataPoint;

/**
  * Reducer class for the Canopy Center finding step.
  */
public class CanopyCenterReducer extends Reducer<IntWritable, DataPoint, IntWritable, DataPoint>
{
	/**
	  * ArrayList containing the Canopy Centers found so far.
	  */
	public static ArrayList<DataPoint> canopyCenters;

	/**
	  * <b>Overridden setup method of Reducer class. </b><br>
	  * <b>Parameters:</b>	Context context <br>
	  * <b>Returns:</b>		Nothing <br><br>
	  * 
	  * Calls the super class setup method and allocates memory for the canopyCenters ArrayList.
	  */
	@Override
	public void setup(Context context)
		throws IOException, InterruptedException
	{
		super.setup(context);
		canopyCenters = new ArrayList<DataPoint>();
	}

	/**
	  * <b>Overridden reduce method of the Reduce class. </b><br>
	  * <b>Parameters:</b>	IntWritable key, IntWritable with value 1.
	  * 					Iterable<DataPoint> value, A list of all Canopy Centers.
	  *						Context context. <br>
	  * <b>Returns:</b>	(key, value) pairs where, 
	  *					key is an IntWritable with value 1, 
	  *					value is a Data Point if it is a global Canopy Center. <br><br>
	  * 
	  * The function receives a (key, value) pair, where 
	  * the key is a k-Means Cluster Centroid
	  * the value is a Iterable list of Data Points in this Cluster
	  * It calculates the average of all the Data Points as the new Cluster Centroid
	  * It outputs the pair (1, New Cluster Centroid, that is Average of all Data Points)
	  */
	@Override
	public void reduce(IntWritable key, Iterable<DataPoint> values, Context context)
		throws IOException, InterruptedException
	{

		for(DataPoint dataPoint : values)
		{
			if(canopyCenters.size() == 0)
			{
				context.write(key, dataPoint);
				canopyCenters.add(new DataPoint(dataPoint));
			}
			else
			{
				boolean insert = true;
				for(DataPoint center : canopyCenters)
				{
					if(dataPoint.withinT2(center))
					{
						insert = false;
						break;
					}
				}
				if(insert)
				{
					context.write(key, dataPoint);
					canopyCenters.add(new DataPoint(dataPoint));
				}
			}
		}

	}
}
package cc.clustercenter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
  * Mapper class for the Cluster Center iteration step.
  */
public class ClusterCenterMapper extends Mapper<LongWritable, Text, DataPoint, DataPoint>
{
	/**
	  * HashMap with keys as Canopy Centers and values as a list of k-Means Centroids associated in this Canopy.
	  */
	HashMap<DataPoint, ArrayList<DataPoint>> canopyCenterKCentroidsMap;

	/**
	  * Overridden setup method of Mapper class.
	  * Parameters:	Context context
	  * Returns:	Nothing
	  * 
	  * Reads the file containing k-Means Centroids, parses it and loads the Centroids into the ArrayList kCentroids
	  * Reads the file containing canopy centers, parses it and loads the Canopy Centers into the ArrayList canopyCenters
	  * Creates a HashMap (Canopy Center, List of Centroids in this canopy)
	  */
	@Override
	public void setup(Context context)
		throws IOException, InterruptedException
	{
		// Call setup of super class
		super.setup(context);

		// Allocate memory for kCentroids, canopyCenters and the HashMap
		ArrayList<DataPoint> kCentroids = new ArrayList<DataPoint>();
		ArrayList<DataPoint> canopyCenters = new ArrayList<DataPoint>();
		canopyCenterKCentroidsMap = new HashMap<DataPoint, ArrayList<DataPoint>>();

		// Get the context's configuration
		Configuration configuration = context.getConfiguration();

		int NUM_ITERATIONS = Integer.parseInt(configuration.get("NUM_ITERATIONS"));

		// Get a handle of the HDFS
		FileSystem filesystem = FileSystem.get(configuration);
		
		// Read the k-Means Centroid File
		BufferedReader centroidReader = new BufferedReader(new InputStreamReader(filesystem.open(
			new Path(configuration.get("fs.default.name") + configuration.get("kCentroidsFile")))));
		String line = centroidReader.readLine();
		while(line != null)
		{
			DataPoint kCentroid = null;
			if(NUM_ITERATIONS == 0)
				kCentroid =  new DataPoint(line);
			else
			{
				int tabPosition = line.indexOf("\t");
				kCentroid =  new DataPoint(line.substring(tabPosition + 1));
			}
			kCentroids.add(kCentroid);
			line = centroidReader.readLine();
		}

		// Read the Canopy Centers file
		BufferedReader canopyReader = new BufferedReader(new InputStreamReader(filesystem.open(
			new Path(configuration.get("fs.default.name") + configuration.get("canopyCentersFile")))));
		line = canopyReader.readLine();
		while(line != null)
		{
			int tabPosition = line.indexOf("\t");
			DataPoint canopyCenter =  new DataPoint(line.substring(tabPosition + 1));
			canopyCenters.add(canopyCenter);
			line = canopyReader.readLine();
		}

		// Set up the HashMap
		for(DataPoint canopyCenter : canopyCenters)
		{
			// For each Canopy Center, create an ArrayList of all Data Points within this Canopy
			ArrayList<DataPoint> centroidList = new ArrayList<DataPoint>();

			for(DataPoint kCentroid : kCentroids)
			{
				// If a k-Means Centroid is within this Canopy, add it to the ArrayList
				if(canopyCenter.withinT1(kCentroid))
						centroidList.add(kCentroid);
			}

			// Add all the k-Means Centroids in this Canopy to the HashMap as this Canopy Center's value
			if(centroidList.size() > 0)
				canopyCenterKCentroidsMap.put(canopyCenter, centroidList);
		}
	}

	/**
	  * Overridden map function of Mapper Class
	  * Parameters:	LongWritable key, an offset in the file
	  * 			Text value, A tab separated String of Canopy Center and Data Point
	  *				Context context
	  * Returns:	(key, value) pairs where
	  *				key is a Canopy Center associated with the current Data Point
	  *				value is the Data Point being considered
	  * 
	  * The function receives a (key, value) pair, parses it into the Canopy Center and Data Point.
	  * The HashMap canopyCenterKCentroidsMap is looked up with the key as the Canopy Center and a list of K-Means Centroids in obtained.
	  * For each K-Means Centroid, we find the (complex or expensive) distance of the Data Point with the Centroid.
	  * The pair (K-Means Centroid, DataPoint) with the minimum distance is written as output.
	  */
	@Override
	public void map(LongWritable key, Text value, Context context)
		throws IOException, InterruptedException
	{
		// Convert the value from Text to String 
		String keyValueString = value.toString();

		// Parse the value to get Canopy Center and Data Point
		int tabPosition = keyValueString.indexOf("\t");
		DataPoint canopyCenter = new DataPoint(keyValueString.substring(0, tabPosition));
		DataPoint dataPoint = new DataPoint(keyValueString.substring(tabPosition + 1));

		// Get the list of k-Means Centroids in this Canopy
		ArrayList<DataPoint> centroids = canopyCenterKCentroidsMap.get(canopyCenter);
		if(centroids != null)
		{
			// Set the minimum distance to the maximum value a double can hold and create
			double minDistance = Double.MAX_VALUE;
			int offset = -1;

			for(int i = 0; i < centroids.size(); i++)
			{
				DataPoint centroid = centroids.get(i);
				double distance = dataPoint.complexDistance(centroid);

				// Check if the distance is less than the minimum distance found so far
				if(distance < minDistance)
				{
					minDistance = distance;
					offset = i;
				}
			}
			context.write(centroids.get(offset), dataPoint);
		}
	}
}
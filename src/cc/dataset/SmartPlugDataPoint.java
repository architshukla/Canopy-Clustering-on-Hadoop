/**
  * @author Archit Shukla
  * @version 1.0
  */
package cc.dataset;

import java.io.IOException;
import java.io.DataInput;
import java.io.DataOutput;

import org.apache.hadoop.io.WritableComparable;

/**
  * A class to model a simple [year, temperature] Data Set.
  */
public class TemperatureDataPoint implements WritableComparable<TemperatureDataPoint>
{
	/**
	  * Attributes for the class.
	  */
	public float time, reading;
	int id1, id2, id3, id4;

	/**
	  * T1 and T2 thresholds for this Data Set.
	  */
	public final static double T1 = 5, T2 = 3;
	/**
	  * Threshold for convergence. A distance value below the specified value denotes the point has converged.
	  */
	public final static double CONVERGENCE_THRESHOLD = 1.0;

	/**
	 * The number of iterations so far.
	 */
	public static long NUM_ITERATIONS = 0;

	/**
	  * <b>Default Constructor. </b><br>
	  * <b>Parameters:</b>	None <br>
	  * <b>Returns:</b>		Nothing <br/><br>
	  *
	  * Sets the year and temperature values to 0.
	  */
	public TemperatureDataPoint()
	{
		time = reading = 0.0f;
		id1 = id2 = id3 = id4 = 0;
	}

	/**
	  * <b>Parameterized Constructor (String). </b><br>
	  * <b>Parameters:</b>	String dataPointString, a string representation of an object of the class. <br>
	  * <b>Returns:</b>		Nothing <br><br>
	  *
	  * Parses the input string and sets the appropriate fields of the object. Inverse of toString() method.
	  */
	public TemperatureDataPoint(String dataPointString)
	{
		int commaPosition1 = dataPointString.indexOf(",");
		time = Float.parseFloat(dataPointString.substring(0, commaPosition1));
		int commaPosition2 = dataPointString.indexOf(",", commaPosition1 + 1);
		reading = Float.parseFloat(dataPointString.substring(commaPosition1 + 1, commaPosition2));
		int commaPosition3 = dataPointString.indexOf(",", commaPosition2 + 1);
		id1 = Integer.parseInt(dataPointString.substring(commaPosition2 + 1, commaPosition3));
		int commaPosition4 = dataPointString.indexOf(",", commaPosition3 + 1);
		id2 = Integer.parseInt(dataPointString.substring(commaPosition3 + 1, commaPosition4));
		int commaPosition5 = dataPointString.indexOf(",", commaPosition4 + 1);
		id3 = Integer.parseInt(dataPointString.substring(commaPosition4 + 1, commaPosition5));
		id4 = Integer.parseInt(dataPointString.substring(commaPosition5 + 1));
	}

	/**
	  * <b>Copy Constructor (TemperatureDataPoint). </b><br>
	  * <b>Parameters:</b>	TemperatureDataPoint dataPoint, the reference Data Point whose fields are to be copied into this object <br>
	  * <b>Returns:</b>		Nothing <br><br>
	  *
	  * Sets this object's fields to the corresponding fields of the passed object.
	  */
	public TemperatureDataPoint(TemperatureDataPoint dataPoint)
	{
		time = dataPoint.time;
		reading = dataPoint.reading;
		id1 = dataPoint.id1;
		id2 = dataPoint.id2;
		id3 = dataPoint.id3;
		id4 = dataPoint.id4;
	}

	/**
	  * <b>write method of the Writable Interface. </b><br>
	  * <b>Parameters:</b>	DataOutput out, to write the fields of this object serially <br>
	  * <b>Returns:</b>		Nothing <br><br>
	  *
	  * Serializes the object by writing the fields in a specific serial order to out.
	  */
	public void write(DataOutput out)
		throws IOException
	{
		out.writeFloat(time);
		out.writeFloat(reading);
		out.writeInt(id1);
		out.writeInt(id2);
		out.writeInt(id3);
		out.writeInt(id4);
	}

	/**
	  * <b>readFields method of the Writable Interface. </b><br>
	  * <b>Parameters:</b> 	DataInput in, to read the fields of this object serially <br>
	  * <b>Returns:</b>		Nothing <br><br>
	  *
	  * Serially reads the fields of the object in the order they were written.
	  */
	public void readFields(DataInput in)
		throws IOException
	{
		time = in.readFloat();
		reading = in.readFloat();
		id1 = in.readInt();
		id2 = in.readInt();
		id3 = in.readInt();
		id4 = in.readInt();
	}

	/**
	  * <b>Read method. </b><br>
	  * <b>Parameters:</b>	DataInput in, to read the fields of an object serially <br>
	  * <b>Returns:</b>		Object of type TemperatureDataPoint <br>
	  * <b>Uses:</b>		void readFields(DataInput in) <br><br>
	  *
	  * Serially reads the fields of the object in the order they were written into another object and returns it.
	  */
	public TemperatureDataPoint read(DataInput in)
		throws IOException
	{
		TemperatureDataPoint dataPoint = new TemperatureDataPoint();
		dataPoint.readFields(in);
		return dataPoint;	
	}

	/**
	  * <b>compareTo method of. </b><br>
	  * <b>Parameters:</b>	TemperatureDataPoint dataPoint, the object to compare this object to <br>
	  * <b>Returns:</b>		int ,possible values are -1, 0 and 1 <br><br>
	  *
	  * Compares the fields of two objects to obtain an ordering.
	  */
	public int compareTo(TemperatureDataPoint dataPoint)
	{
		return (time < dataPoint.time ? -1 : 
			(time == dataPoint.time ? (reading < dataPoint.reading ? -1 : (reading == dataPoint.reading ? 0 : 1)) : 1));
	}

	/**
	  * <b>Checks if the distance between two Data Points is within T1. </b><br>
	  * <b>Parameters:</b>	TemperatureDataPoint dataPoint, the object to compare this object to. <br>
	  * <b>Returns:</b>		boolean <br>
	  * <b>Uses:</b>		int simpleDistance(TemperatureDataPoint). <br><br>
	  *
	  * Compares this object with the passed object to check if they are within T1 distance of each other.
	  * Returns true if they are within T1 distance, false otherwise.
	  */
	public boolean withinT1(TemperatureDataPoint dataPoint)
	{
		return (simpleDistance(dataPoint) < T1);
	}

	/**
	  * <b>Checks if the distance between two Data Points is within T1. </b><br>
	  * <b>Parameters:</b>	TemperatureDataPoint dataPoint, the object to compare this object to. <br>
	  * <b>Returns:</b>		boolean <br>
	  * <b>Uses:</b>		int simpleDistance(TemperatureDataPoint) <br>
	  *
	  * Compares this object with the passed object to check if they are within T2 distance of each other.
	  * It returns true if they are within T2 distance, false otherwise.
	  */
	public boolean withinT2(TemperatureDataPoint dataPoint)
	{
		return (simpleDistance(dataPoint) < T2);
	}

	/**
	  * <b>Simple and inexpensive distance metric. </b><br>
	  * <b>Parameters:</b>	TemperatureDataPoint dataPoint, the object to compare this object to. <br>
	  * <b>Returns:</b>		int, a simple distance value. <br><br>
	  *
	  * Finds a simple, cheap distance between two Data Points.
	  * Used in Canopy Generation phase.
	  */
	public int simpleDistance(TemperatureDataPoint dataPoint)
	{
		return Math.abs((int)Math.floor((time - dataPoint.time) * 10));
	}

	/**
	  * <b>Expensive distance metric for clustering. </b><br>
	  * <b>Parameters:</b>	TemperatureDataPoint dataPoint, the object to compare this object to. <br>
	  * <b>Returns:</b>		double, a complex distance value. <br>
	  *
	  * Finds a complex, more expensive distance between two Data Points.
	  * Used in Clustering phase.
	  */
	public double complexDistance(TemperatureDataPoint dataPoint)
	{
		return Math.sqrt(Math.abs((time - dataPoint.time) * (time - dataPoint.time) 
				+ (reading - dataPoint.reading) * (reading - dataPoint.reading)));
	}

	/**
	  * <b>Converts the Data Point to a String. </b><br>
	  * <b>Parameters:</b>	None <br>
	  * <b>Returns:</b>		String <br><br>
	  *
	  * Returns a string representation of this object.
	  */
	public String toString()
	{
		return time + "," + reading + "," + id1 + "," + id2 + "," + id3 + "," + id4;
	}

	/**
	  * <b>Overridden equals method of Object. </b><br>
	  * <b>Parameters:</b>	Object object, the passed object to check for equality <br>
	  * <b>Returns:</b>		boolean <br><br>
	  *
	  * Returns true if this object and the passed object are the same.
	  * Similarity conditions depend on the Data Set.
	  */
	@Override
	public boolean equals(Object object)
	{
		if(object == null)
			return false;
		TemperatureDataPoint dataPoint = (TemperatureDataPoint) object;
		if(time ==  dataPoint.time && reading == dataPoint.reading 
			&& id1 == dataPoint.id1  && id2 == dataPoint.id2  && id3 == dataPoint.id3  && id4 == dataPoint.id4)
			return true;
		return false;
	}

	/**
	  * <b>Overridden hashCode method of Object Class. </b><br>
	  * <b>Parameters:</b>	Nothing
	  * <b>Returns:</b>		int, the hash code. <br><br>
	  *
	  * Returns a user defined hash code for the object.
	  */
	@Override
	public int hashCode()
	{
		return (17 * (int)time + 31 * (int)reading);
	}

	/**
	  * <b>Method to return average of Data Points passed to it. </b><br>
	  * <b>Parameters:</b>	Iterable<TemperatureDataPoint> dataPoints, an iterable list of Data Points to be averaged. <br>
	  * <b>Returns:</b>		TemperatureDataPoint, the average Data Point <br><br>
	  *
	  * This function traverses the Tterable list of Data Points passed to it.
	  * The average of all these points is found out and returned.
	  */
	public static TemperatureDataPoint getAverageDataPoint(Iterable<TemperatureDataPoint> dataPoints)
	{
		float timeSum = 0;
		float readingSum = 0;
		long count = 0;
		for(TemperatureDataPoint dataPoint: dataPoints)
		{
			timeSum += dataPoint.time;
			readingSum += dataPoint.reading;
			count++;
		}
		TemperatureDataPoint averageDataPoint =  new TemperatureDataPoint();
		averageDataPoint.time = (timeSum/count);
		averageDataPoint.reading = (readingSum/count);
		return averageDataPoint;
	}
}
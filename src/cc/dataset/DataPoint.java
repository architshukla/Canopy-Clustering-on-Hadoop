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
public class DataPoint implements WritableComparable<DataPoint>
{
	/**
	  * Attributes for the class: year and temperature.
	  */
	public int year, temperature;

	/**
	  * T1 and T2 thresholds for this Data Set.
	  */
	public final static double T1 = 10, T2 = 6;
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
	public DataPoint()
	{
		year = temperature = 0;
	}

	/**
	  * <b>Parameterized Constructor (String). </b><br>
	  * <b>Parameters:</b>	String dataPointString, a string representation of an object of the class. <br>
	  * <b>Returns:</b>		Nothing <br><br>
	  *
	  * Parses the input string and sets the appropriate fields of the object. Inverse of toString() method.
	  */
	public DataPoint(String dataPointString)
	{
		int commaPosition = dataPointString.indexOf(",");
		year = Integer.parseInt(dataPointString.substring(0, commaPosition));
		temperature = Integer.parseInt(dataPointString.substring(commaPosition + 1));
	}

	/**
	  * <b>Copy Constructor (DataPoint). </b><br>
	  * <b>Parameters:</b>	DataPoint dataPoint, the reference Data Point whose fields are to be copied into this object <br>
	  * <b>Returns:</b>		Nothing <br><br>
	  *
	  * Sets this object's fields to the corresponding fields of the passed object.
	  */
	public DataPoint(DataPoint dataPoint)
	{
		year = dataPoint.year;
		temperature = dataPoint.temperature;
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
		out.writeInt(year);
		out.writeInt(temperature);
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
		year = in.readInt();
		temperature = in.readInt();
	}

	/**
	  * <b>Read method. </b><br>
	  * <b>Parameters:</b>	DataInput in, to read the fields of an object serially <br>
	  * <b>Returns:</b>		Object of type DataPoint <br>
	  * <b>Uses:</b>		void readFields(DataInput in) <br><br>
	  *
	  * Serially reads the fields of the object in the order they were written into another object and returns it.
	  */
	public DataPoint read(DataInput in)
		throws IOException
	{
		DataPoint dataPoint = new DataPoint();
		dataPoint.readFields(in);
		return dataPoint;	
	}

	/**
	  * <b>compareTo method of. </b><br>
	  * <b>Parameters:</b>	DataPoint dataPoint, the object to compare this object to <br>
	  * <b>Returns:</b>		int ,possible values are -1, 0 and 1 <br><br>
	  *
	  * Compares the fields of two objects to obtain an ordering.
	  */
	public int compareTo(DataPoint dataPoint)
	{
		return (temperature < dataPoint.temperature ? -1 : 
			(temperature == dataPoint.temperature ? (year < dataPoint.year ? -1 : (year == dataPoint.year ? 0 : 1)) : 1));
	}

	/**
	  * <b>Checks if the distance between two Data Points is within T1. </b><br>
	  * <b>Parameters:</b>	DataPoint dataPoint, the object to compare this object to. <br>
	  * <b>Returns:</b>		boolean <br>
	  * <b>Uses:</b>		int simpleDistance(DataPoint). <br><br>
	  *
	  * Compares this object with the passed object to check if they are within T1 distance of each other.
	  * Returns true if they are within T1 distance, false otherwise.
	  */
	public boolean withinT1(DataPoint dataPoint)
	{
		return (simpleDistance(dataPoint) < T1);
	}

	/**
	  * <b>Checks if the distance between two Data Points is within T1. </b><br>
	  * <b>Parameters:</b>	DataPoint dataPoint, the object to compare this object to. <br>
	  * <b>Returns:</b>		boolean <br>
	  * <b>Uses:</b>		int simpleDistance(DataPoint) <br>
	  *
	  * Compares this object with the passed object to check if they are within T2 distance of each other.
	  * It returns true if they are within T2 distance, false otherwise.
	  */
	public boolean withinT2(DataPoint dataPoint)
	{
		return (simpleDistance(dataPoint) < T2);
	}

	/**
	  * <b>Simple and inexpensive distance metric. </b><br>
	  * <b>Parameters:</b>	DataPoint dataPoint, the object to compare this object to. <br>
	  * <b>Returns:</b>		int, a simple distance value. <br><br>
	  *
	  * Finds a simple, cheap distance between two Data Points.
	  * Used in Canopy Generation phase.
	  */
	public int simpleDistance(DataPoint dataPoint)
	{
		return Math.abs(temperature - dataPoint.temperature);
	}

	/**
	  * <b>Expensive distance metric for clustering. </b><br>
	  * <b>Parameters:</b>	DataPoint dataPoint, the object to compare this object to. <br>
	  * <b>Returns:</b>		double, a complex distance value. <br>
	  *
	  * Finds a complex, more expensive distance between two Data Points.
	  * Used in Clustering phase.
	  */
	public double complexDistance(DataPoint dataPoint)
	{
		return Math.sqrt(Math.abs((year - dataPoint.year) * (year - dataPoint.year) 
				+ (temperature - dataPoint.temperature) * (temperature - dataPoint.temperature)));
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
		return year + "," + temperature;
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
		DataPoint dataPoint = (DataPoint) object;
		if(year ==  dataPoint.year && temperature == dataPoint.temperature)
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
		return (17 * year + 31 * temperature);
	}

	/**
	  * <b>Method to return average of Data Points passed to it. </b><br>
	  * <b>Parameters:</b>	Iterable<DataPoint> dataPoints, an iterable list of Data Points to be averaged. <br>
	  * <b>Returns:</b>		DataPoint, the average Data Point <br><br>
	  *
	  * This function traverses the Tterable list of Data Points passed to it.
	  * The average of all these points is found out and returned.
	  */
	public static DataPoint getAverageDataPoint(Iterable<DataPoint> dataPoints)
	{
		double yearSum = 0;
		double temperatureSum = 0;
		long count = 0;
		for(DataPoint dataPoint: dataPoints)
		{
			yearSum += dataPoint.year;
			temperatureSum += dataPoint.temperature;
			count++;
		}
		DataPoint averageDataPoint =  new DataPoint();
		averageDataPoint.year = (int) (yearSum/count);
		averageDataPoint.temperature = (int) (temperatureSum/count);
		return averageDataPoint;
	}
}
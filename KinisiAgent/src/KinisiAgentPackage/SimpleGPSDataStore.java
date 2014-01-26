package KinisiAgentPackage;

import java.util.LinkedList;

public class SimpleGPSDataStore {
	LinkedList<String> dataPointsList;

	public SimpleGPSDataStore() {
		dataPointsList = new LinkedList<String>();
	}

	public void addDataPoint(String newDataPoint) {
		try {
			dataPointsList.push(newDataPoint); // prolly should return something
												// to see if it succeeded ;)
		} catch (Exception e) {
			System.out
					.println("Caught exception in addDataPoint method in SimpleGPSDataStore object\n");
		}
		// should add specific Exception for empty LinkedList, among many other
		// things...
	}

	public String getNextDataPoint() {
		String nextDataPoint = null;
		try {
			nextDataPoint = dataPointsList.pop();
		} catch (Exception e) {
			System.out
					.println("Caught exception in getNextDataPoint method in SimpleGPSDataStore object\n");
		}
		// need to check somehow for empty list and null string values...
		return nextDataPoint;
	}

	public int getDataPointsCount() {
		return dataPointsList.size(); // does this return an int for sure? an
										// "abstract int"? double-check.
	}

	public boolean isFull() {
		// set max number of dataPointsCount here, if desired, for testing
		// purposes
		if (dataPointsList.size() >= 300) {
			return true;
		} else
			return false;
	}

	public boolean isEmpty() {
		return dataPointsList.isEmpty();
	}

}
package KinisiAgentPackage;

public class SimpleGPSDataPoint {
	private String device_id; // change to proper type later;
	private String timestamp; // change to proper type later
	private String latitude; // change to proper type later
	private String longitude; // change to proper type later
	private String altitude; // change to proper type later
	private String track; // change to proper type later
	private String speed; // change to proper type later
	private String climb; // change to proper type later
	private String JsonFormattedDataPoint;
	// following are misc values included in GPSD JSON data
	private String ept;
	private String epx;
	private String epy;
	private String epv;

	public SimpleGPSDataPoint(String device_id, String timestamp,
			String latitude, String longitude, String altitude, String track,
			String speed, String climb) {
		this.device_id = device_id;
		this.timestamp = timestamp;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.track = track;
		this.speed = speed;
		this.climb = climb;

		this.ept = "0.005"; // hard-coded value for testing right now
		this.epx = "12.861"; // hard-coded value for testing right now
		this.epy = "8.871"; // hard-coded value for testing right now
		this.epv = "26.856"; // hard-coded value for testing right now

		// create the JSON data point using values retrieved from constructor
		this.createJsonDataPoint();
	}

	public String getJsonData() {
		// this method takes the data point and formats it into a JSON-formatted
		// string
		// this is useful for sending a POST request to sync with the cloud
		// server, for example
		return JsonFormattedDataPoint;
	}

	private void createJsonDataPoint() {
		String Line1 = "{";
		String Line2 = "\"deviceId\": \"" + device_id + "\",";
		String Line3 = "\"deviceRecord\": {\"class\":\"TPV\",\"tag\":\"RMC\",\"device\":\"stdin\",\"mode\":3"
				+ ",\"time\":\""
				+ timestamp
				+ "\",\"ept\":"
				+ ept
				+ ",\"lat\":"
				+ latitude
				+ ",\"lon\":"
				+ longitude
				+ ",\"alt\":"
				+ altitude
				+ ",\"epx\":"
				+ epx
				+ ",\"epy\":"
				+ epy
				+ ",\"epv\":"
				+ epv
				+ ",\"track\":"
				+ track
				+ ",\"speed\":"
				+ speed
				+ ",\"climb\":"
				+ climb + "}";
		String Line4 = "}";
		JsonFormattedDataPoint = Line1 + Line2 + Line3 + Line4;
	}

	private String createAlternateJsonDataPoint(String device_id,
			String inputJsonDataPoint) {
		String JsonFormattedDataPoint;
		String Line1 = "{";
		String Line2 = "\"deviceId\": \"" + device_id + "\",";
		String Line3 = "\"deviceRecord\": ";
		String Line4 = inputJsonDataPoint;
		String Line5 = "}";
		JsonFormattedDataPoint = Line1 + Line2 + Line3 + Line4 + Line5;
		return JsonFormattedDataPoint;
	}
}

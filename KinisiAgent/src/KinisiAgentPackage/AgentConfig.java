package KinisiAgentPackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner; 
import java.util.Properties; 
import java.io.IOException; 
import java.io.InputStream;
import java.io.FileInputStream; 

public class AgentConfig {
	
	//should have a set of default properties in case of file-not-found
	private Properties AgentProperties; 	
	private String propertiesFilename; 
//	private File propertiesFile; 
	// Variable names match those in KinisiAgent.properties file
//	private String OS_TYPE; // Operating System type that the Kinisi Agent runs on
//	private String GPS_DATA_DIR; // directory where GPS data is located on disk, e.g. "/home/u13-user/GPSData/"
//	private String GPS_DATA_SYNC_DIR; // directory where synced GPS data files are moved/logged to, e.g. "/home/u13-user/GPSData_Synced/"
//	private String LOG_FILE_DIR; 
//	private String LOG_FILENAME; 
	private String logfilePathAndName; 
//	private String SERVER_HOSTNAME; // IP address or hostname
//	private String SERVER_PORT; // e.g. port 8080, populated as a String here
	private int serverPort; //now it's an int
	private String insecureUrl; 
	private String secureUrl; 
	
	public AgentConfig() {
		AgentProperties = new Properties(); 	
	}
	
	public AgentConfig(String inputFilename) {
		try {
			propertiesFilename = inputFilename;
		}
		catch (Exception e) { //change to FileNotFoundException somehow? 
			e.printStackTrace(); //log to file instead! 
		}
		loadPropertiesFile(propertiesFilename);
	}

	public String getDeviceID() {
		//need to implement some checks here for null values
		// and returning proper defaults based on the OS type
			return AgentProperties.getProperty("DEVICE_ID");
	}
	
	public String getLogfilePathAndName() {
		//need to implement some checks here for null values
		// and returning proper defaults based on the OS type
			return logfilePathAndName;
	}
	
	public String getGPSDataSyncedDirectory() {
		//need to implement some checks here for null values
		// and returning proper defaults based on the OS type
			return AgentProperties.getProperty("GPS_DATA_SYNC_DIR");
	}
	
	public String getGPSDataDirectory() {
		//need to implement some checks here for null values
		// and returning proper defaults based on the OS type
			return AgentProperties.getProperty("GPS_DATA_DIR");
	}
	
	public String getOS() {  //return the OS specified in the configuration file
		//need to implement some checks here for null values
		// and returning proper defaults based on the OS type
			return AgentProperties.getProperty("OS_TYPE"); 
	}
	
	public int getPort() {
		//need to implement some checks here for null values
		// and returning proper defaults based on the OS type
			return serverPort;	
	}
	
	public String getHostname() {
		//need to implement some checks here for null values
		// and returning proper defaults based on the OS type
			return AgentProperties.getProperty("SERVER_HOSTNAME"); 			
	}
	
	public String getSecureUrl() {
		if (secureUrl!=null) {
			return secureUrl; 			
		}
		else return "www.kinisi.cc"; //definitely need a better method here... 
	}
	
	public String getInsecureUrl() {
		if (insecureUrl!=null) {
			return insecureUrl; 			
		}
		else return "www.kinisi.cc"; //definitely need a better method here... 
	}
	
    public void loadPropertiesFile(String inputFilename) {    	
    	InputStream inStream = null; 
    	try {
    		inStream = new FileInputStream(inputFilename); 
    		if (inStream!=null) {
        		AgentProperties.load(inStream); 
        		// break out these items into a more appropriate try/catch statement
        		insecureUrl = "http://" + AgentProperties.getProperty("SERVER_HOSTNAME")
        				+ ":" + AgentProperties.getProperty("SERVER_PORT") + "/geostore"; 
        		secureUrl = "https://" + AgentProperties.getProperty("SERVER_HOSTNAME")
        				+ "/geostore"; 
        		logfilePathAndName = AgentProperties.getProperty("LOG_FILE_DIR")
        				+ AgentProperties.getProperty("LOG_FILENAME"); 
        		serverPort = Integer.parseInt(AgentProperties.getProperty("SERVER_PORT")); 
    		}
    	}
    	catch (IOException e) {
    		e.printStackTrace(); //print to log file, instead! 
    	}
    	finally {
    		if (inStream != null) {
    			try {
        			inStream.close();     				
    			}
    			catch (IOException e) {
    				e.printStackTrace(); //print to log file! 
    			}

    		}
    	}
    }    
	
}



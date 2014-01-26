
package KinisiAgentPackage; 


import java.io.BufferedReader;
import java.io.BufferedWriter; 
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileWriter; 
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Random; 
import java.util.Collection; 
import java.util.ArrayList; 
import java.util.Date; 
import java.net.Socket;
import java.text.DateFormat; 
import java.text.SimpleDateFormat; 

//import javax.net.ssl.HttpsURLConnection;

public class KinisiAgent {
	 // Each Kinisi Client has a cache for storing data points, before dumping to disk or cloud
	 // The SimpleGPSDataStore is a rough LIFO data structure for testing/prototyping
     private SimpleGPSDataStore dataPointsCache; 
     // The AgentConfiguration is all settings for this Agent, loaded from a config file on disk on the device
     private static AgentConfig AgentConfiguration; //code review -> is static appropriate here & working for all test cases? 
     /**
      * @param args
      */
    
     public static void main(String[] args) throws Exception {

          KinisiAgent quickSync = new KinisiAgent();
          //read the Configuration file, update this code to the same directory, perhaps home or /etc/bin/kinisi (?), instead of hard-coded, so it works on all platforms 
          quickSync.loadAgentConfiguration("/home/u13-user/KinisiAgent.conf"); //Ubuntu for testing
//          quickSync.loadAgentConfiguration("/home/pi/KinisiAgent.conf"); //Raspberry Pi
          AgentConfiguration.getLogfilePathAndName(); 
          
          quickSync.appendToLogfile(AgentConfiguration.getLogfilePathAndName(), "Kinisi Agent started at " + quickSync.returnCurrentDateAndTime() + "\n"); 
        		  
          // Retrieve list of files, then add JSON data to cache from each
          String[] listOfFiles = KinisiAgent.gatherFileNames(AgentConfiguration.getGPSDataDirectory());
          // --> Add a better check here to see if the String array is empty, then proceed accordingly
          if (listOfFiles.length == 0){
        	  quickSync.appendToLogfile(AgentConfiguration.getLogfilePathAndName(), "No GPS data files found on disk at " + quickSync.returnCurrentDateAndTime() + "\n"); 
          }
          else {
              for (int i = 0; i < listOfFiles.length; i++) {
            	  FileInputStream finput = new FileInputStream(AgentConfiguration.getGPSDataDirectory() + listOfFiles[i]);
            	  BufferedReader bfreader = new BufferedReader(new InputStreamReader(finput)); 
            	  String dataPoint = null; 
            	  String readLine = null; 
            	  while ((readLine = bfreader.readLine()) != null) {
            		  dataPoint = quickSync.createAlternateJsonDataPoint(AgentConfiguration.getDeviceID(), readLine); 
            		  quickSync.addPointToCache(dataPoint); 
            	  }
            	  bfreader.close();   
              }   
          }

          // Write the cache data to the cloud or to console, respectively, for testing
          if (KinisiAgent.isHostAvailable(AgentConfiguration.getHostname(), AgentConfiguration.getPort())) {
        	  // If server is available, write cache to server
        	  while (!quickSync.isCacheEmpty()) {
//        		  quickSync.sendPostRequest(serverUrl, quickSync.getNextPointInCache());        		          	
        		  quickSync.sendPostRequest(AgentConfiguration.getInsecureUrl(), quickSync.getNextPointInCache()); 
//        		  System.out.println(AgentConfiguration.getInsecureUrl()); 
//        		  System.out.println(quickSync.getNextPointInCache()); 
//        		  System.out.println(quickSync.getNextPointInCache());
        	  }
        	  // Now clean up your mess (of files on disk)
              for (int i = 0; i < listOfFiles.length; i++) {
            	  quickSync.moveFileToSyncedRepo(AgentConfiguration.getGPSDataDirectory(), AgentConfiguration.getGPSDataSyncedDirectory(), listOfFiles[i]); 
              }
          }
          else {
        	  System.out.println("Server " + AgentConfiguration.getHostname() + " unavailable on port " + AgentConfiguration.getPort() + "\n");   
          }
     }

     public KinisiAgent() {
          dataPointsCache = new SimpleGPSDataStore();
          AgentConfiguration = new AgentConfig(); 
     }
     
     public void loadAgentConfiguration(String inputFile) {
         try {
        	 AgentConfiguration.loadPropertiesFile(inputFile);   
        }
        catch (Exception e) {
             System.out.println("Caught exception in loadAgentConfiguration method in KinisiClient.java.\n");
        }
   }

     
     
     public String returnCurrentDateAndTime() {
    	 DateFormat thisDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); 
    	 Date dateRightNow = new Date(); 
    	 return thisDateFormat.format(dateRightNow); 
     }
     
     public void appendToLogfile(String logFile, String logEvent) {
    	 try {
    		 File outputFile = new File(logFile); 
    		 if (!outputFile.exists()) {
    			 outputFile.createNewFile(); 
    		 }
    		 FileWriter thisFileWriter = new FileWriter(outputFile.getAbsolutePath(), true); 
    		 BufferedWriter thisBufferedWriter = new BufferedWriter(thisFileWriter); 
    		 thisBufferedWriter.append(logEvent); 
    		 thisBufferedWriter.close();        
    	 } 
    	 catch (IOException e) {
    		 e.printStackTrace(); 
    	 }
    	 
     }

     public void moveFileToSyncedRepo(String fromDirectory, String toDirectory, String inputFile) {
    	 try {
//    		 System.out.println("fromDirectory is " + fromDirectory + " toDirectory is " + toDirectory + " inputFile is " + inputFile); 
    		 File thisFile = new File(fromDirectory + inputFile); 
    		 if(thisFile.renameTo(new File(toDirectory + thisFile.getName()))) {
//    			 System.out.println("File moved successfully."); 
    		 }
    		 else {
    			 System.out.println("File move was unsuccessful."); 
    		 }
    	 }
    	 catch (Exception e) {
    		 e.printStackTrace(); 
    	 }    	 
     }
     
     public static boolean isHostAvailable(String serverName, int serverPort) { 
	    try (Socket s = new Socket(serverName, serverPort)) {
	        return true;
	    } catch (IOException ex) {
	        /* ignore */
	    }
	    return false;
	}
     
     public static String[] gatherFileNames(String directoryPath) throws Exception {
    	 //need to do a try/catch, or find another way to check if directoryPath is found... 
   		 File directory = new File(directoryPath);
		 Collection<String> files = new ArrayList<String>();
    	 if(directory.isDirectory()){
    		 File[] listFiles = directory.listFiles();
    		 for(File file : listFiles){
    			 if(file.isFile()) {
    				 files.add(file.getName());
    			 }
    		 }
    	 }
    	 return files.toArray(new String[]{});
     }
     
     private String createAlternateJsonDataPoint(String device_id, String inputJsonDataPoint) throws Exception{
          String JsonFormattedDataPoint;
          String Line1 = "{";
          String Line2 = "\"deviceId\": \"" + device_id + "\",";
          String Line3 = "\"deviceRecord\": ";
          String Line4 = inputJsonDataPoint;
          String Line5 = "}";
          JsonFormattedDataPoint = Line1 + Line2 + Line3 + Line4 + Line5;
          return JsonFormattedDataPoint;
     }
    
     private void addPointToCache(String newCacheDataPoint) throws Exception { //make non-void/return a value
          try {
               dataPointsCache.addDataPoint(newCacheDataPoint);
          }
          catch (Exception e) {
               System.out.println("Caught exception in addPointToCache method in KinisiClient.java.\n");
          }
     }
    
     private String getNextPointInCache() throws Exception {
          String dataPointBuf = null;
          try {
               dataPointBuf = dataPointsCache.getNextDataPoint();
          }
          catch (Exception e) {
               System.out.println("Caught exception in getNextPointInCache method in KinisiClient.java.\n");
          }
          return dataPointBuf;
     }
    
     private boolean isCacheEmpty() throws Exception {
          return dataPointsCache.isEmpty();
     }
    
     private void flushCacheToServer(String inputUrlString) throws Exception {
          //THIS IS A MESS AND DOES NOT WORK, NEEDS TO BE FIXED OR OTHERWISE REMOVED
          URL serverUrl = new URL(inputUrlString); //try/catch in case of malformed URL? check somehow?
          //put a while statement in here to flush the cache.
          try {
               System.out.println("break1");
               HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();
               //connection setup tasks
               connection.setRequestMethod("POST");
               connection.setRequestProperty("User-Agent", "Mozilla/25.0"); //unnecessary?
               connection.setDoOutput(true);
               System.out.println("break2");
               //setup I/O with the server connection
               DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
               System.out.println("break2.1");
               InputStreamReader inputReader;// = new InputStreamReader(connection.getInputStream());
               System.out.println("break2.2");
               BufferedReader inputBuf;// = new BufferedReader(inputReader);
               System.out.println("break2.3");
               String inputLine;
               System.out.println("break2.4");
               StringBuffer response = new StringBuffer();
               System.out.println("break2.5");
               //response code from server
               int responseCode; //set to something initially?
               System.out.println("break3");
               while (!dataPointsCache.isEmpty()) {
                    System.out.println("start of while loop");
                    String UrlParameters = dataPointsCache.getNextDataPoint();
                    writer.writeBytes(UrlParameters);
                    writer.flush();
                    writer.close();
                    responseCode = connection.getResponseCode();
                    System.out.println("\nSending 'POST' request to URL: " + inputUrlString);
                    System.out.println("Post parameters: " + UrlParameters);
                    System.out.println("Response Code: " + responseCode);
                    inputReader = new InputStreamReader(connection.getInputStream());
                    inputBuf = new BufferedReader(inputReader);
                    while ((inputLine = inputBuf.readLine()) != null) {
                         response.append(inputLine);
                    }
                    inputBuf.close(); //leave open?
                    System.out.println(response.toString());
               }
//               connection.disconnect(); //let's not disconnect, best for java to utilize opened connection elsewhere
          }
          catch (Exception e) {
               System.out.println("Caught exception in flushCacheToServer in KinisiClient.java.");
          }
         
     }
    
     private void sendPostRequest(String inputURL, String dataPoint) throws Exception {
          URL thisURL = new URL(inputURL);
          try {
	          HttpURLConnection connection = (HttpURLConnection) thisURL.openConnection();
	          connection.setRequestMethod("POST");
	          connection.setRequestProperty("User-Agent", "Mozilla/25.0");
	          String URLParameters = dataPoint;
	          connection.setDoOutput(true);
	          DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
	          wr.writeBytes(URLParameters);
	          wr.flush();
	          wr.close();
	          int responseCode = connection.getResponseCode();
	          System.out.println("\nSending 'POST' request to URL: " + inputURL);
//	          System.out.println("Post parameters: " + URLParameters);
	          System.out.println("Response Code: " + responseCode);
	          BufferedReader in = new BufferedReader(
	          new InputStreamReader(connection.getInputStream()));
	          String inputLine;
	          StringBuffer response = new StringBuffer();
	          while ((inputLine = in.readLine()) != null) {
	               response.append(inputLine);
	          }
	          in.close();
	          System.out.println(response.toString());
          }
          catch (Exception e) {
        	  System.out.println("Caught exception in sendPostRequest method in KinisiClient.java"); 
        	  System.out.println(e); 
          }
     }

     private void sendGetRequest(String inputURL) throws Exception {
          URL thisURL = new URL(inputURL);
          HttpURLConnection connection = (HttpURLConnection) thisURL.openConnection();
          connection.setRequestMethod("GET");
          connection.setRequestProperty("User-Agent", "Mozilla/25.0");
          int responseCode = connection.getResponseCode();
          System.out.println("\nSending 'GET' request to URL: " + thisURL);
          System.out.println("Response Code: " + responseCode);
          BufferedReader inputBuf = new BufferedReader(
          new InputStreamReader(connection.getInputStream()));
          String inputLine;
          StringBuffer response = new StringBuffer();

          while ((inputLine = inputBuf.readLine()) != null) {
               response.append(inputLine);
          }
          inputBuf.close();
          System.out.println(response.toString());
     }
    
     private int getRandomInt(int highestValue) throws Exception {
          //quick random number generator for testing, creates int from zero to highestValue
          Random gen = new Random();
          return gen.nextInt(highestValue);
     }
    
     private void getGPSData() throws Exception {
         
     }

     private void storeGPSData() throws Exception {
         
     }
    
}
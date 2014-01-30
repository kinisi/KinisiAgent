KinisiAgent
===================

This is development code for the data collection agent that runs on the Kinisi Raspberry Pi prototypes. The Java application created from these java files has been used to perform both realtime and cached/delayed tracking of moving vehicles, people and other objects. 

Technical Overview
-------------------
The Kinisi Agent application runs as a service on the Raspberry Pi, collecting GPS data and syncing this data with a remote server ("the cloud"). The agent uses a caching mechanism to retrieve data and send this data to a server, once communication is established. More details about the features and technical details of the current version and forthcoming versions are contained in this README. 

Early Version Description
--------------------------
The first development versions of this code (current version as of January, 2014) are intended to demonstrate feasibility, rather than production-worthy stability and efficiency. The Java application consists of several key components: The core agent class, the data store, and the agent configuration. The agent, itself, is responsible for the following: 

(1) Reading configuration information from a file to determine the agent's configuration for the operating system and the Use Case its running, then creating the appropriate settings in the agent. A Use Case for Kinisi varies based on the type of object its tracking, the type of networking capabilities such as realtime via cell phone data versus historical/cached via WiFi, battery life, and many more parameters. An authentication ("dev token") is also read from the configuration file on disk (KinisiAgent.config), which is in the format of a Java Properties file. Configuring the agent is accomplished via the AgentConfig class. 

(2) Gathering GPS data from JSON files stored on disk and loading this data into the cache. The JSON files are created by a series of several Linux tools included in the gpsd package. The JSON formatted data is parsed and stored as new JSON data that the server understands and can store in its database. This is accomplished via the SimpleGPSDataPoint class. These data points are stored in and retrieved from cache via the SimpleGPSDataStore class. 

(3) Determining if there is connectivity between the Raspberry Pi and "the cloud" (server) and POSTing the data from cache. These actions are currently performed in the KinisiAgent class. 

(4) Moving the GPS data stored in JSON format/files on disk to a secondary repository in case of data loss, communication issues or other bugs. This is currently performed in the KinisiAgent class. 

The Kinisi Agent Java application for Raspberry Pi currently relies on several shell scripts to perform tasks, such as gathering GPS data and launching the Java application on regular intervals, as well as ensuring that these actions are running as a service on the operating system, starting their operation on power-on and running continually. These shell scripts are also included with the code for the Kinisi Agent. 

Future Version Goals
---------------------
Future versions of the Kinisi Agent can make great improvements to the development code provided in this Java application. The following bullets describes these goals at a high-level: 

(1) Improved caching and syncing mechanisms to overcome "bugs", which include outlier scenarios where data is moved without syncing, or vice se versa. 

(2) Implementation of a "thinner" protocol for communication with remote servers, such as protocol buffers. 

(3) Improving the SimpleGPSDataPoint and SimpleGPSDataStore classes, or otherwise create new, better classes for improved features. For example, moving away from LinkedList in order to overcome limitations in efficiently syncing specific data points for unique scenarios, such as "most recent mode", "burst mode", "low power mode", "averaged data mode", etc. 

(4) Direct access to USB devices to do away with the reliance on shell scripts to run the Java application and GPS data collection. 

(5) Use of power-saving features in the operating system and the ability for the agent to determine appropriate times to "sleep", based on algorithms specific to each Use Case. 

(6) Creation of an "auto-update" service to ensure that the latest Kinisi Agent code is running

(7) Many more basic improvements to the existing code for Java programming "best practices" that would be too long to list here :) 

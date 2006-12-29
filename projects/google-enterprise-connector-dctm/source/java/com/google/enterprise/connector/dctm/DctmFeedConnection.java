package com.google.enterprise.connector.dctm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


import com.google.enterprise.connector.common.StringUtils;
import com.google.enterprise.connector.pusher.FeedConnection;


public class DctmFeedConnection implements FeedConnection {
	
	private int number;
	public DctmFeedConnection(){
		number = 0;
		
	}
	
	private void generateFile(InputStream data){
		number++;
		//File file = new File("C:/_dev/gsa/crawl/out"+number+".txt");
		
		
		System.out.println("dans generateFile " + number);
		try {
			//fileOutputStream = new FileOutputStream(file);
			FileOutputStream fileOutputStream = new FileOutputStream("C:/_dev/gsa/crawl/out"+number+".txt");
			byte[] buffer = new byte[1000];
		    int readCount = 0;
		    if(data == null){
		    	System.out.println("null");
		    }
		    while ((readCount = data.read(buffer)) != -1) { 
		      if (readCount < 1000) {
		        fileOutputStream.write(buffer, 0, readCount);
		      } else {
		        fileOutputStream.write(buffer);
		      }
		    }
			fileOutputStream.close();
			data.close();
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String sendData(InputStream data) throws IOException {
		if(data != null){
		//String dataStr = StringUtils.streamToString(data);
			generateFile(data);
		}else{
			System.out.println("null " );
		}
		 
		return "DctmFeedConnection";
	}

}

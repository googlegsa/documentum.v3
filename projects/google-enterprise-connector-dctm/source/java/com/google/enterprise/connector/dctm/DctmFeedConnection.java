package com.google.enterprise.connector.dctm;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.google.enterprise.connector.pusher.FeedConnection;

public class DctmFeedConnection implements FeedConnection {
	
	private int number;
	private FileOutputStream fileOutputStream;
	
	public DctmFeedConnection() {
		number = 0;
	}
	
	public String sendData(InputStream data) throws IOException {
		number++;
		try {
			fileOutputStream = new FileOutputStream("testdata/crawl/out" + number + ".txt");
			byte[] buffer = new byte[1000];
			int readCount = 0;
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "DctmFeedConnection";
	}
	
}

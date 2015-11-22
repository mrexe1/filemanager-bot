package me.shib.java.app.telegram.bot.filemanager.navigator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Date;

public class LocalCacheManager {
	
private static final String defaultLocalCacheDirectory = "LocalDataCacheDir";
	
	private File localCacheDirectory;
	private long localCacheRenewalInterval;
	
	protected LocalCacheManager(long localCacheRenewalIntervalInMinutes, String localCacheDirectoryName) {
		if((localCacheDirectoryName == null) || (localCacheDirectoryName.isEmpty())) {
			this.localCacheDirectory = new File(defaultLocalCacheDirectory);
		}
		else {
			this.localCacheDirectory = new File(localCacheDirectoryName);
		}
		if((!this.localCacheDirectory.exists()) || (!this.localCacheDirectory.isDirectory())) {
			this.localCacheDirectory.mkdirs();
		}
		this.localCacheRenewalInterval = localCacheRenewalIntervalInMinutes * 60000;
	}
	
	private String getEncodedName(String name) {
		try {
			return String.format("%x", new BigInteger(1, name.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	protected String getDataforKey(String type, String key) {
		try {
			File storeDir = new File(localCacheDirectory.getPath()
					+ File.separator + getEncodedName(type));
			if((!storeDir.exists()) || (!storeDir.isDirectory())) {
				storeDir.mkdirs();
			}
			File dataFile = new File(storeDir.getPath()
					+ File.separator + getEncodedName(key) + ".json");
			if(dataFile.exists()) {
				long diffTime = (new Date().getTime()) - dataFile.lastModified();
				if(diffTime < localCacheRenewalInterval) {
					StringBuilder contentBuilder = new StringBuilder();
					BufferedReader br = new BufferedReader(new FileReader(dataFile));
					String line = null;
					while((line =br.readLine()) != null) {
						contentBuilder.append(line + "\n");
					}
					br.close();
					if(!contentBuilder.toString().isEmpty()) {
						return contentBuilder.toString();
					}
				}
			}
		} catch (Exception e) {}
		return null;
	}
	
	protected boolean putDataForKey(String type, String key, String content) {
		try {
			File storeDir = new File(localCacheDirectory.getPath()
					+ File.separator + getEncodedName(type));
			if((!storeDir.exists()) || (!storeDir.isDirectory())) {
				storeDir.mkdirs();
			}
			File dataFile = new File(storeDir.getPath()
					+ File.separator + getEncodedName(key) + ".json");
			if(dataFile.exists()) {
				dataFile.delete();
			}
			PrintWriter pw = new PrintWriter(dataFile);
			pw.append(content);
			pw.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}

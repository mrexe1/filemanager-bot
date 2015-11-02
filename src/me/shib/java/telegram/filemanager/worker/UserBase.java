package me.shib.java.telegram.filemanager.worker;

import java.util.ArrayList;

import me.shib.java.telegram.filemanager.navigator.UserDir;

public class UserBase {
	
	private static ArrayList<UserDir> userDirList = new ArrayList<UserDir>();
	
	public static UserDir getUserDir(long userId) {
		for(int i = 0; i < userDirList.size(); i++) {
			if(userDirList.get(i).getUserId() == userId) {
				return userDirList.get(i);
			}
		}
		UserDir newUserDir = new UserDir(userId);
		userDirList.add(newUserDir);
		return newUserDir;
	}
	
}

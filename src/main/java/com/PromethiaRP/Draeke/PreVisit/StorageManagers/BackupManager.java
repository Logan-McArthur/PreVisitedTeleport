package com.PromethiaRP.Draeke.PreVisit.StorageManagers;

import java.io.File;

public class BackupManager {

	private final int NUM_BACKUPS = 3;
	
	private File dataFolder;
	
	public BackupManager(File dataFolder) {
		this.dataFolder = dataFolder;
		
		
	}
	
	
}

package com.PromethiaRP.Draeke.PreVisit.DataManagers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.PromethiaRP.Draeke.PreVisit.Utilities.Provider;

public class CombatManager implements Provider {

	private Map<UUID, Date> cooldownTimes;
	
	private final long WAIT_DELAY = 15 * 1000;
	
	public CombatManager() {
		cooldownTimes = new HashMap<UUID, Date>();
	}
	
	public boolean isTracking(UUID uid) {
		return cooldownTimes.containsKey(uid);
	}
	
	
	/**
	 * 
	 * @param uid
	 * @return True when the player has Waited long enough
	 */
	public boolean check(UUID uid) {
		if (isTracking(uid)) {
			Date now = new Date();
			if ( now.after(cooldownTimes.get(uid))) {
				cooldownTimes.remove(uid);
			}
			return true;
		} else {
			track(uid);
			return false;
		}

	}
	
	public void track(UUID uid) {
		cooldownTimes.put(uid, new Date(System.currentTimeMillis() + WAIT_DELAY));
	}
	
	public long getRemainingTime(UUID uid) {
		if (isTracking(uid)) {
			return cooldownTimes.get(uid).getTime() - System.currentTimeMillis();
		} else {
			return -1;
		}
	}
}

package org.ctyc.mgt.model.summercamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CampSite implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private Collection<CanteenTable> canteenTables;
	private Collection<Participant> participants;
	private Map<String, Integer> campPreassignedMap;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Collection<CanteenTable> getCanteenTables() {
		if (this.canteenTables == null){
			this.canteenTables = new ArrayList<CanteenTable>();
		}
		return canteenTables;
	}

	public Collection<Participant> getParticipants() {
		if (this.participants == null){
			this.participants = new HashSet<Participant>();
		}
		return participants;
	}

	public Map<String, Integer> getCampPreassignedMap() {
		if (this.campPreassignedMap == null){
			this.campPreassignedMap = new HashMap<String, Integer>();
		}
		return campPreassignedMap;
	}
}

package org.ctyc.mgt.summercamp;

import java.util.ArrayList;
import java.util.Collection;

import org.ctyc.mgt.model.summercamp.Participant;

public class GroupAssignmentPlan {

	private String campName;
	private int groupNumber;
	private Collection<Participant> participants;
	
	public GroupAssignmentPlan(String campName, int groupNumber) {
		super();
		this.campName = campName;
		this.groupNumber = groupNumber;
	}
	
	public String getCampName() {
		return campName;
	}
	
	public int getGroupNumber() {
		return groupNumber;
	}
	
	public Collection<Participant> getParticipants() {
		if (this.participants == null){
			this.participants = new ArrayList<Participant>();
		}
		return participants;
	}
}

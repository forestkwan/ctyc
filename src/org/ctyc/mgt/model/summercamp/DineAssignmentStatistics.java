package org.ctyc.mgt.model.summercamp;

import java.util.ArrayList;
import java.util.Collection;

public class DineAssignmentStatistics {

	private int numberOfDay;
	private String timeOfDay;
	private int tableNumber;
	private int participantCount;
	
	private Collection<DineTimeAssignmentStatistics> dineTimeAssignmentStatistics;
	
	public DineAssignmentStatistics(int numberOfDay, String timeOfDay, int tableNumber, int participantCount) {
		super();
		this.tableNumber = tableNumber;
		this.numberOfDay = numberOfDay;
		this.timeOfDay = timeOfDay;
		this.participantCount = participantCount;
	}
	
	public int getTableNumber() {
		return tableNumber;
	}
	public int getNumberOfDay() {
		return numberOfDay;
	}
	public String getTimeOfDay() {
		return timeOfDay;
	}
	public int getParticipantCount() {
		return participantCount;
	}

	public Collection<DineTimeAssignmentStatistics> getDineTimeAssignmentStatistics() {
		if (this.dineTimeAssignmentStatistics == null){
			this.dineTimeAssignmentStatistics = new ArrayList<DineTimeAssignmentStatistics>();
		}
		return dineTimeAssignmentStatistics;
	}
}

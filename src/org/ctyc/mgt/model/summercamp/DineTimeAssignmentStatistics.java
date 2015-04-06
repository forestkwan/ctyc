package org.ctyc.mgt.model.summercamp;

public class DineTimeAssignmentStatistics {
	
	private int numberOfDay;
	private int participantCount;
	
	public DineTimeAssignmentStatistics(int numberOfDay, int participantCount) {
		super();
		this.numberOfDay = numberOfDay;
		this.participantCount = participantCount;
	}
	
	public int getNumberOfDay() {
		return numberOfDay;
	}

	public int getParticipantCount() {
		return participantCount;
	}
}

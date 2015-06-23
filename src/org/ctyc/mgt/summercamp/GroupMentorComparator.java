package org.ctyc.mgt.summercamp;

import org.ctyc.mgt.model.summercamp.Participant;

public class GroupMentorComparator implements java.util.Comparator<Participant> {
	
	private int day;
	
	public GroupMentorComparator(int day){
		this.day = day;
	}

	public int compare(Participant p1, Participant p2) {
		return Integer.compare(p2.countAvailableDine(this.day), p1.countAvailableDine(this.day));
	}
}

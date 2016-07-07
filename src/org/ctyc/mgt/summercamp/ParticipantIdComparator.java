package org.ctyc.mgt.summercamp;

import java.util.Comparator;

import org.ctyc.mgt.model.summercamp.Participant;

public class ParticipantIdComparator implements Comparator<Participant> {

	@Override
	public int compare(Participant left, Participant right) {
		
	    if (left == null || left.getId() == null) {
	        return -1;
	    }
	    if (right == null || right.getId() == null) {
	        return 1;
	    }
		return left.getId().compareTo(right.getId());
	}

}

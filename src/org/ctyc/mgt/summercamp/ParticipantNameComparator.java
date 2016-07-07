package org.ctyc.mgt.summercamp;

import java.util.Comparator;

import org.ctyc.mgt.model.summercamp.Participant;

public class ParticipantNameComparator implements Comparator<Participant> {

	@Override
	public int compare(Participant left, Participant right) {
		if (left == null || left.getName() == null) {
	        return -1;
	    }
	    if (right == null || right.getName() == null) {
	        return 1;
	    }
		return left.getName().compareTo(right.getName());
	}

}

package org.ctyc.mgt.model.summercamp;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ctyc.mgt.model.Believer;

public class Participant extends Believer {

	private static final long serialVersionUID = 1L;
	private int groupNumber;
	private boolean isGroupMentor;
	private Set<DineAvailability> dineAvailabilitys;

	public Participant() {

	}

	public int getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(int groupNumber) {
		this.groupNumber = groupNumber;
	}

	public boolean isGroupMentor() {
		return isGroupMentor;
	}

	public void setGroupMentor(boolean isGroupMentor) {
		this.isGroupMentor = isGroupMentor;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Set<DineAvailability> getDineAvailabilitys() {
		if (this.dineAvailabilitys == null){
			this.dineAvailabilitys = new HashSet<DineAvailability>();
		}
		return dineAvailabilitys;
	}

}

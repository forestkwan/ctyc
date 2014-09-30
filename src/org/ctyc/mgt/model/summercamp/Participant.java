package org.ctyc.mgt.model.summercamp;

import org.ctyc.mgt.model.Believer;

public class Participant extends Believer {

	private static final long serialVersionUID = 1L;
	private int groupNumber;
	private boolean isGroupMentor;

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

}

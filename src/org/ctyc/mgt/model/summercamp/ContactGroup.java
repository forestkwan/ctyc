package org.ctyc.mgt.model.summercamp;

import java.util.ArrayList;
import java.util.List;

public class ContactGroup {

	private String groupCode;
	private List<ParticipantContact> participantContacts;
	private int mentorCount;
	private int nonMentorCount;
	
	public String getGroupCode() {
		return groupCode;
	}
	
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	
	public List<ParticipantContact> getParticipantContacts() {
		if (this.participantContacts == null){
			this.participantContacts = new ArrayList<>();
		}
		return participantContacts;
	}

	public int getMentorCount() {
		return mentorCount;
	}

	public void setMentorCount(int mentorCount) {
		this.mentorCount = mentorCount;
	}

	public int getNonMentorCount() {
		return nonMentorCount;
	}

	public void setNonMentorCount(int nonMentorCount) {
		this.nonMentorCount = nonMentorCount;
	}
	
}

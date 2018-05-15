package org.ctyc.mgt.model.summercamp;

import java.util.ArrayList;
import java.util.List;

public class ContactGroup {

	private String groupCode;
	private List<ParticipantContact> participantContacts;
	
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
}

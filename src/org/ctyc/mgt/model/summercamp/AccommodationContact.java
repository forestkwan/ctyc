package org.ctyc.mgt.model.summercamp;

import java.util.ArrayList;
import java.util.List;

public class AccommodationContact {

	private List<ParticipantContact> participantContacts;

	public List<ParticipantContact> getParticipantContacts() {
		if (this.participantContacts == null){
			this.participantContacts = new ArrayList<>();
		}
		return participantContacts;
	}
}

package org.ctyc.mgt.summercamp.accommodation;

import java.util.ArrayList;
import java.util.Collection;

import org.ctyc.mgt.model.summercamp.Participant;
import org.ctyc.mgt.model.summercamp.ParticipantContact;
import org.springframework.util.CollectionUtils;

public class ParticipantContactMapper {
	
	public Collection<ParticipantContact> mapToParticipantContacts(Collection<Participant> participants){
		
		if (CollectionUtils.isEmpty(participants)){
			return new ArrayList<>();
		}
		
		Collection<ParticipantContact> participantContacts = new ArrayList<>();
		
		for (Participant participant : participants){
			participantContacts.add(mapToParticipantContact(participant));
		}
		
		return participantContacts;
		
	}

	public ParticipantContact mapToParticipantContact(Participant participant){
		
		ParticipantContact contact = new ParticipantContact();
		
		contact.setName(participant.getName());
		contact.setAccommodationDay("全營");
		contact.setContact("");
		contact.setGoTogether(true);
		contact.setLeaveTogether(true);
		contact.setGroupNumber(participant.getGroupNumber());
		contact.setRemark("");
		
		return contact;
	}
}

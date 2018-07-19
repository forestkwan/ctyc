package org.ctyc.mgt.summercamp.accommodation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ctyc.mgt.model.summercamp.Participant;
import org.ctyc.mgt.model.summercamp.ParticipantContact;
import org.springframework.util.CollectionUtils;

public class ParticipantContactMapper {
	
	public List<ParticipantContact> mapToParticipantContacts(Collection<Participant> participants){
		
		if (CollectionUtils.isEmpty(participants)){
			return new ArrayList<>();
		}
		
		List<ParticipantContact> participantContacts = new ArrayList<>();
		
		for (Participant participant : participants){
			participantContacts.add(mapToParticipantContact(participant));
		}
		
		return participantContacts;
		
	}

	public ParticipantContact mapToParticipantContact(Participant participant){
		
		ParticipantContact contact = new ParticipantContact();
		
		contact.setName(participant.getName());
		contact.setAccommodationName(participant.getAccommodationRoom());
		contact.setAccommodationDay(this.deduceToAccommodationDayLabel(participant));
		contact.setContact(participant.getPersonalContact());
		contact.setGoTogether(participant.isGoTogether());
		contact.setLeaveTogether(participant.isLeaveTogether());
		contact.setGroupNumber(participant.getGroupNumber());
		contact.setRemark(participant.getCampRemark());
		contact.setGroupMentor(participant.isGroupMentor());
		
		return contact;
	}
	
	private String deduceToAccommodationDayLabel(Participant participant){
		
		if (participant.isFullCamp()){
			return "全營";
		}
		
		String label = "";
		if (participant.getAccommodationAvailability(1) != null
				&& participant.getAccommodationAvailability(1).isJoin()){
			label += "日";
		} else {
			label += "　";
		}
		
		if (participant.getAccommodationAvailability(2) != null
				&& participant.getAccommodationAvailability(2).isJoin()){
			label += "一";
		} else {
			label += "　";
		}
		
		if (participant.getAccommodationAvailability(3) != null
				&& participant.getAccommodationAvailability(3).isJoin()){
			label += "二";
		} else {
			label += "　";
		}
		
		if (participant.getAccommodationAvailability(4) != null
				&& participant.getAccommodationAvailability(4).isJoin()){
			label += "三";
		} else {
			label += "　";
		}
		
		return label;
	}
}

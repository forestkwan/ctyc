package org.ctyc.mgt.summercamp.accommodation;

import java.util.Collection;

import org.ctyc.mgt.model.summercamp.AccommodationContact;
import org.ctyc.mgt.model.summercamp.CampSite;
import org.ctyc.mgt.model.summercamp.Participant;
import org.ctyc.mgt.model.summercamp.ParticipantContact;
import org.ctyc.mgt.summercamp.SummerCampService;

public class AccommodationService {

	public AccommodationContact getAccommodationContact(String camp){
		
		AccommodationContact accommodationContact = new AccommodationContact();
		
		SummerCampService summerCampService = SummerCampService.getInstance();
		CampSite campSite = summerCampService.getCampSite(camp);
		
		Collection<Participant> participants = campSite.getParticipants();
		ParticipantContactMapper participantContactMapper = new ParticipantContactMapper();
		Collection<ParticipantContact> participantContacts = participantContactMapper.mapToParticipantContacts(participants);
		accommodationContact.getParticipantContacts().addAll(participantContacts);
		
		return accommodationContact;
		
	}
}

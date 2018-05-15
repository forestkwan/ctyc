package org.ctyc.mgt.summercamp.accommodation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ctyc.mgt.model.summercamp.AccommodationContact;
import org.ctyc.mgt.model.summercamp.CampSite;
import org.ctyc.mgt.model.summercamp.ContactGroup;
import org.ctyc.mgt.model.summercamp.Participant;
import org.ctyc.mgt.model.summercamp.ParticipantContact;
import org.ctyc.mgt.summercamp.SummerCampService;
import org.springframework.util.CollectionUtils;

public class AccommodationService {

	public AccommodationContact getAccommodationContact(String camp){
		
		AccommodationContact accommodationContact = new AccommodationContact();
		
		SummerCampService summerCampService = SummerCampService.getInstance();
		CampSite campSite = summerCampService.getCampSite(camp);
		
		List<Participant> participants = new ArrayList<>(campSite.getParticipants());
		ParticipantContactMapper participantContactMapper = new ParticipantContactMapper();
		List<ParticipantContact> participantContacts = participantContactMapper.mapToParticipantContacts(participants);
		
		List<ContactGroup> contactGroups = this.createContactGroups(participantContacts);
		
		accommodationContact.getParticipantContacts().addAll(participantContacts);
		accommodationContact.getContactGroups().addAll(contactGroups);
		
		return accommodationContact;
		
	}

	private List<ContactGroup> createContactGroups(
			List<ParticipantContact> participantContacts) {
		
		if (CollectionUtils.isEmpty(participantContacts)){
			return new ArrayList<>();
		}
		
		Map<Integer, ContactGroup> groupNumberAndContactGroupMap = new HashMap<>();
		
		for (ParticipantContact participantContact : participantContacts){
			
			int groupNumber = participantContact.getGroupNumber();
			
			if (groupNumberAndContactGroupMap.containsKey(groupNumber)){
				ContactGroup contactGroup = groupNumberAndContactGroupMap.get(groupNumber);
				contactGroup.getParticipantContacts().add(participantContact);
			}else {
				ContactGroup contactGroup = new ContactGroup();
				contactGroup.setGroupCode(String.valueOf(groupNumber));
				contactGroup.getParticipantContacts().add(participantContact);
				
				groupNumberAndContactGroupMap.put(groupNumber, contactGroup);
			}
			
		}
		
		List<ContactGroup> contactGroups = new ArrayList<>(groupNumberAndContactGroupMap.values());
		contactGroups.sort((ContactGroup o1, ContactGroup o2)->o1.getGroupCode().compareTo(o2.getGroupCode()));	
		
		return contactGroups;
	}
}

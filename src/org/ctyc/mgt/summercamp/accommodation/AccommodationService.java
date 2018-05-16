package org.ctyc.mgt.summercamp.accommodation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
		for (ContactGroup contactGroup : contactGroups){
			contactGroup = this.setMentorCount(contactGroup);
			contactGroup = this.setNonMentorCount(contactGroup);
			contactGroup = this.setParticipantSequenceCode(contactGroup);
			contactGroup = this.sortParticipantContactsBySequenceCode(contactGroup);
		}
		
		contactGroups.sort((ContactGroup o1, ContactGroup o2)->o1.getGroupCode().compareTo(o2.getGroupCode()));	
		
		return contactGroups;
	}

	private ContactGroup setMentorCount(ContactGroup contactGroup) {
		
		if (contactGroup == null){
			return contactGroup;
		}
		
		int count = 0;
		for (ParticipantContact participantContact : contactGroup.getParticipantContacts()){
			
			if (participantContact.isGroupMentor()){
				count++;
			}
		}
		contactGroup.setMentorCount(count);
		return contactGroup;
	}
	
	private ContactGroup setNonMentorCount(ContactGroup contactGroup) {
		
		if (contactGroup == null){
			return contactGroup;
		}
		
		int count = 0;
		for (ParticipantContact participantContact : contactGroup.getParticipantContacts()){
			
			if (!participantContact.isGroupMentor()){
				count++;
			}
		}
		contactGroup.setNonMentorCount(count);
		return contactGroup;
	}
	
	private ContactGroup setParticipantSequenceCode(ContactGroup contactGroup) {
		
		if (contactGroup == null){
			return contactGroup;
		}
		
		List<ParticipantContact> mentorContacts = contactGroup.getParticipantContacts().stream()
                .filter(participantContact -> participantContact.isGroupMentor())
                .collect(Collectors.toList());
		
		mentorContacts.forEach(mentor -> mentor.setSequenceCode("*"));
		
		List<ParticipantContact> nonMentorContacts = contactGroup.getParticipantContacts().stream()
                .filter(participantContact -> !participantContact.isGroupMentor())
                .collect(Collectors.toList());
		
		for (int seq = 0; seq < nonMentorContacts.size(); seq++){
			nonMentorContacts.get(seq).setSequenceCode(String.valueOf(seq + 1));		
		}
		
		return contactGroup;
	}
	
	private ContactGroup sortParticipantContactsBySequenceCode(ContactGroup contactGroup) {
		
		if (contactGroup == null || CollectionUtils.isEmpty(contactGroup.getParticipantContacts())){
			return contactGroup;
		}
		
		Comparator<ParticipantContact> comparator = ParticipantContact.getSequenceCodeComparator();
		Collections.sort(contactGroup.getParticipantContacts(), comparator);
		return contactGroup;
	}
}

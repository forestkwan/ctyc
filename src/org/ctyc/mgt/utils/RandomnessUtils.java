package org.ctyc.mgt.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import org.ctyc.mgt.model.Gender;
import org.ctyc.mgt.model.summercamp.DineTableGroup;
import org.ctyc.mgt.model.summercamp.Participant;
import org.springframework.util.CollectionUtils;

public class RandomnessUtils {
	
	public static DineTableGroup pickRandomDineTableGroup(Collection<DineTableGroup> dineTableGroups, Random randomObj){
		
		if (CollectionUtils.isEmpty(dineTableGroups) || randomObj == null){
			return null;
		}
		
		DineTableGroup target = null;
		int targetIndex = randomObj.nextInt(dineTableGroups.size());
		int index = 0;
		for (DineTableGroup dineTableGroup : dineTableGroups){
			if (index == targetIndex){
				target = dineTableGroup;
				break;
			}
			
			index++;
		}
		return target;
	}
	
	public static Participant pickRandomParticipant(Collection<Participant> participants, Random randomObj){
		
		if (CollectionUtils.isEmpty(participants) || randomObj == null){
			return null;
		}

		int item = randomObj.nextInt(participants.size());
		int i = 0;
		
		for(Participant participant : participants)
		{
			if (i != item){
				i = i + 1;
				continue;
			}
			return participant;
		}
		
		return null;
	}
	
	public static Collection<Participant> pickRandomMultiParticipant(Collection<Participant> participants, Random randomObj){
		
		if (CollectionUtils.isEmpty(participants) || participants.size() < 2|| randomObj == null){
			return null;
		}
		
		if (participants.size() == 3){
			return participants;
		}

		int item1 = randomObj.nextInt(participants.size());
		int item2 = item1;
		do {
			item2 = randomObj.nextInt(participants.size());
		} while (item2 == item1);
		
		int i = 0;
		
		Collection<Participant> resultParticipants = new ArrayList<Participant>();
		for(Participant participant : participants)
		{
			if (i == item1 || i == item2){
				resultParticipants.add(participant);
			}
			i++;
		}
		
		return resultParticipants;
	}
	
	public static Collection<Participant> pickRandomMultiParticipant(Collection<Participant> participants, int numberOfPick, Random randomObj){
		
		if (CollectionUtils.isEmpty(participants) || randomObj == null){
			return new HashSet<Participant>();
		}
		
		if (participants.size() <= numberOfPick){
			return participants;
		}
		
		Collection<Participant> resultParticipants = new HashSet<Participant>();
		do {
			
			int index = randomObj.nextInt(participants.size());
			
			int i = 0;
			for(Participant participant : participants)
			{
				if (i == index || i == index){
					resultParticipants.add(participant);
					break;
				}
				i++;
			}
			
		}while (resultParticipants.size() < numberOfPick);
		
		return resultParticipants;
	}
	
	public static int randInt(int min, int max, Random randomObject) {

	    // NOTE: This will (intentionally) not run as written so that folks
	    // copy-pasting have to think about how to initialize their
	    // Random instance.  Initialization of the Random instance is outside
	    // the main scope of the question, but some decent options are to have
	    // a field that is initialized once and then re-used as needed or to
	    // use ThreadLocalRandom (if using at least Java 1.7).
		
	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = randomObject.nextInt((max - min) + 1) + min;

	    return randomNum;
	}

	public static Collection<Participant> pickRandomParticipantGroupWithGender(
			Map<Integer, Collection<Participant>> groupNumberParticipantMap,
			int genderBalance,
			int pickNumber,
			Random randomObj) {
		
		if (CollectionUtils.isEmpty(groupNumberParticipantMap.entrySet())){
			return new HashSet<Participant>();
		}
		
		/* Select the potential groups that fulfill the criteria */
		Collection<Collection<Participant>> potentialGroups = new ArrayList<Collection<Participant>>();
		for (Collection<Participant> participantGroup : groupNumberParticipantMap.values()){
			if (participantGroup.size() < pickNumber){
				continue;
			}
			
			if (genderBalance == 0){
				potentialGroups.add(participantGroup);
			}else {
				
				int tempGenderBalance = 0 ;
				for (Participant participant : participantGroup){
					if (participant.getGender() == Gender.MALE){
						tempGenderBalance++;
					}else {
						tempGenderBalance--;
					}
				}
				
				if (tempGenderBalance <= 0 && genderBalance < 0){
					potentialGroups.add(participantGroup);
				}
				
				if (tempGenderBalance >= 0 && genderBalance > 0){
					potentialGroups.add(participantGroup);
				}
			}
		}
		
		/* Randomly pick a group of participant from potential group */
		if (CollectionUtils.isEmpty(potentialGroups) || randomObj == null){
			return new HashSet<Participant>();
		}

		int item = randomObj.nextInt(potentialGroups.size());
		int i = 0;
		
		for(Collection<Participant> participantGroup : potentialGroups)
		{
			if (i != item){
				i = i + 1;
				continue;
			}
			return participantGroup;
		}
		
		return new HashSet<Participant>();
	}
	
	public static Collection<Participant> pickRandomParticipantGroupWithoutGender(
			Map<Integer, Collection<Participant>> groupNumberParticipantMap,
			int pickNumber,
			Random randomObj) {
		
		if (CollectionUtils.isEmpty(groupNumberParticipantMap.entrySet())){
			return new HashSet<Participant>();
		}
		
		/* Select the potential groups that fulfill the criteria */
		Collection<Collection<Participant>> potentialGroups = new ArrayList<Collection<Participant>>();
		for (Collection<Participant> participantGroup : groupNumberParticipantMap.values()){
			if (participantGroup.size() < pickNumber){
				continue;
			}
			
			potentialGroups.add(participantGroup);
		}
		
		/* Randomly pick a group of participant from potential group */
		if (CollectionUtils.isEmpty(potentialGroups) || randomObj == null){
			return new HashSet<Participant>();
		}

		int item = randomObj.nextInt(potentialGroups.size());
		int i = 0;
		
		for(Collection<Participant> participantGroup : potentialGroups)
		{
			if (i != item){
				i = i + 1;
				continue;
			}
			return participantGroup;
		}
		
		return new HashSet<Participant>();
	}
}

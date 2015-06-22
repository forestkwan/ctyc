package org.ctyc.mgt.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

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
}

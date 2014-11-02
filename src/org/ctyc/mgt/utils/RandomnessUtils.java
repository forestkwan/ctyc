package org.ctyc.mgt.utils;

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
}

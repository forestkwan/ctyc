package org.ctyc.mgt.utils;

import java.util.Collection;
import java.util.Random;
import java.util.Stack;

import org.ctyc.mgt.model.summercamp.Participant;
import org.springframework.util.CollectionUtils;

public class ParticipantCollectionUtils {
	
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
	
	public static Participant popRandomParticipant(Stack<Participant> participants, Random randomObj){
		
		if (CollectionUtils.isEmpty(participants) || randomObj == null){
			return null;
		}

		int item = randomObj.nextInt(participants.size());

		for(int i=0; i<participants.size(); i++)
		{
			if (i != item){
				continue;
			}
			return participants.remove(i);
		}
		
		return null;
	}

}

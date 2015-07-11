package org.ctyc.mgt.summercamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.ctyc.mgt.model.Believer;
import org.ctyc.mgt.model.FamilyGroup;
import org.ctyc.mgt.model.Gender;
import org.ctyc.mgt.model.summercamp.Participant;
import org.springframework.util.CollectionUtils;

public class SimulatedAnnealing {
	private static final int tableSize = 8;
	private static final double e = 2.71828;
	
    public static Collection<Participant> execute(ArrayList<Participant> sol) {
    	int oldCost = cost(sol);
    	double currentTemp = 1.0;
    	double minTemp = 0.000001;
    	double alpha = 0.9;
    	Random random = new Random();
    	
    	while (currentTemp > minTemp) {
    		System.out.println("Temp: " + currentTemp);
    		int i = 1;
    		while (i <= 1000) {
    			ArrayList<Participant> newSol = neighbor(sol);
    			int newCost = cost(newSol);
    			double ap = acceptanceProb(oldCost, newCost, currentTemp);
    			if (ap > random.nextDouble()) {
    				sol = newSol;
    				oldCost = newCost;
    			}
    			i++;
    			System.out.println("Cost: " + oldCost);
    		}
    		currentTemp = currentTemp * alpha;
    	}
    	
    	return sol;
    }
    
    private static int cost(ArrayList<Participant> candidate) {
		int score = 0;
		int numOfTable = (int)Math.ceil(candidate.size() / (1.0 * tableSize));
		for (int i = 0; i < numOfTable; i++) {
			int tableScore = 0;
			
			int startIndex = i * tableSize;
			int endIndex = i * tableSize + tableSize;
			if (endIndex > candidate.size()) {
				endIndex = candidate.size();
			}
			
			ArrayList<Participant> table = new ArrayList<Participant>(candidate.subList(startIndex, endIndex));
			
			// check M/F balance
			int genderScore = 0;
			int maleCount = 0;
			int femaleCount = 0;
			for (Participant p : table) {
				if (p.getGender() == Gender.MALE) {
					maleCount++;
				} else {
					femaleCount++;
				}
			}
			int diff = Math.abs(maleCount - femaleCount);
			genderScore += diff * diff;
			
			// check teacher existence
			int mentorScore = 10000;
			for (Participant p : table) {
				if (p.isMentor()) {
					mentorScore = 0;
					break;
				}
			}
			
			// check family
			int familyScore = 0;
			Collection<FamilyGroup> familyGroups = new HashSet<FamilyGroup>();
			for (Participant p : table) {
				if (p.getFamilyGroup() == null){
					continue;
				}
				familyGroups.add(p.getFamilyGroup());
			}
			
			/* All family group member must be exist in the table */
			for (FamilyGroup familyGroup : familyGroups){
				for (String believerId : familyGroup.getBelieverIds()){
					if (!isExistInTable(table, believerId)){
						familyScore += 1000000;
					}
				}
			}
			
			tableScore = genderScore + mentorScore + familyScore;
			score += tableScore;
		}
		
		return score;
    }
    
	public static boolean isExistInTable(ArrayList<Participant> participants,String believerId) {

		if (CollectionUtils.isEmpty(participants)) {
			return false;
		}

		for (Believer believer : participants) {
			if (StringUtils.equalsIgnoreCase(believer.getId(), believerId)) {
				return true;
			}
		}

		return false;
	}
    
    private static ArrayList<Participant> neighbor(ArrayList<Participant> pool) {
    	ArrayList<Participant> newSol = pool;
    	int solLength = newSol.size();
    	
    	Random random = new Random();
//		int n = random.nextInt(100) + 1; // 1 - 100 inclusively
		for (int k = 0; k < 10; k++) {
			int i = random.nextInt(solLength);
			int j;
			do {
				j = random.nextInt(solLength);
			} while (i == j);
			Collections.swap(newSol, i, j);
		}
		
		return newSol;
    }
    
    private static double acceptanceProb(int oldCost, int newCost, double currentTemp) {
    	int costDiff = newCost - oldCost;
    	double power = costDiff / currentTemp;
    	return Math.pow(e, power);
    }
}

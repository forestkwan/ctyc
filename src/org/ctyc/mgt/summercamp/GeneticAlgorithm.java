package org.ctyc.mgt.summercamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.ctyc.mgt.model.Believer;
import org.ctyc.mgt.model.FamilyGroup;
import org.ctyc.mgt.model.Gender;
import org.ctyc.mgt.model.summercamp.Participant;
import org.springframework.util.CollectionUtils;

public class GeneticAlgorithm {
	
	public static void mutate(ArrayList<ArrayList<Participant>> pool) {
		// probability = 0.1
		Random rand = new Random();
		
		for (ArrayList<Participant> list : pool) {
			int n = rand.nextInt(100) + 1; // 1 - 100 inclusively
			if (n <= 20) {
				for (int k = 0; k < 4; k++) {
					Random randA = new Random();
					Random randB = new Random();
					int i = randA.nextInt(list.size());
					int j = randB.nextInt(list.size());
					while (i == j) {
						j = randB.nextInt(list.size());
					}
					Collections.swap(list, i, j);
				}
			}
		}
	}
	
	public static int fitness(ArrayList<Participant> candidate) {
		int score = 0;
		int numOfTable = (int)Math.ceil(candidate.size() / 8.0);
		for (int i = 0; i < numOfTable; i++) {
			int tableScore = 0;
			
			int startIndex = i * 8;
			int endIndex = i * 8 + 8;
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
	
	public static boolean isExistInTable(ArrayList<Participant> participants, String believerId){
		
		if (CollectionUtils.isEmpty(participants)){
			return false;
		}
		
		for (Believer believer :  participants){
			if (StringUtils.equalsIgnoreCase(believer.getId(), believerId)){
				return true;
			}
		}
		
		return false;
		
	}
	
	public static void mate(ArrayList<ArrayList<Participant>> pool) {
		int numOfParent = pool.size();
		Collection<ArrayList<Participant>> offspringPool = new ArrayList<ArrayList<Participant>>();
		
		Random rand = new Random();
		for (int i = 0; i < numOfParent / 2; i++) {
			ArrayList<Participant> mother = ((ArrayList<ArrayList<Participant>>)pool).get(i);
			ArrayList<Participant> father = ((ArrayList<ArrayList<Participant>>)pool).get(i + numOfParent/2);
			
			ArrayList<Participant> son = new ArrayList<Participant>(father);	
			ArrayList<Participant> daughter = new ArrayList<Participant>(mother);
			
			// crossover probability = 0.6
//			int n = rand.nextInt(10) + 1;
			int length = mother.size();
			int crossoverPoint = rand.nextInt(length) + 1;
			
			// keep first portion of father
			son.removeAll(father.subList(crossoverPoint, length));
			ArrayList<Participant> motherClone = new ArrayList<Participant>(mother);
			motherClone.removeAll(father.subList(0, crossoverPoint));
			son.addAll(motherClone);
			
			// keep first portion of mother
			daughter.removeAll(mother.subList(crossoverPoint, length));
			ArrayList<Participant> fatherClone = new ArrayList<Participant>(father);
			fatherClone.removeAll(mother.subList(0, crossoverPoint));
			daughter.addAll(fatherClone);
			
			offspringPool.add(son);
			offspringPool.add(daughter);
		}
		
//		pool.clear();
		pool.addAll(offspringPool);
		
		//Sorting
		Collections.sort((ArrayList<ArrayList<Participant>>)pool, new Comparator<ArrayList<Participant>>() {
				@Override
				public int compare(ArrayList<Participant> o1,
						ArrayList<Participant> o2) {
					
					return fitness(o1) - fitness(o2);
				}
		    });
		
		// restore the population size
		pool.subList(numOfParent, pool.size()).clear();
	}
	
	public static Collection<Participant> applyGA(Collection<Participant> participants) {	
		
		// initialization of the first pool
		ArrayList<ArrayList<Participant>> initPopulation = new ArrayList<ArrayList<Participant>>();
		int populationSize = 100;
		for (int i = 0; i < populationSize; i++) {
			ArrayList<Participant> tempList = new ArrayList<Participant>();
			tempList.addAll(participants);
			Collections.shuffle((ArrayList<Participant>) tempList);
			initPopulation.add(tempList);
		}
		
		for (int i = 0; i < 1000; i++) {
			Collections.shuffle(initPopulation);
						
			mate(initPopulation);
			
			mutate(initPopulation);
			
			System.out.println("[Round " + i + "] Best score: " + fitness(initPopulation.iterator().next()));
		}	
		
		int maleCount = 0;
		int femaleCount = 0;
		for (Participant participant : participants) {
			if (participant.getGender() == Gender.MALE) {
				maleCount++;
			} else {
				femaleCount++;
			}
		}
		
		System.out.println(String.format("male: %d, female: %d", maleCount, femaleCount));
		
		return initPopulation.iterator().next();
	}
}

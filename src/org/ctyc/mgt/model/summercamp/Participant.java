package org.ctyc.mgt.model.summercamp;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.ctyc.mgt.model.Believer;

public class Participant extends Believer {

	private static final long serialVersionUID = 1L;
	private int groupNumber;
	private boolean isGroupMentor;
	private Set<DineAvailability> dineAvailabilitys;
	private Integer specialGroup;

	public Participant() {

	}

	public int getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(int groupNumber) {
		this.groupNumber = groupNumber;
	}

	public boolean isGroupMentor() {
		return isGroupMentor;
	}

	public void setGroupMentor(boolean isGroupMentor) {
		this.isGroupMentor = isGroupMentor;
	}
	
	public Integer getSpecialGroup() {
		return specialGroup;
	}

	public void setSpecialGroup(Integer specialGroup) {
		this.specialGroup = specialGroup;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Set<DineAvailability> getDineAvailabilitys() {
		if (this.dineAvailabilitys == null){
			this.dineAvailabilitys = new HashSet<DineAvailability>();
		}
		return dineAvailabilitys;
	}
	
	public void setDineTableNumber(int numberOfDay, String timeOfDay, int tableNumber){
		for (DineAvailability dineAvailability : this.getDineAvailabilitys()){
			if (dineAvailability.getNumberOfDay() == numberOfDay &&
					StringUtils.equalsIgnoreCase(dineAvailability.getTimeOfDay(), timeOfDay)){
				dineAvailability.setAssignedTableNumber(tableNumber);
			}
		}
	}
	
	public int countAvailableDine(int day){
		int count = 0;
		
		for (DineAvailability dineAvailability : this.dineAvailabilitys){
			if (dineAvailability.getNumberOfDay() != day){
				continue;
			}
			
			if (dineAvailability.isJoin()){
				count++;
			}
		}
		
		return count;
	}

}

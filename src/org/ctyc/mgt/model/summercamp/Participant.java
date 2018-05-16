package org.ctyc.mgt.model.summercamp;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.ctyc.mgt.model.Believer;
import org.springframework.util.CollectionUtils;

public class Participant extends Believer {

	private static final long serialVersionUID = 1L;
	private int groupNumber;
	private boolean isGroupMentor;
	private Set<DineAvailability> dineAvailabilitys;
	private Set<AccommodationAvailability> accommodationAvailabilitys;
	private Integer specialGroup;
	private String personalContact;
	private String parentContact;
	private String accommodation;
	private String campRemark;
	private boolean goTogether;
	private boolean leaveTogether;

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
			this.dineAvailabilitys = new HashSet<>();
		}
		return dineAvailabilitys;
	}
	
	public Set<AccommodationAvailability> getAccommodationAvailabilitys() {
		if (this.accommodationAvailabilitys == null){
			this.accommodationAvailabilitys = new HashSet<>();
		}
		return accommodationAvailabilitys;
	}

	public String getPersonalContact() {
		return personalContact;
	}

	public void setPersonalContact(String personalContact) {
		this.personalContact = personalContact;
	}

	public String getParentContact() {
		return parentContact;
	}

	public void setParentContact(String parentContact) {
		this.parentContact = parentContact;
	}

	public String getAccommodation() {
		return accommodation;
	}

	public void setAccommodation(String accommodation) {
		this.accommodation = accommodation;
	}

	public String getCampRemark() {
		return campRemark;
	}

	public void setCampRemark(String campRemark) {
		this.campRemark = campRemark;
	}

	public boolean isGoTogether() {
		return goTogether;
	}

	public void setGoTogether(boolean goTogether) {
		this.goTogether = goTogether;
	}

	public boolean isLeaveTogether() {
		return leaveTogether;
	}

	public void setLeaveTogether(boolean leaveTogether) {
		this.leaveTogether = leaveTogether;
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
	
	public int countTotalAvailableDine(){
		int count = 0;
		
		for (DineAvailability dineAvailability : this.dineAvailabilitys){
			
			if (dineAvailability.isJoin()){
				count++;
			}
		}
		
		return count;
	}
	
	public boolean isFullCamp(){
		
		for (AccommodationAvailability availability : this.accommodationAvailabilitys){
			
			if (!availability.isJoin()){
				return false;
			}
		}
		return true;
	}
	
	public AccommodationAvailability getAccommodationAvailability(int numberOfDay){
		
		for (AccommodationAvailability availability : this.accommodationAvailabilitys){
			
			if (availability.getNumberOfDay() == numberOfDay){
				return availability;
			}
		}
		return null;
	}
	
	public boolean isSpecialParticipant(){
		if (this.getSpecialGroup() != null && this.getSpecialGroup().intValue() >= 1){
			return true;
		}
		return false;
	}
	
	public boolean isNormalParticipant(){
		return !this.isSpecialParticipant();
	}
	
	public boolean hasFamilyGroup(){
		if (this.getFamilyGroup() == null || CollectionUtils.isEmpty(this.getFamilyGroup().getBelieverIds())
				|| StringUtils.isBlank(this.getFamilyGroup().getFamilyId())){
			return false;
		}
		return true;
	}

}

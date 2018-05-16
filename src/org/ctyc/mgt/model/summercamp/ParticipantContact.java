package org.ctyc.mgt.model.summercamp;

import java.util.Comparator;

public class ParticipantContact {

	private String name;
	private int groupNumber;
	private String sequenceCode;
	private String accommodationName;
	private String accommodationDay;
	private String remark;
	private boolean goTogether;
	private boolean leaveTogether;
	private String contact;
	private boolean groupMentor;
	
	public static Comparator<ParticipantContact> getSequenceCodeComparator(){
		
		Comparator<ParticipantContact> comparator = new Comparator<ParticipantContact>(){

			@Override
			public int compare(ParticipantContact o1, ParticipantContact o2) {
				if ("*".equals(o1.getSequenceCode()) || "*".equals(o2.getSequenceCode())){
					return o1.getSequenceCode().compareTo(o2.getSequenceCode());
				}
				
				Integer sequenceNum1 = Integer.parseInt(o1.getSequenceCode());
				Integer sequenceNum2 = Integer.parseInt(o2.getSequenceCode());
				return sequenceNum1.compareTo(sequenceNum2);
			}
			
		};
		
		return comparator;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getGroupNumber() {
		return groupNumber;
	}
	public void setGroupNumber(int groupNumber) {
		this.groupNumber = groupNumber;
	}
	public String getSequenceCode() {
		return sequenceCode;
	}
	public void setSequenceCode(String sequenceCode) {
		this.sequenceCode = sequenceCode;
	}
	public String getAccommodationName() {
		return accommodationName;
	}
	public void setAccommodationName(String accommodationName) {
		this.accommodationName = accommodationName;
	}
	public String getAccommodationDay() {
		return accommodationDay;
	}
	public void setAccommodationDay(String accommodationDay) {
		this.accommodationDay = accommodationDay;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public boolean isGroupMentor() {
		return groupMentor;
	}
	public void setGroupMentor(boolean groupMentor) {
		this.groupMentor = groupMentor;
	}
}

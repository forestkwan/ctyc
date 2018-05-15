package org.ctyc.mgt.model.summercamp;

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
}

package org.ctyc.mgt.model;

import java.io.Serializable;

public class Believer implements Serializable {

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Believer)) {
			return false;
		}
		Believer other = (Believer) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private Gender gender;
	private int yearOfBirth;
	private String sundaySchoolClass;
	private FamilyGroup familyGroup;

	private boolean isMentor;
	
	public Believer(){
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public int getYearOfBirth() {
		return yearOfBirth;
	}

	public void setYearOfBirth(int yearOfBirth) {
		this.yearOfBirth = yearOfBirth;
	}

	public String getSundaySchoolClass() {
		return sundaySchoolClass;
	}

	public void setSundaySchoolClass(String sundaySchoolClass) {
		this.sundaySchoolClass = sundaySchoolClass;
	}

	public FamilyGroup getFamilyGroup() {
		return familyGroup;
	}
	
	public void setFamilyGroup(FamilyGroup familyGroup){
		this.familyGroup = familyGroup;
	}

	public boolean isMentor() {
		return isMentor;
	}

	public void setMentor(boolean isMentor) {
		this.isMentor = isMentor;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}

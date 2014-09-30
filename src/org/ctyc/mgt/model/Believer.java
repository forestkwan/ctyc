package org.ctyc.mgt.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

enum Sex {
    MALE, FEMALE;
}

public class Believer implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private Sex sex;
	private int yearOfBirth;
	private String sundaySchoolClass;
	private Collection<Believer> relatives;

	private boolean isMentor;
	
	public Believer(){
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
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

	public Collection<Believer> getRelatives() {
		if (this.relatives == null){
			this.relatives = new ArrayList<Believer>();
		}
		return relatives;
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

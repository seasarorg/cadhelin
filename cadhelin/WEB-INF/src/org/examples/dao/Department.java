package org.examples.dao;

public class Department {
	private int deptno;
	private String name;
	private String location;
	
	public Department(int deptno, String name, String location) {
		this.deptno = deptno;
		this.name = name;
		this.location = location;
	}
	public int getDeptno() {
		return deptno;
	}
	public void setDeptno(int deptno) {
		this.deptno = deptno;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
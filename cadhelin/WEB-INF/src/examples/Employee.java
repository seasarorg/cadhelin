package examples;

import java.util.Date;

public class Employee {
	private int empno;
	private String name;
	private Date date;
	public Employee(){
	}
	public Employee(int empno, String name, Date hireDate, float salary) {
		this.empno = empno;
		this.name = name;
	}
	public int getEmpno() {
		return empno;
	}
	public void setEmpno(int empno) {
		this.empno = empno;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
}

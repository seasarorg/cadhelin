package examples;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

public class EmployeeDaoImpl implements EmployeeDao {
	HashMap <Integer,Employee> employees 
		= new HashMap<Integer,Employee>();
	public EmployeeDaoImpl() {
		employees.put(1,new Employee(1,"test",new Date()));
	}
	public Collection<Employee> findAll() {
		return employees.values();
	}

	public Employee getEmployee(int empno) {
		return employees.get(empno);
	}

	public void addEmployee(Employee employee) {
		employees.put(employee.getEmpno(),employee);
	}

	public void updateEmployee(Employee employee) {
		employees.put(employee.getEmpno(),employee);
	}

	public void deleteEmployee(int empno) {
		employees.remove(empno);
	}

}

package examples;

import java.util.Collection;

public interface EmployeeDao {
	public Collection<Employee> findAll();
	public Employee getEmployee(int empno);
	public void addEmployee(Employee employee);
	public void updateEmployee(Employee employee);
	public void deleteEmployee(int empno);
}

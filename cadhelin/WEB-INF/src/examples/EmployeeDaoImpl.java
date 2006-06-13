package examples;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.ListUtils;

public class EmployeeDaoImpl implements EmployeeDao {
	HashMap <Integer,Employee> employees 
		= new HashMap<Integer,Employee>();
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

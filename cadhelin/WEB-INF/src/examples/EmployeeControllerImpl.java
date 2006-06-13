package examples;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.seasar.cadhelin.Action;
import org.seasar.cadhelin.Input;
import org.seasar.cadhelin.ResultName;

public class EmployeeControllerImpl {
	Map<Integer,Employee> employees = new HashMap<Integer,Employee>();
	
	@Action
	public Collection<Employee> index(){
		return employees.values();
	}
	
	@Action
	public void addForm(){
	}
	
	@Action("employee")
	@Input("addForm")
	public void add(Employee employee){
		employees.put(employee.getEmpno(),employee);
		index();
	}
	@Action("empno")
	public void delete(int empno){
		employees.remove(empno);
		index();
	}
	@Action("empno")
	@ResultName("employee")
	public Employee updateForm(int empno){
		return employees.get(empno);
	}
	@Action("employee")
	public void update(Employee employee){
		employees.put(employee.getEmpno(),employee);
		index();
	}
}

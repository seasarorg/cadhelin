package examples;

import java.util.Collection;

import org.seasar.cadhelin.Action;
import org.seasar.cadhelin.Input;
import org.seasar.cadhelin.ResultName;
import org.seasar.cadhelin.Role;

public class EmployeeControllerImpl {
	EmployeeDao empdao;
	public EmployeeControllerImpl(EmployeeDao empdao){
		this.empdao = empdao;
	}
	@Action
	public Collection<Employee> index(){
		return empdao.findAll();
	}
	
	@Action
	public void addForm(){
	}
	
	@Action("employee")
	@Input("addForm")
	public void add(Employee employee){
		empdao.addEmployee(employee);
		index();
	}
	@Action("empno")
	@Role("admin")
	public void delete(int empno){
		empdao.deleteEmployee(empno);
		index();
	}
	@Action("empno")
	@ResultName("employee")
	public Employee updateForm(int empno){
		return empdao.getEmployee(empno);
	}
	@Action("employee")
	public void update(Employee employee){
		empdao.updateEmployee(employee);
		updateForm(employee.getEmpno());
	}
}

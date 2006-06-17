package examples;

import java.util.Collection;

import org.seasar.cadhelin.annotation.ResultName;

public class EmployeeControllerImpl {
	EmployeeDao empdao;
	public EmployeeControllerImpl(EmployeeDao empdao){
		this.empdao = empdao;
	}
	@ResultName("employees")
	public Collection<Employee> showIndex(){
		return empdao.findAll();
	}
	
	public void showAddForm(){
	}
	
	public void doAdd(Employee employee){
		empdao.addEmployee(employee);
		showIndex();
	}
	public void showDelete(int empno){
		empdao.deleteEmployee(empno);
		showIndex();
	}

	@ResultName("employee")
	public Employee showUpdateForm(int empno){
		return empdao.getEmployee(empno);
	}
	public void doUpdate(Employee employee){
		empdao.updateEmployee(employee);
		showIndex();
	}
}

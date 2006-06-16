package examples;

import java.util.Collection;

import org.seasar.cadhelin.Action;
import org.seasar.cadhelin.ResultName;

public class EmployeeControllerImpl {
	EmployeeDao empdao;
	public EmployeeControllerImpl(EmployeeDao empdao){
		this.empdao = empdao;
	}
	@ResultName("employees")
	public Collection<Employee> getIndex(){
		return empdao.findAll();
	}
	
	public void getAddForm(){
	}
	
	public void postAdd(Employee employee){
		empdao.addEmployee(employee);
		getIndex();
	}
	public void postDelete(int empno){
		empdao.deleteEmployee(empno);
		getIndex();
	}

	@ResultName("employee")
	public Employee getUpdateForm(int empno){
		return empdao.getEmployee(empno);
	}
	public void postUpdate(Employee employee){
		empdao.updateEmployee(employee);
		getIndex();
	}
}

/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package examples.employee;

import java.util.Collection;

import org.seasar.cadhelin.Param;
import org.seasar.cadhelin.annotation.Render;
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
	
	@Render("Employee")
	@ResultName("employee")
	public Employee showAdd(){
		return new Employee();
	}
	public void doAdd(Employee employee){
		empdao.addEmployee(employee);
		doTest();
	}
	public void doTest(){
		showIndex();		
	}
	public void showDelete(int empno){
		Employee employee = empdao.getEmployee(empno);
		empdao.deleteEmployee(empno);
		showDeleted(employee);
	}
	
	public void showDeleted(@Param(name="redirect")Employee employee){
	}
	
	@Render("Employee")
	@ResultName("employee")
	public Employee showUpdate(int empno){
		return empdao.getEmployee(empno);
	}
	public void doUpdate(Employee employee){
		empdao.updateEmployee(employee);
		showIndex();
	}
}

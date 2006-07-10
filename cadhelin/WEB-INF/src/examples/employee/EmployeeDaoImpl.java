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
import java.util.Date;
import java.util.HashMap;


public class EmployeeDaoImpl implements EmployeeDao {
	private DepartmentDao departmentDao;
	HashMap <Integer,Employee> employees 
		= new HashMap<Integer,Employee>();
	public EmployeeDaoImpl(DepartmentDao departmentDao) {
		this.departmentDao = departmentDao;
		addEmployee(new Employee(0,"test",new Date(),1));
	}
	public Collection<Employee> findAll() {
		return employees.values();
	}

	public Employee getEmployee(int empno) {
		return employees.get(empno);
	}

	public void addEmployee(Employee employee) {
		employee.setEmpno(employees.size()+1);
		employees.put(employee.getEmpno(),employee);
	}

	public void updateEmployee(Employee employee) {
		employees.put(employee.getEmpno(),employee);
	}

	public void deleteEmployee(int empno) {
		employees.remove(empno);
	}

}

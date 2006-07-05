package examples;

import java.util.Collection;
import java.util.HashMap;

public class DepartmentDaoImpl implements DepartmentDao {
	private HashMap<Integer,Department> map = new HashMap<Integer,Department>();
	public DepartmentDaoImpl(){
		map.put(1,new Department(1,"SALSE","TOKYO"));
		map.put(2,new Department(2,"DEVELOP","OSAKA"));
		map.put(3,new Department(3,"RESEARCH","TUKUBA"));
		map.put(4,new Department(4,"TEST","NAGOYA"));
	}
	public Collection<Department> getAllDepartment(){
		return map.values();
	}

	public Department getDepartment(int deptno){
		return map.get(deptno);
	}
}
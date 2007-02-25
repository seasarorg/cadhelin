package examples.employee;

import java.util.Collection;

import org.examples.dao.Department;

public interface DepartmentDao {

	/* (non-Javadoc)
	 * @see examples.DepartmentDap#getAllDepartment()
	 */
	public abstract Collection<Department> getAllDepartment();

	/* (non-Javadoc)
	 * @see examples.DepartmentDap#getDepartment(int)
	 */
	public abstract Department getDepartment(int deptno);

}
package org.examples.controller.impl;

import org.examples.controller.AddController;

public class AddControllerImpl implements AddController {
	/* (non-Javadoc)
	 * @see org.examples.controller.impl.AddController#showAdd(int, int)
	 */
	public int showAdd(int lhs,int rhs){
		return lhs + rhs;
	}
	public int showSub(int lhs,int rhs){
		return lhs - rhs;
	}
}

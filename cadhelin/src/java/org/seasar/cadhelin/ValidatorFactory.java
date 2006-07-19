package org.seasar.cadhelin;

import java.util.List;

public interface ValidatorFactory {

	public abstract List<ValidatorMetadata> getValidatorsFor(Class clazz);

}
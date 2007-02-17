package org.seasar.cadhelin.creator;

import org.seasar.framework.container.ComponentCustomizer;
import org.seasar.framework.container.creator.ComponentCreatorImpl;
import org.seasar.framework.container.deployer.InstanceDefFactory;
import org.seasar.framework.convention.NamingConvention;

public class ControllerCreator extends ComponentCreatorImpl{
    public ControllerCreator(NamingConvention namingConvention)
    {
        super(namingConvention);
        setNameSuffix("Controller");
        setInstanceDef(InstanceDefFactory.SINGLETON);
        setEnableInterface(true);
        setEnableAbstract(true);
    }

    public ComponentCustomizer getControllerCustomizer()
    {
        return getCustomizer();
    }

    public void setControllerCustomizer(ComponentCustomizer customizer)
    {
        setCustomizer(customizer);
    }
}

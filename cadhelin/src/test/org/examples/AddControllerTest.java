package org.examples;

import org.examples.controller.AddController;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.container.hotdeploy.HotdeployUtil;

public class AddControllerTest {
    public static void main(String[] args) throws Exception {
        SingletonS2ContainerFactory.init();
        S2Container container = SingletonS2ContainerFactory.getContainer();

        Class.forName(AddController.class.getName());
        for (int i = 0; i < 3; ++i) {
            HotdeployUtil.start();
            AddController add = (AddController) container
                    .getComponent("addController");
//            add.print();
            System.out.println(add.showAdd(1, 3));
            HotdeployUtil.stop();
        }
    }
}
package com.tny.game.basics.item.xml;

import com.thoughtworks.xstream.XStream;
import com.tny.game.basics.item.*;

public class TestItemModelManager extends AbstractXMLModelManager<TestItemModel> {

    private ItemModelContext context;

    protected TestItemModelManager(String PATH, ItemModelContext context) {
        super(TestItemModelImpl.class, TestBehavior.class, TestDemandType.class, TestAction.class, TestAbility.class,
                TestOption.class, PATH);
        this.context = context;
    }

    protected void initThis() throws Exception {
        this.initManager();
    }

    @Override
    protected ItemModelContext context() {
        return context;
    }

    @Override
    protected void initXStream(XStream xStream) {
    }

}

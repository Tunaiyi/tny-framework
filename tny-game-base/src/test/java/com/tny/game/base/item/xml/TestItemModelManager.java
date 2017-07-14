package com.tny.game.base.item.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.tny.game.base.item.probability.SequenceRandomCreatorFactory;

public class TestItemModelManager extends AbstractXMLModelManager<TestItemModel> {

    protected TestItemModelManager(String PATH) {
        super(TestItemModelImpl.class, TestBehavior.class, TestDemandType.class, TestAction.class, TestAbility.class,
                TestOption.class, PATH);
    }

    protected void initThis() throws Exception {
        this.initManager();
    }

    @Override
    protected void initXStream(XStream xStream) {
    }

    @Override
    protected SingleValueConverter getFormulaConverter() {
        return new String2Formula();
    }

    @Override
    protected SingleValueConverter getRandomConverter() {
        return new String2RandomCreator(null) {

            @Override
            public Object fromString(String name) {
                return new SequenceRandomCreatorFactory().getRandomCreator();
            }

        };
    }

}

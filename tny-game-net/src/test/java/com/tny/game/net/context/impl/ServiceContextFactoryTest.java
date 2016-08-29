package com.tny.game.net.context.impl;

import com.tny.game.net.config.properties.PropertiesServerConfigFactory;
import com.tny.game.net.config.ServerConfigFactory;
import org.junit.*;


public class ServiceContextFactoryTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetInstance() {
        ServerConfigFactory serviceContextFactory = new PropertiesServerConfigFactory("service.properties");
        Assert.assertEquals(serviceContextFactory.getServerContext(), serviceContextFactory.getServerContext());
    }

}

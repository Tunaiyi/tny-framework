package com.tny.game.net.context.impl;

import com.tny.game.net.config.ServerConfig;
import com.tny.game.net.config.SystemIp;
import com.tny.game.net.config.properties.PropertiesServerConfigFactory;
import org.junit.*;

import java.net.InetSocketAddress;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class ServerContextTest {
    private final ServerConfig context = new PropertiesServerConfigFactory("service.properties").getServerContext();
    private final Map<String, SystemIp> systemIpMap = new HashMap<String, SystemIp>();
    private final List<InetSocketAddress> bindIpList = new ArrayList<InetSocketAddress>();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        systemIpMap.clear();
        systemIpMap.put("loginSystem", new SystemIp(Arrays.asList(new String[]{"127.0.0.1"}),
                "5dcd73d391c90e8769618d42a916ea3b"));
        systemIpMap.put("giftSystem", new SystemIp(Arrays.asList(new String[]{"127.0.0.2"}),
                "5dcd73d391c90e8769618d42a916ea3c"));
        systemIpMap.put("dataSystem", new SystemIp(Arrays.asList(new String[]{"127.0.0.3"}),
                "5dcd73d391c90e8769618d42a916ea3d"));
        bindIpList.clear();
        bindIpList.add(new InetSocketAddress("127.0.0.1", 3001));
        bindIpList.add(new InetSocketAddress("127.0.0.1", 3002));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetProperty() {
        assertEquals("TW", context.getConfig().getStr("langauge"));
        assertEquals("China", context.getConfig().getStr("loaction"));
    }

}

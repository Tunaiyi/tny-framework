package com.tny.game.net.netty4.cloud.nacos;

import com.alibaba.cloud.nacos.*;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import org.slf4j.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/10 1:31 下午
 */
public class NacosServeNodeClient extends BaseServeNodeClient {

	public static final Logger LOGGER = LoggerFactory.getLogger(NacosServeNodeClient.class);

	private final NacosDiscoveryProperties properties;

	private final NacosServiceManager nacosServiceManager;

	private final EventListener listener = this::handleEvent;

	public NacosServeNodeClient(NacosDiscoveryProperties properties, NacosServiceManager nacosServiceManager) {
		this.properties = properties;
		this.nacosServiceManager = nacosServiceManager;
	}

	@Override
	protected void doSubscribe(String serveName) {
		NamingService namingService = nacosServiceManager.getNamingService(properties.getNacosProperties());
		try {
			namingService.subscribe(serveName, properties.getGroup(), listener);
		} catch (NacosException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doUnsubscribe(String serveName) {
		NamingService namingService = nacosServiceManager.getNamingService(properties.getNacosProperties());
		try {
			namingService.unsubscribe(serveName, properties.getGroup(), listener);
		} catch (NacosException e) {
			e.printStackTrace();
		}
	}

}

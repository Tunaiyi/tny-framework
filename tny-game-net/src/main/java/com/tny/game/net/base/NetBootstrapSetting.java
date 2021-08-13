package com.tny.game.net.base;

public interface NetBootstrapSetting {

	String getName();

	String getTunnelIdGenerator();

	String getMessageFactory();

	String getCertificateFactory();

	String getMessageDispatcher();

	String getCommandTaskProcessor();

}

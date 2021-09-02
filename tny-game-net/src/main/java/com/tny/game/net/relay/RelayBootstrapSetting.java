package com.tny.game.net.relay;

public interface RelayBootstrapSetting {

	String getName();

	String getTunnelIdGenerator();

	String getMessageFactory();

	String getCertificateFactory();

}

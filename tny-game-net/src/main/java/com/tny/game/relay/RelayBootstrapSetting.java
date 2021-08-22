package com.tny.game.relay;

public interface RelayBootstrapSetting {

	String getName();

	String getTunnelIdGenerator();

	String getMessageFactory();

	String getCertificateFactory();

}

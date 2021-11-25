package com.tny.game.net.command;

import com.tny.game.common.enums.*;

public enum CertificateStatus implements Enumerable<Integer> {

	/**
	 * 无效的
	 */
	INVALID(0, false),

	/**
	 * 未认证
	 */
	UNAUTHENTICATED(1, false),

	/**
	 * 已认证
	 */
	AUTHENTICATED(2, true),

	/**
	 * 续约认证
	 */
	RENEW(3, true),

	//
	;

	private final Integer id;

	private final boolean authenticated;

	CertificateStatus(Integer id, boolean authenticated) {
		this.id = id;
		this.authenticated = authenticated;
	}

	@Override
	public Integer getId() {
		return this.id;
	}

	public boolean isAuthenticated() {
		return this.authenticated;
	}
}
package com.tny.game.net.base;

import com.tny.game.common.context.*;
import com.tny.game.common.lifecycle.unit.annotation.*;

import java.util.List;

@UnitInterface
public interface NetAppContext {

	String getName();

	String getAppType();

	String getLocale();

	String getScopeType();

	long getServerId();

	List<String> getScanPackages();

	Attributes attributes();

}

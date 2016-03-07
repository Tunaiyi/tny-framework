package com.tny.game.net;

import com.tny.game.common.config.Config;
import com.tny.game.common.config.ConfigLib;

/**
 * Created by Kun Yang on 16/3/7.
 */
public interface DevUtils {

    Config DEV_CONFIG = ConfigLib.getConfig("develop.properties");

}

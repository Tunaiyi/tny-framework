package com.tny.game.suite.login.spring;

import com.tny.game.net.command.auth.AuthProvider;
import com.tny.game.suite.login.ServerAuthProvider;
import com.tny.game.suite.login.UserLoginAuthProvider;
import com.tny.game.suite.login.UserReloginAuthProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static com.tny.game.suite.SuiteProfiles.*;

/**
 * Game Suite 的默认配置
 * Created by Kun Yang on 16/1/27.
 */
@Configuration
public class AuthConfiguration {

    @Bean
    @Profile({GAME})
    public AuthProvider userLoginAuthProvider() {
        return new UserLoginAuthProvider();
    }

    @Bean
    @Profile({GAME})
    public AuthProvider userReloginAuthProvider() {
        return new UserReloginAuthProvider();
    }

    @Bean
    @Profile({SERVER, GAME, SERVER_AUTH})
    public AuthProvider serveAuthProvider() {
        return new ServerAuthProvider();
    }


}

package com.tny.game.suite.net.spring;

import com.tny.game.net.command.auth.AuthenticateProvider;
import com.tny.game.suite.login.ServerAuthenticateProvider;
import com.tny.game.suite.login.UserLoginAuthenticateProvider;
import com.tny.game.suite.login.UserReloginAuthenticateProvider;
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
    public AuthenticateProvider userLoginAuthProvider() {
        return new UserLoginAuthenticateProvider();
    }

    @Bean
    @Profile({GAME})
    public AuthenticateProvider userReloginAuthProvider() {
        return new UserReloginAuthenticateProvider();
    }

    @Bean
    @Profile({SERVER, GAME, SERVER_AUTH})
    public AuthenticateProvider serveAuthProvider() {
        return new ServerAuthenticateProvider();
    }

}

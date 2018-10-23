package com.tny.game.suite.net.spring;

import com.tny.game.net.command.auth.AuthenticateValidator;
import com.tny.game.suite.login.ServerAuthenticateValidator;
import com.tny.game.suite.login.UserLoginAuthenticateValidator;
import com.tny.game.suite.login.UserReloginAuthenticateValidator;
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
    public AuthenticateValidator userLoginAuthProvider() {
        return new UserLoginAuthenticateValidator();
    }

    @Bean
    @Profile({GAME})
    public AuthenticateValidator userReloginAuthProvider() {
        return new UserReloginAuthenticateValidator();
    }

    @Bean
    @Profile({SERVER, GAME, SERVER_AUTH})
    public AuthenticateValidator serveAuthProvider() {
        return new ServerAuthenticateValidator();
    }

}

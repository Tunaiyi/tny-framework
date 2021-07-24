package com.tny.game.common.boot.configuration;

import com.tny.game.common.boot.initiator.*;
import org.springframework.context.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/15 8:55 下午
 */
@Configuration
public class TnyFrameworkAutoConfiguration {

    @Bean
    public UnitLoadInitiator unitLoadInitiator() {
        return new UnitLoadInitiator();
    }

}

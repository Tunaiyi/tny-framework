package com.tny.game.data.configuration.redisson;

import com.tny.game.data.configuration.*;
import com.tny.game.data.redisson.*;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:29 下午
 */
@Configuration
@ConditionalOnClass(RedissonStorageAccessorFactory.class)
@AutoConfigureBefore(GameDataAutoConfiguration.class)
@Import({
		ImportRedissonStorageAccessorFactoryDefinitionRegistrar.class
})
public class RedissonStorageAutoConfiguration {

}

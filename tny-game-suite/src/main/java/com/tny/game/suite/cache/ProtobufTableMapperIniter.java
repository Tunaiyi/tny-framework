package com.tny.game.suite.cache;

import com.tny.game.cache.annotation.ToCache;
import com.tny.game.common.collection.CollectorsAide;
import com.tny.game.common.lifecycle.ServerPostStart;
import static com.tny.game.common.utils.StringAide.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.filter.AnnotationClassFilter;
import com.tny.game.suite.utils.Configs;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static com.tny.game.suite.SuiteProfiles.PROTOBUF_MAPPER;

/**
 * Created by Kun Yang on 2017/1/18.
 */
@Component
@Profile(PROTOBUF_MAPPER)
public class ProtobufTableMapperIniter implements ServerPostStart, ApplicationContextAware {

    private ClassSelector selector = ClassSelector.instance(AnnotationClassFilter.ofInclude(ToCache.class));

    private ApplicationContext context;

    private ForkJoinTask<?> task;

    public ProtobufTableMapperIniter() {
        task = ForkJoinPool.commonPool()
                .submit(() -> ClassScanner.instance()
                        .addSelector(selector)
                        .scan(Configs.getScanPathArray()));
    }

    @Override
    public void postStart() throws Exception {
        task.join();
        Collection<String> profiles = Configs.getProfiles();
        Map<Class<?>, ProtoCacheFormatter> formatterMap =
                context.getBeansOfType(ProtoCacheFormatter.class)
                        .values()
                        .stream()
                        .collect(CollectorsAide.toMap(ProtoCacheFormatter::getClass));
        for (Class<?> clazz : selector.getClasses()) {
            ToCache cache = clazz.getAnnotation(ToCache.class);
            String[] cacheProfiles = cache.profiles();
            if (cacheProfiles.length > 0 && Stream.of(cacheProfiles).noneMatch(profiles::contains))
                continue;
            String table = cache.prefix();
            if (StringUtils.isBlank(table))
                throw new IllegalArgumentException(
                        format("{} 没有 prefix 参数", clazz));
            if (cache.triggers().length == 0)
                continue;
            Class<?> triggerClass = cache.triggers()[0];
            if (!ProtoCacheFormatter.class.isAssignableFrom(triggerClass))
                continue;
            ProtoCacheFormatter<?, ?> formatter = formatterMap.get(triggerClass);
            if (formatter == null)
                throw new IllegalArgumentException(
                        format("{} 找不到 {} formatter", clazz, triggerClass));
            ProtobufTableMapper.loadOrCreate(table, formatter);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

}

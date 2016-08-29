package com.tny.game.suite.net.spring;

import com.google.common.collect.Range;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.kafka.KafkaServeAuthProvider;
import com.tny.game.suite.login.GameAuthProvider;

import java.util.Set;

/**
 * Created by Kun Yang on 16/8/11.
 */
public class SpringKafkaServeAuthProvider extends GameAuthProvider implements KafkaServeAuthProvider {

    public SpringKafkaServeAuthProvider(Set<Integer> includes) {
        this(includes, null);
    }

    public SpringKafkaServeAuthProvider(Range<Integer> includeRange) {
        this(includeRange, null);
    }

    public SpringKafkaServeAuthProvider(Range<Integer> includeRange, Range<Integer> excludeRange) {
        this(null, null, includeRange, excludeRange);
    }

    public SpringKafkaServeAuthProvider(Set<Integer> includes, Set<Integer> excludes) {
        this(includes, excludes, null, null);
    }

    public SpringKafkaServeAuthProvider(Set<Integer> includes, Set<Integer> excludes, Range<Integer> includeRange, Range<Integer> excludeRange) {
        super("kafka-serve-auth-provider", includes, excludes, includeRange, excludeRange);
    }

    @Override
    public boolean isCanValidate(Request request) {
        return KafkaServeAuthProvider.super.isCanValidate(request) && super.isCanValidate(request);
    }
}

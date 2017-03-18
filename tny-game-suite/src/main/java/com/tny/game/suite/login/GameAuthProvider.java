package com.tny.game.suite.login;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.tny.game.net.auth.AuthProvider;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.suite.utils.Configs;

import java.util.Set;

public abstract class GameAuthProvider implements AuthProvider {

    private Set<Integer> includes = ImmutableSet.of();
    private Set<Integer> excludes = ImmutableSet.of();
    private Range<Integer> includeRange;
    private Range<Integer> excludeRange;

    private String name;

    public GameAuthProvider(String name, Set<Integer> includes) {
        this(name, includes, null, null, null);
    }

    public GameAuthProvider(String name, Range<Integer> includeRange) {
        this(name, null, null, includeRange, null);
    }

    public GameAuthProvider(String name, Range<Integer> includeRange, Range<Integer> excludeRange) {
        this(name, null, null, includeRange, excludeRange);
    }

    public GameAuthProvider(String name, Set<Integer> includes, Set<Integer> excludes) {
        this(name, includes, excludes, null, null);
    }

    public GameAuthProvider(String name, Set<Integer> includes, Set<Integer> excludes, Range<Integer> includeRange, Range<Integer> excludeRange) {
        this.name = name;
        if (includes != null)
            this.includes = ImmutableSet.copyOf(includes);
        if (excludes != null)
            this.excludes = ImmutableSet.copyOf(excludes);
        this.includeRange = includeRange;
        this.excludeRange = excludeRange;
    }


    @Override
    public boolean isCanValidate(Request request) {
        if (includes.contains(request.getProtocol()) || (includeRange != null && includeRange.contains(request.getProtocol()))) {
            if (excludes.contains(request.getProtocol()) || (excludeRange != null && excludeRange.contains(request.getProtocol())))
                return false;
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    protected boolean isAuth() {
        return Configs.DEVELOP_CONFIG.getBoolean(Configs.DEVELOP_AUTH_CHECK, true);
    }
}

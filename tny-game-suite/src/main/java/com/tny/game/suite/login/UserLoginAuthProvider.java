package com.tny.game.suite.login;

import com.google.common.collect.Range;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.dispatcher.exception.DispatchException;

import java.util.Set;

public class UserLoginAuthProvider extends UserAuthProvider {

    public UserLoginAuthProvider(Set<Integer> includes) {
        this(includes, null);
    }

    public UserLoginAuthProvider(Range<Integer> includeRange) {
        this(includeRange, null);
    }

    public UserLoginAuthProvider(Range<Integer> includeRange, Range<Integer> excludeRange) {
        this(null, null, includeRange, excludeRange);
    }

    public UserLoginAuthProvider(Set<Integer> includes, Set<Integer> excludes) {
        this(includes, excludes, null, null);
    }

    public UserLoginAuthProvider(Set<Integer> includes, Set<Integer> excludes, Range<Integer> includeRange, Range<Integer> excludeRange) {
        super("user-login-auth-provider", includes, excludes, includeRange, excludeRange);
    }

    @Override
    public LoginCertificate validate(Request request) throws DispatchException {
        return checkUserLogin(request, false);
    }

}

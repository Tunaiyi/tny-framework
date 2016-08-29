package com.tny.game.suite.login;

import com.google.common.collect.Range;
import com.tny.game.net.LoginCertificate;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.dispatcher.exception.DispatchException;

import java.util.Set;

public class UserReloginAuthProvider extends UserAuthProvider {

    public UserReloginAuthProvider(Set<Integer> includes) {
        this(includes, null);
    }

    public UserReloginAuthProvider(Range<Integer> includeRange) {
        this(includeRange, null);
    }

    public UserReloginAuthProvider(Range<Integer> includeRange, Range<Integer> excludeRange) {
        this(null, null, includeRange, excludeRange);
    }

    public UserReloginAuthProvider(Set<Integer> includes, Set<Integer> excludes) {
        this(includes, excludes, null, null);
    }

    public UserReloginAuthProvider(Set<Integer> includes, Set<Integer> excludes, Range<Integer> includeRange, Range<Integer> excludeRange) {
        super("user-relogin-auth-provider", includes, excludes, includeRange, excludeRange);
    }

    // public UserReloginAuthProvider(Set<Integer> authProtocols) {
    //     this("user-relogin-auth-provider", authProtocols);
    // }

    @Override
    public LoginCertificate validate(Request request) throws DispatchException {
        return checkUserLogin(request, true);
    }

}

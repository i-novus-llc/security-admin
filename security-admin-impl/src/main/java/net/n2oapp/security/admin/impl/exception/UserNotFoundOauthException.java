package net.n2oapp.security.admin.impl.exception;

import org.springframework.security.core.AuthenticationException;

public class UserNotFoundOauthException extends AuthenticationException {
    public UserNotFoundOauthException(String msg, Throwable t) {
        super(msg, t);
    }

    public UserNotFoundOauthException(String msg) {
        super(msg);
    }
}

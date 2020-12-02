package com.stubbz.springboot.dreamlog.security;

import com.stubbz.springboot.dreamlog.domain.User;

public interface ISecurityUserService {

    String validatePasswordResetToken(long id, String token);

    User getUserByToken(String token);

}
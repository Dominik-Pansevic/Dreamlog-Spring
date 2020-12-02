package com.stubbz.springboot.dreamlog.security;

import com.stubbz.springboot.dreamlog.domain.PasswordResetToken;
import com.stubbz.springboot.dreamlog.domain.PasswordResetTokenRepository;
import com.stubbz.springboot.dreamlog.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Calendar;


@Service
@Transactional
public class UserSecurityService implements ISecurityUserService {


    @Autowired
    private PasswordResetTokenRepository passwordTokenRepository;

    public String validatePasswordResetToken(long id, String token) {
        PasswordResetToken passToken =
                passwordTokenRepository.findByToken(token);
        if ((passToken == null) || (passToken.getUser()
                .getId() != id)) {
            return "invalidToken";
        }

        Calendar cal = Calendar.getInstance();
        if ((passToken.getExpiryDate()
                .getTime() - cal.getTime()
                .getTime()) <= 0) {
            return "expired";
        }

        User user = passToken.getUser();
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user, null, Arrays.asList(
                new SimpleGrantedAuthority("CHANGE_PASSWORD_PRIVILEGE")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        return null;
    }


    public User getUserByToken(String token)
    {
        PasswordResetToken passToken = passwordTokenRepository.findByToken(token);
        User user = passToken.getUser();

        return user;
    }




}

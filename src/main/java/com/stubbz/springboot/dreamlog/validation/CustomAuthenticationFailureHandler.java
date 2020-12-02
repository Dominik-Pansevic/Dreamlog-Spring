package com.stubbz.springboot.dreamlog.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private MessageSource messages;


    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        setDefaultFailureUrl("/login.html?error=true");

        super.onAuthenticationFailure(request, response, exception);


//        String errorMessage = messages.getMessage("message.badCredentials", null, locale);
        String errorMessage = "User not found";

        if (exception.getMessage().equalsIgnoreCase("User is disabled")) {
//            errorMessage = messages.getMessage("auth.message.disabled", null, locale);
              errorMessage = "User is disabled";
        } else if (exception.getMessage().equalsIgnoreCase("User account has expired")) {
//            errorMessage = messages.getMessage("auth.message.expired", null, locale);
              errorMessage = "User account has expired";
        }

        request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, errorMessage);
    }
}
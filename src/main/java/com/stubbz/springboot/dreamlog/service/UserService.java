package com.stubbz.springboot.dreamlog.service;

import com.stubbz.springboot.dreamlog.domain.User;
import com.stubbz.springboot.dreamlog.domain.VerificationToken;

public interface UserService {

    void saveUser(User user);

    void createVerificationToken(User user, String token);

    VerificationToken getVerificationToken(String VerificationToken);

    boolean isUserVerified(User user);

    Long removeByEmail(String email);

    void deleteVerificationToken(Long id);

    void createPasswordResetTokenForUser(User user, String token);

    User findVerifiedUserByEmail(String email);

    User encodePassword(User user);

    void changeUserPassword(User user, String password);

    void deletePasswordResetTokenByUser(User user);

    boolean isEmailTaken(User user);

    boolean isUsernameTaken(User user);

    User trimInput(User user);

}

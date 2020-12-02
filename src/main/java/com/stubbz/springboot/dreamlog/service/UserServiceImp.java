package com.stubbz.springboot.dreamlog.service;

import com.stubbz.springboot.dreamlog.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;


@Service
@Transactional
public class UserServiceImp implements UserService {

    @Autowired
    BCryptPasswordEncoder encoder;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRepository userRepository;


    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordTokenRepository;

    @Override
    public void saveUser(User user) {
        user.setStatus("VERIFIED");
        Role userRole = roleRepository.findByRole("USER");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        userRepository.save(user);

    }

    @Override
    public boolean isEmailTaken(User user) {

        if(userRepository.findByEmailAndEnabled(user.getEmail(), true) != null  )
        {
            return true;
        }
        return false;
    }

    @Override
    public boolean isUsernameTaken(User user) {

        if(userRepository.findByUsernameAndEnabled(user.getUsername(), true) != null  )
        {
            return true;
        }
        return false;
    }

    public boolean isUserVerified(User user) {

            if(user.isEnabled())
            {
                return true;
            }
            return false;
    }

    @Override
    public Long removeByEmail(String email) {
       return userRepository.removeByEmail(email);
    }

    public String getUsernameByEmail(String email)
    {
        return userRepository.findUserByEmail(email).getUsername();
    }

    @Override
    public VerificationToken getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    @Override
    public void createVerificationToken(User user, String token) {
        user.setPassword(encoder.encode(user.getPassword()));
        VerificationToken myToken = new VerificationToken(token, user);

        tokenRepository.save(myToken);
    }

    @Override
    public void deleteVerificationToken(Long id)
    {
        tokenRepository.removeById(id);
    }

    @Override
    public void deletePasswordResetTokenByUser(User user)
    {
        passwordTokenRepository.removeByUser(user);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);
    }

    @Override
    public User findVerifiedUserByEmail(String email) {
        if(userRepository.findUserByEmail(email) != null)
        {
            if( userRepository.findUserByEmail(email).isEnabled() )
            {
                return userRepository.findUserByEmail(email);
            }
            else
            {
                return null;
            }
        }
        return null;
    }

    public User encodePassword(User user)
    {
        user.setPassword(encoder.encode(user.getPassword()));

        return user;
    }

    public void changeUserPassword(User user, String password) {
        user.setPassword(encoder.encode(password));
        userRepository.save(user);
    }

    public User trimInput(User user)
    {
        user.setName(user.getUsername().trim());
        user.setLastName(user.getLastName().trim());
        user.setEmail(user.getEmail().trim());
        user.setUsername(user.getUsername().trim());

        return user;
    }

}

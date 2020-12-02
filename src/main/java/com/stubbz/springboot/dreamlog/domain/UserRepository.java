package com.stubbz.springboot.dreamlog.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Integer>
{
    User findUserByEmail (String email);
    User findByEmailAndEnabled(String email, boolean enabled);
    User findByUsernameAndEnabled(String username, boolean enabled);
    Long removeByEmail(String email);


}


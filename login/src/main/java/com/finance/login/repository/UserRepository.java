package com.finance.login.repository;

import com.finance.login.entity.UserDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<UserDetailsEntity, String> {
  UserDetailsEntity  findByEmailIdAndPassword(String emailId, String password);

  UserDetailsEntity findByEmailId(String emailId);

  boolean existsByEmailId(String emailId);

}


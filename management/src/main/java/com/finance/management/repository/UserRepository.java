package com.finance.management.repository;

import com.finance.management.entity.UserDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<UserDetailsEntity, String> {
  UserDetailsEntity  findByEmailIdAndPassword(String emailId, String password);

  UserDetailsEntity findByEmailId(String emailId);

  boolean existsByEmailId(String emailId);

}


package com.matrimony.site.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matrimony.site.entity.Users;

public interface UserRepository extends JpaRepository<Users, Integer> {

	Optional<Users> findByUserName(String username);

}

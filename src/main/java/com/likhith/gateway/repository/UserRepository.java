package com.likhith.gateway.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.likhith.gateway.document.User;

public interface UserRepository extends MongoRepository<User, String> {

	Optional<User> findByUsername(String username);

}
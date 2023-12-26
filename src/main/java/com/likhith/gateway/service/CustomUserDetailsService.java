package com.likhith.gateway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.likhith.gateway.document.User;
import com.likhith.gateway.dto.UserResponse;
import com.likhith.gateway.exception.ValidationException;
import com.likhith.gateway.mapper.GatewayMapper;
import com.likhith.gateway.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	GatewayMapper gatewayMapper;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

		return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
				.password(user.getPassword()).roles(user.getRoles()).build();
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<UserResponse> getAllUsers() {

		List<UserResponse> userResponseList = new ArrayList<>();
		List<User> users = userRepository.findAll();

		if (!CollectionUtils.isEmpty(users)) {
			userResponseList = users.stream().map(user -> {
				UserResponse userResponse = gatewayMapper.mapToUserResponse(user);
				return userResponse;
			}).collect(Collectors.toList());
		}

		return userResponseList;
	}

	@PreAuthorize("hasAnyRole('ADMIN','USER','CONSUMER')")
	public UserResponse getUser(String username) {

		UserResponse userResponse = null;
		Optional<User> user = userRepository.findByUsername(username);

		if (user.isPresent()) {
			userResponse = gatewayMapper.mapToUserResponse(user.get());
		}

		return userResponse;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public UserResponse getOtherUser(String username) {

		UserResponse userResponse = null;
		Optional<User> user = userRepository.findByUsername(username);

		if (user.isPresent()) {
			userResponse = gatewayMapper.mapToUserResponse(user.get());
		}

		return userResponse;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public UserResponse createUser(User user) {

		String message = null;
		Optional<User> userFromDB = userRepository.findByUsername(user.getUsername());

		if (userFromDB.isPresent()) {
			throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "User already available");
		} else {
			String encodedPassword = passwordEncoder.encode(user.getPassword());
			user.setPassword(encodedPassword);

			userRepository.save(user);
			message = "User created successfully";
		}

		return new UserResponse(message);
	}

	@PreAuthorize("hasAnyRole('ADMIN','USER','CONSUMER')")
	public UserResponse updateUser(User user) {

		String message = null;
		Optional<User> userFromDB = userRepository.findByUsername(user.getUsername());

		if (userFromDB.isEmpty()) {
			throw new ValidationException(HttpStatus.NOT_FOUND.value(), "No user found that can be updated");
		} else {
			boolean anyUpdate = false;

			if (!ObjectUtils.isEmpty(user.getPassword())) {
				String encodedPassword = passwordEncoder.encode(user.getPassword());
				userFromDB.get().setPassword(encodedPassword);
				anyUpdate = true;
			}
			if (!ObjectUtils.isEmpty(user.getRoles())) {
				userFromDB.get().setRoles(user.getRoles());
				anyUpdate = true;
			}

			if (anyUpdate) {
				userRepository.save(userFromDB.get());
				message = "User updated successfully";
			} else {
				throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "Nothing to update");
			}
		}

		return new UserResponse(message);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public UserResponse updateOtherUser(User user) {

		String message = null;
		Optional<User> userFromDB = userRepository.findByUsername(user.getUsername());

		if (userFromDB.isEmpty()) {
			throw new ValidationException(HttpStatus.NOT_FOUND.value(), "No user found that can be updated");
		} else {
			boolean anyUpdate = false;

			if (!ObjectUtils.isEmpty(user.getPassword())) {
				throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "Only SELF USER can update the password");
			}
			if (!ObjectUtils.isEmpty(user.getRoles())) {
				userFromDB.get().setRoles(user.getRoles());
				anyUpdate = true;
			}

			if (anyUpdate) {
				userRepository.save(userFromDB.get());
				message = "User updated successfully";
			} else {
				throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "Nothing to update");
			}
		}

		return new UserResponse(message);
	}

	@PreAuthorize("hasAnyRole('ADMIN','USER','CONSUMER')")
	public UserResponse deleteUser(String username) {

		String message = null;
		Optional<User> userFromDB = userRepository.findByUsername(username);

		if (userFromDB.isEmpty()) {
			throw new ValidationException(HttpStatus.NOT_FOUND.value(), "No user found that can be deleted");
		} else {
			userRepository.delete(userFromDB.get());

			message = "User deleted successfully";
		}

		return new UserResponse(message);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public UserResponse deleteOtherUser(String username) {

		String message = null;
		Optional<User> userFromDB = userRepository.findByUsername(username);

		if (userFromDB.isEmpty()) {
			throw new ValidationException(HttpStatus.NOT_FOUND.value(), "No user found that can be deleted");
		} else {
			userRepository.delete(userFromDB.get());

			message = "User deleted successfully";
		}

		return new UserResponse(message);
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

}

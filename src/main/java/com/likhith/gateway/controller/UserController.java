package com.likhith.gateway.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likhith.gateway.document.User;
import com.likhith.gateway.dto.UserResponse;
import com.likhith.gateway.exception.ValidationException;
import com.likhith.gateway.service.CustomUserDetailsService;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	CustomUserDetailsService service;

	@GetMapping("/getAllUsers")
	public ResponseEntity<List<UserResponse>> getAllUsers() {

		List<UserResponse> responseList = service.getAllUsers();

		if (CollectionUtils.isEmpty(responseList)) {
			throw new ValidationException(HttpStatus.NOT_FOUND.value(), "No Users found");
		}

		return ResponseEntity.ok().body(responseList);

	}

	@GetMapping("/getUser")
	public ResponseEntity<UserResponse> getUser() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = ((UserDetails) authentication.getPrincipal()).getUsername();

		UserResponse response = service.getUser(username);

		if (ObjectUtils.isEmpty(response)) {
			throw new ValidationException(HttpStatus.NOT_FOUND.value(), "No User found");
		}

		return ResponseEntity.ok().body(response);

	}

	@GetMapping("/getOtherUser/{username}")
	public ResponseEntity<UserResponse> getOtherUser(@PathVariable("username") String username) {

		UserResponse response = service.getOtherUser(username);

		if (ObjectUtils.isEmpty(response)) {
			throw new ValidationException(HttpStatus.NOT_FOUND.value(), "No User found");
		}

		return ResponseEntity.ok().body(response);

	}

	@PostMapping("/createUser")
	public ResponseEntity<UserResponse> createUser(@RequestBody User user) {
		return ResponseEntity.ok().body(service.createUser(user));
	}

	@PutMapping("/updateUser")
	public ResponseEntity<UserResponse> updateUser(@RequestBody User user) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = ((UserDetails) authentication.getPrincipal()).getUsername();

		if (username.equalsIgnoreCase(user.getUsername())) {
			return ResponseEntity.ok().body(service.updateUser(user));
		} else {
			throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "Cannot update other User");
		}
	}

	@PutMapping("/updateOtherUser")
	public ResponseEntity<UserResponse> updateOtherUser(@RequestBody User user) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = ((UserDetails) authentication.getPrincipal()).getUsername();

		if (!username.equalsIgnoreCase(user.getUsername())) {
			return ResponseEntity.ok().body(service.updateOtherUser(user));
		} else {
			throw new ValidationException(HttpStatus.BAD_REQUEST.value(), "Cannot update same User");
		}

	}

	@DeleteMapping("/deleteUser")
	public ResponseEntity<UserResponse> deleteUser() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = ((UserDetails) authentication.getPrincipal()).getUsername();

		return ResponseEntity.ok().body(service.deleteUser(username));
	}

	@DeleteMapping("/deleteOtherUser/{username}")
	public ResponseEntity<UserResponse> deleteOtherUser(@PathVariable("username") String username) {
		return ResponseEntity.ok().body(service.deleteOtherUser(username));
	}

}
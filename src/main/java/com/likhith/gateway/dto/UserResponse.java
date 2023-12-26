package com.likhith.gateway.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.likhith.gateway.exception.ErrorMessage;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

	private String id;
	private String username;
	private List<String> roles;
	private String message;
	private ErrorMessage error;

	public UserResponse(String message) {
		super();
		this.message = message;
	}

	public UserResponse(ErrorMessage error) {
		super();
		this.error = error;
	}

}
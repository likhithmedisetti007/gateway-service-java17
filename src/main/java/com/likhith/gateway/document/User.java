package com.likhith.gateway.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Document(collection = "user")
@Data
public class User {

	@Id
	private String id;
	@NotNull
	private String username;
	@NotNull
	private String password;
	@NotNull
	private String[] roles;

}
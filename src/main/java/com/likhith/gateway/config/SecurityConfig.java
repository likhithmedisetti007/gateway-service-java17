package com.likhith.gateway.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import com.likhith.gateway.document.User;
import com.likhith.gateway.service.CustomUserDetailsService;

import lombok.Data;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ConfigurationProperties(prefix = "spring.security.user")
@Data
public class SecurityConfig {

	String name;
	String password;
	List<String> roles;

	@Bean
	UserDetailsService mongoUserDetails() {
		return new CustomUserDetailsService();
	}

	@Bean
	AuthenticationEntryPoint authenticationEntryPoint() {
		return new CustomAuthenticationEntryPoint();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers("/category/protected/**")
						.hasRole("ADMIN").requestMatchers("/category/private/**").hasAnyRole("ADMIN", "CONSUMER")
						.requestMatchers("/actuator/**", "/category/public/**").permitAll().requestMatchers("/user/**")
						.hasAnyRole("ADMIN", "USER").anyRequest().authenticated())
				.httpBasic(t -> t.authenticationEntryPoint(authenticationEntryPoint()))
				.userDetailsService(mongoUserDetails());
		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		return encoder;
	}

	@Bean
	CommandLineRunner initialUserSetup(CustomUserDetailsService customUserDetailsService,
			PasswordEncoder passwordEncoder) {
		return args -> {
			// Check if the initial user exists
			if (customUserDetailsService.getUserRepository().findByUsername(name).isEmpty()) {

				// Encode the initial password
				String initialEncodedPassword = passwordEncoder.encode(password);

				// Create the initial admin user
				User initialUser = new User();
				initialUser.setUsername(name);
				initialUser.setPassword(initialEncodedPassword);
				initialUser.setRoles(roles.toArray(new String[0]));

				// Save the initial admin user to the database
				customUserDetailsService.getUserRepository().save(initialUser);
			}
		};
	}

}
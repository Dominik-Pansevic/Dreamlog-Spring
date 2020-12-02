package com.stubbz.springboot.dreamlog.domain;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "auth_user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "auth_user_id")
	private long id;

	@NotBlank(message = "First name is compulsory.")
	@Column(name = "first_name")
	private String name;

	@NotBlank(message = "Last name is compulsory.")
	@Column(name = "last_name")
	private String lastName;

	@NotBlank(message = "Username is compulsory.")
	@Column(name = "username")
	private String username;

	@NotBlank(message = "Email is compulsory.")
	@Email(message = "Email is invalid.")
	@Column(name = "email")
	private String email;

	@NotEmpty(message = "Password is compulsory.")
	@Length(min=5, message = "Password should be at least 5 characters long.")
	@Column(name = "password")
	private String password;

	@Column(name = "status")
	private String status;

	@Column(name = "enabled")
	private boolean enabled;


	public User() {
		super();
		this.enabled=false;
	}

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "auth_user_role", joinColumns = @JoinColumn(name = "auth_user_id"), inverseJoinColumns = @JoinColumn(name = "auth_role_id"))
	private Set<Role> roles;


	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<String> getRoles() {
		List list = new ArrayList<String>(Collections.singleton(roles.toString()));
		return list;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}

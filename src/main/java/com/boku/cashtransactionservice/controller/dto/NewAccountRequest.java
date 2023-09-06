package com.boku.cashtransactionservice.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class NewAccountRequest {

	@NotNull
	@NotEmpty
	private String name;
	@NotNull
	@NotEmpty
	private String surname;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}
}

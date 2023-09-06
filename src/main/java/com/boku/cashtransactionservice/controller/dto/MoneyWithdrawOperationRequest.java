package com.boku.cashtransactionservice.controller.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class MoneyWithdrawOperationRequest {
	@DecimalMin("1.0")
	private Double amount;
	@NotNull @NotEmpty
	private String withdrawalId;
	@NotNull @NotEmpty
	private String address;

	public MoneyWithdrawOperationRequest(Double amount, String withdrawalId, String address) {
		this.amount = amount;
		this.withdrawalId = withdrawalId;
		this.address = address;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getWithdrawalId() {
		return withdrawalId;
	}

	public void setWithdrawalId(String withdrawalId) {
		this.withdrawalId = withdrawalId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}

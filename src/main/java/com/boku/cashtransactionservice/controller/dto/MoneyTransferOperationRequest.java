package com.boku.cashtransactionservice.controller.dto;

import jakarta.validation.constraints.DecimalMin;

public class MoneyTransferOperationRequest {

	@DecimalMin("1.0")
	private Double amount;

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}


}

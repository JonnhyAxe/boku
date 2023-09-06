package com.boku.cashtransactionservice.controller;


import com.boku.cashtransactionservice.controller.dto.*;
import com.boku.cashtransactionservice.model.account.Account;
import com.boku.cashtransactionservice.model.account.AccountKey;
import com.boku.cashtransactionservice.service.AccountService;
import com.boku.cashtransactionservice.service.WithdrawalService;
import com.boku.cashtransactionservice.service.exception.HttpParametersException;
import com.boku.cashtransactionservice.service.exception.UserAccountNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.boku.cashtransactionservice.service.WithdrawalService.WithdrawalId;
import com.boku.cashtransactionservice.service.WithdrawalService.Address;
import java.net.URI;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/account")
public class AccountController {

	private AccountService accountService;
	private WithdrawalService withdrawalService;

	public AccountController(AccountService accountService, WithdrawalService withdrawalService) {
		this.accountService = accountService;
		this.withdrawalService = withdrawalService;
	}

	@PostMapping()
	public ResponseEntity createAccount(@RequestBody @Valid NewAccountRequest newAccountRequest) {
		Account account = new Account(AccountKey.valueOf(AccountKey.getNewId()),
				newAccountRequest.getName(),
				newAccountRequest.getSurname(),
				0.0);
		accountService.createAccount(account);
		URI location = URI.create(String.format("/api/account/%s", account.getAccountKey().getAccountId()));
		return ResponseEntity.created(location).build();
	}

	@GetMapping("/{accountId}")
	public ResponseEntity getBalance(@PathVariable("accountId") long accountId) {
		return ResponseEntity.ok(accountService.getAccountBalance(accountId));
	}

	@PostMapping("/{accountId}/credit")
	public ResponseEntity creditAccount(@PathVariable("accountId") long accountId, @RequestBody MoneyTransferOperationRequest request) {
		accountService.credit(accountId, request.getAmount());
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{accountId}/withdraw")
	public ResponseEntity makeWithdraw(@PathVariable("accountId") long accountId, @RequestBody MoneyWithdrawOperationRequest request) {

		verifyAccount(accountService.containsAccount(accountId), accountId);

		accountService.withdraw(accountId, request.getAmount());
	try {

		withdrawalService.requestWithdrawal(new WithdrawalId(UUID.fromString(request.getWithdrawalId())),
				new Address(request.getAddress()),
				request.getAmount());

	} catch (IllegalArgumentException e) {
		//debit operation in case of failure
		accountService.credit(accountId, request.getAmount());
	}
		return ResponseEntity.noContent().build(); // no content is necessary
	}

	@GetMapping("/withdraw/{withdrawalId}/status")
	public ResponseEntity getStatus(@PathVariable("withdrawalId") String accountId) {

		return ResponseEntity.
				ok(withdrawalService.getRequestState(new WithdrawalId(UUID.fromString(accountId))));
	}

	@PostMapping("/{fromAccount}/transfer/{toAccount}")
	public ResponseEntity transfer(
			@PathVariable("fromAccount") long fromAccountId,
			@PathVariable("toAccount") long toAccountId,
			@RequestBody @Valid MoneyTransferOperationRequest request) {

		verifyParameters(fromAccountId, toAccountId, request.getAmount());
		verifyAccount(accountService.containsAccount(fromAccountId), fromAccountId);
		verifyAccount(accountService.containsAccount(toAccountId), toAccountId);

		accountService.transfer(fromAccountId, toAccountId, request.getAmount());
		return ResponseEntity.noContent().build(); // no content is necessary
	}

	private void verifyParameters(Long sourceId, Long targetId, Double amount) {
		if(Objects.isNull(sourceId) || Objects.isNull(targetId) || Objects.isNull(amount) || 
				sourceId <= 0 || targetId <= 0 || amount<=0  || sourceId == targetId) {
			throw new HttpParametersException();
		}
		
	}
	private void verifyAccount(boolean accountExists, Long accountId) {
		if (!accountExists) {
			throw new UserAccountNotFoundException("Account not found for id: " + accountId);
		}
	}
}

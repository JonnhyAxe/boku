
package com.boku.cashtransactionservice.service;


import com.boku.cashtransactionservice.model.account.Account;
import com.boku.cashtransactionservice.model.account.AccountKey;
import com.boku.cashtransactionservice.service.exception.InsufficientAccountBalanceException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountServiceImpl implements AccountService {

	private final Map<AccountKey, Account> accounts = new ConcurrentHashMap<AccountKey, Account>();

	@Override
	public void clear() {
		accounts.clear();
	}

	@Override
	public void createAccount(Account account) {
		accounts.putIfAbsent(account.getAccountKey(), account);
	}

	@Override
	public Double getAccountBalance(long id) {
		Account account = accounts.get(AccountKey.valueOf(id));
		return Objects.nonNull(account) ? account.getBalance() : null ;
	}

	public boolean containsAccount(long id) { return accounts.containsKey(AccountKey.valueOf(id));}
	@Override
	public void transfer(long sourceId, long targetId, double amount) { //only one thread can do transfer

		AccountKey accountKeySource = AccountKey.valueOf(sourceId);
		AccountKey accountKeyTarget = AccountKey.valueOf(targetId);
		Account source = accounts.get(accountKeySource);
		Account target = accounts.get(accountKeyTarget);

		//Sort by Id in order to lock accounts with same strategy
		List<Account> accounts = Arrays.asList(source, target);
		Collections.sort(accounts);
		synchronized (accounts.get(0)) {
			synchronized (accounts.get(1)) {
			Double sourceBalance = source.getBalance();

			if (sourceBalance.doubleValue() < amount) {
				throw new InsufficientAccountBalanceException();
			}

			BigDecimal bigAmount = BigDecimal.valueOf(amount).setScale(5, RoundingMode.HALF_EVEN);

			BigDecimal sourceBigInteger = new BigDecimal(source.getBalance());
			BigDecimal targetBigInteger = new BigDecimal(target.getBalance());

			source.setBalance(sourceBigInteger.subtract(bigAmount).setScale(5, RoundingMode.HALF_EVEN).doubleValue());
			target.setBalance(targetBigInteger.add(bigAmount).setScale(5, RoundingMode.HALF_EVEN).doubleValue());

			}
		}
	}

	@Override
	public void withdraw(long accountId, double amount) {

		accounts.compute(AccountKey.valueOf(accountId), (k, v) -> {
			Account account = accounts.get(k);

			Double sourceBalance = account.getBalance();
			if (sourceBalance.doubleValue() < amount) {
				throw new InsufficientAccountBalanceException();
			}

			BigDecimal bigAmount = BigDecimal.valueOf(amount).setScale(5, RoundingMode.HALF_EVEN);
			BigDecimal targetBigInteger = new BigDecimal(account.getBalance());
			account.setBalance(targetBigInteger.subtract(bigAmount).setScale(5, RoundingMode.HALF_EVEN).doubleValue());

			return account;
		});
	}

	@Override
	public void credit(long accountId, double amount) {
		accounts.compute(AccountKey.valueOf(accountId), (k, v) -> {
			Account account = accounts.get(k);
			BigDecimal bigAmount = BigDecimal.valueOf(amount).setScale(5, RoundingMode.HALF_EVEN);
			BigDecimal targetBigInteger = new BigDecimal(account.getBalance());
			account.setBalance(targetBigInteger.add(bigAmount).setScale(5, RoundingMode.HALF_EVEN).doubleValue());

			return account;
		});
	}
}

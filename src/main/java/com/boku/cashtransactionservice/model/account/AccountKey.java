package com.boku.cashtransactionservice.model.account;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Unique Account identifier
 *
 * <p>
 * NOTE: immutable class to prevent changing during lifecycle of the app.
 * 		Implements the Comparable to support the ordering of accounts for Deadlock prevention strategy
 * */
public class AccountKey implements Comparable<AccountKey> {

	private static final AtomicLong ids =new AtomicLong(0);

	public static final long getNewId(){
		return ids.incrementAndGet();
	}
    private final long accountId;

	public long getAccountId() {
		return accountId;
	}

	private AccountKey(long accountId) {
        this.accountId = accountId;
    }

    public static AccountKey valueOf(long accountId) {
        return new AccountKey(accountId);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (accountId ^ (accountId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccountKey other = (AccountKey) obj;
		if (accountId != other.accountId)
			return false;
		return true;
	}

	@Override
	public int compareTo(AccountKey o) {
		return (int) (this.accountId - o.accountId);
	}

}

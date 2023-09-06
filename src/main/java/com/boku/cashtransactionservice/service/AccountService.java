package com.boku.cashtransactionservice.service;


import com.boku.cashtransactionservice.model.account.Account;

public interface AccountService {

    enum TransferState {
        PROCESSING, COMPLETED, FAILED
    }

    /**
     * Clears account cache
     *
     * */
    void clear();

    /**
     * Creates a new account
     *
     * @param account account entity to add or update
     * */
    void createAccount(Account account);


/**
     * Get account balance from the cache
     *
     * @param  id identification of an account to search for
     * @return account amount associated with given id or {@code null} if account is not found in the cache
     * */

    Double getAccountBalance(long id);


    /**
     * Check if the account by id exists
     *
     * @param  id identification of an account to search for
     * @return account if exists or not
     * */
     boolean containsAccount(long id);

    /**
     * Transfers given amount of money from source account to target account
     *
     * @param source account to transfer money from
     * @param target account to transfer money to
     * @param amount dollar amount to transfer
     * */
    void transfer(long source, long target, double amount);

    /**
     * Transfers given amount of money from source account to target account
     *
     * @param source account to transfer money from
     * @param amount dollar amount to transfer
     * */
    void withdraw(long source, double amount);

    /**
     * Transfers given amount of money from source account to target account
     *
     * @param source account to transfer money from
     * @param amount dollar amount to transfer
     * */
    void credit(long source, double amount);

}

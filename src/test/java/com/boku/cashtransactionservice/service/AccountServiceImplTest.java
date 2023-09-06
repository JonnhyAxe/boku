package com.boku.cashtransactionservice.service;

import com.boku.cashtransactionservice.model.account.Account;
import com.boku.cashtransactionservice.model.account.AccountKey;
import com.boku.cashtransactionservice.service.exception.InsufficientAccountBalanceException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

class AccountServiceImplTest {

    @Test
    public void givenTwoAccounts_WhenTransfer1kToAnother_thenAccountsAreConsistent() throws Exception {
        //Given
        AccountService accountService = new AccountServiceImpl();
        Account accountFrom = new Account(AccountKey.valueOf(1), "J", "M", 1000.0);
        Account accountTo = new Account(AccountKey.valueOf(2), "J", "M", 0.0);

        accountService.createAccount(accountFrom);
        accountService.createAccount(accountTo);

        //When
        accountService.transfer(1, 2, 1000.0);


        //Then
        assertEquals(0.0, accountFrom.getBalance());
        assertEquals(1000.0, accountTo.getBalance());

    }

    @Test
    public void givenAccountWith1k_WhenWithdraw1k_thenAccountIsBroke() throws Exception {
        //Given
        AccountService accountService = new AccountServiceImpl();
        Account accountFrom = new Account(AccountKey.valueOf(1), "J", "M", 1000.0);
        accountService.createAccount(accountFrom);

        //When
        accountService.withdraw(1,  1000.0);

        //Then
        assertEquals(0.0, accountFrom.getBalance());
    }


    @Test
    public void givenAccountWith1k_WhenDebit1k_thenAccountIsBalanceIs2k() throws Exception {
        //Given
        AccountService accountService = new AccountServiceImpl();
        Account accountFrom = new Account(AccountKey.valueOf(1), "J", "M", 1000.0);
        accountService.createAccount(accountFrom);

        //When
        accountService.credit(1,  1000.0);

        //Then
        assertEquals(2000.0, accountFrom.getBalance());
    }

    @Test
    public void givenOneTransferIsOngoing_whenAnotherTransferArrivesWForSameAccount_thenGetCorrectValue() throws InterruptedException, ExecutionException {
        //Given

        Map<AccountKey, Account> frequencyMap = new ConcurrentHashMap<>();
        Account accountFrom = new Account(AccountKey.valueOf(1), "J", "M", 1500.0);
        Account accountTo = new Account(AccountKey.valueOf(2), "J", "M", 0.0);

        frequencyMap.put(AccountKey.valueOf(1), accountFrom);
        frequencyMap.put(AccountKey.valueOf(2), accountTo);

        Double amount = 1000.0;
        Double amount2 = 500.0;
        ExecutorService threadExecutor = Executors.newFixedThreadPool(3);
        Callable<Account> writeAfter1Sec = () -> frequencyMap.computeIfPresent(AccountKey.valueOf(1), (k, v) -> {
            synchronized (accountFrom) {
                synchronized (accountTo) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    transfer(accountFrom, accountTo, amount);

                }
           }
           return frequencyMap.get(k);
        });

        Callable<Account>  writeAfter500ms = () -> frequencyMap.computeIfPresent(AccountKey.valueOf(2), (k, v) -> {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            synchronized (accountFrom) {
                synchronized (accountTo) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    transfer(accountFrom, accountTo, amount2);

                }
            }
            return frequencyMap.get(k);
        });
        
        Callable<Account> readAfter400ms = () -> {
            try {
                TimeUnit.MILLISECONDS.sleep(400);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Account account = frequencyMap.get(AccountKey.valueOf(1));
            return new Account(AccountKey.valueOf(0), account.getFirstName(), account.getLastName(), account.getBalance());

        };

        //When
        List<Future<Account>> results = threadExecutor.invokeAll(asList(readAfter400ms, writeAfter1Sec, writeAfter500ms));


        //Then
        assertEquals(AccountKey.valueOf(0), results.get(0).get().getAccountKey());
        assertEquals(AccountKey.valueOf(1), results.get(1).get().getAccountKey());
        assertEquals(AccountKey.valueOf(2), results.get(2).get().getAccountKey());

        assertEquals(0.0, accountFrom.getBalance());
        assertEquals(1500.0, accountTo.getBalance());

        if (threadExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
            threadExecutor.shutdown();
        }
    }

    private static void transfer(Account accountFrom, Account accountTo, Double amount) {
        Double sourceBalance = accountFrom.getBalance();

        if (sourceBalance.doubleValue() < amount) {
            throw new InsufficientAccountBalanceException();
        }

        BigDecimal bigAmount = BigDecimal.valueOf(amount).setScale(5, RoundingMode.HALF_EVEN);

        BigDecimal sourceBigInteger = new BigDecimal(accountFrom.getBalance());
        BigDecimal targetBigInteger = new BigDecimal(accountTo.getBalance());

        accountFrom.setBalance(sourceBigInteger.subtract(bigAmount).setScale(5, RoundingMode.HALF_EVEN).doubleValue());
        accountTo.setBalance(targetBigInteger.add(bigAmount).setScale(5, RoundingMode.HALF_EVEN).doubleValue());
    }
}
package edu.temple.cis.c3238.banksim;
import java.util.concurrent.locks.*;
/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 */

public class Bank {

    public static final int NTEST = 10;
    private final Account[] accounts;
    private long ntransacts = 0;
    private final int initialBalance;
    private final int numAccounts;
    private final Lock t_lock = new ReentrantLock();
    private final Condition enoughFunds = t_lock.newCondition();

    public Bank(int numAccounts, int initialBalance) {
        this.initialBalance = initialBalance;
        this.numAccounts = numAccounts;
        accounts = new Account[numAccounts];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account(this, i, initialBalance);
        }
        ntransacts = 0;
    }

    public void transfer(int from, int to, int amount) throws InterruptedException {
//        accounts[from].waitForAvailableFunds(amount);
        //Beginning of critical section; lock the reentrant lock.
        t_lock.lock();
        try {
            //While there is not enough balance in the acount to satisfy the transfer amount: wait!
            while(accounts[from].getBalance() < amount) 
                enoughFunds.await();
            
            if (accounts[from].withdraw(amount)) {
                accounts[to].deposit(amount);
                if (shouldTest()) 
                    test();
            }
            
            enoughFunds.signal();
        } finally {
            //Unlock Reentrant lock now that critical section is over.
            t_lock.unlock();
        }
    }

    
    public void test() {
        int sum = 0;
         t_lock.lock();
        try {
            for (Account account : accounts) {
                System.out.printf("%s %s%n", Thread.currentThread().toString(), account.toString());
                sum += account.getBalance();
            }
        } finally{
        t_lock.unlock();
        }
        System.out.println(Thread.currentThread().toString() + 
                " Sum: " + sum);
        if (sum != numAccounts * initialBalance) {
            System.out.println(Thread.currentThread().toString() + 
                    " Money was gained or lost");
            System.exit(0);
        } else {
            System.out.println(Thread.currentThread().toString() + 
                    " The bank is in balance");
        }
    }


    public int size() {
        return accounts.length;
    }
    
    
    public boolean shouldTest() {
        return ++ntransacts % NTEST == 0;
    }

}

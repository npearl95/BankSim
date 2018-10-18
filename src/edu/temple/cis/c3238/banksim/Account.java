package edu.temple.cis.c3238.banksim;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 */
public class Account {

    private volatile int balance;
    private final int id;
    private final Bank myBank;
    private final static Lock t_lock = new ReentrantLock();
    private final static Condition t_condition=t_lock.newCondition();

    public Account(Bank myBank, int id, int initialBalance) {
        this.myBank = myBank;
        this.id = id;
        balance = initialBalance;
    }

    public int getBalance() {
        return balance;
    }

    public boolean withdraw(int amount) {
        if (amount <= balance) {
            int currentBalance = balance;
//            Thread.yield(); // Try to force collision
            int newBalance = currentBalance - amount;
            balance = newBalance;
            return true;
        } else {
            return false;
        }
    }

    public void deposit(int amount){
        int currentBalance = balance;
//        Thread.yield();   // Try to force collision
        int newBalance = currentBalance + amount;
        balance = newBalance;
    }
    
    @Override
    public String toString() {
        return String.format("Account[%d] balance %d", id, balance);
    }
    
    public void waitForAvailableFunds(int amount) throws InterruptedException{
        t_lock.lock();
        try{
            while(balance < amount){
                //System.out.println("balance: "+balance+" amount: "+amount);
                t_condition.await();

            }
            t_condition.signalAll();
        }finally{
            t_lock.unlock();
        }
    }
}
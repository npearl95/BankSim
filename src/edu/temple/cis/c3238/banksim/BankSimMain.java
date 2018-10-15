package edu.temple.cis.c3238.banksim;
import java.util.concurrent.locks.*;
/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 */
public class BankSimMain {

    public static final int NACCOUNTS = 10;
    public static final int INITIAL_BALANCE = 10000;
    private static final Lock t_lock = new ReentrantLock();

    public static void main(String[] args) {
        
        
        Bank b = new Bank(NACCOUNTS, INITIAL_BALANCE);
        
        Thread[] threads = new Thread[NACCOUNTS];
        
        // Start a thread for each account
        for (int i = 0; i < NACCOUNTS; i++) {
            t_lock.lock();
            try{
                threads[i] = new TransferThread(b, i, INITIAL_BALANCE);
                threads[i].start();
            }
            finally{
                t_lock.unlock();
            }
            
            
        }

//        b.test();
          System.out.printf("Bank transfer is in the process.\n");
    }
}



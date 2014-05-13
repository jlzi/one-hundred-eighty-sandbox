package org.ys.finder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by syaskov on 13.05.2014.
 */
public class StringGenerator {

    private final BlockingQueue<String> outcome = new ArrayBlockingQueue<>(1024);

    public void start() {
        final List<Character> alphabet = new ArrayList<>();

        for (char c = 'a'; c <= 'z'; ++c) {
            alphabet.add(c);
        }
        for (char c = 'A'; c <= 'Z'; ++c) {
            alphabet.add(c);
        }
        for (char c = '0'; c <= '9'; ++c) {
            alphabet.add(c);
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                printAllKLength(alphabet.toArray(new Character[alphabet.size()]), 4);
            }
        });
        thread.setDaemon(true); // TODO how the daemon thread is interrupted?
        thread.start();
    }

    public String next() {
        try {
            return outcome.take();
        } catch (InterruptedException e) {
            e.printStackTrace(); // TODO
            return null;
        }
    }

    // The method that prints all possible strings of length k.  It is
    //  mainly a wrapper over recursive function printAllKLengthRec()
    private void printAllKLength(Character set[], int k) {
        int n = set.length;
        printAllKLengthRec(set, "", n, k);
    }

    // The main recursive method to print all possible strings of length k
    private void printAllKLengthRec(Character set[], String prefix, int n, int k) {

        // Base case: k is 0, print prefix
        if (k == 0) {
            try {
                outcome.put(prefix);
            } catch (InterruptedException e) {
                e.printStackTrace(); // TODO
            }
            return;
        }

        // One by one add all characters from set and recursively
        // call for k equals to k-1
        for (int i = 0; i < n; ++i) {

            // Next character of input added
            String newPrefix = prefix + set[i];

            // k is decreased, because we have added a new character
            printAllKLengthRec(set, newPrefix, n, k - 1);
        }
    }

}

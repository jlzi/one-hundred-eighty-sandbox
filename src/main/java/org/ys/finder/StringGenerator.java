package org.ys.finder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class StringGenerator {

    private static final int LENGTH_OF_WORDS = 4;

    private final BlockingQueue<String> words = new ArrayBlockingQueue<>(1024);

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

        Thread thread = new Thread(() -> allPossibleWords(alphabet.toArray(new Character[alphabet.size()]), LENGTH_OF_WORDS));
        thread.setDaemon(true); // TODO how the daemon thread is interrupted?
        thread.start();
    }

    public String next() {
        try {
            return words.take();
        } catch (InterruptedException e) {
            e.printStackTrace(); // TODO
            return null;
        }
    }

    private void allPossibleWords(Character[] alphabet, int length) {
        int n = alphabet.length;
        allPossibleWords(alphabet, "", n, length);
    }

    private void allPossibleWords(Character[] alphabet, String prefix, int n, int length) {
        if (length == 0) {
            try {
                words.put(prefix);
            } catch (final InterruptedException e) {
                e.printStackTrace(); // TODO
            }
            return;
        }

        for (int i = 0; i < n; ++i) {
            String newPrefix = prefix + alphabet[i];
            allPossibleWords(alphabet, newPrefix, n, length - 1);
        }
    }
}

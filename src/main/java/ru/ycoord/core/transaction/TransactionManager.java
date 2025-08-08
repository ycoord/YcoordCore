package ru.ycoord.core.transaction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionManager {
    private static final ConcurrentHashMap<String, Set<String>> transactions = new ConcurrentHashMap<>();

    public static void lock(String key, String value) {
        Set<String> values = transactions.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet());
        assert !values.contains(value);
        values.add(value);
    }

    public static void unlock(String key, String value) {
        Set<String> values = transactions.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet());
        assert values.contains(value);
        values.remove(value);
    }

    public static boolean inProgress(String key, String value) {
        Set<String> values = transactions.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet());
        return values.contains(value);
    }
}

package de.joint.util;

import java.util.Map;

public interface Counter {
    static <K, V extends Comparable<? super V>> Iterable<Map.Entry<K, V>> sortedIterator(Map<K, V> counter) {
        return counter.entrySet().stream().sorted(Map.Entry.<K, V>comparingByValue().reversed())::iterator;
    }
}

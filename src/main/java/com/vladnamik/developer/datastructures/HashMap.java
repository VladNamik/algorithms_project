package com.vladnamik.developer.datastructures;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;


// TODO find a mistake and check again
public class HashMap<K, V> {
    private static final int MAX_BUCKET_SIZE = 10;
    private static final int INITIAL_TABLE_SIZE = 11;
    private static final float RESIZE_COEFFICIENT = 1.618f;

    private ArrayList<ArrayList<Pair<K, V>>> hashTable = new ArrayList<>(INITIAL_TABLE_SIZE);
    {
        for (int i = 0; i < INITIAL_TABLE_SIZE; i++) {
            hashTable.add(new ArrayList<>());
        }
    }
    int size = 0;

    public boolean containsKey(K key) {
        if (get(key) != null) {
            return true;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public void put(K key, V value) {
        if (containsKey(key)) {
            remove(key);
        }

        int index = key.hashCode() % hashTable.size();
        hashTable.get(index).add(new Pair<>(key, value));
        size++;

        if (hashTable.get(index).size() > MAX_BUCKET_SIZE) {
            resize();
        }
    }

    private void resize() {
        int newSize = (int)(hashTable.size() * RESIZE_COEFFICIENT);
        ArrayList<ArrayList<Pair<K, V>>> newHashTable = new ArrayList<>(newSize);
        for (int i = 0; i < newSize; i++) {
            newHashTable.add(new ArrayList<>());
        }
        for (ArrayList<Pair<K, V>> bucket: hashTable) {
            for (Pair<K, V> pair : bucket) {
                newHashTable.get(pair.getKey().hashCode() % newSize).add(pair);
            }
        }
        hashTable.clear();
        hashTable = newHashTable;
    }

    public void remove(K key) {
        int index = key.hashCode() % hashTable.size();
        List<Pair<K, V>> bucket = hashTable.get(index);
        int bucketSize = bucket.size();
        for (int i = 0; i <  bucketSize; i++) {
            if (bucket.get(i).getKey().equals(key)) {
                bucket.remove(i);
                size--;
                break;
            }
        }
    }

    public V get(K key) {
        int index = key.hashCode() % hashTable.size();
        for (Pair<K, V> pair: hashTable.get(index)) {
            if (pair.getKey().equals(key)) {
                return pair.getValue();
            }
        }
        return null;
    }
}

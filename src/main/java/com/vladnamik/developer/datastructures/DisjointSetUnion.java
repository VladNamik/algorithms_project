package com.vladnamik.developer.datastructures;

import java.util.ArrayList;

/**
 * Система неперсекающихся множеств, построенная на корневом дереве
 */

public class DisjointSetUnion<T> {
    public static class IntegerProperty {
        private int data;

        public IntegerProperty(int data) {
            this.data = data;
        }

        public int getInt() {
            return data;
        }

        public void setInt(int data) {
            this.data = data;
        }
    }

    private ArrayList<IntegerProperty> parents = new ArrayList<>();
    private ArrayList<IntegerProperty> rank = new ArrayList<>();

    private ArrayList<T> sets = new ArrayList<>();

    public int makeSet(T data) {
        sets.add(data);
        parents.add(new IntegerProperty(sets.size() - 1));
        rank.add(new IntegerProperty(0));
        return sets.size() - 1;
    }

    public int find(int id) {
        ArrayList<Integer> parentResets = new ArrayList<>();
        while (parents.get(id).getInt() != id) {
            parentResets.add(id);
            id = parents.get(id).getInt();
        }
        id = parents.get(id).getInt();
        for (Integer child: parentResets) {
            parents.get(child).setInt(id);
            rank.get(child).setInt(1);
        }
        return id;
    }

    public T get(int id) {
        return sets.get(id);
    }

    public void union(int sourceId, int destinationId) {
        int sourceHeadId = find(sourceId);
        int destinationHeadId = find(destinationId);

        if (sourceHeadId == destinationHeadId) {
            return;
        }
        if (rank.get(sourceHeadId).getInt() > rank.get(destinationHeadId).getInt()) {
            parents.get(destinationHeadId).setInt(sourceHeadId);
        } else {
            parents.get(sourceHeadId).setInt(destinationHeadId);
            if (rank.get(sourceHeadId).getInt() == rank.get(destinationHeadId).getInt()) {
                rank.get(destinationHeadId).setInt(rank.get(destinationHeadId).getInt() + 1);
            }
        }

    }

}

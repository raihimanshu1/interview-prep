package com.companywisejavasolutions.ebay.solutions;

import java.util.Random;

public class DesignSkiplist {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Implement search, add, and erase for a skiplist. A skiplist stores sorted
     * values with multiple forward lanes so we can skip over many nodes quickly.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * It is like a sorted train route with express lanes above the normal lane.
     * Search moves right while it can, then drops down one lane.
     */
    public static class Skiplist {
        private static final int MAX_LEVEL = 16;
        private final Node head = new Node(-1, MAX_LEVEL);
        private final Random random = new Random(1);
        private int level = 1;

        public boolean search(int target) {
            Node current = head;
            for (int i = level - 1; i >= 0; i--) {
                while (current.next[i] != null && current.next[i].value < target) {
                    current = current.next[i];
                }
            }
            current = current.next[0];
            return current != null && current.value == target;
        }

        public void add(int num) {
            Node[] update = new Node[MAX_LEVEL];
            Node current = head;

            for (int i = level - 1; i >= 0; i--) {
                while (current.next[i] != null && current.next[i].value < num) {
                    current = current.next[i];
                }
                update[i] = current;
            }

            int nodeLevel = randomLevel();
            if (nodeLevel > level) {
                for (int i = level; i < nodeLevel; i++) {
                    update[i] = head;
                }
                level = nodeLevel;
            }

            Node node = new Node(num, nodeLevel);
            for (int i = 0; i < nodeLevel; i++) {
                node.next[i] = update[i].next[i];
                update[i].next[i] = node;
            }
        }

        public boolean erase(int num) {
            Node[] update = new Node[MAX_LEVEL];
            Node current = head;

            for (int i = level - 1; i >= 0; i--) {
                while (current.next[i] != null && current.next[i].value < num) {
                    current = current.next[i];
                }
                update[i] = current;
            }

            current = current.next[0];
            if (current == null || current.value != num) {
                return false;
            }

            for (int i = 0; i < level; i++) {
                if (update[i].next[i] != current) {
                    break;
                }
                update[i].next[i] = current.next[i];
            }

            while (level > 1 && head.next[level - 1] == null) {
                level--;
            }

            return true;
        }

        private int randomLevel() {
            int newLevel = 1;
            while (newLevel < MAX_LEVEL && random.nextBoolean()) {
                newLevel++;
            }
            return newLevel;
        }
    }

    private static class Node {
        int value;
        Node[] next;
        Node(int value, int level) {
            this.value = value;
            this.next = new Node[level];
        }
    }
}

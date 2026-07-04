
package com.patternwisejavasolutions.trees.important;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class SerializeAndDeserializeBinaryTree {

    /* PROBLEM IN SIMPLE WORDS
     * Convert a binary tree to a string, then build the exact same tree back from that string.
     *
     * Sample Input:  root = [1,2,3,null,null,4,5]
     * Sample Output after serialize: "1,2,#,#,3,4,#,#,5,#,#,"
     * Sample Output after deserialize: [1,2,3,null,null,4,5]
     */

    /* SCHOOL-LEVEL INTUITION
     * To rebuild the exact same tree, the string must record both values and missing children.
     * Null markers are important because they tell us where a branch stopped.
     * If we write nodes in a consistent order, we can read the string back in that same order.
     */

    /* APPROACH 1: BRUTE FORCE INTUITION
     * Use level order and include null markers, so structure is not lost.
     */

    /* BRUTE FORCE DRY RUN
     * Tree [1,2,3,null,null,4,5] can become "1,2,3,#,#,4,5,#,#,#,#".
     */

    /* BRUTE FORCE ALGORITHM
     * 1. Visit nodes level by level using BFS.
     * 2. Write real values for real nodes and # for null child positions.
     * 3. During deserialization, read tokens in level order and rebuild left and right children.
     *
     * Time Complexity:
     * Every real node and null marker is processed once, so the time is O(n).
     *
     * Space Complexity:
     * The queue and output string store tree information, so the space is O(n).
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public String bruteForceSerialize(TreeNode root) {
        if (root == null) {
            return "#,";
        }

        StringBuilder builder = new StringBuilder();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();

            if (node == null) {
                builder.append("#,");
                continue;
            }

            // Write the node value, then keep both child positions so structure is preserved.
            builder.append(node.val).append(',');
            queue.offer(node.left);
            queue.offer(node.right);
        }

        return builder.toString();
    }

    public TreeNode bruteForceDeserialize(String data) {
        String[] values = data.split(",");
        if (values.length == 0 || values[0].equals("#")) {
            return null;
        }

        TreeNode root = new TreeNode(Integer.parseInt(values[0]));
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int index = 1;

        while (!queue.isEmpty() && index < values.length) {
            TreeNode node = queue.poll();

            if (!values[index].equals("#")) {
                node.left = new TreeNode(Integer.parseInt(values[index]));
                queue.offer(node.left);
            }
            index++;

            if (index < values.length && !values[index].equals("#")) {
                node.right = new TreeNode(Integer.parseInt(values[index]));
                queue.offer(node.right);
            }
            index++;
        }

        return root;
    }

    /* APPROACH 2: OPTIMIZED INTUITION
     * Preorder DFS is simple: write node value, then left subtree, then right subtree. Use # for null so we know where branches end.
     */

    /* OPTIMIZED DRY RUN
     * Preorder of root 1 writes 1, then serializes left 2, then #,# for 2 children, then right subtree.
     */

    /* OPTIMIZED ALGORITHM
     * 1. Serialize using preorder: node, left subtree, right subtree.
     * 2. Write # whenever a null pointer appears.
     * 3. Deserialize by reading tokens in the same preorder order.
     * 4. When a # is read, return null for that child.
     *
     * Time Complexity:
     * Every token is written and read once, so the time is O(n).
     *
     * Space Complexity:
     * The string, token queue, and recursion stack use O(n) space.
     */

    /* OPTIMIZED IMPLEMENTATION */
    public String serialize(TreeNode root) {
        StringBuilder builder = new StringBuilder();
        serializeDfs(root, builder);
        return builder.toString();
    }

    public TreeNode deserialize(String data) {
        Queue<String> tokens = new LinkedList<>(Arrays.asList(data.split(",")));
        return deserializeDfs(tokens);
    }

    private void serializeDfs(TreeNode node, StringBuilder builder) {
        if (node == null) {
            builder.append("#,");
            return;
        }

        // Preorder writes the current node before its children.
        builder.append(node.val).append(',');
        serializeDfs(node.left, builder);
        serializeDfs(node.right, builder);
    }

    private TreeNode deserializeDfs(Queue<String> tokens) {
        String token = tokens.poll();
        if (token.equals("#")) {
            return null;
        }

        // Because serialize used preorder, the next tokens belong to this node's left and right subtrees.
        TreeNode node = new TreeNode(Integer.parseInt(token));
        node.left = deserializeDfs(tokens);
        node.right = deserializeDfs(tokens);
        return node;
    }


    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int val) {
            this.val = val;
        }
    }
}

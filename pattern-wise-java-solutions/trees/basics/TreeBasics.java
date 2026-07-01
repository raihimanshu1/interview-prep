/**
 * TREE — Basics & Warmup
 * 
 * Before jumping into problems, master these fundamentals:
 * 1. TreeNode structure (binary tree)
 * 2. Tree traversals: inorder, preorder, postorder (recursive & iterative)
 * 3. Level-order traversal (BFS)
 * 4. Tree height & depth
 * 5. Check if a tree is balanced
 * 6. Build tree from arrays
 * 7. Lowest Common Ancestor (basic idea)
 * 8. Tree patterns & framework
 */

import java.util.*;

public class TreeBasics {

    // ==========================================
    // 1. TREE NODE STRUCTURE
    // ==========================================
    
    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    // ==========================================
    // 2. DEPTH-FIRST TRAVERSALS (RECURSIVE) — O(n)
    // ==========================================
    
    // INORDER: left → root → right
    public static void inorderRecursive(TreeNode root, List<Integer> result) {
        if (root == null) return;
        inorderRecursive(root.left, result);
        result.add(root.val);
        inorderRecursive(root.right, result);
    }
    
    // PREORDER: root → left → right
    public static void preorderRecursive(TreeNode root, List<Integer> result) {
        if (root == null) return;
        result.add(root.val);
        preorderRecursive(root.left, result);
        preorderRecursive(root.right, result);
    }
    
    // POSTORDER: left → right → root
    public static void postorderRecursive(TreeNode root, List<Integer> result) {
        if (root == null) return;
        postorderRecursive(root.left, result);
        postorderRecursive(root.right, result);
        result.add(root.val);
    }

    // ==========================================
    // 3. DEPTH-FIRST TRAVERSALS (ITERATIVE) — O(n)
    // ==========================================
    // Critical for interviews! Must know both recursive & iterative.
    
    // INORDER iterative: use a stack
    // Pattern: go left as far as possible, pop, go right
    public static List<Integer> inorderIterative(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        TreeNode curr = root;
        
        while (curr != null || !stack.isEmpty()) {
            while (curr != null) {          // go left as far as possible
                stack.push(curr);
                curr = curr.left;
            }
            curr = stack.pop();             // process node
            result.add(curr.val);
            curr = curr.right;              // go right
        }
        return result;
    }
    
    // PREORDER iterative
    // Pattern: process root, push right, push left (LIFO)
    public static List<Integer> preorderIterative(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            result.add(node.val);
            if (node.right != null) stack.push(node.right);  // right first (LIFO)
            if (node.left != null) stack.push(node.left);     // left processed first
        }
        return result;
    }
    
    // POSTORDER iterative (using 2 stacks)
    // Pattern: reverse of preorder (root→right→left)
    public static List<Integer> postorderIterative(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;
        Stack<TreeNode> stack1 = new Stack<>();
        Stack<TreeNode> stack2 = new Stack<>();
        stack1.push(root);
        
        while (!stack1.isEmpty()) {
            TreeNode node = stack1.pop();
            stack2.push(node);
            if (node.left != null) stack1.push(node.left);
            if (node.right != null) stack1.push(node.right);
        }
        
        while (!stack2.isEmpty()) {
            result.add(stack2.pop().val);
        }
        return result;
    }

    // ==========================================
    // 4. BFS (LEVEL-ORDER) TRAVERSAL — O(n)
    // ==========================================
    // Uses a queue. Very important for tree problems.
    
    public static List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;
        
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            int levelSize = queue.size();          // nodes at current level
            List<Integer> currentLevel = new ArrayList<>();
            
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                currentLevel.add(node.val);
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            result.add(currentLevel);
        }
        return result;
    }

    // ==========================================
    // 5. TREE HEIGHT & DEPTH — O(n)
    // ==========================================
    
    // Height: max edges from root to deepest leaf
    public static int height(TreeNode root) {
        if (root == null) return 0;
        return 1 + Math.max(height(root.left), height(root.right));
    }
    
    // Depth of a specific node (edges from root to node)
    public static int depth(TreeNode root, TreeNode target, int d) {
        if (root == null) return -1;
        if (root == target) return d;
        int left = depth(root.left, target, d + 1);
        if (left != -1) return left;
        return depth(root.right, target, d + 1);
    }

    // ==========================================
    // 6. CHECK IF BALANCED — O(n)
    // ==========================================
    // A tree is balanced if height difference ≤ 1 for ALL nodes
    
    public static boolean isBalanced(TreeNode root) {
        return checkHeight(root) != -1;
    }
    
    private static int checkHeight(TreeNode root) {
        if (root == null) return 0;
        int left = checkHeight(root.left);
        if (left == -1) return -1;        // left subtree not balanced
        int right = checkHeight(root.right);
        if (right == -1) return -1;       // right subtree not balanced
        if (Math.abs(left - right) > 1) return -1;  // current node not balanced
        return 1 + Math.max(left, right);
    }

    // ==========================================
    // 7. BUILD TREE FROM ARRAY (NULL-safe)
    // ==========================================
    // Input: [1, 2, 3, null, 5, null, null]
    // Output:    1
    //           / \
    //          2   3
    //           \
    //            5
    
    public static TreeNode buildTree(Integer[] arr) {
        if (arr == null || arr.length == 0 || arr[0] == null) return null;
        
        TreeNode root = new TreeNode(arr[0]);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        int i = 1;
        while (i < arr.length && !queue.isEmpty()) {
            TreeNode current = queue.poll();
            
            // Left child
            if (i < arr.length && arr[i] != null) {
                current.left = new TreeNode(arr[i]);
                queue.offer(current.left);
            }
            i++;
            
            // Right child
            if (i < arr.length && arr[i] != null) {
                current.right = new TreeNode(arr[i]);
                queue.offer(current.right);
            }
            i++;
        }
        return root;
    }
    
    // Helper: print tree structure
    public static void printTree(TreeNode root) {
        List<List<Integer>> levels = levelOrder(root);
        for (int i = 0; i < levels.size(); i++) {
            System.out.println("Level " + i + ": " + levels.get(i));
        }
    }

    // ==========================================
    // 8. DFS FRAMEWORK (RECURSIVE PATTERN)
    // ==========================================
    // Most tree problems follow this structure:
    
    public static void dfsFramework(TreeNode root) {
        if (root == null) return;
        
        // Pre-order: process BEFORE children
        // System.out.println(root.val);  // top-down
        
        dfsFramework(root.left);
        
        // In-order: process BETWEEN children
        // System.out.println(root.val);  // sorted order for BST
        
        dfsFramework(root.right);
        
        // Post-order: process AFTER children
        // System.out.println(root.val);  // bottom-up
    }

    // ==========================================
    // 9. BFS FRAMEWORK
    // ==========================================
    
    public static void bfsFramework(TreeNode root) {
        if (root == null) return;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                // Process node
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
        }
    }

    // ==========================================
    // MAIN — Test everything
    // ==========================================
    
    public static void main(String[] args) {
        // Build tree: [1, 2, 3, 4, 5, 6, 7]
        //       1
        //      / \
        //     2   3
        //    / \ / \
        //   4  5 6  7
        TreeNode root = buildTree(new Integer[]{1, 2, 3, 4, 5, 6, 7});
        
        System.out.println("=== TREE BASICS ===");
        System.out.println("Tree structure:");
        printTree(root);
        
        System.out.println("\n=== DEPTH-FIRST TRAVERSALS ===");
        
        List<Integer> inorder = new ArrayList<>();
        inorderRecursive(root, inorder);
        System.out.println("Inorder (recursive): " + inorder);
        System.out.println("Inorder (iterative): " + inorderIterative(root));
        
        List<Integer> preorder = new ArrayList<>();
        preorderRecursive(root, preorder);
        System.out.println("Preorder (recursive): " + preorder);
        System.out.println("Preorder (iterative): " + preorderIterative(root));
        
        List<Integer> postorder = new ArrayList<>();
        postorderRecursive(root, postorder);
        System.out.println("Postorder (recursive): " + postorder);
        System.out.println("Postorder (iterative): " + postorderIterative(root));
        
        System.out.println("\n=== BFS LEVEL-ORDER ===");
        System.out.println("Level-order: " + levelOrder(root));
        
        System.out.println("\n=== PROPERTIES ===");
        System.out.println("Height: " + height(root));
        System.out.println("Is balanced: " + isBalanced(root));
        
        System.out.println("\n=== KEY PATTERNS & FRAMEWORK ===");
        System.out.println("1. DFS (Recursive): Inorder/Preorder/Postorder — O(n)");
        System.out.println("   → Use preorder for top-down processing");
        System.out.println("   → Use inorder for BST sorted order");  
        System.out.println("   → Use postorder for bottom-up processing");
        System.out.println("2. BFS (Iterative with Queue): Level-order — O(n)");
        System.out.println("   → Use when you need shortest path or level info");
        System.out.println("3. Height-based recursion pattern:");
        System.out.println("   int solve(TreeNode root) {");
        System.out.println("       if (root == null) return 0;");
        System.out.println("       int left = solve(root.left);");
        System.out.println("       int right = solve(root.right);");
        System.out.println("       return 1 + Math.max(left, right);");
        System.out.println("   }");
        System.out.println("4. BST Property: inorder traversal gives sorted order");
    }
}
# Trees

> **Core Pattern:** Recursion — traverse, compare, and construct binary trees using DFS/BFS.  
> **Learning Path:** Traversals → DFS core (depth, symmetry) → BFS (level order) → BST → Structural → Construction → Advanced.

---

## 📖 Conceptual Foundation

### Tree Traversal Cheatsheet
| Traversal | Order | Use Case |
|-----------|-------|----------|
| Preorder | Root → Left → Right | Tree copying, serialization |
| Inorder | Left → Root → Right | BST → sorted order |
| Postorder | Left → Right → Root | Tree deletion, bottom-up calc |
| Level order (BFS) | Level by level | Right side view, zigzag |

### Recursive DFS Template
```java
public void dfs(TreeNode node) {
    if (node == null) return;
    // pre-order: process node
    dfs(node.left);
    // in-order: process node
    dfs(node.right);
    // post-order: process node
}
```

### BFS (Level Order) Template
```java
Queue<TreeNode> queue = new LinkedList<>();
queue.offer(root);
while (!queue.isEmpty()) {
    int size = queue.size();
    for (int i = 0; i < size; i++) {
        TreeNode node = queue.poll();
        // process level node
        if (node.left != null) queue.offer(node.left);
        if (node.right != null) queue.offer(node.right);
    }
}
```

---

## 📚 Learning Order

### Phase 1: Tree Traversals

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 1 | **Binary Tree Inorder** | [traversal/BinaryTreeInorderTraversal.java](traversal/BinaryTreeInorderTraversal.java) | Left → Root → Right (recursive + iterative) | 🟢 Easy |
| 2 | **Binary Tree Preorder** | [traversal/BinaryTreePreorderTraversal.java](traversal/BinaryTreePreorderTraversal.java) | Root → Left → Right | 🟢 Easy |
| 3 | **Binary Tree Postorder** | [traversal/BinaryTreePostorderTraversal.java](traversal/BinaryTreePostorderTraversal.java) | Left → Right → Root | 🟢 Easy |

### Phase 2: DFS Core (Recursion)

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 4 | **Maximum Depth of Binary Tree** | [dfs-core/MaximumDepthOfBinaryTree.java](dfs-core/MaximumDepthOfBinaryTree.java) | `1 + max(depth(left), depth(right))` | 🟢 Easy |
| 5 | **Same Tree** | [dfs-core/SameTree.java](dfs-core/SameTree.java) | Compare both trees simultaneously | 🟢 Easy |
| 6 | **Invert Binary Tree** | [dfs-core/InvertBinaryTree.java](dfs-core/InvertBinaryTree.java) | Swap children recursively | 🟢 Easy |
| 7 | **Validate BST** | [dfs-core/ValidateBST.java](dfs-core/ValidateBST.java) | Pass `min/max` range downward | 🟡 Medium |

### Phase 3: BFS (Level Order)

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 8 | **Level Order Traversal** | [bfs/LevelOrderTraversal.java](bfs/LevelOrderTraversal.java) | Queue per level | 🟡 Medium |
| 9 | **Binary Tree Zigzag Level Order** | [bfs/BinaryTreeZigzagLevelOrderTraversal.java](bfs/BinaryTreeZigzagLevelOrderTraversal.java) | Level order with direction toggle | 🟡 Medium |
| 10 | **Binary Tree Right Side View** | [bfs/BinaryTreeRightSideView.java](bfs/BinaryTreeRightSideView.java) | BFS — take last node at each level | 🟡 Medium |

### Phase 4: BST (Binary Search Tree)

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 11 | **Search in BST** | [bst/SearchInBST.java](bst/SearchInBST.java) | Compare value, go left/right | 🟢 Easy |
| 12 | **Insert into BST** | [bst/InsertIntoBST.java](bst/InsertIntoBST.java) | Recursive insert at leaf | 🟡 Medium |
| 13 | **Delete Node in BST** | [bst/DeleteNodeInBST.java](bst/DeleteNodeInBST.java) | 3 cases: leaf, one child, two children | 🟡 Medium |
| 14 | **Convert Sorted Array to BST** | [bst/ConvertSortedArrayToBST.java](bst/ConvertSortedArrayToBST.java) | Pick mid as root, recurse left/right | 🟢 Easy |
| 15 | **LCA of BST** | [bst/LowestCommonAncestorOfBST.java](bst/LowestCommonAncestorOfBST.java) | If both on left/right → go that way, else this is LCA | 🟡 Medium |
| 16 | **BST Iterator** | [bst/BSTIterator.java](bst/BSTIterator.java) | Controlled inorder via stack | 🟡 Medium |

### Phase 5: Structural

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 17 | **Symmetric Tree** | [structural/SymmetricTree.java](structural/SymmetricTree.java) | Compare left.left with right.right + left.right with right.left | 🟢 Easy |
| 18 | **Balanced Binary Tree** | [structural/BalancedBinaryTree.java](structural/BalancedBinaryTree.java) | Post-order: compute height, check balance at each node | 🟢 Easy |

### Phase 6: Important / Frequently Asked

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 19 | **Diameter of Binary Tree** | [important/DiameterOfBinaryTree.java](important/DiameterOfBinaryTree.java) | Post-order: `max(leftHeight + rightHeight)`, return height | 🟢 Easy |
| 20 | **Path Sum** | [important/PathSum.java](important/PathSum.java) | DFS with remaining target | 🟢 Easy |
| 21 | **Lowest Common Ancestor (BT)** | [important/LowestCommonAncestor.java](important/LowestCommonAncestor.java) | Post-order: if node is p/q, return upward | 🟡 Medium |
| 22 | **Kth Smallest in BST** | [important/KthSmallestElementInBST.java](important/KthSmallestElementInBST.java) | Inorder traversal with counter | 🟡 Medium |
| 23 | **Binary Tree Maximum Path Sum** | [important/BinaryTreeMaximumPathSum.java](important/BinaryTreeMaximumPathSum.java) | Post-order: `max(left, right, left+right+node)` | 🔴 Hard |
| 24 | **Serialize and Deserialize BT** | [important/SerializeAndDeserializeBinaryTree.java](important/SerializeAndDeserializeBinaryTree.java) | Preorder with `#` for null markers | 🔴 Hard |
| 25 | **Construct BT from Preorder & Inorder** | [important/ConstructBinaryTreeFromPreorderAndInorderTraversal.java](important/ConstructBinaryTreeFromPreorderAndInorderTraversal.java) | Pre[0]=root, find in inorder, split left/right | 🟡 Medium |
| 26 | **Construct BT from Inorder & Postorder** | [important/ConstructBinaryTreeFromInorderAndPostorderTraversal.java](important/ConstructBinaryTreeFromInorderAndPostorderTraversal.java) | Post[last]=root, find in inorder, split | 🟡 Medium |

---

## 🔑 Key Insights

1. **Recursion is the foundation** — every tree problem starts with understanding how to recurse
2. **Inorder on BST** gives sorted order
3. **Post-order** is best for bottom-up calculations (height, diameter, max path)
4. **BFS** for level-related problems, **DFS** for path/value problems
5. **BST property** = `left < root < right` — use ranges for validation

---

## 🎯 Practice Checklist

- [ ] Phase 1: Three traversals memorized
- [ ] Phase 2: DFS core recursion
- [ ] Phase 3: BFS level order
- [ ] Phase 4: BST operations
- [ ] Phase 5: Structural checks
- [ ] Phase 6: Important & advanced
import java.util.HashMap;
import java.util.Map;

public class DesignFileSystem {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: createPath("/a", 1), get("/a"), createPath("/a/b", 2)
     * Sample Output: true, 1, true
     *
     * Build a file system that creates a path only when its parent exists.
     * get(path) returns the stored value or -1.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A folder can be created only inside an existing folder. So "/a/b" is valid
     * only after "/a" already exists.
     * A trie maps directly to a real folder tree: each node is one path part,
     * and a child map represents folders inside that folder.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Store full paths in a map. To create a path, cut off the last name and
     * check whether the parent path exists.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Map full path string to value.
     * 2. createPath returns false if path already exists or parent is missing.
     * 3. Otherwise save path and value.
     * 4. get reads from the map or returns -1.
     *
     * Time Complexity: O(path length)
     * Space Complexity: O(number of paths)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * createPath("/a/b", 2) before "/a" exists returns false.
     * createPath("/a", 1) succeeds because parent is root.
     * createPath("/a/b", 2) now succeeds.
     */

    public static class BruteForce {
        private Map<String, Integer> values = new HashMap<>();

        public boolean createPath(String path, int value) {
            if (path == null || path.length() == 0 || path.equals("/") || values.containsKey(path)) {
                return false;
            }

            int lastSlash = path.lastIndexOf('/');
            String parent = path.substring(0, lastSlash);

            if (parent.length() > 0 && !values.containsKey(parent)) {
                return false;
            }

            values.put(path, value);
            return true;
        }

        public int get(String path) {
            return values.getOrDefault(path, -1);
        }
    }

    /*
     * OPTIMIZED INTUITION
     *
     * A trie matches the folder tree itself. Each node is a folder name, and its
     * children are the folders directly inside it.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Start at root.
     * 2. For createPath, walk every component except the last; all must exist.
     * 3. Add the last component only if it is missing.
     * 4. For get, walk all components and return the final node value.
     *
     * Time Complexity: O(path length)
     * Space Complexity: O(total path components)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * create "/a": root gets child "a" with value 1.
     * create "/a/b": walk root -> a, then add child "b".
     * get "/a/b": walk to b and return 2.
     */

    public static class Optimized {
        private Node root = new Node(-1);

        public boolean createPath(String path, int value) {
            if (path == null || path.length() == 0 || path.equals("/")) {
                return false;
            }

            String[] parts = path.split("/");
            Node current = root;

            for (int i = 1; i < parts.length - 1; i++) {
                current = current.children.get(parts[i]);
                if (current == null) {
                    // A missing parent folder means the new path cannot be created.
                    return false;
                }
            }

            String name = parts[parts.length - 1];
            if (name.length() == 0 || current.children.containsKey(name)) {
                // Empty names or duplicate folder names are not valid creations.
                return false;
            }

            current.children.put(name, new Node(value));
            return true;
        }

        public int get(String path) {
            Node node = findNode(path);
            return node == null ? -1 : node.value;
        }

        private Node findNode(String path) {
            if (path == null || path.length() == 0 || path.equals("/")) {
                return null;
            }

            String[] parts = path.split("/");
            Node current = root;

            for (int i = 1; i < parts.length; i++) {
                current = current.children.get(parts[i]);
                if (current == null) {
                    return null;
                }
            }

            return current;
        }
    }

    private static class Node {
        private int value;
        private Map<String, Node> children = new HashMap<>();

        private Node(int value) {
            this.value = value;
        }
    }
}

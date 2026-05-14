public class NumberOfProvinces {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Input isConnected matrix where 1 means two cities are connected.
     * Output number of provinces, meaning connected city groups.
     *
     * Sample Input:
     * isConnected = [[1,1,0],[1,1,0],[0,0,1]]
     *
     * Sample Output:
     * 2
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * This is connected components again, but graph is given as a matrix.
     * A row tells which cities are directly connected to the current city.
     * Start DFS from each unvisited city; every city reached in that DFS is one province.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     * Treat each city row in the matrix like a list of direct friends.
     * Start from an unvisited city and visit all cities connected to it, directly or indirectly.
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * City 0 connected to city 1.
     * DFS from 0 visits 0 and 1.
     * City 2 unvisited later starts a second province.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Keep visited cities.
     * 2. For each city, if unvisited, count one province.
     * 3. DFS through matrix connections.
     * Time Complexity: O(n^2)
     * Space Complexity: O(n)
     */

    /* BRUTE FORCE IMPLEMENTATION */
    public int bruteForce(int[][] isConnected) {
        boolean[] visited = new boolean[isConnected.length];
        int provinces = 0;

        for (int city = 0; city < isConnected.length; city++) {
            if (!visited[city]) {
                provinces++;
                dfs(city, isConnected, visited);
            }
        }

        return provinces;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * DFS is already optimal for matrix input because reading the matrix costs O(n^2).
     * Union Find is an alternate optimized pattern: each connected pair joins two city groups,
     * and the number of remaining group leaders is the province count.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * If matrix[i][j] is 1, union i and j.
     * After all unions, number of roots is number of provinces.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Start each city as its own group.
     * 2. Union connected city pairs.
     * 3. Count unique roots.
     * Time Complexity: O(n^2)
     * Space Complexity: O(n)
     */

    /* OPTIMIZED IMPLEMENTATION */
    public int optimized(int[][] isConnected) {
        int n = isConnected.length;
        SimpleUnionFind uf = new SimpleUnionFind(n);

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (isConnected[i][j] == 1) {
                    uf.union(i, j);
                }
            }
        }

        int provinces = 0;

        for (int city = 0; city < n; city++) {
            if (uf.find(city) == city) {
                provinces++;
            }
        }

        return provinces;
    }


    private void dfs(int city, int[][] isConnected, boolean[] visited) {
        visited[city] = true; // This city has been assigned to the current province.

        for (int next = 0; next < isConnected.length; next++) {
            if (isConnected[city][next] == 1 && !visited[next]) {
                dfs(next, isConnected, visited);
            }
        }
    }

    private static class SimpleUnionFind {
        int[] parent;

        SimpleUnionFind(int n) {
            parent = new int[n];

            for (int i = 0; i < n; i++) {
                parent[i] = i;
            }
        }

        int find(int node) {
            if (parent[node] != node) {
                parent[node] = find(parent[node]); // Path compression: jump closer to the province leader.
            }

            return parent[node];
        }

        void union(int a, int b) {
            parent[find(b)] = find(a); // Put both cities in the same province group.
        }
    }
}

package com.companywisejavasolutions.karat.lldjavasolutions;

import java.util.HashMap;
import java.util.Map;

public class CrosswordHintsClientVsServer {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Crossword hints can live inside the app or on a server. Design a hint
     * service that supports both, while keeping the game code independent.
     *
     * Sample Flow:
     * app asks for hint puzzle-1 / clue-7
     * cache checks local hints first
     * server provider can refresh the hint if needed
     *
     * Sample Input:
     * puzzleId="p1", clueId="7", local hint exists, server hint may override it
     *
     * Sample Output:
     * The hint service returns the server hint when available, otherwise local fallback.
     *
     * What is the problem really asking?
     * The game needs hints, but product may want offline support and also server-side updates.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * The game should not care where hints come from. Use a HintProvider
     * interface, then plug in client, server, or cached providers.
     */

    /*
     * BASELINE DESIGN
     *
     * Hardcode hints in the app. This works offline but needs an app release
     * for every hint fix.
     */

    /*
     * STRONGER DESIGN
     *
     * Use a provider abstraction with local cache. Server hints allow updates,
     * experiments, and abuse controls. Cache keeps reads fast and offline.
     */

    /*
     * APPROACH AND WHY
     *
     * Approach:
     * Use a HintProvider interface. Implement client provider, server provider, and cached service that tries cache/server/local in readable order.
     *
     * Why this approach works:
     * The game code depends on an interface, so changing where hints live does not rewrite game logic.
     */
    public interface HintProvider {
        String getHint(String puzzleId, String clueId);
    }

    public static class ClientHintProvider implements HintProvider {
        private final Map<String, String> hints = new HashMap<>();

        public void addHint(String puzzleId, String clueId, String hint) {
            // Convert puzzle + clue into one stable key for map storage.
            String hintKey = key(puzzleId, clueId);

            // Store the local/offline hint under that key.
            hints.put(hintKey, hint);
        }

        public String getHint(String puzzleId, String clueId) {
            // Use the same key formula so reads match writes.
            String hintKey = key(puzzleId, clueId);

            // Missing local hint returns null; caller can then try another provider.
            return hints.get(hintKey);
        }
    }

    public static class ServerHintProvider implements HintProvider {
        private final Map<String, String> serverHints;

        public ServerHintProvider(Map<String, String> serverHints) {
            this.serverHints = serverHints;
        }

        public String getHint(String puzzleId, String clueId) {
            // Server storage uses the same key shape as the client provider.
            String hintKey = key(puzzleId, clueId);

            // Missing server hint returns null so a caller can fall back to local.
            return serverHints.get(hintKey);
        }
    }

    public static class CachedHintService implements HintProvider {
        private final HintProvider localProvider;
        private final HintProvider remoteProvider;
        private final Map<String, String> cache = new HashMap<>();

        public CachedHintService(HintProvider localProvider, HintProvider remoteProvider) {
            this.localProvider = localProvider;
            this.remoteProvider = remoteProvider;
        }

        public String getHint(String puzzleId, String clueId) {
            // Build one cache key so cache, server, and local provider all speak the same lookup language.
            String cacheKey = key(puzzleId, clueId);

            // Fast path: if we already fetched this hint before, return it immediately.
            if (cache.containsKey(cacheKey)) {
                return cache.get(cacheKey);
            }

            // Prefer the server because server hints can be corrected without an app release.
            String hint = remoteProvider.getHint(puzzleId, clueId);

            // If server does not have it, fall back to the offline local hint.
            if (hint == null) {
                hint = localProvider.getHint(puzzleId, clueId);
            }

            // Cache non-null results so the next read is fast.
            if (hint != null) {
                cache.put(cacheKey, hint);
            }

            // Return server hint, local hint, or null if nobody has it.
            return hint;
        }
    }

    private static String key(String puzzleId, String clueId) {
        // Combine both ids because clue "7" can exist in many puzzles.
        return puzzleId + ":" + clueId;
    }

    public static void main(String[] args) {
        ClientHintProvider local = new ClientHintProvider();
        local.addHint("p1", "7", "Local: four-letter river");
        local.addHint("p2", "3", "Local: common campus building");

        Map<String, String> serverData = new HashMap<>();
        serverData.put(key("p1", "7"), "Server: river in Egypt");
        serverData.put(key("p3", "9"), "Server: starts with Q");

        ServerHintProvider server = new ServerHintProvider(serverData);
        CachedHintService hints = new CachedHintService(local, server);

        runSample(hints, "Sample 1 - server overrides local", "p1", "7");
        runSample(hints, "Sample 2 - local fallback", "p2", "3");
        runSample(hints, "Sample 3 - missing hint", "p9", "1");
    }

    private static void runSample(HintProvider provider, String label, String puzzleId, String clueId) {
        System.out.println(label);
        System.out.println("request: puzzle=" + puzzleId + ", clue=" + clueId);
        System.out.println("hint: " + provider.getHint(puzzleId, clueId));
        System.out.println();
    }
}

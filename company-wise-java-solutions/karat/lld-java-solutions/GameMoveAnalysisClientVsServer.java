import java.util.ArrayDeque;
import java.util.Queue;

public class GameMoveAnalysisClientVsServer {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * A game analyzes moves. Decide whether to run analysis on phone, server,
     * or both.
     *
     * Sample Input:
     * moveCount=200, needsTrustedResult=true
     *
     * Sample Output:
     * planner chooses SERVER.
     *
     * What is the problem really asking?
     * Expensive game analysis may be slow on phones and unsafe if users can tamper with it.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Phones are good for quick hints. Servers are better for expensive,
     * trusted, consistent analysis.
     */

    /*
     * BASELINE DESIGN
     *
     * Run all analysis on the phone. Offline works, but battery, latency, and
     * cheating become concerns.
     */

    /*
     * STRONGER DESIGN
     *
     * Use hybrid mode: quick local checks on phone, deep analysis on server job
     * queue, and cache repeated positions.
     */

    /*
     * APPROACH AND WHY
     *
     * Approach:
     * Use AnalysisPlanner to choose CLIENT for small local hints, SERVER for trusted deep analysis, HYBRID for larger non-sensitive work.
     *
     * Why this approach works:
     * This balances latency, battery, server cost, and trust.
     */
    public enum ExecutionPlace {
        CLIENT, SERVER, HYBRID
    }

    public static class AnalysisRequest {
        private final String gameId;
        private final int moveCount;
        private final boolean needsTrustedResult;

        public AnalysisRequest(String gameId, int moveCount, boolean needsTrustedResult) {
            // gameId lets logs/jobs point back to the game being analyzed.
            this.gameId = gameId;

            // moveCount is a simple proxy for how expensive the analysis may be.
            this.moveCount = moveCount;

            // Trusted results should be computed server-side because clients can be tampered with.
            this.needsTrustedResult = needsTrustedResult;
        }
    }

    public static class AnalysisPlanner {
        public ExecutionPlace choose(AnalysisRequest request) {
            // Trust beats speed. If correctness/security matters, use the server.
            if (request.needsTrustedResult) {
                return ExecutionPlace.SERVER;
            }

            // Small analysis can happen on device for low latency and offline support.
            if (request.moveCount <= 20) {
                return ExecutionPlace.CLIENT;
            }

            // Larger non-sensitive analysis can mix local quick feedback with server deep analysis.
            return ExecutionPlace.HYBRID;
        }
    }

    public static class ServerAnalysisQueue {
        private final Queue<AnalysisRequest> queue = new ArrayDeque<>();

        public void submit(AnalysisRequest request) {
            // Queue server jobs so workers can process them later.
            queue.offer(request);
        }

        public AnalysisRequest nextJob() {
            // poll() gives the oldest queued job or null when no server work exists.
            return queue.poll();
        }
    }

    public static void main(String[] args) {
        AnalysisPlanner planner = new AnalysisPlanner();
        ServerAnalysisQueue serverQueue = new ServerAnalysisQueue();

        runSample(planner, serverQueue, new AnalysisRequest("game-small", 8, false));
        runSample(planner, serverQueue, new AnalysisRequest("game-ranked", 12, true));
        runSample(planner, serverQueue, new AnalysisRequest("game-large", 200, false));

        AnalysisRequest nextJob = serverQueue.nextJob();
        System.out.println("First queued server job: " + (nextJob == null ? "none" : nextJob.gameId));
    }

    private static void runSample(AnalysisPlanner planner, ServerAnalysisQueue serverQueue, AnalysisRequest request) {
        ExecutionPlace place = planner.choose(request);
        System.out.println("gameId: " + request.gameId);
        System.out.println("moveCount: " + request.moveCount);
        System.out.println("needsTrustedResult: " + request.needsTrustedResult);
        System.out.println("chosen place: " + place);
        if (place == ExecutionPlace.SERVER || place == ExecutionPlace.HYBRID) {
            serverQueue.submit(request);
            System.out.println("queued for server: yes");
        }
        System.out.println();
    }
}

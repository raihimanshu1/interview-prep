package com.companywisejavasolutions.karat.lldjavasolutions;

public class ServerCapacityEstimation {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Given storage size and query load, estimate how many servers are needed.
     *
     * Sample Input:
     * dataTb = 30, replicationFactor = 3, usableTbPerServer = 10
     * peakQps = 6000, qpsPerServer = 1000
     *
     * Sample Output:
     * storage needs 9 servers, compute needs 6 servers, choose 12 with headroom.
     *
     * What is the problem really asking?
     * Server count should be based on the largest bottleneck, not guessed.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Capacity is usually constrained by the biggest bottleneck. Calculate
     * storage need and compute need separately, then choose the larger number.
     */

    /*
     * BASELINE DESIGN
     *
     * Guess server count. This is fast but weak because it hides assumptions.
     */

    /*
     * STRONGER DESIGN
     *
     * Compute storage servers, compute servers, choose the max, then add
     * headroom for spikes, failover, and growth.
     */

    /*
     * APPROACH AND WHY
     *
     * Approach:
     * Calculate storage servers and compute servers separately, choose the larger, then add headroom.
     *
     * Why this approach works:
     * This makes assumptions explicit and avoids under-provisioning one dimension.
     */
    public static class CapacityPlan {
        private final int storageServers;
        private final int computeServers;
        private final int recommendedServers;

        public CapacityPlan(int storageServers, int computeServers, int recommendedServers) {
            // Storage server count answers: how many boxes hold replicated data?
            this.storageServers = storageServers;

            // Compute server count answers: how many boxes handle peak QPS?
            this.computeServers = computeServers;

            // Recommended count is the final answer after choosing the bottleneck and adding headroom.
            this.recommendedServers = recommendedServers;
        }

        public int getStorageServers() {
            return storageServers;
        }

        public int getComputeServers() {
            return computeServers;
        }

        public int getRecommendedServers() {
            return recommendedServers;
        }
    }

    public CapacityPlan estimate(double dataTb, int replicationFactor, double usableTbPerServer,
            int peakQps, int qpsPerServer, double headroomRatio) {
        // Storage requirement includes replicated copies, not only raw data.
        double replicatedDataTb = dataTb * replicationFactor;

        // Divide by usable capacity per server to know how many storage servers are needed.
        int storageServers = ceil(replicatedDataTb / usableTbPerServer);

        // Compute requirement is based on peak traffic, not average traffic.
        int computeServers = ceil((double) peakQps / qpsPerServer);

        // The final fleet must satisfy both storage and compute, so choose the larger need.
        int base = Math.max(storageServers, computeServers);

        // Headroom protects against spikes, failover, and near-future growth.
        int recommended = ceil(base * (1.0 + headroomRatio));

        // Return all numbers so the interviewer can see the reasoning.
        return new CapacityPlan(storageServers, computeServers, recommended);
    }

    private int ceil(double value) {
        // Server counts must be whole numbers, and partial need rounds up.
        return (int) Math.ceil(value);
    }

    public static void main(String[] args) {
        ServerCapacityEstimation estimator = new ServerCapacityEstimation();

        runSample(estimator, "Sample 1 - balanced", 30, 3, 10, 6000, 1000, 0.25);
        runSample(estimator, "Sample 2 - compute heavy", 5, 3, 10, 25000, 2000, 0.20);
        runSample(estimator, "Sample 3 - storage heavy", 120, 3, 12, 4000, 1000, 0.30);
    }

    private static void runSample(ServerCapacityEstimation estimator, String label,
            double dataTb, int replicationFactor, double usableTbPerServer,
            int peakQps, int qpsPerServer, double headroomRatio) {
        CapacityPlan plan = estimator.estimate(dataTb, replicationFactor, usableTbPerServer,
                peakQps, qpsPerServer, headroomRatio);
        System.out.println(label);
        System.out.println("storageServers: " + plan.getStorageServers());
        System.out.println("computeServers: " + plan.getComputeServers());
        System.out.println("recommendedServers: " + plan.getRecommendedServers());
        System.out.println();
    }
}


package com.companywisejavasolutions.karat.lldJavaSolutions;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

public class PhotoLinkPartitionByFirstLetter {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * A photo app partitions data by the first letter of username. Explain the
     * problem and implement a better partition strategy.
     *
     * Sample Input:
     * username="alice", userId="user-931", partitionCount=16
     *
     * Sample Output:
     * first-letter partition may overload bucket A; hash partition spreads userId across 16 buckets.
     *
     * What is the problem really asking?
     * First-letter partitioning creates hot partitions and breaks when usernames change.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * First letters are not evenly distributed and usernames can change. Stable
     * hashed user IDs spread data more evenly.
     */

    /*
     * BASELINE DESIGN
     *
     * Partition "alice" into bucket A. Easy to debug, but A/S/M may become hot.
     */

    /*
     * STRONGER DESIGN
     *
     * Partition by stable user ID hash. Username stays editable metadata.
     */

    /*
     * APPROACH AND WHY
     *
     * Approach:
     * Show bad FirstLetterPartitioner and better HashPartitioner using stable userId.
     *
     * Why this approach works:
     * Stable hash partitioning distributes load more evenly and does not depend on editable usernames.
     */
    public interface Partitioner {
        int partition(String userIdOrName, int partitionCount);
    }

    public static class FirstLetterPartitioner implements Partitioner {
        public int partition(String username, int partitionCount) {
            // Empty usernames need a safe fallback bucket.
            if (username == null || username.isEmpty()) {
                return 0;
            }

            // Lowercase keeps "Alice" and "alice" in the same first-letter bucket.
            char firstLetter = Character.toLowerCase(username.charAt(0));

            // Convert 'a' to 0, 'b' to 1, and so on.
            int alphabetIndex = Math.abs(firstLetter - 'a');

            // Mod keeps the bucket inside 0..partitionCount-1.
            return alphabetIndex % partitionCount;
        }
    }

    public static class HashPartitioner implements Partitioner {
        public int partition(String userId, int partitionCount) {
            // CRC32 gives a stable numeric hash for the same user id.
            CRC32 crc32 = new CRC32();

            // Convert the user id to bytes before feeding it into the hash.
            crc32.update(userId.getBytes(StandardCharsets.UTF_8));

            // Mod maps a large hash number into the available partition range.
            return (int) (crc32.getValue() % partitionCount);
        }
    }

    public static void main(String[] args) {
        FirstLetterPartitioner firstLetter = new FirstLetterPartitioner();
        HashPartitioner hash = new HashPartitioner();

        runSample(firstLetter, hash, "Sample 1 - alice", "alice", "user-931", 16);
        runSample(firstLetter, hash, "Sample 2 - sam", "sam", "user-101", 16);
        runSample(firstLetter, hash, "Sample 3 - empty username", "", "user-202", 16);
    }

    private static void runSample(Partitioner firstLetter, Partitioner hash,
            String label, String username, String userId, int partitionCount) {
        System.out.println(label);
        System.out.println("username: " + username);
        System.out.println("userId: " + userId);
        System.out.println("first-letter partition: " + firstLetter.partition(username, partitionCount));
        System.out.println("hash partition: " + hash.partition(userId, partitionCount));
        System.out.println();
    }
}

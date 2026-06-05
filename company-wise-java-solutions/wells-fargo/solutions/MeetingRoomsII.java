package wellsfargo.solutions;

import java.util.Arrays;
import java.util.PriorityQueue;

public class MeetingRoomsII {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: intervals = [[0,30],[5,10],[15,20]]
     * Sample Output: 2
     *
     * Input meetings = [[0,30],[5,10],[15,20]]
     * Output: 2 minimum rooms needed.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * If a meeting starts before another meeting ends, we need another room.
     * We need to track which room becomes free the earliest.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * The natural first attempt is to process meetings in time order and try to
     * fit each meeting into one of the rooms we have already opened.
     */

    /*
     * APPROACH 1: BRUTE FORCE DRY RUN
     *
     * Meeting [0,30] uses room 1.
     * Meeting [5,10] starts before 30, needs room 2.
     * Meeting [15,20] can reuse room 2 because 10 <= 15.
     */

    /*
     * APPROACH 1: BRUTE FORCE ALGORITHM
     *
     * 1. For every meeting, try to place it into an existing room.
     * 2. If no room is free, create a new room.
     * Time Complexity: O(n^2)
     * Space Complexity: O(n)
     */

    /*
     * APPROACH 1: BRUTE FORCE IMPLEMENTATION
     *
     * roomEndTimes stores when each currently opened room becomes free.
     */
    public int bruteForce(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        java.util.List<Integer> roomEndTimes = new java.util.ArrayList<>();

        for (int[] meeting : intervals) {
            boolean placed = false;

            for (int i = 0; i < roomEndTimes.size(); i++) {
                if (roomEndTimes.get(i) <= meeting[0]) {
                    roomEndTimes.set(i, meeting[1]);
                    placed = true;
                    break;
                }
            }

            if (!placed) {
                roomEndTimes.add(meeting[1]);
            }
        }

        return roomEndTimes.size();
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * A min-heap stores end times of rooms.
     * The smallest end time is the room that frees first.
     * If it is <= current start, reuse that room.
     */

    /*
     * APPROACH 2: OPTIMIZED DRY RUN
     *
     * Heap [30]
     * Meeting [5,10], earliest 30 > 5, add room -> [10,30]
     * Meeting [15,20], earliest 10 <= 15, reuse -> [20,30]
     */

    /*
     * APPROACH 2: OPTIMIZED ALGORITHM
     *
     * 1. Sort meetings by start.
     * 2. Keep min-heap of room end times.
     * 3. If earliest end <= start, remove it.
     * 4. Add current end.
     * 5. Heap size is rooms needed.
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */

    /*
     * APPROACH 2: OPTIMIZED IMPLEMENTATION
     *
     * The heap replaces the room scan by always exposing the earliest free room.
     */
    public int optimized(int[][] intervals) {
        // Meetings must be considered in the order they request rooms.
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        PriorityQueue<Integer> minEndTime = new PriorityQueue<>();

        for (int[] meeting : intervals) {
            if (!minEndTime.isEmpty() && minEndTime.peek() <= meeting[0]) {
                // The earliest-ending room is free, so reuse it for this meeting.
                minEndTime.poll();
            }

            // Add this meeting's end time as an occupied room.
            minEndTime.offer(meeting[1]);
        }

        return minEndTime.size();
    }
}

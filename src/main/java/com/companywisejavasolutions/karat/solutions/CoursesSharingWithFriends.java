package com.companywisejavasolutions.karat.solutions;

import java.util.*;

public class CoursesSharingWithFriends {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given enrollments and friendships, recommend courses taken by direct friends that the target user has not taken.
     *
     * INPUT
     * targetUser, enrollments [user, course], friendships [userA, userB].
     *
     * OUTPUT
     * A list of recommended courses.
     *
     * EXAMPLE
     * targetUser = "1"
     * enrollments = [["1","Math"], ["2","CS"], ["2","Math"], ["3","History"], ["4","Art"]]
     * friendships = [["1","2"], ["1","3"], ["2","4"]]
     * Output: [CS, History]
     * 
     * Only direct friends 2 and 3 matter. Math is removed because user 1 already took it.
     *
     * WHAT IT MEANS
     * Collect direct friends, collect their courses, then subtract target user courses.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * 
     * Think of friends as edges and courses as labels on people. Recommendations
     * come from friends' course lists after removing courses the target already
     * has.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * The problem combines two separate pieces of information:
     *
     * 1. Friendship rows tell us who is directly connected to the target user.
     * 2. Enrollment rows tell us which courses each user has taken.
     *
     * Recommendations should come only from direct friends. Friends of friends do
     * not count. Once we know the direct friends, we collect their courses and
     * remove anything the target already took.
     *
     * The brute force version keeps the thinking very literal:
     * scan friendships to find friends, scan enrollments to find target courses,
     * then scan enrollments again for each friend.
     */

    /*
     * EXAMPLES TO UNDERSTAND THE PROBLEM
     *
     * Example 1 - Basic direct friends
     * targetUser = "1"
     * enrollments = [["1","Math"], ["2","CS"], ["2","Math"], ["3","History"]]
     * friendships = [["1","2"], ["1","3"]]
     * Output: [CS, History]
     * Math is removed because user 1 already took it.
     *
     * Example 2 - Friendship can appear in either order
     * targetUser = "1"
     * enrollments = [["1","Math"], ["4","Art"]]
     * friendships = [["4","1"]]
     * Output: [Art]
     * The pair still means 1 and 4 are direct friends.
     *
     * Example 3 - Friend only has courses target already took
     * targetUser = "1"
     * enrollments = [["1","Math"], ["2","Math"]]
     * friendships = [["1","2"]]
     * Output: []
     *
     * Edge Case 1 - Target has no friends
     * No direct friends means there is nobody to recommend courses from, so the result is empty.
     *
     * Edge Case 2 - Target has no courses
     * Then every course taken by direct friends can be recommended.
     *
     * Edge Case 3 - Multiple friends share the same course
     * The HashSet keeps the course once in the final answer.
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * - Friendships are undirected: ["1","2"] and ["2","1"] both connect users 1 and 2.
     * - Only direct friends count.
     * - The target user's own courses must be filtered out.
     * - Duplicate recommendations should appear only once.
     * - Brute force can rescan enrollments because we are prioritizing clear logic.
     */

    /*
     * WHAT WE DO TO SOLVE IT
     *
     * First gather all direct friends of the target. Next gather all courses the
     * target already took. Then, for each direct friend, walk through enrollments
     * and add the friend's courses only when the target does not already have them.
     */


    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     *
     * 1. What do I notice first?
     *    This is a relationship question: friends are edges and courses are labels attached to people.
     *
     * 2. What data structure does that naturally suggest?
     *    Use maps/sets because we need fast course membership checks for each friend pair.
     *
     * 3. How do I build the brute force like a human?
     *    Brute force: find the target's direct friends, then scan enrollments to collect their untaken courses.
     *
     * 4. What repeated work should I remove?
     *    Optimized: build course sets and friend sets first, then subtract target courses directly.
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     *
     * Start with the most literal reading of the question. Do not try to be clever yet.
     * Brute force: find direct friends, then scan enrollments to find friend courses the target has not taken.
     *
     * This version is useful in an interview because it proves we understand what
     * the question is asking before we introduce extra data structures.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 
     * 1. Create a set for direct friends.
     * 2. Scan every friendship:
     *    if target is on the left, add the right user.
     *    if target is on the right, add the left user.
     * 3. Create a set of courses already taken by the target.
     * 4. For each direct friend, scan all enrollments.
     * 5. If an enrollment belongs to that friend and target has not taken the
     *    course, add it to recommendations.
     * 6. Return the unique recommendation list.
     * 
     * Time Complexity: O(friendships + enrollments + friends * enrollments).
     * Space Complexity: O(friends + targetCourses + recommendations).
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Use the example above with target user 1.
     * Direct friends are 2 and 3.
     * Friend 2 has CS and Math; Math is removed because user 1 already took it.
     * Friend 3 has History.
     * Final answer: [CS, History]
     */
    public List<String> bruteForce(String targetUser, String[][] enrollments, String[][] friendships) {

        Set<String> directFriends = new HashSet<>();

        for (String[] friendship : friendships) {
            if (friendship[0].equals(targetUser)) {
                // Friendship is undirected, so the user on the other side is a direct friend.
                directFriends.add(friendship[1]);
            }
            if (friendship[1].equals(targetUser)) {
                // Also handle rows written in the reverse order.
                directFriends.add(friendship[0]);
            }
        }

        Set<String> targetCourses = new HashSet<>();
        for (String[] enrollment : enrollments) {
            if (enrollment[0].equals(targetUser)) {
                // These courses must be filtered out of the recommendation list.
                targetCourses.add(enrollment[1]);
            }
        }

        Set<String> recommendations = new HashSet<>();
        for (String friend : directFriends) {
            for (String[] enrollment : enrollments) {
                // Brute force: for this one friend, scan every enrollment row
                // instead of using a prebuilt user -> courses map.
                if (enrollment[0].equals(friend) && !targetCourses.contains(enrollment[1])) {
                    recommendations.add(enrollment[1]);
                }
            }
        }

        return new ArrayList<>(recommendations);
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     *
     * Now look at the brute force and ask: what am I recomputing again and again?
     * Optimized: build course sets and friend sets first, then subtract target courses directly.
     *
     * The optimized approach keeps the same answer logic, but stores the right
     * intermediate state so each lookup or decision is cheaper.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 
     * 1. Keep the same goal: find target user friends and course recommendations.
     * 2. Remove repeated work: build friend set and user-course sets once, then subtract target courses.
     * 3. Use the stored state for faster lookups or traversal decisions.
     * 4. Return the same output as brute force.
     * 
     * Time Complexity: Lower than brute force because the stored state avoids repeated direct checks.
     * Space Complexity: O(n) for the optimized helper structure.
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Use the example above with target user 1.
     * Direct friends are 2 and 3.
     * Friend 2 has CS and Math; Math is removed because user 1 already took it.
     * Friend 3 has History.
     * Final answer: [CS, History]
     */
    public List<String> optimized(String targetUser, String[][] enrollments, String[][] friendships) {
        Map<String, Set<String>> coursesByUser = new HashMap<>();
        Map<String, Set<String>> friendsByUser = new HashMap<>();
        for (String[] enrollment : enrollments) {
            String user = enrollment[0];
            String course = enrollment[1];

            // Create this user's course set the first time we see the user.
            coursesByUser.putIfAbsent(user, new HashSet<>());

            // Add the course to that user's own course list.
            coursesByUser.get(user).add(course);
        }
        for (String[] friendship : friendships) {
            String firstUser = friendship[0];
            String secondUser = friendship[1];

            // Friendships are two-way, so both users need a friend set.
            friendsByUser.putIfAbsent(firstUser, new HashSet<>());
            friendsByUser.putIfAbsent(secondUser, new HashSet<>());

            // Store secondUser as a friend of firstUser.
            friendsByUser.get(firstUser).add(secondUser);

            // Store firstUser as a friend of secondUser.
            friendsByUser.get(secondUser).add(firstUser);
        }

        // HashSet lets us ask "have we seen this before?" in constant average time.
        Set<String> recommendations = new HashSet<>();
        for (String friend : friendsByUser.getOrDefault(targetUser, new HashSet<>())) {
            recommendations.addAll(coursesByUser.getOrDefault(friend, new HashSet<>()));
        }
        recommendations.removeAll(coursesByUser.getOrDefault(targetUser, new HashSet<>()));
        return new ArrayList<>(recommendations);
    }

    public static void main(String[] args) {
        CoursesSharingWithFriends solution = new CoursesSharingWithFriends();

        String[] targetUsers = {"1", "1", "1"};

        String[][][] sampleEnrollments = {
                {
                        {"1", "Math"},
                        {"2", "CS"},
                        {"2", "Math"},
                        {"3", "History"},
                        {"4", "Art"}
                },
                {
                        {"1", "Math"},
                        {"4", "Art"}
                },
                {
                        {"1", "Math"},
                        {"2", "Math"}
                }
        };

        String[][][] sampleFriendships = {
                {
                        {"1", "2"},
                        {"1", "3"},
                        {"2", "4"}
                },
                {
                        {"4", "1"}
                },
                {
                        {"1", "2"}
                }
        };

        for (int i = 0; i < targetUsers.length; i++) {
            System.out.println("Sample " + (i + 1));
            System.out.println("bruteForce: " + formatList(solution.bruteForce(
                    targetUsers[i], sampleEnrollments[i], sampleFriendships[i])));
            System.out.println("optimized:  " + formatList(solution.optimized(
                    targetUsers[i], sampleEnrollments[i], sampleFriendships[i])));
            System.out.println();
        }
    }

    private static String formatList(List<String> values) {
        List<String> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        return sorted.toString();
    }
}

package com.companywisejavasolutions.karat.solutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class SharedCourses {

    /*
     * ORIGINAL PROBLEM STATEMENT
     *
     * Given a list of student-course pairs, return every pair of students and
     * the courses they both took.
     *
     * EXAMPLE
     * enrollments = [["1","Math"], ["1","Physics"], ["2","Math"],
     *                ["2","CS"], ["3","CS"], ["3","Math"]]
     *
     * Output:
     * {"1,2" = ["Math"], "1,3" = ["Math"], "2,3" = ["Math", "CS"]}
     *
     * What It Means:
     * The output has every student pair, not just one pair.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * Imagine each student has a notebook listing their courses.
     * To find shared classes, take two notebooks and highlight course names
     * that appear in both.
     *
     * The brute force way keeps checking the original enrollment sheet again
     * and again. The optimized way first creates each student's notebook as a
     * set, then compares notebooks directly.
     */

    /*
     * HUMAN THOUGHT PROCESS BEFORE CODING
     *
     * 1. What is one record?
     *    One row says: this student took this course.
     *
     * 2. What is the answer asking?
     *    For every pair of students, list the courses present for both.
     *
     * 3. What does brute force do?
     *    Pick two students, then scan the raw enrollment list to find courses
     *    for both of them.
     *
     * 4. What should be optimized?
     *    Stop rescanning raw enrollments. Build student -> set of courses once.
     */

    /*
     * IN-DEPTH EXPLANATION
     *
     * The input is a signup sheet. Each row says one student took one course.
     *
     * The answer is not student -> courses. The answer is pair of students ->
     * courses both students share.
     *
     * That means we need to think in two layers:
     * 1. Who are all the students?
     * 2. For each possible pair, which course names appear for both?
     *
     * Brute force keeps the raw signup sheet as the source of truth. For every
     * student pair, it rereads that sheet to rebuild each student's course list,
     * then compares those two lists.
     */

    /*
     * EXAMPLES
     *
     * Example 1 - Three students with overlap
     * enrollments = [["1","Math"], ["1","Physics"], ["2","Math"],
     *                ["2","CS"], ["3","CS"], ["3","Math"]]
     * Student 1 and 2 share Math.
     * Student 1 and 3 share Math.
     * Student 2 and 3 share Math and CS.
     * Output: {"1,2"=["Math"], "1,3"=["Math"], "2,3"=["Math","CS"]}
     *
     * Example 2 - Pair with no shared courses
     * enrollments = [["1","Math"], ["2","History"]]
     * The pair exists, but their shared list is empty.
     * Output: {"1,2"=[]}
     *
     * Example 3 - More than one pair
     * enrollments = [["A","CS"], ["A","Art"], ["B","CS"], ["C","Art"]]
     * A,B share CS. A,C share Art. B,C share nothing.
     * Output contains keys "A,B", "A,C", and "B,C".
     *
     * Edge Case 1 - Only one student
     * enrollments = [["1","Math"], ["1","Physics"]]
     * There is no student pair to compare.
     * Output: {}
     *
     * Edge Case 2 - Empty enrollments
     * enrollments = []
     * There are no students and no pairs.
     * Output: {}
     */

    /*
     * WHAT TO KNOW BEFORE SOLVING
     *
     * 1. A pair means two different students.
     * 2. Each pair should appear once, so we use j = i + 1 instead of comparing
     *    both "1,2" and "2,1".
     * 3. The output value can be an empty list when two students share nothing.
     * 4. In brute force, List.contains is a linear search, which is acceptable
     *    for demonstrating the idea but not ideal for large input.
     */

    /*
     * WHAT WE DO TO SOLVE
     *
     * First, collect every unique student id. Then turn that set into a list so
     * we can pick pairs by index.
     *
     * For each pair:
     * collect courses for the first student,
     * collect courses for the second student,
     * keep any course from the first list that appears in the second list.
     *
     * Finally, store the answer under a key like "1,2".
     */

    /*
     * APPROACH 1: BRUTE FORCE INTUITION
     *
     * First collect unique student ids. Then for every pair, scan the raw input
     * to collect the first student's courses and the second student's courses.
     * Intersect those two lists.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. Collect all unique students.
     * 2. For every pair of students:
     *    a. Scan enrollments to collect courses of student A.
     *    b. Scan enrollments to collect courses of student B.
     *    c. Compare the two course lists and keep common courses.
     * 3. Store the pair key as "studentA,studentB".
     *
     * Time Complexity: O(s^2 * e), where s is students and e is enrollments.
     * Space Complexity: O(e)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * Use the multi-student example above.
     * Pair 1,2 shares Math.
     * Pair 1,3 shares Math.
     * Pair 2,3 shares Math and CS.
     * Final answer contains all three student pairs.
     */
    public Map<String, List<String>> bruteForce(String[][] enrollments) {
        // A set removes duplicate student ids while we scan the signup sheet.
        Set<String> uniqueStudents = new HashSet<>();
        for (String[] enrollment : enrollments) {
            // enrollment[0] is the student id.
            uniqueStudents.add(enrollment[0]);
        }

        // Convert to a list so we can generate each student pair by index.
        List<String> students = new ArrayList<>(uniqueStudents);
        // TreeMap keeps pair keys ordered for stable output.
        Map<String, List<String>> result = new TreeMap<>();

        // Pick the first student in the pair.
        for (int i = 0; i < students.size(); i++) {
            // Pick only students after i so every pair is created once.
            for (int j = i + 1; j < students.size(); j++) {
                String first = students.get(i);
                String second = students.get(j);

                // Brute force rereads the raw enrollments for each student.
                List<String> firstCourses = collectCourses(enrollments, first);
                List<String> secondCourses = collectCourses(enrollments, second);
                List<String> shared = new ArrayList<>();

                // Compare the first student's courses against the second student's list.
                for (String course : firstCourses) {
                    // In brute force, contains() searches the second list linearly.
                    if (secondCourses.contains(course)) {
                        shared.add(course);
                    }
                }

                // Save the shared courses even when the list is empty.
                result.put(first + "," + second, shared);
            }
        }

        // All possible student pairs have been checked.
        return result;
    }

    /*
     * APPROACH 2: OPTIMIZED INTUITION
     *
     * The repeated work is collecting courses again for every pair.
     * Instead, build one map:
     * student -> set of courses
     *
     * Now checking whether another student took a course is a fast set lookup.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Build studentToCourses map.
     * 2. For each enrollment, add the course to that student's set.
     * 3. For every pair of students, iterate one set.
     * 4. If the other set contains the course, add it to shared list.
     *
     * Time Complexity: O(e + s^2 * c), where c is average courses per student.
     * Space Complexity: O(e)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * Use the multi-student example above.
     * Pair 1,2 shares Math.
     * Pair 1,3 shares Math.
     * Pair 2,3 shares Math and CS.
     * Final answer contains all three student pairs.
     */
    public Map<String, List<String>> optimized(String[][] enrollments) {
        Map<String, Set<String>> studentToCourses = new HashMap<>();

        for (String[] enrollment : enrollments) {
            String student = enrollment[0];
            String course = enrollment[1];

            // Create the student's course set the first time we see that student.
            studentToCourses.putIfAbsent(student, new HashSet<>());

            // Add this course to that student's set.
            studentToCourses.get(student).add(course);
        }

        List<String> students = new ArrayList<>(studentToCourses.keySet());
        Map<String, List<String>> result = new TreeMap<>();

        for (int i = 0; i < students.size(); i++) {
            for (int j = i + 1; j < students.size(); j++) {
                String first = students.get(i);
                String second = students.get(j);
                List<String> shared = new ArrayList<>();

                for (String course : studentToCourses.get(first)) {
                    // Set lookup is the optimization: no raw enrollment rescan.
                    if (studentToCourses.get(second).contains(course)) {
                        shared.add(course);
                    }
                }

                result.put(first + "," + second, shared);
            }
        }

        return result;
    }

    private List<String> collectCourses(String[][] enrollments, String student) {
        // This helper rebuilds one student's "notebook" of courses from the raw
        // signup sheet, which is exactly what the brute-force approach is meant
        // to demonstrate before we optimize with a map.
        List<String> courses = new ArrayList<>();

        // Scan every enrollment row because, in the raw input, a student's
        // courses may be scattered across the list rather than grouped together.
        for (String[] enrollment : enrollments) {
            // enrollment[0] is the student id, so this row belongs to the
            // student we are collecting only when the ids match exactly.
            if (enrollment[0].equals(student)) {
                // enrollment[1] is the course name; add it to this student's
                // course list so the caller can later compare two students.
                courses.add(enrollment[1]);
            }
        }

        // Return all courses found for this one student; an empty list means
        // the student had no matching rows in the enrollment sheet.
        return courses;
    }

    public static void main(String[] args) {
        SharedCourses solution = new SharedCourses();

        String[][][] samples = {
                {
                        {"1", "Math"},
                        {"1", "Physics"},
                        {"2", "Math"},
                        {"2", "CS"},
                        {"3", "CS"},
                        {"3", "Math"}
                },
                {
                        {"1", "Math"},
                        {"2", "History"}
                },
                {
                        {"A", "CS"},
                        {"A", "Art"},
                        {"B", "CS"},
                        {"C", "Art"}
                }
        };

        for (int i = 0; i < samples.length; i++) {
            System.out.println("Sample " + (i + 1));
            System.out.println("bruteForce: " + formatSharedCourses(solution.bruteForce(samples[i])));
            System.out.println("optimized:  " + formatSharedCourses(solution.optimized(samples[i])));
            System.out.println();
        }
    }

    private static String formatSharedCourses(Map<String, List<String>> map) {
        Map<String, List<String>> sorted = new TreeMap<>();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            List<String> courses = new ArrayList<>(entry.getValue());
            courses.sort(String::compareTo);
            sorted.put(entry.getKey(), courses);
        }
        return sorted.toString();
    }
}

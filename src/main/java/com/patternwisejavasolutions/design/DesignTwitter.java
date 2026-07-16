package com.patternwisejavasolutions.design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class DesignTwitter {

    /*
     * PROBLEM IN SIMPLE WORDS
     *
     * Sample Input: postTweet(1,5), getNewsFeed(1), follow(1,2),
     * postTweet(2,6), getNewsFeed(1)
     * Sample Output: [5], [6,5]
     *
     * Build a tiny Twitter. Users post tweets, follow/unfollow users, and see
     * the 10 most recent tweet ids from themselves and people they follow.
     */

    /*
     * SCHOOL-LEVEL INTUITION
     *
     * A news feed is a latest-first list made from a user's own tweets and all
     * tweets from followed users.
     * Users map to follow sets, and each user's tweets form a newest-first chain,
     * like a personal timeline that can be merged into the feed.
     */

    /*
     * BRUTE FORCE INTUITION
     *
     * Store every tweet in one global list. To build a feed, scan all tweets and
     * keep tweets written by the user or by followed users.
     */

    /*
     * BRUTE FORCE ALGORITHM
     *
     * 1. postTweet appends tweet with an increasing time.
     * 2. follow adds followee to follower's set.
     * 3. unfollow removes followee unless it is the same user.
     * 4. getNewsFeed scans tweets from newest to oldest and takes first 10 valid.
     *
     * Time Complexity: post/follow/unfollow O(1), feed O(total tweets)
     * Space Complexity: O(users + tweets)
     */

    /*
     * BRUTE FORCE DRY RUN
     *
     * User 1 posts 5, feed is [5].
     * User 1 follows 2. User 2 posts 6.
     * Scanning newest first gives 6 then 5, so feed is [6,5].
     */

    public static class BruteForce {
        private int time;
        private List<Tweet> allTweets = new ArrayList<>();
        private Map<Integer, Set<Integer>> following = new HashMap<>();

        public void postTweet(int userId, int tweetId) {
            allTweets.add(new Tweet(userId, tweetId, time++));
        }

        public List<Integer> getNewsFeed(int userId) {
            List<Integer> feed = new ArrayList<>();
            Set<Integer> allowedUsers = following.getOrDefault(userId, new HashSet<>());

            for (int i = allTweets.size() - 1; i >= 0 && feed.size() < 10; i--) {
                Tweet tweet = allTweets.get(i);
                if (tweet.userId == userId || allowedUsers.contains(tweet.userId)) {
                    feed.add(tweet.tweetId);
                }
            }

            return feed;
        }

        public void follow(int followerId, int followeeId) {
            if (followerId == followeeId) {
                return;
            }
            following.computeIfAbsent(followerId, key -> new HashSet<>()).add(followeeId);
        }

        public void unfollow(int followerId, int followeeId) {
            if (following.containsKey(followerId)) {
                following.get(followerId).remove(followeeId);
            }
        }
    }

    /*
     * OPTIMIZED INTUITION
     *
     * Store tweets per user. A feed only needs to merge the newest tweet streams
     * from the user and followed users, similar to merging sorted lists.
     */

    /*
     * OPTIMIZED ALGORITHM
     *
     * 1. Each user's tweets are stored newest-first as a linked chain.
     * 2. Add the head tweet of the user and each followee into a max heap.
     * 3. Pop newest tweet, add its id, then push the next tweet from same user.
     * 4. Stop after 10 tweets or when heap is empty.
     *
     * Time Complexity: post/follow/unfollow O(1), feed O(10 log f)
     * Space Complexity: O(users + tweets)
     */

    /*
     * OPTIMIZED DRY RUN
     *
     * User 1 tweet chain: 5. User 2 tweet chain: 6.
     * Feed heap starts with heads 6 and 5.
     * Pop 6, then pop 5, answer [6,5].
     */

    public static class Optimized {
        private int time;
        private Map<Integer, TweetNode> userToTweets = new HashMap<>();
        private Map<Integer, Set<Integer>> following = new HashMap<>();

        public void postTweet(int userId, int tweetId) {
            TweetNode oldHead = userToTweets.get(userId);
            // New tweet becomes the head of this user's timeline.
            userToTweets.put(userId, new TweetNode(userId, tweetId, time++, oldHead));
        }

        public List<Integer> getNewsFeed(int userId) {
            PriorityQueue<TweetNode> newestFirst = new PriorityQueue<>((a, b) -> b.time - a.time);
            addUserHead(userId, newestFirst);

            for (int followeeId : following.getOrDefault(userId, new HashSet<>())) {
                addUserHead(followeeId, newestFirst);
            }

            List<Integer> feed = new ArrayList<>();
            while (!newestFirst.isEmpty() && feed.size() < 10) {
                TweetNode tweet = newestFirst.poll();
                feed.add(tweet.tweetId);

                // After using one tweet from a user, expose that user's next tweet.
                if (tweet.next != null) {
                    newestFirst.offer(tweet.next);
                }
            }

            return feed;
        }

        public void follow(int followerId, int followeeId) {
            if (followerId == followeeId) {
                return;
            }
            following.computeIfAbsent(followerId, key -> new HashSet<>()).add(followeeId);
        }

        public void unfollow(int followerId, int followeeId) {
            if (following.containsKey(followerId)) {
                following.get(followerId).remove(followeeId);
            }
        }

        private void addUserHead(int userId, PriorityQueue<TweetNode> heap) {
            if (userToTweets.containsKey(userId)) {
                // Only the newest tweet from each timeline is needed at first.
                heap.offer(userToTweets.get(userId));
            }
        }
    }

    private static class Tweet {
        private int userId;
        private int tweetId;
        private int time;

        private Tweet(int userId, int tweetId, int time) {
            this.userId = userId;
            this.tweetId = tweetId;
            this.time = time;
        }
    }

    private static class TweetNode {
        private int userId;
        private int tweetId;
        private int time;
        private TweetNode next;

        private TweetNode(int userId, int tweetId, int time, TweetNode next) {
            this.userId = userId;
            this.tweetId = tweetId;
            this.time = time;
            this.next = next;
        }
    }
}

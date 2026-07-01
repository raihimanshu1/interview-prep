# 🏆 Problem 71: Real-Time Leaderboard (Gaming)

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: Gaming, sports, any ranking system  
> **Est. Time**: 90 min | **Patterns**: Strategy, Observer, Data Structures

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a real-time leaderboard for ranking players."

**What the interviewer tests**:
```
1. Can you maintain rankings? (Sorted scores)
2. Can you handle high throughput? (1000s of score updates/sec)
3. Can you query top players? (Top 10, around me)
4. Can you handle ties? (Same score)
```

### Step 2: The "Aha!" Moment

The key insight: **Leaderboard = sorted data structure with efficient updates.**

```
OPERATIONS:
  1. Update score: player123 → 1500 (was 1200)
  2. Get top 10: [player456: 2000, ...]
  3. Get rank: player123 → rank 42
  4. Get around: players around rank 42

DATA STRUCTURES:
  - TreeMap: Sorted by score
  - Min-heap: Top K players
  - Skip list: Fast rank queries
```

### Step 3: How to handle scale?

```
SCALE:
  10M players
  1000 score updates/sec
  1000 leaderboard queries/sec
  
OPTIMIZATIONS:
  - Bucket by score range
  - Shard by player prefix
  - Cache top 1000
  - Approximate for "around me"
```

---

## 💻 Core Implementation

```java
package com.leaderboard;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: Leaderboard maintains player rankings.
 */
public class Leaderboard {
    
    private final Map<String, Player> players;
    private final NavigableMap<Integer, Set<String>> scoreIndex;
    private final int maxCached;

    public Leaderboard(int maxCached) {
        this.players = new ConcurrentHashMap<>();
        this.scoreIndex = new ConcurrentSkipListMap<>(Collections.reverseOrder());
        this.maxCached = maxCached;
    }

    /**
     * INTUITION: Update player score.
     */
    public synchronized void updateScore(String playerId, int score) {
        Player player = players.get(playerId);
        
        if (player == null) {
            player = new Player(playerId, score);
            players.put(playerId, player);
        } else {
            // Remove from old score index
            Set<String> oldScores = scoreIndex.get(player.getScore());
            if (oldScores != null) {
                oldScores.remove(playerId);
                if (oldScores.isEmpty()) {
                    scoreIndex.remove(player.getScore());
                }
            }
            
            // Update score
            player.setScore(score);
        }
        
        // Add to new score index
        scoreIndex.computeIfAbsent(score, k -> ConcurrentHashMap.newKeySet())
                  .add(playerId);
    }

    /**
     * INTUITION: Get top K players.
     */
    public List<Player> getTopK(int k) {
        List<Player> top = new ArrayList<>();
        int count = 0;
        
        for (Map.Entry<Integer, Set<String>> entry : scoreIndex.entrySet()) {
            for (String playerId : entry.getValue()) {
                top.add(players.get(playerId));
                count++;
                
                if (count >= k) break;
            }
            if (count >= k) break;
        }
        
        return top;
    }

    /**
     * INTUITION: Get player rank (1-based).
     */
    public int getRank(String playerId) {
        Player player = players.get(playerId);
        if (player == null) return -1;
        
        int rank = 1;
        
        for (Map.Entry<Integer, Set<String>> entry : scoreIndex.entrySet()) {
            if (entry.getKey() > player.getScore()) {
                rank += entry.getValue().size();
            } else if (entry.getKey() == player.getScore()) {
                // Same score, rank is position within tie group
                return rank + entry.getValue().headSet(playerId).size();
            } else {
                break;
            }
        }
        
        return rank;
    }

    /**
     * Get players around given player (e.g., rank ± 5).
     */
    public List<Player> getAroundPlayer(String playerId, int range) {
        int rank = getRank(playerId);
        if (rank == -1) return Collections.emptyList();
        
        List<Player> around = new ArrayList<>();
        int count = 0;
        
        for (Map.Entry<Integer, Set<String>> entry : scoreIndex.entrySet()) {
            for (String pid : entry.getValue()) {
                int currentRank = count + 1;
                if (currentRank >= rank - range && currentRank <= rank + range) {
                    around.add(players.get(pid));
                }
                count++;
            }
        }
        
        return around;
    }

    public Player getPlayer(String playerId) {
        return players.get(playerId);
    }
}

/**
 * Player with score.
 */
class Player {
    private final String playerId;
    private int score;
    private final long createdAt;

    Player(String playerId, int score) {
        this.playerId = playerId;
        this.score = score;
        this.createdAt = System.currentTimeMillis();
    }

    public String getPlayerId() { return playerId; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}

/**
 * Sharded leaderboard for scale.
 */
class ShardedLeaderboard {
    private final Map<String, Leaderboard> shards;
    private final int shardCount;

    ShardedLeaderboard(int shardCount) {
        this.shardCount = shardCount;
        this.shards = new ConcurrentHashMap<>();
        
        for (int i = 0; i < shardCount; i++) {
            shards.put("shard-" + i, new Leaderboard(10000));
        }
    }

    private Leaderboard getShard(String playerId) {
        int shard = Math.abs(playerId.hashCode()) % shardCount;
        return shards.get("shard-" + shard);
    }

    void updateScore(String playerId, int score) {
        getShard(playerId).updateScore(playerId, score);
    }

    List<Player> getGlobalTopK(int k) {
        List<Player> global = new ArrayList<>();
        
        // Merge top K from each shard
        for (Leaderboard shard : shards.values()) {
            global.addAll(shard.getTopK(k));
        }
        
        // Sort and return top K
        global.sort(Comparator.comparingInt(Player::getScore).reversed());
        return global.subList(0, Math.min(k, global.size()));
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle ties?"
> "Lexicographic ordering. Timestamp as secondary sort. Shared ranks."

### Q2: "How to prevent cheating?"
> "Server-side validation. Rate limiting. Anomaly detection."

### Q3: "How to handle real-time updates?"
> "WebSocket push. Delta updates. Throttle to 1/sec per player."

### Q4: "How to paginate through rankings?"
> "Cursor-based pagination. Seek by score. Avoid offset."
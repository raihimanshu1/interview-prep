# 🎵 Problem 39: Music Player (Spotify-like)

> **Difficulty**: ⭐⭐ | **Company Fit**: Spotify, Apple Music, any streaming service  
> **Est. Time**: 60 min | **Patterns**: Observer, Strategy, Singleton, Iterator

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a music player."

**What the interviewer tests**:
```
1. Can you model playlists? (User playlists, queue management)
2. Can you handle playback controls? (Play, pause, next, shuffle)
3. Can you implement different modes? (Shuffle, repeat)
4. Can you manage songs? (Library, search, favorites)
```

### Step 2: The "Aha!" Moment

The key insight: **Playback state management + queue manipulation.**

```
MUSIC PLAYER STATE:
  [Stopped] → [Playing] → [Paused] → [Playing]
      ↑           ↓           ↓
      └───────────┴───────────┘

QUEUE MANAGEMENT:
  Now Playing: Song A
  Up Next: [B, C, D, E]
  User adds: F, G
  
  New Queue: [B, C, D, E, F, G]

SHUFFLE:
  Now Playing: A (was at position 0)
  Shuffle rest: [D, B, G, E, C]
  New Queue: [A, D, B, G, E, C]

REPEAT MODES:
  OFF: Play queue once → stop
  ONE: Repeat current song
  ALL: After queue done, restart queue
```

### Step 3: How to handle large libraries?

```
SONG LIBRARY:
  100M songs, 100M users

Search:
  - Trie for song title prefix search
  - HashMap for artist/album lookup
  - Inverted index for tags/genres

Playback:
  - Stream chunks (don't load entire song)
  - Cache next 3 songs in queue
  - Pre-fetch based on listening history
```

---

## 💻 Core Implementation

```java
package com.player;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: MusicPlayer is the facade.
 * 
 * Coordinates:
 * - Playback engine
 * - Queue manager
 * - Library search
 * - User playlists
 */
public class MusicPlayer {
    
    // Singleton: only one player instance
    private static volatile MusicPlayer instance;
    
    private final PlaybackEngine engine;
    private final QueueManager queueManager;
    private final MusicLibrary library;
    private final UserManager userManager;
    
    // Current state
    private volatile PlayerState state;
    private Song currentSong;
    private int currentIndex;

    private MusicPlayer() {
        this.engine = new PlaybackEngine();
        this.queueManager = new QueueManager();
        this.library = new MusicLibrary();
        this.userManager = new UserManager();
        this.state = PlayerState.STOPPED;
    }

    // Singleton pattern
    public static MusicPlayer getInstance() {
        if (instance == null) {
            synchronized (MusicPlayer.class) {
                if (instance == null) {
                    instance = new MusicPlayer();
                }
            }
        }
        return instance;
    }

    /**
     * INTUITION: Play a song.
     * 
     * 1. If queue empty, add song
     * 2. Set current index
     * 3. Start playback
     * 4. Notify listeners
     */
    public synchronized void play(String songId) {
        Song song = library.getSong(songId);
        if (song == null) throw new SongNotFoundException(songId);
        
        if (queueManager.isEmpty()) {
            queueManager.addToQueue(song);
            currentIndex = 0;
        } else {
            // Replace or insert at current position
            queueManager.playNow(song);
        }
        
        currentSong = song;
        state = PlayerState.PLAYING;
        
        engine.play(song);
        notifyListeners();
    }

    /**
     * INTUITION: Toggle pause/play.
     */
    public void togglePause() {
        if (state == PlayerState.PLAYING) {
            engine.pause();
            state = PlayerState.PAUSED;
        } else if (state == PlayerState.PAUSED) {
            engine.resume();
            state = PlayerState.PLAYING;
        }
        notifyListeners();
    }

    /**
     * INTUITION: Next song.
     * 
     * Depends on repeat mode:
     - NORMAL: currentIndex++
     - REPEAT_ONE: same song
     - REPEAT_ALL: currentIndex++ (wrap around)
     */
    public synchronized void next() {
        if (queueManager.isEmpty()) return;
        
        RepeatMode mode = queueManager.getRepeatMode();
        
        if (mode == RepeatMode.REPEAT_ONE) {
            // Replay current song
            engine.play(currentSong);
            return;
        }
        
        // Move to next
        currentIndex++;
        
        // Wrap around if repeat all
        if (currentIndex >= queueManager.getQueueSize() && mode == RepeatMode.REPEAT_ALL) {
            currentIndex = 0;
        }
        
        if (currentIndex < queueManager.getQueueSize()) {
            currentSong = queueManager.getSongAt(currentIndex);
            engine.play(currentSong);
        } else {
            // Queue exhausted
            state = PlayerState.STOPPED;
            engine.stop();
        }
        
        notifyListeners();
    }

    /**
     * INTUITION: Previous song.
     * 
     * If playing > 3 seconds → restart song
     * Else → go to previous in queue
     */
    public synchronized void previous() {
        if (queueManager.isEmpty()) return;
        
        if (engine.getCurrentPosition() > 3000) {
            // Restart current song
            engine.play(currentSong);
            return;
        }
        
        currentIndex = Math.max(0, currentIndex - 1);
        currentSong = queueManager.getSongAt(currentIndex);
        engine.play(currentSong);
        notifyListeners();
    }

    /**
     * INTUITION: Shuffle queue.
     * 
     * Keep current song first, shuffle rest.
     */
    public synchronized void shuffle() {
        List<Song> queue = queueManager.getQueue();
        if (queue.size() <= 1) return;
        
        // Remove current from queue
        Song current = queue.remove(0);
        
        // Shuffle remaining (Fisher-Yates)
        Random random = new Random();
        for (int i = queue.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Song temp = queue.get(i);
            queue.set(i, queue.get(j));
            queue.set(j, temp);
        }
        
        // Rebuild queue
        queueManager.setQueue(queue);
        currentIndex = 0;
        currentSong = queue.get(0);
        
        engine.play(currentSong);
        notifyListeners();
    }

    public void addToQueue(String songId) {
        Song song = library.getSong(songId);
        if (song != null) {
            queueManager.addToQueue(song);
            notifyListeners();
        }
    }

    public void addToQueue(String songId, int position) {
        Song song = library.getSong(songId);
        if (song != null) {
            queueManager.addToQueue(song, position);
            notifyListeners();
        }
    }

    public void removeFromQueue(int position) {
        queueManager.removeFromQueue(position);
        if (position < currentIndex) {
            currentIndex--;
        }
        notifyListeners();
    }

    public void setRepeatMode(RepeatMode mode) {
        queueManager.setRepeatMode(mode);
        notifyListeners();
    }

    // --- Getters ---

    public PlayerState getState() { return state; }
    public Song getCurrentSong() { return currentSong; }
    public int getCurrentPosition() { return engine.getCurrentPosition(); }
    public List<Song> getPlayQueue() { return queueManager.getQueue(); }
    public RepeatMode getRepeatMode() { return queueManager.getRepeatMode(); }

    // --- Observer pattern ---

    private final List<PlaybackListener> listeners = new CopyOnWriteArrayList<>();

    public void addListener(PlaybackListener listener) {
        listeners.add(listener);
    }

    public void removeListener(PlaybackListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (PlaybackListener listener : listeners) {
            listener.onStateChanged(state, currentSong, currentIndex);
        }
    }
}

enum PlayerState {
    PLAYING, PAUSED, STOPPED
}

enum RepeatMode {
    OFF, REPEAT_ONE, REPEAT_ALL
}

interface PlaybackListener {
    void onStateChanged(PlayerState state, Song currentSong, int position);
}
```

```java
package com.player;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: QueueManager manages the playback queue.
 * 
 * Supports:
 * - Add/remove songs
 * - Play now (replace current)
 * - Shuffle (randomize order)
 * - Repeat modes
 */
class QueueManager {
    
    private final List<Song> queue;
    private RepeatMode repeatMode = RepeatMode.OFF;
    private final Random random = new Random();

    QueueManager() {
        this.queue = new CopyOnWriteArrayList<>();
    }

    boolean isEmpty() {
        return queue.isEmpty();
    }

    int getQueueSize() {
        return queue.size();
    }

    void addToQueue(Song song) {
        queue.add(song);
    }

    void addToQueue(Song song, int position) {
        if (position >= queue.size()) {
            queue.add(song);
        } else {
            queue.add(position, song);
        }
    }

    /**
     * INTUITION: Play a song immediately (replace current).
     */
    void playNow(Song song) {
        if (!queue.isEmpty()) {
            queue.set(0, song);
        } else {
            queue.add(0, song);
        }
    }

    void removeFromQueue(int position) {
        if (position >= 0 && position < queue.size()) {
            queue.remove(position);
        }
    }

    Song getSongAt(int position) {
        if (position >= 0 && position < queue.size()) {
            return queue.get(position);
        }
        return null;
    }

    List<Song> getQueue() {
        return new ArrayList<>(queue);
    }

    void setQueue(List<Song> newQueue) {
        queue.clear();
        queue.addAll(newQueue);
    }

    RepeatMode getRepeatMode() { return repeatMode; }
    void setRepeatMode(RepeatMode mode) { this.repeatMode = mode; }
}
```

```java
package com.player;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * INTUITION: MusicLibrary indexes all songs.
 * 
 * Multiple indexes for different searches:
 * - Trie: prefix search on title
 * - HashMap: exact search on songId
 * - Inverted index: search by artist/genre
 */
class MusicLibrary {
    
    // songId → Song
    private final Map<String, Song> songs;
    
    // Trie for title search
    private final Trie titleIndex;
    
    // Inverted index: artist → songIds
    private final Map<String, List<String>> artistIndex;

    MusicLibrary() {
        this.songs = new ConcurrentHashMap<>();
        this.titleIndex = new Trie();
        this.artistIndex = new ConcurrentHashMap<>();
    }

    /**
     * Add song to library.
     */
    void addSong(Song song) {
        songs.put(song.getId(), song);
        titleIndex.insert(song.getTitle(), song.getId());
        artistIndex.computeIfAbsent(song.getArtist(), k -> new CopyOnWriteArrayList<>())
                   .add(song.getId());
    }

    Song getSong(String songId) {
        return songs.get(songId);
    }

    /**
     * INTUITION: Search songs by title prefix.
     */
    List<Song> searchByTitle(String prefix) {
        List<String> songIds = titleIndex.search(prefix);
        List<Song> results = new ArrayList<>();
        for (String id : songIds) {
            Song song = songs.get(id);
            if (song != null) results.add(song);
        }
        return results;
    }

    /**
     * INTUITION: Search songs by artist.
     */
    List<Song> searchByArtist(String artist) {
        List<String> songIds = artistIndex.getOrDefault(artist, Collections.emptyList());
        List<Song> results = new ArrayList<>();
        for (String id : songIds) {
            Song song = songs.get(id);
            if (song != null) results.add(song);
        }
        return results;
    }

    public int getLibrarySize() {
        return songs.size();
    }
}
```

```java
package com.player;

import java.time.LocalDateTime;
import java.util.*;

/**
 * INTUITION: Song is immutable once created.
 */
public class Song {
    private final String id;
    private final String title;
    private final String artist;
    private final String album;
    private final int duration;  // in seconds
    private final String genre;
    private final String spotifyId;  // For streaming
    private final LocalDateTime addedAt;

    public Song(String id, String title, String artist, String album, 
                int duration, String genre) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.genre = genre;
        this.spotifyId = "";
        this.addedAt = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public int getDuration() { return duration; }
    public String getGenre() { return genre; }
}

/**
 * Trie for prefix search on song titles.
 */
class Trie {
    private final TrieNode root = new TrieNode();

    void insert(String word, String songId) {
        TrieNode current = root;
        for (char c : word.toLowerCase().toCharArray()) {
            current = current.children.computeIfAbsent(c, k -> new TrieNode());
            current.songIds.add(songId);
        }
    }

    List<String> search(String prefix) {
        TrieNode current = root;
        for (char c : prefix.toLowerCase().toCharArray()) {
            current = current.children.get(c);
            if (current == null) return Collections.emptyList();
        }
        return new ArrayList<>(current.songIds);
    }

    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        Set<String> songIds = new HashSet<>();
    }
}
```

```java
package com.player;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * INTUITION: UserManager handles user-specific data.
 * 
 * - Playlists
 * - Favorites
 * - Listening history
 */
class UserManager {
    
    private final Map<String, User> users;
    private final Map<String, Playlist> playlists;

    UserManager() {
        this.users = new ConcurrentHashMap<>();
        this.playlists = new ConcurrentHashMap<>();
    }

    User getUser(String userId) {
        return users.computeIfAbsent(userId, k -> new User(userId, "User " + k));
    }

    Playlist createPlaylist(String userId, String name) {
        User user = getUser(userId);
        Playlist playlist = new Playlist(UUID.randomUUID().toString(), name, userId);
        playlists.put(playlist.getId(), playlist);
        user.addPlaylist(playlist.getId());
        return playlist;
    }

    void addFavorite(String userId, String songId) {
        User user = getUser(userId);
        user.addFavorite(songId);
    }

    List<Song> getFavorites(String userId) {
        User user = getUser(userId);
        List<Song> favorites = new ArrayList<>();
        for (String songId : user.getFavorites()) {
            favorites.add(new Song(songId, "Song " + songId, "Artist", "Album", 
                                  180, "Pop"));
        }
        return favorites;
    }
}

class User {
    private final String userId;
    private String name;
    private final Set<String> playlists;
    private final Set<String> favorites;
    private final LocalDateTime createdAt;

    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
        this.playlists = ConcurrentHashMap.newKeySet();
        this.favorites = ConcurrentHashMap.newKeySet();
        this.createdAt = LocalDateTime.now();
    }

    void addPlaylist(String playlistId) {
        playlists.add(playlistId);
    }

    void addFavorite(String songId) {
        favorites.add(songId);
    }

    public String getUserId() { return userId; }
    public Set<String> getFavorites() { return favorites; }
}

class Playlist {
    private final String id;
    private String name;
    private final String userId;
    private final List<String> songs;
    private final LocalDateTime createdAt;

    public Playlist(String id, String name, String userId) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.songs = new CopyOnWriteArrayList<>();
        this.createdAt = LocalDateTime.now();
    }

    void addSong(String songId) {
        songs.add(songId);
    }

    void removeSong(String songId) {
        songs.remove(songId);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<String> getSongs() { return new ArrayList<>(songs); }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle offline playback?"
> "Download encrypted songs to device. DRM protection. Check license on play. Sync when online."

### Q2: "How to recommend songs?"
> "Collaborative filtering: users like you also liked. Content-based: similar genre/tempo. Hybrid approach."

### Q3: "How to handle lyrics syncing?"
> "Store LRC format with timestamps. Seek to current time. Highlight current line. Karaoke mode."

### Q4: "How to support multiple devices?"
> "Sync queue via cloud. Resume playback on new device. State machine tracks current position."
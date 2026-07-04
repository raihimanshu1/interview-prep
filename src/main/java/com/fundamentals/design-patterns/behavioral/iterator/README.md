# Iterator Pattern

> **Provides a way to access elements of an aggregate object sequentially without exposing its underlying representation.**

## 📖 Concept

**Real-world analogy:** A TV remote channel button — you can cycle through channels without knowing how the TV stores or organizes them internally.

## 🔍 When to Use

- Need to access elements of a collection without exposing internals
- Want to provide multiple traversal algorithms for same collection
- Want uniform interface for traversing different collections
- Collection implementation should be independent of traversal

## ✅ Interview Checklist

- [ ] Iterator interface with hasNext() and next()
- [ ] Aggregate/Collection interface with iterator() method
- [ ] Concrete Iterator implements traversal logic
- [ ] Concrete Collection returns iterator
- [ ] Consider fail-fast vs fail-safe iterators

## 🧪 Common Interview Question

**Problem:** Design a custom playlist iterator for a music app. The playlist supports different traversal modes: sequential and shuffle.

## 💻 Java Implementation

### 1. Basic Iterator

```java
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

// Iterator Interface
interface PlaylistIterator {
    boolean hasNext();
    Song next();
}

// Aggregate
class Playlist {
    private List<Song> songs = new ArrayList<>();
    private String name;

    public Playlist(String name) { this.name = name; }

    public void addSong(Song song) { songs.add(song); }
    public List<Song> getSongs() { return songs; }

    public PlaylistIterator iterator(String mode) {
        return switch (mode) {
            case "shuffle" -> new ShuffleIterator(this);
            default -> new SequentialIterator(this);
        };
    }
}

// Song
class Song {
    private String title;
    private String artist;
    private int duration;

    public Song(String title, String artist, int duration) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return title + " - " + artist + " (" + duration + "s)";
    }
}

// Concrete Iterator: Sequential
class SequentialIterator implements PlaylistIterator {
    private Playlist playlist;
    private int index = 0;

    public SequentialIterator(Playlist playlist) {
        this.playlist = playlist;
    }

    @Override
    public boolean hasNext() {
        return index < playlist.getSongs().size();
    }

    @Override
    public Song next() {
        if (!hasNext()) throw new NoSuchElementException();
        return playlist.getSongs().get(index++);
    }
}

// Concrete Iterator: Shuffle
import java.util.Collections;

class ShuffleIterator implements PlaylistIterator {
    private List<Song> songs;
    private int index = 0;

    public ShuffleIterator(Playlist playlist) {
        this.songs = new ArrayList<>(playlist.getSongs());
        Collections.shuffle(this.songs);
    }

    @Override public boolean hasNext() { return index < songs.size(); }
    @Override public Song next() { return songs.get(index++); }
}
```

### 2. Usage

```java
public class IteratorDemo {
    public static void main(String[] args) {
        Playlist playlist = new Playlist("Favorites");
        playlist.addSong(new Song("Song A", "Artist A", 180));
        playlist.addSong(new Song("Song B", "Artist B", 200));
        playlist.addSong(new Song("Song C", "Artist C", 210));

        System.out.println("Sequential:");
        PlaylistIterator it = playlist.iterator("sequential");
        while (it.hasNext()) System.out.println("  " + it.next());

        System.out.println("Shuffle:");
        it = playlist.iterator("shuffle");
        while (it.hasNext()) System.out.println("  " + it.next());
    }
}
```

### 3. Full Working Example: Custom Collection

```java
// Custom Collection
class MyList<T> {
    private T[] items;
    private int size = 0;

    @SuppressWarnings("unchecked")
    public MyList(int capacity) {
        items = (T[]) new Object[capacity];
    }

    public void add(T item) {
        items[size++] = item;
    }

    public int size() { return size; }

    public MyIterator<T> iterator() {
        return new MyListIterator();
    }

    // Iterator Interface
    public interface MyIterator<T> {
        boolean hasNext();
        T next();
        boolean hasPrev();
        T prev();
    }

    // Concrete Iterator
    private class MyListIterator implements MyIterator<T> {
        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex < size;
        }

        @Override
        public T next() {
            if (!hasNext()) throw new NoSuchElementException();
            return items[currentIndex++];
        }

        @Override
        public boolean hasPrev() {
            return currentIndex > 0;
        }

        @Override
        public T prev() {
            if (!hasPrev()) throw new NoSuchElementException();
            return items[--currentIndex];
        }
    }
}

// Usage
public class CustomListDemo {
    public static void main(String[] args) {
        MyList<String> list = new MyList<>(5);
        list.add("A");
        list.add("B");
        list.add("C");

        MyList.MyIterator<String> it = list.iterator();
        System.out.println("Forward:");
        while (it.hasNext()) System.out.println("  " + it.next());

        System.out.println("Backward:");
        while (it.hasPrev()) System.out.println("  " + it.prev());
    }
}
```

## ⚠️ Pitfalls to Avoid

| Issue | Solution |
|-------|----------|
| Concurrent modification | Make iterator fail-fast or use copy-on-write |
| Iterator invalidation | Document clearly or use safe iterators |
| Leaking internal structure | Return copy or unmodifiable view |
| Performance on linked structures | Consider using indexed access where possible |

## 🎯 Related Interview Questions

1. **Design a custom collection** — Implement `MyArrayList` with iterator
2. **Design a NestedIterator** for flattening nested lists (LeetCode 341)
3. **Java's built-in Iterator** — `Iterator<E>` with `hasNext()` and `next()`

## 🆚 Iterator vs ListIterator

| Aspect | Iterator | ListIterator |
|--------|----------|-------------|
| Direction | Forward only | Forward and backward |
| Methods | hasNext(), next() | hasNext(), next(), hasPrev(), prev() |
| Index access | No | Yes (nextIndex, prevIndex) |
| Add/Set | No | Yes (add, set) |
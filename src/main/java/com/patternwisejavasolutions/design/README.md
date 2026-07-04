# Design / OOD

> **Core Pattern:** Design data structures and systems using built-in Java collections, linked lists, hashmaps, and custom algorithms.  
> **Learning Path:** LRU Cache → Hit counter → Browser history → OO Design (Parking Lot) → System design light.

---

## 📖 Conceptual Foundation

### Design Patterns Used
| Problem | Data Structure | Key Pattern |
|---------|---------------|-------------|
| LRU Cache | `HashMap` + `DoublyLinkedList` | O(1) get/put with eviction |
| Hit Counter | `Queue` or `Circular Array` | Sliding window timestamp |
| Browser History | Two stacks | Back/Forward navigation |
| Snake Game | `Deque` + `HashSet` | Move head, remove tail |
| Time-based KV Store | `HashMap<String, TreeMap<Integer, String>>` | Binary search on timestamps |

---

## 📚 Learning Order

| # | Problem | File | Key Technique | Difficulty |
|---|---------|------|---------------|------------|
| 1 | **LRU Cache** | [LRUCache.java](LRUCache.java) | HashMap + Doubly Linked List (remove/add to head) | 🟡 Medium |
| 2 | **Design Hit Counter** | [DesignHitCounter.java](DesignHitCounter.java) | Queue of timestamps OR circular array | 🟡 Medium |
| 3 | **Design Browser History** | [DesignBrowserHistory.java](DesignBrowserHistory.java) | Two stacks (back/forward) | 🟡 Medium |
| 4 | **Design File System** | [DesignFileSystem.java](DesignFileSystem.java) | Trie-like path structure with HashMap | 🟡 Medium |
| 5 | **Time Based Key-Value Store** | [TimeBasedKeyValueStore.java](TimeBasedKeyValueStore.java) | TreeMap per key → floorEntry for timestamp | 🟡 Medium |
| 6 | **Design Twitter** | [DesignTwitter.java](DesignTwitter.java) | HashMap of users + tweets + follows, merge K feeds | 🟡 Medium |
| 7 | **Design Snake Game** | [DesignSnakeGame.java](DesignSnakeGame.java) | Deque for body, HashSet for collision detection | 🟡 Medium |
| 8 | **Parking Lot System** | [ParkingLotSystem.java](ParkingLotSystem.java) | OOD with levels, spots, vehicle types | 🟡 Medium |

---

## 🔑 Key Insights

1. **LRU Cache** = HashMap for O(1) lookup + DLL for O(1) removal/insertion
2. **Hit Counter** = queue for O(1) per hit, or circular array for O(1) bucket
3. **Browser History** = two stacks: one for back, one for forward
4. **Twitter** = pull model: collect tweets from followed users, merge by timestamp

---

## 🎯 Practice Checklist

- [ ] LRU Cache
- [ ] Hit Counter
- [ ] Browser History
- [ ] File System
- [ ] Time Based KV Store
- [ ] Twitter
- [ ] Snake Game
- [ ] Parking Lot
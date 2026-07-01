# Proxy Pattern

> **Provides a surrogate or placeholder for another object to control access to it.**

## 📖 Concept

**Real-world analogy:** A credit card is a proxy for your bank account. The card controls access to the account without exposing it directly.

## 🔍 When to Use

- Lazy loading (virtual proxy) — load heavy object only when needed
- Access control (protection proxy) — check permissions before access
- Caching — serve cached results instead of hitting the real object
- Logging — log each access to an object
- Remote access — represent object in different address space

## ✅ Interview Checklist

- [ ] Subject interface — common interface for RealSubject and Proxy
- [ ] RealSubject — the actual object being proxied
- [ ] Proxy implements Subject, controls access to RealSubject
- [ ] Client interacts with Proxy, unaware of RealSubject
- [ ] Proxy decides when to create/call RealSubject

## 🧪 Common Interview Question

**Problem:** Design a proxy for an expensive `VideoDownloader`. It should cache downloaded videos so if the same video is requested again, it returns the cached version instead of downloading again.

## 💻 Java Implementation

### 1. Basic Proxy

```java
// Subject Interface
interface VideoDownloader {
    void download(String videoName);
}

// Real Subject (expensive to create/call)
class RealVideoDownloader implements VideoDownloader {
    @Override
    public void download(String videoName) {
        System.out.println("Downloading video: " + videoName + " (from server)");
        // Simulate expensive network call
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
    }
}

// Proxy
class CachedVideoDownloader implements VideoDownloader {
    private RealVideoDownloader downloader;
    private Set<String> cache = new HashSet<>();

    @Override
    public void download(String videoName) {
        if (cache.contains(videoName)) {
            System.out.println("Returning cached video: " + videoName);
            return;
        }
        if (downloader == null) downloader = new RealVideoDownloader();
        downloader.download(videoName);
        cache.add(videoName);
    }
}
```

### 2. Usage

```java
public class ProxyDemo {
    public static void main(String[] args) {
        VideoDownloader downloader = new CachedVideoDownloader();

        downloader.download("video1.mp4"); // Downloads from server
        downloader.download("video2.mp4"); // Downloads from server
        downloader.download("video1.mp4"); // Returns from cache!
    }
}
```

### 3. Full Working Example: Protection Proxy (Access Control)

```java
// Subject
interface Document {
    void view();
    void edit();
}

// Real Subject
class RealDocument implements Document {
    private String content;
    private String owner;

    public RealDocument(String owner, String content) {
        this.owner = owner;
        this.content = content;
    }

    @Override
    public void view() {
        System.out.println("Viewing document: " + content);
    }

    @Override
    public void edit() {
        System.out.println("Editing document: " + content);
    }
}

// Protection Proxy
class DocumentProxy implements Document {
    private RealDocument document;
    private String currentUser;
    private Set<String> editors;

    public DocumentProxy(String owner, String content) {
        this.document = new RealDocument(owner, content);
        this.editors = new HashSet<>();
        editors.add(owner); // owner can edit
    }

    public void addEditor(String user) {
        editors.add(user);
    }

    @Override
    public void view() {
        // Anyone can view
        document.view();
    }

    @Override
    public void edit() {
        if (editors.contains(currentUser)) {
            document.edit();
        } else {
            System.out.println("Access denied: " + currentUser + " cannot edit this document");
        }
    }

    public void setCurrentUser(String user) {
        this.currentUser = user;
    }
}

// Usage
public class DocumentDemo {
    public static void main(String[] args) {
        DocumentProxy doc = new DocumentProxy("Alice", "Secret Project Plan");
        doc.addEditor("Bob");

        // Alice can edit
        doc.setCurrentUser("Alice");
        doc.edit();

        // Bob can edit
        doc.setCurrentUser("Bob");
        doc.edit();

        // Charlie cannot edit
        doc.setCurrentUser("Charlie");
        doc.view(); // OK - anyone can view
        doc.edit(); // Access denied
    }
}
```

## ⚠️ Pitfalls to Avoid

| Issue | Solution |
|-------|----------|
| Proxy adds too much latency | Cache aggressively, use async loading |
| Proxy becomes a God object | Keep single responsibility — one proxy per concern |
| Breaking transparency | Proxy should be indistinguishable from RealSubject |
| Memory leak with cached objects | Implement cache eviction policy |

## 🎯 Related Interview Questions

1. **Design a protection proxy** for sensitive documents — check user role before access
2. **Design a virtual proxy** for lazy loading high-resolution images
3. **Difference between Proxy and Decorator?** — Proxy controls access; Decorator adds behavior
4. **Spring AOP uses Proxy pattern** — JDK dynamic proxy / CGLIB

## 🆚 Proxy vs Decorator

| Aspect | Proxy | Decorator |
|--------|-------|-----------|
| Purpose | Control access | Add behavior |
| Relationship | Same interface, different object | Wraps and delegates |
| Created by | Client or third party | Client |
| Example | Caching, lazy loading | Adding toppings, encryption |
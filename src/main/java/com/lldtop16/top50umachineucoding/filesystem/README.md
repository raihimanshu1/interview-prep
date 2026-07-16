# 📁 Problem 28: File System (Unix-like)

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: Google, Microsoft, Dropbox  
> **Est. Time**: 120 min | **Patterns**: Composite, Command, Chain of Responsibility

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a Unix-like file system."

**What the interviewer tests**:
```
1. Can you model the hierarchy? (Directories contain files/directories)
2. Can you handle paths? (/home/user/file.txt)
3. Can you handle permissions? (read/write/execute per user/group)
4. Can you handle file operations? (create, delete, read, write, search)
```

### Step 2: The "Aha!" Moment

The key insight: **Composite Pattern for the file hierarchy.**

```
FileSystemEntity (abstract)
├── File (leaf)
└── Directory (composite)
    ├── File
    ├── Directory
    └── Directory (nested!)

A Directory IS-A FileSystemEntity and CONTAINS FileSystemEntities.
This is the classic Composite pattern.
```

Path resolution:
```
/home/user/docs/file.txt
  ↓
root (/) 
  → home (dir)
    → user (dir)
      → docs (dir)
        → file.txt (file)
```

### Step 3: How to handle large files?

```
Don't load entire file into memory.

Instead:
  - File is split into CHUNKS (4KB, like Unix)
  - Each chunk stored separately
  - File metadata: list of chunk pointers
  
Reading: Load chunks on demand (streaming).
Writing: Append new chunks.
```

---

## 💻 Core Implementation

```java
package com.fs;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * INTUITION: FileSystem is the main API.
 * 
 * Models a Unix-like file system with:
 * - Hierarchical directories (Composite pattern)
 * - File permissions (rwx for user/group/others)
 * - Path resolution (/home/user/file.txt)
 */
public class FileSystem {
    
    private final FileSystemEntity root;
    private final Map<String, User> users;
    private final User currentUser;
    private final AtomicInteger inodeCounter = new AtomicInteger(1);

    public FileSystem() {
        this.root = new Directory("/", null, User.ROOT);
        this.users = new ConcurrentHashMap<>();
        this.currentUser = User.ROOT;
    }

    /**
     * INTUITION: Create a file.
     * 
     * 1. Resolve parent directory from path
     * 2. Check if name already exists
     * 3. Create file in parent directory
     * 
     * @param path Full path like /home/user/file.txt
     * @param content File content
     * @throws FileAlreadyExistsException if file exists
     */
    public synchronized File createFile(String path, String content) {
        // Step 1: Resolve parent directory
        int lastSlash = path.lastIndexOf('/');
        String parentPath = path.substring(0, lastSlash);
        String fileName = path.substring(lastSlash + 1);
        
        Directory parent = resolveDirectory(parentPath);
        if (parent == null) {
            throw new IllegalArgumentException("Parent directory not found: " + parentPath);
        }
        
        // Step 2: Check if exists
        if (parent.contains(fileName)) {
            throw new FileAlreadyExistsException(fileName);
        }
        
        // Step 3: Create file
        File file = new File(fileName, parent, content);
        parent.addChild(file);
        
        return file;
    }

    /**
     * INTUITION: Create directory.
     * Same as file but creates Directory instead.
     */
    public synchronized Directory createDirectory(String path) {
        int lastSlash = path.lastIndexOf('/');
        String parentPath = path.substring(0, lastSlash);
        String dirName = path.substring(lastSlash + 1);
        
        Directory parent = resolveDirectory(parentPath);
        if (parent == null) {
            throw new IllegalArgumentException("Parent directory not found: " + parentPath);
        }
        
        if (parent.contains(dirName)) {
            throw new FileAlreadyExistsException(dirName);
        }
        
        Directory dir = new Directory(dirName, parent);
        parent.addChild(dir);
        
        return dir;
    }

    /**
     * INTUITION: Delete file/directory recursively.
     * 
     * 1. Resolve parent directory
     * 2. Remove from parent
     * 3. If it's a directory, delete all children first (recursive)
     */
    public synchronized void delete(String path) {
        int lastSlash = path.lastIndexOf('/');
        String parentPath = path.substring(0, lastSlash);
        String name = path.substring(lastSlash + 1);
        
        Directory parent = resolveDirectory(parentPath);
        if (parent == null) {
            throw new IllegalArgumentException("Parent not found: " + parentPath);
        }
        
        FileSystemEntity entity = parent.getChild(name);
        if (entity == null) {
            throw new FileNotFoundException(name);
        }
        
        // Check permissions
        if (!entity.canWrite(currentUser)) {
            throw new PermissionException("Write permission denied");
        }
        
        // If directory, delete contents first (recursive)
        if (entity instanceof Directory) {
            Directory dir = (Directory) entity;
            for (FileSystemEntity child : new ArrayList<>(dir.getChildren())) {
                delete(path + "/" + child.getName());
            }
        }
        
        // Remove from parent
        parent.removeChild(name);
    }

    /**
     * INTUITION: Resolve a path to a FileSystemEntity.
     * 
     * /home/user/file.txt → ["", "home", "user", "file.txt"]
     * Split by /, then traverse from root.
     */
    public FileSystemEntity resolvePath(String path) {
        if (path.equals("/")) {
            return root;
        }
        
        String[] parts = path.split("/");
        FileSystemEntity current = root;
        
        for (String part : parts) {
            if (part.isEmpty()) continue;
            
            if (current instanceof Directory) {
                current = ((Directory) current).getChild(part);
                if (current == null) {
                    return null;  // Not found
                }
            } else {
                return null;  // Can't traverse through a file
            }
        }
        
        return current;
    }

    /**
     * INTUITION: Search for files by name pattern.
     * DFS traversal of entire file tree.
     */
    public List<File> search(String pattern) {
        List<File> results = new ArrayList<>();
        searchRecursive(root, pattern, results);
        return results;
    }

    private void searchRecursive(FileSystemEntity entity, String pattern, List<File> results) {
        if (entity instanceof File) {
            File file = (File) entity;
            if (file.getName().contains(pattern)) {
                results.add(file);
            }
        } else if (entity instanceof Directory) {
            Directory dir = (Directory) entity;
            for (FileSystemEntity child : dir.getChildren()) {
                searchRecursive(child, pattern, results);
            }
        }
    }

    // --- Helpers ---

    private Directory resolveDirectory(String path) {
        FileSystemEntity entity = resolvePath(path);
        if (entity instanceof Directory) {
            return (Directory) entity;
        }
        return null;
    }
}
```

```java
package com.fs;

import java.util.*;

/**
 * INTUITION: FileSystemEntity is the abstract base for both files and directories.
 * 
 * This is the Component in the Composite pattern.
 * Both File and Directory implement this interface.
 */
public abstract class FileSystemEntity {
    protected final String name;
    protected final FileSystemEntity parent;
    protected final int inode;
    protected final Permissions permissions;
    protected final long createdAt;
    protected final long modifiedAt;

    public FileSystemEntity(String name, FileSystemEntity parent) {
        this.name = name;
        this.parent = parent;
        this.inode = 0;  // Set by FileSystem
        this.permissions = new Permissions(User.ROOT, 0755);  // rwxr-xr-x
        this.createdAt = System.currentTimeMillis();
        this.modifiedAt = System.currentTimeMillis();
    }

    public abstract boolean isDirectory();
    public abstract long getSize();
    
    public abstract boolean canRead(User user);
    public abstract boolean canWrite(User user);
    public abstract boolean canExecute(User user);

    public String getName() { return name; }
    public FileSystemEntity getParent() { return parent; }
    public int getInode() { return inode; }
    public Permissions getPermissions() { return permissions; }
}
```

```java
package com.fs;

import java.util.*;

/**
 * INTUITION: File is a leaf node in the Composite.
 * 
 * Contains:
 * - Content (as list of chunks)
 * - Size
 * - Permissions
 */
public class File extends FileSystemEntity {
    private final List<String> chunks;  // Split file into 4KB chunks
    private long size;

    public File(String name, FileSystemEntity parent, String content) {
        super(name, parent);
        this.chunks = new ArrayList<>();
        this.size = 0;
        
        // Split content into 4KB chunks
        int chunkSize = 4096;
        for (int i = 0; i < content.length(); i += chunkSize) {
            int end = Math.min(i + chunkSize, content.length());
            chunks.add(content.substring(i, end));
        }
        this.size = content.length();
    }

    @Override
    public boolean isDirectory() { return false; }

    @Override
    public long getSize() { return size; }

    public String getContent() {
        StringBuilder sb = new StringBuilder();
        for (String chunk : chunks) {
            sb.append(chunk);
        }
        return sb.toString();
    }

    public void appendContent(String moreContent) {
        chunks.add(moreContent);
        size += moreContent.length();
    }

    @Override
    public boolean canRead(User user) {
        return permissions.canRead(user);
    }

    @Override
    public boolean canWrite(User user) {
        return permissions.canWrite(user);
    }

    @Override
    public boolean canExecute(User user) {
        return permissions.canExecute(user);
    }
}
```

```java
package com.fs;

import java.util.*;

/**
 * INTUITION: Directory is a composite node.
 * 
 * It CONTAINS other FileSystemEntities (files and subdirectories).
 * This is the Composite pattern in action.
 */
public class Directory extends FileSystemEntity {
    private final Map<String, FileSystemEntity> children;

    public Directory(String name, FileSystemEntity parent) {
        super(name, parent);
        this.children = new LinkedHashMap<>();  // Preserves insertion order
    }

    @Override
    public boolean isDirectory() { return true; }

    @Override
    public long getSize() {
        // Directory size = sum of all children sizes
        return children.values().stream()
            .mapToLong(FileSystemEntity::getSize)
            .sum();
    }

    public void addChild(FileSystemEntity child) {
        children.put(child.getName(), child);
    }

    public void removeChild(String name) {
        children.remove(name);
    }

    public FileSystemEntity getChild(String name) {
        return children.get(name);
    }

    public boolean contains(String name) {
        return children.containsKey(name);
    }

    public List<FileSystemEntity> getChildren() {
        return new ArrayList<>(children.values());
    }

    public int getChildCount() {
        return children.size();
    }

    @Override
    public boolean canRead(User user) {
        return permissions.canRead(user);
    }

    @Override
    public boolean canWrite(User user) {
        return permissions.canWrite(user);
    }

    @Override
    public boolean canExecute(User user) {
        return permissions.canExecute(user);
    }
}
```

```java
package com.fs;

import java.util.*;

/**
 * INTUITION: Permissions model Unix rwx bits.
 * 
 * Format: 0755
 *   - Owner: rwx = 7
 *   - Group: r-x = 5
 *   - Others: r-x = 5
 * 
 * Each digit is 3 bits: [r][w][x]
 *   - Read = 4 (100 in binary)
 *   - Write = 2 (010 in binary)
 *   - Execute = 1 (001 in binary)
 *   - Sum = permission (e.g., 4+2+1=7)
 */
class Permissions {
    private final User owner;
    private final int mode;  // Unix permission bits (e.g., 0755)

    public Permissions(User owner, int mode) {
        this.owner = owner;
        this.mode = mode;
    }

    public boolean canRead(User user) {
        if (user.isRoot()) return true;
        if (user.equals(owner)) return (mode & 400) != 0;
        if (user.getGroup().equals(owner.getGroup())) return (mode & 40) != 0;
        return (mode & 4) != 0;
    }

    public boolean canWrite(User user) {
        if (user.isRoot()) return true;
        if (user.equals(owner)) return (mode & 200) != 0;
        if (user.getGroup().equals(owner.getGroup())) return (mode & 20) != 0;
        return (mode & 2) != 0;
    }

    public boolean canExecute(User user) {
        if (user.isRoot()) return true;
        if (user.equals(owner)) return (mode & 100) != 0;
        if (user.getGroup().equals(owner.getGroup())) return (mode & 10) != 0;
        return (mode & 1) != 0;
    }
}

class User {
    public static final User ROOT = new User("root", "root", "root");
    
    private final String username;
    private final String homeDir;
    private final String group;

    public User(String username, String homeDir, String group) {
        this.username = username;
        this.homeDir = homeDir;
        this.group = group;
    }

    public boolean isRoot() { return this == ROOT; }
    public String getUsername() { return username; }
    public String getHomeDir() { return homeDir; }
    public String getGroup() { return group; }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle symbolic links?"
> "Add SymbolicLink class that points to target path. When resolving, follow the link. Detect cycles by tracking visited paths."

### Q2: "How to support hard links?"
> "Multiple directory entries point to same inode. Reference count on inode. File only deleted when refcount = 0."

### Q3: "How to handle concurrent access?"
> "File-level locks. Read lock: multiple readers allowed. Write lock: exclusive. Use ReadWriteLock."

### Q4: "How to implement file permissions (ACLs)?"
> "Replace simple mode bits with AccessControlList. ACL = list of (user, permission) tuples. Check user's entry first, then group, then others."
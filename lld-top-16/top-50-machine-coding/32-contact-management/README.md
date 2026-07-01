# 👤 Problem 32: Contact Management System

> **Difficulty**: ⭐⭐ | **Company Fit**: Google Contacts, Outlook Contacts  
> **Est. Time**: 60 min | **Patterns**: Trie, Observer, Factory

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a contact management system."

**What the interviewer tests**:
```
1. Can you search contacts fast? (By name prefix, phone, email)
2. Can you handle duplicates? (Same person, multiple sources)
3. Can you organize contacts? (Groups, favorites, labels)
4. Can you handle syncing? (Phone, Gmail, Outlook)
```

### Step 2: The "Aha!" Moment

The key insight: **Multiple indexes for different search modes.**

```
Index 1: Trie by name (prefix search: "jo" → "John", "Joe", "Jordan")
Index 2: HashMap by phone (exact search: "+1-555-1234" → John)
Index 3: HashMap by email (exact search: "john@example.com" → John)

Search "jo" on phone? → HashMap doesn't help. Use Trie.
Search "+1-555-1234"? → Trie doesn't help. Use HashMap.
```

### Step 3: How to handle duplicates?

```
Contact matching (merge strategy):
  1. Same phone number → MERGE (keep both)
  2. Same email → MERGE
  3. Same name + birthday → PROBABLE MATCH (ask user)
  4. Different sources (phone + Gmail) → SYNC

When merging:
  - Combine phone numbers from both
  - Combine emails from both
  - Keep most recent name
  - Mark as merged (source: multiple)
```

---

## 💻 Core Implementation

```java
package com.contacts;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: ContactService manages all contacts.
 * 
 * Maintains multiple indexes for fast search:
 * - Trie for name prefix search
 * - HashMap for phone/email exact match
 * - HashMap for ID-based lookup
 */
public class ContactService {
    
    // Primary storage: contactId → Contact
    private final Map<String, Contact> contacts;
    
    // Index 1: Trie for name search (prefix matching)
    private final ContactTrie nameIndex;
    
    // Index 2: Phone → contactId (exact match)
    private final Map<String, String> phoneIndex;
    
    // Index 3: Email → contactId (exact match)
    private final Map<String, String> emailIndex;
    
    // Groups: groupId → Group
    private final Map<String, Group> groups;

    public ContactService() {
        this.contacts = new ConcurrentHashMap<>();
        this.nameIndex = new ContactTrie();
        this.phoneIndex = new ConcurrentHashMap<>();
        this.emailIndex = new ConcurrentHashMap<>();
        this.groups = new ConcurrentHashMap<>();
    }

    /**
     * INTUITION: Add a new contact.
     * 
     * 1. Create contact
     * 2. Index by name (Trie)
     * 3. Index by phone
     * 4. Index by email
     */
    public synchronized Contact addContact(String firstName, String lastName,
                                           String phone, String email) {
        Contact contact = new Contact(firstName, lastName, phone, email);
        
        // Store
        contacts.put(contact.getId(), contact);
        
        // Index by name
        nameIndex.insert(contact);
        
        // Index by phone and email
        if (phone != null) phoneIndex.put(phone, contact.getId());
        if (email != null) emailIndex.put(email, contact.getId());
        
        return contact;
    }

    /**
     * INTUITION: Search by name prefix.
     * 
     * "jo" → matches "John", "Joe", "Joseph"
     * O(L) where L = length of prefix
     */
    public List<Contact> searchByName(String prefix) {
        return nameIndex.search(prefix);
    }

    /**
     * INTUITION: Search by phone.
     * 
     * O(1) exact match.
     */
    public Contact findByPhone(String phone) {
        String contactId = phoneIndex.get(phone);
        return contactId != null ? contacts.get(contactId) : null;
    }

    /**
     * INTUITION: Search by email.
     * 
     * O(1) exact match.
     */
    public Contact findByEmail(String email) {
        String contactId = emailIndex.get(email);
        return contactId != null ? contacts.get(contactId) : null;
    }

    /**
     * INTUITION: Merge two contacts (deduplication).
     * 
     * Same person found in phone AND Gmail.
     * Merge into single contact.
     */
    public Contact mergeContacts(String contactId1, String contactId2) {
        Contact c1 = contacts.get(contactId1);
        Contact c2 = contacts.get(contactId2);
        
        if (c1 == null || c2 == null) return null;
        
        // Create merged contact
        Contact merged = new Contact(
            c1.getFirstName(), c2.getLastName(),
            mergePhones(c1, c2),
            mergeEmails(c1, c2)
        );
        
        // Remove old contacts
        contacts.remove(contactId1);
        contacts.remove(contactId2);
        
        // Add merged
        contacts.put(merged.getId(), merged);
        
        // Rebuild indexes
        rebuildIndexes(merged);
        
        return merged;
    }

    /**
     * INTUITION: Add contact to group.
     */
    public void addToGroup(String contactId, String groupId) {
        Group group = groups.computeIfAbsent(groupId, 
            k -> new Group(groupId, "Group " + k));
        group.addContact(contactId);
    }

    /**
     * Get all contacts in a group.
     */
    public List<Contact> getGroupContacts(String groupId) {
        Group group = groups.get(groupId);
        if (group == null) return Collections.emptyList();
        
        List<Contact> result = new ArrayList<>();
        for (String contactId : group.getContactIds()) {
            Contact contact = contacts.get(contactId);
            if (contact != null) result.add(contact);
        }
        return result;
    }

    public Contact getContact(String contactId) {
        return contacts.get(contactId);
    }

    public void deleteContact(String contactId) {
        Contact contact = contacts.remove(contactId);
        if (contact != null) {
            // Remove from indexes
            nameIndex.remove(contact);
            if (contact.getPhone() != null) phoneIndex.remove(contact.getPhone());
            if (contact.getEmail() != null) emailIndex.remove(contact.getEmail());
        }
    }

    // --- Helpers ---

    private List<String> mergePhones(Contact c1, Contact c2) {
        Set<String> phones = new HashSet<>();
        if (c1.getPhone() != null) phones.add(c1.getPhone());
        if (c2.getPhone() != null) phones.add(c2.getPhone());
        return new ArrayList<>(phones);
    }

    private List<String> mergeEmails(Contact c1, Contact c2) {
        Set<String> emails = new HashSet<>();
        if (c1.getEmail() != null) emails.add(c1.getEmail());
        if (c2.getEmail() != null) emails.add(c2.getEmail());
        return new ArrayList<>(emails);
    }

    private void rebuildIndexes(Contact contact) {
        nameIndex.insert(contact);
        if (contact.getPhone() != null) phoneIndex.put(contact.getPhone(), contact.getId());
        if (contact.getEmail() != null) emailIndex.put(contact.getEmail(), contact.getId());
    }
}
```

```java
package com.contacts;

import java.util.*;

/**
 * INTUITION: Trie for fast name prefix search.
 * 
 * Each node is a character. Paths form names.
 * 
 * Example for "John", "Joe", "Joseph":
 * 
 *        [root]
 *         |
 *         J
 *         |
 *     o---+---e
 *     |       |
 *     h       o
 *     |       |
 *     n       S
 *             |
 *             e
 *             |
 *             p
 *             |
 *             h
 * 
 * Search "Jo": Traverse J → o, return all descendants.
 */
class ContactTrie {
    
    private final TrieNode root;

    ContactTrie() {
        this.root = new TrieNode();
    }

    /**
     * Insert contact into trie (index by first + last name).
     */
    void insert(Contact contact) {
        insertName(root, contact.getFirstName() + " " + contact.getLastName(), 
                   contact.getId());
        insertName(root, contact.getLastName() + ", " + contact.getFirstName(), 
                   contact.getId());
    }

    private void insertName(TrieNode node, String name, String contactId) {
        TrieNode current = node;
        
        for (char c : name.toLowerCase().toCharArray()) {
            current = current.children.computeIfAbsent(c, k -> new TrieNode());
        }
        
        current.contactIds.add(contactId);
    }

    /**
     * Search contacts by name prefix.
     * 
     * "jo" → ["John Doe", "Joe Smith", "Joseph Brown"]
     */
    List<Contact> search(String prefix) {
        TrieNode node = root;
        
        // Navigate to prefix node
        for (char c : prefix.toLowerCase().toCharArray()) {
            node = node.children.get(c);
            if (node == null) {
                return Collections.emptyList();
            }
        }
        
        // Collect all contactIds from this node and descendants
        Set<String> resultIds = new HashSet<>();
        collectContactIds(node, resultIds);
        
        return resultIds.stream()
            .map(id -> null)  // Would lookup in contacts map
            .collect(Collectors.toList());
    }

    private void collectContactIds(TrieNode node, Set<String> resultIds) {
        resultIds.addAll(node.contactIds);
        for (TrieNode child : node.children.values()) {
            collectContactIds(child, resultIds);
        }
    }

    void remove(Contact contact) {
        // Simplified: in production, remove from trie nodes
    }

    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        Set<String> contactIds = new HashSet<>();
    }
}
```

```java
package com.contacts;

import java.util.*;

/**
 * INTUITION: Contact is the central entity.
 * 
 * Contains:
 * - Personal info (name, birthday)
 * - Contact methods (phone, email, address)
 * - Metadata (source: phone/Gmail, last updated, favorite)
 */
public class Contact {
    private final String id;
    private String firstName;
    private String lastName;
    private String phone;
    private final List<String> emails;
    private String birthday;
    private String company;
    private String notes;
    private final Set<String> sources;  // phone, gmail, outlook
    private boolean favorite;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Contact(String firstName, String lastName, String phone, String email) {
        this.id = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.emails = new ArrayList<>();
        if (email != null) this.emails.add(email);
        this.sources = new HashSet<>();
        this.sources.add("manual");
        this.favorite = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void addEmail(String email) {
        if (!emails.contains(email)) {
            emails.add(email);
            updatedAt = LocalDateTime.now();
        }
    }

    public void addPhone(String phone) {
        this.phone = phone;
        updatedAt = LocalDateTime.now();
    }

    // Getters and setters
    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { 
        this.firstName = firstName; 
        this.updatedAt = LocalDateTime.now();
    }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { 
        this.lastName = lastName; 
        this.updatedAt = LocalDateTime.now();
    }
    public String getPhone() { return phone; }
    public List<String> getEmails() { return Collections.unmodifiableList(emails); }
    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
    public String getDisplayName() { 
        return firstName + " " + lastName; 
    }
}
```

```java
package com.contacts;

import java.util.*;

/**
 * INTUITION: Group organizes contacts.
 * 
 * Like labels in Gmail.
 * One contact can be in multiple groups.
 */
public class Group {
    private final String id;
    private String name;
    private final Set<String> contactIds;
    private final LocalDateTime createdAt;

    public Group(String id, String name) {
        this.id = id;
        this.name = name;
        this.contactIds = ConcurrentHashMap.newKeySet();
        this.createdAt = LocalDateTime.now();
    }

    public void addContact(String contactId) {
        contactIds.add(contactId);
    }

    public void removeContact(String contactId) {
        contactIds.remove(contactId);
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public Set<String> getContactIds() { return Collections.unmodifiableSet(contactIds); }
    public int getSize() { return contactIds.size(); }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to sync with phone contacts?"
> "Use ContentProvider on Android, CNContactStore on iOS. Two-way sync: track 'dirty' flags and last-modified timestamps. Conflict resolution: keep newest, or ask user."

### Q2: "How to handle international phone numbers?"
> "Parse with libphonenumber (Google library). Store in E.164 format (+14155551234). Display in user's locale format."

### Q3: "How to suggest contact merges?"
> "Fuzzy matching: same name + similar email. Same phone prefix + same last name. Use Levenshtein distance for typos. Ask user to confirm."

### Q4: "How to handle favorites/recent contacts?"
> "Maintain separate ordered list. Update on contact view. Cache top 10 in memory."
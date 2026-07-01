# 📋 Problem 48: Kanban Board (Trello-like)

> **Difficulty**: ⭐⭐ | **Company Fit**: Any SaaS company  
> **Est. Time**: 60 min | **Patterns**: Observer, State, Strategy

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a task management board with columns and cards."

**What the interviewer tests**:
```
1. Can you model a board? (Columns, cards, labels)
2. Can you handle card movement? (Drag between columns)
3. Can you assign tasks? (Users, due dates)
4. Can you track history? (Activity log)
```

### Step 2: The "Aha!" Moment

The key insight: **Board is a state machine for tasks.**

```
BOARD STRUCTURE:
  Board "Project X"
    ├── Column "TODO" (3 cards)
    │     ├── Card: "Design UI" [HIGH] [Alice] [Due: Today]
    │     ├── Card: "Write docs" [MEDIUM] [Bob] [Due: Tomorrow]
    │     └── Card: "Setup CI/CD" [LOW]
    ├── Column "IN_PROGRESS" (1 card)
    │     └── Card: "API dev" [HIGH] [Alice] [Due: Today]
    └── Column "DONE" (2 cards)
          ├── Card: "Project plan" ✓
          └── Card: "Database design" ✓

CARD FLOW: TODO → IN_PROGRESS → DONE
```

### Step 3: How to track activity?

```
ACTIVITY LOG:
  - Who moved card
  - When
  - From where to where
  - Comment added
  
Example:
  2024-06-01 10:30 AM - Alice moved "API dev" from TODO to IN_PROGRESS
  2024-06-01 10:31 AM - Alice added comment "Working on auth"
  2024-06-01 11:00 AM - Bob assigned "Write docs" to Charlie
```

---

## 💻 Core Implementation

```java
package com.kanban;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: KanbanService manages boards, columns, and cards.
 */
public class KanbanService {
    
    private final Map<String, Board> boards;
    private final Map<String, User> users;

    public KanbanService() {
        this.boards = new ConcurrentHashMap<>();
        this.users = new ConcurrentHashMap<>();
    }

    /**
     * INTUITION: Create a new board.
     */
    public synchronized Board createBoard(String name, String ownerId) {
        User owner = users.get(ownerId);
        Board board = new Board(UUID.randomUUID().toString(), name, owner);
        boards.put(board.getId(), board);
        return board;
    }

    /**
     * INTUITION: Add column to board.
     */
    public synchronized Column addColumn(String boardId, String columnName) {
        Board board = boards.get(boardId);
        if (board == null) throw new IllegalArgumentException("Board not found");
        
        Column column = new Column(UUID.randomUUID().toString(), columnName);
        board.addColumn(column);
        return column;
    }

    /**
     * INTUITION: Create card in column.
     */
    public synchronized Card createCard(String columnId, String title, String description) {
        Column column = findColumn(columnId);
        if (column == null) throw new IllegalArgumentException("Column not found");
        
        Card card = new Card(UUID.randomUUID().toString(), title, description);
        column.addCard(card);
        
        // Log activity
        logActivity(card, ActivityType.CREATED, null, columnId);
        
        return card;
    }

    /**
     * INTUITION: Move card between columns.
     */
    public synchronized void moveCard(String cardId, String targetColumnId) {
        Card card = findCard(cardId);
        Column targetColumn = findColumn(targetColumnId);
        if (card == null || targetColumn == null) return;
        
        Column sourceColumn = card.getColumn();
        if (sourceColumn != null) {
            sourceColumn.removeCard(card);
        }
        
        targetColumn.addCard(card);
        card.setColumn(targetColumn);
        
        // Log activity
        logActivity(card, ActivityType.MOVED, sourceColumn, targetColumn);
    }

    /**
     * INTUITION: Assign card to user.
     */
    public synchronized void assignCard(String cardId, String userId) {
        Card card = findCard(cardId);
        User user = users.get(userId);
        if (card == null || user == null) return;
        
        User previousAssignee = card.getAssignee();
        card.setAssignee(user);
        
        // Log activity
        logActivity(card, ActivityType.ASSIGNED, previousAssignee, user);
    }

    /**
     * INTUITION: Add label to card.
     */
    public synchronized void addLabel(String cardId, String labelName, String color) {
        Card card = findCard(cardId);
        if (card != null) {
            card.addLabel(new Label(labelName, color));
            logActivity(card, ActivityType.LABEL_ADDED, null, null);
        }
    }

    /**
     * INTUITION: Add comment to card.
     */
    public synchronized void addComment(String cardId, String userId, String comment) {
        Card card = findCard(cardId);
        User user = users.get(userId);
        if (card == null || user == null) return;
        
        Comment c = new Comment(UUID.randomUUID().toString(), user, comment);
        card.addComment(c);
        
        logActivity(card, ActivityType.COMMENT_ADDED, null, null);
    }

    /**
     * INTUITION: Get board with all data.
     */
    public Board getBoard(String boardId) {
        return boards.get(boardId);
    }

    public User createUser(String name, String email) {
        User user = new User(UUID.randomUUID().toString(), name, email);
        users.put(user.getId(), user);
        return user;
    }

    // --- Helpers ---

    private Card findCard(String cardId) {
        for (Board board : boards.values()) {
            for (Column column : board.getColumns()) {
                Card card = column.getCard(cardId);
                if (card != null) return card;
            }
        }
        return null;
    }

    private Column findColumn(String columnId) {
        for (Board board : boards.values()) {
            for (Column column : board.getColumns()) {
                if (column.getId().equals(columnId)) return column;
            }
        }
        return null;
    }

    private void logActivity(Card card, ActivityType type, Object oldValue, Object newValue) {
        Activity activity = new Activity(card, type, oldValue, newValue);
        card.addActivity(activity);
    }
}

/**
 * Activity log entry.
 */
class Activity {
    private final String activityId;
    private final Card card;
    private final ActivityType type;
    private final Object oldValue;
    private final Object newValue;
    private final LocalDateTime timestamp;

    Activity(Card card, ActivityType type, Object oldValue, Object newValue) {
        this.activityId = UUID.randomUUID().toString();
        this.card = card;
        this.type = type;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public ActivityType getType() { return type; }
    public LocalDateTime getTimestamp() { return timestamp; }
}

enum ActivityType {
    CREATED, MOVED, ASSIGNED, UNASSIGNED, LABEL_ADDED, COMMENT_ADDED, DUE_DATE_SET
}
```

```java
package com.kanban;

import java.time.LocalDateTime;
import java.util.*;

/**
 * INTUITION: Board is a project workspace.
 */
public class Board {
    private final String boardId;
    private String name;
    private final User owner;
    private final List<Column> columns;
    private final LocalDateTime createdAt;

    public Board(String boardId, String name, User owner) {
        this.boardId = boardId;
        this.name = name;
        this.owner = owner;
        this.columns = new CopyOnWriteArrayList<>();
        this.createdAt = LocalDateTime.now();
    }

    void addColumn(Column column) {
        columns.add(column);
    }

    void removeColumn(Column column) {
        columns.remove(column);
    }

    // Getters
    public String getId() { return boardId; }
    public String getName() { return name; }
    public List<Column> getColumns() { return Collections.unmodifiableList(columns); }
}

/**
 * Column (lane) on the board.
 */
class Column {
    private final String columnId;
    private String name;
    private final List<Card> cards;
    private int position;  // Column order

    public Column(String columnId, String name) {
        this.columnId = columnId;
        this.name = name;
        this.cards = new CopyOnWriteArrayList<>();
    }

    void addCard(Card card) {
        cards.add(card);
        card.setColumn(this);
    }

    void removeCard(Card card) {
        cards.remove(card);
    }

    Card getCard(String cardId) {
        return cards.stream()
            .filter(c -> c.getId().equals(cardId))
            .findFirst()
            .orElse(null);
    }

    // Getters
    public String getId() { return columnId; }
    public String getName() { return name; }
    public List<Card> getCards() { return Collections.unmodifiableList(cards); }
}
```

```java
package com.kanban;

import java.time.LocalDateTime;
import java.util.*;

/**
 * INTUITION: Card is a task.
 */
public class Card {
    private final String cardId;
    private String title;
    private String description;
    private Column column;
    private User assignee;
    private final List<Label> labels;
    private final List<Comment> comments;
    private final List<Activity> activities;
    private LocalDateTime dueDate;
    private int position;
    private CardStatus status;
    private final LocalDateTime createdAt;

    public Card(String cardId, String title, String description) {
        this.cardId = cardId;
        this.title = title;
        this.description = description;
        this.labels = new CopyOnWriteArrayList<>();
        this.comments = new CopyOnWriteArrayList<>();
        this.activities = new CopyOnWriteArrayList<>();
        this.status = CardStatus.TODO;
        this.createdAt = LocalDateTime.now();
    }

    void setColumn(Column column) {
        this.column = column;
    }

    void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    void addLabel(Label label) {
        labels.add(label);
    }

    void addComment(Comment comment) {
        comments.add(comment);
    }

    void addActivity(Activity activity) {
        activities.add(activity);
    }

    // Getters
    public String getId() { return cardId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Column getColumn() { return column; }
    public User getAssignee() { return assignee; }
    public List<Label> getLabels() { return labels; }
    public List<Comment> getComments() { return comments; }
    public List<Activity> getActivities() { return activities; }
}

enum CardStatus {
    TODO, IN_PROGRESS, DONE, ARCHIVED
}

class Label {
    private final String name;
    private final String color;

    Label(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() { return name; }
    public String getColor() { return color; }
}

class Comment {
    private final String commentId;
    private final User author;
    private final String text;
    private final LocalDateTime createdAt;

    Comment(String commentId, User author, String text) {
        this.commentId = commentId;
        this.author = author;
        this.text = text;
        this.createdAt = LocalDateTime.now();
    }

    public User getAuthor() { return author; }
    public String getText() { return text; }
}

class User {
    private final String userId;
    private String name;
    private String email;
    private final LocalDateTime createdAt;

    public User(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return userId; }
    public String getName() { return name; }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle concurrent edits?"
> "Optimistic locking: version number per card. Last write wins. Conflict detection on move."

### Q2: "How to implement swimlanes (horizontal grouping)?"
> "Add Lane entity above columns. Each card belongs to a lane (e.g., by feature)."

### Q3: "How to handle due date reminders?"
> "Scheduled job checks due dates. Email/Push 1 day before. Overdue: red highlight + daily alerts."

### Q4: "How to support power-ups (integrations)?"
> "Plugin architecture. Webhook on card move. Slack/GitHub/Jira sync."
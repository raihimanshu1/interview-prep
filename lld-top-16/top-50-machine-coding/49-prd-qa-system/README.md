# 💬 Problem 49: PRD Question Answering System (Like Intercom/Crisp)

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: SaaS, support tools  
> **Est. Time**: 90 min | **Patterns**: NLP, Vector Search, Observer

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Answer user questions from a product requirements document."

**What the interviewer tests**:
```
1. Can you chunk a document? (Split into sections)
2. Can you search relevant sections? (Similarity matching)
3. Can you generate answers? (Extractive or abstractive)
4. Can you handle follow-up questions? (Context tracking)
```

### Step 2: The "Aha!" Moment

The key insight: **RAG = Retrieve + Generate.**

```
USER: "What is the refund policy?"
SYSTEM:
  1. Convert query to vector
  2. Search document for similar sections
  3. Return top 3 matches
  4. Generate answer from context

VECTOR SEARCH:
  Document sections → Embeddings → Vector DB
  
  Query: "refund policy" → [0.1, 0.2, ..., 0.9]
  
  Find top 3 most similar sections:
    - Section 4.2: "Refunds are processed within 7 days"
    - Section 7.1: "No refund after 30 days"
    - Section 9.3: "Partial refunds for unused portion"
```

### Step 3: How to handle conversation context?

```
CONVERSATION:
  User: "What is the refund policy?"
  Bot: "Refunds are processed within 7 days (Section 4.2)"
  
  User: "And for international orders?"
  Bot: "International orders take 14 days (Section 4.3)"
  
CONTEXT:
  - Previous question: "refund policy"
  - Current question: "And for international orders?"
  - Combined: "refund policy AND international orders"
  
HANDOFF:
  If confidence < threshold: "Let me connect you to a human"
```

---

## 💻 Core Implementation

```java
package com.prd;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: PRDQuestionAnswering answers from a document.
 * 
 * Uses RAG (Retrieval-Augmented Generation):
 * 1. Chunk document into sections
 * 2. Embed sections into vectors
 * 3. On query, find similar sections
 * 4. Return answer with citation
 */
public class PRDQuestionAnswering {
    
    private final DocumentStore documentStore;
    private final EmbeddingService embeddingService;
    private final LLMService llmService;
    private final ConversationTracker conversationTracker;

    public PRDQuestionAnswering() {
        this.documentStore = new DocumentStore();
        this.embeddingService = new EmbeddingService();
        this.llmService = new LLMService();
        this.conversationTracker = new ConversationTracker();
    }

    /**
     * INTUITION: Load PRD document.
     */
    public synchronized void loadDocument(PRDDocument document) {
        // Step 1: Chunk document
        List<DocumentSection> sections = chunkDocument(document);
        
        // Step 2: Generate embeddings
        for (DocumentSection section : sections) {
            double[] embedding = embeddingService.embed(section.getContent());
            section.setEmbedding(embedding);
            documentStore.addSection(section);
        }
        
        System.out.println("Loaded " + sections.size() + " sections from PRD");
    }

    /**
     * INTUITION: Answer user question.
     */
    public synchronized Answer askQuestion(String sessionId, String question) {
        Conversation conversation = conversationTracker.getOrCreate(sessionId);
        
        // Step 1: Enhance question with context
        String enhancedQuestion = enhanceWithContext(conversation, question);
        
        // Step 2: Embed question
        double[] questionEmbedding = embeddingService.embed(enhancedQuestion);
        
        // Step 3: Find relevant sections
        List<DocumentSection> relevantSections = documentStore.search(questionEmbedding, 3);
        
        // Step 4: Calculate confidence
        double confidence = relevantSections.isEmpty() ? 0.0 : relevantSections.get(0).getScore();
        
        // Step 5: Generate answer
        String answer;
        if (confidence < CONFIDENCE_THRESHOLD) {
            answer = generateFallbackAnswer(question);
        } else {
            answer = llmService.generateAnswer(question, relevantSections);
        }
        
        // Step 6: Update conversation context
        conversation.addTurn(question, answer);
        
        // Step 7: Build response
        Answer result = new Answer(answer, confidence, relevantSections);
        
        // Step 8: Check if handoff needed
        if (confidence < HANDOFF_THRESHOLD) {
            result.setHandoffSuggested(true);
            result.setSuggestedAgent("support");
        }
        
        return result;
    }

    /**
     * INTUITION: Enhance question with conversation context.
     */
    private String enhanceWithContext(Conversation conversation, String question) {
        if (conversation.getHistory().isEmpty()) {
            return question;
        }
        
        // Add last 2 turns for context
        StringBuilder enhanced = new StringBuilder(question);
        List<QAHistory> history = conversation.getRecentHistory(2);
        
        for (QAHistory qa : history) {
            enhanced.append(" [Context: ").append(qa.getQuestion()).append("]");
        }
        
        return enhanced.toString();
    }

    private String generateFallbackAnswer(String question) {
        return "I don't have information about that. Let me connect you to a human agent.";
    }

    public List<DocumentSection> getDocumentSections() {
        return documentStore.getAllSections();
    }

    private static final double CONFIDENCE_THRESHOLD = 0.7;
    private static final double HANDOFF_THRESHOLD = 0.5;
}

/**
 * Answer with citations.
 */
class Answer {
    private final String answer;
    private final double confidence;
    private final List<DocumentSection> sources;
    private boolean handoffSuggested;
    private String suggestedAgent;

    Answer(String answer, double confidence, List<DocumentSection> sources) {
        this.answer = answer;
        this.confidence = confidence;
        this.sources = sources;
        this.handoffSuggested = false;
    }

    public String getAnswer() { return answer; }
    public double getConfidence() { return confidence; }
    public List<DocumentSection> getSources() { return sources; }
    
    public boolean isHandoffSuggested() { return handoffSuggested; }
    public void setHandoffSuggested(boolean suggested) { this.handoffSuggested = suggested; }
    public String getSuggestedAgent() { return suggestedAgent; }
    public void setSuggestedAgent(String agent) { this.suggestedAgent = agent; }
}
```

```java
package com.prd;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * INTUITION: DocumentStore indexes document sections by embedding.
 * 
 * Vector search: find most similar sections.
 */
class DocumentStore {
    
    // sectionId → section
    private final Map<String, DocumentSection> sections;
    
    // Inverted index: keyword → sectionIds
    private final Map<String, Set<String>> keywordIndex;

    DocumentStore() {
        this.sections = new ConcurrentHashMap<>();
        this.keywordIndex = new ConcurrentHashMap<>();
    }

    void addSection(DocumentSection section) {
        sections.put(section.getId(), section);
        
        // Index keywords
        for (String keyword : extractKeywords(section.getContent())) {
            keywordIndex.computeIfAbsent(keyword, k -> ConcurrentHashMap.newKeySet())
                       .add(section.getId());
        }
    }

    /**
     * INTUITION: Search for similar sections.
     * 
     * Uses cosine similarity on embeddings.
     * In production: use Pinecone, Weaviate, or Qdrant.
     */
    List<DocumentSection> search(double[] queryEmbedding, int topK) {
        List<DocumentSection> all = new ArrayList<>(sections.values());
        
        // Calculate similarity scores
        for (DocumentSection section : all) {
            double score = cosineSimilarity(queryEmbedding, section.getEmbedding());
            section.setScore(score);
        }
        
        // Sort by score and return top K
        all.sort(Comparator.comparingDouble(DocumentSection::getScore).reversed());
        
        return all.subList(0, Math.min(topK, all.size()));
    }

    /**
     * Cosine similarity between two vectors.
     */
    private double cosineSimilarity(double[] a, double[] b) {
        if (a == null || b == null || a.length != b.length) return 0.0;
        
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        
        if (normA == 0 || normB == 0) return 0.0;
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    List<DocumentSection> getAllSections() {
        return new ArrayList<>(sections.values());
    }

    private Set<String> extractKeywords(String text) {
        // Simplified: split by spaces
        return new HashSet<>(Arrays.asList(text.toLowerCase().split("\\s+")));
    }
}

/**
 * Section of the PRD document.
 */
class DocumentSection {
    private final String id;
    private final String sectionId;  // e.g., "4.2"
    private final String title;
    private final String content;
    private double[] embedding;
    private double score;

    DocumentSection(String sectionId, String title, String content) {
        this.id = UUID.randomUUID().toString();
        this.sectionId = sectionId;
        this.title = title;
        this.content = content;
    }

    // Getters
    public String getId() { return id; }
    public String getSectionId() { return sectionId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public double[] getEmbedding() { return embedding; }
    public double getScore() { return score; }

    public void setEmbedding(double[] embedding) { this.embedding = embedding; }
    public void setScore(double score) { this.score = score; }
}
```

```java
package com.prd;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * INTUITION: PRDDocument represents a product requirements doc.
 * 
 * Contains sections with IDs, titles, and content.
 */
public class PRDDocument {
    private final String documentId;
    private String title;
    private String version;
    private final List<DocumentSection> sections;
    private final LocalDateTime createdAt;

    public PRDDocument(String title, String version) {
        this.documentId = UUID.randomUUID().toString();
        this.title = title;
        this.version = version;
        this.sections = new CopyOnWriteArrayList<>();
        this.createdAt = LocalDateTime.now();
    }

    public void addSection(String sectionId, String title, String content) {
        DocumentSection section = new DocumentSection(sectionId, title, content);
        sections.add(section);
    }

    public List<DocumentSection> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public String getTitle() { return title; }
    public String getDocumentId() { return documentId; }
}
```

```java
package com.prd;

import java.util.*;

/**
 * INTUITION: EmbeddingService creates vector representations.
 * 
 * In production: use OpenAI embeddings, HuggingFace, or Cohere.
 */
class EmbeddingService {
    
    private static final int EMBEDDING_DIM = 384;  // Vector size
    
    /**
     * Generate embedding for text.
     * Simplified: returns random vector.
     * In production: call embedding API.
     */
    double[] embed(String text) {
        // Simplified: hash-based embedding
        double[] embedding = new double[EMBEDDING_DIM];
        
        for (int i = 0; i < EMBEDDING_DIM; i++) {
            // Pseudo-random based on text hash
            embedding[i] = Math.sin(text.hashCode() + i) * 0.5 + 0.5;
        }
        
        // Normalize
        double norm = 0.0;
        for (double v : embedding) norm += v * v;
        norm = Math.sqrt(norm);
        
        for (int i = 0; i < EMBEDDING_DIM; i++) {
            embedding[i] /= norm;
        }
        
        return embedding;
    }
}
```

```java
package com.prd;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: LLMService generates answers from context.
 * 
 * In production: call OpenAI GPT, Anthropic Claude, etc.
 */
class LLMService {
    
    /**
     * Generate answer from question and relevant sections.
     */
    String generateAnswer(String question, List<DocumentSection> sections) {
        // Build prompt
        StringBuilder context = new StringBuilder();
        for (DocumentSection section : sections) {
            context.append("[").append(section.getSectionId())
                   .append("] ").append(section.getContent()).append("\n\n");
        }
        
        String prompt = "Based on the following PRD sections:\n\n" +
                       context.toString() +
                       "Answer the question: " + question;
        
        // In production: call LLM API
        return generateMockAnswer(question, sections);
    }

    private String generateMockAnswer(String question, List<DocumentSection> sections) {
        if (sections.isEmpty()) {
            return "I don't have information about that.";
        }
        
        StringBuilder answer = new StringBuilder();
        answer.append("Based on the PRD: ");
        
        DocumentSection top = sections.get(0);
        answer.append(top.getContent());
        
        if (sections.size() > 1) {
            answer.append("\n\nRelated: ").append(sections.get(1).getTitle());
        }
        
        return answer.toString();
    }
}
```

```java
package com.prd;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: ConversationTracker maintains chat context.
 */
class ConversationTracker {
    
    private final Map<String, Conversation> conversations;
    private final long SESSION_TIMEOUT_MS = 30 * 60 * 1000;  // 30 minutes

    ConversationTracker() {
        this.conversations = new ConcurrentHashMap<>();
    }

    Conversation getOrCreate(String sessionId) {
        return conversations.computeIfAbsent(sessionId, k -> new Conversation(k));
    }

    void endSession(String sessionId) {
        conversations.remove(sessionId);
    }
}

class Conversation {
    private final String sessionId;
    private final List<QAHistory> history;
    private final LocalDateTime createdAt;

    Conversation(String sessionId) {
        this.sessionId = sessionId;
        this.history = new CopyOnWriteArrayList<>();
        this.createdAt = LocalDateTime.now();
    }

    void addTurn(String question, String answer) {
        history.add(new QAHistory(question, answer, LocalDateTime.now()));
    }

    List<QAHistory> getRecentHistory(int n) {
        int size = history.size();
        return history.subList(Math.max(0, size - n), size);
    }

    public List<QAHistory> getHistory() {
        return Collections.unmodifiableList(history);
    }
}

class QAHistory {
    private final String question;
    private final String answer;
    private final LocalDateTime timestamp;

    QAHistory(String question, String answer, LocalDateTime timestamp) {
        this.question = question;
        this.answer = answer;
        this.timestamp = timestamp;
    }

    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle multi-language PRDs?"
> "Translate query to English, search, translate back. Or multilingual embeddings (mBERT)."

### Q2: "How to update when PRD changes?"
> "Version PRDs. Re-index on change. Incremental updates. Invalidate cache."

### Q3: "How to handle tables and diagrams?"
> "OCR for images. Parse tables to text. Describe diagrams in alt-text."

### Q4: "How to measure answer quality?"
> "Ground truth testing. Human evaluation. User feedback (thumbs up/down)."
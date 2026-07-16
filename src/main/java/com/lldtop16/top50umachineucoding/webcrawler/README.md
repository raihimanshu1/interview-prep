# 🕷️ Problem 61: Web Crawler (Like Google Crawler)

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: Search engines, data companies  
> **Est. Time**: 90 min | **Patterns**: Producer-Consumer, Observer, BFS

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a web crawler to fetch and index web pages."

**What the interviewer tests**:
```
1. Can you crawl the web? (Fetch pages)
2. Can you avoid duplicates? (URL deduplication)
3. Can you respect politeness? (Rate limiting per domain)
4. Can you handle scale? (Millions of pages)
```

### Step 2: The "Aha!" Moment

The key insight: **Crawler = BFS graph traversal with politeness.**

```
CRAWL FLOW:
  1. Start with seed URLs
  2. Fetch page
  3. Extract links
  4. Add to queue
  5. Repeat
  
                              → [wikipedia.org]
                             /
  [seed] → [google.com] → [github.com]
                             \
                              → [stackoverflow.com]
  
POLITENESS:
  - robots.txt compliance
  - Delay between requests to same domain
  - User-Agent header
```

### Step 3: How to handle scale?

```
DISTRIBUTED CRAWLING:
  - Multiple crawler nodes
  - Shared URL frontier (queue)
  - Partition by domain
  
STORAGE:
  - URL frontier: PriorityQueue
  - Visited URLs: Bloom filter + DB
  - Content: Distributed file system (HDFS)
```

---

## 💻 Core Implementation

```java
package com.crawler;

import java.util.*;
import java.util.concurrent.*;
import java.net.*;
import java.io.*;

/**
 * INTUITION: WebCrawler crawls the web using BFS.
 */
public class WebCrawler {
    
    private final BlockingQueue<String> urlQueue;
    private final Set<String> visited;
    private final Map<String, Integer> domainDelays;
    private final int maxDepth;
    private final int maxPages;
    private final ExecutorService executor;
    private volatile boolean isRunning;

    public WebCrawler(int maxDepth, int maxPages, int threadCount) {
        this.urlQueue = new LinkedBlockingQueue<>();
        this.visited = ConcurrentHashMap.newKeySet();
        this.domainDelays = new ConcurrentHashMap<>();
        this.maxDepth = maxDepth;
        this.maxPages = maxPages;
        this.executor = Executors.newFixedThreadPool(threadCount);
        this.isRunning = true;
    }

    /**
     * INTUITION: Start crawling from seed URLs.
     */
    public void crawl(List<String> seedUrls) {
        // Add seed URLs
        for (String url : seedUrls) {
            urlQueue.offer(url + ":0");  // URL:depth
        }
        
        // Start worker threads
        int workerCount = 10;
        for (int i = 0; i < workerCount; i++) {
            executor.submit(this::crawlWorker);
        }
        
        // Wait for completion
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Crawl worker thread.
     */
    private void crawlWorker() {
        while (isRunning) {
            try {
                String urlWithDepth = urlQueue.poll(1, TimeUnit.SECONDS);
                if (urlWithDepth == null) continue;
                
                String[] parts = urlWithDepth.split(":");
                String url = parts[0];
                int depth = Integer.parseInt(parts[1]);
                
                // Check if already visited
                if (visited.contains(url)) continue;
                
                // Mark as visited
                visited.add(url);
                
                // Check depth
                if (depth > maxDepth) continue;
                
                // Check page limit
                if (visited.size() > maxPages) {
                    isRunning = false;
                    break;
                }
                
                // Respect politeness
                String domain = getDomain(url);
                respectPoliteness(domain);
                
                // Fetch page
                System.out.println("Crawling: " + url);
                String content = fetchPage(url);
                
                // Index page (in production: store in DB)
                indexPage(url, content);
                
                // Extract links if not at max depth
                if (depth < maxDepth) {
                    List<String> links = extractLinks(content, url);
                    for (String link : links) {
                        urlQueue.offer(link + ":" + (depth + 1));
                    }
                }
                
            } catch (Exception e) {
                System.err.println("Crawl error: " + e.getMessage());
            }
        }
    }

    /**
     * Fetch page content.
     */
    private String fetchPage(String url) {
        try {
            // Respect robots.txt
            if (!isAllowedByRobots(url)) {
                return "";
            }
            
            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MyCrawler/1.0)");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
            );
            
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();
            
            return content.toString();
            
        } catch (Exception e) {
            System.err.println("Failed to fetch " + url + ": " + e.getMessage());
            return "";
        }
    }

    /**
     * Extract links from HTML.
     */
    private List<String> extractLinks(String html, String baseUrl) {
        List<String> links = new ArrayList<>();
        
        // Simple regex for href extraction
        String hrefPattern = "href=[\"'](.*?)[\"']";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(hrefPattern);
        java.util.regex.Matcher matcher = pattern.matcher(html);
        
        while (matcher.find()) {
            String link = matcher.group(1);
            
            // Convert relative to absolute URL
            try {
                link = new URL(new URL(baseUrl), link).toString();
                links.add(link);
            } catch (MalformedURLException e) {
                // Skip invalid URLs
            }
        }
        
        return links;
    }

    /**
     * Check robots.txt.
     */
    private boolean isAllowedByRobots(String url) {
        // Simplified: always allow
        return true;
    }

    /**
     * Respect politeness delay for domain.
     */
    private void respectPoliteness(String domain) {
        Integer lastCrawlTime = domainDelays.get(domain);
        int delay = lastCrawlTime != null ? lastCrawlTime : 0;
        
        if (delay > 0) {
            long elapsed = System.currentTimeMillis() - delay;
            if (elapsed < 1000) {  // 1 second between requests
                try {
                    Thread.sleep(1000 - elapsed);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        domainDelays.put(domain, (int) System.currentTimeMillis());
    }

    /**
     * Index page content.
     */
    private void indexPage(String url, String content) {
        // In production: store in database
        System.out.println("Indexed: " + url + " (" + content.length() + " chars)");
    }

    /**
     * Get domain from URL.
     */
    private String getDomain(String url) {
        try {
            URL urlObj = new URL(url);
            return urlObj.getHost();
        } catch (MalformedURLException e) {
            return "";
        }
    }

    public int getVisitedCount() {
        return visited.size();
    }

    public void stop() {
        isRunning = false;
        executor.shutdownNow();
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle infinite crawling?"
> "URL normalization. Canonical URLs. Depth limit. Duplicate detection."

### Q2: "How to handle dynamic content?"
> "Headless browser (Selenium). JavaScript rendering. Wait for load."

### Q3: "How to scale to billions of URLs?"
> "Distributed frontier. Consistent hashing. Sharding by domain."

### Q4: "How to handle malicious sites?"
> "Content scanning. Malware detection. Safe browsing API."
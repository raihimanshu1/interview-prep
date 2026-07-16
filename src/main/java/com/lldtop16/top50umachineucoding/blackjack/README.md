# 🃏 Problem 50: Design a Blackjack Game

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: Gaming companies, Any tech company  
> **Est. Time**: 90 min | **Patterns**: State Machine, Strategy, Observer

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design the card game Blackjack (21)."

**What the interviewer tests**:
```
1. Can you model a deck of cards? (Shuffle, deal)
2. Can you model hand values? (Ace = 1 or 11)
3. Can you implement game rules? (Hit, stand, bust)
4. Can you handle multiple players? (Dealer + N players)
```

### Step 2: The "Aha!" Moment

The key insight: **Blackjack is a state machine with score calculation.**

```
GAME FLOW:
  [BETTING] → [DEALING] → [PLAYER_TURN] → [DEALER_TURN] → [SETTLEMENT]

HAND VALUE:
  Cards: [A♠, 8♥, K♣]
  Values: 11 + 8 + 10 = 29 (bust!)
  
  Cards: [A♠, 8♥, 3♦]
  Values: 1 + 8 + 3 = 14 (Ace as 1)
  OR: 11 + 8 + 3 = 22 (bust)
  → Choose 14

CARDS:
  Suits: ♠ ♥ ♣ ♦
  Ranks: A, 2-10, J, Q, K
  Values: A=1/11, 2-9=face, 10/J/Q/K=10

DEALER RULES:
  - Must hit until 17 or higher
  - Must stand on 17+
```

### Step 3: How to handle deck management?

```
DECK:
  - 52 cards (no jokers)
  - Shuffle before each game
  - Reshuffle when < 20% remaining

SHOE (Multiple decks):
  - Casino uses 6-8 decks
  - Continuous shuffle
  - Card counting prevention

BURN CARD:
  - Burn top card after shuffle
  - Prevents first-card advantage
```

---

## 💻 Core Implementation

```java
package com.blackjack;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: BlackjackGame manages the entire game.
 * 
 * State machine:
 * BETTING → DEALING → PLAYER_TURN → DEALER_TURN → SETTLEMENT
 */
public class BlackjackGame {
    
    public enum GameState {
        BETTING, DEALING, PLAYER_TURN, DEALER_TURN, SETTLEMENT, FINISHED
    }
    
    private final Deck deck;
    private final Dealer dealer;
    private final List<Player> players;
    private final List<Hand> hands;  // Active hands
    
    private GameState state;
    private int currentPlayerIndex;
    private final int deckCount;  // Number of decks in shoe

    public BlackjackGame() {
        this(1);  // Single deck by default
    }

    public BlackjackGame(int deckCount) {
        this.deckCount = deckCount;
        this.deck = new Deck(deckCount);
        this.deck.shuffle();
        this.dealer = new Dealer();
        this.players = new CopyOnWriteArrayList<>();
        this.hands = new CopyOnWriteArrayList<>();
        this.state = GameState.BETTING;
    }

    /**
     * INTUITION: Add player to game.
     */
    public synchronized void addPlayer(Player player) {
        if (state != GameState.BETTING) {
            throw new IllegalStateException("Cannot join game in progress");
        }
        players.add(player);
    }

    /**
     * INTUITION: Player places bet.
     */
    public synchronized boolean placeBet(String playerId, double amount) {
        if (state != GameState.BETTING) {
            return false;
        }
        
        Player player = findPlayer(playerId);
        if (player == null || player.getBalance() < amount) {
            return false;
        }
        
        player.placeBet(amount);
        return true;
    }

    /**
     * INTUITION: Start dealing cards.
     */
    public synchronized void deal() {
        if (state != GameState.BETTING) {
            return;
        }
        
        state = GameState.DEALING;
        
        // Create hands for all players
        for (Player player : players) {
            Hand hand = new Hand(player);
            hands.add(hand);
        }
        Hand dealerHand = new Hand(dealer);
        hands.add(dealerHand);
        
        // Deal 2 cards to each
        for (int i = 0; i < 2; i++) {
            for (Hand hand : hands) {
                hand.addCard(deck.deal());
            }
        }
        
        // Check for blackjack
        for (Hand hand : hands) {
            if (hand.isBlackjack()) {
                hand.setStatus(HandStatus.BLACKJACK);
            }
        }
        
        // Next: player turns
        state = GameState.PLAYER_TURN;
        currentPlayerIndex = 0;
    }

    /**
     * INTUITION: Player hits (takes another card).
     */
    public synchronized void hit(String playerId) {
        if (state != GameState.PLAYER_TURN) {
            return;
        }
        
        Hand currentHand = getCurrentHand();
        if (currentHand == null || !currentHand.getPlayer().getId().equals(playerId)) {
            return;
        }
        
        currentHand.addCard(deck.deal());
        
        // Check if bust
        if (currentHand.getValue() > 21) {
            currentHand.setStatus(HandStatus.BUST);
            nextPlayer();
        }
    }

    /**
     * INTUITION: Player stands (keeps current hand).
     */
    public synchronized void stand(String playerId) {
        if (state != GameState.PLAYER_TURN) {
            return;
        }
        
        Hand currentHand = getCurrentHand();
        if (currentHand == null || !currentHand.getPlayer().getId().equals(playerId)) {
            return;
        }
        
        currentHand.setStatus(HandStatus.STAND);
        nextPlayer();
    }

    /**
     * INTUITION: Player doubles down (double bet, one more card).
     */
    public synchronized void doubleDown(String playerId) {
        if (state != GameState.PLAYER_TURN) {
            return;
        }
        
        Hand currentHand = getCurrentHand();
        Player player = currentHand.getPlayer();
        
        if (player.getBalance() < player.getCurrentBet()) {
            return;  // Not enough balance
        }
        
        // Double the bet
        player.doubleDown();
        
        // Deal one more card
        currentHand.addCard(deck.deal());
        
        // Must stand after double down
        if (currentHand.getValue() > 21) {
            currentHand.setStatus(HandStatus.BUST);
        } else {
            currentHand.setStatus(HandStatus.STAND);
        }
        
        nextPlayer();
    }

    /**
     * INTUITION: Move to next player.
     */
    private void nextPlayer() {
        currentPlayerIndex++;
        
        // Skip busted/blackjack hands
        while (currentPlayerIndex < hands.size() - 1) {  // -1 for dealer
            Hand hand = hands.get(currentPlayerIndex);
            if (hand.getStatus() == HandStatus.ACTIVE) {
                return;
            }
            currentPlayerIndex++;
        }
        
        // All players done, dealer's turn
        dealerTurn();
    }

    /**
     * INTUITION: Dealer plays.
     * 
     * Dealer must hit until 17+.
     */
    private void dealerTurn() {
        state = GameState.DEALER_TURN;
        
        Hand dealerHand = hands.get(hands.size() - 1);
        
        // Dealer hits until 17 or higher
        while (dealerHand.getValue() < 17) {
            dealerHand.addCard(deck.deal());
        }
        
        if (dealerHand.getValue() > 21) {
            dealerHand.setStatus(HandStatus.BUST);
        } else {
            dealerHand.setStatus(HandStatus.STAND);
        }
        
        // Settle bets
        settleBets();
    }

    /**
     * INTUITION: Calculate winners and pay out.
     */
    private void settleBets() {
        state = GameState.SETTLEMENT;
        
        Hand dealerHand = hands.get(hands.size() - 1);
        int dealerValue = dealerHand.getValue();
        
        for (Hand hand : hands) {
            if (hand.getPlayer() instanceof Dealer) continue;
            
            Player player = hand.getPlayer();
            int playerValue = hand.getValue();
            
            double payout = 0;
            
            if (hand.getStatus() == HandStatus.BLACKJACK) {
                // Blackjack pays 3:2
                payout = player.getCurrentBet() * 1.5;
                player.notify("Blackjack! You win " + payout);
            } else if (hand.getStatus() == HandStatus.BUST) {
                // Player busts, loses bet
                payout = 0;
                player.notify("Bust! You lose " + player.getCurrentBet());
            } else if (dealerValue > 21) {
                // Dealer busts, player wins
                payout = player.getCurrentBet() * 2;
                player.notify("Dealer busts! You win " + payout);
            } else if (playerValue > dealerValue) {
                // Player wins
                payout = player.getCurrentBet() * 2;
                player.notify("You win " + payout);
            } else if (playerValue < dealerValue) {
                // Player loses
                payout = 0;
                player.notify("Dealer wins. You lose " + player.getCurrentBet());
            } else {
                // Push (tie)
                payout = player.getCurrentBet();
                player.notify("Push! Bet returned");
            }
            
            player.addBalance(payout);
        }
        
        state = GameState.FINISHED;
    }

    // --- Getters ---

    public GameState getState() { return state; }
    public Hand getCurrentHand() {
        if (currentPlayerIndex < hands.size()) {
            return hands.get(currentPlayerIndex);
        }
        return null;
    }
    public List<Hand> getVisibleHands() {
        // Only show dealer's first card during player turn
        return hands;
    }

    private Player findPlayer(String playerId) {
        return players.stream()
            .filter(p -> p.getId().equals(playerId))
            .findFirst()
            .orElse(null);
    }
}
```

```java
package com.blackjack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * INTUITION: Card represents a playing card.
 */
public class Card {
    private final String suit;
    private final String rank;
    private final String display;
    private final int value;

    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
        
        // Calculate value
        switch (rank) {
            case "A":
                this.value = 11;  // Can be 1 or 11
                break;
            case "K":
            case "Q":
            case "J":
                this.value = 10;
                break;
            default:
                this.value = Integer.parseInt(rank);
        }
        
        // Display: ♠A, ♥K, etc.
        this.display = suit + rank;
    }

    public int getValue() { return value; }
    public String getSuit() { return suit; }
    public String getRank() { return rank; }
    
    @Override
    public String toString() {
        return display;
    }
}
```

```java
package com.blackjack;

import java.util.*;

/**
 * INTUITION: Deck manages cards.
 * 
 * Shuffles, deals, replenishes.
 */
class Deck {
    private final List<Card> cards;
    private final int initialSize;
    private static final String[] SUITS = {"♠", "♥", "♣", "♦"};
    private static final String[] RANKS = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

    Deck(int deckCount) {
        this.cards = new ArrayList<>();
        this.initialSize = 52 * deckCount;
        
        // Create cards
        for (int d = 0; d < deckCount; d++) {
            for (String suit : SUITS) {
                for (String rank : RANKS) {
                    cards.add(new Card(suit, rank));
                }
            }
        }
    }

    /**
     * INTUITION: Shuffle deck.
     */
    void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * INTUITION: Deal one card.
     */
    Card deal() {
        if (cards.isEmpty()) {
            // Reshuffle (shouldn't happen with multiple decks)
            throw new IllegalStateException("Out of cards");
        }
        return cards.remove(cards.size() - 1);
    }

    /**
     * INTUITION: Check if need reshuffle.
     */
    boolean needsReshuffle() {
        return cards.size() < initialSize * 0.2;  // < 20% remaining
    }

    public int size() {
        return cards.size();
    }
}
```

```java
package com.blackjack;

import java.util.*;

/**
 * INTUITION: Hand represents a player's cards.
 */
public class Hand {
    private final List<Card> cards;
    private Player player;
    private HandStatus status;
    private boolean soft;  // Has Ace counted as 11

    Hand(Player player) {
        this.player = player;
        this.cards = new CopyOnWriteArrayList<>();
        this.status = HandStatus.ACTIVE;
        this.soft = false;
    }

    void addCard(Card card) {
        cards.add(card);
        updateSoft();
    }

    /**
     * INTUITION: Calculate hand value.
     * 
     * - Sum all cards (Ace = 11 initially)
     * - If bust, convert Aces from 11 to 1 until not bust
     */
    int getValue() {
        int value = 0;
        int aceCount = 0;
        
        for (Card card : cards) {
            value += card.getValue();
            if (card.getRank().equals("A")) {
                aceCount++;
            }
        }
        
        // Convert Aces from 11 to 1 if bust
        while (value > 21 && aceCount > 0) {
            value -= 10;  // 11 → 1
            aceCount--;
            this.soft = true;
        }
        
        return value;
    }

    private void updateSoft() {
        soft = cards.stream().anyMatch(c -> c.getRank().equals("A")) && getValue() <= 21;
    }

    /**
     * INTUITION: Check for blackjack (Ace + 10-value = 21).
     */
    boolean isBlackjack() {
        return cards.size() == 2 && getValue() == 21;
    }

    // Getters
    public Player getPlayer() { return player; }
    public List<Card> getCards() { return Collections.unmodifiableList(cards); }
    public HandStatus getStatus() { return status; }
    public void setStatus(HandStatus status) { this.status = status; }
    public boolean isSoft() { return soft; }
}
```

```java
package com.blackjack;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * INTUITION: Player interface.
 * Could be HumanPlayer or AIPlayer.
 */
abstract class Player {
    private final String id;
    private String name;
    protected double balance;
    protected double currentBet;
    private final List<Hand> hands;

    Player(String id, String name, double initialBalance) {
        this.id = id;
        this.name = name;
        this.balance = initialBalance;
        this.hands = new CopyOnWriteArrayList<>();
    }

    void placeBet(double amount) {
        if (amount > balance) throw new IllegalArgumentException("Insufficient balance");
        this.currentBet = amount;
        this.balance -= amount;
    }

    void doubleDown() {
        this.balance -= currentBet;
        this.currentBet *= 2;
    }

    void addBalance(double amount) {
        this.balance += amount;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getBalance() { return balance; }
    public double getCurrentBet() { return currentBet; }
    public List<Hand> getHands() { return hands; }
    
    public void notify(String message) {
        System.out.println(name + ": " + message);
    }
}

enum HandStatus {
    ACTIVE,    // Waiting for player decision
    STAND,     // Player stands
    BUST,      // Over 21
    BLACKJACK, // Natural 21
    DEALER     // Dealer's hand
}

/**
 * Dealer follows fixed rules.
 */
class Dealer extends Player {
    Dealer() {
        super("DEALER", "Dealer", 0);
    }

    @Override
    public void notify(String message) {
        // Dealer doesn't need notifications
    }
}

/**
 * Human player (makes decisions).
 */
class HumanPlayer extends Player {
    HumanPlayer(String id, String name, double balance) {
        super(id, name, balance);
    }
}

/**
 * AI player (auto-play based on basic strategy).
 */
class AIPlayer extends Player {
    AIPlayer(String id, String name, double balance) {
        super(id, name, balance);
    }

    /**
     * Basic strategy: hit until 17+.
     */
    boolean shouldHit(Hand hand) {
        int value = hand.getValue();
        if (value < 17) return true;
        // Soft 17: hit
        if (value == 17 && hand.isSoft()) return true;
        return false;
    }

    boolean shouldDoubleDown(Hand hand) {
        // Double on 10 or 11 if dealer shows low card
        int value = hand.getValue();
        return value == 10 || value == 11;
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle multiple hands (split pairs)?"
> "When player has pair, offer split. Create second hand. Double bet. Play each independently. Resplit if another pair."

### Q2: "How to implement card counting detection?"
> "Track running count (hi-lo). True count = running / decks remaining. Alert if player bets big on high count."

### Q3: "How to handle side bets (Perfect Pairs, 21+3)?"
> "Separate side bet pool. Check at deal: pair suit/match, three-card flush/straight. Payouts: 5:1 to 100:1."

### Q4: "How to multiplayer online?"
> "WebSocket for real-time. Game state sync. Lock table during dealing. Spectator mode."
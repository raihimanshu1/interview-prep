# 🃏 Problem 70: Blackjack Game (21)

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: Gaming companies  
> **Est. Time**: 90 min | **Patterns**: State Machine, Strategy, Observer

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design the card game Blackjack (21)."

**What the interviewer tests**:
```
1. Can you model cards and deck? (Shuffle, deal)
2. Can you calculate hand values? (Ace=1 or 11)
3. Can you implement game rules? (Hit, stand, bust)
4. Can you handle betting? (Chips, payouts)
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
  Values: 1 + 8 + 3 = 14 (soft hand)
  
BLACKJACK:
  Ace + 10-value = 21 (pays 3:2)
```

### Step 3: How to handle Ace?

```
ACE LOGIC:
  1. Count all Aces as 11 initially
  2. If bust, convert Aces to 1 until not bust
  
  [A, K] = 11 + 10 = 21 ✓
  [A, 9, 5] = 11 + 9 + 5 = 25 (bust)
             = 1 + 9 + 5 = 15 ✓
```

---

## 💻 Core Implementation

```java
package com.blackjack;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: BlackjackGame manages game state.
 */
public class BlackjackGame {
    
    private final Deck deck;
    private final Dealer dealer;
    private final List<Player> players;
    private GameState state;

    public BlackjackGame() {
        this.deck = new Deck(1);  // Single deck
        this.deck.shuffle();
        this.dealer = new Dealer();
        this.players = new CopyOnWriteArrayList<>();
        this.state = GameState.BETTING;
    }

    /**
     * Add player.
     */
    public void addPlayer(Player player) {
        players.add(player);
    }

    /**
     * Deal initial cards.
     */
    public void deal() {
        if (state != GameState.BETTING) return;
        
        state = GameState.DEALING;
        
        // Deal 2 cards to each
        for (int i = 0; i < 2; i++) {
            for (Player player : players) {
                player.getHand().addCard(deck.deal());
            }
            dealer.getHand().addCard(deck.deal());
        }
        
        // Check for blackjacks
        for (Player player : players) {
            if (player.getHand().isBlackjack()) {
                player.setStatus(HandStatus.BLACKJACK);
            }
        }
        
        state = GameState.PLAYER_TURN;
    }

    /**
     * Player hits.
     */
    public void hit(Player player) {
        if (state != GameState.PLAYER_TURN) return;
        
        player.getHand().addCard(deck.deal());
        
        if (player.getHand().getValue() > 21) {
            player.setStatus(HandStatus.BUST);
        }
    }

    /**
     * Player stands.
     */
    public void stand(Player player) {
        if (state != GameState.PLAYER_TURN) return;
        player.setStatus(HandStatus.STAND);
        nextPlayer();
    }

    /**
     * Dealer's turn.
     */
    public void dealerTurn() {
        state = GameState.DEALER_TURN;
        
        Hand dealerHand = dealer.getHand();
        
        // Hit until 17+
        while (dealerHand.getValue() < 17) {
            dealerHand.addCard(deck.deal());
        }
        
        if (dealerHand.getValue() > 21) {
            dealer.setStatus(HandStatus.BUST);
        } else {
            dealer.setStatus(HandStatus.STAND);
        }
        
        settleBets();
    }

    /**
     * Settle bets.
     */
    private void settleBets() {
        state = GameState.SETTLEMENT;
        
        int dealerValue = dealer.getHand().getValue();
        boolean dealerBust = dealer.getStatus() == HandStatus.BUST;
        
        for (Player player : players) {
            Hand playerHand = player.getHand();
            int playerValue = playerHand.getValue();
            
            if (playerHand.getStatus() == HandStatus.BUST) {
                // Player loses
                player.notify("Bust! You lose $" + player.getBet());
            } else if (playerHand.isBlackjack()) {
                // Blackjack pays 3:2
                double payout = player.getBet() * 1.5;
                player.addChips(payout);
                player.notify("Blackjack! You win $" + payout);
            } else if (dealerBust || playerValue > dealerValue) {
                // Player wins
                double payout = player.getBet() * 2;
                player.addChips(payout);
                player.notify("You win $" + payout);
            } else if (playerValue == dealerValue) {
                // Push
                player.addChips(player.getBet());
                player.notify("Push! Bet returned");
            } else {
                player.notify("Dealer wins. You lose $" + player.getBet());
            }
        }
        
        state = GameState.FINISHED;
    }

    private void nextPlayer() {
        // Simplified: advance to next active player
        boolean allDone = true;
        for (Player player : players) {
            if (player.getStatus() == HandStatus.ACTIVE) {
                allDone = false;
                break;
            }
        }
        
        if (allDone) {
            dealerTurn();
        }
    }
}

/**
 * Playing card.
 */
class Card {
    private final String suit;
    private final String rank;
    private final int value;

    Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
        
        // Calculate value
        if ("A".equals(rank)) {
            this.value = 11;
        } else if ("KQJ".contains(rank)) {
            this.value = 10;
        } else {
            this.value = Integer.parseInt(rank);
        }
    }

    public int getValue() { return value; }
    public String getSuit() { return suit; }
}

/**
 * Deck of cards.
 */
class Deck {
    private final List<Card> cards;
    private static final String[] SUITS = {"♠", "♥", "♣", "♦"};
    private static final String[] RANKS = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

    Deck(int deckCount) {
        this.cards = new ArrayList<>();
        
        // Create cards
        for (int d = 0; d < deckCount; d++) {
            for (String suit : SUITS) {
                for (String rank : RANKS) {
                    cards.add(new Card(suit, rank));
                }
            }
        }
    }

    void shuffle() {
        Collections.shuffle(cards);
    }

    Card deal() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Out of cards");
        }
        return cards.remove(cards.size() - 1);
    }
}

/**
 * Hand of cards.
 */
class Hand {
    private final List<Card> cards;
    private HandStatus status;

    Hand() {
        this.cards = new CopyOnWriteArrayList<>();
        this.status = HandStatus.ACTIVE;
    }

    /**
     * INTUITION: Calculate hand value.
     */
    int getValue() {
        int value = 0;
        int aceCount = 0;
        
        for (Card card : cards) {
            value += card.getValue();
            if ("A".equals(card.getSuit())) {
                aceCount++;
            }
        }
        
        // Convert Aces to 1 if bust
        while (value > 21 && aceCount > 0) {
            value -= 10;
            aceCount--;
        }
        
        return value;
    }

    boolean isBlackjack() {
        return cards.size() == 2 && getValue() == 21;
    }

    void addCard(Card card) {
        cards.add(card);
    }

    public HandStatus getStatus() { return status; }
    public void setStatus(HandStatus status) { this.status = status; }
}

class Player {
    private final String name;
    private double chips;
    private double bet;
    private final Hand hand;
    private HandStatus status;

    Player(String name, double initialChips) {
        this.name = name;
        this.chips = initialChips;
        this.hand = new Hand();
    }

    void placeBet(double amount) {
        if (amount > chips) throw new IllegalArgumentException("Insufficient chips");
        this.bet = amount;
        this.chips -= amount;
    }

    void addChips(double amount) {
        this.chips += amount;
    }

    void notify(String message) {
        System.out.println(name + ": " + message);
    }

    public String getName() { return name; }
    public double getChips() { return chips; }
    public double getBet() { return bet; }
    public Hand getHand() { return hand; }
    public HandStatus getStatus() { return status; }
    public void setStatus(HandStatus status) { this.status = status; }
}

class Dealer extends Player {
    Dealer() {
        super("Dealer", 0);
    }
}

enum GameState {
    BETTING, DEALING, PLAYER_TURN, DEALER_TURN, SETTLEMENT, FINISHED
}

enum HandStatus {
    ACTIVE, STAND, BUST, BLACKJACK
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle splits?"
> "When pair, offer split. Create 2 hands. Double bet for each."

### Q2: "How to handle doubles?"
> "Double bet, deal 1 card, auto-stand. Only on 9-11."

### Q3: "How to handle insurance?"
> "Offer when dealer shows Ace. 2:1 payout if dealer has blackjack."

### Q4: "How multiplayer online?"
> "Table-based rooms. WebSocket for state sync. Lock during dealing."
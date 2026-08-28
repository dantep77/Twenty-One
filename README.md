# TwentyOneGame

A full-featured command line Blackjack game, complete with betting, splits, double downs, insurance, and surrender.

## Requirements

- Java 21+
- Maven 3.6+

## Build & Run

From the `TwentyOneGame/` directory:

```
mvn clean package
java -jar target/TwentyOneGame.jar
```

Pass `--fast` (or `-f`) to skip all pacing delays for near-instant play, and `--coach` (or `-c`) to turn on the basic-strategy coach. Flags can be combined:

```
java -jar target/TwentyOneGame.jar --fast --coach
```

## Run the tests

```
mvn test
```

## How to play

You start each session with 1000 chips. Every round:

1. **Place a bet** (minimum 10 chips, up to your full stack).
2. Cards are dealt: two face up to you, one face up and one face down to the dealer.
3. If the dealer's face-up card is an Ace, you'll be offered **insurance** for half your bet, paying 2:1 if the dealer has Blackjack.
4. On your turn, choose an action for each hand:
   - **[H]it** — take another card
   - **[S]tand** — end your turn on the current total
   - **[D]ouble Down** — double your bet, take exactly one more card, then stand (only on your first two cards)
   - **[P]Split** — split a pair into two separate hands, each with its own bet (up to 3 splits per round; splitting Aces deals each new hand exactly one card)
   - **[R]Surrender** — forfeit half your bet and end the hand immediately (only as your first decision, and only if you haven't split)
   - **[Q]uit** — exit the game at any prompt
5. The dealer reveals their hole card and hits until reaching 17 or higher (hitting on a soft 17).
6. Hands are settled: Blackjack pays 3:2, a win pays 1:1, and ties push (bet returned).

The shoe uses 6 decks and automatically reshuffles once it runs low. Your win/loss/tie record and chip balance are shown after every round. The game ends automatically if you run out of chips.

## Coach mode

Run with `--coach` (or `-c`) to get a basic-strategy advisor:

- Before every decision, the coach prints the statistically optimal action (hit, stand, double, split, or surrender) for your hand against the dealer's up card.
- After each round, a review shows how many of your decisions matched basic strategy, with an explanation for any mismatch.
- Your cumulative coaching accuracy for the session is shown in the score panel.

The advisor follows standard multi-deck basic strategy (dealer hits soft 17, late surrender, double after split), and falls back to the next-best legal action when the ideal one (e.g. doubling after you've already hit) isn't available.

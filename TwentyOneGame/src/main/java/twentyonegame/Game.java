package twentyonegame;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import twentyonegame.exception.HandValueException;
import twentyonegame.exception.InsufficientFundsException;
import twentyonegame.exception.QuitGameException;

public class Game {

	/** Chips the player starts a session with */
	private static final int STARTING_CHIPS = 1000;

	/** Minimum allowed wager on a single hand */
	private static final int MIN_BET = 10;

	/** Maximum number of hands a player may reach through splitting (3 splits) */
	private static final int MAX_HANDS = 4;

	static Game instance;

	private int wins = 0;
	private int losses = 0;
	private int ties = 0;

	public static Game getInstance() {
		if (instance == null) {
			instance = new Game();
		}
		return instance;
	}

	public int getWins() {
		return wins;
	}

	public int getLosses() {
		return losses;
	}

	public int getTies() {
		return ties;
	}

	public void playGame() {
		displayWelcomeMessage();
		try {
			gameLoop();
		} catch (QuitGameException e) {
			// Player chose to quit; fall through to the exit message.
		} catch (HandValueException e) {
			e.printStackTrace();
		} catch (InsufficientFundsException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		displayExitMessage();
	}

	public void displayExitMessage() {
		System.out.println();
		System.out.println("Thanks for playing! See you next time!");
	}

	private void gameLoop()
			throws HandValueException, InsufficientFundsException, InterruptedException {
		Deck deck = Deck.getInstance();
		Scanner scan = new Scanner(System.in);
		Player player = new Player("You", STARTING_CHIPS);
		Dealer dealer = new Dealer();

		try {
			boolean gameActive = true;
			while (gameActive) {
				if (player.getChips() < MIN_BET) {
					System.out.println("\nYou're out of chips! Game over.");
					break;
				}

				if (deck.needsReshuffle()) {
					System.out.println("\nReshuffling the shoe...");
					deck.reset();
					Thread.sleep(600);
				}

				playRound(player, dealer, deck, scan);

				displayRecord(player);

				if (player.getChips() < MIN_BET) {
					System.out.println("\nYou're out of chips! Game over.");
					break;
				}

				gameActive = promptPlayAgain(scan);
			}
		} finally {
			scan.close();
		}
	}

	/**
	 * Plays a single round: betting, dealing, insurance, player turns, dealer
	 * turn, and settlement.
	 */
	private void playRound(Player player, Dealer dealer, Deck deck, Scanner scan)
			throws HandValueException, InsufficientFundsException, InterruptedException {

		dealer.resetHand();
		player.resetHands();

		int bet = promptForBet(scan, player);
		Hand playerHand = player.placeBet(bet);

		System.out.println("\nDealing cards...");
		Thread.sleep(700);

		playerHand.hit(deck.deal());
		Thread.sleep(300);
		dealer.getHand().hit(deck.deal());
		Thread.sleep(300);
		playerHand.hit(deck.deal());
		Thread.sleep(300);
		Card holeCard = deck.deal();
		holeCard.setFaceDown(true);
		dealer.getHand().hit(holeCard);
		Thread.sleep(300);

		displayTable(player, dealer, true);

		int insuranceBet = 0;
		Card upCard = dealer.getUpCard();
		boolean dealerShowsBlackjackCard = upCard.getRank().equals(Rank.ACE) || upCard.getValue() == 10;

		if (upCard.getRank().equals(Rank.ACE)) {
			if (promptInsurance(scan, player, bet)) {
				insuranceBet = bet / 2;
				player.deductChips(insuranceBet);
			}
		}

		if (dealerShowsBlackjackCard && dealer.getHand().isBlackJack()) {
			holeCard.setFaceDown(false);
			System.out.println("\nDealer reveals:");
			System.out.println(dealer.getHand().toString());
			System.out.println("Dealer has Blackjack!");
			Thread.sleep(800);

			if (insuranceBet > 0) {
				int payout = insuranceBet * 3;
				player.addChips(payout);
				System.out.println("Insurance pays 2:1! You receive " + payout + " chips.");
			} else if (upCard.getRank().equals(Rank.ACE)) {
				System.out.println("You did not take insurance.");
			}

			for (Hand hand : player.getHands()) {
				settleHand(hand, dealer.getHand(), player);
			}
			return;
		}

		if (insuranceBet > 0) {
			System.out.println("\nDealer does not have Blackjack. Insurance lost.");
			Thread.sleep(500);
		}

		playHands(player, dealer, deck, scan);

		Thread.sleep(700);
		System.out.println("\nDealer's turn:");
		Thread.sleep(500);
		holeCard.setFaceDown(false);
		System.out.println(dealer.getHand().toString());
		System.out.println("Dealer value: " + dealer.getHand().getValue());
		Thread.sleep(500);

		boolean anyoneStillIn = player.getHands().stream()
				.anyMatch(h -> !h.isBust() && !h.isSurrendered());

		if (anyoneStillIn) {
			while (dealer.shouldHit()) {
				Thread.sleep(600);
				Card card = deck.deal();
				dealer.getHand().hit(card);
				System.out.println("\nThe dealer drew:");
				Thread.sleep(300);
				System.out.println(card.getAsciiArt());
				System.out.println("Dealer value: " + dealer.getHand().getValue());
				Thread.sleep(400);
			}
			if (dealer.getHand().isBust()) {
				System.out.println("\nThe dealer busts!");
				Thread.sleep(600);
			}
		}

		Thread.sleep(500);
		System.out.println();
		List<Hand> hands = player.getHands();
		for (int i = 0; i < hands.size(); i++) {
			if (hands.size() > 1) {
				System.out.println("-- Hand " + (i + 1) + " --");
			}
			settleHand(hands.get(i), dealer.getHand(), player);
		}
	}

	/**
	 * Plays every one of the player's hands to completion, including any hands
	 * created mid-round by splitting.
	 */
	private void playHands(Player player, Dealer dealer, Deck deck, Scanner scan)
			throws HandValueException, InsufficientFundsException, InterruptedException {
		List<Hand> hands = player.getHands();
		int i = 0;
		while (i < hands.size()) {
			playSingleHand(hands.get(i), i, player, dealer, deck, scan);
			i++;
		}
	}

	void playSingleHand(Hand hand, int handIndex, Player player, Dealer dealer, Deck deck,
			Scanner scan) throws HandValueException, InsufficientFundsException, InterruptedException {
		List<Hand> hands = player.getHands();
		String label = hands.size() > 1 ? "Hand " + (handIndex + 1) : "Your hand";

		System.out.println("\n" + label + ":");
		System.out.println(hand.toString());
		System.out.println("Value: " + hand.getValue());

		if (hand.isSplitAces()) {
			System.out.println("Split aces receive only one card.");
			Thread.sleep(500);
			return;
		}

		if (hand.isBlackJack()) {
			System.out.println("Blackjack!");
			Thread.sleep(800);
			return;
		}

		boolean firstAction = true;
		boolean acting = true;
		while (acting) {
			List<String> options = new ArrayList<String>();
			options.add("[H]it");
			options.add("[S]tand");
			boolean canDouble = firstAction && hand.canDoubleDown() && player.getChips() >= hand.getBet();
			boolean canSplit = firstAction && hand.canSplit() && hands.size() < MAX_HANDS
					&& player.getChips() >= hand.getBet();
			boolean canSurrender = firstAction && hands.size() == 1 && hand.getCards().size() == 2;
			if (canDouble) options.add("[D]ouble Down");
			if (canSplit) options.add("[P] Split");
			if (canSurrender) options.add("[R] Surrender");
			options.add("[Q]uit");

			System.out.println("\nWould you like to " + String.join(", ", options) + "?");
			String answer = scan.next();

			if (answer.equalsIgnoreCase("h")) {
				Card card = deck.deal();
				hand.hit(card);
				firstAction = false;
				System.out.println("You were dealt:");
				Thread.sleep(300);
				System.out.println(card.getAsciiArt());
				System.out.println("Value: " + hand.getValue());
				Thread.sleep(400);
				if (hand.isBust()) {
					System.out.println("Bust!");
					Thread.sleep(800);
					acting = false;
				}
			} else if (answer.equalsIgnoreCase("s")) {
				acting = false;
			} else if (answer.equalsIgnoreCase("d") && canDouble) {
				player.deductChips(hand.getBet());
				Card card = deck.deal();
				hand.doubleDown(card);
				System.out.println("Doubling down! You were dealt:");
				Thread.sleep(300);
				System.out.println(card.getAsciiArt());
				System.out.println("Value: " + hand.getValue());
				if (hand.isBust()) {
					System.out.println("Bust!");
					Thread.sleep(800);
				}
				acting = false;
			} else if (answer.equalsIgnoreCase("p") && canSplit) {
				splitHand(hand, handIndex, player, deck);
				System.out.println("Hand split!");
				System.out.println(hand.toString());
				System.out.println("Value: " + hand.getValue());
				firstAction = true;
				if (hand.isSplitAces()) {
					acting = false;
				}
			} else if (answer.equalsIgnoreCase("r") && canSurrender) {
				hand.surrender();
				int refund = hand.getBet() / 2;
				player.addChips(refund);
				System.out.println("Hand surrendered. " + refund + " chips returned.");
				Thread.sleep(500);
				acting = false;
			} else if (answer.equalsIgnoreCase("q")) {
				throw new QuitGameException();
			} else {
				System.out.println("Invalid input, please try again.");
			}
		}
	}

	/**
	 * Splits a pair into two hands: the original hand keeps its first card and
	 * draws a new second card, while a freshly created hand takes the removed
	 * card plus one of its own.
	 */
	void splitHand(Hand hand, int handIndex, Player player, Deck deck)
			throws HandValueException, InsufficientFundsException {
		boolean splittingAces = hand.getCards().get(0).getRank().equals(Rank.ACE);

		player.deductChips(hand.getBet());
		Hand newHand = new Hand(hand.getBet());
		Card movedCard = hand.removeCardForSplit();
		newHand.hit(movedCard);

		if (splittingAces) {
			hand.markSplitAces();
			newHand.markSplitAces();
		}

		player.getHands().add(handIndex + 1, newHand);

		hand.hit(deck.deal());
		newHand.hit(deck.deal());
	}

	/**
	 * Settles one hand against the dealer's final hand, paying out or
	 * collecting chips and updating the session record.
	 */
	void settleHand(Hand hand, Hand dealerHand, Player player) {
		int bet = hand.getBet();

		if (hand.isSurrendered()) {
			System.out.println("Hand surrendered - half bet forfeited.");
			losses++;
			return;
		}

		if (hand.isBust()) {
			System.out.println("Hand busts (" + hand.getValue() + "). You lose " + bet + " chips.");
			losses++;
			return;
		}

		boolean playerBlackjack = hand.isBlackJack();
		boolean dealerBlackjack = dealerHand.isBlackJack();

		if (dealerHand.isBust()) {
			int winnings = playerBlackjack ? bet * 3 / 2 : bet;
			player.addChips(bet + winnings);
			System.out.println("Dealer busts! You win " + winnings + " chips.");
			wins++;
			return;
		}

		if (playerBlackjack && !dealerBlackjack) {
			int winnings = bet * 3 / 2;
			player.addChips(bet + winnings);
			System.out.println("Blackjack! You win " + winnings + " chips.");
			wins++;
			return;
		}

		if (dealerBlackjack && !playerBlackjack) {
			System.out.println("Dealer has Blackjack. You lose " + bet + " chips.");
			losses++;
			return;
		}

		if (playerBlackjack && dealerBlackjack) {
			player.addChips(bet);
			System.out.println("Both have Blackjack - push. Bet returned.");
			ties++;
			return;
		}

		if (hand.getValue() > dealerHand.getValue()) {
			player.addChips(bet * 2);
			System.out.println("You win with " + hand.getValue() + " vs " + dealerHand.getValue()
					+ "! You win " + bet + " chips.");
			wins++;
		} else if (hand.getValue() < dealerHand.getValue()) {
			System.out.println("Dealer wins with " + dealerHand.getValue() + " vs " + hand.getValue()
					+ ". You lose " + bet + " chips.");
			losses++;
		} else {
			player.addChips(bet);
			System.out.println("Push at " + hand.getValue() + ". Bet returned.");
			ties++;
		}
	}

	int promptForBet(Scanner scan, Player player) {
		while (true) {
			System.out.println("\nYou have " + player.getChips() + " chips. Enter your bet (min "
					+ MIN_BET + ", max " + player.getChips() + "), or [Q] to quit:");
			String input = scan.next();

			if (input.equalsIgnoreCase("q")) {
				throw new QuitGameException();
			}

			try {
				int bet = Integer.parseInt(input);
				if (bet < MIN_BET) {
					System.out.println("Bet must be at least " + MIN_BET + " chips.");
				} else if (bet > player.getChips()) {
					System.out.println("You don't have enough chips for that bet.");
				} else {
					return bet;
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid input, please enter a whole number.");
			}
		}
	}

	boolean promptInsurance(Scanner scan, Player player, int bet) {
		int insuranceCost = bet / 2;
		if (insuranceCost <= 0 || player.getChips() < insuranceCost) {
			return false;
		}
		System.out.println("\nDealer is showing an Ace. Would you like insurance for " + insuranceCost
				+ " chips? [Y]es or [N]o");
		while (true) {
			String answer = scan.next();
			if (answer.equalsIgnoreCase("y")) {
				return true;
			} else if (answer.equalsIgnoreCase("n")) {
				return false;
			} else if (answer.equalsIgnoreCase("q")) {
				throw new QuitGameException();
			} else {
				System.out.println("Invalid input, please try again");
			}
		}
	}

	private boolean promptPlayAgain(Scanner scan) throws InterruptedException {
		System.out.println("\nWould you like to play again? [Y]es or [N]o");
		while (true) {
			String answer = scan.next();
			if (answer.equalsIgnoreCase("y")) {
				System.out.println("Starting next round...");
				Thread.sleep(600);
				return true;
			} else if (answer.equalsIgnoreCase("n")) {
				return false;
			} else if (answer.equalsIgnoreCase("q")) {
				throw new QuitGameException();
			} else {
				System.out.println("Invalid input, try again");
			}
		}
	}

	private void displayTable(Player player, Dealer dealer, boolean showValues) {
		System.out.println("\nDealer's Hand:");
		System.out.println(dealer.getHand().toString());

		for (Hand hand : player.getHands()) {
			System.out.println("Your Hand:");
			System.out.println(hand.toString());
			if (showValues) {
				System.out.println("Your hand value: " + hand.getValue());
			}
		}
	}

	private void displayRecord(Player player) {
		System.out.println("\n----------------------------");
		System.out.println("|      Current Record:      |");
		System.out.printf("| Wins: %5d  Losses: %5d |%n", wins, losses);
		System.out.printf("| Ties: %5d  Chips: %6d |%n", ties, player.getChips());
		System.out.println("----------------------------");
	}

	public void displayWelcomeMessage() {
		System.out.println("Welcome to...");
		System.out.println();
		displayLogo();
		System.out.println();
	}

	/**
	 * Prints the logo, the total width is 114 characters 114
	 */
	private void displayLogo() {
		System.out.println(Art.bigLogo);
	}
}

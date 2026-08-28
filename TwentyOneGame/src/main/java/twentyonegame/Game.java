package twentyonegame;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import twentyonegame.BasicStrategy.Action;
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

	private static final int TABLE_WIDTH = 60;
	private static final int PANEL_WIDTH = 26;

	private static final String RESET = ANSI.RESET.getCode();
	private static final String BORDER = ANSI.BRIGHT_GREEN.getCode();
	private static final String HEADER = ANSI.BOLD.getCode() + ANSI.BRIGHT_WHITE.getCode();
	private static final String PROMPT = ANSI.BRIGHT_CYAN.getCode();
	private static final String KEY = ANSI.BOLD.getCode() + ANSI.BRIGHT_YELLOW.getCode();
	private static final String WIN = ANSI.BOLD.getCode() + ANSI.BRIGHT_GREEN.getCode();
	private static final String LOSE = ANSI.BOLD.getCode() + ANSI.BRIGHT_RED.getCode();
	private static final String TIE = ANSI.BOLD.getCode() + ANSI.BRIGHT_YELLOW.getCode();
	private static final String GOLD = ANSI.BOLD.getCode() + ANSI.YELLOW.getCode();
	private static final String COACH = ANSI.BOLD.getCode() + ANSI.BRIGHT_MAGENTA.getCode();

	static Game instance;

	/** When true, all pacing delays are skipped for near-instant play. */
	private boolean fastMode = false;

	/** When true, basic-strategy suggestions and a post-round review are shown. */
	private boolean coachMode = false;

	private int wins = 0;
	private int losses = 0;
	private int ties = 0;

	private int coachCorrect = 0;
	private int coachTotal = 0;

	/**
	 * One recorded coaching comparison: what basic strategy recommended versus
	 * what the player actually chose for a given hand.
	 */
	private record Decision(String situation, Action suggested, Action actual) {
		boolean isCorrect() {
			return suggested == actual;
		}
	}

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

	void setCoachMode(boolean coachMode) {
		this.coachMode = coachMode;
	}

	int getCoachCorrect() {
		return coachCorrect;
	}

	int getCoachTotal() {
		return coachTotal;
	}

	public void playGame() {
		playGame(false, false);
	}

	public void playGame(boolean fastMode) {
		playGame(fastMode, false);
	}

	public void playGame(boolean fastMode, boolean coachMode) {
		this.fastMode = fastMode;
		this.coachMode = coachMode;
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
		System.out.println(HEADER + "Thanks for playing! See you next time!" + RESET);
	}

	/**
	 * Sleeps for the given duration, unless fast mode is enabled.
	 */
	private void pause(long millis) throws InterruptedException {
		if (!fastMode) {
			Thread.sleep(millis);
		}
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
					System.out.println("\n" + LOSE + "You're out of chips! Game over." + RESET);
					break;
				}

				if (deck.needsReshuffle()) {
					System.out.println("\n" + PROMPT + "Reshuffling the shoe..." + RESET);
					deck.reset();
					pause(600);
				}

				System.out.println(Art.divider(TABLE_WIDTH));
				playRound(player, dealer, deck, scan);

				printScorePanel(player);

				if (player.getChips() < MIN_BET) {
					System.out.println("\n" + LOSE + "You're out of chips! Game over." + RESET);
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

		System.out.println("\n" + PROMPT + "Dealing cards..." + RESET);
		pause(700);

		playerHand.hit(deck.deal());
		pause(300);
		dealer.getHand().hit(deck.deal());
		pause(300);
		playerHand.hit(deck.deal());
		pause(300);
		Card holeCard = deck.deal();
		holeCard.setFaceDown(true);
		dealer.getHand().hit(holeCard);
		pause(300);

		displayTable(player, dealer);

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
			System.out.println("\n" + HEADER + "Dealer reveals:" + RESET);
			System.out.println(dealer.getHand().toString());
			System.out.println(GOLD + "Dealer has Blackjack!" + RESET);
			pause(800);

			if (insuranceBet > 0) {
				int payout = insuranceBet * 3;
				player.addChips(payout);
				System.out.println(WIN + "Insurance pays 2:1! You receive " + payout + " chips." + RESET);
			} else if (upCard.getRank().equals(Rank.ACE)) {
				System.out.println("You did not take insurance.");
			}

			for (Hand hand : player.getHands()) {
				settleHand(hand, dealer.getHand(), player);
			}
			return;
		}

		if (insuranceBet > 0) {
			System.out.println("\n" + LOSE + "Dealer does not have Blackjack. Insurance lost." + RESET);
			pause(500);
		}

		List<Decision> roundDecisions = new ArrayList<Decision>();
		playHands(player, dealer, deck, scan, roundDecisions);
		if (coachMode) {
			printCoachReview(roundDecisions);
		}

		pause(700);
		System.out.println("\n" + HEADER + "Dealer's turn:" + RESET);
		pause(500);
		holeCard.setFaceDown(false);
		System.out.println(dealer.getHand().toString());
		System.out.println("Dealer value: " + colorValue(dealer.getHand()));
		pause(500);

		boolean anyoneStillIn = player.getHands().stream()
				.anyMatch(h -> !h.isBust() && !h.isSurrendered());

		if (anyoneStillIn) {
			while (dealer.shouldHit()) {
				pause(600);
				Card card = deck.deal();
				dealer.getHand().hit(card);
				System.out.println("\n" + PROMPT + "The dealer drew:" + RESET);
				pause(300);
				System.out.println(card.getAsciiArt());
				System.out.println("Dealer value: " + colorValue(dealer.getHand()));
				pause(400);
			}
			if (dealer.getHand().isBust()) {
				System.out.println("\n" + WIN + "The dealer busts!" + RESET);
				pause(600);
			}
		}

		pause(500);
		System.out.println();
		List<Hand> hands = player.getHands();
		for (int i = 0; i < hands.size(); i++) {
			if (hands.size() > 1) {
				System.out.println(HEADER + "-- Hand " + (i + 1) + " --" + RESET);
			}
			settleHand(hands.get(i), dealer.getHand(), player);
		}
	}

	/**
	 * Plays every one of the player's hands to completion, including any hands
	 * created mid-round by splitting.
	 */
	private void playHands(Player player, Dealer dealer, Deck deck, Scanner scan, List<Decision> decisions)
			throws HandValueException, InsufficientFundsException, InterruptedException {
		List<Hand> hands = player.getHands();
		int i = 0;
		while (i < hands.size()) {
			playSingleHand(hands.get(i), i, player, dealer, deck, scan, decisions);
			i++;
		}
	}

	void playSingleHand(Hand hand, int handIndex, Player player, Dealer dealer, Deck deck, Scanner scan)
			throws HandValueException, InsufficientFundsException, InterruptedException {
		playSingleHand(hand, handIndex, player, dealer, deck, scan, new ArrayList<Decision>());
	}

	void playSingleHand(Hand hand, int handIndex, Player player, Dealer dealer, Deck deck,
			Scanner scan, List<Decision> decisions)
			throws HandValueException, InsufficientFundsException, InterruptedException {
		List<Hand> hands = player.getHands();
		String label = hands.size() > 1 ? "Hand " + (handIndex + 1) : "Your hand";

		System.out.println("\n" + HEADER + label + ":" + RESET);
		System.out.println(hand.toString());
		System.out.println("Value: " + colorValue(hand));

		if (hand.isSplitAces()) {
			System.out.println("Split aces receive only one card.");
			pause(500);
			return;
		}

		if (hand.isBlackJack()) {
			System.out.println(GOLD + "Blackjack!" + RESET);
			pause(800);
			return;
		}

		boolean firstAction = true;
		boolean acting = true;
		while (acting) {
			List<String> options = new ArrayList<String>();
			options.add(option("H", "it"));
			options.add(option("S", "tand"));
			boolean canDouble = firstAction && hand.canDoubleDown() && player.getChips() >= hand.getBet();
			boolean canSplit = firstAction && hand.canSplit() && hands.size() < MAX_HANDS
					&& player.getChips() >= hand.getBet();
			boolean canSurrender = firstAction && hands.size() == 1 && hand.getCards().size() == 2;
			if (canDouble) options.add(option("D", "ouble Down"));
			if (canSplit) options.add(option("P", " Split"));
			if (canSurrender) options.add(option("R", " Surrender"));
			options.add(option("Q", "uit"));

			Action suggestion = null;
			if (coachMode) {
				suggestion = BasicStrategy.recommend(hand, dealer.getUpCard(), canDouble, canSplit, canSurrender);
				System.out.println(COACH + "Coach suggests: " + actionLabel(suggestion) + RESET);
			}

			System.out.println("\n" + PROMPT + "Would you like to " + String.join(", ", options) + "?" + RESET);
			String answer = scan.next();

			if (answer.equalsIgnoreCase("h")) {
				recordDecision(decisions, hand, dealer, suggestion, Action.HIT);
				Card card = deck.deal();
				hand.hit(card);
				firstAction = false;
				System.out.println(PROMPT + "You were dealt:" + RESET);
				pause(300);
				System.out.println(card.getAsciiArt());
				System.out.println("Value: " + colorValue(hand));
				pause(400);
				if (hand.isBust()) {
					System.out.println(LOSE + "Bust!" + RESET);
					pause(800);
					acting = false;
				}
			} else if (answer.equalsIgnoreCase("s")) {
				recordDecision(decisions, hand, dealer, suggestion, Action.STAND);
				acting = false;
			} else if (answer.equalsIgnoreCase("d") && canDouble) {
				recordDecision(decisions, hand, dealer, suggestion, Action.DOUBLE);
				player.deductChips(hand.getBet());
				Card card = deck.deal();
				hand.doubleDown(card);
				System.out.println(PROMPT + "Doubling down! You were dealt:" + RESET);
				pause(300);
				System.out.println(card.getAsciiArt());
				System.out.println("Value: " + colorValue(hand));
				if (hand.isBust()) {
					System.out.println(LOSE + "Bust!" + RESET);
					pause(800);
				}
				acting = false;
			} else if (answer.equalsIgnoreCase("p") && canSplit) {
				recordDecision(decisions, hand, dealer, suggestion, Action.SPLIT);
				splitHand(hand, handIndex, player, deck);
				System.out.println(HEADER + "Hand split!" + RESET);
				System.out.println(hand.toString());
				System.out.println("Value: " + colorValue(hand));
				firstAction = true;
				if (hand.isSplitAces()) {
					acting = false;
				}
			} else if (answer.equalsIgnoreCase("r") && canSurrender) {
				recordDecision(decisions, hand, dealer, suggestion, Action.SURRENDER);
				hand.surrender();
				int refund = hand.getBet() / 2;
				player.addChips(refund);
				System.out.println(TIE + "Hand surrendered. " + refund + " chips returned." + RESET);
				pause(500);
				acting = false;
			} else if (answer.equalsIgnoreCase("q")) {
				throw new QuitGameException();
			} else {
				System.out.println(LOSE + "Invalid input, please try again." + RESET);
			}
		}
	}

	/**
	 * Records a coaching comparison between the suggested and actual action,
	 * and updates the session-wide accuracy tally. No-op when coaching is off.
	 */
	private void recordDecision(List<Decision> decisions, Hand hand, Dealer dealer, Action suggestion,
			Action actual) {
		if (!coachMode || suggestion == null) {
			return;
		}
		Decision decision = new Decision(describeSituation(hand, dealer.getUpCard()), suggestion, actual);
		decisions.add(decision);
		coachTotal++;
		if (decision.isCorrect()) {
			coachCorrect++;
		}
	}

	private String describeSituation(Hand hand, Card dealerUpCard) {
		String dealerLabel = dealerUpCard.getRank().equals(Rank.ACE) ? "A" : String.valueOf(dealerUpCard.getValue());
		if (hand.getCards().size() == 2
				&& hand.getCards().get(0).getRank().equals(hand.getCards().get(1).getRank())) {
			String label = hand.getCards().get(0).getRank().getRank();
			return "a pair of " + label + "s vs dealer " + dealerLabel;
		}
		String softLabel = hand.isSoft() ? "soft " : "";
		return softLabel + hand.getValue() + " vs dealer " + dealerLabel;
	}

	private String actionLabel(Action action) {
		switch (action) {
			case HIT: return "Hit";
			case STAND: return "Stand";
			case DOUBLE: return "Double Down";
			case SPLIT: return "Split";
			case SURRENDER: return "Surrender";
			default: return action.toString();
		}
	}

	/**
	 * Prints a summary of how many of this round's decisions matched basic
	 * strategy, plus a line for each mismatch.
	 */
	private void printCoachReview(List<Decision> decisions) {
		if (decisions.isEmpty()) {
			return;
		}
		long correct = decisions.stream().filter(Decision::isCorrect).count();
		System.out.println("\n" + COACH + "Coach Review: " + correct + "/" + decisions.size()
				+ " decisions matched basic strategy" + RESET);
		for (Decision decision : decisions) {
			if (!decision.isCorrect()) {
				System.out.println(COACH + "  On " + decision.situation() + ", basic strategy says "
						+ actionLabel(decision.suggested()) + " - you chose " + actionLabel(decision.actual())
						+ "." + RESET);
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
			System.out.println(TIE + "Hand surrendered - half bet forfeited." + RESET);
			losses++;
			return;
		}

		if (hand.isBust()) {
			System.out.println(LOSE + "Hand busts (" + hand.getValue() + "). You lose " + bet + " chips." + RESET);
			losses++;
			return;
		}

		boolean playerBlackjack = hand.isBlackJack();
		boolean dealerBlackjack = dealerHand.isBlackJack();

		if (dealerHand.isBust()) {
			int winnings = playerBlackjack ? bet * 3 / 2 : bet;
			player.addChips(bet + winnings);
			System.out.println(WIN + "Dealer busts! You win " + winnings + " chips." + RESET);
			wins++;
			return;
		}

		if (playerBlackjack && !dealerBlackjack) {
			int winnings = bet * 3 / 2;
			player.addChips(bet + winnings);
			System.out.println(GOLD + "Blackjack! You win " + winnings + " chips." + RESET);
			wins++;
			return;
		}

		if (dealerBlackjack && !playerBlackjack) {
			System.out.println(LOSE + "Dealer has Blackjack. You lose " + bet + " chips." + RESET);
			losses++;
			return;
		}

		if (playerBlackjack && dealerBlackjack) {
			player.addChips(bet);
			System.out.println(TIE + "Both have Blackjack - push. Bet returned." + RESET);
			ties++;
			return;
		}

		if (hand.getValue() > dealerHand.getValue()) {
			player.addChips(bet * 2);
			System.out.println(WIN + "You win with " + hand.getValue() + " vs " + dealerHand.getValue()
					+ "! You win " + bet + " chips." + RESET);
			wins++;
		} else if (hand.getValue() < dealerHand.getValue()) {
			System.out.println(LOSE + "Dealer wins with " + dealerHand.getValue() + " vs " + hand.getValue()
					+ ". You lose " + bet + " chips." + RESET);
			losses++;
		} else {
			player.addChips(bet);
			System.out.println(TIE + "Push at " + hand.getValue() + ". Bet returned." + RESET);
			ties++;
		}
	}

	int promptForBet(Scanner scan, Player player) {
		while (true) {
			System.out.println("\n" + PROMPT + "You have " + HEADER + player.getChips() + RESET + PROMPT
					+ " chips. Enter your bet (min " + MIN_BET + ", max " + player.getChips() + "), or "
					+ option("Q", "uit") + PROMPT + ":" + RESET);
			String input = scan.next();

			if (input.equalsIgnoreCase("q")) {
				throw new QuitGameException();
			}

			try {
				int bet = Integer.parseInt(input);
				if (bet < MIN_BET) {
					System.out.println(LOSE + "Bet must be at least " + MIN_BET + " chips." + RESET);
				} else if (bet > player.getChips()) {
					System.out.println(LOSE + "You don't have enough chips for that bet." + RESET);
				} else {
					return bet;
				}
			} catch (NumberFormatException e) {
				System.out.println(LOSE + "Invalid input, please enter a whole number." + RESET);
			}
		}
	}

	boolean promptInsurance(Scanner scan, Player player, int bet) {
		int insuranceCost = bet / 2;
		if (insuranceCost <= 0 || player.getChips() < insuranceCost) {
			return false;
		}
		System.out.println("\n" + PROMPT + "Dealer is showing an Ace. Would you like insurance for "
				+ insuranceCost + " chips? " + option("Y", "es") + " or " + option("N", "o") + RESET);
		while (true) {
			String answer = scan.next();
			if (answer.equalsIgnoreCase("y")) {
				return true;
			} else if (answer.equalsIgnoreCase("n")) {
				return false;
			} else if (answer.equalsIgnoreCase("q")) {
				throw new QuitGameException();
			} else {
				System.out.println(LOSE + "Invalid input, please try again" + RESET);
			}
		}
	}

	private boolean promptPlayAgain(Scanner scan) throws InterruptedException {
		System.out.println("\n" + PROMPT + "Would you like to play again? " + option("Y", "es") + " or "
				+ option("N", "o") + RESET);
		while (true) {
			String answer = scan.next();
			if (answer.equalsIgnoreCase("y")) {
				System.out.println(PROMPT + "Starting next round..." + RESET);
				pause(600);
				return true;
			} else if (answer.equalsIgnoreCase("n")) {
				return false;
			} else if (answer.equalsIgnoreCase("q")) {
				throw new QuitGameException();
			} else {
				System.out.println(LOSE + "Invalid input, try again" + RESET);
			}
		}
	}

	private void displayTable(Player player, Dealer dealer) {
		System.out.println("\n" + HEADER + "Dealer's Hand:" + RESET);
		System.out.println(dealer.getHand().toString());

		for (Hand hand : player.getHands()) {
			System.out.println(HEADER + "Your Hand:" + RESET);
			System.out.println(hand.toString());
			System.out.println("Your hand value: " + colorValue(hand));
		}
	}

	private void printScorePanel(Player player) {
		String top = "╔" + "═".repeat(PANEL_WIDTH) + "╗";
		String divider = "╟" + "─".repeat(PANEL_WIDTH) + "╢";
		String bottom = "╚" + "═".repeat(PANEL_WIDTH) + "╝";

		System.out.println();
		System.out.println(BORDER + top + RESET);
		System.out.println(BORDER + "║" + RESET + HEADER + center("CURRENT RECORD", PANEL_WIDTH) + RESET
				+ BORDER + "║" + RESET);
		System.out.println(BORDER + divider + RESET);
		System.out.println(BORDER + "║" + RESET + ANSI.BRIGHT_GREEN.getCode()
				+ padRight(" Wins:    " + wins, PANEL_WIDTH) + RESET + BORDER + "║" + RESET);
		System.out.println(BORDER + "║" + RESET + ANSI.BRIGHT_RED.getCode()
				+ padRight(" Losses:  " + losses, PANEL_WIDTH) + RESET + BORDER + "║" + RESET);
		System.out.println(BORDER + "║" + RESET + ANSI.BRIGHT_YELLOW.getCode()
				+ padRight(" Ties:    " + ties, PANEL_WIDTH) + RESET + BORDER + "║" + RESET);
		System.out.println(BORDER + divider + RESET);
		System.out.println(BORDER + "║" + RESET + GOLD
				+ padRight(" Chips:   " + player.getChips(), PANEL_WIDTH) + RESET + BORDER + "║" + RESET);
		if (coachMode && coachTotal > 0) {
			System.out.println(BORDER + divider + RESET);
			int accuracy = coachCorrect * 100 / coachTotal;
			System.out.println(BORDER + "║" + RESET + COACH
					+ padRight(" Coach:   " + coachCorrect + "/" + coachTotal + " (" + accuracy + "%)", PANEL_WIDTH)
					+ RESET + BORDER + "║" + RESET);
		}
		System.out.println(BORDER + bottom + RESET);
	}

	private String colorValue(Hand hand) {
		if (hand.isBust()) return LOSE + hand.getValue() + RESET;
		if (hand.isBlackJack()) return GOLD + hand.getValue() + RESET;
		return HEADER + hand.getValue() + RESET;
	}

	private String option(String key, String rest) {
		return KEY + "[" + key + "]" + RESET + PROMPT + rest + RESET;
	}

	private String padRight(String s, int width) {
		if (s.length() >= width) return s.substring(0, width);
		return s + " ".repeat(width - s.length());
	}

	private String center(String s, int width) {
		int pad = width - s.length();
		int left = Math.max(pad / 2, 0);
		int right = Math.max(pad - left, 0);
		return " ".repeat(left) + s + " ".repeat(right);
	}

	public void displayWelcomeMessage() {
		System.out.println(Art.divider(TABLE_WIDTH));
		System.out.println(HEADER + "Welcome to..." + RESET);
		System.out.println();
		displayLogo();
		System.out.println();
		if (fastMode) {
			System.out.println(GOLD + "Fast mode enabled - dealing at full speed." + RESET);
			System.out.println();
		}
		if (coachMode) {
			System.out.println(COACH + "Coach mode enabled - basic strategy suggestions and round reviews are on."
					+ RESET);
			System.out.println();
		}
		System.out.println(Art.divider(TABLE_WIDTH));
	}

	/**
	 * Prints the logo, the total width is 114 characters 114
	 */
	private void displayLogo() {
		System.out.println(Art.bigLogo);
	}
}

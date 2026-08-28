package twentyonegame;

import java.util.Scanner;

import twentyonegame.exception.HandValueException;

public class Game {

	static Game instance;

	private static final int TABLE_WIDTH = 60;
	private static final int PANEL_WIDTH = 22;

	private static final String RESET = ANSI.RESET.getCode();
	private static final String BORDER = ANSI.BRIGHT_GREEN.getCode();
	private static final String HEADER = ANSI.BOLD.getCode() + ANSI.BRIGHT_WHITE.getCode();
	private static final String PROMPT = ANSI.BRIGHT_CYAN.getCode();
	private static final String KEY = ANSI.BOLD.getCode() + ANSI.BRIGHT_YELLOW.getCode();
	private static final String WIN = ANSI.BOLD.getCode() + ANSI.BRIGHT_GREEN.getCode();
	private static final String LOSE = ANSI.BOLD.getCode() + ANSI.BRIGHT_RED.getCode();
	private static final String TIE = ANSI.BOLD.getCode() + ANSI.BRIGHT_YELLOW.getCode();
	private static final String GOLD = ANSI.BOLD.getCode() + ANSI.YELLOW.getCode();

	/** When true, all pacing delays are skipped for near-instant play. */
	private boolean fastMode = false;

	public static Game getInstance() {
		if (instance == null) {
			return new Game();
		} else {
			return instance;
		}
	}

	public void playGame() {
		playGame(false);
	}

	public void playGame(boolean fastMode) {
		this.fastMode = fastMode;
		displayWelcomeMessage();
		try {
			gameLoop();
		} catch (HandValueException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
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

	private void gameLoop() throws HandValueException, InterruptedException {
		boolean gameActive = true;
		Deck deck = Deck.getInstance();
		Scanner scan = new Scanner(System.in);
		int wins = 0;
		int losses = 0;
		int ties = 0;

		while (gameActive) {
			System.out.println(Art.divider(TABLE_WIDTH));
			System.out.println(PROMPT + "Dealing cards..." + RESET + "\n");
			pause(500);

			Hand userHand = new Hand();
			Hand dealerHand = new Hand();

			Card fourthCard = dealInitialHands(deck, userHand, dealerHand);
			printInitialHands(userHand, dealerHand);

			if (userHand.isBlackJack()) {
				System.out.println(GOLD + "Blackjack!" + RESET);
				pause(1000);
			} else if (playerTurn(scan, deck, userHand)) {
				scan.close();
				return;
			}

			dealerTurn(deck, dealerHand, fourthCard, userHand);

			pause(800);
			Hand winner = determineWinner(userHand, dealerHand);

			if (winner == null) {
				System.out.println("\n" + TIE + "The round is a tie!" + RESET);
				ties++;
			} else if (winner.equals(userHand)) {
				System.out.println("\n" + WIN + "You win!" + RESET);
				wins++;
			} else {
				System.out.println("\n" + LOSE + "The dealer wins!" + RESET);
				losses++;
			}
			pause(800);

			printScorePanel(wins, losses, ties);

			pause(400);
			System.out.println(PROMPT + "Would you like to play again? " + option("Y", "es") + " or " + option("N", "o") + RESET);

			boolean roundOver = false;
			while (!roundOver) {
				String playAgain = scan.next();
				if (playAgain.equalsIgnoreCase("y")) {
					System.out.println(PROMPT + "Starting next round..." + RESET);
					pause(600);
					roundOver = true;
				} else if (playAgain.equalsIgnoreCase("n")) {
					gameActive = false;
					roundOver = true;
				} else if (playAgain.equalsIgnoreCase("q")) {
					scan.close();
					return;
				} else {
					System.out.println(LOSE + "Invalid input, try again" + RESET);
					pause(300);
				}
			}
			deck.reset();
		}
		scan.close();
	}

	/**
	 * Deals the opening two cards to each player, leaving the dealer's second card face down.
	 * @return The dealer's face-down card, for later reveal.
	 */
	private Card dealInitialHands(Deck deck, Hand userHand, Hand dealerHand) throws HandValueException, InterruptedException {
		userHand.hit(deck.deal());
		pause(300);
		dealerHand.hit(deck.deal());
		pause(300);
		userHand.hit(deck.deal());
		pause(300);

		Card fourthCard = deck.deal();
		fourthCard.setFaceDown(true);
		dealerHand.hit(fourthCard);
		return fourthCard;
	}

	private void printInitialHands(Hand userHand, Hand dealerHand) throws InterruptedException {
		System.out.println(HEADER + "Dealer's Hand:" + RESET);
		pause(500);
		System.out.println(dealerHand.toString());
		pause(400);

		System.out.println(HEADER + "Your Hand:" + RESET);
		System.out.println(userHand.toString());
		pause(400);
		System.out.println("Your hand value: " + colorValue(userHand));
		pause(300);
	}

	/**
	 * Runs the player's turn.
	 * @return true if the player chose to quit the game entirely
	 */
	private boolean playerTurn(Scanner scan, Deck deck, Hand userHand) throws HandValueException, InterruptedException {
		boolean hit = true;
		while (hit) {
			System.out.println("\n" + PROMPT + "Would you like to " + option("H", "it") + " or " + option("S", "tand") + "?" + RESET);
			String answer = scan.next();

			if (answer.equalsIgnoreCase("h")) {
				pause(400);
				Card card = deck.deal();
				userHand.hit(card);
				System.out.println(PROMPT + "You were dealt:" + RESET);
				pause(300);
				System.out.println(card.getAsciiArt());
				pause(300);
				System.out.println("Hand Value: " + colorValue(userHand));
				pause(300);
				if (userHand.isBust()) {
					System.out.println(LOSE + "Bust!" + RESET);
					pause(800);
					break;
				}

			} else if (answer.equalsIgnoreCase("s")) {
				hit = false;
				pause(300);
			} else if (answer.equalsIgnoreCase("q")) {
				return true;
			} else {
				System.out.println(LOSE + "Invalid input, please try again" + RESET);
				pause(300);
			}
		}
		return false;
	}

	private void dealerTurn(Deck deck, Hand dealerHand, Card fourthCard, Hand userHand) throws HandValueException, InterruptedException {
		pause(500);
		System.out.println("\n" + HEADER + "Dealer's turn:" + RESET);
		pause(400);
		fourthCard.setFaceDown(false);
		System.out.println(dealerHand.toString());
		System.out.println("Dealer Value: " + colorValue(dealerHand));
		pause(300);

		if (userHand.isBust()) {
			return;
		}

		if (dealerHand.isBlackJack()) {
			System.out.println(GOLD + "Dealer Blackjack!" + RESET);
			pause(700);
			return;
		}

		while (dealerHand.getValue() < 17) {
			pause(500);
			Card card = deck.deal();
			dealerHand.hit(card);
			System.out.println(PROMPT + "The dealer drew:" + RESET);
			pause(300);
			System.out.println(card.getAsciiArt());
			pause(300);
			System.out.println("Dealer Value: " + colorValue(dealerHand));
			pause(300);

			if (dealerHand.isBust()) {
				pause(500);
				System.out.println(WIN + "The dealer bust!" + RESET);
			}
		}
	}

	/**
	 * Determines the winner of a single round
	 *
	 * @param hand1 The first hand
	 * @param hand2 The second hand
	 * @return The hand that wins the round, null if a tie
	 */
	private Hand determineWinner(Hand hand1, Hand hand2) {
		if (hand1.isBlackJack() && hand2.isBlackJack()) {
			return null;
		}
		if (hand1.isBust() && hand2.isBust()) {
			return null;
		}
		if (hand1.getValue() > hand2.getValue() && !hand1.isBust()) {
			return hand1;
		} else if (hand1.isBust()) {
			return hand2;
		}

		if (hand2.getValue() > hand1.getValue() && !hand2.isBust()) {
			return hand2;
		} else if (hand2.isBust()) {
			return hand1;
		}
		return null;
	}

	private void printScorePanel(int wins, int losses, int ties) {
		String top = "╔" + "═".repeat(PANEL_WIDTH) + "╗";
		String divider = "╟" + "─".repeat(PANEL_WIDTH) + "╢";
		String bottom = "╚" + "═".repeat(PANEL_WIDTH) + "╝";

		System.out.println(BORDER + top + RESET);
		System.out.println(BORDER + "║" + RESET + HEADER + center("CURRENT RECORD", PANEL_WIDTH) + RESET + BORDER + "║" + RESET);
		System.out.println(BORDER + divider + RESET);
		System.out.println(BORDER + "║" + RESET + ANSI.BRIGHT_GREEN.getCode() + padRight(" Wins:    " + wins, PANEL_WIDTH) + RESET + BORDER + "║" + RESET);
		System.out.println(BORDER + "║" + RESET + ANSI.BRIGHT_RED.getCode() + padRight(" Losses:  " + losses, PANEL_WIDTH) + RESET + BORDER + "║" + RESET);
		System.out.println(BORDER + "║" + RESET + ANSI.BRIGHT_YELLOW.getCode() + padRight(" Ties:    " + ties, PANEL_WIDTH) + RESET + BORDER + "║" + RESET);
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
			System.out.println(GOLD + "Fast mode enabled — dealing at full speed." + RESET);
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

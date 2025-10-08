package twentyonegame;

import java.util.Scanner;

import twentyonegame.exception.HandValueException;

public class Game {
	
	static Game instance;
	
	public static Game getInstance() {
		if (instance == null) {
			return new Game();
		} else {
			return instance;
		}
	}

	public void playGame() {
		try {
			gameLoop();
		} catch (HandValueException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void displayExitMessage() {
	    System.out.println(ANSI.BOLD.getCode() + "===========================================" + ANSI.RESET.getCode());
	    System.out.println(
	        ANSI.RED.getCode() + "♠ ♥ ♦ ♣ " +
	        ANSI.RESET.getCode() + ANSI.BOLD.getCode() + "   THANKS FOR PLAYING 2 1 !   " +
	        ANSI.RESET.getCode() + ANSI.RED.getCode() + "♣ ♦ ♥ ♠" +
	        ANSI.RESET.getCode()
	    );
	    System.out.println(ANSI.BOLD.getCode() + "===========================================" + ANSI.RESET.getCode());
	    System.out.println();
	    System.out.println("We hope you had fun and didn’t bust too often!");
	    System.out.println("Come back anytime to test your luck against the dealer.");
	    System.out.println();
	    System.out.println(ANSI.BOLD.getCode() + "Goodbye!" + ANSI.RESET.getCode());
	    System.out.println();
	}


	private void gameLoop() throws HandValueException, InterruptedException {
		boolean gameActive = true;
		Deck deck = Deck.getInstance();
		Scanner scan = new Scanner(System.in);
		
		while (gameActive) {
			System.out.println("Dealing cards...");
			Thread.sleep(1000);
			Hand userHand = new Hand();
			Hand dealerHand = new Hand();
			
			Card firstCard = deck.deal(); 
			userHand.hit(firstCard);
			
			Card secondCard = deck.deal();
			dealerHand.hit(secondCard);
			
			Card thirdCard = deck.deal();
			userHand.hit(thirdCard);
			
			Card fourthCard = deck.deal();
			dealerHand.hit(fourthCard);
			
			System.out.println("Dealer's Hand:");
			Thread.sleep(2000);
			System.out.println(secondCard.getAsciiArt() + Art.faceDownArt);
			
			Thread.sleep(2000);
			System.out.println("Your Hand:");
			System.out.println(firstCard.getAsciiArt() + thirdCard.getAsciiArt());
			Thread.sleep(1000);
			System.out.println("Your hand value: " + userHand.getValue());
			
			Thread.sleep(500);
			
			if (userHand.isBlackJack()) {
				System.out.println("BlackJack!");
			}
			
			boolean hit = !userHand.isBlackJack();
			
			while (hit) {
				System.out.println("Would you like to [H]it or [S]tand? > ");
				String answer = scan.next();
				if (answer.toLowerCase().equals("h")) {
					Thread.sleep(1000);
					Card card = deck.deal();
					userHand.hit(card);
					System.out.println("You were dealt a: ");
					System.out.println(card.getAsciiArt());
					Thread.sleep(1000);
					if (userHand.isBust()) {
						System.out.println("Bust!");
						break;
					}
					System.out.println("Hand Value: " + userHand.getValue());
					
				} else if (answer.toLowerCase().equals("s")) {
					hit = false;
				} else {
					System.out.println("Invalid input, please try again");
					Thread.sleep(500);
				}
			}
			
			Thread.sleep(1000);
			System.out.println("Dealer's turn: ");
			System.out.println("Dealer's Hand:");
			Thread.sleep(1000);
			System.out.println(secondCard.getAsciiArt());
			Thread.sleep(500);
			System.out.println(fourthCard.getAsciiArt());
			System.out.println("Dealer Value: " + dealerHand.getValue());
			while (dealerHand.getValue() < 17) {
				Card card = deck.deal();
				dealerHand.hit(card);
				Thread.sleep(500);
				System.out.println("The dealer drew:\n" + card.getAsciiArt());
				if (dealerHand.isBust()) {
					Thread.sleep(1000);
					System.out.println("The dealer bust!");
				}
			}
			
			System.out.println("Dealer Value: " + dealerHand.getValue());
			
			Hand winner = determineWinner(userHand, dealerHand);
			Thread.sleep(2000);
			if (winner == null) {
				System.out.println("The round is a tie!");
			} else if (winner.equals(userHand)) {
				System.out.println("You win!");
			} else if (winner.equals(dealerHand)) {
				System.out.println("The dealer wins!");
			}
			
			System.out.println("Would you like to play again? [Y]es or [N]o");
			String playAgain = scan.next();
			while (true) {
				if (playAgain.toLowerCase().equals("y")) {
					System.out.println("Starting next round...");
					Thread.sleep(1000);
					break;
				} else if (playAgain.toLowerCase().equals("n")) {
					gameActive = false;
					break;
				} else {
					System.out.println("Invalid input, try again");
				}
			}
		}
	}
	
	/**
	 * Determines the winner of a single round
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
	
	public void displayWelcomeMessage() {
	    System.out.println(ANSI.BOLD.getCode() + "===========================================" + ANSI.RESET.getCode());
	    System.out.println(
	        ANSI.RED.getCode() + "♠ ♥ ♦ ♣ " + 
	        ANSI.RESET.getCode() + ANSI.BOLD.getCode() + "   W E L C O M E   T O   2 1 !   " + 
	        ANSI.RESET.getCode() + ANSI.RED.getCode() + "♣ ♦ ♥ ♠" + 
	        ANSI.RESET.getCode()
	    );
	    System.out.println(ANSI.BOLD.getCode() + "===========================================" + ANSI.RESET.getCode());
	    System.out.println();
	    System.out.println("You’ve entered the world of Blackjack —");
	    System.out.println("the classic casino card game where your goal");
	    System.out.println("is to beat the dealer by getting as close to 21");
	    System.out.println("as possible, without going over.");
	    System.out.println();
	    System.out.println(ANSI.BOLD.getCode() + "RULES:" + ANSI.RESET.getCode());
	    System.out.println(" - Face cards (J, Q, K) are worth 10.");
	    System.out.println(" - Aces are worth 1 or 11, whichever is better.");
	    System.out.println(" - You start with 2 cards. Dealer shows one.");
	    System.out.println(" - Type 'H' to Hit (draw a card).");
	    System.out.println(" - Type 'S' to Stand (end your turn).");
	    System.out.println(" - Type 'Q' to Quit the game at any time.");
	    System.out.println();
	    System.out.println(ANSI.BOLD.getCode() + "COMMANDS:" + ANSI.RESET.getCode());
	    System.out.println(" [H]it    → draw another card");
	    System.out.println(" [S]tand  → stop drawing cards");
	    System.out.println(" [Q]uit   → exit the game");
	    System.out.println();
	    System.out.println("Good luck — the house always *almost* wins...");
	    System.out.println();
	    System.out.println("┌───────────────────────────┐");
	    System.out.println("│     DEALING IN 3...2...1  │");
	    System.out.println("└───────────────────────────┘");
	    displayLogo();
	    System.out.println();
	}
	
	/**
	 * Prints the logo, the total width is 114 characters
	 * 114
	 */
	private void displayLogo() {
		System.out.println("==================================================================================================================");
		System.out.println();
		System.out.println(""
				+ ANSI.BRIGHT_RED.getCode() + " ███████████                                      █████              " + ANSI.WHITE.getCode() + "           " + ANSI.CYAN.getCode() + "    ███████                       \r\n"
				+ ANSI.BRIGHT_RED.getCode() + "░█░░░███░░░█                                     ░░███               " + ANSI.WHITE.getCode() + "           " + ANSI.CYAN.getCode() + "  ███░░░░░███                     \r\n"
				+ ANSI.BRIGHT_RED.getCode() + "░   ░███  ░  █████ ███ █████  ██████  ████████   ███████   █████ ████" + ANSI.WHITE.getCode() + "           " + ANSI.CYAN.getCode() + " ███     ░░███ ████████    ██████ \r\n"
				+ ANSI.BRIGHT_RED.getCode() + "    ░███    ░░███ ░███░░███  ███░░███░░███░░███ ░░░███░   ░░███ ░███ " + ANSI.WHITE.getCode() + " ██████████" + ANSI.CYAN.getCode() + "░███      ░███░░███░░███  ███░░███\r\n"
				+ ANSI.BRIGHT_RED.getCode() + "    ░███     ░███ ░███ ░███ ░███████  ░███ ░███   ░███     ░███ ░███ " + ANSI.WHITE.getCode() + "░░░░░░░░░░ " + ANSI.CYAN.getCode() + "░███      ░███ ░███ ░███ ░███████ \r\n"
				+ ANSI.BRIGHT_RED.getCode() + "    ░███     ░░███████████  ░███░░░   ░███ ░███   ░███ ███ ░███ ░███ " + ANSI.WHITE.getCode() + "           " + ANSI.CYAN.getCode() + "░░███     ███  ░███ ░███ ░███░░░  \r\n"
				+ ANSI.BRIGHT_RED.getCode() + "    █████     ░░████░████   ░░██████  ████ █████  ░░█████  ░░███████ " + ANSI.WHITE.getCode() + "           " + ANSI.CYAN.getCode() + " ░░░███████░   ████ █████░░██████ \r\n"
				+ ANSI.BRIGHT_RED.getCode() + "   ░░░░░       ░░░░ ░░░░     ░░░░░░  ░░░░ ░░░░░    ░░░░░    ░░░░░███ " + ANSI.WHITE.getCode() + "           " + ANSI.CYAN.getCode() + "   ░░░░░░░    ░░░░ ░░░░░  ░░░░░░  \r\n"
				+ ANSI.BRIGHT_RED.getCode() + "                                                            ███ ░███ " + ANSI.WHITE.getCode() + "           " + ANSI.CYAN.getCode() + "                                  \r\n"
				+ ANSI.BRIGHT_RED.getCode() + "                                                           ░░██████  " + ANSI.WHITE.getCode() + "           " + ANSI.CYAN.getCode() + "                                  \r\n"
				+ ANSI.BRIGHT_RED.getCode() + "                                                            ░░░░░░   " + ANSI.WHITE.getCode() + "           " + ANSI.RESET.getCode());
		System.out.println("==================================================================================================================");
	}
}

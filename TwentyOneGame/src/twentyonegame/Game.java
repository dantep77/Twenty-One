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
		displayWelcomeMessage();
		try {
			gameLoop();
		} catch (HandValueException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		displayExitMessage();
	}
	
	public void displayExitMessage() {
		System.out.println();
		System.out.println("Thanks for playing! See you next time!");
	}


	private void gameLoop() throws HandValueException, InterruptedException {
	    boolean gameActive = true;
	    Deck deck = Deck.getInstance();
	    Scanner scan = new Scanner(System.in);
	    int wins = 0;
	    int losses = 0;
	    int ties = 0;

	    while (gameActive) {
	        System.out.println("Dealing cards...\n");
	        Thread.sleep(800);

	        Hand userHand = new Hand();
	        Hand dealerHand = new Hand();

	        // Initial dealing
	        userHand.hit(deck.deal());
	        Thread.sleep(400);
	        dealerHand.hit(deck.deal());
	        Thread.sleep(400);
	        userHand.hit(deck.deal());
	        Thread.sleep(400);

	        Card fourthCard = deck.deal();
	        fourthCard.setFaceDown(true);
	        dealerHand.hit(fourthCard);

	        System.out.println("Dealer's Hand:");
	        Thread.sleep(800);
	        System.out.println(dealerHand.toString());
	        Thread.sleep(600);

	        System.out.println("Your Hand:");
	        System.out.println(userHand.toString());
	        Thread.sleep(600);
	        System.out.println("Your hand value: " + userHand.getValue());
	        Thread.sleep(500);

	        if (userHand.isBlackJack()) {
	            System.out.println("Blackjack!");
	            Thread.sleep(1200);
	        }

	        boolean hit = !userHand.isBlackJack();

	        // ---- Player's turn ----
	        while (hit) {
	            System.out.println("\nWould you like to [H]it or [S]tand?");
	            String answer = scan.next();

	            if (answer.equalsIgnoreCase("h")) {
	                Thread.sleep(600);
	                Card card = deck.deal();
	                userHand.hit(card);
	                System.out.println("You were dealt:");
	                Thread.sleep(400);
	                System.out.println(card.getAsciiArt());
	                Thread.sleep(500);

	                if (userHand.isBust()) {
	                    System.out.println("Bust!");
	                    Thread.sleep(1000);
	                    break;
	                }

	                System.out.println("Hand Value: " + userHand.getValue());
	                Thread.sleep(400);

	            } else if (answer.equalsIgnoreCase("s")) {
	                hit = false;
	                Thread.sleep(400);
	            } else if (answer.equalsIgnoreCase("q")) {
	                scan.close();
	                return;
	            } else {
	                System.out.println("Invalid input, please try again");
	                Thread.sleep(400);
	            }
	        }

	        // ---- Dealer's turn ----
	        Thread.sleep(700);
	        System.out.println("\nDealer's turn:");
	        Thread.sleep(600);
	        fourthCard.setFaceDown(false);
	        System.out.println(dealerHand.toString());
	        System.out.println("Dealer Value: " + dealerHand.getValue());
	        Thread.sleep(500);

	        if (dealerHand.isBlackJack()) {
	            System.out.println("Dealer Blackjack!");
	            Thread.sleep(1000);
	        } else {
	            while (dealerHand.getValue() < 17) {
	                Thread.sleep(700);
	                Card card = deck.deal();
	                dealerHand.hit(card);
	                System.out.println("The dealer drew:");
	                Thread.sleep(400);
	                System.out.println(card.getAsciiArt());
	                Thread.sleep(500);
	                System.out.println("Dealer Value: " + dealerHand.getValue());
	                Thread.sleep(500);

	                if (dealerHand.isBust()) {
	                    Thread.sleep(800);
	                    System.out.println("The dealer bust!");
	                }
	            }
	        }

	        // ---- Determine winner ----
	        Thread.sleep(1000);
	        Hand winner = determineWinner(userHand, dealerHand);

	        if (winner == null) {
	            System.out.println("\nThe round is a tie!");
	            ties++;
	        } else if (winner.equals(userHand)) {
	            System.out.println("\nYou win!");
	            wins++;
	        } else {
	            System.out.println("\nThe dealer wins!");
	            losses++;
	        }
	        Thread.sleep(1000);

	        System.out.println("-------------------");
	        System.out.println("| Current Record: |");
	        System.out.printf("| Wins: %9d |\n", wins);
	        System.out.printf("| Losses: %7d |\n", losses);
	        System.out.printf("| Ties: %9d |\n", ties);
	        System.out.print("-------------------\n");

	        Thread.sleep(500);
	        System.out.println("Would you like to play again? [Y]es or [N]o");

	        while (true) {
	            String playAgain = scan.next();
	            if (playAgain.equalsIgnoreCase("y")) {
	                System.out.println("Starting next round...");
	                Thread.sleep(800);
	                break;
	            } else if (playAgain.equalsIgnoreCase("n")) {
	                gameActive = false;
	                break;
	            } else if (playAgain.equalsIgnoreCase("q")) {
	                scan.close();
	                return;
	            } else {
	                System.out.println("Invalid input, try again");
	                Thread.sleep(400);
	            }
	        }
	        deck.reset();
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
	    System.out.println("Welcome to...");
	    System.out.println();
	    displayLogo();
	    System.out.println();
	}
	
	/**
	 * Prints the logo, the total width is 114 characters
	 * 114
	 */
	private void displayLogo() {
		System.out.println(Art.bigLogo);
	}
}

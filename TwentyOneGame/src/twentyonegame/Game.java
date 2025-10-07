package twentyonegame;

public class Game {

	public static void main(String[] args) {
		displayWelcomeMessage();
	}
	
	public static void displayWelcomeMessage() {
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
	    System.out.println();
	}


}

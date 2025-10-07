package twentyonegame;


public enum Suit {
	HEARTS("Hearts", "♥", ANSI.BRIGHT_RED),
	DIAMONDS("Diamonds", "♦", ANSI.BRIGHT_RED),
	CLUBS("Clubs", "♣", ANSI.CYAN),
	SPADES("Spades", "♠", ANSI.CYAN);
	
	private final String suit;
	
	private final String cardLabel;
	
	private final ANSI colorCode;
	
	Suit(String suit, String cardLabel, ANSI colorCode) {
		this.suit = suit;
		this.cardLabel = cardLabel;
		this.colorCode = colorCode;
	}
	
	String getSuit() {
		return this.suit;
	}
	
	String getCardLabel() {
		return this.cardLabel;
	}	
	
	String getColorCode() {
		return this.colorCode.getCode();
	}
	

}

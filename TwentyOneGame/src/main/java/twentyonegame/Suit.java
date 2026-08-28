package twentyonegame;


public enum Suit {
	HEARTS("Hearts", "H", ANSI.BRIGHT_RED),
	DIAMONDS("Diamonds", "D", ANSI.BRIGHT_RED),
	CLUBS("Clubs", "C", ANSI.CYAN),
	SPADES("Spades", "S", ANSI.CYAN);
	
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
		return this.colorCode.getCode() + this.cardLabel + ANSI.RESET.getCode();
	}	
	
	String getColorCode() {
		return this.colorCode.getCode();
	}
	

}

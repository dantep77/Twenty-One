package twentyonegame;

public enum Rank {

	TWO("Two", "2",2, 1),
	THREE("Three", "3", 3, 2),
	FOUR("Four", "4", 4, 3),
	FIVE("Five", "5", 5, 4),
	SIX("Six", "6", 6, 5),
	SEVEN("Seven", "7", 7, 6),
	EIGHT("Eight", "8", 8, 7),
	NINE("Nine", "9", 9, 8),
	TEN("Ten", "10", 10, 9),
	JACK("Jack", "J", 10, 10),
	QUEEN("Queen", "Q", 10, 11),
	KING("King", "K", 10, 12),
	ACE("Ace", "A", 11, 13);
	
	private final String rank;
	
	private final String cardLabel;
	
	private final int value;
	
	private final int ordinal;
	
	Rank(String rank, String cardLabel, int value, int ordinal) {
		this.rank = rank;
		this.cardLabel = cardLabel;
		this.value = value;
		this.ordinal = ordinal;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public String getRank() {
		return this.rank;
	}
	
	public int getOrdinal() {
		return this.ordinal;
	}
	
	public String getCardLabel() {
		return this.cardLabel;
	}

}



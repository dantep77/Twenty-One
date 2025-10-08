package twentyonegame;

public class Card implements Comparable<Card> {

	/** The BlackJack value of this card 2-11 */
	private int value;

	/** The rank of this card: 2 - A */
	private Rank rank;

	/** The suit of this card */
	private Suit suit;
	
	/** If the card is face down */
	private boolean isFaceDown;

	/**
	 * Constructs a card with suit and rank
	 * 
	 * @param suit The suit of this card
	 * @param rank The rank of this card
	 */
	public Card(Suit suit, Rank rank) {
		setSuit(suit);
		setRank(rank);
		setValue();
	}

	public Suit getSuit() {
		return this.suit;
	}

	public void setSuit(Suit suit) {
		this.suit = suit;
	}

	public Rank getRank() {
		return this.rank;
	}

	public void setRank(Rank rank) {
		this.rank = rank;
	}

	public int getValue() {
		return this.value;
	}

	private void setValue() {
		this.value = rank.getValue();
	}
	
	public boolean isFaceDown() {
		return isFaceDown;
	}
	
	public void setFaceDown(boolean bool) {
		this.isFaceDown = bool;
	}

	public boolean isFace() {
		Rank rank = getRank();
		return rank.equals(Rank.JACK) || rank.equals(Rank.KING) || rank.equals(Rank.QUEEN);
	}

	public String getAsciiArt() {
		String rankStr = getRank().getCardLabel();
		String suitStr = getSuit().getCardLabel();
		StringBuilder sb = new StringBuilder();
		sb.append("┌─────┐\n");
		if (!getRank().equals(Rank.TEN)) sb.append("|" + rankStr + "    |\n");
		else sb.append("|" + rankStr + "   |\n");
		sb.append("|  " + suitStr + "  |\n");
		if (!getRank().equals(Rank.TEN)) sb.append("|    " + rankStr + "|\n");
		else sb.append("|   " + rankStr + "|\n");
		sb.append("└─────┘\n");
		return sb.toString();
	}

	@Override
	public int compareTo(Card o) {
		if (o == null) {
			throw new NullPointerException("Cannot compare to null Card");
		}

		int rankCmp = rank.compareTo(o.getRank());
		return rankCmp == 0 ? suit.compareTo(o.getSuit()) : rankCmp;
	}

}

package twentyonegame;

public class Dealer extends Player {

	/** The dealer stands on any hand valued at or above this total, except a soft 17 */
	private static final int STAND_VALUE = 17;

	public Dealer() {
		super("Dealer", 0);
	}

	/**
	 * Gets the dealer's single hand, creating an empty one if needed.
	 *
	 * @return The dealer's hand
	 */
	public Hand getHand() {
		if (getHands().isEmpty()) {
			addHand(new Hand());
		}
		return getHands().get(0);
	}

	/**
	 * Discards the dealer's hand and starts a fresh, empty one for the next round.
	 */
	public void resetHand() {
		resetHands();
		addHand(new Hand());
	}

	/**
	 * The dealer's face-up card, shown to the player before the hole card is revealed.
	 *
	 * @return The dealer's up card
	 */
	public Card getUpCard() {
		return getHand().getCards().get(0);
	}

	/**
	 * Standard casino rule: the dealer hits on any total below 17, and also
	 * hits a soft 17 (an ace counted as 11).
	 *
	 * @return True if the dealer must take another card
	 */
	public boolean shouldHit() {
		Hand hand = getHand();
		if (hand.getValue() < STAND_VALUE) {
			return true;
		}
		return hand.getValue() == STAND_VALUE && hand.isSoft();
	}

}

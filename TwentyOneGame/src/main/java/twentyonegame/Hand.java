package twentyonegame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import twentyonegame.exception.HandValueException;

public class Hand {

	/** The list of cards */
	private ArrayList<Card> cards;

	/** The value of the hand */
	private int value;

	/** Whether the current value counts an ace as 11 */
	private boolean soft;

	/** The amount wagered on this hand */
	private int bet;

	/** Whether this hand has already doubled down */
	private boolean doubled;

	/** Whether this hand has been surrendered */
	private boolean surrendered;

	/** Whether this hand was formed by splitting a pair of aces */
	private boolean splitAces;

	/**
	 * Constructs a new empty hand with no bet.
	 */
	public Hand() {
		this(0);
	}

	/**
	 * Constructs a new empty hand with the given bet.
	 *
	 * @param bet The amount wagered on this hand
	 */
	public Hand(int bet) {
		cards = new ArrayList<Card>();
		value = 0;
		soft = false;
		this.bet = bet;
	}

	/**
	 * Simulates adding a new card to the hand. Handles differing Ace values.
	 *
	 * @param card The new card to add to the hand.
	 * @return The value of the hand after adding the card
	 * @throws HandValueException If the value of the hand is already 21 or greater
	 */
	public int hit(Card card) throws HandValueException {
		if (value >= 21) {
			throw new HandValueException("Cannot hit a hand already at or above 21");
		}

		cards.add(card);
		recalculateValue();
		return value;
	}

	/**
	 * Recomputes the hand's value, softly reducing aces from 11 to 1 as needed.
	 */
	private void recalculateValue() {
		int total = 0;
		int aceCount = 0;
		for (Card c : cards) {
			total += c.getValue();
			if (c.getRank().equals(Rank.ACE)) aceCount++;
		}

		int acesAsEleven = aceCount;
		while (total > 21 && acesAsEleven > 0) {
			total -= 10;
			acesAsEleven--;
		}

		this.value = total;
		this.soft = acesAsEleven > 0;
	}

	/**
	 * Removes the second card dealt to this hand so it can start a new, split
	 * hand. Only valid on a two-card hand.
	 *
	 * @return The card removed from this hand
	 */
	public Card removeCardForSplit() {
		if (cards.size() != 2) {
			throw new IllegalStateException("Can only split a hand with exactly two cards");
		}
		Card removed = cards.remove(1);
		recalculateValue();
		return removed;
	}

	/**
	 * Checks if the hand is bust
	 *
	 * @return True if the hand is bust, false if not
	 */
	public boolean isBust() {
		return value > 21;
	}

	/**
	 * Checks if the hand is a natural blackjack (21 on the first two cards, not
	 * resulting from a split).
	 *
	 * @return True if the hand is blackjack, false if not.
	 */
	public boolean isBlackJack() {
		return !splitAces && cards.size() == 2 && value == 21;
	}

	/**
	 * Checks if the hand currently counts an ace as 11.
	 *
	 * @return True if the hand is soft, false if not.
	 */
	public boolean isSoft() {
		return soft;
	}

	/**
	 * Checks if this hand is eligible to be split into two hands.
	 *
	 * @return True if the hand can be split
	 */
	public boolean canSplit() {
		return cards.size() == 2 && !doubled && !surrendered
				&& cards.get(0).getRank().equals(cards.get(1).getRank());
	}

	/**
	 * Checks if this hand is eligible to double down.
	 *
	 * @return True if the hand can double down
	 */
	public boolean canDoubleDown() {
		return cards.size() == 2 && !doubled && !surrendered && !splitAces;
	}

	/**
	 * Doubles this hand's bet, deals exactly one more card, and locks the hand
	 * from further action.
	 *
	 * @param card The single card dealt for the double down
	 * @return The value of the hand after the card is added
	 * @throws HandValueException If the value of the hand is already 21 or greater
	 */
	public int doubleDown(Card card) throws HandValueException {
		if (!canDoubleDown()) {
			throw new IllegalStateException("This hand is not eligible to double down");
		}
		bet *= 2;
		doubled = true;
		return hit(card);
	}

	/**
	 * Marks this hand as surrendered, forfeiting half of the bet.
	 */
	public void surrender() {
		surrendered = true;
	}

	public boolean isSurrendered() {
		return surrendered;
	}

	public boolean isDoubled() {
		return doubled;
	}

	/**
	 * Marks this hand as having originated from a split pair of aces. Such
	 * hands receive exactly one card and cannot be hit, doubled, or treated as
	 * a natural blackjack even when the total is 21.
	 */
	public void markSplitAces() {
		this.splitAces = true;
	}

	public boolean isSplitAces() {
		return splitAces;
	}

	public int getBet() {
		return bet;
	}

	public int getValue() {
		return value;
	}

	public List<Card> getCards() {
		return Collections.unmodifiableList(cards);
	}

	/**
	 * Returns a string representation of the hand.
	 */
	public String toString() {

		if (cards.size() == 0) {
			return "";
		}

		StringBuilder sb = new StringBuilder();

		for (Card c : cards) {
			sb.append("┌─────┐ ");
		}
		sb.append("\n");
		for (Card c : cards) {
			String rankLabel;
			if (c.isFaceDown()) rankLabel = "?";
			else rankLabel = c.getRank().getCardLabel();
			if (c.getRank().equals(Rank.TEN) && !c.isFaceDown()) sb.append("|10   | ");
			else sb.append("|" + rankLabel + "    | ");
		}
		sb.append("\n");
		for (Card c : cards) {
			String suitLabel;
			if (c.isFaceDown()) suitLabel = "?";
			else suitLabel = c.getSuit().getCardLabel();
			sb.append("|  " + suitLabel + "  | ");
		}
		sb.append("\n");
		for (Card c : cards) {
			String rankLabel;
			if (c.isFaceDown()) rankLabel = "?";
			else rankLabel = c.getRank().getCardLabel();
			if (c.getRank().equals(Rank.TEN) && !c.isFaceDown()) sb.append("|   10| ");
			else sb.append("|    " + rankLabel + "| ");
		}
		sb.append("\n");
		for (Card c : cards) {
			sb.append("└─────┘ ");
		}
		sb.append("\n");
		return sb.toString();
	}
}

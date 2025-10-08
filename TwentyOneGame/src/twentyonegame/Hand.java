package twentyonegame;

import java.util.ArrayList;

import twentyonegame.exception.HandValueException;

public class Hand {
	
	/** The list of cards */
	private ArrayList<Card> cards;
	
	/** The value of the hand*/
	private int value;
	
	/**
	 * Constructs a new empty hand
	 */
	public Hand() {
		cards = new ArrayList<Card>();
		value = 0;
	}
	
	/**
	 * Simulates adding a new card to the hand. Handles differing Ace values.
	 * @param card The new card to add to the hand.
	 * @return The value of the hand after adding the card
	 * @throws HandValueException If the value of the hand is 21 or greater
	 */
	public int hit(Card card) throws HandValueException {
		int value = this.value;
		if (value >= 21) {
			throw new HandValueException();
		}
		
		cards.add(card);
		
		// Process the new value
		value = 0;
		int aceCount = 0;
		for (Card c : cards) {
			value += c.getValue();
			if (card.getRank().equals(Rank.ACE)) aceCount++;
		}
		
		while (value > 21 && aceCount > 0) {
			value -= 10;
			aceCount--;
		}
		
		this.value = value;
		return value;
	}
	
	/**
	 * Checks if the hand is bust
	 * @return True if the hand is bust, false if not
	 */
	public boolean isBust() {
		return value > 21;
	}
	
	/**
	 * Checks if the hand is blackjack 
	 * @return True if the hand is blackjack, false if not.
	 */
	public boolean isBlackJack() {
		return cards.size() == 2 && value == 21;
	}
	
	public int getValue() {
		return value;
	}
	
	/**
	 * Returns a string representation of the hand.
	 */
	@SuppressWarnings("unused")
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

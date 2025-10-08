package twentyonegame;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {

	/** The cards in the deck */
	private ArrayList<Card> cards;
	
	/** The Deck instance */
	private static Deck instance;
	
	/**
	 * Creates a new deck with a deck and shuffles.
	 */
	private Deck() {
		loadDeck();
	}
	
	/**
	 * Deals a card from the deck
	 * @return A card from the top of the deck
	 */
	public Card deal() {
		return cards.removeLast();
	}
	
	/**
	 * Gets the instance of the deck
	 * @return The deck instance
	 */
	public static Deck getInstance() {
		if (instance == null) {
			return new Deck();
		} else {
			return instance;
		}
	}
	
	/**
	 * Resets the deck by adding all cards back and shuffling
	 */
	public void reset() {
		loadDeck();
		shuffle();
	}
	
	/**
	 * Loads the deck with all cards and shuffles.
	 */
	private void loadDeck() {
		cards = new ArrayList<Card>();
		Rank[] ranks = Rank.values();
		Suit[] suits = Suit.values();
		
		for (Rank rank : ranks) {
			for (Suit suit: suits) {
				cards.add(new Card(suit, rank));
			}
		}
		shuffle();
	}
	
	/**
	 * Shuffles the deck
	 */
	private void shuffle() {
		Collections.shuffle(cards);
	}
	
}

package twentyonegame;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {

	/** Number of standard 52-card decks combined into this shoe */
	public static final int DEFAULT_NUM_DECKS = 6;

	/** Reshuffle once remaining cards fall to this fraction of the full shoe */
	public static final double PENETRATION = 0.25;

	/** The cards in the deck */
	private ArrayList<Card> cards;

	/** How many standard decks make up this shoe */
	private final int numDecks;

	/** The Deck instance */
	private static Deck instance;

	/**
	 * Creates a new shoe of DEFAULT_NUM_DECKS decks and shuffles.
	 */
	private Deck() {
		this(DEFAULT_NUM_DECKS);
	}

	/**
	 * Creates a new shoe made up of the given number of standard decks and shuffles.
	 *
	 * @param numDecks The number of 52-card decks to combine into this shoe
	 */
	private Deck(int numDecks) {
		this.numDecks = numDecks;
		loadDeck();
	}

	/**
	 * Deals a card from the deck, reshuffling a fresh shoe first if it is empty.
	 *
	 * @return A card from the top of the deck
	 */
	public Card deal() {
		if (cards.isEmpty()) {
			loadDeck();
		}
		return cards.removeLast();
	}

	/**
	 * Gets the instance of the deck
	 *
	 * @return The deck instance
	 */
	public static Deck getInstance() {
		if (instance == null) {
			instance = new Deck();
		}
		return instance;
	}

	/**
	 * Checks whether the shoe has been depleted past its penetration threshold
	 * and should be reshuffled before the next round.
	 *
	 * @return True if the shoe needs to be reshuffled
	 */
	public boolean needsReshuffle() {
		return cards.size() < numDecks * 52 * PENETRATION;
	}

	/**
	 * How many cards remain in the shoe.
	 *
	 * @return The number of undealt cards
	 */
	public int remainingCards() {
		return cards.size();
	}

	/**
	 * Resets the deck by adding all cards back and shuffling
	 */
	public void reset() {
		loadDeck();
	}

	/**
	 * Loads the deck with all cards and shuffles.
	 */
	private void loadDeck() {
		cards = new ArrayList<Card>();
		Rank[] ranks = Rank.values();
		Suit[] suits = Suit.values();

		for (int i = 0; i < numDecks; i++) {
			for (Rank rank : ranks) {
				for (Suit suit : suits) {
					cards.add(new Card(suit, rank));
				}
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

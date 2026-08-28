package twentyonegame;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeckTest {

	private Deck deck;
	private static final int FULL_SHOE_SIZE = Deck.DEFAULT_NUM_DECKS * 52;

	@BeforeEach
	void freshShoe() {
		deck = Deck.getInstance();
		deck.reset();
	}

	@Test
	void getInstanceReturnsSameInstance() {
		Deck first = Deck.getInstance();
		Deck second = Deck.getInstance();
		assertTrue(first == second, "Deck.getInstance() must return a singleton");
	}

	@Test
	void resetFillsFullShoe() {
		assertEquals(FULL_SHOE_SIZE, deck.remainingCards());
	}

	@Test
	void dealReturnsCardAndReducesCount() {
		int before = deck.remainingCards();
		Card card = deck.deal();
		assertNotNull(card);
		assertEquals(before - 1, deck.remainingCards());
	}

	@Test
	void freshShoeDoesNotNeedReshuffle() {
		assertFalse(deck.needsReshuffle());
	}

	@Test
	void needsReshuffleOncePenetrationThresholdCrossed() {
		int threshold = (int) (FULL_SHOE_SIZE * Deck.PENETRATION);
		while (deck.remainingCards() >= threshold) {
			deck.deal();
		}
		assertTrue(deck.needsReshuffle());
	}

	@Test
	void dealingPastEmptyShoeAutoReplenishes() {
		for (int i = 0; i < FULL_SHOE_SIZE; i++) {
			deck.deal();
		}
		assertEquals(0, deck.remainingCards());

		Card card = deck.deal();
		assertNotNull(card);
		assertEquals(FULL_SHOE_SIZE - 1, deck.remainingCards());
	}
}

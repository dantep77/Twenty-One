package twentyonegame;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CardTest {

	@Test
	void valueMatchesRank() {
		Card card = new Card(Suit.SPADES, Rank.KING);
		assertEquals(10, card.getValue());
	}

	@Test
	void aceValueIsEleven() {
		Card card = new Card(Suit.HEARTS, Rank.ACE);
		assertEquals(11, card.getValue());
	}

	@Test
	void faceCardsAreRecognized() {
		assertTrue(new Card(Suit.CLUBS, Rank.JACK).isFace());
		assertTrue(new Card(Suit.CLUBS, Rank.QUEEN).isFace());
		assertTrue(new Card(Suit.CLUBS, Rank.KING).isFace());
		assertFalse(new Card(Suit.CLUBS, Rank.TEN).isFace());
		assertFalse(new Card(Suit.CLUBS, Rank.ACE).isFace());
	}

	@Test
	void faceDownDefaultsFalse() {
		Card card = new Card(Suit.DIAMONDS, Rank.FIVE);
		assertFalse(card.isFaceDown());
		card.setFaceDown(true);
		assertTrue(card.isFaceDown());
	}

	@Test
	void compareToOrdersByRankThenSuit() {
		Card twoHearts = new Card(Suit.HEARTS, Rank.TWO);
		Card threeHearts = new Card(Suit.HEARTS, Rank.THREE);
		assertTrue(twoHearts.compareTo(threeHearts) < 0);
		assertTrue(threeHearts.compareTo(twoHearts) > 0);
	}

	@Test
	void compareToNullThrows() {
		Card card = new Card(Suit.HEARTS, Rank.TWO);
		assertThrows(NullPointerException.class, () -> card.compareTo(null));
	}
}

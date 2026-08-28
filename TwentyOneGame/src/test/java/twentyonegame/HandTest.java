package twentyonegame;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import twentyonegame.exception.HandValueException;

class HandTest {

	@Test
	void emptyHandHasZeroValue() {
		Hand hand = new Hand();
		assertEquals(0, hand.getValue());
	}

	@Test
	void hitAddsCardValue() throws HandValueException {
		Hand hand = new Hand();
		hand.hit(new Card(Suit.HEARTS, Rank.SEVEN));
		hand.hit(new Card(Suit.CLUBS, Rank.NINE));
		assertEquals(16, hand.getValue());
	}

	@Test
	void softAceCountsAsEleven() throws HandValueException {
		Hand hand = new Hand();
		hand.hit(new Card(Suit.HEARTS, Rank.ACE));
		hand.hit(new Card(Suit.CLUBS, Rank.SIX));
		assertEquals(17, hand.getValue());
		assertTrue(hand.isSoft());
	}

	@Test
	void aceReducesToOneToAvoidBust() throws HandValueException {
		Hand hand = new Hand();
		hand.hit(new Card(Suit.HEARTS, Rank.ACE));
		hand.hit(new Card(Suit.CLUBS, Rank.SIX));
		hand.hit(new Card(Suit.SPADES, Rank.NINE));
		assertEquals(16, hand.getValue());
		assertFalse(hand.isSoft());
		assertFalse(hand.isBust());
	}

	@Test
	void multipleAcesReduceAsNeeded() throws HandValueException {
		Hand hand = new Hand();
		hand.hit(new Card(Suit.HEARTS, Rank.ACE));
		hand.hit(new Card(Suit.CLUBS, Rank.ACE));
		hand.hit(new Card(Suit.SPADES, Rank.NINE));
		// 11 + 1 + 9 = 21
		assertEquals(21, hand.getValue());
		assertTrue(hand.isSoft());
	}

	@Test
	void bustHandIsDetected() throws HandValueException {
		Hand hand = new Hand();
		hand.hit(new Card(Suit.HEARTS, Rank.KING));
		hand.hit(new Card(Suit.CLUBS, Rank.QUEEN));
		hand.hit(new Card(Suit.SPADES, Rank.TWO));
		assertTrue(hand.isBust());
	}

	@Test
	void naturalBlackjackDetected() throws HandValueException {
		Hand hand = new Hand();
		hand.hit(new Card(Suit.HEARTS, Rank.ACE));
		hand.hit(new Card(Suit.CLUBS, Rank.KING));
		assertTrue(hand.isBlackJack());
	}

	@Test
	void twentyOneOnThreeCardsIsNotBlackjack() throws HandValueException {
		Hand hand = new Hand();
		hand.hit(new Card(Suit.HEARTS, Rank.SEVEN));
		hand.hit(new Card(Suit.CLUBS, Rank.SEVEN));
		hand.hit(new Card(Suit.SPADES, Rank.SEVEN));
		assertEquals(21, hand.getValue());
		assertFalse(hand.isBlackJack());
	}

	@Test
	void hittingAtOrAboveTwentyOneThrows() throws HandValueException {
		Hand hand = new Hand();
		hand.hit(new Card(Suit.HEARTS, Rank.KING));
		hand.hit(new Card(Suit.CLUBS, Rank.QUEEN));
		hand.hit(new Card(Suit.SPADES, Rank.TWO));
		assertTrue(hand.isBust());
		assertThrows(HandValueException.class, () -> hand.hit(new Card(Suit.DIAMONDS, Rank.TWO)));
	}

	@Test
	void canSplitMatchingRanks() throws HandValueException {
		Hand hand = new Hand(50);
		hand.hit(new Card(Suit.HEARTS, Rank.EIGHT));
		hand.hit(new Card(Suit.CLUBS, Rank.EIGHT));
		assertTrue(hand.canSplit());
	}

	@Test
	void cannotSplitDifferentRanks() throws HandValueException {
		Hand hand = new Hand(50);
		hand.hit(new Card(Suit.HEARTS, Rank.EIGHT));
		hand.hit(new Card(Suit.CLUBS, Rank.NINE));
		assertFalse(hand.canSplit());
	}

	@Test
	void splitMovesSecondCardOut() throws HandValueException {
		Hand hand = new Hand(50);
		Card first = new Card(Suit.HEARTS, Rank.EIGHT);
		Card second = new Card(Suit.CLUBS, Rank.EIGHT);
		hand.hit(first);
		hand.hit(second);

		Card removed = hand.removeCardForSplit();
		assertEquals(second, removed);
		assertEquals(1, hand.getCards().size());
		assertEquals(8, hand.getValue());
	}

	@Test
	void doubleDownDoublesBetAndAddsOneCard() throws HandValueException {
		Hand hand = new Hand(50);
		hand.hit(new Card(Suit.HEARTS, Rank.FIVE));
		hand.hit(new Card(Suit.CLUBS, Rank.SIX));
		assertTrue(hand.canDoubleDown());

		hand.doubleDown(new Card(Suit.SPADES, Rank.TEN));

		assertEquals(100, hand.getBet());
		assertTrue(hand.isDoubled());
		assertEquals(3, hand.getCards().size());
		assertFalse(hand.canDoubleDown());
	}

	@Test
	void surrenderMarksHandSurrendered() {
		Hand hand = new Hand(50);
		assertFalse(hand.isSurrendered());
		hand.surrender();
		assertTrue(hand.isSurrendered());
	}

	@Test
	void splitAcesGetOnlyOneCardAndAreNotBlackjack() throws HandValueException {
		Hand hand = new Hand(50);
		hand.markSplitAces();
		hand.hit(new Card(Suit.HEARTS, Rank.ACE));
		hand.hit(new Card(Suit.CLUBS, Rank.KING));
		assertEquals(21, hand.getValue());
		assertFalse(hand.isBlackJack());
		assertFalse(hand.canDoubleDown());
	}

	@Test
	void getCardsIsUnmodifiable() throws HandValueException {
		Hand hand = new Hand();
		hand.hit(new Card(Suit.HEARTS, Rank.TWO));
		assertThrows(UnsupportedOperationException.class,
				() -> hand.getCards().add(new Card(Suit.CLUBS, Rank.THREE)));
	}
}

package twentyonegame;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import twentyonegame.exception.HandValueException;

class DealerTest {

	@Test
	void hitsBelowSeventeen() throws HandValueException {
		Dealer dealer = new Dealer();
		dealer.getHand().hit(new Card(Suit.HEARTS, Rank.TEN));
		dealer.getHand().hit(new Card(Suit.CLUBS, Rank.SIX));
		assertTrue(dealer.shouldHit());
	}

	@Test
	void standsOnHardSeventeen() throws HandValueException {
		Dealer dealer = new Dealer();
		dealer.getHand().hit(new Card(Suit.HEARTS, Rank.TEN));
		dealer.getHand().hit(new Card(Suit.CLUBS, Rank.SEVEN));
		assertFalse(dealer.shouldHit());
	}

	@Test
	void hitsOnSoftSeventeen() throws HandValueException {
		Dealer dealer = new Dealer();
		dealer.getHand().hit(new Card(Suit.HEARTS, Rank.ACE));
		dealer.getHand().hit(new Card(Suit.CLUBS, Rank.SIX));
		assertEquals(17, dealer.getHand().getValue());
		assertTrue(dealer.getHand().isSoft());
		assertTrue(dealer.shouldHit());
	}

	@Test
	void standsOnHandsAboveSeventeen() throws HandValueException {
		Dealer dealer = new Dealer();
		dealer.getHand().hit(new Card(Suit.HEARTS, Rank.TEN));
		dealer.getHand().hit(new Card(Suit.CLUBS, Rank.NINE));
		assertFalse(dealer.shouldHit());
	}

	@Test
	void upCardIsFirstCardDealt() throws HandValueException {
		Dealer dealer = new Dealer();
		Card first = new Card(Suit.HEARTS, Rank.KING);
		dealer.getHand().hit(first);
		dealer.getHand().hit(new Card(Suit.CLUBS, Rank.SEVEN));
		assertEquals(first, dealer.getUpCard());
	}

	@Test
	void resetHandGivesFreshEmptyHand() throws HandValueException {
		Dealer dealer = new Dealer();
		dealer.getHand().hit(new Card(Suit.HEARTS, Rank.KING));
		dealer.resetHand();
		assertEquals(0, dealer.getHand().getValue());
		assertTrue(dealer.getHand().getCards().isEmpty());
	}
}

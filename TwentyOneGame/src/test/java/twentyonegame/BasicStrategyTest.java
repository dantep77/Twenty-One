package twentyonegame;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import twentyonegame.exception.HandValueException;
import twentyonegame.BasicStrategy.Action;
import twentyonegame.BasicStrategy.Recommendation;

class BasicStrategyTest {

	private Hand handOf(Rank... ranks) throws HandValueException {
		Hand hand = new Hand();
		Suit[] suits = { Suit.HEARTS, Suit.CLUBS, Suit.DIAMONDS, Suit.SPADES };
		int i = 0;
		for (Rank r : ranks) {
			hand.hit(new Card(suits[i % suits.length], r));
			i++;
		}
		return hand;
	}

	private Card dealer(Rank rank) {
		return new Card(Suit.HEARTS, rank);
	}

	@Test
	void hardLowTotalAlwaysHits() throws HandValueException {
		Hand hand = handOf(Rank.THREE, Rank.FOUR); // hard 7
		assertEquals(Action.HIT, BasicStrategy.recommend(hand, dealer(Rank.SIX), true, true, true));
	}

	@Test
	void hardElevenDoublesAgainstNonAce() throws HandValueException {
		Hand hand = handOf(Rank.SIX, Rank.FIVE); // hard 11
		assertEquals(Action.DOUBLE, BasicStrategy.recommend(hand, dealer(Rank.NINE), true, true, true));
	}

	@Test
	void hardElevenHitsAgainstAceWhenDoubleUnavailableFallsBackToHit() throws HandValueException {
		Hand hand = handOf(Rank.SIX, Rank.FIVE, Rank.TWO); // 13, three cards, no double
		assertEquals(Action.HIT, BasicStrategy.recommend(hand, dealer(Rank.ACE), false, false, false));
	}

	@Test
	void hardTwelveStandsAgainstMiddleUpcards() throws HandValueException {
		Hand hand = handOf(Rank.SEVEN, Rank.FIVE); // hard 12
		assertEquals(Action.STAND, BasicStrategy.recommend(hand, dealer(Rank.FIVE), true, true, true));
	}

	@Test
	void hardTwelveHitsAgainstLowOrHighUpcards() throws HandValueException {
		Hand hand = handOf(Rank.SEVEN, Rank.FIVE); // hard 12
		assertEquals(Action.HIT, BasicStrategy.recommend(hand, dealer(Rank.TWO), true, true, true));
		assertEquals(Action.HIT, BasicStrategy.recommend(hand, dealer(Rank.SEVEN), true, true, true));
	}

	@Test
	void hardSixteenSurrendersAgainstTenWhenAvailable() throws HandValueException {
		Hand hand = handOf(Rank.NINE, Rank.SEVEN); // hard 16
		assertEquals(Action.SURRENDER, BasicStrategy.recommend(hand, dealer(Rank.TEN), true, false, true));
	}

	@Test
	void hardSixteenHitsAgainstTenWhenSurrenderUnavailable() throws HandValueException {
		Hand hand = handOf(Rank.NINE, Rank.SEVEN); // hard 16
		assertEquals(Action.HIT, BasicStrategy.recommend(hand, dealer(Rank.TEN), true, false, false));
	}

	@Test
	void hardSeventeenAlwaysStands() throws HandValueException {
		Hand hand = handOf(Rank.TEN, Rank.SEVEN); // hard 17
		assertEquals(Action.STAND, BasicStrategy.recommend(hand, dealer(Rank.ACE), true, false, true));
	}

	@Test
	void softEighteenDoublesAgainstWeakUpcards() throws HandValueException {
		Hand hand = handOf(Rank.ACE, Rank.SEVEN); // soft 18
		assertEquals(Action.DOUBLE, BasicStrategy.recommend(hand, dealer(Rank.FOUR), true, false, true));
	}

	@Test
	void softEighteenStandsAgainstTwoSevenEight() throws HandValueException {
		Hand hand = handOf(Rank.ACE, Rank.SEVEN); // soft 18
		assertEquals(Action.STAND, BasicStrategy.recommend(hand, dealer(Rank.TWO), true, false, true));
		assertEquals(Action.STAND, BasicStrategy.recommend(hand, dealer(Rank.SEVEN), true, false, true));
		assertEquals(Action.STAND, BasicStrategy.recommend(hand, dealer(Rank.EIGHT), true, false, true));
	}

	@Test
	void softEighteenHitsAgainstStrongUpcards() throws HandValueException {
		Hand hand = handOf(Rank.ACE, Rank.SEVEN); // soft 18
		assertEquals(Action.HIT, BasicStrategy.recommend(hand, dealer(Rank.NINE), true, false, true));
		assertEquals(Action.HIT, BasicStrategy.recommend(hand, dealer(Rank.ACE), true, false, true));
	}

	@Test
	void softNineteenAlwaysStands() throws HandValueException {
		Hand hand = handOf(Rank.ACE, Rank.EIGHT); // soft 19
		assertEquals(Action.STAND, BasicStrategy.recommend(hand, dealer(Rank.SIX), true, false, true));
	}

	@Test
	void pairOfAcesAlwaysSplits() throws HandValueException {
		Hand hand = handOf(Rank.ACE, Rank.ACE);
		assertEquals(Action.SPLIT, BasicStrategy.recommend(hand, dealer(Rank.TEN), true, true, true));
	}

	@Test
	void pairOfAcesWithoutSplitTreatedAsSoftTwelveHits() throws HandValueException {
		Hand hand = handOf(Rank.ACE, Rank.ACE);
		assertEquals(Action.HIT, BasicStrategy.recommend(hand, dealer(Rank.TEN), true, false, true));
	}

	@Test
	void pairOfFivesNeverSplitsDoublesInstead() throws HandValueException {
		Hand hand = handOf(Rank.FIVE, Rank.FIVE);
		assertEquals(Action.DOUBLE, BasicStrategy.recommend(hand, dealer(Rank.SIX), true, true, true));
	}

	@Test
	void pairOfTensNeverSplitsStandsInstead() throws HandValueException {
		Hand hand = handOf(Rank.KING, Rank.QUEEN);
		assertEquals(Action.STAND, BasicStrategy.recommend(hand, dealer(Rank.SIX), true, true, true));
	}

	@Test
	void pairOfEightsAlwaysSplitsEvenAgainstAce() throws HandValueException {
		Hand hand = handOf(Rank.EIGHT, Rank.EIGHT);
		assertEquals(Action.SPLIT, BasicStrategy.recommend(hand, dealer(Rank.ACE), true, true, true));
	}

	@Test
	void pairOfNinesStandsAgainstSeven() throws HandValueException {
		Hand hand = handOf(Rank.NINE, Rank.NINE);
		assertEquals(Action.STAND, BasicStrategy.recommend(hand, dealer(Rank.SEVEN), true, true, true));
	}

	@Test
	void pairOfNinesSplitsAgainstSix() throws HandValueException {
		Hand hand = handOf(Rank.NINE, Rank.NINE);
		assertEquals(Action.SPLIT, BasicStrategy.recommend(hand, dealer(Rank.SIX), true, true, true));
	}

	@Test
	void doubleDowngradesToHitWhenUnavailable() throws HandValueException {
		Hand hand = handOf(Rank.SIX, Rank.FOUR); // hard 10
		assertEquals(Action.HIT, BasicStrategy.recommend(hand, dealer(Rank.FIVE), false, false, false));
	}

	@Test
	void recommendWithReasonMatchesRecommendAction() throws HandValueException {
		Hand hand = handOf(Rank.SIX, Rank.FIVE); // hard 11
		Recommendation recommendation = BasicStrategy.recommendWithReason(hand, dealer(Rank.NINE), true, true, true);
		assertEquals(Action.DOUBLE, recommendation.action());
	}

	@Test
	void everyRecommendationIncludesANonEmptyReason() throws HandValueException {
		Hand hand = handOf(Rank.NINE, Rank.SEVEN); // hard 16
		Recommendation recommendation = BasicStrategy.recommendWithReason(hand, dealer(Rank.TEN), true, false, true);
		assertEquals(Action.SURRENDER, recommendation.action());
		assertFalse(recommendation.reason().isBlank());
	}

	@Test
	void reasonExplainsFallbackWhenDoubleUnavailable() throws HandValueException {
		Hand hand = handOf(Rank.SIX, Rank.FOUR); // hard 10
		Recommendation recommendation = BasicStrategy.recommendWithReason(hand, dealer(Rank.FIVE), false, false, false);
		assertEquals(Action.HIT, recommendation.action());
		assertFalse(recommendation.reason().isBlank());
	}
}

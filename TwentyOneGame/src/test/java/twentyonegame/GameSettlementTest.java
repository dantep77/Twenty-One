package twentyonegame;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import twentyonegame.exception.HandValueException;
import twentyonegame.exception.InsufficientFundsException;

class GameSettlementTest {

	private Hand handOf(int bet, Rank... ranks) throws HandValueException {
		Hand hand = new Hand(bet);
		Suit[] suits = { Suit.HEARTS, Suit.CLUBS, Suit.DIAMONDS, Suit.SPADES };
		int i = 0;
		for (Rank r : ranks) {
			hand.hit(new Card(suits[i % suits.length], r));
			i++;
		}
		return hand;
	}

	@Test
	void playerBustsLosesBet() throws HandValueException {
		Game game = new Game();
		Player player = new Player("You", 1000);
		Hand playerHand = handOf(100, Rank.KING, Rank.QUEEN, Rank.TWO);
		Hand dealerHand = handOf(0, Rank.TEN, Rank.SEVEN);

		game.settleHand(playerHand, dealerHand, player);

		assertEquals(1000, player.getChips());
		assertEquals(1, game.getLosses());
	}

	@Test
	void dealerBustsPlayerWins() throws HandValueException {
		Game game = new Game();
		Player player = new Player("You", 1000);
		Hand playerHand = handOf(100, Rank.TEN, Rank.SEVEN);
		Hand dealerHand = handOf(0, Rank.TEN, Rank.QUEEN, Rank.FIVE);

		game.settleHand(playerHand, dealerHand, player);

		assertEquals(1200, player.getChips());
		assertEquals(1, game.getWins());
	}

	@Test
	void playerBlackjackPaysThreeToTwo() throws HandValueException {
		Game game = new Game();
		Player player = new Player("You", 1000);
		Hand playerHand = handOf(100, Rank.ACE, Rank.KING);
		Hand dealerHand = handOf(0, Rank.TEN, Rank.SEVEN);

		game.settleHand(playerHand, dealerHand, player);

		assertEquals(1250, player.getChips());
		assertEquals(1, game.getWins());
	}

	@Test
	void dealerBlackjackBeatsPlainTwentyOne() throws HandValueException {
		Game game = new Game();
		Player player = new Player("You", 1000);
		Hand playerHand = handOf(100, Rank.SEVEN, Rank.SEVEN, Rank.SEVEN);
		Hand dealerHand = handOf(0, Rank.ACE, Rank.KING);

		game.settleHand(playerHand, dealerHand, player);

		assertEquals(1000, player.getChips());
		assertEquals(1, game.getLosses());
	}

	@Test
	void bothBlackjackIsPush() throws HandValueException {
		Game game = new Game();
		Player player = new Player("You", 1000);
		Hand playerHand = handOf(100, Rank.ACE, Rank.QUEEN);
		Hand dealerHand = handOf(0, Rank.ACE, Rank.KING);

		game.settleHand(playerHand, dealerHand, player);

		assertEquals(1100, player.getChips());
		assertEquals(1, game.getTies());
	}

	@Test
	void higherValueWins() throws HandValueException {
		Game game = new Game();
		Player player = new Player("You", 1000);
		Hand playerHand = handOf(100, Rank.TEN, Rank.NINE);
		Hand dealerHand = handOf(0, Rank.TEN, Rank.SEVEN);

		game.settleHand(playerHand, dealerHand, player);

		assertEquals(1200, player.getChips());
		assertEquals(1, game.getWins());
	}

	@Test
	void lowerValueLoses() throws HandValueException {
		Game game = new Game();
		Player player = new Player("You", 1000);
		Hand playerHand = handOf(100, Rank.TEN, Rank.SIX);
		Hand dealerHand = handOf(0, Rank.TEN, Rank.NINE);

		game.settleHand(playerHand, dealerHand, player);

		assertEquals(1000, player.getChips());
		assertEquals(1, game.getLosses());
	}

	@Test
	void equalValueIsPush() throws HandValueException {
		Game game = new Game();
		Player player = new Player("You", 1000);
		Hand playerHand = handOf(100, Rank.TEN, Rank.EIGHT);
		Hand dealerHand = handOf(0, Rank.TEN, Rank.EIGHT);

		game.settleHand(playerHand, dealerHand, player);

		assertEquals(1100, player.getChips());
		assertEquals(1, game.getTies());
	}

	@Test
	void surrenderedHandCountsAsLossWithNoAdditionalChipMovement() throws HandValueException {
		Game game = new Game();
		Player player = new Player("You", 950); // half of 100 bet already refunded at surrender time
		Hand playerHand = handOf(100, Rank.TEN, Rank.SIX);
		playerHand.surrender();
		Hand dealerHand = handOf(0, Rank.TEN, Rank.NINE);

		game.settleHand(playerHand, dealerHand, player);

		assertEquals(950, player.getChips());
		assertEquals(1, game.getLosses());
	}

	@Test
	void splitAcesTwentyOneIsNotPaidAsBlackjack() throws HandValueException {
		Game game = new Game();
		Player player = new Player("You", 1000);
		Hand playerHand = handOf(100, Rank.ACE, Rank.KING);
		playerHand.markSplitAces();
		Hand dealerHand = handOf(0, Rank.TEN, Rank.SEVEN);

		game.settleHand(playerHand, dealerHand, player);

		// Even value (21 vs 17) wins, but only 1:1, not 3:2.
		assertEquals(1200, player.getChips());
		assertEquals(1, game.getWins());
	}

	@Test
	void splitHandCreatesTwoHandsAndDeductsAdditionalBet() throws HandValueException, InsufficientFundsException {
		Game game = new Game();
		Deck deck = Deck.getInstance();
		deck.reset();
		Player player = new Player("You", 1000);
		Hand hand = player.placeBet(100);
		hand.hit(new Card(Suit.HEARTS, Rank.EIGHT));
		hand.hit(new Card(Suit.CLUBS, Rank.EIGHT));

		game.splitHand(hand, 0, player, deck);

		assertEquals(2, player.getHands().size());
		assertEquals(800, player.getChips());
		assertEquals(2, hand.getCards().size());
		assertEquals(2, player.getHands().get(1).getCards().size());
	}

	@Test
	void splitAcesDealsExactlyOneCardEach() throws HandValueException, InsufficientFundsException {
		Game game = new Game();
		Deck deck = Deck.getInstance();
		deck.reset();
		Player player = new Player("You", 1000);
		Hand hand = player.placeBet(100);
		hand.hit(new Card(Suit.HEARTS, Rank.ACE));
		hand.hit(new Card(Suit.CLUBS, Rank.ACE));

		game.splitHand(hand, 0, player, deck);

		Hand second = player.getHands().get(1);
		assertEquals(2, hand.getCards().size());
		assertEquals(2, second.getCards().size());
		assertEquals(true, hand.isSplitAces());
		assertEquals(true, second.isSplitAces());
	}
}

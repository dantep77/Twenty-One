package twentyonegame;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import twentyonegame.exception.HandValueException;
import twentyonegame.exception.InsufficientFundsException;
import twentyonegame.exception.QuitGameException;

class GamePlayerActionsTest {

	private Scanner scannerOf(String input) {
		return new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
	}

	@Test
	void standEndsTurnWithoutChangingHand() throws HandValueException, InsufficientFundsException, InterruptedException {
		Game game = new Game();
		Deck deck = Deck.getInstance();
		deck.reset();
		Player player = new Player("You", 1000);
		Dealer dealer = new Dealer();
		Hand hand = player.placeBet(100);
		hand.hit(new Card(Suit.HEARTS, Rank.TEN));
		hand.hit(new Card(Suit.CLUBS, Rank.SIX));

		try (Scanner scan = scannerOf("s\n")) {
			game.playSingleHand(hand, 0, player, dealer, deck, scan);
		}

		assertEquals(2, hand.getCards().size());
		assertEquals(16, hand.getValue());
	}

	@Test
	void hitAddsCardToHand() throws HandValueException, InsufficientFundsException, InterruptedException {
		Game game = new Game();
		Deck deck = Deck.getInstance();
		deck.reset();
		Player player = new Player("You", 1000);
		Dealer dealer = new Dealer();
		Hand hand = player.placeBet(100);
		hand.hit(new Card(Suit.HEARTS, Rank.TWO));
		hand.hit(new Card(Suit.CLUBS, Rank.THREE));

		try (Scanner scan = scannerOf("h\ns\n")) {
			game.playSingleHand(hand, 0, player, dealer, deck, scan);
		}

		assertEquals(3, hand.getCards().size());
	}

	@Test
	void doubleDownDeductsChipsAndEndsTurnAfterOneCard() throws HandValueException, InsufficientFundsException,
			InterruptedException {
		Game game = new Game();
		Deck deck = Deck.getInstance();
		deck.reset();
		Player player = new Player("You", 1000);
		Dealer dealer = new Dealer();
		Hand hand = player.placeBet(100);
		hand.hit(new Card(Suit.HEARTS, Rank.FIVE));
		hand.hit(new Card(Suit.CLUBS, Rank.SIX));

		try (Scanner scan = scannerOf("d\n")) {
			game.playSingleHand(hand, 0, player, dealer, deck, scan);
		}

		assertEquals(800, player.getChips()); // 1000 - 100 (initial bet) - 100 (double)
		assertEquals(200, hand.getBet());
		assertEquals(3, hand.getCards().size());
		assertTrue(hand.isDoubled());
	}

	@Test
	void surrenderRefundsHalfBetAndEndsTurn() throws HandValueException, InsufficientFundsException, InterruptedException {
		Game game = new Game();
		Deck deck = Deck.getInstance();
		deck.reset();
		Player player = new Player("You", 900);
		Dealer dealer = new Dealer();
		Hand hand = player.placeBet(100);
		hand.hit(new Card(Suit.HEARTS, Rank.TEN));
		hand.hit(new Card(Suit.CLUBS, Rank.SIX));

		try (Scanner scan = scannerOf("r\n")) {
			game.playSingleHand(hand, 0, player, dealer, deck, scan);
		}

		assertTrue(hand.isSurrendered());
		assertEquals(850, player.getChips()); // 800 after bet + 50 refunded
	}

	@Test
	void invalidInputIsReprompted() throws HandValueException, InsufficientFundsException, InterruptedException {
		Game game = new Game();
		Deck deck = Deck.getInstance();
		deck.reset();
		Player player = new Player("You", 1000);
		Dealer dealer = new Dealer();
		Hand hand = player.placeBet(100);
		hand.hit(new Card(Suit.HEARTS, Rank.TEN));
		hand.hit(new Card(Suit.CLUBS, Rank.SIX));

		try (Scanner scan = scannerOf("bogus\ns\n")) {
			game.playSingleHand(hand, 0, player, dealer, deck, scan);
		}

		assertEquals(2, hand.getCards().size());
	}

	@Test
	void quitDuringActionThrowsQuitGameException() throws InsufficientFundsException, HandValueException {
		Game game = new Game();
		Deck deck = Deck.getInstance();
		deck.reset();
		Player player = new Player("You", 1000);
		Dealer dealer = new Dealer();
		Hand hand = player.placeBet(100);
		hand.hit(new Card(Suit.HEARTS, Rank.TEN));
		hand.hit(new Card(Suit.CLUBS, Rank.SIX));

		try (Scanner scan = scannerOf("q\n")) {
			assertThrows(QuitGameException.class,
					() -> game.playSingleHand(hand, 0, player, dealer, deck, scan));
		}
	}

	@Test
	void blackjackHandAutoStandsWithoutPrompting() throws HandValueException, InsufficientFundsException,
			InterruptedException {
		Game game = new Game();
		Deck deck = Deck.getInstance();
		deck.reset();
		Player player = new Player("You", 1000);
		Dealer dealer = new Dealer();
		Hand hand = player.placeBet(100);
		hand.hit(new Card(Suit.HEARTS, Rank.ACE));
		hand.hit(new Card(Suit.CLUBS, Rank.KING));

		// No input consumed - should return immediately without reading from scanner.
		try (Scanner scan = scannerOf("")) {
			game.playSingleHand(hand, 0, player, dealer, deck, scan);
		}

		assertEquals(2, hand.getCards().size());
		assertEquals(21, hand.getValue());
	}
}

package twentyonegame;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import twentyonegame.exception.HandValueException;
import twentyonegame.exception.InsufficientFundsException;

class GameCoachTest {

	private Scanner scannerOf(String input) {
		return new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
	}

	@Test
	void correctDecisionIsTalliedAsCorrect() throws HandValueException, InsufficientFundsException,
			InterruptedException {
		Game game = new Game();
		game.setCoachMode(true);
		Deck deck = Deck.getInstance();
		deck.reset();
		Player player = new Player("You", 1000);
		Dealer dealer = new Dealer();
		dealer.getHand().hit(new Card(Suit.HEARTS, Rank.SIX));
		Hand hand = player.placeBet(100);
		hand.hit(new Card(Suit.HEARTS, Rank.TEN));
		hand.hit(new Card(Suit.CLUBS, Rank.SEVEN)); // hard 17 - basic strategy says stand

		try (Scanner scan = scannerOf("s\n")) {
			game.playSingleHand(hand, 0, player, dealer, deck, scan);
		}

		assertEquals(1, game.getCoachTotal());
		assertEquals(1, game.getCoachCorrect());
	}

	@Test
	void incorrectDecisionIsTalliedAsIncorrect() throws HandValueException, InsufficientFundsException,
			InterruptedException {
		Game game = new Game();
		game.setCoachMode(true);
		Deck deck = Deck.getInstance();
		deck.reset();
		Player player = new Player("You", 1000);
		Dealer dealer = new Dealer();
		dealer.getHand().hit(new Card(Suit.HEARTS, Rank.SIX));
		Hand hand = player.placeBet(100);
		hand.hit(new Card(Suit.HEARTS, Rank.SIX));
		hand.hit(new Card(Suit.CLUBS, Rank.FOUR)); // hard 10 vs dealer 6 - strategy says double, we stand

		try (Scanner scan = scannerOf("s\n")) {
			game.playSingleHand(hand, 0, player, dealer, deck, scan);
		}

		assertEquals(1, game.getCoachTotal());
		assertEquals(0, game.getCoachCorrect());
	}

	@Test
	void coachModeOffRecordsNothing() throws HandValueException, InsufficientFundsException, InterruptedException {
		Game game = new Game();
		Deck deck = Deck.getInstance();
		deck.reset();
		Player player = new Player("You", 1000);
		Dealer dealer = new Dealer();
		dealer.getHand().hit(new Card(Suit.HEARTS, Rank.SIX));
		Hand hand = player.placeBet(100);
		hand.hit(new Card(Suit.HEARTS, Rank.TEN));
		hand.hit(new Card(Suit.CLUBS, Rank.SEVEN));

		try (Scanner scan = scannerOf("s\n")) {
			game.playSingleHand(hand, 0, player, dealer, deck, scan);
		}

		assertEquals(0, game.getCoachTotal());
		assertEquals(0, game.getCoachCorrect());
	}
}

package twentyonegame;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import twentyonegame.exception.QuitGameException;

class GamePromptTest {

	private Scanner scannerOf(String input) {
		return new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
	}

	@Test
	void promptForBetAcceptsValidAmount() {
		Game game = new Game();
		Player player = new Player("You", 500);
		try (Scanner scan = scannerOf("100\n")) {
			assertEquals(100, game.promptForBet(scan, player));
		}
	}

	@Test
	void promptForBetRejectsBelowMinimumThenAcceptsValid() {
		Game game = new Game();
		Player player = new Player("You", 500);
		try (Scanner scan = scannerOf("1\n50\n")) {
			assertEquals(50, game.promptForBet(scan, player));
		}
	}

	@Test
	void promptForBetRejectsAboveBalanceThenAcceptsValid() {
		Game game = new Game();
		Player player = new Player("You", 500);
		try (Scanner scan = scannerOf("9999\n200\n")) {
			assertEquals(200, game.promptForBet(scan, player));
		}
	}

	@Test
	void promptForBetRejectsNonNumericThenAcceptsValid() {
		Game game = new Game();
		Player player = new Player("You", 500);
		try (Scanner scan = scannerOf("abc\n75\n")) {
			assertEquals(75, game.promptForBet(scan, player));
		}
	}

	@Test
	void promptForBetQuitThrows() {
		Game game = new Game();
		Player player = new Player("You", 500);
		try (Scanner scan = scannerOf("q\n")) {
			assertThrows(QuitGameException.class, () -> game.promptForBet(scan, player));
		}
	}

	@Test
	void insuranceAcceptedReturnsTrue() {
		Game game = new Game();
		Player player = new Player("You", 500);
		try (Scanner scan = scannerOf("y\n")) {
			assertTrue(game.promptInsurance(scan, player, 100));
		}
	}

	@Test
	void insuranceDeclinedReturnsFalse() {
		Game game = new Game();
		Player player = new Player("You", 500);
		try (Scanner scan = scannerOf("n\n")) {
			assertFalse(game.promptInsurance(scan, player, 100));
		}
	}

	@Test
	void insuranceUnaffordableAutomaticallyDeclines() {
		Game game = new Game();
		Player player = new Player("You", 10);
		try (Scanner scan = scannerOf("")) {
			assertFalse(game.promptInsurance(scan, player, 100));
		}
	}

	@Test
	void insuranceInvalidInputRepromptsThenAccepts() {
		Game game = new Game();
		Player player = new Player("You", 500);
		try (Scanner scan = scannerOf("maybe\ny\n")) {
			assertTrue(game.promptInsurance(scan, player, 100));
		}
	}
}

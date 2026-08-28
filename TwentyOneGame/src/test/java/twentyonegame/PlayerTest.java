package twentyonegame;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import twentyonegame.exception.InsufficientFundsException;

class PlayerTest {

	@Test
	void startsWithGivenChips() {
		Player player = new Player("Alice", 500);
		assertEquals(500, player.getChips());
	}

	@Test
	void addChipsIncreasesBalance() {
		Player player = new Player("Alice", 500);
		player.addChips(100);
		assertEquals(600, player.getChips());
	}

	@Test
	void deductChipsDecreasesBalance() throws InsufficientFundsException {
		Player player = new Player("Alice", 500);
		player.deductChips(200);
		assertEquals(300, player.getChips());
	}

	@Test
	void deductMoreThanBalanceThrows() {
		Player player = new Player("Alice", 100);
		assertThrows(InsufficientFundsException.class, () -> player.deductChips(200));
		assertEquals(100, player.getChips(), "balance must be unchanged after a failed deduction");
	}

	@Test
	void placeBetCreatesHandAndDeductsChips() throws InsufficientFundsException {
		Player player = new Player("Alice", 500);
		Hand hand = player.placeBet(50);
		assertEquals(450, player.getChips());
		assertEquals(50, hand.getBet());
		assertTrue(player.getHands().contains(hand));
	}

	@Test
	void placeBetBeyondBalanceThrowsAndDoesNotCreateHand() {
		Player player = new Player("Alice", 30);
		assertThrows(InsufficientFundsException.class, () -> player.placeBet(50));
		assertTrue(player.getHands().isEmpty());
	}

	@Test
	void isBrokeWhenChipsRunOut() throws InsufficientFundsException {
		Player player = new Player("Alice", 10);
		assertFalse(player.isBroke());
		player.deductChips(10);
		assertTrue(player.isBroke());
	}

	@Test
	void resetHandsClearsAllHands() throws InsufficientFundsException {
		Player player = new Player("Alice", 500);
		player.placeBet(50);
		player.placeBet(50);
		assertEquals(2, player.getHands().size());
		player.resetHands();
		assertTrue(player.getHands().isEmpty());
	}
}

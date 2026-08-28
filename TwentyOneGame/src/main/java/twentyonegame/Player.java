package twentyonegame;

import java.util.ArrayList;
import java.util.List;

import twentyonegame.exception.InsufficientFundsException;

public class Player {

	/** The player's display name */
	private String name;

	/** The player's chip bankroll */
	private int chips;

	/** The hands currently in play for this player (more than one after a split) */
	private List<Hand> hands;

	public Player(String name, int chips) {
		this.name = name;
		this.chips = chips;
		this.hands = new ArrayList<Hand>();
	}

	public String getName() {
		return name;
	}

	public int getChips() {
		return chips;
	}

	/**
	 * Adds chips to the player's bankroll, e.g. winnings or a returned bet.
	 *
	 * @param amount The number of chips to add
	 */
	public void addChips(int amount) {
		this.chips += amount;
	}

	/**
	 * Deducts chips from the player's bankroll.
	 *
	 * @param amount The number of chips to deduct
	 * @throws InsufficientFundsException If the player does not have enough chips
	 */
	public void deductChips(int amount) throws InsufficientFundsException {
		if (amount > chips) {
			throw new InsufficientFundsException(
					"Not enough chips: have " + chips + ", need " + amount);
		}
		this.chips -= amount;
	}

	public boolean isBroke() {
		return chips <= 0;
	}

	public List<Hand> getHands() {
		return hands;
	}

	/**
	 * Clears all hands, e.g. at the start of a new round.
	 */
	public void resetHands() {
		hands.clear();
	}

	public void addHand(Hand hand) {
		hands.add(hand);
	}

	/**
	 * Deducts the wager from the player's bankroll and starts a new hand with
	 * that bet.
	 *
	 * @param amount The amount to wager
	 * @return The newly created hand
	 * @throws InsufficientFundsException If the player does not have enough chips
	 */
	public Hand placeBet(int amount) throws InsufficientFundsException {
		deductChips(amount);
		Hand hand = new Hand(amount);
		hands.add(hand);
		return hand;
	}

}

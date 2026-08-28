package twentyonegame;

/**
 * Recommends the statistically optimal action for a hand against a dealer
 * up card, following standard multi-deck blackjack basic strategy (dealer
 * hits soft 17, late surrender, double after split allowed).
 */
public final class BasicStrategy {

	public enum Action {
		HIT, STAND, DOUBLE, SPLIT, SURRENDER
	}

	private BasicStrategy() {
	}

	/**
	 * Recommends the best action for the given hand and dealer up card.
	 *
	 * @param hand          The hand to act on
	 * @param dealerUpCard  The dealer's face-up card
	 * @param canDouble     Whether doubling down is currently a legal option
	 * @param canSplit      Whether splitting is currently a legal option
	 * @param canSurrender  Whether surrendering is currently a legal option
	 * @return The recommended action, downgraded to a legal one if the ideal
	 *         action (double or split) is not currently available
	 */
	public static Action recommend(Hand hand, Card dealerUpCard, boolean canDouble, boolean canSplit,
			boolean canSurrender) {
		int dealerValue = dealerUpCard.getRank().equals(Rank.ACE) ? 11 : dealerUpCard.getValue();

		Action ideal;
		if (canSplit && isPair(hand)) {
			ideal = recommendPair(hand.getCards().get(0).getRank(), dealerValue);
		} else if (hand.isSoft()) {
			ideal = recommendSoft(hand.getValue(), dealerValue);
		} else {
			ideal = recommendHard(hand.getValue(), dealerValue, canSurrender);
		}

		if (ideal == Action.DOUBLE && !canDouble) {
			return Action.HIT;
		}
		return ideal;
	}

	private static boolean isPair(Hand hand) {
		return hand.getCards().size() == 2
				&& hand.getCards().get(0).getRank().equals(hand.getCards().get(1).getRank());
	}

	private static Action recommendPair(Rank rank, int dealerValue) {
		switch (rank) {
			case ACE:
				return Action.SPLIT;
			case TWO:
			case THREE:
				return dealerValue <= 7 ? Action.SPLIT : Action.HIT;
			case FOUR:
				return (dealerValue == 5 || dealerValue == 6) ? Action.SPLIT : Action.HIT;
			case FIVE:
				return dealerValue <= 9 ? Action.DOUBLE : Action.HIT;
			case SIX:
				return dealerValue <= 6 ? Action.SPLIT : Action.HIT;
			case SEVEN:
				return dealerValue <= 7 ? Action.SPLIT : Action.HIT;
			case EIGHT:
				return Action.SPLIT;
			case NINE:
				return (dealerValue <= 6 || dealerValue == 8 || dealerValue == 9) ? Action.SPLIT : Action.STAND;
			case TEN:
			case JACK:
			case QUEEN:
			case KING:
				return Action.STAND;
			default:
				return Action.HIT;
		}
	}

	private static Action recommendHard(int total, int dealerValue, boolean canSurrender) {
		if (total <= 8) return Action.HIT;
		if (total == 9) return (dealerValue >= 3 && dealerValue <= 6) ? Action.DOUBLE : Action.HIT;
		if (total == 10) return dealerValue <= 9 ? Action.DOUBLE : Action.HIT;
		if (total == 11) return dealerValue <= 10 ? Action.DOUBLE : Action.HIT;
		if (total == 12) return (dealerValue >= 4 && dealerValue <= 6) ? Action.STAND : Action.HIT;
		if (total >= 13 && total <= 14) return dealerValue <= 6 ? Action.STAND : Action.HIT;
		if (total == 15) {
			if (canSurrender && dealerValue == 10) return Action.SURRENDER;
			return dealerValue <= 6 ? Action.STAND : Action.HIT;
		}
		if (total == 16) {
			if (canSurrender && (dealerValue == 9 || dealerValue == 10 || dealerValue == 11)) {
				return Action.SURRENDER;
			}
			return dealerValue <= 6 ? Action.STAND : Action.HIT;
		}
		return Action.STAND;
	}

	private static Action recommendSoft(int total, int dealerValue) {
		switch (total) {
			case 12:
				return Action.HIT;
			case 13:
			case 14:
				return (dealerValue == 5 || dealerValue == 6) ? Action.DOUBLE : Action.HIT;
			case 15:
			case 16:
				return (dealerValue >= 4 && dealerValue <= 6) ? Action.DOUBLE : Action.HIT;
			case 17:
				return (dealerValue >= 3 && dealerValue <= 6) ? Action.DOUBLE : Action.HIT;
			case 18:
				if (dealerValue >= 3 && dealerValue <= 6) return Action.DOUBLE;
				if (dealerValue == 2 || dealerValue == 7 || dealerValue == 8) return Action.STAND;
				return Action.HIT;
			default:
				return Action.STAND;
		}
	}
}

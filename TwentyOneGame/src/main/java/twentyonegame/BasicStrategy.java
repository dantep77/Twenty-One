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

	/** A recommended action, paired with a short explanation of why. */
	public record Recommendation(Action action, String reason) {
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
		return recommendWithReason(hand, dealerUpCard, canDouble, canSplit, canSurrender).action();
	}

	/**
	 * Recommends the best action for the given hand and dealer up card, along
	 * with a short explanation of the reasoning behind it.
	 *
	 * @param hand          The hand to act on
	 * @param dealerUpCard  The dealer's face-up card
	 * @param canDouble     Whether doubling down is currently a legal option
	 * @param canSplit      Whether splitting is currently a legal option
	 * @param canSurrender  Whether surrendering is currently a legal option
	 * @return The recommended action and why, downgraded to a legal action if
	 *         the ideal action (double or split) is not currently available
	 */
	public static Recommendation recommendWithReason(Hand hand, Card dealerUpCard, boolean canDouble,
			boolean canSplit, boolean canSurrender) {
		String dealerLabel = dealerUpCard.getRank().equals(Rank.ACE) ? "Ace" : String.valueOf(dealerUpCard.getValue());
		int dealerValue = dealerUpCard.getRank().equals(Rank.ACE) ? 11 : dealerUpCard.getValue();

		Recommendation ideal;
		if (canSplit && isPair(hand)) {
			ideal = recommendPair(hand.getCards().get(0).getRank(), dealerValue, dealerLabel);
		} else if (hand.isSoft()) {
			ideal = recommendSoft(hand.getValue(), dealerValue, dealerLabel);
		} else {
			ideal = recommendHard(hand.getValue(), dealerValue, dealerLabel, canSurrender);
		}

		if (ideal.action() == Action.DOUBLE && !canDouble) {
			return new Recommendation(Action.HIT,
					"Doubling isn't available right now, and the total is still low enough that hitting is correct.");
		}
		return ideal;
	}

	private static boolean isPair(Hand hand) {
		return hand.getCards().size() == 2
				&& hand.getCards().get(0).getRank().equals(hand.getCards().get(1).getRank());
	}

	private static Recommendation recommendPair(Rank rank, int dealerValue, String dealerLabel) {
		switch (rank) {
			case ACE:
				return new Recommendation(Action.SPLIT,
						"Always split Aces - two hands starting at 11 are far stronger than one soft 12.");
			case TWO:
			case THREE:
				return dealerValue <= 7
						? new Recommendation(Action.SPLIT, "Dealer " + dealerLabel
								+ " is weak, so splitting turns one mediocre hand into two hands with good odds.")
						: new Recommendation(Action.HIT, "Dealer " + dealerLabel
								+ " is too strong to profit from splitting - just hit.");
			case FOUR:
				return (dealerValue == 5 || dealerValue == 6)
						? new Recommendation(Action.SPLIT,
								"A pair of 4s is only worth splitting against a dealer 5 or 6, where the dealer is likely to bust.")
						: new Recommendation(Action.HIT,
								"Splitting 4s creates two weak hands unless the dealer is showing a 5 or 6 - hit instead.");
			case FIVE:
				return dealerValue <= 9
						? new Recommendation(Action.DOUBLE,
								"A pair of 5s totals 10 - never split it, and against dealer " + dealerLabel
										+ " it's a strong total to double instead.")
						: new Recommendation(Action.HIT, "A pair of 5s totals 10, but dealer " + dealerLabel
								+ " is too strong to double into - just hit.");
			case SIX:
				return dealerValue <= 6
						? new Recommendation(Action.SPLIT, "Dealer " + dealerLabel
								+ " is weak enough that splitting 6s gives you two hands likely to beat a dealer bust.")
						: new Recommendation(Action.HIT,
								"Dealer " + dealerLabel + " is too strong to split 6s against - just hit.");
			case SEVEN:
				return dealerValue <= 7
						? new Recommendation(Action.SPLIT, "Dealer " + dealerLabel
								+ " is weak enough that splitting 7s gives you two competitive hands.")
						: new Recommendation(Action.HIT,
								"Dealer " + dealerLabel + " is too strong to split 7s against - just hit.");
			case EIGHT:
				return new Recommendation(Action.SPLIT,
						"Always split 8s - a hard 16 is one of the worst hands in blackjack, and splitting salvages it even against a strong dealer card.");
			case NINE:
				return (dealerValue <= 6 || dealerValue == 8 || dealerValue == 9)
						? new Recommendation(Action.SPLIT, "Dealer " + dealerLabel
								+ " isn't strong enough to just stand on 18 - splitting into two 9s wins more.")
						: new Recommendation(Action.STAND, "A pair of 9s already totals 18, which is strong enough to stand against dealer "
								+ dealerLabel + " without risking a split.");
			case TEN:
			case JACK:
			case QUEEN:
			case KING:
				return new Recommendation(Action.STAND,
						"A pair of 10-value cards already totals 20 - never break up a hand that strong.");
			default:
				return new Recommendation(Action.HIT, "No stronger option is available - hit.");
		}
	}

	private static Recommendation recommendHard(int total, int dealerValue, String dealerLabel, boolean canSurrender) {
		if (total <= 8) {
			return new Recommendation(Action.HIT, total + " is too low to double or stand on - take another card.");
		}
		if (total == 9) {
			return (dealerValue >= 3 && dealerValue <= 6)
					? new Recommendation(Action.DOUBLE,
							"9 against a weak dealer " + dealerLabel + " is a good spot to double for extra value.")
					: new Recommendation(Action.HIT,
							"9 isn't strong enough to double against dealer " + dealerLabel + " - just hit.");
		}
		if (total == 10) {
			return dealerValue <= 9
					? new Recommendation(Action.DOUBLE,
							"10 is one of the best totals to double, and dealer " + dealerLabel + " isn't strong enough to worry about.")
					: new Recommendation(Action.HIT,
							"Dealer " + dealerLabel + " is too strong to double a 10 against - just hit.");
		}
		if (total == 11) {
			return dealerValue <= 10
					? new Recommendation(Action.DOUBLE, "11 is the best possible total to double - you can't bust and have great odds to make 21.")
					: new Recommendation(Action.HIT,
							"Even at 11, doubling into a dealer Ace is too risky - just hit.");
		}
		if (total == 12) {
			return (dealerValue >= 4 && dealerValue <= 6)
					? new Recommendation(Action.STAND, "Dealer " + dealerLabel
							+ " is likely to bust, so standing on 12 avoids the risk of busting yourself.")
					: new Recommendation(Action.HIT,
							"12 is too weak to stand on against dealer " + dealerLabel + " - take another card.");
		}
		if (total >= 13 && total <= 14) {
			return dealerValue <= 6
					? new Recommendation(Action.STAND, "Dealer " + dealerLabel
							+ " is likely to bust, so standing on " + total + " avoids unnecessary risk.")
					: new Recommendation(Action.HIT, "Dealer " + dealerLabel
							+ " is unlikely to bust, so " + total + " needs to improve - hit.");
		}
		if (total == 15) {
			if (canSurrender && dealerValue == 10) {
				return new Recommendation(Action.SURRENDER,
						"15 against a dealer 10 wins less than half the time even played perfectly - surrendering limits the loss to half your bet.");
			}
			return dealerValue <= 6
					? new Recommendation(Action.STAND, "Dealer " + dealerLabel
							+ " is likely to bust, so standing on 15 avoids unnecessary risk.")
					: new Recommendation(Action.HIT,
							"Dealer " + dealerLabel + " is unlikely to bust, so 15 needs to improve - hit.");
		}
		if (total == 16) {
			if (canSurrender && (dealerValue == 9 || dealerValue == 10 || dealerValue == 11)) {
				return new Recommendation(Action.SURRENDER,
						"16 against dealer " + dealerLabel + " is a losing hand either way - surrendering limits the loss to half your bet.");
			}
			return dealerValue <= 6
					? new Recommendation(Action.STAND, "Dealer " + dealerLabel
							+ " is likely to bust, so standing on 16 avoids unnecessary risk.")
					: new Recommendation(Action.HIT,
							"Dealer " + dealerLabel + " is unlikely to bust, so 16 needs to improve - hit.");
		}
		return new Recommendation(Action.STAND, total + " is strong enough that hitting risks busting for no benefit.");
	}

	private static Recommendation recommendSoft(int total, int dealerValue, String dealerLabel) {
		switch (total) {
			case 12:
				return new Recommendation(Action.HIT, "Soft 12 is weak, and an Ace can never bust you - hit to try to improve.");
			case 13:
			case 14:
				return (dealerValue == 5 || dealerValue == 6)
						? new Recommendation(Action.DOUBLE, "The extra card can't bust a soft hand, and dealer "
								+ dealerLabel + " is weak enough to attack.")
						: new Recommendation(Action.HIT,
								"Dealer " + dealerLabel + " is too strong to double a soft " + total + " against - hit.");
			case 15:
			case 16:
				return (dealerValue >= 4 && dealerValue <= 6)
						? new Recommendation(Action.DOUBLE, "The extra card can't bust a soft hand, and dealer "
								+ dealerLabel + " is weak enough to attack.")
						: new Recommendation(Action.HIT,
								"Dealer " + dealerLabel + " is too strong to double a soft " + total + " against - hit.");
			case 17:
				return (dealerValue >= 3 && dealerValue <= 6)
						? new Recommendation(Action.DOUBLE, "The extra card can't bust a soft hand, and dealer "
								+ dealerLabel + " is weak enough to attack.")
						: new Recommendation(Action.HIT,
								"Dealer " + dealerLabel + " is too strong to double a soft 17 against - hit.");
			case 18:
				if (dealerValue >= 3 && dealerValue <= 6) {
					return new Recommendation(Action.DOUBLE, "The extra card can't bust a soft hand, and dealer "
							+ dealerLabel + " is weak enough to attack.");
				}
				if (dealerValue == 2 || dealerValue == 7 || dealerValue == 8) {
					return new Recommendation(Action.STAND,
							"Soft 18 is already a strong total against dealer " + dealerLabel + " - stand.");
				}
				return new Recommendation(Action.HIT,
						"Dealer " + dealerLabel + " is too strong for a soft 18 to stand against - hit to try to improve.");
			default:
				return new Recommendation(Action.STAND, "Soft " + total + " is already strong enough to stand.");
		}
	}
}

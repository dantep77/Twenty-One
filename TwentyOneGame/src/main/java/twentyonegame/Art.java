package twentyonegame;

public class Art {

	public static String faceDownArt = "┌─────┐\n"
								     + "|?    |\n"
							         + "|  ?  |\n"
							         + "|    ?|\n"
							         + "└─────┘\n";

	/** Card-back art shown for face-down cards */
	public static String cardBackArt =
			"┌─────┐\n" +
			ANSI.BRIGHT_BLUE.getCode() + "│▒▒▒▒▒│" + ANSI.RESET.getCode() + "\n" +
			ANSI.BRIGHT_BLUE.getCode() + "│▒▒▒▒▒│" + ANSI.RESET.getCode() + "\n" +
			ANSI.BRIGHT_BLUE.getCode() + "│▒▒▒▒▒│" + ANSI.RESET.getCode() + "\n" +
			"└─────┘\n";

	public static String bigLogo = ""
			+ ANSI.BRIGHT_RED.getCode() + " ███████████                                      █████              " + ANSI.WHITE.getCode() + "           " + ANSI.CYAN.getCode() + "    ███████                       \r\n"
			+ ANSI.BRIGHT_RED.getCode() + "░█░░░███░░░█                                     ░░███               " + ANSI.WHITE.getCode() + "           " + ANSI.CYAN.getCode() + "  ███░░░░░███                     \r\n"
			+ ANSI.BRIGHT_RED.getCode() + "░   ░███  ░  █████ ███ █████  ██████  ████████   ███████   █████ ████" + ANSI.WHITE.getCode() + "           " + ANSI.CYAN.getCode() + " ███     ░░███ ████████    ██████ \r\n"
			+ ANSI.BRIGHT_RED.getCode() + "    ░███    ░░███ ░███░░███  ███░░███░░███░░███ ░░░███░   ░░███ ░███ " + ANSI.WHITE.getCode() + " ██████████" + ANSI.CYAN.getCode() + "░███      ░███░░███░░███  ███░░███\r\n"
			+ ANSI.BRIGHT_RED.getCode() + "    ░███     ░███ ░███ ░███ ░███████  ░███ ░███   ░███     ░███ ░███ " + ANSI.WHITE.getCode() + "░░░░░░░░░░ " + ANSI.CYAN.getCode() + "░███      ░███ ░███ ░███ ░███████ \r\n"
			+ ANSI.BRIGHT_RED.getCode() + "    ░███     ░░███████████  ░███░░░   ░███ ░███   ░███ ███ ░███ ░███ " + ANSI.WHITE.getCode() + "           " + ANSI.CYAN.getCode() + "░░███     ███  ░███ ░███ ░███░░░  \r\n"
			+ ANSI.BRIGHT_RED.getCode() + "    █████     ░░████░████   ░░██████  ████ █████  ░░█████  ░░███████ " + ANSI.WHITE.getCode() + "           " + ANSI.CYAN.getCode() + " ░░░███████░   ████ █████░░██████ \r\n"
			+ ANSI.BRIGHT_RED.getCode() + "   ░░░░░       ░░░░ ░░░░     ░░░░░░  ░░░░ ░░░░░    ░░░░░    ░░░░░███ " + ANSI.WHITE.getCode() + "           " + ANSI.CYAN.getCode() + "   ░░░░░░░    ░░░░ ░░░░░  ░░░░░░  \r\n"
			+ ANSI.BRIGHT_RED.getCode() + "                                                            ███ ░███ " + ANSI.WHITE.getCode() + "           " + ANSI.CYAN.getCode() + "                                  \r\n"
			+ ANSI.BRIGHT_RED.getCode() + "                                                           ░░██████  " + ANSI.WHITE.getCode() + "           " + ANSI.CYAN.getCode() + "                                  \r\n"
			+ ANSI.BRIGHT_RED.getCode() + "                                                            ░░░░░░   " + ANSI.WHITE.getCode() + "           " + ANSI.RESET.getCode();

	/**
	 * Builds a horizontal felt-table divider of the given width.
	 */
	public static String divider(int width) {
		return ANSI.BRIGHT_GREEN.getCode() + "═".repeat(width) + ANSI.RESET.getCode();
	}
}

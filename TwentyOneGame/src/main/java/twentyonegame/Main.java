package twentyonegame;

public class Main {
	public static void main(String[] args) {
		boolean fastMode = false;
		for (String arg : args) {
			if (arg.equalsIgnoreCase("--fast") || arg.equalsIgnoreCase("-f")) {
				fastMode = true;
			}
		}
		Game.getInstance().playGame(fastMode);
	}
}

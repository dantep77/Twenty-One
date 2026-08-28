package twentyonegame;

public class Main {
	public static void main(String[] args) {
		boolean fastMode = false;
		boolean coachMode = false;
		for (String arg : args) {
			if (arg.equalsIgnoreCase("--fast") || arg.equalsIgnoreCase("-f")) {
				fastMode = true;
			} else if (arg.equalsIgnoreCase("--coach") || arg.equalsIgnoreCase("-c")) {
				coachMode = true;
			}
		}
		Game.getInstance().playGame(fastMode, coachMode);
	}
}

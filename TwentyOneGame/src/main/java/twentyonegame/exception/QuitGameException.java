package twentyonegame.exception;

/**
 * Signals that the player has chosen to quit mid-round. Used as flow
 * control to unwind out of nested prompts back to the top-level game loop.
 */
public class QuitGameException extends RuntimeException {

	private static final long serialVersionUID = 1L;

}

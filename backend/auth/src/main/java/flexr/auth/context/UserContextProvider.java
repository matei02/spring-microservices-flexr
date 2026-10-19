package flexr.auth.context;

/**
 * Provides thread-local storage for UserContext, allowing per-request user info access.
 */
public class UserContextProvider {
    private static final ThreadLocal<UserContext> CONTEXT = new ThreadLocal<>();

    /**
     * Sets the current user context for this thread.
     * @param userId the user ID
     * @param email the user email
     */
    public static void set(String userId, String email) {
        CONTEXT.set(new UserContext(userId, email));
    }

    /**
     * Gets the current user context for this thread.
     * @return the UserContext, or null if not set
     */
    public static UserContext get() {
        return CONTEXT.get();
    }

    /**
     * Clears the user context for this thread.
     */
    public static void clear() {
        CONTEXT.remove();
    }
}


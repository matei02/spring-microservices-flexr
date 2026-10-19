package flexr.auth.context;

/**
 * Holds user context information extracted from gateway headers.
 * @param userId the unique user identifier (from JWT 'sub')
 * @param email the user's email address (from JWT 'email')
 */
public record UserContext(String userId, String email) {}


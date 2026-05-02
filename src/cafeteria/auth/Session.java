package cafeteria.auth;

import cafeteria.model.User;

/**
 * Session – Singleton that holds the currently logged-in user's data.
 * All UI panels access the current user through this class.
 *
 * Course: SW121 – Object Oriented Programming | Batch: K25SW
 */
public class Session {

    private static Session  instance    = null;
    private        User     currentUser = null;

    private Session() {}

    /** Returns the single Session instance. */
    public static Session getInstance() {
        if (instance == null) instance = new Session();
        return instance;
    }

    public void setCurrentUser(User user) { this.currentUser = user; }
    public User getCurrentUser()          { return currentUser; }

    public boolean isLoggedIn()  { return currentUser != null; }
    public boolean isAdmin()     { return isLoggedIn() && currentUser.isAdmin(); }

    /** Clears the session (logout). */
    public void logout() { currentUser = null; }
}

package cafeteria.model;

/**
 * User – Model class representing an application user.
 * Supports Admin and regular User roles.
 *
 * Course: SW121 – Object Oriented Programming | Batch: K25SW
 */
public class User {

    private int     userId;
    private String  username;
    private String  password;
    private String  fullName;
    private String  role;        // "ADMIN" | "USER"
    private String  email;
    private boolean isActive;

    // ── Constructors ─────────────────────────────────────────────────────────

    public User() {}

    public User(int userId, String username, String password,
                String fullName, String role, String email, boolean isActive) {
        this.userId   = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role     = role;
        this.email    = email;
        this.isActive = isActive;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public int     getUserId()             { return userId;   }
    public void    setUserId(int userId)   { this.userId = userId; }

    public String  getUsername()           { return username; }
    public void    setUsername(String u)   { this.username = u; }

    public String  getPassword()           { return password; }
    public void    setPassword(String p)   { this.password = p; }

    public String  getFullName()           { return fullName; }
    public void    setFullName(String n)   { this.fullName = n; }

    public String  getRole()              { return role; }
    public void    setRole(String r)      { this.role = r; }

    public String  getEmail()             { return email; }
    public void    setEmail(String e)     { this.email = e; }

    public boolean isActive()             { return isActive; }
    public void    setActive(boolean a)   { this.isActive = a; }

    /** Convenience check for admin role. */
    public boolean isAdmin() { return "ADMIN".equalsIgnoreCase(role); }

    @Override
    public String toString() {
        return fullName + " (" + role + ")";
    }
}

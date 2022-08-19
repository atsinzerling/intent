public class User {
    private String username;
    private String password;
    private String privileges;
    public boolean wasCreated;

    public User(String username, String password, boolean wasCreated) {
        this.username = username;
        this.password = password;
        this.wasCreated = wasCreated;
    }

    public String toString() {
        return "User: " + username + " - " + password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrivileges() {
        return privileges;
    }

    public void setPrivileges(String privileges) {
        this.privileges = privileges;
    }

    public void setWasCreated(boolean wasCreated) {
        this.wasCreated = wasCreated;
    }
}

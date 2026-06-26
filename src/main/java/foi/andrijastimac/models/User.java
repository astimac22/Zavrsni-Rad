package foi.andrijastimac.models;

public class User {

    private final int id;
    private final String email;
    private final String password;
    private final String name;

    public User(int id, String email, String password, String name) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getName() { return name; }
}

package spring.model;

public class User {
    private int id;
    private final String password;

    public User(int id, String pass) {
        this.id = id;
        this.password = pass;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

}

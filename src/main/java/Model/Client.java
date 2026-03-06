package Model;

/**
 * Represents a client with personal and contact details.
 */
public class Client {
    private int id;
    private String name;
    private String address;
    private String email;
    private String phone;

    /**
     * Creates an empty client object.
     */
    public Client() {}

    /**
     * Creates a Client with specified values.
     *
     * @param id      the client ID
     * @param name    the client's name
     * @param address the client's address
     * @param email   the client's email
     * @param phone   the client's phone number
     */
    public Client(int id, String name, String address, String email, String phone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
    }

    /** @return the client ID */
    public int getId() { return id; }

    /** @param id the client ID to set */
    public void setId(int id) { this.id = id; }

    /** @return the client's name */
    public String getName() { return name; }

    /** @param name the client's name to set */
    public void setName(String name) { this.name = name; }

    /** @return the client's address */
    public String getAddress() { return address; }

    /** @param address the client's address to set */
    public void setAddress(String address) { this.address = address; }

    /** @return the client's email */
    public String getEmail() { return email; }

    /** @param email the client's email to set */
    public void setEmail(String email) { this.email = email; }

    /** @return the client's phone number */
    public String getPhone() { return phone; }

    /** @param phone the client's phone number to set */
    public void setPhone(String phone) { this.phone = phone; }

    /**
     * Returns a string representation of the client.
     *
     * @return string describing the client
     */
    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}

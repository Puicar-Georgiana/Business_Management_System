package BusinessLogic;

import Data_Access.ClientDAO;
import Model.Client;

import java.util.List;

/**
 * Business Logic Layer for handling operations related to Client entities.
 */
public class ClientBLL {
    private ClientDAO clientDAO;

    /**
     * Constructs a new ClientBLL instance and initializes the ClientDAO.
     */
    public ClientBLL() {
        this.clientDAO = new ClientDAO();
    }

    /**
     * Retrieves all clients.
     *
     * @return a list of all Client objects
     */
    public List<Client> getClients() {
        return clientDAO.findAll();
    }

    /**
     * Adds a new client.
     *
     * @param client the Client object to be added
     */
    public void addClient(Client client) {
        try {
            clientDAO.insert(client);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates an existing client.
     *
     * @param client the Client object with updated data
     */
    public void updateClient(Client client) {
        clientDAO.update(client);
    }

    /**
     * Deletes a client by their ID.
     *
     * @param clientId the ID of the client to be deleted
     */
    public void deleteClient(int clientId) {
        Client client = new Client();
        client.setId(clientId);
        clientDAO.delete(client);
    }

    /**
     * Finds a client by their ID.
     *
     * @param id the client ID
     * @return the found Client object, or null if not found
     */
    public Client findClientById(int id) {
        return clientDAO.findById(id);
    }
}

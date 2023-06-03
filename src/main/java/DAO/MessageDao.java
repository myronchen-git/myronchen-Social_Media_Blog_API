package DAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import Model.Message;

public interface MessageDao {

    /**
     * Adds a new message to a database.
     * 
     * @param message The new message to add, which contains the poster's ID, message text, and time of posting.
     * @return The same Message object passed into this method, but with an ID generated by the database.
     * @throws SQLException If there is an issue with the database.
     */
    Message addMessage(Message message) throws SQLException;

    /**
     * Gets all messages from a database.  If there are no messages, then the list is empty.
     * 
     * @return List containing Messages that exist in the database.
     * @throws SQLException If there is an issue with the database.
     */
    List<Message> getAllMessages() throws SQLException;

    /**
     * Gets a message from a database by using message ID.  If the message does not exist, return an empty Optional.
     * 
     * @param id The message ID of the message to retrieve.
     * @return A Message object with ID, poster ID, message text, and time of posting.
     * @throws SQLException If there is an issue with the database.
     */
    Optional<Message> getMessage(int id) throws SQLException;

    /**
     * Deletes a message from a database by using message ID.  If the message doesn't exist, nothing happens.
     * 
     * @param id The message ID of the message to delete.
     * @throws SQLException If there is an issue with the database.
     */
    void deleteMessage(int id) throws SQLException;

    /**
     * Updates a message's text in a database, belonging to the provided message ID.  If the message doesn't exist,
     *  nothing happens.
     * 
     * @param id The message ID of the message to update.
     * @param text The text that will replace the original message text.
     * @throws SQLException If there is an issue with the database.
     */
    void updateMessage(int id, String text) throws SQLException;
}

package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Model.Message;

public class MessageDaoH2 implements MessageDao {

    private final Connection connection;
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDaoH2.class);

    public MessageDaoH2(Connection connection) {
        this.connection = connection;
    }


    @Override
    public Message addMessage(Message message) throws SQLException {
        LOGGER.info("Adding new message to database: {}", message);

        String sql = "INSERT INTO message(posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?);";

        try {
            PreparedStatement preparedStatement =
             connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, message.getPosted_by());
            preparedStatement.setString(2, message.getMessage_text());
            preparedStatement.setLong(3, message.getTime_posted_epoch());

            int numMessagesCreated = preparedStatement.executeUpdate();
            LOGGER.debug("Number of messages added to database: {}", numMessagesCreated);

            ResultSet pkeyResultSet = preparedStatement.getGeneratedKeys();

            if (pkeyResultSet.next()){
                message.setMessage_id(pkeyResultSet.getInt(1));
                return message;

            } else {
                LOGGER.error("No ID returned after adding new message to database: {}", message);
                throw new SQLException(
                    String.format(
                        "New message added to database, but no generated keys (IDs) were returned.  " +
                         "New message: %s",
                         message));
            }

        } catch (SQLException e) {
            LOGGER.error("Database error when adding message: {}", message);
            throw e;
        }
    }


    @Override
    public List<Message> getAllMessages() throws SQLException {
        LOGGER.info("Getting all messages from database");

        List<Message> messages = new ArrayList<>();

        String sql = "SELECT * FROM message;";

        try {
            ResultSet resultSet = connection.createStatement().executeQuery(sql);

            while (resultSet.next()) {
                messages.add(
                    new Message(
                        resultSet.getInt("message_id"),
                        resultSet.getInt("posted_by"),
                        resultSet.getString("message_text"),
                        resultSet.getLong("time_posted_epoch"))
                );
            }

        } catch (SQLException e) {
            LOGGER.error("Database error when getting all messages.");
            throw e;
        }

        return messages;
    }


    @Override
    public List<Message> getAllMessages(int accountId) throws SQLException {
        LOGGER.info("Getting all messages from user with account ID: {}", accountId);

        List<Message> messages = new ArrayList<>();

        String sql = "SELECT * FROM message WHERE posted_by = ?;";

        try {
            PreparedStatement preparedStatement =
             connection.prepareStatement(sql);
            preparedStatement.setInt(1, accountId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                messages.add(
                    new Message(
                        resultSet.getInt("message_id"),
                        resultSet.getInt("posted_by"),
                        resultSet.getString("message_text"),
                        resultSet.getLong("time_posted_epoch"))
                );
            }

        } catch (SQLException e) {
            LOGGER.error("Database error when getting all messages from user with account ID: {}", accountId);
            throw e;
        }

        return messages;
    }


    @Override
    public Optional<Message> getMessage(int messageId) throws SQLException {
        LOGGER.info("Getting message from database with ID: {}", messageId);

        String sql = "SELECT * FROM message WHERE message_id = ?;";

        try {
            PreparedStatement preparedStatement =
             connection.prepareStatement(sql);
            preparedStatement.setInt(1, messageId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(
                    new Message(
                        resultSet.getInt("message_id"), 
                        resultSet.getInt("posted_by"), 
                        resultSet.getString("message_text"),
                        resultSet.getLong("time_posted_epoch")));
            }

        } catch (SQLException e) {
            LOGGER.error("Database error when getting message for ID: {}", messageId);
            throw e;
        }

        return Optional.empty();
    }


    @Override
    public void deleteMessage(int messageId) throws SQLException {
        LOGGER.info("Deleting message from database with ID: {}", messageId);

        String sql = "DELETE FROM message where message_id = ?;";

        try {
            PreparedStatement preparedStatement =
             connection.prepareStatement(sql);
            preparedStatement.setInt(1, messageId);

            int numMessagesDeleted = preparedStatement.executeUpdate();
            LOGGER.debug("Number of messages deleted from database: {}", numMessagesDeleted);

        } catch (SQLException e) {
            LOGGER.error("Database error when deleting message for ID: {}", messageId);
            throw e;
        }
    }

    
    @Override
    public void updateMessage (int messageId, String messageText) throws SQLException {
        LOGGER.info("Updating message with ID: {} from database, with text: {}", messageId, messageText);

        String sql = "UPDATE message SET message_text = '?' WHERE message_id = ?;";

        try {
            PreparedStatement preparedStatement =
             connection.prepareStatement(sql);
            preparedStatement.setString(1, messageText);
            preparedStatement.setInt(2, messageId);

            int numMessagesUpdated = preparedStatement.executeUpdate();
            LOGGER.debug("Number of messages updated in database: {}", numMessagesUpdated);

        } catch (SQLException e) {
            LOGGER.error("Database error when updating message with ID: {}", messageId);
            throw e;
        }
    }

}

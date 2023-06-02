package Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import DAO.AccountDao;
import DAO.MessageDao;
import Exception.AccountAlreadyExistsException;
import Exception.AccountDoesNotExistException;
import Exception.InvalidMessageTextException;
import Exception.InvalidNewAccountInputException;
import Exception.MessageDoesNotExistException;
import Model.Account;
import Model.Message;

public class SocialMediaService {
    
    private AccountDao accountDao;
    private MessageDao messageDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(SocialMediaService.class);

    public SocialMediaService(AccountDao accountDao, MessageDao messageDao) {
        this.accountDao = accountDao;
        this.messageDao = messageDao;
    }

    /**
     * Registers a new account by adding it to the database.  Rejects the addition by throwing an exception if the
     *  username is empty or if the password length is too short.
     * An Account is returned with a generated ID, and the same username and password.
     * 
     * @param account The account, containing only the username and password, to add to the Account database.
     * @return An Account object with the ID, username, and password of the newly registered account.
     * @throws InvalidNewAccountInputException If the new account's username or password is not acceptable.
     * @throws AccountAlreadyExistsException If an account with the provided username already exists.
     * @throws SQLException If there is an issue with the database.
     */
    public Account addAccount(Account account)
     throws InvalidNewAccountInputException, AccountAlreadyExistsException, SQLException {
        LOGGER.info("Social media service is adding an account: {}", account);

        int minPasswordLength = 4;

        if (account.getUsername().isEmpty()
         || account.getPassword().length() < minPasswordLength) {
            LOGGER.error("New account username or password is not acceptable: {}", account);
            throw new InvalidNewAccountInputException(
                String.format(
                    "Can not create a new account.  Username or password is not acceptable for account: %s",
                     account));
        }

        if (accountDao.getAccount(account.getUsername()).isPresent()) {
            LOGGER.error("Account already exists for username: {}", account.getUsername());
            throw new AccountAlreadyExistsException(
                String.format(
                    "Can not create a new account.  Account with username '%s' already exists.",
                     account.getUsername()));
        }

        return accountDao.addAccount(account);
    }

    /**
     * Logs in a user by looking up an account stored in the database and comparing if the username and password
     *  matches.  If there's a match, then return an Account object that includes the ID as well.
     * If an account does not exist under the provided username or if the provided password does not match the one
     *  stored in the database, an Exception is thrown.
     * 
     * @param account Contains the inputted username and password of a supposedly existing account.
     * @return The found Account containing the ID, along with username and password.
     * @throws IllegalArgumentException If an account with the provided username does not exist or if the provided
     *  password does not match the stored password.
     * @throws SQLException If there is an issue with the database.
     */
    public Account loginAccount(Account account)
     throws IllegalArgumentException, SQLException {
        LOGGER.info("Social media service is logging into an account: {}", account);

        Optional<Account> retrievedAccount = accountDao.getAccount(account.getUsername());
        LOGGER.debug("Retrieved account: {}", retrievedAccount);

        return retrievedAccount
            .filter(
                (acc) -> acc.getPassword().equals(account.getPassword())
            ).orElseThrow(
                () -> {
                    LOGGER.error("Username and/or password is incorrect for log in: {}", account);
                    throw new IllegalArgumentException(
                        String.format(
                            "Can not log into account.  Username or password is incorrect.  %s",
                             account));
                }
            );
    }

    /**
     * Creates/posts a new message by adding it to the database.  Throws an exception if the message text is empty or
     *  not less than 255 characters, or if the poster does not have an account in the database.
     * A Message is returned with a generated ID from the database, along with the same provided fields.
     * 
     * @param message Contains poster ID, message text, and time of posting.
     * @return A Message object with the message ID, poster ID, message text, and time of posting of the newly added
     *  message.
     * @throws InvalidMessageTextException If the new message's text is not acceptable.
     * @throws AccountDoesNotExistException If an account with the provided poster ID does not exist.
     * @throws SQLException If there is an issue with the database.
     */
    public Message createMessage(Message message)
     throws InvalidMessageTextException, AccountDoesNotExistException, SQLException {
        LOGGER.info("Social media service is creating a new message: {}", message);

        if (message.getMessage_text().isEmpty()
         || message.getMessage_text().length() >= 255) {
            LOGGER.error("Message text is empty or too long: {}", message);
            throw new InvalidMessageTextException(
                String.format(
                    "Can not create a new message.  Message text is empty or too long: %s",
                     message));
        }

        if(!accountDao.getAccount(message.getPosted_by()).isPresent()) {
            LOGGER.error("Account does not exist for account ID: {}", message.getPosted_by());
            throw new AccountDoesNotExistException(
                String.format(
                    "Can not create a new message.  Account with ID '%s' does not exist.",
                     message.getPosted_by()));
        }

        return messageDao.addMessage(message);
    }

    /**
     * Gets all messages that exist in the database.
     * Returns a list of messages.  If there are no messages, then the list is empty.
     * 
     * @return List containing Messages that exist in the database.
     * @throws SQLException If there is an issue with the database.
     */
    public List<Message> getAllMessages() throws SQLException {
        LOGGER.info("Social media service is getting all messages.");

        return messageDao.getAllMessages();
    }

    /**
     * Gets all messages in the database that belong to a particular user.
     * Returns a list of messages.
     * If there are no messages or if the account doesn't exist, then the returned list will be empty.
     * 
     * @param accountId The account ID of the user of the messages to retrieve.
     * @return List containing Messages from a particular user.
     * @throws SQLException If there is an issue with the database.
     */
    public List<Message> getAllMessages(int accountId) throws SQLException {
        LOGGER.info("Social media service is getting all messages from user with account ID: {}", accountId);

        return messageDao.getAllMessages(accountId);
    }

    /**
     * Gets a message from the database by using message ID.  If the message does not exist, return an empty Optional.
     * 
     * @param id The message ID of the message to retrieve.
     * @return An Optional containing the Message object with ID, poster ID, message text, and time of posting.
     * @throws SQLException If there is an issue with the database.
     */
    public Optional<Message> getMessage(int id) throws SQLException {
        LOGGER.info("Social media service is getting message with ID: {}", id);

        return messageDao.getMessage(id);
    }

    /**
     * Gets the message corresponding to the provided ID from the database, deletes the message, and returns an Optional
     *  containing the message.
     * If the message does not exist, the delete operation in the database is not performed, and an empty Optional is
     *  returned.
     * 
     * @param id The message ID of the message to delete.
     * @return An Optional containing the Message object/record that was deleted.
     * @throws SQLException If there is an issue with the database.
     */
    public Optional<Message> deleteMessage(int id) throws SQLException {
        LOGGER.info("Social media service is deleting message with ID: {}", id);

        Optional<Message> retrievedMessage = messageDao.getMessage(id);

        if (retrievedMessage.isPresent()) {
            messageDao.deleteMessage(id);
        }

        return retrievedMessage;
    }

    /**
     * Updates the message text belonging to the provided ID in the database.  The old message is retrieved from the
     *  database before doing the update and is used as the return object.
     * Returns a Message object representing the updated message in the database.
     * Throws Exceptions if the message text is empty, if the message text is too long, or if message with provided ID
     *  doesn't exist in the database.
     * 
     * @param id The message ID of the message to update.
     * @param text The text that will replace the original message text.
     * @return The Message object representing the updated message in the database.
     * @throws InvalidMessageTextException If the new message's text is not acceptable.
     * @throws MessageDoesNotExistException If the message with the provided ID does not exist.
     * @throws SQLException If there is an issue with the database.
     */
    public Message updateMessage(int id, String text)
     throws InvalidMessageTextException, MessageDoesNotExistException, SQLException {
        LOGGER.info("Social media service is updating message text with ID: {}, with new text: {}", id, text);

        if (text.isEmpty()
         || text.length() >= 255) {
            LOGGER.error("Message text is empty or too long: {}", text);
            throw new InvalidMessageTextException(
                String.format(
                    "Can not update message with ID: %s.  Message text is empty or too long: %s.",
                     id, text));
        }

        Message retrievedMessage = messageDao.getMessage(id)
            .orElseThrow(
                () -> {
                    LOGGER.error("Message does not exist for ID: {}", id);
                    return new MessageDoesNotExistException(
                        String.format(
                            "Can not update message with ID: %s.  Message does not exist.",
                             id));
                }
            );

        retrievedMessage.setMessage_text(text);

        return retrievedMessage;
    }

}

package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.SQLException;
import java.util.List;

import DAO.AccountDaoH2;
import DAO.MessageDaoH2;
import Exception.AccountAlreadyExistsException;
import Exception.AccountDoesNotExistException;
import Exception.InvalidMessageTextException;
import Exception.InvalidNewAccountInputException;
import Exception.MessageDoesNotExistException;
import Model.Account;
import Model.Message;
import Service.SocialMediaService;
import Util.ConnectionUtil;

public class SocialMediaController {

    private static final SocialMediaService SOCIAL_MEDIA_SERVICE = 
    new SocialMediaService(
        new AccountDaoH2(ConnectionUtil.getConnection()), 
        new MessageDaoH2(ConnectionUtil.getConnection()));

    /**
     * Starts the API using Javalin.
     * 
     * @return a Javalin app object which defines the behavior of the Javalin 
     * controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();

        app.post("/register", this::addAccountHandler);
        app.post("/login", this::loginAccountHandler);
        app.post("/messages", this::createMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageByIdHandler);
        app.patch("/messages/{message_id}", this::patchMessageByIdHandler);
        app.get("/accounts/{account_id}/messages", this::getAllMessagesFromUserHandler);

        return app;
    }

    /**
     * Takes an account without an ID and adds it to the Account database table.
     * An account with an ID is returned thru the API.
     * If the account already exists or if the username or password is not acceptable, then a HTTP response code of
     *  400 is returned to the client.
     * If there is an issue interacting with the database, a HTTP response code of 500 is returned to the client.
     * 
     * @param context Contains an account without an ID, in JSON, from the client.  Sends back the newly registered
     *  account with an ID, username, and password, in JSON.
     */
    private void addAccountHandler(Context context) {
        Account unregisteredAccount = context.bodyAsClass(Account.class);

        try {
            Account registeredAccount = SOCIAL_MEDIA_SERVICE.addAccount(unregisteredAccount);
            context.status(200);
            context.json(registeredAccount);
        } catch (InvalidNewAccountInputException | AccountAlreadyExistsException e) {
            context.status(400);
        } catch (SQLException e) {
            context.status(500);
        }
    }

    /**
     * Attempts to log in a user by looking up an account record in the database.
     * The correct account with ID is returned thru the API.
     * If the password is incorrect or if the account does not exist, a HTTP response code of 401 is returned.
     * If there is an issue interacting with the database, a HTTP response code of 500 is returned to the client.
     * 
     * @param context Contains an account in JSON that has username and password only.  Sends back the correct matching
     *  account with ID, username, and password, in JSON.
     */
    private void loginAccountHandler(Context context) {
        Account account = context.bodyAsClass(Account.class);

        try {
            Account registeredAccount = SOCIAL_MEDIA_SERVICE.loginAccount(account);
            context.status(200);
            context.json(registeredAccount);
        } catch (IllegalArgumentException e) {
            context.status(401);
        } catch (SQLException e) {
            context.status(500);
        }
    }

    /**
     * Adds a message that contains poster ID, message text, and time of posting to the message database table.
     * A message with a message ID is returned thru the API.
     * If the message text is empty or is too long, or if the poster does not have an account, then a HTTP response code
     *  of 400 is returned.
     * 
     * @param context Contains a Message object in JSON, only without a message ID.  Sends back a Message object with
     *  the same fields but with ID included, in JSON.
     */
    private void createMessageHandler(Context context) {
        Message message = context.bodyAsClass(Message.class);

        try {
            Message submittedMessage = SOCIAL_MEDIA_SERVICE.createMessage(message);
            context.status(200);
            context.json(submittedMessage);
        } catch (InvalidMessageTextException | AccountDoesNotExistException e) {
            context.status(400);
        } catch (SQLException e) {
            context.status(500);
        }
    }

    /**
     * Gets all messages stored in the database.
     * Returns messages thru the context.
     * If there are no messages, then the list of retrieved messages from the database will be empty.
     * 
     * @param context Does not contain anything from the client, but will contain messages, if any, from the database.
     */
    private void getAllMessagesHandler(Context context) {
        try {
            List<Message> retrievedMessages = SOCIAL_MEDIA_SERVICE.getAllMessages();
            context.status(200);
            context.json(retrievedMessages);
        } catch (SQLException e) {
            context.status(500);
        }
    }

    /**
     * Gets all messages in the database that belong to a particular user.  The account ID is provided in the URL and
     *  stored in the context.
     * Returns a list of messages thru the context.
     * If there are no messages or if the account doesn't exist, then the returned list will be empty.
     * 
     * @param context Contains the account ID of the user of the messages to retrieve.
     */
    private void getAllMessagesFromUserHandler(Context context) {
        int accountId = Integer.parseInt(
            context.pathParam("account_id"));

        try {
            List<Message> retrievedMessages = SOCIAL_MEDIA_SERVICE.getAllMessages(accountId);
            context.status(200);
            context.json(retrievedMessages);
        } catch (SQLException e) {
            context.status(500);
        }
    }

    /**
     * Gets a message from the database by using the message ID provided in the URL, which is stored inside the context.
     * Returns a Message object in JSON, with ID, poster ID, message text, and time of posting, thru the context.
     * If the message does not exist, an empty response body is returned to the client.
     * 
     * @param context Contains an ID from the URL path parameter.  Sends back a Message object in JSON.
     */
    private void getMessageByIdHandler(Context context) {
        int id = Integer.parseInt(
            context.pathParam("message_id"));

        try {
            SOCIAL_MEDIA_SERVICE.getMessage(id)
                .ifPresent(
                    (retrievedMessage) -> context.json(retrievedMessage));
            context.status(200);
        } catch (SQLException e) {
            context.status(500);
        }
    }

    /**
     * Deletes a message from the database by using the message ID provided in the URL, which is stored inside the
     *  context.
     * Returns the deleted Message in JSON, with ID, poster ID, message text, and time of posting, thru the context.
     * If the message does not exist, an empty response body is returned to the client.
     * 
     * @param context Contains an ID from the URL path parameter.  Sends back the deleted Message in JSON.
     */
    private void deleteMessageByIdHandler(Context context) {
        int id = Integer.parseInt(
            context.pathParam("message_id"));

        try {
            SOCIAL_MEDIA_SERVICE.deleteMessage(id)
                .ifPresent(
                    (deletedMessage) -> context.json(deletedMessage));
            context.status(200);
        } catch (SQLException e) {
            context.status(500);
        }
    }

    /**
     * Updates a message's text in the database by using the message ID provided in the URL and the request body text in
     *  the context.
     * Returns thru the context, the updated Message in JSON, with ID, poster ID, message text, and time of posting.
     * If the message text is empty or is too long, or if the message does not exist in the database, then a HTTP
     *  response code of 400 is returned.
     * 
     * @param context Contains an ID from the URL path parameter and text from the request body.  Sends back the updated
     *  Message in JSON.
     */
    private void patchMessageByIdHandler(Context context) {
        int id = Integer.parseInt(
            context.pathParam("message_id"));
        String text = context.bodyAsClass(Message.class).getMessage_text();
        
        try {
            Message updatedMessage = SOCIAL_MEDIA_SERVICE.updateMessage(id, text);
            context.status(200);
            context.json(updatedMessage);
        } catch (InvalidMessageTextException | MessageDoesNotExistException e) {
            context.status(400);
        } catch (SQLException e) {
            context.status(500);
        }
    }

}

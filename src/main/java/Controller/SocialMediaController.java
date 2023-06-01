package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.SQLException;

import DAO.AccountDaoH2;
import Exception.AccountAlreadyExistsException;
import Exception.InvalidNewAccountInputException;
import Model.Account;
import Service.SocialMediaService;
import Util.ConnectionUtil;

/**
 * TODO: You will need to write your own endpoints and handlers for your 
 * controller. The endpoints you will need can be found in readme.md as well as 
 * the test cases. You should refer to prior mini-project labs and lecture 
 * materials for guidance on how a controller may be built.
 */
public class SocialMediaController {

    private static final SocialMediaService SOCIAL_MEDIA_SERVICE = 
    new SocialMediaService(
        new AccountDaoH2(ConnectionUtil.getConnection()));

    /**
     * In order for the test cases to work, you will need to write the endpoints 
     * in the startAPI() method, as the test suite must receive a Javalin object 
     * from this method.
     * 
     * @return a Javalin app object which defines the behavior of the Javalin 
     * controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();

        app.get("example-endpoint", this::exampleHandler);
        app.post("/register", this::addAccountHandler);
        
        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both 
     * the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }

    /**
     * Takes an account without an ID and adds it to the Account database table.
     * An account with an ID is returned thru the API.
     * If the account already exists or if the username or password is not acceptable, then a HTTP response code of
     *  400 is returned to the client.
     * If there is an issue interacting with the database, a HTTP response code of 500 is returned to the client.
     * 
     * @param context The context that contains an account without an ID, in JSON.
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

}

package Service;

import java.sql.SQLException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import DAO.AccountDao;
import Exception.AccountAlreadyExistsException;
import Exception.InvalidNewAccountInputException;
import Model.Account;

public class SocialMediaService {
    
    private AccountDao accountDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(SocialMediaService.class);

    public SocialMediaService(AccountDao accountDao) {
        this.accountDao = accountDao;
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

        if (retrievedAccount.isPresent() && retrievedAccount.get().getPassword().equals(account.getPassword())) {
            return retrievedAccount.get();
        }

        LOGGER.error("Username and/or password is incorrect for log in: {}", account);
        throw new IllegalArgumentException(
            String.format(
                "Can not log into account.  Username or password is incorrect.  %s",
                 account));
    }

}

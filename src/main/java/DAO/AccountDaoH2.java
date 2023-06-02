package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Model.Account;

public class AccountDaoH2 implements AccountDao {

    private final Connection connection;
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountDaoH2.class);

    public AccountDaoH2(Connection connection) {
        this.connection = connection;
    }
    
    /**
     * Adds a new account to the H2 database.
     * 
     * @param account The new account to add, which contains a valid username and password.
     * @return The same Account object passed into this method, but with an ID generated by the database.
     * @throws SQLException If there is an issue with the database.
     */
    @Override
    public Account addAccount(Account account) throws SQLException {
        LOGGER.info("Adding new account to database: {}", account);
        
        String sql = "INSERT INTO account(username, password) VALUES (?, ?);";

        try {
            PreparedStatement preparedStatement =
             connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());

            int numAccountsAdded = preparedStatement.executeUpdate();
            LOGGER.debug("Number of accounts added to database: {}", numAccountsAdded);

            ResultSet pkeyResultSet = preparedStatement.getGeneratedKeys();

            if (pkeyResultSet.next()){
                account.setAccount_id(pkeyResultSet.getInt(1));
                return account;

            } else {
                LOGGER.error("No ID returned after adding new account to database: {}", account);
                throw new SQLException(
                    String.format(
                        "New account added to database, but no generated keys (IDs) were returned.  " +
                         "New account: %s",
                         account));
            }

        } catch (SQLException e) {
            LOGGER.error("Database error when adding account: {}", account);
            throw e;
        }
    }

    /**
     * Retrieves an account from the H2 database by username.
     * 
     * @param username The username to use to look up an account in the database.
     * @return An Optional containing an Account object or an empty Optional if there is no account with the provided
     *  username in the database.
     * @throws SQLException If there is an issue with the database.
     */
    @Override
    public Optional<Account> getAccount(String username) throws SQLException {
        LOGGER.info("Retrieving an account from database with username: {}", username);

        String sql = "SELECT * FROM account WHERE username = ?";

        try {
            PreparedStatement preparedStatement =
             connection.prepareStatement(sql);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int retrievedAccountId = resultSet.getInt("account_id");
                String retrievedUsername = resultSet.getString("username");
                String retrievedPassword = resultSet.getString("password");

                return Optional.of(
                    new Account(retrievedAccountId, retrievedUsername, retrievedPassword));
            }

        } catch (SQLException e) {
            LOGGER.error("Database error when getting account for username: {}", username);
            throw e;
        }

        return Optional.empty();
    }

    /**
     * Retrieves an account from the H2 database by using ID.
     * 
     * @param id The ID to use to look up an account in the database.
     * @return An Optional containing an Account object or an empty Optional if there is no account with the provided
     *  ID in the database.
     * @throws SQLException If there is an issue with the database.
     */
    @Override
    public Optional<Account> getAccount(int id) throws SQLException {
        LOGGER.info("Retrieving an account from database with account ID: {}", id);

        String sql = "SELECT * FROM account WHERE account_id = ?";

        try {
            PreparedStatement preparedStatement =
             connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int retrievedAccountId = resultSet.getInt("account_id");
                String retrievedUsername = resultSet.getString("username");
                String retrievedPassword = resultSet.getString("password");

                return Optional.of(
                    new Account(retrievedAccountId, retrievedUsername, retrievedPassword));
            }

        } catch (SQLException e) {
            LOGGER.error("Database error when getting account for ID: {}", id);
            throw e;
        }

        return Optional.empty();
    }

}

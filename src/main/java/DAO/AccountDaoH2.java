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


    @Override
    public Optional<Account> getAccount(String username) throws SQLException {
        LOGGER.info("Retrieving an account from database with username: {}", username);

        String sql = "SELECT * FROM account WHERE username = ?;";

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


    @Override
    public Optional<Account> getAccount(int accountId) throws SQLException {
        LOGGER.info("Retrieving an account from database with account ID: {}", accountId);

        String sql = "SELECT * FROM account WHERE account_id = ?;";

        try {
            PreparedStatement preparedStatement =
             connection.prepareStatement(sql);
            preparedStatement.setInt(1, accountId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int retrievedAccountId = resultSet.getInt("account_id");
                String retrievedUsername = resultSet.getString("username");
                String retrievedPassword = resultSet.getString("password");

                return Optional.of(
                    new Account(retrievedAccountId, retrievedUsername, retrievedPassword));
            }

        } catch (SQLException e) {
            LOGGER.error("Database error when getting account for ID: {}", accountId);
            throw e;
        }

        return Optional.empty();
    }

}

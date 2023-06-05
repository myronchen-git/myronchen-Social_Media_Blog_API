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
        return getAccountHelper("username", username, "username");
    }


    @Override
    public Optional<Account> getAccount(int accountId) throws SQLException {
        return getAccountHelper("account ID", accountId, "account_id");
    }


    /**
     * Helper method used to store common code from the getAccount methods.
     * 
     * @param type Descriptive word(s) used in logging, comments, descriptions, etc.
     * @param value The username or account ID that the getAccount method is supposed to look up.
     * @param databaseTableColumnName The name of the column in the relevant table that is supposed to be used to filter
     *  for the value.
     * @return An Optional containing an Account object or an empty Optional if there is no account containing the
     *  provided value.
     * @throws SQLException If there is an issue with the database.
     */
    private Optional<Account> getAccountHelper(
        String type, Object value, String databaseTableColumnName)
         throws SQLException {
            LOGGER.info("Retrieving an account from database with " + type + ": {}", value);

            if (!"username".equals(databaseTableColumnName) && !"account_id".equals(databaseTableColumnName)) {
                LOGGER.error("Incorrect column name argument for getAccountHelper: {}", databaseTableColumnName);
                throw new IllegalArgumentException(
                    String.format(
                        "Argument 'databaseTableColumnName' is not a recognized name.  " + 
                         "'databaseTableColumnName': %s.", databaseTableColumnName));
            }

            String sql = "SELECT * FROM account WHERE " + databaseTableColumnName + " = ?;";

            try {
                PreparedStatement preparedStatement =
                 connection.prepareStatement(sql);
                
                if (value instanceof String) {
                    preparedStatement.setString(1, (String) value);
                } else if (value instanceof Integer) {
                    preparedStatement.setInt(1, (Integer) value);
                } else {
                    LOGGER.error("Incorrect value type for getAccountHelper: {}", value.getClass().getName());
                    throw new IllegalArgumentException(
                        String.format(
                            "The argument named 'value' is not a recognizable type. " + 
                             "'value' type: %s.", value.getClass().getName()));
                }

                ResultSet resultSet = preparedStatement.executeQuery();
    
                if (resultSet.next()) {
                    return Optional.of(
                        new Account(
                            resultSet.getInt("account_id"),
                            resultSet.getString("username"),
                            resultSet.getString("password")));
                }

            } catch (SQLException e) {
                LOGGER.error("Database error when getting account for " + type + ": {}", value);
                throw e;
            }

            return Optional.empty();
    }

}

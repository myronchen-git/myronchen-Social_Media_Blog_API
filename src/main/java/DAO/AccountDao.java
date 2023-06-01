package DAO;

import java.sql.SQLException;
import java.util.Optional;

import Model.Account;

public interface AccountDao {
    Account addAccount(Account account) throws SQLException;
    Optional<Account> getAccount(String username) throws SQLException;
}

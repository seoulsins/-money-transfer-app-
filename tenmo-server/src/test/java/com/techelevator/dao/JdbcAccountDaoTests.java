package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class JdbcAccountDaoTests extends BaseDaoTests {

    protected static final Account ACCOUNT_1 = new Account(1001, "user1", 2001, new BigDecimal("1000.00"));
    protected static final Account ACCOUNT_2 = new Account(1002, "user1", 2002, new BigDecimal("1000.00"));
    protected static final Account ACCOUNT_3 = new Account(1002, "user1", 2003, new BigDecimal("1000.00"));

    private JdbcAccountDao accountDao;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        accountDao = new JdbcAccountDao(jdbcTemplate);
    }

    @Test
    public void getAccounts_returnsListOfAccounts(){
        List<Account> accounts = accountDao.getAccounts();
        assertEquals(3,accounts.size());
    }

    @Test
    public void getAccountByUsername_validUsername_returnsAccount() {
        // Arrange
        User user = JdbcUserDaoTests.USER_1;

        // Act
        Account account = accountDao.getAccountByUsername(user.getUsername());

        // Assert
        assertNotNull(account);
        assertEquals(user.getUsername(), account.getUsername());
    }

    @Test
    public void getAccountByUsername_invalidUsername_returnsNull() {
        // Arrange
        String invalidUsername = "Invalid";

        // Act
        Account account = accountDao.getAccountByUsername(invalidUsername);

        // Assert
        assertNull(account);
    }

    @Test
    public void getAccountById_validAccountId_returnsAccount() {
        // Arrange
        int accountId = ACCOUNT_1.getAccountId();

        // Act
        Account account = accountDao.getAccountById(accountId);

        // Assert
        assertNotNull(account);
        assertEquals(accountId, account.getAccountId());
    }

    @Test
    public void getAccountById_invalidAccountId_returnsNull() {
        // Arrange
        int invalidId = -1;

        // Act
        Account account = accountDao.getAccountById(invalidId);

        // Assert
        assertNull(account);
    }

    @Test
    public void getAccountByUserId_validUserId_returnsAccount() {
        // Arrange
        int userId = ACCOUNT_1.getUserId();

        // Act
        Account account = accountDao.getAccountByUserId(userId);

        // Assert
        assertNotNull(account);
        assertEquals(userId, account.getUserId());
    }

    @Test
    public void getAccountByUserId_invalidUserId_returnsNull() {
        // Arrange
        int userId = -1;

        // Act
        Account account = accountDao.getAccountByUserId(userId);

        // Assert
        assertNull(account);
    }

    @Test
    public void updateAccount_validAccount_returnsUpdatedAccount() {
        // Arrange
        BigDecimal newBalance = new BigDecimal("10.00");
        Account updatedAccount = new Account(ACCOUNT_1.getUserId(), ACCOUNT_1.getUsername(),
                ACCOUNT_1.getAccountId(), newBalance);

        // Act
        Account account = accountDao.updateAccountBalance(updatedAccount);

        // Assert
        assertNotNull(account);
        assertEquals(newBalance, account.getBalance());
    }

    @Test(expected = DaoException.class)
    public void updateAccount_zeroRowsAffected_throwsDaoException() {
        // Arrange
        BigDecimal newBalance = new BigDecimal("10.00");
        int invalidAccountId = -1;
        Account invalidAccount = new Account(ACCOUNT_1.getUserId(), ACCOUNT_1.getUsername(),
                invalidAccountId, newBalance);
        accountDao.updateAccountBalance(invalidAccount);
    }
}
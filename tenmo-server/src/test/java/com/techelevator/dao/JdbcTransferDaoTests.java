package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

public class JdbcTransferDaoTests extends BaseDaoTests{

    protected static final Transfer TRANSFER_1 = new Transfer(3001, 1, 1, 2001, 2002, new BigDecimal("50.00"));
    protected static final Transfer TRANSFER_2 = new Transfer(3002,1, 2, 2001, 2003, new BigDecimal("50.00"));
    protected static final Transfer TRANSFER_3 = new Transfer(3003, 1, 2, 2002, 2003, new BigDecimal("50.00"));
    protected static final Transfer TRANSFER_4 = new Transfer(3004, 2, 3, 2002, 2001, new BigDecimal("50.00"));
    protected static final Transfer TRANSFER_5 = new Transfer(3005, 2, 3, 2003, 2001, new BigDecimal("50.00"));
    protected static final Transfer TRANSFER_6 = new Transfer(3006, 2, 3, 2003, 2002, new BigDecimal("50.00"));

    private JdbcTransferDao transferDao;

    @Before
    public void setup(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        transferDao = new JdbcTransferDao(jdbcTemplate);
    }

    @Test
    public void getTransfers_returnsListOfTransfers() {
        List<Transfer> transfers = transferDao.getTransfers();
        assertEquals(6,transfers.size());
    }

    @Test
    public void getTransfersByUserId_validUserId_returnsListOfTransfers() {
        List<Transfer> transfers = transferDao.getTransfersByUserId(JdbcUserDaoTests.USER_1.getId());
        assertEquals(4,transfers.size());
    }

    @Test
    public void getTransfersByUserId_invalidUserId_returnsEmptyList() {
        // Arrange
        int invalidUserId = -1;

        // Act
        List<Transfer> transfers = transferDao.getTransfersByUserId(invalidUserId);

        // Assert
        assertNotNull(transfers);
        assertTrue(transfers.isEmpty());
    }

    @Test
    public void getTransferById_validTransferId_returnsTransfer() {
        // Arrange
        int transferId = TRANSFER_1.getTransferId();

        // Act
        Transfer transfer = transferDao.getTransferById(transferId);

        // Assert
        assertNotNull(transfer);
        assertEquals(transferId, transfer.getTransferId());
    }

    @Test
    public void getTransferById_invalidTransferId_returnsNull() {
        // Arrange
        int invalidTransferId = -1;

        // Act
        Transfer transfer = transferDao.getTransferById(invalidTransferId);

        // Assert
        assertNull(transfer);
    }

    @Test
    public void getPendingTransfersByUserId_validUserId_returnsListOfTransfers() {
        List<Transfer> transfers = transferDao.getPendingTransferRequestsByUserId(JdbcUserDaoTests.USER_2.getId());

        assertNotNull(transfers);
        assertEquals(1, transfers.size());
    }

    @Test
    public void getPendingTransfersByUserId_invalidUserId_returnsEmptyList() {
        List<Transfer> transfers = transferDao.getPendingTransferRequestsByUserId(-1);

        // Assert
        assertNotNull(transfers);
        assertTrue(transfers.isEmpty());
    }

    @Test
    public void createTransfer_validTransfer_returnsCreatedTransfer() {
        Transfer transfer = transferDao.createTransfer(new Transfer(1, 2, 2001, 2002, new BigDecimal("50.00")));

        assertNotNull(transfer);
        assertEquals(3007, transfer.getTransferId());
    }

    @Test
    public void updateTransfer_validTransfer_returnsUpdatedTransfer() {
        // Arrange
        Transfer transferUpdate = new Transfer(TRANSFER_1.getTransferId(),
                TRANSFER_1.getTypeId(), 2,
                TRANSFER_1.getAccountFromId(), TRANSFER_1.getAccountToId(), TRANSFER_1.getAmount());

        // Act
        Transfer updatedTransfer = transferDao.updateTransfer(transferUpdate);

        // Assert
        assertNotNull(updatedTransfer);
        assertEquals(2, updatedTransfer.getStatusId());
    }

    @org.junit.Test(expected = DaoException.class)
    public void updateTransfer_zeroRowsAffected_throwsDaoException() {
        Transfer transferUpdate = new Transfer(-1,
                TRANSFER_1.getTypeId(), TRANSFER_1.getStatusId(),
                TRANSFER_1.getAccountFromId(), TRANSFER_1.getAccountToId(), TRANSFER_1.getAmount());

        Transfer updatedTransfer = transferDao.updateTransfer(new Transfer());
    }
}

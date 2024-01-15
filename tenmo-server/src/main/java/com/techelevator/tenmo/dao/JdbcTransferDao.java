package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
@Component
public class JdbcTransferDao implements TransferDao{
    private JdbcTemplate jdbcTemplate;

    //private Logger log = (Logger) LoggerFactory.getLogger(getClass());
    public JdbcTransferDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<Transfer> getTransfers() {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT * FROM transfer JOIN transfer_type USING(transfer_type_id) JOIN transfer_status USING(transfer_status_id)";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                Transfer transfer = mapRowtoTransfer(results);
                transfers.add((transfer));
            }
        }
        catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }

        return transfers;
    }

    @Override
    public List<Transfer> getTransfersByUserId(int id) {
        List<Transfer> transfers = new ArrayList<>();
        String fromSql = "SELECT * FROM transfer JOIN transfer_type USING(transfer_type_id) JOIN transfer_status USING(transfer_status_id) JOIN account ON transfer.account_from = account.account_id  WHERE user_id = ?";
        String toSql = "SELECT * FROM transfer JOIN transfer_type USING(transfer_type_id) JOIN transfer_status USING(transfer_status_id) JOIN account ON transfer.account_to = account.account_id  WHERE user_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(fromSql, id);
            while (results.next()) {
                Transfer transfer = mapRowtoTransfer(results);
                addAccountUsernamesToTransfer(transfer);
                transfers.add((transfer));
            }
            results = jdbcTemplate.queryForRowSet(toSql, id);
            while (results.next()){
                Transfer transfer = mapRowtoTransfer(results);
                addAccountUsernamesToTransfer(transfer);
                transfers.add((transfer));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transfers;
    }
    @Override
    public Transfer getTransferById(int id) {
            Transfer transfer = null;
            String sql = "SELECT * FROM transfer JOIN transfer_type USING(transfer_type_id) JOIN transfer_status USING(transfer_status_id) WHERE transfer_id = ?";
            try {
                SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
                if (results.next()) {
                    transfer = mapRowtoTransfer(results);
                }
            } catch (CannotGetJdbcConnectionException e) {
                throw new DaoException("Unable to connect to server or database", e);
            }
            return transfer;
        }


    public List<Transfer> getPendingTransferRequestsByUserId(int id) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT * FROM transfer JOIN transfer_type USING(transfer_type_id) JOIN transfer_status USING(transfer_status_id)" +
                " JOIN account ON transfer.account_to = account.account_id" +
                " WHERE user_id = ? AND transfer_status_desc = 'Pending'";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            while (results.next()) {
                Transfer transfer = mapRowtoTransfer(results);
                transfers.add((transfer));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transfers;
    }


    @Override
    public Transfer createTransfer(Transfer transfer) {
            Transfer newTransfer = null;
            String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES(?,?,?,?,?) RETURNING transfer_id";
            try{
                int transferId = jdbcTemplate.queryForObject(sql, int.class, transfer.getTypeId(), transfer.getStatusId(), transfer.getAccountFromId(), transfer.getAccountToId(), transfer.getAmount());
                newTransfer= getTransferById(transferId); }
            catch (CannotGetJdbcConnectionException e) {
                throw new DaoException("Unable to connect to server or database", e);
             } catch (DataIntegrityViolationException e) {
                throw new DaoException("Data integrity violation", e);
            }

            return newTransfer;
        }

    @Override
    public Transfer updateTransfer(Transfer transfer) {
        Transfer newTransfer = null;
        String sql = "UPDATE transfer SET transfer_type_id = ? ,transfer_status_id = ?, account_from = ?, account_to = ?, amount = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, transfer.getTypeId(), transfer.getStatusId(), transfer.getAccountFromId(), transfer.getAccountToId(), transfer.getAmount());
            if (rowsAffected == 0) {
                throw new DaoException("Zero rows affected, expected at least one");
            }
            newTransfer = getTransferById(transfer.getTransferId());
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return newTransfer;
    }




    private  Transfer mapRowtoTransfer(SqlRowSet rs){
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setTypeId(rs.getInt("transfer_type_id"));
        transfer.setTypeDescription(rs.getString("transfer_type_desc"));
        transfer.setStatusId(rs.getInt("transfer_status_id"));
        transfer.setStatusDescription(rs.getString("transfer_status_desc"));
        transfer.setAccountFromId(rs.getInt("account_from"));
        transfer.setAccountToId(rs.getInt("account_to"));
        transfer.setAmount(rs.getBigDecimal("amount"));
        addAccountUsernamesToTransfer(transfer);
        return transfer;

    }

    private void addAccountUsernamesToTransfer(Transfer transfer){
        String usernameSql = "SELECT username FROM tenmo_user JOIN account USING(user_id) WHERE account_id = ?";

        SqlRowSet fromUsernameResult = jdbcTemplate.queryForRowSet(usernameSql, transfer.getAccountFromId());
        SqlRowSet toUsernameResult = jdbcTemplate.queryForRowSet(usernameSql, transfer.getAccountToId());
        if (fromUsernameResult.next()){
            transfer.setFromUsername(fromUsernameResult.getString("username"));
        }
        if (toUsernameResult.next()) {
            transfer.setToUsername(toUsernameResult.getString("username"));
        }
    }
}

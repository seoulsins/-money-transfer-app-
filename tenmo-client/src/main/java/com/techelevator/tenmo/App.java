package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.UserService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TransferService;

import java.util.List;

import java.math.BigDecimal;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final UserService userService = new UserService(API_BASE_URL);
    private final TransferService transferService = new TransferService(API_BASE_URL);
    private AuthenticatedUser currentUser;


    private final int PENDING_STATUS_ID = 1;
    private final int ACCEPTED_STATUS_ID = 2;
    private final int REJECTED_STATUS_ID = 3;
    private final int SEND_TYPE_ID = 2;
    private final int REQUEST_TYPE_ID = 1;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        consoleService.setUserService(userService);
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }

    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        } else {
            userService.setAuthToken(currentUser.getToken());
            transferService.setAuthToken(currentUser.getToken());
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    private void viewCurrentBalance() {
        Account myAccount = userService.getMyAccount();
        consoleService.printBalanceHeader();
        consoleService.printCurrentBalance(myAccount.getBalance());
    }

    private void viewTransferHistory() {
        consoleService.printTransferHistoryHeader();
        consoleService.printTransfers(transferService.getTransferHistory());
    }

    private void viewPendingRequests() {
        List<Transfer> pendingTransfers = transferService.getPendingTransfers();
        consoleService.printPendingRequestsHeader();
        consoleService.printTransfers(pendingTransfers);
        // prompt the user to type a value and choose to accept or deny their request for TE bucks
        int requestId = consoleService.promptForRequestID("Enter a user's transfer ID to accept / reject their request. (Press '0' to cancel): ");
        if (requestId == 0) {
            return; // cancel
        }
        boolean containsId = false;
        Transfer transferToAccept = null;
        for (Transfer transfer : pendingTransfers) {
            if (requestId == transfer.getTransferId()) {
                containsId = true;
                transferToAccept = transfer;
            }


        }
        if (containsId) {
            acceptOrDenyRequests(transferToAccept);
        }


        // make it so that while any selection other than those listed will continue the loop unless 0 is pressed


    }

    public Transfer findTransferById(List<Transfer> transfers, int transferId) {
        for (Transfer transfer : transfers) {
            if (transferId == transfer.getTransferId()) {
                return transfer;
            }


        }
        return null;
    }


    private void sendBucks() {
        consoleService.printSendBucksHeader();
        // Retrieve the list of users
        consoleService.printUsers(userService.getAllAccounts());

        // Prompt the user for recipient and amount
        int recipientId = consoleService.promptForUserId("Enter ID of user you are sending to (0 to cancel): ");
        if (recipientId == 0) {
            return; // User canceled the operation
        }

        if (recipientId == userService.getMyAccount().getUserId()) {
            System.out.println("You may not choose yourself.");
            return; // User tried to send money to self
        }

        BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");

        // Validate the transfer amount & Check if the sender has sufficient funds
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Invalid amount. Please enter a positive value.");
            return;
        } else if (amount.compareTo(userService.getMyAccount().getBalance()) > 0) {
            System.out.println("Insufficient funds. Cannot complete the transfer.");
            return;
        }

        int currentUserAccountId = userService.getMyAccount().getAccountId();
        int recipientUserAccountId = userService.getAccountByUserId(recipientId).getAccountId();


        Transfer transfer = new Transfer(currentUserAccountId, recipientUserAccountId, amount, ACCEPTED_STATUS_ID, SEND_TYPE_ID);

        // Send TE bucks
        transferService.postTransfer(transfer);
        consoleService.printSuccessMessage("TE Bucks sent successfully!");

    }

    private void acceptOrDenyRequests(Transfer transfer ) {
        int choice = consoleService.promptForInt("Enter 1 to accept the request or 2 to reject it (0 to cancel): ");
        int currentUserAccountId;
        if (choice == 1) {
            transfer.setStatusId(ACCEPTED_STATUS_ID);
            transferService.updateTransfer(transfer);
            consoleService.printSuccessMessage("Request Approved, TE bucks sent to " + transfer.getFromUsername());


        }
        if (choice == 2) {
            transfer.setStatusId(REJECTED_STATUS_ID);
            transferService.updateTransfer(transfer);
            consoleService.printSuccessMessage("Request Rejected, TE bucks not sent.");
        }

    }

    private void requestBucks() {
        consoleService.printRequestBucksHeader();
        consoleService.printUsers(userService.getAllAccounts());

        int requestAccountId = consoleService.promptForUserId("Enter ID of user you are requesting from to (0 to cancel): ");

        if (requestAccountId == 0) {
            return; // User canceled the operation
        } else if (requestAccountId == userService.getMyAccount().getUserId()) {
            System.out.println("You may not choose yourself.");
            return; // User tried to send money to self
        }

        BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");

        // Validate the transfer amount & Check if the sender has sufficient funds
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Invalid amount. Please enter a positive value.");
            return;
        } else if (amount.compareTo(userService.getMyAccount().getBalance()) > 0) {
            System.out.println("Insufficient funds. Cannot complete the transfer.");
            return;
        }

        int currentUserAccountId = userService.getMyAccount().getAccountId();
        int RequestedUserAccountId = userService.getAccountByUserId(requestAccountId).getAccountId();

        Transfer transfer = new Transfer(currentUserAccountId, RequestedUserAccountId, amount, PENDING_STATUS_ID, REQUEST_TYPE_ID);

        // Send TE bucks
        transferService.postTransfer(transfer);
        consoleService.printSuccessMessage("TE Bucks requested successfully!");

    }
}


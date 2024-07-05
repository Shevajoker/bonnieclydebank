import entity.BankValidator;
import service.BankService;

import java.util.Scanner;

public class Main {

    static boolean isAuthenticated;
    static Scanner scanner = new Scanner(System.in);
    static String cardNumber;
    static String pin;
    static int enterTryCount;

    public static void main(String[] args) {
        BankService.CreateBank();
        BankService.helpMessage();
        enterTryCount = 3;

        while (!isAuthenticated) {
            System.out.println("Send a command to start");
            if (scanner.nextLine().equals("enter")) {
                System.out.println("Enter your card number:");
                cardNumber = scanner.nextLine();
                if (BankValidator.checkCardNumber(cardNumber)) {
                    System.out.println("Enter your pin number:");
                    pin = scanner.nextLine();
                    if (BankValidator.checkPin(pin)) {
                        if (BankService.authorize(cardNumber, pin) && !BankService.checkIsBlock(cardNumber)) {
                            isAuthenticated = true;
                            System.out.println("You have successfully authenticated");
                        } else if (!BankService.checkIsBlock(cardNumber)) {
                            System.out.println("You have wrong card number or pin number");
                            enterTryCount -= 1;
                            if (enterTryCount > 0) {
                                System.out.println("You have " + enterTryCount + " attempts to authenticate");
                            } else {
                                BankService.blockCard(cardNumber);
                            }
                        } else {
                            System.out.println("Card is blocked!");
                        }
                    } else {
                        System.out.println("Invalid pin number format. Please try again");
                    }
                } else {
                    System.out.println("Invalid card number format. Please try again");
                }
            }


            while (isAuthenticated) {
                String command = scanner.nextLine();
                int amount;
                switch (command) {
                    case "set":
                        System.out.println("Amount:");
                        amount = Integer.parseInt(scanner.nextLine());
                        BankService.setAmount(cardNumber, amount);
                        break;
                    case "get":
                        System.out.println("Amount:");
                        amount = Integer.parseInt(scanner.nextLine());
                        BankService.getAmount(cardNumber, amount);
                        break;
                    case "check-balance":
                        BankService.checkBalance(cardNumber);
                        break;
                    case "exit":
                        cardNumber = "";
                        pin = "";
                        isAuthenticated = false;
                        System.out.println("""
                                ================================
                                Thank you for choosing our bank.
                                  We are waiting for you again.
                                ================================
                                """);
                        break;
                    default:
                        System.out.println("Invalid command. Please try again");
                        break;
                }
            }
        }


    }
}
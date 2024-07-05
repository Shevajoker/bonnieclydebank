package service;

import entity.ATMInfo;
import entity.Card;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class BankService {

    public static final String BANK_TXT = "bank.txt";
    public static final Integer SET_AMOUNT_LIMIT = 1000000;

    public static void CreateBank() {
        File file = new File(BANK_TXT);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write("""
                            1234-1234-1234-1234 1234 50000 false
                            5678-5678-5678-5678 5678 145000 false
                            """);
                    fileWriter.close();
                } else {
                    System.out.println("Bank exist!");
                }
            } catch (IOException e) {
                System.out.println("Bank not created");
            }
        }
        System.out.println("""
                ============************=============
                 Welcome to the Bonnie & Clyde Bank!
                ============************=============
                """);
    }

    public static void helpMessage() {
        System.out.println("""
                Commands:
                ================================================
                enter          |  use to start;
                exit           |  use to exit;
                set            |  use to set some money to card;
                get            |  use to get money;
                check-balance  |  use to check balance;
                ================================================
                """);
    }

    public static boolean authorize(String cardNumber, String pin) {
        ATMInfo atmInfo = getATMInfo();
        Card card = atmInfo.getCards().stream()
                .filter(cardItem -> cardItem.getNumber().equals(cardNumber) && cardItem.getPin().equals(pin))
                .findFirst()
                .orElse(null);
        if (card == null) {
            return false;
        }
        if (card.isBlock() && card.getUnlockDay().before(new Date())) {
            unBlockCard(cardNumber);
        }
        return true;
    }

    public static Card getCard(String cardNumber) {
        ATMInfo atmInfo = getATMInfo();
        return atmInfo.getCards().stream()
                .filter(cardItem -> cardItem.getNumber().equals(cardNumber))
                .findFirst()
                .orElse(null);
    }

    private static ATMInfo getATMInfo() {
        ATMInfo atmInfo = new ATMInfo();
        File file = new File(BANK_TXT);
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(" ");
                Card card = new Card(data[0], data[1])
                        .setAmount(Integer.valueOf((data[2])))
                        .setBlock(Boolean.parseBoolean(data[3]));
                if (card.isBlock()) {
                    card.setUnlockDay(getDate(data[4], data[5]));
                }
                atmInfo.getCards().add(card);
            }
            bufferedReader.close();
            fileReader.close();
        } catch (ParseException | IOException e) {
            throw new RuntimeException(e);
        }
        return atmInfo;
    }

    private static Date getDate(String date, String time) throws ParseException {
        String s = date + " " + time;
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd.MM.yyyy HH:mm");
        return format.parse(s);
    }

    private static String dateToString(Date date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return formatter.format(date);
    }

    public static void setAmount(String cardNumber, Integer amount) {
        ATMInfo atmInfo = getATMInfo();
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(BANK_TXT));
            for (Card item : atmInfo.getCards()) {
                String dataLine = "";
                if (item.getNumber().equals(cardNumber)) {
                    if (item.isBlock()) {
                        System.out.println("Card is blocked until " + item.getUnlockDay());
                    } else {
                        if (amount < SET_AMOUNT_LIMIT) {
                            item.setAmount(item.getAmount() + amount);
                            System.out.println("You set " + amount + " to your card");
                        } else {
                            System.out.println("The amount exceeds the allowable limit");
                        }
                    }
                }
                dataLine = item.getNumber() + " " + item.getPin() + " " + item.getAmount() + " " + item.isBlock();
                if (item.isBlock()) {
                    dataLine += " " + dateToString(item.getUnlockDay());
                }
                printWriter.println(dataLine);
            }
            printWriter.close();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static void getAmount(String cardNumber, Integer amount) {
        ATMInfo atmInfo = getATMInfo();
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(BANK_TXT));
            for (Card item : atmInfo.getCards()) {
                String dataLine = "";
                if (item.getNumber().equals(cardNumber)) {
                    if (item.isBlock()) {
                        System.out.println("Card is blocked until " + item.getUnlockDay());
                    } else {
                        if (item.getAmount() < amount) {
                            System.out.println("Not enough money on your card!");
                        } else if (atmInfo.getLimit() < amount) {
                            System.out.println("Not enough money in ATM!");
                        } else {
                            item.setAmount(item.getAmount() - amount);
                            System.out.println("You get " + amount + " from your card");
                        }
                    }
                }
                dataLine = item.getNumber() + " " + item.getPin() + " " + item.getAmount() + " " + item.isBlock();
                if (item.isBlock()) {
                    dataLine += " " + dateToString(item.getUnlockDay());
                }
                printWriter.println(dataLine);
            }
            printWriter.close();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static void checkBalance(String cardNumber) {
        ATMInfo atmInfo = getATMInfo();
        for (Card item : atmInfo.getCards()) {
            if (item.getNumber().equals(cardNumber)) {
                System.out.println("Balance: " + item.getAmount());
            }
        }
    }

    public static void blockCard(String cardNumber) {
        Card card = getCard(cardNumber);
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        instance.add(Calendar.DAY_OF_MONTH, 1);
        card.setBlock(true);
        card.setUnlockDay(instance.getTime());
        updateCard(card);
        System.out.println("Card blocked!");
    }

    public static void unBlockCard(String cardNumber) {
        Card card = getCard(cardNumber);
        card.setBlock(false);
        updateCard(card);
        System.out.println("Card unblocked!");
    }

    private static void updateCard(Card card) {
        ATMInfo atmInfo = getATMInfo();
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(BANK_TXT));
            for (Card item : atmInfo.getCards()) {
                String dataLine = "";
                if (item.getNumber().equals(card.getNumber())) {
                    item.setAmount(card.getAmount());
                    item.setBlock(card.isBlock());
                    item.setUnlockDay(card.getUnlockDay());
                }
                dataLine = item.getNumber() + " " + item.getPin() + " " + item.getAmount() + " " + item.isBlock();
                if (item.isBlock()) {
                    dataLine += " " + dateToString(item.getUnlockDay());
                }
                printWriter.println(dataLine);
            }
            printWriter.close();
            System.out.println("Card updated!");
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean checkIsBlock(String cardNumber) {
        return getCard(cardNumber).isBlock();
    }
}

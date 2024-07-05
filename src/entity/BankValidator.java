package entity;

public final class BankValidator {

    public static boolean checkCardNumber(String cardNumber) {
        return cardNumber.matches("\\d{4}-\\d{4}-\\d{4}-\\d{4}");
    }

    public static boolean checkPin(String pin) {
        return pin.matches("\\d{4}");
    }
}

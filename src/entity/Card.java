package entity;

import java.util.Date;

public class Card {

    private final String number;
    private final String pin;
    private Integer amount;
    private boolean isBlock;
    private Date unlockDay;

    public Card(String number, String pin) {
        this.number = number;
        this.pin = pin;
    }

    public String getNumber() {
        return number;
    }

    public String getPin() {
        return pin;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public Card setBlock(boolean block) {
        isBlock = block;
        return this;
    }

    public Date getUnlockDay() {
        return unlockDay;
    }

    public Card setUnlockDay(Date unlockDay) {
        this.unlockDay = unlockDay;
        return this;
    }

    public Integer getAmount() {
        return amount;
    }

    public Card setAmount(Integer amount) {
        this.amount = amount;
        return this;
    }
}

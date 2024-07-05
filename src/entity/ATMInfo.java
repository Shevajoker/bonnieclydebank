package entity;

import java.util.ArrayList;
import java.util.List;

public class ATMInfo {

    public static final int LIMIT = 1000000;
    private Integer limit;
    private List<Card> cards;

    public ATMInfo() {
        this.limit = LIMIT;
        this.cards = new ArrayList<Card>();
    }

    public Integer getLimit() {
        return limit;
    }

    public ATMInfo setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    public List<Card> getCards() {
        return cards;
    }

    public ATMInfo setCards(List<Card> cards) {
        this.cards = cards;
        return this;
    }
}

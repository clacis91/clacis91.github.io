package idcard;

import framework.*;

public class IDCard extends Product {
    private String owner;
    private int cardId;

    public IDCard(String owner) {
        System.out.println(owner + "의 카드를 생성합니다.");
        this.owner = owner;
        this.cardId = (int) (Math.random() * 100000);
    }
    public void use() {
        System.out.println(owner + "의 카드(" + String.valueOf(cardId) + ")를 사용합니다.");
    }
    public String getOwner() {
        return owner;
    }
    public int getCardId() {
        return cardId;
    }
}
package game;

import java.io.Serializable;

public class Memento implements Serializable {
    int money;
    Memento(int money) {
        this.money = money;
    }
    public int getMoney() {
        return money;
    }
}
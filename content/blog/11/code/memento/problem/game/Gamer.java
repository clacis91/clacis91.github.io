package game;

import java.util.Random;

public class Gamer {
    private int money;
    private Random random = new Random();
    private FileManager mementoFileManager = new FileManager("game.dat");

    public Memento init() {
        Memento m;
        if(mementoFileManager.exists()) {
            m = loadMemento();
            this.money = m.getMoney();
        }
        else {
            this.money = 100;
            m = createMemento();
        }
        return m;
    }

    public int getMoney() {
        return money;
    }
    public void bet() {
        int dice = random.nextInt(6) + 1;
        if(dice == 1) {
            money += 100;
            System.out.println("소지금 증가");
        }
        else if(dice == 2) {
            money /= 2;
            System.out.println("소지금 절반 감소");
        }
        else if(dice == 3) {
            money /= 10;
            System.out.println("소지금 1/10 감소");
        }
        else if(dice == 6) {
            money *= 2;
            System.out.println("소지금 2배 증가");
        }
        else {
            System.out.println("변화 없음");
        }
    }

    public Memento createMemento() {
        Memento m = new Memento(money);
        return m;
    }

    public Memento loadMemento() {
        return (Memento) mementoFileManager.read();
    }

    public Memento saveMemento() {
        Memento m = createMemento();
        mementoFileManager.write(m);
        return m;
    }

    public void restoreMemento() {
        if(mementoFileManager.exists())
            this.money = loadMemento().getMoney();
        else
            this.money = 100;
    }

    public String toString() {
        return "[money = " + money + "]";
    }
}
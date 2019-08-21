import game.*;

public class Main {
    public static void main(String[] args) {
        Gamer gamer = new Gamer(100);
        Memento memento = gamer.createMemento();
        for(int i = 0; i < 100; i++) {
            System.out.println("==== " + i);
            System.out.println("현재 상태 : " + gamer);

            gamer.bet();

            if(gamer.getMoney() > memento.getMoney()) {
                // 돈이 늘었으면 현재 상태 저장
                System.out.println("대박");
                memento = gamer.createMemento();
            }
            else if(gamer.getMoney() < (memento.getMoney()/2)) {
                // 좋았을때보다 너무 감소하면 로드
                System.out.println("리셋");
                gamer.restoreMemento(memento);
            }
        }

        try {
            Thread.sleep(1000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
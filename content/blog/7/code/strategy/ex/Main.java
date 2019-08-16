public class Main {
    public static void main(String[] args) {
        Player player1 = new Player("유저1", new WinningStrategy((int)System.currentTimeMillis()));
        Player player2 = new Player("유저2", new ProbStrategy((int)System.currentTimeMillis() + 1));

        for(int i = 0; i < 10000; i++) {
            Hand player1Hand = player1.nextHand();
            Hand player2Hand = player2.nextHand();

            if(player1Hand.isStrongerThan(player2Hand)) {
                player1.win();
                player2.lose();
            }
            else if(player1Hand.isWeakerThan(player2Hand)) {
                player1.lose();
                player2.win();
            }
            else {
                player1.even();
                player2.even();
            }
        }
        System.out.println(player1.toString());
        System.out.println(player2.toString());
    }
}
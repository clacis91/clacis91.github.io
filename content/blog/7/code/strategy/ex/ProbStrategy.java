import java.util.Random;

public class ProbStrategy implements Strategy {
    private Random random;
    private boolean won = false;
    private Hand prevHand;
    public ProbStrategy(int seed) {
        random = new Random(seed);
    }

    public Hand nextHand() {
        // 상대가 졌으면 똑같은건 연속으로 안낸다고 생각하는 전략
        // 상대가 가위를 내서 졌다면? -> 주먹 아니면 보를 낼것이다 -> 보를 내면 지지는 않는다
        // => 상대가 방금 턴에 낸 손보다 약한 손을 내면 된다
        Hand prevEnemyHand;
        if(won) {
            prevEnemyHand = Hand.getHand(prevHand.getWeaker()); // 이겼다면 자신의 손보다 약한 손이 상대의 손이다
            prevHand = Hand.getHand(prevEnemyHand.getWeaker()); // 상대의 손보다 약한 손을 반환
        }
        else {
            prevHand = Hand.getHand(random.nextInt(3)); // 이전판에 이기지 않았다면 랜덤 손 반환
        }
        return prevHand;
    }

    public void study(boolean win) {
        won = win;
    }
}
public class Hand {
    public static final int HANDVALUE_MOOK = 0;
    public static final int HANDVALUE_JJI = 1;
    public static final int HANDVALUE_PPA = 2;
    public static final Hand[] hand = {
        new Hand(HANDVALUE_MOOK),
        new Hand(HANDVALUE_JJI),
        new Hand(HANDVALUE_PPA),
    };
    public static final String[] handName = {
        "주먹", "가위", "보"
    };
    
    private int handValue;
    private Hand(int handValue) {
        this.handValue = handValue;
    }

    public static Hand getHand(int handValue) {
        return hand[handValue];
    }

    public boolean isStrongerThan(Hand h) {
        return fight(h) == 1;
    }

    public boolean isWeakerThan(Hand h) {
        return fight(h) == -1;
    }

    // 자신이 낸 손을 이기는 손을 반환
    public int getStronger() {
        int stronger = this.handValue - 1;
        if(stronger < 0) 
            return 2;
        return stronger;
    }

    // 자신이 낸 손으로 이기는 손을 반환
    public int getWeaker() {
        return (this.handValue + 1) % 3;
    }

    private int fight(Hand h) {
        if(this == h) {
            return 0;
        }
        else if((this.handValue + 1) % 3 == h.handValue) {
            return 1;
        }
        else {
            return -1;
        }
    }

    public String toString() {
        return handName[handValue];
    }
}
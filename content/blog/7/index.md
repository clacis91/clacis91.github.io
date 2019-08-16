---
title: Java 디자인 패턴 Study - (5)
date: "2019-08-09"
---

Reference Book : [Java 언어로 배우는 디자인 패턴 입문](https://www.youngjin.com/book/book_detail.asp?prod_cd=9788931436914&seq=4628&cate_cd=1&child_cate_cd=&goPage=11&orderByCd=3), Yuki Hiroshi, 영진닷컴

---

### Bridge 패턴

> '기능의 클래스' <==다리==> '구현의 클래스'

여태까지 패턴의 유형을 봤을 때 클래스는 크게 '기능을 정의'하기 위한 클래스와 그 기능을 '구현'하는 클래스로 나뉘어진다. 클래스는 기능과 구현이 분리되어 있어야 각각을 독립적으로 확장할 수 있다.  
보통 '기능 클래스'의 생성자로 '구현' 클래스의 인스턴스를 전달하는데 이게 바로 '다리'의 역할을 한다.  

![](bridge-1.png)

#### 예제 프로그램 - Display

*'무엇인가'(구현)*를 *'어떻게'* 표시하는 프로그램

|Which?|Name|Description|
|:----:|:--:|:----------|
|기능|Display|'표시한다'의 기능 클래스|
|기능|CountDisplay|'표시한다'를 상속하는 '*지정 횟수만큼* 표시한다'의 기능 클래스|
|구현|DisplayImpl|'*무엇인가를* 표시한다'의 구현 클래스|
|구현|StringDisplayImpl|'*문자열을* 표시한다'의 구현|

```java
public class Display {
    private DisplayImpl impl;
    public Display(DisplayImpl impl) {
        this.impl = impl;
    }

    public void open() {
        impl.rawOpen();
    }

    public void close() {
        impl.rawClose();
    }

    public void print() {
        impl.rawPrint();
    }

    public final void display() {
        open();
        print();
        close();
    }
}
```

```java
public class CountDisplay extends Display {
    public CountDisplay(DisplayImpl impl) {
        super(impl);
    }

    public void multiDisplay(int times) {
        open();
        for(int i = 0; i < times; i++) 
            print();
        close();
    }
}
```

```java
public abstract class DisplayImpl {
    public abstract void rawOpen();
    public abstract void rawClose();
    public abstract void rawPrint();
}
```

```java
public class StringDisplayImpl extends DisplayImpl {
    private String str;
    private int width;

    public StringDisplayImpl(String str) {
        this.str = str;
        this.width = str.getBytes().length;
    }

    public void rawOpen() {
        printLine();
    }

    public void rawClose() {
        printLine();
    }

    public void rawPrint() {
        System.out.println("|" + str + "|");
    }

    private void printLine() {
        System.out.print("+");
        for(int i = 0; i < width; i++) 
            System.out.print("-");
        System.out.println("+");
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        Display d1 = new Display(new StringDisplayImpl("Hello, World!"));
        Display d2 = new CountDisplay(new StringDisplayImpl("World, Hello!"));

        CountDisplay d3 = new CountDisplay(new StringDisplayImpl("World, Bye!"));

        d1.display();
        d2.display();
        d3.display();
        d3.multiDisplay(3);
    }
}
```

---

#### 연습문제

Q1. 랜덤 횟수만큼 표시하는 클래스를 추가해보자

```java
public class RandomCountDisplay extends Display {
    public RandomCountDisplay(DisplayImpl impl) {
        super(impl);
    }

    public void randomDisplay(int times) {
        int randomCnt = (int) (Math.random() * (times-1) + 1);
        open();
        for(int i = 0; i < randomCnt; i++) 
            print();
        close();
    }
}
```

(근데 이걸 꼭 클래스로 추가해야되나?? 메소드만 추가하면 될거같은데....)  
--> RandomCountDisplay는 CountDisplay를 상속하라는 의미였다..!

```java
public class RandomCountDisplay extends CountDisplay {
    public RandomCountDisplay(DisplayImpl impl) {
        super(impl);
    }

    public void randomDisplay(int times) {
        int randomCnt = (int) (Math.random() * (times-1) + 1);
    }
}
```

---

Q2. '텍스트 파일의 내용을 표시한다' 라는 기능을 추가해보자

```java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileDisplayImpl extends DisplayImpl {
    private String filename;
    private BufferedReader reader;

    public FileDisplayImpl(String filename) {
        this.filename = filename;
    }

    public void rawOpen() {
        try {
            reader = new BufferedReader(new FileReader(filename));
            reader.mark(4096);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void rawClose() {
        try {
            reader.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void rawPrint() {
        try {
            System.out.println("==========FILE READER========");
            String fileLine;
            reader.reset();
            while( (fileLine = reader.readLine()) != null ) {
                System.out.println("|" + fileLine + "|");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
```

### Strategy 패턴

> 문제 해결을 위한 알고리즘 교체를 도와주는 패턴

#### 예제 프로그램 - 가위바위보

단순한 가위바위보를 하는 데에도 여러가지 전략이 있을 수 있다. '이기면 냈던 손을 또 낸다' 라는 전략도 있을 수 있고, '상대가 직전에 냈던 것으로 다음 것을 예측해서 낸다' 라는 전략도 있을 수 있다.

|Class(Interface)|Description|
|:--------------:|:----------|
|Hand|가위바위보의 '손'을 나타내는 클래스|
|Strategy|가위바위보의 '전략'을 나타내는 클래스|
|WinningStrategy|이기면 같은 손을 또 내는 전략 클래스|
|ProbStrategy|다음 손을 확률적으로 계산해서 내는 전략 클래스|
|Player|가위바위보를 하는 사람 클래스|

```java
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
```

가위바위보 게임의 규칙과 관련된 내용이 구현되어 있는 클래스이다. 게임의 규칙은 하나만 존재하면 되므로 Singleton 형태로 구현되어 있다.

```java
public interface Strategy {
    public Hand nextHand();
    public void study(boolean win);
}
```

```java
import java.util.Random;

public class WinningStrategy implements Strategy {
    private Random random;
    private boolean won = false;
    private Hand prevHand;
    public WinningStrategy(int seed) {
        random = new Random(seed);
    }

    public Hand nextHand() {
        if(!won) {
            prevHand = Hand.getHand(random.nextInt(3));
        }
        return prevHand;
    }

    public void study(boolean win) {
        won = win;
    }
}
```

첫번째 전략 - <U>이기면 그 손을 다음에 또 낸다는 단순한 전략</U>

```java
import java.util.Random;

public class ProbStrategy implements Strategy {
    private Random random;
    private boolean won = false;
    private Hand prevHand;
    public ProbStrategy(int seed) {
        random = new Random(seed);
    }

    public Hand nextHand() {
        Hand prevEnemyHand;
        if(won) {
            // 이겼다면 자신의 손보다 약한 손이 상대의 손이다
            prevEnemyHand = Hand.getHand(prevHand.getWeaker()); 
            // 상대의 손보다 약한 손을 반환
            prevHand = Hand.getHand(prevEnemyHand.getWeaker()); 
        }
        else {
            prevHand = Hand.getHand(random.nextInt(3));
        }
        return prevHand;
    }

    public void study(boolean win) {
        won = win;
    }
}
```

(책에 나와있는 알고리즘이 쓸데없이 긴것 같아서 다른 전략을 만들어봄)

두번째 전략 - <U>상대가 졌다면 전판과 똑같은 손은 연속으로 안낼것이라고 생각하는 전략</U>

(상대가 가위를 내서 졌다면? -> 주먹 아니면 보를 낼것이다 -> 보를 내면 지지는 않는다)  
=> *상대가 방금 턴에 낸 손보다 약한 손을 내면 된다*

```java
public class Player {
    private String name;
    private Strategy strategy;
    private int winCount;
    private int loseCount;
    private int gameCount;

    public Player(String name, Strategy strategy) {
        this.name = name;
        this.strategy = strategy;
    }

    public Hand nextHand() {
        return strategy.nextHand();
    }

    public void win() {
        strategy.study(true);
        winCount++;
        gameCount++;
    }

    public void lose() {
        strategy.study(false);
        loseCount++;
        gameCount++;
    }

    public void even() {
        strategy.study(false);
        gameCount++;
    }

    public String toString() {
        return "[" + name + ":" + gameCount + " games, " + winCount + " wins, " + loseCount + " loses]";
    }
}
```

```java
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
```

player1은 WinningStrategy를, player2는 ProbStrategy 객체를 생성해서 전략으로 채택했다.

* Strategy 패턴을 사용하면 시스템 동작 중에도 알고리즘을 유연하게 교체하는 것이 가능하다. 
  * 예를들어 메모리가 부족한 상태가 되면 속도는 느리지만 메모리를 조금 사용하는 알고리즘을 사용하고, 메모리가 여유 있으면 속도가 빠르면서 메모리를 많이 사용하는 알고리즘을 사용할 수도 있다. 
  * 이 때 메모리 상태에 따라 Strategy 객체를 바꿔서 할당하기만 해도 알고리즘을 교체할 수 있다.
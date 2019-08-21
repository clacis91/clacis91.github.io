---
title: Java 디자인 패턴 Study - (8)
date: "2019-08-21"
---

Reference Book : [Java 언어로 배우는 디자인 패턴 입문](https://www.youngjin.com/book/book_detail.asp?prod_cd=9788931436914&seq=4628&cate_cd=1&child_cate_cd=&goPage=11&orderByCd=3), Yuki Hiroshi, 영진닷컴

---

### Observer 패턴

> 관찰 대상의 상태가 변하면 관찰자에게 알려주는 패턴

객체의 상태 변화를 관찰하는 관찰자들의 목록을 객체에 등록하여 상태 변화가 있을 때마다 메소드 등을 통해 객체가 각 Observer에게 통지하도록 한다. Pub-Sub 모델을 따르는 패턴이다.

#### 예제 프로그램

랜덤 넘버가 발생하면 관찰중인 객체에서 그 넘버를 출력하는 프로그램

|Name|Description|
|:--:|:----------|
|Observer|관찰자 인터페이스|
|NumberGenerator|넘버를 생성기 클래스|
|RandomNumberGenerator|랜덤 넘버 생성기 클래스|
|DigitObserver|숫자로 넘버를 표시해주는 클래스|
|GraphObserver|그래프(*로 표시)로 넘버를 표시해주는 클래스|

```java
public interface Observer {
    public void update(NumberGenerator generator);
}
```

```java
import java.util.ArrayList;
import java.util.Iterator;

public abstract class NumberGenerator {
    private ArrayList observers = new ArrayList();
    public void addObserver(Observer observer) {
        observers.add(observer);
    }
    public void deleteObserver(Observer observer) {
        observers.remove(observer);
    }
    public void notifyObservers() {
        Iterator it = observers.iterator();
        while(it.hasNext()) {
            Observer observer = (Observer) it.next();
            observer.update(this);
        }
    }

    public abstract int getNumber();
    public abstract void execute();
}
```

```java
import java.util.Random;

public class RandomNumberGenerator extends NumberGenerator {
    private Random random = new Random();
    private int number;

    public int getNumber() {
        return number;
    }
    public void execute() {
        for(int i = 0; i < 20; i++) {
            number = random.nextInt(50);
            notifyObservers();
        }
    }
}
```

```java
public class DigitObserver implements Observer {
    public void update(NumberGenerator generator) {
        System.out.println("DigitObserver : " + generator.getNumber());
        try {
            Thread.sleep(100);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

```java
public class GraphObserver implements Observer {
    public void update(NumberGenerator generator) {
        int count = generator.getNumber();
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < count; i++) {
            sb.append("*"); 
        }
        System.out.println("GraphObserver : " + sb.toString());
        try {
            Thread.sleep(100);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        NumberGenerator generator = new RandomNumberGenerator();
        Observer observer1 = new DigitObserver();
        Observer observer2 = new GraphObserver();
        generator.addObserver(observer1);
        generator.addObserver(observer2);
        generator.execute();
    }
}
```

NumberGenerator를 관찰하는 Observer를 등록(addObserver)해두면, generator에서 이벤트가 발생할때 그 정보를 등록되어있는 Observer들에게 알리는(update) 구조

### Memento 패턴

> Undo

인스턴스의 상태를 기록해서 나중에 인스턴스를 그 시점의 상태로 되돌릴 수 있게 하는 패턴

#### 예제 프로그램

주사위를 던져서 좋은수가 나오면 돈이 증가하고, 나쁜수가 나오면 돈이 감소한다. 돈이 일정 수준으로 떨어지면 제일 돈이 많았을 때로 리셋한다.

|Package|Name|Description|
|:-----:|:--:|:----------|
|game|Memento|Gamer의 상태를 나타내는 클래스|
|game|Gamer|게이머, Memento 인스턴스를 만든다|
||Main|게임을 진행시키는 클래스, Memento 인스턴스를 저장해두고, 필요하면 로드한다|

```java
public class Memento {
    int money;
    Memento(int money) {
        this.money = money;
    }
    public int getMoney() {
        return money;
    }
}
```

```java
import java.util.Random;

public class Gamer {
    private int money;
    private Random random = new Random();
    public Gamer(int money) {
        this.money = money;
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

    public void restoreMemento(Memento memento) {
        this.money = memento.getMoney();
    }

    public String toString() {
        return "[money = " + money + "]";
    }
}
```

```java
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
```

##### Memento 패턴에서 객체들의 역할

* Memento

현재 상태에 대한 정보를 가지고 있는 객체

* Originator 

현재 상태의 Memento를 저장하거나, 리셋시키는 동작을 수행하는 객체

* Caretaker

언제 저장을 할지, 언제 리셋을 할지 결정하고 Originator 에게 그 동작을 수행시키는 객체

Originator - Caretaker 를 분리시키는 것이 핵심

#### 연습 문제

Q. Serializable 인터페이스를 활용해서, 이전 상태에 대한 파일이 남아있다면 프로그램 시작 시 불러오고 아니면 100원을 갖고 시작하도록 프로그램을 수정해보자

```java
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.IOException;


public class FileManager {
    private String FILENAME; 
    private Object obj;
    private File file;

    public FileManager(String filename) {
        this.FILENAME = filename;
    }

    public boolean exists() {
        File file = new File(FILENAME);
        return file.exists();
    }

    public Object read() {
        try {
            ObjectInput oi = new ObjectInputStream(new FileInputStream(FILENAME));
            obj = oi.readObject();
            return obj;
        } catch(IOException e) {
            e.printStackTrace();
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void write(Object obj) {
        try {
            ObjectOutput oo = new ObjectOutputStream(new FileOutputStream(FILENAME));
            oo.writeObject(obj);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
```

```java
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
```

```java
public class Main {
    public static void main(String[] args) {
        Gamer gamer = new Gamer();
        Memento memento = gamer.init();
        for(int i = 0; i < 100; i++) {
            System.out.println("==== " + i);
            System.out.println("현재 상태 : " + gamer);

            gamer.bet();

            if(gamer.getMoney() > memento.getMoney()) {
                // 돈이 늘었으면 현재 상태 저장
                System.out.println("대박");
                memento = gamer.saveMemento();
            }
            else if(gamer.getMoney() < (memento.getMoney()/2)) {
                // 좋았을때보다 너무 감소하면 로드
                System.out.println("리셋");
                gamer.restoreMemento();
            }
        }

        try {
            Thread.sleep(1000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

* Memento.java 는 수정사항 X

### State 패턴

> 추상적인 '상태'를 클래스로 표현하는 패턴

#### 예제 프로그램

시간에 따라 경비 상태가 변화하는 경비 시스템 프로그램

```
1. 금고가 1개 있다
2. 금고는 현재 시간을 감시하고 있다
3. 주간(9:00~16:59), 야간(17:00~8:59)
4. 금고는 주간에만 이용 가능하며, 이용시 기록이 남는다
5. 야간에 사용시 경비실에 비상통보가 간다
```

##### State 패턴이 적용되지 않는다면?

```java
if(time in 주간) {
    // 이용 및 이용 기록 저장
}
else if(time in 야간) {
    // 비상통보 발동
}
```
State 패턴이 적용되지 않는다면 위 코드와 같은 방식으로 분기를 통해 현재 시간을 파악하고, 동작을 수행하는 방식이 될것이다.

##### State 패턴 적용

```java
class 주간 상태를 표현하는 클래스 {
    금고 사용 메소드() {
        금고사용;
    }
}
```

```java
class 야간 상태를 표현하는 클래스 {
    금고 사용 메소드() {
        비상통보;
    }
}
```

```java
class 상태 관리 클래스 {
    상태 변경() {
        curState = 주간 state;
    }
}

...
 
state.금고 사용 메소드();

...
```

마지막 줄의 *state.금고 사용 메소드();* 과 같은 방식으로 금고를 사용을 하는 입장에서는 현재 상태는 알 필요가 없는 형태로 만들어둔다면, 새로운 상태가 추가(주간,야간,점심시간 등으로)되거나 상태에 따른 동작이 변경되어도 의존성을 최소화 시킬 수 있다.  
(대신 <b>상태를 관리해주는 클래스의 역할</b>이 중요해진다.)

### Flyweight 패턴

> 'new' 를 줄이자

이미 만들어진 인스턴스가 있다면 최대한 활용하여 메모리 사용을 줄이자는 내용

#### 예제 프로그램

'무거운 인스턴스'를 만든다는 의미로 '큰 숫자'를 출력하는 프로그램을 만들자. '큰 숫자'는 텍스트 파일로 저장돼있고 출력시 파일 내용을 그대로 활용한다.

```
....######......  
..##......##....  
..........##....  
......####......  
..........##....  
..##......##....  
....######......  
................ 
``` 

이런식으로.

|Name|Description|
|:--:|:----------|
|BigChar|'큰 문자'를 나타내는 클래스|
|BigCharFactory|BigChar의 인스턴스를 공유하면서 생성하는 클래스|
|BigString|BigChar를 모아 '큰 문자열'을 나타내는 클래스|

```java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.StringBuffer;

public class BigChar {
    private char charname;
    private String fontdata;
    public BigChar(char charname) {
        this.charname = charname;
        try {
            BufferedReader reader = new BufferedReader(
                new FileReader("big" + charname + ".txt")
            );
            String line;
            StringBuffer sb = new StringBuffer();
            while((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            this.fontdata = sb.toString();
        } catch(IOException e) {
            e.printStackTrace();
            this.fontdata = charname + "?";
        }
    }

    public void print() {
        System.out.println(fontdata);
    }
}
```

```java
import java.util.HashMap;

public class BigCharFactory {
    private HashMap pool = new HashMap();
    private static BigCharFactory sigleton = new BigCharFactory();
    private BigCharFactory() {}
    public static BigCharFactory getInstance() {
        return sigleton;
    }
    public synchronized BigChar getBigChar(char charname) {
        BigChar bigChar = (BigChar) pool.get(charname);
        if(bigChar == null) {
            bigChar = new BigChar(charname);
            pool.put(charname, bigChar);
        }
        return bigChar;
    }
}
```

```java
public class BigString {
    private BigChar[] bigString;
    public BigString(String string) {
        bigString = new BigChar[string.length()];
        BigCharFactory factory = BigCharFactory.getInstance();
        for(int i = 0; i < bigString.length; i++) {
            bigString[i] = factory.getBigChar(string.charAt(i));
        }
    }
    public void print() {
        for(int i = 0; i < bigString.length; i++) {
            bigString[i].print();
        }
    }
}
```

* 이미 만들어진 '큰 숫자' 인스턴스가 존재 한다면 새로운 인스턴스는 만들 필요가 없다.  
큰3은 큰3일 뿐이지, 추가적인 정보의 변동이 필요없으니(<b>intrinsic</b>한 정보라고 한다), 동일한 인스턴스를 공유해도 문제가 되지 않는다.

* Garbage collecting에 유의

* 이거 결국 singleton을 잘 활용하자는 내용이 되는듯?

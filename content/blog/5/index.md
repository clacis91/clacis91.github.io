---
title: Java 디자인 패턴 Study - (3)
date: "2019-08-07"
---

Reference Book : [Java 언어로 배우는 디자인 패턴 입문](https://www.youngjin.com/book/book_detail.asp?prod_cd=9788931436914&seq=4628&cate_cd=1&child_cate_cd=&goPage=11&orderByCd=3), Yuki Hiroshi, 영진닷컴

---

### Singleton 패턴

> 어떤 클래스의 인스턴스가 전체 시스템에서 '단 하나'만 있어야 할 때

인스턴스 생성(new)을 외부에서 직접 할 수 없게 하고 내부에서만 하는 구조로 하여 인스턴스 개수를 1개로 통제한다. 물론 프로그래머가 주의해서 인스턴스를 1개만 만들어서 쓸 수도 있지만, 프로그램 상에서 표현해둘 경우 1개만 존재한다는 것을 보증할 수 있게된다.

```java 
public class Singleton {
    private static Singleton singleton = new Singleton();

    private Singleton() {
        System.out.println("싱글톤 인스턴스 생성");
    }

    public static Singleton getInstance() {
        return singleton;
    }
}
```

<b>생성자가 public이 아니라 private 인 것에 주목</b> -> 외부에서 인스턴스 생성 불가능

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("Singleton main start");
        Singleton obj1 = Singleton.getInstance();
        Singleton obj2 = Singleton.getInstance();

        if(obj1 == obj2)
            System.out.println("obj1과 obj2는 같은 인스턴스입니다.");
        else 
            System.out.println("obj1과 obj2는 다른 인스턴스입니다.");
    }
}
```

* Singleton 인스턴스는 무조건 1개만 존재하고 있기 때문에 obj1과 obj2는 동일한 인스턴스이다

* 실행 시 *"Singleton main start"* 출력 이후에 *"싱글톤 인스턴스 생성"* 이 출력된다. 시스템 내에서 1개만 static 하게 존재한다고 해도 getInstance()로 호출하기 전에는 생성되지 않는다는 말이 된다.

#### 연습문제

1. TicketMaker를 Singleton 클래스로 수정

```java
public class TicketMaker {
    private int ticket = 1000;
    public int getNextTicketNumber() {
        return ticket++;
    }
}
```

```java
public class TicketMaker {
    private int ticket = 1000;
    private static TicketMaker ticketMaker = new TicketMaker();

    private TicketMaker() {
    }

    public static TicketMaker getInstance() {
        return ticketMaker;
    }

    public synchronized int getNextTicketNumber() {
        return ticket++;
    }
}
```

2. 인스턴스의 개수가 3개로 한정돼있는 클래스 Triple을 만들어라

```java 
public class Triple {
    private int id;
    private static Triple[] triples = {
        new Triple(0), new Triple(1), new Triple(2)
    };
    private Triple(int id) {
        this.id = id;
    }

    public static Triple getInstance(int id) {
        if(id < triples.length)
            return triples[id];
        else 
            return null;
    }
    public String toString() {
        return "This is instance " + String.valueOf(id);
    }
}
```

### Prototype 패턴

> 인스턴스를 새로 생성하는게 아니라 이미 생성된걸 복사

클래스 종류가 너무 많아지면 그만큼 소스 파일도 많아지고, 그러면 관리하기 어려워지니, 일일이 정의하지 말고 복사해서 쓰라는 것

#### 예제 프로그램 - 문자열 강조 프로그램

문자열에 밑줄이나, 박스를 쳐주는 프로그램

|Package|Name|Description|
|:-----:|:--:|:---------:|
|framework|Product|추상 메소드 use()와 createClone()이 정의되어 있는 인터페이스|
|framework|Manager|createClone()을 사용해서 인스턴스를 복제하는 클래스|
||Boxer|문자열에 박스를 쳐주는 클래스, Product를 구현|
||Underliner|문자열에 밑줄을 쳐주는 클래스, Product를 구현|

```java
package framework;

public interface Product extends Cloneable {
    public void use(String str);
    public Product createClone();
}
```

```java
package framework;

import java.util.HashMap;

public class Manager {
    private HashMap showcase = new HashMap();
    public void register(String name, Product proto) {
        showcase.put(name, proto);
    }

    public Product create(String protoname) {
        Product p = (Product) showcase.get(protoname);
        return p.createClone();
    }
}
```

Prototype들의 인터페이스인 Product와, 그 Prototype들을 통해 clone 생성을 관리하는 Manager 클래스이다.  
Product 인터페이스는 Clonable을 상속하는데 Cloneable은 clone() 메소드를 사용할 수 있게 해주는 (clone이 가능한 객체라는 것을 알려주는) 역할을 한다.

---

```java
import framework.*;

public class Underliner implements Product {
    private char liner;
    public Underliner(char liner) {
        this.liner = liner;
    }

    @Override
    public void use(String str) {
        int strLen = str.getBytes().length;
        StringBuilder sb = new StringBuilder();
        sb.append(str + "\n");
        for(int i = 0; i < strLen; i++) 
            sb.append(liner);
        
        System.out.println(sb.toString());
    }

    @Override
    public Product createClone() {
        Product p = null;
        try {
            p = (Product) clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return p;
    }
}
```

```java
import framework.*;

public class Boxer implements Product {
    private char decochar;
    public Boxer(char decochar) {
        this.decochar = decochar;
    }

    @Override
    public void use(String str) {
        int strLen = str.getBytes().length;
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < strLen + 2; i++) 
            sb.append(decochar);

        sb.append("\n" + decochar + str + decochar + "\n");

        for(int i = 0; i < strLen + 2; i++) 
            sb.append(decochar);
        
        System.out.println(sb.toString());
    }

    @Override
    public Product createClone() {
        Product p = null;
        try {
            p = (Product) clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return p;
    }
}
```

Underliner, Boxer는 Product의 구현체로, 실제로 clone될 Prototype이 된다.

---

```java
import framework.*;

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();
        Underliner underlinerProto = new Underliner('-');
        Boxer boxer1Proto = new Boxer('*');
        Boxer boxer2Proto = new Boxer('#');
        manager.register("underliner", underlinerProto);
        manager.register("boxer1", boxer1Proto);
        manager.register("boxer2", boxer2Proto);

        Product p1 = manager.create("underliner");
        Product p2 = manager.create("boxer1");
        Product p3 = manager.create("boxer2");

        p1.use("Hello world!");
        p2.use("Hello world!");
        p3.use("Hello world!");
    }
}
```

Product p1, p2, p3는 생성자를 통해 생성된 객체가 아니라 new Underliner(), new Boxer() 등으로 이미 생성된 Prototype을 복사한 객체이다. 


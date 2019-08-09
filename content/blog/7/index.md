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


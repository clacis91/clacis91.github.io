---
title: Java 디자인 패턴 Study - (6)
date: "2019-08-19"
---

Reference Book : [Java 언어로 배우는 디자인 패턴 입문](https://www.youngjin.com/book/book_detail.asp?prod_cd=9788931436914&seq=4628&cate_cd=1&child_cate_cd=&goPage=11&orderByCd=3), Yuki Hiroshi, 영진닷컴

---

### Composite 패턴

> 디렉토리와 파일을 동일시하여 재귀적인 구조를 만들기 위한 패턴

객체들의 관계를 트리 구조로 구성하여 부분-전체 계층을 표현하는 패턴으로, *단일 객체*와 *복합 객체(Composite)*를 동일하게 다룰 수 있다.

#### 예제 프로그램

* 보통 <b>트리 구조</b>로 되어 있는 파일 시스템을 다룰 땐 composite 패턴으로 구현되어 있다고 생각하면 된다.
* 파일 시스템에서 구조상으로는 디렉토리나 파일이나 타입에 따라 특별하게 취급되는 것이 아니라 <b>동일시</b>하게 취급된다.

```java
public abstract class Entry {
    public abstract String getName();
    public abstract int getSize();
    public Entry add(Entry entry) throws FileTreatmentException {
        throw new FileTreatmentException();
    }
    public void printList() {
        printList("");
    }
    protected abstract void printList(String prefix);
    public String toString() {
        return getName() + " (" + getSize() + ")";
    }    
} 
```
```java
import java.util.ArrayList;
import java.util.Iterator;

public class Directory extends Entry {
    private String name;
    private ArrayList dir = new ArrayList();

    public Directory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        int size = 0;
        Iterator it = dir.iterator();
        while(it.hasNext()) {
            Entry entry = (Entry) it.next();
            size += entry.getSize();
        }
        return size;
    }

    public Entry add(Entry entry) {
        dir.add(entry);
        return this;
    }

    protected void printList(String prefix) {
        System.out.println(prefix + "/" + this);
        Iterator it = dir.iterator();
        while(it.hasNext()) {
            Entry entry = (Entry) it.next();
            entry.printList(prefix + "/" + name);
        }
    }
}
```
```java
public class File extends Entry {
    private String name;
    private int size;

    public File(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    protected void printList(String prefix) {
        System.out.println(prefix + "/" + this);
    }
}
```
```java
public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Making root entries...");
            Directory rootdir = new Directory("root");
            Directory bindir = new Directory("bin");
            Directory tmpdir = new Directory("tmp");
            Directory usrdir = new Directory("usr");

            rootdir.add(bindir);
            rootdir.add(tmpdir);
            rootdir.add(usrdir);

            bindir.add(new File("vi", 10000));
            bindir.add(new File("latex", 20000));

            rootdir.printList();
        }   catch(FileTreatmentException e) {
            e.printStackTrace();
        }
    }
}
```

#### 연습문제

모든 파일을 출력하는 메소드가 아닌, 특정 Entry의 fullpath를 출력하는 메소드를 만들자

* Entry마다 parent 인스턴스를 만들어둬서, parent를 재귀적으로 타고 올라가며 StringBuffer를 구성하는 방식

```java
// Entry.java
...

public String getFullpath() {
    StringBuffer sb = new StringBuffer();
    Entry entry = this;
    while(entry != null) {
        sb.insert(0, "/" + entry.getName());
        entry = entry.parent;
    }
    return sb.toString();
}

...
```

```java
// Directory.java
...

public Entry add(Entry entry) {
    entry.parent = this;
    dir.add(entry);
    return this;
}

...
```

### Decorator 패턴

> 오브젝트에 필요한 기능을 하나씩 장식처럼 추가해가는 패턴

예시: java.io에 있는 InputStream, Reader, OutputStream, Writer  
이것들은 Reader를 예로 들면 FileReader, BufferedReader, InputStreamReader등 용도에 따라 Reader에 기능을 추가하여 구현된 클래스들이다.

* 중요한건 Decorator 패턴도 Composite 패턴처럼 내용물에 상관없이 <b>재귀적</b>으로 구현돼야 한다는 것이다. 포장 안에 또다른 포장이 있는지, 내용물이 있는지에 관계없이 동일한 동작으로 구현되는 것이 중요하다.

#### 예제 프로그램

*'Hello World'*를 꾸며주는 장식을 하나씩 추가하는 방식으로 구현하는 프로그램

|Name|Description|
|:--:|:----------|
|Display|문자열 표시용의 추상 클래스|
|StringDisplay|1행으로 구성된 문자열 표시용 클래스|
|Border|'장식'을 나타내는 클래스|
|SideBorder|좌우에 장식을 붙이는 클래스|
|FullBorder|상하좌우에 장식을 붙이는 클래스|

```java
public abstract class Display {
    public abstract int getColumns();
    public abstract int getRows();
    public abstract String getRowText(int row);

    public final void show() {
        for(int i = 0; i < getRows(); i++) {
            System.out.println(getRowText(i));
        }
    }
}
```

```java
public class StringDisplay extends Display {
    private String str;
    public StringDisplay(String str) {
        this.str = str;
    }
    public int getColumns() {
        return str.getBytes().length;
    }
    public int getRows() {
        return 1;
    }
    public String getRowText(int row) {
        if(row == 0) {
            return str;
        }
        else {
            return null;
        }
    }
}
```

```java
public abstract class Border extends Display {
    protected Display display;
    protected Border(Display display) {
        this.display = display;
    }
}
```

```java
public class SideBorder extends Border {
    private char borderChar;
    public SideBorder(Display display, char ch) {
        super(display);
        this.borderChar = ch;
    }

    public int getColumns() {
        return display.getColumns() + 2;
    }

    public int getRows() {
        return display.getRows();
    }

    public String getRowText(int row) {
        return borderChar + display.getRowText(row) + borderChar;
    }
}
```

```java
public class FullBorder extends Border {
    public FullBorder(Display display) {
        super(display);
    }

    public int getColumns() {
        return display.getColumns() + 2;
    }

    public int getRows() {
        return display.getRows() + 2;
    }

    public String getRowText(int row) {
        if(row == 0 || row == (display.getRows() + 1)) {
            return "+" + makeLine('-', display.getColumns()) + "+";
        }
        else {
            return "|" + display.getRowText(row - 1) + "|";
        }
    }

    private String makeLine(char ch, int cnt) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < cnt; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }
}
```

Display를 상속하는 Border의 구현체 안에서 다시 display 인스턴스를 통해 method를 호출하는 <U>재귀적인 구조</U>가 중요하다.

#### 연습문제

Q1. 문자열의 <U>상하에만</U> 선 장식을 붙여주는 클래스를 만들어보자

```java
public class UpDownBorder extends Border {
    public UpDownBorder(Display display) {
        super(display);
    }

    public int getColumns() {
        return display.getColumns();
    }

    ...

    public String getRowText(int row) {
        if(row == 0 || row == (display.getRows() + 1)) {
            return makeLine('=', display.getColumns());
        }
        else {
            return display.getRowText(row - 1);
        }
    }
}
```

Q2. 한줄만 출력하는 StringDisplay가 아닌 <U>여러줄을 출력하는 MultiStringDisplay 클래스</U>를 만들어보자

```java
import java.util.ArrayList;

public class MultiStringDisplay extends Display {
    private ArrayList<String> strLine;

    public MultiStringDisplay() {
        strLine = new ArrayList<String>();
    }

    public void add(String str) {
        strLine.add(str);
    }

    public int getColumns() {
        int longest = 0;
        for(String line : strLine) {
            longest = Math.max(longest, line.getBytes().length);
        }
        return longest;
    }

    public int getRows() {
        return strLine.size();
    }

    public String getRowText(int row) {
        if(row < strLine.size()) {
            return strLine.get(row);
        }
        else {
            return null;
        }
    }
}
```

```java
public class SideBorder extends Border {
    ...

    public String getRowText(int row) {
        return borderChar 
            + String.format("%-" + display.getColumns() + "s", display.getRowText(row)) 
            + borderChar;
    }

    ...
}
```
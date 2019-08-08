---
title: Java 디자인 패턴 Study - (4)
date: "2019-08-08"
---

Reference Book : [Java 언어로 배우는 디자인 패턴 입문](https://www.youngjin.com/book/book_detail.asp?prod_cd=9788931436914&seq=4628&cate_cd=1&child_cate_cd=&goPage=11&orderByCd=3), Yuki Hiroshi, 영진닷컴

---

### Builder 패턴

> 일정한 구조를 가진 인스턴스의 각 부분을 하나씩 쌓아 올린다

Builder 클래스는 추상 클래스(인터페이스)로서 실제 구현은 들어있지 않고 인스턴스의 *구조*만 정의하고, 실제 구현은 하위 클래스에서  수행한다. 

![](builder-1.png)

#### 예제 프로그램 - 문서 생성기

예제의 문서는 다음과 같은 구조를 가지고 있다

- 타이틀(title) 1개
- 문자열(string) 여러개
- 항목(item) 여러개

Builder 클래스에서 이 구조들을 정의하고 TextBuilder, HTMLBuilder라는 하위 클래스에서 그 구조의 구체적인 구현을 한다.  
어떤 Builder 클래스가 주어져도 구조대로 쌓아올리는 것은 Director라는 인스턴스가 수행한다.

---

```java
public interface Builder {
    public void makeTitle(String title);
    public void makeString(String str);
    public void makeItem(String[] item);
    public void close();
}
```

```java
public class Director {
    private Builder builder;
    public Director(Builder builder) {
        this.builder = builder;
    }

    public void construct() {
        builder.makeTitle("Greeting");
        builder.makeString("아침/낮에");
        builder.makeItem(new String[] {
            "안녕하세요",
            "굿모닝"
        });
        builder.makeString("밤에");
        builder.makeItem(new String[]{
            "안녕하세요",
            "굿나잇"
        });
        builder.close();
    }
}
```

TextBuilder와 HTMLBuilder는 Builder의 구현체이다. Builder에서 정의한 메소드들을 구현하고, 그것들을 Director 인스턴스의 construct() 메소드 안에서 사용한다.

```java
public class TextBuilder implements Builder {
    private StringBuffer buf = new StringBuffer();

    @Override
    public void makeTitle(String title) {
        buf.append("=================\n");
        buf.append("[" + title + "]\n\n");
    }

    @Override
    public void makeString(String str) {
        buf.append("* " + str + "\n\n");
    }

    @Override
    public void makeItem(String[] items) {
        for(String item : items) {
            buf.append("  - " + item + "\n");
        }
        buf.append("\n");
    }

    @Override
    public void close() {
        buf.append("=================\n");
    }

    public String getResult() {
        return buf.toString();
    }
}
```

```java
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class HTMLBuilder implements Builder {
    private String filename;
    private PrintWriter printWriter;

    @Override
    public void makeTitle(String title) {
        filename = title + ".html";
        try {
            printWriter = new PrintWriter(new FileWriter(filename));
        } catch(IOException e) {
            e.printStackTrace();
        }
        printWriter.println("<html><head><title>" + title + "</title></head><body>");
        printWriter.println("<h1>" + title + "</h1>");
    }

    @Override
    public void makeString(String str) {
        printWriter.println("<p>" + str + "</p>");
    }

    @Override
    public void makeItem(String[] items) {
        printWriter.println("<ul>");
        for(String item : items) {
            printWriter.println("<li>" + item + "</li>");
        }
        printWriter.println("</ul>");
    }

    @Override
    public void close() {
        printWriter.println("</body></html>");
        printWriter.close();
    }

    public String getResult() {
        return filename;
    }
}
```

---

```java
public class Main {
    public static void main(String[] args) {
        if(args.length != 1) {
            usage();
            System.exit(0);
        }

        if(args[0].equals("plain")) {
            TextBuilder textBuilder = new TextBuilder();
            Director director = new Director(textBuilder);
            director.construct();
            String result = textBuilder.getResult();
            System.out.println(result);
        }
        else if(args[0].equals("html")) {
            HTMLBuilder htmlBuilder = new HTMLBuilder();
            Director director = new Director(htmlBuilder);
            director.construct();
            String resultFilename = htmlBuilder.getResult();
            System.out.println(resultFilename + " 생성");
        }
        else {
            usage();
            System.exit(0);
        }
    }

    public static void usage() {
        System.out.println("Usage : java Main plain 일반 텍스트 문서 작성");
        System.out.println("Usage : java Main html HTML 파일 문서 작성");
    }
}
```

Main에서는 원하는 형식의 문서를 argument로 전달하면 그에 맞는 builder 인스턴스를 생성하여 Director에게 넘겨준다. Director는 그 builder가 어떤 구현체인지는 전혀 신경쓰지 않고 정의된대로 construct만 수행할 뿐이다.

### Abstract Factory 패턴

> 추상적인 공장에서 추상적인 부품으로 추상적인 제품을 만든다

#### 예제 프로그램 - 계층구조 HTML 만들기

![](abstract-factory-1.png)

|Package|Name|Description|
|:-----:|:--:|:----------|
|factory|Factory|추상적인 공장을 나타내는 클래스|
|factory|Link|추상적인 부품 : HTML link를 나타내는 클래스|
|factory|Tray|추상적인 부품 : Link, Item을 모은 클래스|
|factory|Item|Link와 Tray를 한번에 취급하기 위한 클래스|
|factory|Page|추상적인 제품 : HTML page를 나타내는 클래스|
|listfactory|ListFactory|구체적인 공장을 나타내는 클래스|
|listfactory|ListLink|구체적인 부품 : HTML link를 나타내는 클래스|
|listfactory|ListTray|구체적인 부품 : Link, Item을 모은 클래스|
|listfactory|ListPage|구체적인 제품 : HTML page를 나타내는 클래스|

```java
package factory;

public abstract class Factory {
    public static Factory getFactory(String classname) {
        Factory factory = null;
        try {
            factory = (Factory) Class.forName(classname).newInstance();
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return factory;
    }
    public abstract Link createLink(String caption, String url);
    public abstract Tray createTray(String caption);
    public abstract Page createPage(String title, String author);
}
```

```java
package factory;

public abstract class Item {
    protected String caption;
    public Item(String caption) {
        this.caption = caption;
    }
    public abstract String makeHTML();
}
```

```java
package factory;

public abstract class Link extends Item {
    protected String url;

    public Link(String caption, String url) {
        super(caption);
        this.url = url;
    }
}
```
```java
package factory;

import java.util.ArrayList;

public abstract class Tray extends Item {
    protected ArrayList tray = new ArrayList();

    public Tray(String caption) {
        super(caption);
    }

    public void add(Item item) {
        tray.add(item);
    }
}
```

```java
package factory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public abstract class Page {
    protected String title;
    protected String author;
    protected ArrayList content = new ArrayList();
    public Page(String title, String author) {
        this.title = title;
        this.author = author;
    }
    public void add(Item item) {
        content.add(item);
    }
    public void output() {
        try {
            String filename = title + ".html";
            Writer writer = new FileWriter(filename);
            writer.write(makeHTML());
            writer.close();
            System.out.println(filename + "을 작성");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    public abstract String makeHTML();
}
```
---

```java
package listfactory;

import factory.*;

public class ListFactory extends Factory {
    public Link createLink(String caption, String url) {
        return new ListLink(caption, url);
    }

    public Tray createTray(String caption) {
        return new ListTray(caption);
    }

    public Page createPage(String title, String author) {
        return new ListPage(title, author);
    }
}
```

```java
package listfactory;
import factory.*;

public class ListLink extends Link {
    public ListLink(String caption, String url) {
        super(caption, url);
    }

    public String makeHTML() {
        return "    <li><a href=\"" + url + "\">" + caption + "</a></li>\n";
    }
}
```

```java
package tablefactory;

import java.util.Iterator;
import factory.*;

public class TableTray extends Tray {
    public TableTray(String caption) {
        super(caption);
    }

    public String makeHTML() {
        StringBuffer buf = new StringBuffer();
        buf.append("<td>\n");
        buf.append("<table width=\"100%\" border=\"1\">\n");
        buf.append("<tr>\n");
        buf.append("<td colspan=\"" + tray.size() + "\"><b>" + caption + "</b></td>\n");
        buf.append("</tr>\n");
        buf.append("<tr>\n");
        Iterator it = tray.iterator();
        while(it.hasNext()) {
            Item item = (Item) it.next();
            buf.append(item.makeHTML());
        }
        buf.append("</tr></table>\n");
        buf.append("</td>\n");
        return buf.toString();
    }
}
```

```java
package listfactory;

import java.util.Iterator;

import factory.*;

public class ListPage extends Page {
    public ListPage(String title, String author) {
        super(title, author);
    }

    public String makeHTML() {
        StringBuffer buf = new StringBuffer();
        buf.append("<html><head><title>" + title + "</title></head>\n");
        buf.append("<body>\n");
        buf.append("<h1>" + title + "</h1>\n");
        buf.append("<ul>\n");
        Iterator it = content.iterator();
        while(it.hasNext()) {
            Item item = (Item) it.next();
            buf.append(item.makeHTML());
        }
        buf.append("</ul>\n");
        buf.append("<hr><address>" + author + "</address>");
        buf.append("</body></html>\n");
        return buf.toString();
    }
}
```

---

```java
import factory.*;

public class Main {
    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Usage java Main class.name.of.ConcreteFactory");
            System.out.println("Ex1 : java Main listfactory.ListFactory");
            System.out.println("Ex2 : java Main tablefactory.TableFactory");
            System.exit(0);
        }

        Factory factory = Factory.getFactory(args[0]);

        Link naver = factory.createLink("네이버", "naver.com");
        Link google = factory.createLink("구글", "google.com");

        Link facebook = factory.createLink("페이스북", "facebook.com");
        Link instagram = factory.createLink("인스타그램", "instagram.com");

        Tray traySearch = factory.createTray("검색");
        traySearch.add(naver);
        traySearch.add(google);

        Tray traySns = factory.createTray("SNS");
        traySns.add(facebook);
        traySns.add(instagram);

        Page page = factory.createPage("LinkPage", "하이");
        page.add(traySearch);
        page.add(traySns);
        page.output();

    }
}
```

(* 내생각) Builder와 Adaptor등 여러 패턴을 합쳐놓은 느낌이다

---

## Image Reference

https://zetawiki.com/wiki/Java_%EC%96%B8%EC%96%B4%EB%A1%9C_%EB%B0%B0%EC%9A%B0%EB%8A%94_%EB%94%94%EC%9E%90%EC%9D%B8_%ED%8C%A8%ED%84%B4_%EC%9E%85%EB%AC%B8
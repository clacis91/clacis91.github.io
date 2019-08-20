---
title: Java 디자인 패턴 Study - (7)
date: "2019-08-20"
---

Reference Book : [Java 언어로 배우는 디자인 패턴 입문](https://www.youngjin.com/book/book_detail.asp?prod_cd=9788931436914&seq=4628&cate_cd=1&child_cate_cd=&goPage=11&orderByCd=3), Yuki Hiroshi, 영진닷컴

---

### Visitor 패턴

데이터와 처리하는 클래스를 분리시켜서, 데이터 구조 안의 요소 하나하나마다 Visitor 돌아다니면서 처리 

#### 예제 프로그램

파일 시스템에서 디렉토리와 파일을 visitor가 돌아다니면서 파일의 정보를 표시하는 프로그램을 만들어보자

Visitor 패턴에서는 중요한 두개의 객체가 등장한다.

* Visitor

Visitor에는 파라미터 타입이 다른 두 개의 visit() 메소드를 선언한다. 하나는 File을, 하나는 Directory를 파라미터로 가진다. 파일 객체를 방문하기 위해서는 visit(File)을, 디렉토리를 객체를 방문하기 위해서는 visit(Directory)가 호출된다.

* Element

Visitor가 방문할 수 있는 객체라는 것을 표현해주기 위한 인터페이스로, 방문을 받아들인다는 의미의 accept(Visitor) 메소드가 선언 되어있다.

```java
public abstract class Visitor {
    public abstract void visit(File file);
    public abstract void visit(Directory Directory);
}
```

```java
public interface Element {
    public void accept(Visitor v);
}
```

```java
import java.util.Iterator;

public abstract class Entry implements Element {
    public abstract String getName();
    public abstract int getSize();
    public String toString() {
        return getName() + " (" + getSize() + ")";
    }
}
```

```java
import java.util.Iterator;

public abstract class Entry implements Element {
    public abstract String getName();
    public abstract int getSize();
    public String toString() {
        return getName() + " (" + getSize() + ")";
    }
}
```

```java
import java.util.Iterator;
import java.util.ArrayList;

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
    public Iterator iterator() {
        return dir.iterator();
    }
    public void accept(Visitor v) {
        v.visit(this);
    }
}
```

```java
import java.util.Iterator;

public class ListVisitor extends Visitor {
    private String curDir = "";
    public void visit(File file) {
        System.out.println(curDir + "/" + file);
    }
    public void visit(Directory directory) {
        System.out.println(curDir + "/" + directory);
        String tmpDir = curDir;
        curDir = curDir + "/" + directory.getName();
        Iterator it = directory.iterator();
        while(it.hasNext()) {
            Entry entry = (Entry) it.next();
            entry.accept(this);
        }
        curDir = tmpDir;
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
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

        rootdir.accept(new ListVisitor());
    }
}
```

##### Double dispatch

* visit()과 accept()는 서로가 서로를 호출하는 재귀적인 구조로 되어있고
* element.visit(visitor) / visitor.accept(element) 와 같이 정확히 반대되는 구조를 띄고 있다  
-> 이러한 구조를 Double dispatch 라고 한다.

#### 연습문제

Q1. 파일 시스템을 돌아다니며 지정된 확장자의 파일을 모으는 FileFindVisitor 클래스를 만들어보자.

```java
import java.util.ArrayList;
import java.util.Iterator;

public class FileFindVisitor extends Visitor {
    private ArrayList<File> targetFiles = new ArrayList<>();
    private String targetExtension;

    public FileFindVisitor(String targetExtension) {
        this.targetExtension = targetExtension;
    }
    public Iterator iterator() {
        return targetFiles.iterator();
    }

    public void visit(File file) {
        if(file.getName().endsWith(targetExtension) ) {
            targetFiles.add(file);
        }
    }
    public void visit(Directory directory) {
        Iterator it = directory.iterator();
        while(it.hasNext()) {
            Entry entry = (Entry) it.next();
            entry.accept(this);
        }
    }
}
```

Q2. 예제의 Directory에서 getSize()는 사실 '처리'와 '구조'가 분리되어 있지 않아서 바람직하지 않은 구현이다. SizeVisitor 클래스를 도입해서 분리해보자.

```java 
import java.util.Iterator;

public class SizeVisitor extends Visitor {
    private int size = 0;
    public void visit(File file) {
        size += file.getSize();
    }
    public void visit(Directory directory) {
        Iterator it = directory.iterator();
        while(it.hasNext()) {
            Entry entry = (Entry) it.next();
            entry.accept(this);
        }
    }
    public int getSize() {
        return size;
    }
}
```

```java
...
public int getSize() {
    SizeVisitor v = new SizeVisitor();
    v.visit(this);
    return v.getSize();
}
...
```

---

### Chain of Responsibility 패턴

> 담당 공무원 찾기

복수의 연결된 객체를 연결해두고, 목적으로 하는 객체를 찾을때까지 연결된 객체들을 이동해가는 패턴

#### 예제 프로그램

여러 유형의 트러블을 정의해두고, 담당 객체가 그것을 처리하는 형태의 프로그램

|Name|Description|
|:--:|:----------|
|Trouble|발생한 트러블을 나타내는 클래스|
|Support|트러블을 해결하는 추상 클래스|
|NoSupport|트러블을 해결하는 클래스 - '처리하지 않음'|
|LimitSupport|트러블을 해결하는 클래스 - '지정된 번호 미만의 트러블'|
|OddSupport|트러블을 해결하는 클래스 - '홀수 번호 트러블'|
|EvenSupport|트러블을 해결하는 클래스 - '짝수 번호 트러블'|
|SpecialSupport|트러블을 해결하는 클래스 - '특정 번호의 트러블'|

```java
public class Trouble {
    private int num;
    public Trouble(int num) {
        this.num = num;
    }
    public int getNum() {
        return num;
    }
    public String toString() {
        return "[Trouble " + String.valueOf(num) + "]";
    }
}
```

```java
public abstract class Support {
    private String name;
    private Support next;
    public Support(String name) {
        this.name = name;
    }

    public Support setNext(Support next) {
        this.next = next;
        return next;
    }

    public final void support(Trouble trouble) {
        if(resolve(trouble)) {
            done(trouble);
        }
        else if(next != null) {
            next.support(trouble);
        }
        else {
            fail(trouble);
        }
    }

    protected abstract boolean resolve(Trouble trouble);
    protected void done(Trouble trouble) {
        System.out.println(trouble + " is resolved by " + name);
    }
    protected void fail(Trouble trouble) {
        System.out.println(trouble + " cannot be resolved");
    }
}
```

```java
public class NoSupport extends Support {
    public NoSupport(String name) {
        super(name);
    }

    protected boolean resolve(Trouble trouble) {
        return false;
    }
}
```

```java
public class LimitSupport extends Support {
    private int limit;
    public LimitSupport(String name, int limit) {
        super(name);
        this.limit = limit;
    }

    protected boolean resolve(Trouble trouble) {
        return (trouble.getNum() < limit) ? true : false;
    }
}
```

```java
public class SpecialSupport extends Support {
    private int num;
    public SpecialSupport(String name, int num) {
        super(name);
        this.num = num;
    }

    protected boolean resolve(Trouble trouble) {
        return (trouble.getNum() == num) ? true : false;
    }
}
```

```java
public class OddSupport extends Support {
    public OddSupport(String name) {
        super(name);
    }

    protected boolean resolve(Trouble trouble) {
        return (trouble.getNum() % 2 != 0) ? true : false;
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        Support s1 = new NoSupport("S1");
        Support s2 = new LimitSupport("LIMIT", 100);
        Support s3 = new SpecialSupport("SPECIAL", 429);
        Support s4 = new OddSupport("ODD");
        Support s5 = new LimitSupport("LIMIT", 300);

        s1.setNext(s2).setNext(s3).setNext(s4).setNext(s5);
        
        for(int i = 0; i < 500; i+=33) {
            s1.support(new Trouble(i));
        }
    }
}
```

근데 .setNext() 이렇게 처리할 객체를 쭉 이어놓는게 현실성이 있는 패턴이긴한가??  
- cocoa framework 같은데서 *event 처리를 어느 레이어에서 수행할지* 결정하기 위해서 이런 방식이 적용된다고는 함

---

### Facade 패턴

> 많은 클래스가 얽히고 설키는 구조에서 클래스간의 상호 작용을 위한 창구를 만드는 패턴

내부 구조는 신경쓰지 않고 단순한 인터페이스를 두고 상호작용한다. API controller 같은 느낌인듯?

#### 예제 프로그램

|Name|Controller|
|:--:|:---------|
|Database|메일 주소에서 사용자 이름을 얻는 클래스|
|HtmlWriter|HTML 파일을 작성하는 클래스|
|PageMaker|메일 주소에서 사용자의 웹 페이지를 작성하는 클래스|

```java
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Database {
    private Database(){} // new 로 인스턴스 생성하는 것을 방지

    public static Properties getPropertiess(String dbName) {
        String filename = dbName + ".txt";
        Properties prop = new Properties();

        try {
            prop.load(new FileInputStream(filename));
        } catch(IOException e) {
            e.printStackTrace();
        }
        return prop;
    }
}
```

```java
import java.io.IOException;
import java.io.Writer;

public class Htmlwriter {
    private Writer writer;
    public Htmlwriter(Writer writer) {
        this.writer = writer;
    }

    public void title(String title) throws IOException {
        writer.write(("<html>"));
        writer.write(("<head>"));
        writer.write(("<title>" + title + "</title>"));
        writer.write(("</head>"));
        writer.write(("<body>\n"));
        writer.write(("<h1>" + title + "</h1>"));
    }

    public void paragraph(String msg) throws IOException {
        writer.write(("<p>" + msg + "</p>\n"));
    }

    public void link(String href, String caption) throws IOException {
        paragraph("<a href=\"" + href + "\">" + caption + "</a>\n");
    }

    public void mailto(String mailaddr, String username) throws IOException {
        link("mailto:" + mailaddr, username);
    }

    public void close() throws IOException {
        writer.write("</body>");
        writer.write("</html>\n");
        writer.close();
    }
}
```

```java
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class PageMaker {
    private PageMaker() {}

    public static void makeWelcomePage(String mailaddr, String filename) {
        try {
            Properties mailprop = Database.getPropertiess("maildata");
            String username = mailprop.getProperty(mailaddr);
            Htmlwriter writer = new Htmlwriter(new FileWriter(filename));
            writer.title("Welcome to " + username + "'s page!");
            writer.paragraph("Please mail to me");
            writer.mailto(mailaddr, username);
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        PageMaker.makeWelcomePage("test@example.com", "welcome.html");
    }
}
```

클래스를 사용하는 Main의 입장에서는 PageMaker만 알면 나머지 내부의 DB나 html 설계를 도와주는 클래스의 내용에 대해 전혀 알 필요가 없다. 

#### 연습 문제

Q. maildata.txt 안의 모든 메일 리스트의 링크를 보여주는 페이지를 작성하는 makeLinkPage 메소드를 만들어보자

```java
public static void makeLinkPage(String filename) {
    try {
        Properties mailprop = Database.getPropertiess("maildata");
        Htmlwriter writer = new Htmlwriter(new FileWriter(filename));

        writer.title("Link page");

        Iterator it = mailprop.keys().asIterator();

        while(it.hasNext()) {
            String mailaddr = (String) it.next();
            writer.mailto(mailaddr, mailprop.get(mailaddr).toString());                
        }
        
        writer.close();
    } catch(IOException e) {
        e.printStackTrace();
    }
}
```

---

### Mediator 패턴

> 개인간의 직접적인 대화 말고, 중개인을 통한 대화만 허용하는 패턴

객체들은 서로 대화하지 않고 중개인에게만 보고하며, 중개인은 보고된 정보를 토대로 *대국적으로* 지시를 내려서 동작시킨다.
<span style="color:white">개발을 대국적으로 하십시오</span>

#### 예제 프로그램

![](mediator-1.png)

|Name|Description|
|:--:|:----------|
|Mediator|중개인 인터페이스(API)|
|Colleague|회원 인터페이스(API)|
|ColleagueButton|Colleague 중 버튼을 구현하는 클래스|
|ColleagueTextField|Colleague 중 텍스트 박스를 구현하는 클래스|
|ColleagueRadiobox|Colleague 중 라디오 박스를 구현하는 클래스|
|LoginFrame|Mediator 인터페이스를 구현하는, 로그인 창을 나타내는 클래스|

```java
public interface Mediator {
    public void createColleagues();
    public void colleagueChanged();
}
```

```java
public interface Colleague {
    public void setMediator(Mediator mediator);
    public void setColleagueEnabled(boolean enabled);
} 
```

```java
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ColleagueRadiobox extends Checkbox implements Colleague, ItemListener {
    private Mediator mediator;

    public ColleagueRadiobox(String label, boolean state, CheckboxGroup group) {
        super(label, state, group);
    }
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }
    public void setColleagueEnabled(boolean enabled) {
        setEnabled(enabled);
    }
    public void itemStateChanged(ItemEvent e) {
        mediator.colleagueChanged();
    }
}
```

```java
import java.awt.TextField;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

public class ColleagueTextField extends TextField implements Colleague, TextListener {
    private Mediator mediator;
    public ColleagueTextField(String text, int columns) {
        super(text, columns);
    }
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }
    public void setColleagueEnabled(boolean enabled) {
        setEnabled(enabled);
    }
    public void textValueChanged(TextEvent e) {
        mediator.colleagueChanged();
    }
}
```

```java
import java.awt.Button;

public class ColleagueButton extends Button implements Colleague {
    private Mediator mediator;
    public ColleagueButton(String caption) {
        super(caption);
    }
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }
    public void setColleagueEnabled(boolean enabled) {
        setEnabled(enabled);
    }
}
```

```java
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Label;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends Frame implements ActionListener, Mediator {
    private ColleagueButton buttonOk;
    private ColleagueButton buttonCancel;

    private ColleagueRadiobox radioGuest;
    private ColleagueRadiobox radioLogin;

    private ColleagueTextField textId;
    private ColleagueTextField textPw;

    public LoginFrame(String title) {
        super(title);
        setBackground(Color.lightGray);
        setLayout(new GridLayout(4, 2));
        createColleagues();
        add(radioGuest);
        add(radioLogin);
        add(new Label("Username: "));
        add(textId);
        add(new Label("Password: "));
        add(textPw);
        add(buttonOk);
        add(buttonCancel);
        colleagueChanged();
        pack();
        show();
    }

    public void createColleagues() {
        CheckboxGroup g = new CheckboxGroup();
        radioGuest = new ColleagueRadiobox("Guest", true, g);
        radioLogin = new ColleagueRadiobox("Login", false, g); 

        textId = new ColleagueTextField("", 10);
        textPw = new ColleagueTextField("", 10);
        textPw.setEchoChar('*');

        buttonOk = new ColleagueButton("OK");
        buttonCancel = new ColleagueButton("Cancel");

        radioGuest.setMediator(this);
        radioLogin.setMediator(this);
        textId.setMediator(this);
        textPw.setMediator(this);
        buttonOk.setMediator(this);
        buttonCancel.setMediator(this);

        radioGuest.addItemListener(radioGuest);
        radioLogin.addItemListener(radioLogin);
        textId.addTextListener(textId);
        textPw.addTextListener(textPw);
        buttonOk.addActionListener(this);
        buttonCancel.addActionListener(this);
    }

    public void colleagueChanged() {
        if(radioGuest.getState()) {
            textId.setColleagueEnabled(false);
            textPw.setColleagueEnabled(false);
            buttonOk.setColleagueEnabled(true);
        }
        else {
            textId.setColleagueEnabled(true);
            userpassChanged();
        }
    }

    private void userpassChanged() {
        if(textId.getText().length() > 0) {
            textPw.setColleagueEnabled(true);
            if(textPw.getText().length() > 0) {
                buttonOk.setColleagueEnabled(true);
            }
            else {
                buttonOk.setColleagueEnabled(false);
            }
        }
        else {
            textPw.setColleagueEnabled(false);
            buttonOk.setColleagueEnabled(false);
        }
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println(e);
        System.exit(0);
    }
}
```

Mediator 를 구현하는 LoginFrame이 중요하다. Colleague 객체 각각에 this로 Mediator를 등록하고(setMediator), event가 발생하면 mediator에게 이벤트가 발생했다는 신호(colleagueChanged)만 보내주고 자신들은 신경쓰지 않는다. mediator는 신호를 받으면 colleague들의 상태를 체크하여 알맞는 조치를 취한다.

```java
public class Main {
    public static void main(String[] args) {
        new LoginFrame("Mediator sample");
    }
}
```

---

## Image Reference

https://en.wikipedia.org/wiki/Mediator_pattern
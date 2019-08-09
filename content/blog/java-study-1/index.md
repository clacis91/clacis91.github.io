---
title: Java Study (1)
date: "2019-07-19"
---

# 시작

Reference Book : [자바를 다루는 기술 (2014)](https://www.gilbut.co.kr/book/view?bookcode=BN000854), 김병부, 길벗

## Fundamental

안다고 생각하지만 모르고 있는 것들

---

### JVM

> Write once, run everywhere

기존의 언어의 경우 OS에 맞는 Libray / API 등을 가져와 쓰거나 그 특성에 맞게 개발해야하는 불편함이 존재했다.  
JVM은 OS와 Java 프로그램의 매개체 역할을 하고, Java Application은 JVM 위에서 동작하도록 설계되어 있다. 각각에 맞는 JVM이 개발되어 있으니 개발자가 OS 환경까지 신경쓰지 말자는 것.

* JVM 자체도 하나의 프로세스이기 때문에 리소스를 차지해서 하드웨어 성능이 떨어지는 경우 성능 이슈 발생 가능

* System 명령어를 실행하기 위해서는 JNI (Java Native Interface)를 한 번 더 거쳐야하기 때문에 성능 이슈 발생 가능

#### JVM의 구조

![](./image1.png)

##### 클래스 파일

개발자가 만든 '.java' 파일이 컴파일 과정을 거쳐 '.class' 파일로 변환됨. 이 클래스 파일들은 JVM에서 실행 가능하다.

##### 클래스 로더 서브 시스템

실행할 클래스 파일을 JVM 메모리에 올려주는 일을 담당한다.  
```
1. 실행할 클래스 파일의 구조(메소드, 변수 등)를 먼저 분석한다.
2. 분석된 결과를 바탕으로 데이터를 적합한 메모리 영역에 올린다
3. 각자의 영역에 있던 데이터는 클래스의 실행에 맞춰 힙 메모리로 복사된다
```

* 파일 분석 과정에서 <u>Reflection 기법</u>이 사용된다.  
* 클래스는 동적으로 로딩 가능하다. 프로그램 실행 초기에 로딩하는 방법 (Load Time Dynamic Loading) 실행 중간에 로딩하는 방법 (Runtime Dynamic Loading)이 존재.

##### 실행 데이터 영역 (Runtime Data Area)

데이터가 실제로 저장되는 영역. 각각의 영역은 데이터의 목적과 종류에 따라 분리되어 있다

1. 메소드 영역

클래스의 메타 데이터가 저장

2. 스택 영역

실제로 실행된 메소드와 거기에 포함된 변수나 데이터들이 저장

3. 힙 영역

생성된 객체가 저장되는 영역.

4. 레지스터 영역

JVM이 수행할 PC register 영역

5. 네이티브 메소드 영역

JNI 메소드들이 저장되는 영역


> 힙 영역은 크게 Young / Old / Permanent로 나뉜다  
> 각 영역에 따라 Garbage Collector가 실행되는 빈도가 달라진다


6. Young Generation 영역

새롭게 생성된 객체들이 저장된다. GC가 자주 실행됨

7. Old 영역

Young에서 계속해서 사용되는 객체들이 Old로 이동하게 된다. GC 수행 횟수가 Young에 비해 적음

8. Permanent 영역

---

* Wrapper Class?

  * int가 아닌 Integer, long이 아닌 Long등 기본 데이터형과 비슷하게 생긴 type들

  * 기본 데이터형의 정보나 관련된 메소드를 사용할 수 있게 제공되는 클래스

  * ex) 시스템에서 제공하는 int형의 최대값을 구하고 싶을 때 : 
  
  ```
  (x) int.MAX_VALUE (기본형에 메소드가??) 
  (o) Integer.MAX_VALUE (객체로 존재하기 때문에 메소드 활용 가능)
  ```

---

### Java Class Loader

JVM에 로딩된 클래스만이 객체로 사용될 수 있다.  
클래스 로더는 클래스가 요청될 때 파일로부터 읽어 메모리로 로딩하는 역할을 수행한다.

![](./image2.gif)

자바의 클래스 로딩은 로딩(loading) / 링킹(linking) / 초기화(initializing) 세 단계로 나뉜다.  

로딩 - 클래스 파일을  byte 코드로 읽어 메모리로 가져오는 과정  
링킹 - byte 코드가 자바 규칙을 따르는지 검증(verifying)하고, 클래스에 정의된 메소드 등을 위한 메모리 구조를 준비(preparing)하고, 참조하는 다른 클래스들을 로딩(resolving)한다  
초기화 - super 클래스 및 static 필드들을 초기화한다

#### 동적 클래스 로딩

##### 로드타임 동적 로딩

```java
public class Hello {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}
```

*Hello* 클래스를 로딩하기 위해서는 먼저 이 클래스가 참조하고 있는 Object, String, System 클래스가 먼저 로딩되어야 한다. 이와같이 한 클래스의 로드 타임에 다른 필요한 클래스까지 로딩하는 것을 로드타임 동적 로딩이라 한다. 

##### 런타임 동적 로딩

런타임 동적 로딩은 컴파일 시기에는 전혀 알 수 없는 클래스를 런타임에 로드한다.

```java
public class RuntimeLoading {
    public static void main(String[] args) {
        try {
            Class<?> cls = Class.forName(args[0]);
            Object obj = cls.newInstance();
            PrintInterface printer = (PrintInterface) obj;
            printer.print();
        }
        ...
```

RuntimeLoading 예제에서는 printer 객체 생성에 사용할 클래스를 매개변수로 받아서 로드한다. *Class.forName* 메소드로 매개변수에 맞는 class의 정보를 로드(Reflection)하고 (cls), *newInstance* 메소드로 인스턴스화하고 (obj), 필요한 타입으로 캐스팅한다.  
매개변수로 어떤 클래스가 요청될지 모르기 때문에 클래스는 런타임 도중에 로드될 수 밖에 없다.

#### 클래스 로더 계층 구조

자바의 클래스 로더는 동적 로딩 매커니즘을 위한 계층 구조를 이루고 있다.

![](./image3.gif)

##### Bootstrap Class Loader

JVM이 실행되면서 동작하는 클래스로더로 $JAVA_HOME/jre/lib에 있는 JVM 실행을 위해 필요한 기본적인 라이브러리를 로드한다. 

##### Extension Class Loader

Bootstrap 로딩 후 $JAVA_HOME/jre/lib/ext에 있는 기본적인 라이브러리들을 로드한다. 

##### System Class Loader

classpath에 정의되어 있는 라이브러리들을 로드한다.

##### User-Defined Class Loader

사용자가 구현한 클래스로더가 동작한다.

---

### 리플렉션 (Reflection)

> Reflection = 반영 -> 객체는 클래스를 '반영'한다

![](./image4.png)  
(가장 익숙한 리플렉션의 예시)

IDE는 어떻게 print 객체가 갖고 있는 메소드, 변수등의 정보를 보여줄 수 있을까?  
이것은 print 객체를 통해 PrintInterface의 정보를 확인했기 때문이다.

리플렉션 기법을 사용하면 JVM에 인스턴스된 객체를 통해 객체의 원래 클래스 정보를 알 수 있다.  
객체의 클래스 정보를 아는 것은 *동적인 프로그래밍*을 위한 중요한 요소이다.

- <b>리플렉션을 통해 얻을 수 있는 정보들</b>  
  - 클래스 이름  
  - 클래스 제어자 (public, private, protected)  
  - 패키지 정보  
  - 부모 클래스  
  - 생성자  
  - 메소드  
  - 변수  
  - Annotation

#### 클래스의 정보를 위한 클래스

<b>java.lang.Class</b>

클래스 정보를 담고 있는, 자바 클래스를 추상화한 클래스  
= *이름이 Class인 클래스*

##### Class 객체 생성 방법

<b> 1. getClass() </b>
```java
String str = new String();
Class cls1 = str.getClass();
```
<b> 2. 대상 클래스 이용 </b>
```java
Class cls2 = String.class;
```

<b> 3. 클래스 이름 이용 </b>
```java
Class cls3 = Class.forName("java.lang.String");
```

(JVM 특성상 cls1, cls2, cls3는 모두 동일한 객체를 참조한다)

<b>java.lang.reflect</b>

Class의 정보를 단순히 알아내는 것은 큰 의미가 없다. 리플렉션을 위한 클래스들을 통해 Class 정보를 기반으로 동적으로 인스턴스할 수 있다.

##### Constructor 클래스

특정 클래스의 생성자 정보를 알아낸 후, 알아낸 생성자 정보를 통해 객체를 생성하는 것이 가능

- Constructor 객체를 받아오는 방법

```java
import java.lang.reflect.Constructor;

Class<String> cls = String.class;
// getConstructor : 모든 생성자를 다 받아오는 메소드
Constructor<String> constructor1 = cls.getConstructor(String.class);
// getDeclaredConstructor : 명시적으로 선언된 생성자만 받아오는 메소드
Constructor<String> constructor2 = cls.getDeclaredConstructor(String.class);
// 여러 타입을 매개변수로 사용하는 생성자를 받아오는 메소드
Constructor<String> constructor3 = cls.getConstructor(byte[].class, Integer.TYPE);
// 여러 타입을 매개변수로 사용하는 생성자를 받아오는 메소드 사용법2
Class<?>[] paramClasses = new Class[] {
    byte[].class, Integer.TYPE, Integer.TYPE
}
Constructor<String> constructor4 = cls.getConstructor(paramClasses);
```

- 받아온 Constructor 객체를 통해 동적으로 객체를 인스턴스하는 방법

```java
Constructor<String> paramCons = cls.getConstructor(String.class);
// newInstance() 메소드에 매개변수로 넣기위한 배열
Object[] params = new Object[] {
    new String("Reflection")
}
String str = paramCons.newInstance(params);
```

##### Method 클래스

특정 클래스가 갖고 있는 메소드 정보를 알아낸 후, Method 객체를 생성.  
생성된 Method 객체의 invoke() 메소드를 사용하여 메소드 실행

```java
import java.lang.reflect.Method;

Class<String> cls = String.class;
Method m = cls.getMethod("replace", char.class, char.class);
Object obj = m.invoke(str, 'l', 'x');
```

##### Field(변수) 클래스

특정 클래스가 갖고 있는 멤버변수 정보를 알아낸 후, Field 객체를 생성.  
생성된 Field 객체의 get() 메소드를 사용하여 특정 객체의 변수 참조

```java
import java.lang.reflect.Field;

SomeClass some = new SomeClass();
Class<SomeClass>cls = SomeClass.class;
Field f = cls.getField("someField");
Object obj = f.get(some);
```
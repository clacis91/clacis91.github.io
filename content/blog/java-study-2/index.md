---
title: Java Study (2)
date: "2019-07-24"
---

Reference Book : [자바를 다루는 기술 (2014)](https://www.gilbut.co.kr/book/view?bookcode=BN000854), 김병부, 길벗

---

## Object Oriented

TBW

## Collection

TBW

## Exception

TBW

## Generic

> 클래스 내부에서 사용할 타입을 외부에서 지정하는 기법

ArrayList<<b>String</b>> strList = new ArrayList<<b>String</b>>();
ArrayList<<b>Integer</b>> intList = new ArrayList<<b>Integer</b>>();

이런식으로 ArrayList *내부에서 쓰일 타입을 외부에서 지정*해주는 개념  
<b>제네릭이 없었다면?</b> - StringArrayList, IntegerArrayList와 같이 타입마다 구현이 필요했을 것이다.

### Wildcard와 다이아몬드 지시자

```java
ArrayList<String> arrayList = new ArrayList<String>();  
Hashtable<String, String> hashTable = new Hashtable<String, String>();
```

위 클래스들의 Java API 문서에는

Class ArrayList<<b>E</b>>  
Class Hashtable<<b>K</b>, <b>V</b>>

와 같이 <>(다이아몬드) 안에 특정 타입이 아닌 E, K, V 등의 알파벳으로 설명되어있다. 이것을 *와일드카드 문자*라고 한다.

제네릭은 클래스 내부에서 사용할 클래스를 외부에서 정해주는 것이라고 했는데, 뭐가 들어올진 몰라도 일단 구현은 해야하니 와일드카드 문자로 대체해서 써놓는 것.

보통 클래스의 성격에 따라 앞글자를 따서 와일드카드 문자를 사용한다  
<b>E</b> - 'E'lement  
<b>K</b> - 'K'ey  
<b>V</b> - 'V'alue  
<b>T</b> - 'T'ype  
<b>N</b> - 'N'umber  
<b>?</b> - ?는 모든 클래스를 의미한다. 단, 클래스 선언부에는 사용할 수 없고, 뒤에 언급할 <b>확장 문법</b>에서만 사용할 수 있다.

#### 주의사항

1. 와일드카드 중복 사용은 불가  
public class ValueMapper<T, T> 같이  와일드 카드를 중복해서 사용하는 것은 JVM이 매개변수 T를 구분하지 못하기 때문에 불가능.  
public class ValueMapper<K, V> 같이 사용해야함.

2. 클래스 선언부에서 사용한 문자만 클래스 내부에서 사용 가능

```java
public class ValueMapper<T> {
    private T t;
    public ValueMapper(T t) {...}

    public void setValue(T t) {
        this.t = t;
    }

    public T getValue() {
        return t;
    }
}
````

```java
ValueMapper<String> sMapper = new ValueMapper<String>();
ValueMapper<Integer> iMapper = new ValueMapper<Integer>();

sMapper.setValue("Hello");
iMapper.setValue(7);
```

선언할 때는 와일드카드 문자로 선언해놓고, 클래스 인스턴스 시 사용할 클래스를 지정

### 제네릭 확장 문법

외부에서 사용할 클래스를 지정한다고 해도 지나친 자유를 주면 예외처리에 혼란을 줄 수 있다. 이를 방지하기 위해 사용될 클래스 범위를 제한할 수 있는 *extends* 기능이 존재한다.

(*extends 외에 super 개념으로도 확장가능)

```java
1. public class MyCase<T extends Number> {}
2. public class MyBox<T extends Comparable> {}
3. public void setValue(Collection<? extends T> collection) {}
```

제네릭에서 사용되는 extends는 상속의 extends와 인터페이스의 implements를 모두 포함한다.

1번 구문의 경우 java.lang.Number 를 상속받는 클래스만 제네릭으로 사용가능하다.

2번 구문은 Comparable 인터페이스가 구현된 클래스만 제네릭으로 사용가능하다.

3번 구문은 <? extends <b>T</b>>와 같이 제한할 타입 또한 와일드카드 문자를 사용할 수 있다는 것을 보여주는 에제이다. 말하자면 T가 뭔지는 모르겠지만 T 를 상속받는 클래스를 사용하겠다는 뜻.


## Threading

TBW

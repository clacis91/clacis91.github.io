---
title: Clean-code와 Refactoring
date: "2019-09-05"
---

# Reference

[읽기 좋은 코드가 좋은 코드다](http://www.hanbit.co.kr/store/books/look.php?p_code=B3602722207)

[유지보수하기 어렵게 코딩하는 방법](http://cfile9.uf.tistory.com/attach/175A3A4850DAB8AC12CCB7)

---

# Clean Code

* 과거의 내가 짠 코드를 보고 민망해봐야..
* 나쁜 코드를 짜두면 나중에 유지보수의 cost가 더 높아짐
* 깨진 유리창 효과
  * 잘못된 코드를 발견하면 고치는건 발견한 사람의 책임이다
* Clean 코드는 잘 쓴 문장처럼 술술 읽혀야한다

## Rule

대부분은 작명과 관련된 내용

### Naming Rule

* Data, Info 등 너무 general한 변수명을 만들지 말고 좀더 명확한
* 변수는 꼭 명사로, 메소드는 동사로
* 카멜코드 말고 _ 쓰는 틀...없제?
* 쓸데없이 주석 달지 말고 변수명만 봐도 잘 이해되게 지어라 (변수명 길어지는걸 무서워하지 말고)
* DB는 약어로 되어 있어도 코드에서는 괜한 약어 쓰지마라
* DeviceList (x), Devices (o)
* tmp/sum/num - 조심
* 측정치 - 특히 time 같은거 단위 좀 써줘라 (30)이 30sec인지 30ms인지 누가 아냐
  * startTime (x) startMilliSec (o)
* 일관성 있는 어휘 사용
  * 같은 단어도 retrieve / get / select 등 여러 용어로 쓸 수 있는데, 일관되게 써야된다
  * send -> deliver, announce, distribut, broadcast... 동사를 좀더 명확하게 써달라는거
* for 문에서도 i, j 쓰지말고 무슨 index인지 변수명으로 표현해달라
* 멤버변수 앞에 m을 붙인다거나, static 변수 앞에 s를 붙이는거 하지마라 (틀..)
* condition은 최대한 긍정의 의미로 사용해라
  * '~하지 않으면 ~해라' (x) '~하면 ~해라' (o)

### Method

* 역할을 잘 표현해야한다
  * generateId면 ID만 생성하고 말아야지, 어디다 저장하고 그런 동작이 들어가면 generateIdAndStore임
* return 은 가능하면 앞에서 빨리
  * parameter validation 체크 같은거 해서 그냥 위에서 리턴해버려라
* 삼항연산자 자제 (쓸거면 진짜 간단한거에만 써라)
* 조건문에서 변수가 앞으로 오게 해라
  * "KIM".equals(name) vs name.equals("KIM")
  * 앞에꺼는 NullPointerException이 x, 뒤어꺼는 NullPointerException이 o
    * 그래도 뒤에꺼로 하고 Exception check를 해라
* 조건문 안에서 다른 메소드 호출하지 마라
* isDone, 이런 식으로 조건 flag 쓰는거 지양하고 그냥 continue, break를 써라
* 람다 써라
  * 가독성이 높아짐
  * 익숙치 않아도 람다좀 써봐라
* logger Debugging 레벨 체크하고 쓰면 성능 많이 높아짐

### 주석

> 주석 달 시간에 코드 고쳐라

* 정규식에는 주석 달아라
  * 주석 없으면 정규식 직접 해석하라는 소리임
* compareTo 정도는 달아주자, 뭐가 -1이고 뭐가 1인지
* depth가 깊어졌을 때 
```java
} // end of while 
```
이 정도 주석은 excuse
* "// 나중에 여기 수정!!!!!!!!!!!"  <- 이런거 하지마
* sysout <- never
* 이력 관리 주석 - 이거 하지마, Commit 메시지를 봐라

```java
/**
 * since : 2014. xxx
 * author : xx
 */
```

* 주석 처리된 코드
  * 히스토리를 모르는 사람이 보니 뭔가 의미가 있어보인다.......
  * 그냥 지워버려라
* RFC spec 정도로 코드로 설명이 안될 내용을 표현해줄거 아니면 주석 쓰지 마라

## 표준

> 위 내용 모두 자신의 프로젝트의 표준에 반하는 내용이 있다면 다 무시해라

프로젝트 표준 >>>>> 일반적인 Coding Convention Rule >>>>> 개인취향

---

# Refactoring

> 앞에서 못했으면 나중에라도..

리팩토링은 고도화가 아니다. 기능도 성능도 바뀌는게 아니다.  

* 맨날 틀리는걸 또 틀리면 리팩토링을 진지하게 고민
* 안보이던 버그가 보이기도 함
* 코드 리뷰를 하면 좋음

---

# Misc.

* Exception 은 자기가 만들어서 써라
  * RuntimeException 이런 상위 Exception 던지지 마라

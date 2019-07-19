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

#### JVM의 구조

1. 클래스 파일

2. 클래스 로더 서브 시스템

3. 실행 데이터 영역

4. 메소드 영역

5. 스택 영역

6. 힙 영역

7. 레지스터 영역

8. 네이티브 메소드 영역

9. Young Generation 영역

10. Old 영역

11. Permanent 영역
---
layout: post
title:  "K-1"
categories: [exam]
---

 1. ***자바에서 public, protected, private 키워드가 있는데 아무것도 안 쓸 경우 default로 적용되는 범위는?***

    private과 protected 사이의 범위로 적용된다. 
    **private**는 자신의 class  내부에서만 사용 가능하고 **protected**는 상속된 subclass에서, 혹은 **동일한 패키지** 내에서 사용 가능하다. 
    
    default 레벨의 instance, 혹은 method는 subclass에서는 접근이 불가능하지만 동일한 패키지에서는 접근이 가능하다.

	|Modifier|Class|Package|Subclass|World|
|:--------|:--------:|:--------:|:-------:|--------:|
|public|Y|Y|Y|Y|
|protected|Y|Y|Y|N|
|no modifier|Y|Y|N|N|
|private|Y|N|N|N|


 
 2. ***Static typing과 dynamic typing의 차이점은? C++/JAVA은 어떤 typing을 사용하는가?***
 
	Static typing은 자료의 type이 **compile-time**에 정해지는 방식이고 dynamic typing은 **run-time**에 정해지는 방식이다. Dynamic typing은 문법적으로 문제가 없다면 상황에 맞는 type이 정해진다. python이나 javascript 같은 언어가 이에 해당한다. C++/JAVA 등의 static typing에선 type을 지정해주지 않으면 그 자체로 문법적인 에러가 된다.
	
 3. ***C++에서 서로 다른 타입의 오브젝트를 가리키는 포인터를 사용할 수 있나?***
	
	상속관계에 따라 가능할 수 있을 듯 하다. 가령 A의 subclass B의 object pointer는 A의 object를 가리킬 수 있다.
	
	문제의 의도가 상속관계가 아닌 오브젝트를 뜻하는 것이라면 사용이 불가능할 것이라 본다
	
 4. ***파라미터 패싱 방식에는 eager evaluation 방식과 lazy evaluation 방식이 있다. 두 방식의 차이점을 비교 설명하세요. call-by-value와 call-by-name 파라미터 패싱 방식은 각각 어느 방식에 속하는지 구분하세요.***

	eager evaluation은 선언 즉시 계산하고, lazy evaluation은 필요할 때 계산한다.
	'10/0'을 파라미터로 넘기는 경우를 생각해보면 eager 방식에선 에러지만 lazy 방식에선 해당 변수가 사용되지만 않는다면 계산이 이루어지지 않기 때문에 에러가 발생하지 않는다.
	
	Call-by-name = lazy evaluation / Call-by-value = eager evaluation


5번 이후로는 compiler와 parsing에 대한 내용인듯 한데 배운적이 없다... 
  
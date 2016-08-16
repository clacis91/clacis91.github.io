---
layout: post
title:  "K-5"
categories: [exam]
---

 1. ***Data inconsistency란 무엇인가?***
 
	*Data Inconsistency / Data Redundancy
 	
	Data Redundancy 부터 설명을 하면 데이터의 중복성을 뜻한다. 같은 정보를 나타내는 데이터가 db에 여러개 존재하고 있으면 이는 저장공간 낭비를 초래하며, 데이터 수정 시 여러곳을 동시에 수정해야하는 문제가 발생한다. 만약 이 때 일부만 수정이 이루어지는 경우, 내용이 일치해야하는 데이터들의 consistency(일관성)이 깨졌다고 한다. 이 현상을 Data Inconsistency라 한다.
 
 1. ***Data independence란?***
 
	기존의 데이터(혹은 schema)를 수정해도 다른 relation에 영향을 주지 않아야된다는 성질을 얘기한다.
 
 1. ***Data normalization이 무엇인가? 왜 필요한가?***
 
	단순하게 말하면 DB에 저장되는 데이터의 중복(redundancy)을 최소화하고, 종속성(dependency)을 최소화 시키기 위해 최적화된 DB를 설계하는 작업이라고 할 수 있다. 
 
	데이터 중복이 최소화돼야한다는 것은 당연한 얘기이며, 종속성을 줄이는 것도 중요한 문제이다. 가령, A교수가 맡고있던 B강의가 폐강되었다고 생각해보자. 만약 정규화가 돼있지 않은 DB가 있다면 B강의 정보를 삭제하기 위해 해당 tuple을 삭제했는데 덩달아 교수정보까지 삭제되는 일이 벌어질 수 있다.
 
	위와 같은 예시를 비롯한 문제들을 방지하기 위해 사전에 Data normalization을 통해 DB schema를 잘 설계해두는 것이 필요하다. (대규모 시스템에서는 성능을 고려하여 일부러 normalization을 하지 않는 경우도 있다고 한다.) 
 
	이론상으로 normalization 에는 1~6단계가 있다고 하는데 보통 3단계까지 이루어지면 normalize됐다고 한다.
 
	* 1NF : 하나의 PK를 기준으로 여러값을 가지는 attribute는 있을 수 없다.
 
	* 2NF : 모든 attribute는 PK에 종속적이어야 한다. (PK와 연관없는 attribute는 나눠라)
 
	* 3NF : 다른 attribute에 종속적인 것은 분리되어야 한다. (PK가 아닌 attribute간에 table 구성이 가능하면 나눠라) 
 
 1. ***Inner Join과 Outer Join의 차이점은? Outer Join은 언제 사용하는가?***
 
	Inner join : 특정 조건을 달아(ON 명령어) Table과 Table을 join한다. 양쪽 table에서 모든 조건에 맞는 tuple들만 선택되어 결합된다.
 
	Outer join : 조건을 지정하지만, 한쪽 혹은 양쪽 table의 data는 조건없이 무조건 다 결합된다. 예를들어 table1에 id 1234에 해당하는 항목이 있고 table2에는 id 1234에 해당하는 항목이 없을때 inner join의 경우 table join 시 id 1234에 대한 tuple이 생성되지 않는다. 하지만 outer join에서는 id 1234 tuple이 생기는 대신 table2에서 갖는 attribute에 대한 정보가 NULL로 표시된다.
 
	outer join은 일반적으로는 잘 사용되지는 않지만 전체 데이터에 대한 집계가 필요할때 사용되지 않을까 싶다.. 
 
 1. ***DB와 file의 차이점은?***

	사실 DB로 할 수 있는건 file로도 다 처리할 수 있긴하다. 속도면으로 보면 DB보다 훨씬 빠르고, 구조가 복잡하지 않다면 구현도 오히려 더 간단할 수 있다.
 
	단 data independence, inconsistency, integrity 등의 요소를 일일이 신경써야 하는 것이 이슈일 것이다. 또한 보안성이나 생산성적인 측면에서도 DB가 제공해주는 이점을 사용할 수 없는 것이 단점이다.
	
	
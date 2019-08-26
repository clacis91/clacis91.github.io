---
title: Spring Study - (1)
date: "2019-08-26"
---

Reference Book : [토비의 스프링 3.1](http://acornpub.co.kr/book/toby-spring3-1-vol2), 이일민, 에이콘 출판사

---

## 오브젝트와 의존관계

스프링의 핵심은 '자바는 객체지향 언어'라는 점이다. 객체지향적으로 프로그램을 만들기 위해 스프링이 가장 관심을 두는 대상은 <b>객체</b>가 된다.

스프링은 객체지향 설계나 구현에 특정 방법을 강요하지 않지만, 어떻게 객체를 효과적으로 설계, 구현, 사용할 것인지 기준을 마련해준다.

### 초난감 DAO

사용자 정보를 JDBC API를 통해 DB에 저장하고 조회할 수 있는 간단한 DAO를 만들어보자

> JDBC (Java DataBase Connectivity) API : 자바에서 어느 DB든지 쉽게 사용할 수 있게 만들어진 API  
> Vendor에 따라 첫 세팅만 잘 해주면 이후 사용은 JDBC의 형식만 따르면 된다

> DAO (Data Access Object) : DB에 접근하는 기능을 수행하는 목적만 가진 객체

일단은 User VO

```java
public class User {
	String id;
	String name;
	String password;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
}
```

VO에 대응하는 DB table도 하나 생성 (PostgreSQL 사용)

```sql
CREATE TABLE USERS (
  id VARCHAR(10) PRIMARY KEY,
  name VARCHAR(20) NOT NULL,
  password VARCHAR(20) NOT NULL
);
```

User 정보를 DB에 넣고 관리할 수 있는 DAO 클래스를 만들어보자. 일단은 getter와 setter만.

> JDBC를 이용하는 작업의 일반적인 순서는 다음과 같다.
> 1. DB 연결을 위한 *Connection*을 가져온다
> 2. SQL을 담은 *Statement*를 만들고 실행
> 3. 조회의 경우 쿼리의 실행 결과를 
> 4. *ResultSet*으로 받아 VO에 저장
> 5. JDBC API가 만들어낸 Exception을 처리

```java
public class UserDAO {
	public void add(User user) throws ClassNotFoundException, SQLException {
		Class.forName("org.postgresql.Driver");
		Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "wjnam", "1111");
		
		PreparedStatement ps = c.prepareStatement(
				"INSERT INTO USERS(id,name,password) VALUES (?, ?, ?)"
		);
		ps.setString(1,  user.getId());
		ps.setString(2,  user.getName());
		ps.setString(3,  user.getPassword());
		
		ps.executeUpdate();
		
		ps.close();
		c.close();
	}
	
	public User get(String id) throws ClassNotFoundException, SQLException {
		Class.forName("org.postgresql.Driver");
		Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "wjnam", "1111");
		
		PreparedStatement ps = c.prepareStatement(
				"SELECT * FROM USERS WHERE id=?"
		);
		ps.setString(1, id);
		
		ResultSet rs = ps.executeQuery();
		rs.next();
		User user = new User();
		user.setId(rs.getString("id"));
		user.setName(rs.getString("name"));
		user.setPassword(rs.getString("password"));
		
		rs.close();
		ps.close();
		c.close();
		
		return user;
	}
}
```

위 DAO를 테스트해보기 위한 Main 테스트 코드 작성

```java
public class ApplicationMain {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		UserDAO dao = new UserDAO();
		
		User user = new User();
		user.setId("testId");
		user.setName("testName");
		user.setPassword("testPW");
		
		dao.add(user);
		
		System.out.println(user.getId() + "등록 성공");
		
		User user2 = dao.get(user.getId());
		System.out.println(user2.getName());
		System.out.println(user2.getPassword());
		System.out.println(user2.getId() + "조회 성공");
    }
}
```

```
#실행결과
testId등록 성공
testName
testPW
testId조회 성공
```

일단 원하는대로 동작하는 것은 확인했지만, 위와 같은 형태의 DAO는 잘못된 요소를 고루 갖춘 코드다.  
일단 무엇이 잘못됐는지를 먼저 파악해봐야한다. 

#### 관심사의 분리

패턴이 됐던지 프레임워크가 됐던지, 객체지향을 공부하면서 항상 강조되는 점은 *분리와 확장*을 고려한 설계가 이뤄져야 한다는 것이다. 당장 위의 DAO 코드에서 PostgreSQL을 MySQL로 이전하려면 get(), add() 메소드 내용을 수정해야한다. 지금이야 메소드가 2개 밖에 없지만 쿼리 수백개에 대한 메소드가 있었다면 그 수백개의 메소드 내용을 모두 수정해야 했을 것이다.

변화가 일어나면 그 변화에 관심이 집중돼야한다. 우리가 할 일은 <U>한가지 관심이 한 군데에 집중되게</U> 하는 것이다. 즉 관심이 같은 것끼리는 모으고, 관심이 다른 것은 분리시켜야 한다. *(Separation of Concerns)*

add() 메소드를 다시 한번 봐보자. 메소드 하나에서 적어도 세 가지 관심 사항을 발견할 수 있다.

> 1. DB connection을 어떻게 맺을지에 대한 관심
> 2. SQL 쿼리 실행과 그 결과를 어떻게 다룰지에 대한 관심
> 3. Statement와 Connection을 닫아서 리소스를 시스템에 돌려주는 것에 대한 관심

2,3번이야 메소드마다 조금씩 다르겠지만 1번의 경우 모든 메소드에서 동일한 중복 코드로 구현되어 있다. 우선적으로 이런 중복 코드를 처리해야 한다.

```java
public void add(User user) throws ClassNotFoundException, SQLException {
    Connection c = getConnection();
    ...
}

public User get(String id) throws ClassNotFoundException, SQLException {
    Connection c = getConnection();
    ...
}

private Connection getConnection() throws ClassNotFoundException, SQLException {
    Class.forName("org.postgresql.Driver");
    Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "wjnam", "1111");
    return c;
}
```

더 나아가서 UserDAO를 추상클래스로 만들어, 직접 수정하지 않고 상속해서 사용하게 하면 사용자들은 DB connection과 관계된 메소드만 설정하는 것만으로 DAO 클래스를 사용할 수 있게 된다.

```java
public abstract class UserDAO {
    public void add(User user) throws ClassNotFoundException, SQLException {
        ...
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        ...
    }

    public abstract Connection getConnection() throws ClassNotFoundException, SQLException;
}

public class NUserDAO extends UserDAO {
	public Connection getConnection() throws ClassNotFoundException, SQLException {
		...
	}
}
```

이런식으로 새로운 DB를 사용하는 환경에서도 UserDAO를 상속받고 getConnection() 메소드에서 DB 설정만 환경에 맞게 만들어주면 된다. ([Template 패턴, Factory method패턴](../4)이 적용됨)

---

그런데 항상 상속을 사용하는건 유의해야 한다. 자바에서 상속은 다중상속이 되지 않으며, 여전히 상하위 클래스 간에 밀접한 관계를 가진다는 단점을 가지기 때문에 분리에는 효과적일 수 있어도 확장에 어려움이 생길 수 있다.

UserDAO를 상속하는 클래스를 만들면서 *(1) Data Access 로직*과 *(2) DB 연결* 이라는 두 개의 관심사를 분리했지만 상속이라는 형태를 사용했기 때문에 여전히 긴밀하게 구현되어 있다.  
이것을 화끈하게 분리시켜버릴 예정이다. DB 연결과 관련된 기능은 DAO가 아닌 별도의 클래스로 분리시켜버리는 것이다.

```java
public class SimpleConnectionMaker {
	public Connection makeNewConnection() throws ClassNotFoundException, SQLException {
		Class.forName("org.postgresql.Driver");
		Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "tobi", "1234");
		return c;
	}
}
```

```java
public class UserDAO {
    private SimpleConnectionMaker simpleConnectionMaker;
        public UserDAO() {
            simpleConnectionMaker = new SimpleConnectionMaker();
        }
        
        public void add(User user) throws ClassNotFoundException, SQLException {
            Connection c = simpleConnectionMaker.makeNewConnection();
            ...
```

하지만 이런 방식에서는 만약 connection을 해주는 메소드명이 변경이라도 된다면(makeNewConnection -> openConnection) DAO 안의 모든 메소드를 바꿔줘야 하는 상황이 펼쳐진다. 또한 DAO 쪽에서 SimpleConnectionMaker의 내용을 모두 따라야 한다는 점이다. 만약 UserDAO를 상속 받는 쪽에서 약간의 다른 동작을 구현하기 위해서는 UserDAO 자체를 수정하는 일이 생겨버린다. 

이러한 일을 해결하기 위해 <b>인터페이스</b>로 <U>*추상적인 연결관계*</U>만 만들어 주는 방식이 권장된다.

```java
public interface ConnectionMaker {
	public Connection makeConnection() throws ClassNotFoundException, SQLException; 
}
```

```java
public class NConnectionMaker implements ConnectionMaker {
	public Connection makeConnection() throws ClassNotFoundException, SQLException {
		Class.forName("org.postgresql.Driver");
		Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "wjnam", "1111");
		return c;
	}
}
```

```java
public class UserDAO {
	private ConnectionMaker connectionMaker;
	
	public UserDAO() {
		connectionMaker = new NConnectionMaker();
	}
	
	public void add(User user) throws ClassNotFoundException, SQLException {
        Connection c = connectionMaker.makeConnection();
        ...
```

하지만 여전히 *connectionMaker = new NConnectionMaker();* 와 같이 구체적인 클래스 명으로 객체를 초기화 시켜주지 않으면 안된다. 이래서는 DAO와 DB Connector가 여전히 종속적이다. 

이러한 종속성을 깨기 위해 두 객체 사이에는 *런타임 의존관계*를 맺어줘야 한다. 코드에는 없던 의존관계를 런타임 단계에서 만들어 주는 것이다. 

```java
public class UserDAO {
	private ConnectionMaker connectionMaker;
	
	public UserDAO(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }
    ...
```

위와 같이 UserDAO의 생성자를 수정하면 UserDAO는 connectionMaker의 정확한 구현체는 알 필요가 없고, 대신 런타임에 외부(현재는 main 메소드)에서 *'주입'* 해준 구현체를 사용하기만 하면 된다.

```java
public static void main(String[] args) throws ClassNotFoundException, SQLException {
    ConnectionMaker connectionMaker = new NConnectionMaker(); 
    UserDAO dao = new UserDAO(connectionMaker);
    ...
```

### 원칙과 패턴

여태까지 진행된 리팩토링은 비록 간단한 과정이었지만, 객체지향의 여러 패턴과 원칙을 지키며 이뤄졌다.

> (참고) 객체지향 설계 원칙 (SOLID)
> * <b>S</b>RP (Single Responsibility Principle)
> * <b>O</b>CP (Open-Closed Principle)
> * <b>L</b>SP (Liskov Substitution Principle)
> * <b>I</b>SP (Interface Segregation Principle)
> * <b>D</b>IP (Dependency Inversion Principle)

* 개방 폐쇄 원칙(Open-Closed Principle)  
<U>클래스나 모듈은 *확장에는 열려 있어야 하고, 변경에는 닫혀 있어야 한다.*</U>  
위 예제에서 인터페이스를 구현하는 부분은 연결 방법이나 기능을 확장할 수 있게 열려있지만, 인터페이스를 사용하는 UserDAO는 코드 자체를 변경하는 것에 닫혀 있다.

* 높은 응집도와 낮은 결합도(High coherence / Low coupling)

*응집도*가 높다는 것은 <U>하나의 기능이 수정될 때 하나의 모듈에서 많은 변화가 발생</U>한다는 것을 뜻하고, *결합도*가 낮다는 것은 <U>하나의 기능이 수정될 때 수정될 모듈이 적다는 것</U>을 뜻한다.

### 제어의 역전 (IoC)

예제 코드에서 main method는 두개의 역할을 떠맡았다. UserDAO가 어떤 ConnectionMaker를 사용할지 결정해주는 역할과, UserDAO의 기능을 확인하는 역할이다. 이러한 역할은 다음과 같이 ConnectionMaker를 만들어주는 팩토리를 구현해서 분리시킬수 있다.

```java
public class DaoFactory {
	public UserDAO userDao() {
		ConnectionMaker connectionMaker = new NConnectionMaker(); 
		UserDAO userDao = new UserDAO(connectionMaker);
		return userDao;
	}
}
```

```java
public static void main(String[] args) throws ClassNotFoundException, SQLException {
        UserDAO dao = new DaoFactory().userDao();
        ...
```

DaoFactory에 UserDAO가 아닌 다른 DAO 생성 기능을 넣으면 어떻게 될까? 

```java
public class DaoFactory {
	public UserDAO userDao() {
		ConnectionMaker connectionMaker = new NConnectionMaker(); 
		UserDAO userDao = new UserDAO(connectionMaker);
		return userDao;
	}
	
	public AccountDAO accountDao() {
		ConnectionMaker connectionMaker = new NConnectionMaker(); 
		AccountDAO accountDao = new AccountDAO(connectionMaker);
		return userDao;
	}
	
	public MsgDAO MsgDao() {
		ConnectionMaker connectionMaker = new NConnectionMaker(); 
		MsgDAO msgDao = new MsgDAO(connectionMaker);
		return msgDao;
	}
}
```

위와 같이 될 것이다. 중복 코드가 너무 많아 보인다. 분리해내야 한다.

```java
public class DaoFactory {
	public UserDAO userDao() {
		return new UserDAO(connectionMaker());
	}
	
	public AccountDAO accountDao() {
		return new AccountDAO(connectionMaker());
	}
	
	public MsgDAO MsgDao() {
		return new MsgDAO(connectionMaker());
	}
	
	public ConnectionMaker connectionMaker() {
		return new NConnectionMaker(); 
	}
}
```

충분히 깔끔해졌다. 이제 제어의 역전(Inversion of Control)이 무엇인지 알아볼 차례다.

제어가 역전 되었다는 것은 *'자신의 동작을 자신이 아닌 외부에서 결정해준다'*는 말이 된다. main 메소드 같은 엔트리 포인트를 제외하면 모든 오브젝트는 제어 권한을 갖는 특별한 오브젝트에 의해 만들어지고 동작한다. 

예제에서 UserDAO는 DaoFactory에 의해 생성되고, 그 DaoFactory는 자신이 어떤 DAO를 생성할지 main에서 결정해줘야 한다. DaoFactory에서 ConnectionMaker를 반환하는 메소드에서도 실제로 객체를 생성하는 것은 구현체에게 맡기고 있다. 

이런식으로 이미 스프링 없이도 IoC를 적용한 객체지향 코딩은 가능하다. 스프링은 이러한 IoC원칙을 극한까지 적용시킬 수 있도록 도와주는 프레임워크다.
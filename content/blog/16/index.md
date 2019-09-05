---
title: Spring Study - (4)
date: "2019-09-05"
---

Reference Book : [토비의 스프링 3.1](http://acornpub.co.kr/book/toby-spring3-1-vol2), 이일민, 에이콘 출판사

[Spring Study - (3)](../15)

---

# 예외

자바에서 Exception은 다루기 귀찮지만 중요한 기능이다.  
그냥 출력하고 넘어가는 방법도 있고, 위로 throw만 하는 방법도 있는데 무책임한 방법이다. 

### 예외의 종류

명시적인 처리가 가능한 예외

* Error : 시스템적으로 비정상적인 상황에 발생(out of memory, thread death 등)
* Exception : 
  * Checked Exception : 해당 Exception이 발생하면 반드시 catch하거나 throw 해야함
  * Unchecked Exception : RuntimeException이 이 경우인데, 꼭 catch할 필요가 없음

### 예외처리 방법

* 예외 복구 : try-catch
* 예외처리 회피 : throws
* 예외 전환
  * 발생한 예외를 그대로 throw 해줄 경우 의미가 불분명해지는 경우
  * Checked Exception을 RuntimeException으로 포장(wrap)해서 넘겨주기


### 예외처리 전략

* 런타임 예외
  * throws Exception으로 점철된 의미없는 메소드들이 많아지면서 예외처리가 너무 번거로워짐
  * RuntimeException으로 만들어서 넘겨주도록 처리

* 어플리케이션 예외
  * 시스템이나 외부의 예외상황이 아니라 어플리케이션 로직에서 발생시키는 예외
  * try-catch로 꼭 예외 상황을 처리해줘야 한다

### UserDAO의 예외처리

UserDAO에서 JdbcTemplate을 적용하기 이전에는 DB 쿼리를 실행하는 메소드에서 SQLException을 던져줘야 했다. 하지만 JdbcTemplate을 적용하면서 예외처리가 전부 사라졌다. SQLException은 어디로 갔을까?

JdbcTemplate의 메소드들은 DB 관련 에러를 런타임 에러인 DataAccessException으로 전환하여 던져주고 있어서 사용측에서 처리 의무가 없도록 해준다. 

### Java 표준 Persistence의 예외처리

* JDBC, JPA, 하이버네이트 등 Data Access 기술에서 Exception을 어떻게 다루는지에 대한 설명
* <U>이 부분은 나중에 다시 읽어봐야할듯</U>

## 기술에 독립적인 UserDAO 만들기

지금까지 만든 UserDAO 클래스를 인터페이스와 구현으로 나눠보자. 구현체는 JDBC를 이용할 수도 있고, JPA나 하이버네이트를 이용할 수도 있다. 인터페이스는 DAO의 기능을 사용하려는 클라이언트들이 필요한 것만 추출하면 된다.

```java
public interface UserDAO {
	void add(User user);
	User get(String id);
	List<User> getAll();
	int getCount();
	void deleteAll();
}
```

```java
public class UserDAOJdbc implements UserDAO {
	...
```

```xml
<bean id="userDao" class="tobi.user.dao.UserDAOJdbc">
	<property name="dataSource" ref="dataSource" /> 
</bean>
```

동일한 Key를 가진 오브젝트를 등록시켜서 예외를 발생시키는 테스트 코드를 만들어보자.

```java
@Test(expected=DataAccessException.class)
public void duplicateKey() {
	dao.deleteAll();
	
	dao.add(user1);
	dao.add(user1);
}
```

DataAccessException이 발생하기 때문에 테스트가 통과된다. 하지만 이 Exception은 JDBC에서만 발생하기 때문에 이 테스트를 범용적으로 사용할 수는 없다. (예를 들어, 하이버네이트는 같은 상황에서 ConstaintViolationException을 발생시킴)  
만약 DAO에서 사용하는 기술에 상관없이 동일한 예외를 얻고 싶다면, 스프링의 예외 전환 클래스(SQLExceptionTranslator)를 사용해야한다.

```java
@Test
public void sqlExceptionTranslate() {
	dao.deleteAll();
	
	try {
		dao.add(user1);
		dao.add(user1);
	} catch(DuplicateKeyException ex) {
		SQLException sqlEx = (SQLException) ex.getRootCause();
		SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
		
		assertThat(set.translate(null, null, sqlEx), is(instanceOf(DuplicateKeyException.class)));
	}
}
```

# 서비스 추상화

시대가 지나고 환경과 상황에 따라 기술이 바뀌고, 그에 따라 다른 API를 사용하고, 다른 스타일의 접근 방법을 사용하고.. 머리가 아프다. 스프링을 통해 어떻게 여러 종류의 기술을 추상화하고 일관된 방법으로 사용할 수 있도록 지원하는지 배워본다.

## UserDAO 비즈니스 로직

기존의 UserDAO에는 간단한 CRUD 기능만 있었다. 여기에 간단한 비즈니스 로직을 추가해보자.

> 1. 사용자 레벨 추가 (BASIC, SILVER, GOLD)
> 2. 첫 가입시 BASIC 레벨, 이후 활동에 따라 업그레이드 된다
> 3. 50회 이상 로그인 시 SILVER
> 4. SILVER 레벨에서 30회 이상 추천 받으면 GOLD
> 5. 레벨 변경은 조건이 충족돼도 즉시 일어나는 것이 아니라, 일정한 주기를 가지고 일괄적으로 처리

### 필드 추가

* Level enum

회원 등급에 대한 정보부터 시작해보자. DB에 varchar로 "GOLD", "SILVER" 이렇게 넣는 방법도 있겠지만 굳이? 각 레벨을 코드화해서 숫자로 관리하면 보다 쉽고 가볍게 관리가 가능하다. 그렇다고 DAO에 사용될 프로퍼티도 숫자로 표현하면? 다루기 쉽기야 하겠지만 타입이 안전하지 않다는 단점이 있다. (잘못된 값 입력 등)

이런 경우엔 보통 enum을 쓴다

```java
public enum Level {
	BASIC(1), SILVER(2), GOLD(3);
	
	private final int value;
	
	Level(int value) {
		this.value = value;
	}
	
	public int intValue() {
		return value;
	}
	
	public static Level valueOf(int value) {
		switch(value) {
			case 1 : return BASIC;
			case 2 : return SILVER;
			case 3 : return GOLD;
			default : throw new AssertionError("Unknown error : " + value);
		}
	}
}
```

이러면 DB에 저장할 int 타입의 값을 갖고 있으면서도, setLevel(1000) 같은 잘못된 메소드 사용이 불가능(1000은 Level type이 아니기 때문)해지기 때문에 안전하다.

* User 필드 내용 추가

User 필드에 방금 생성한 Level 변수를 추가한다. 로그인 횟수와 추천 횟수를 표현할 login, recommend 변수(int)도 추가한다. DB의 User 테이블도 업데이트 하고, DAO, Test코드 등에 새로운 멤버와 관련된 내용을 모두 반영한다.

```java
public User(String id, String name, String password) {
	this.id = id;
	this.name = name;
	this.password = password;
	this.level = Level.BASIC;
	this.login = 1;
	this.recommend = 0;
}
```

```java
public class UserDAOJdbc implements UserDAO {
	private JdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public void add(final User user) {
		this.jdbcTemplate.update(
				"INSERT INTO USERS(id,name,password,level,login,recommend) VALUES (?, ?, ?, ?, ?, ?)",
				user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend()
		);
	}
	
	public void deleteAll() {
		this.jdbcTemplate.update(
				"DELETE FROM USERS"
		);
	}
	
	public int getCount() {
		return this.jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM USERS", 
				int.class
		);
	}
	
	public User get(String id) {
		return this.jdbcTemplate.queryForObject(
				"SELECT * FROM USERS WHERE id=?", 
				new Object[] {id}, // 파라미터로 사용될 배열
				this.userMapper
		); 
	}

	public List<User> getAll() {
		return this.jdbcTemplate.query(
				"SELECT * FROM USERS ORDER BY id",
				this.userMapper
		);
	}
	
	private RowMapper<User> userMapper = new RowMapper<User>() {
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
			user.setLevel(Level.valueOf(rs.getInt("level")));
			user.setLogin(rs.getInt("login"));
			user.setRecommend(rs.getInt("recommend"));
			return user;
		}
	};
}
```

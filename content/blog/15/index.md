---
title: Spring Study - (3)
date: "2019-09-03"
---

Reference Book : [토비의 스프링 3.1](http://acornpub.co.kr/book/toby-spring3-1-vol2), 이일민, 에이콘 출판사

[Spring Study - (2)](../14)

---

# 템플릿

개방-폐쇄 원칙OCP는 확장에는 열려있고 변경에는 닫혀 있어야 한다는 원칙이다. 이 원칙은 어떤 부분은 변경을 통해 기능이 <U>확장되려는 성질</U>이 있고, 어떤 부분은 고정되어 <U>변하지 않으려는 성질</U>이 있다는 것을 설명해준다.

템플릿이란 코드 변경이 거의 일어나지 않는 성질을 가진 부분을 독립시켜서 효과적으로 활용할 수 있도록 하는 방법이다.

## 예외상황 처리

JDBC 코드에는 반드시 지켜야할 원칙이 있다. JDBC 코드의 흐름을 따르지 않고 중간에 *예외가 발생하면 반드시 리소스를 반환*해줘야 한다. 

```java
public void deleteAll() throws SQLException {
	Connection c = dataSource.getConnection();

	PreparedStatement ps = c.prepareStatement(
			"DELETE FROM USERS"
	);
	ps.executeUpdate();

	ps.close();
	c.close();
}
```

위 코드에서 DELETE 쿼리를 실행하는 과정에서 에러가 발생하면 <U>리소스를 반환</U>하고 예외를 발생시켜야 한다. 

```java
public void deleteAll() throws SQLException {
	Connection c = null;
	PreparedStatement ps = null;
	try {
		c = dataSource.getConnection();
		ps = c.prepareStatement(
				"DELETE FROM USERS"
		);
		ps.executeUpdate();
	} catch(SQLException e) {
		throw e;
	} finally {
		if(ps != null) {
			try {
				ps.close();
			} catch(SQLException e) {}
		}
		if(c != null) {
			try {
				c.close();
			} catch(SQLException e) {}
		}
	}
}
```

getCount() 메소드에도 똑같이 예외처리를 넣어준다.

```java
public int getCount() throws SQLException {
	Connection c = null;
	PreparedStatement ps = null;
	ResultSet rs = null ;
	
	try {
		c = dataSource.getConnection();
		ps = c.prepareStatement(
				"SELECT COUNT(*) FROM USERS"
		);
		rs = ps.executeQuery();
		rs.next();
		return rs.getInt(1);
	} catch(SQLException e) {
		throw e;
	} finally {
		if(rs != null) {
			try {
				rs.close();
			} catch(SQLException e) {}
		}
		if(ps != null) {
			try {
				ps.close();
			} catch(SQLException e) {}
		}
		if(c != null) {
			try {
				c.close();
			} catch(SQLException e) {}
		}
	}
}
```

찝찝하다. 

try-catch-finally가 중복해서 나타나는데다가 코드 depth가 너무 깊어지고, SQL 관련 메소드마다 if문을 걸어주고.. 게다가 예외상황은 테스트하기도 어렵기 때문에 잘못된 것을 찾기도 어렵다.

이 문제의 핵심은 *변하지는 않지만 많은 곳에서 중복되는* <U>코드를 분리</U>해내는 작업이다. 

### 전략 패턴의 적용

단순 메소드 추출 방식이나, 템플릿 메소드(변경되는 기능 메소드를 제외하고는 상속해서 사용)는 논리적으로 맞지 않거나 확장 유연성이 떨어지는 문제가 있다.

![](strategy.png)

전략패턴은 변하지 않는 부분(Context)과 변하는 부분(Strategy A,B)을 <U>별도의 클래스로 만들고</U>, 확장할 기능은 추상화된 인터페이스(Strategy)에 위임해서 동작시키는 패턴이다.

JDBC 동작을 수행하는 메소드는 변하지 않는 맥락Context을 가지게 된다. 
> 1. DB 커넥션 가져오기
> 2. PreparedStatement를 만들어줄 외부 기능 호출
> 3. PreparedStatement 실행
> 4. 예외 발생시 throw
> 5. PreparedStatement와 Connection close

전략 패턴에서 말하는 전략Strategy는 이중에서 2번에서 말하는 '외부 기능'이 된다. 이 외부 기능을 인터페이스의 구현체로 만들어두고 사용하는 형태가 된다.

```java
public interface StatementStrategy {
	PreparedStatement makePreparedStatement(Connection c) throws SQLException;
}
```

```java
public class DeleteAllStatement implements StatementStrategy {
	@Override
	public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
		PreparedStatement ps = c.prepareStatement("DELETE FROM USERS");
		return ps;
	}

}
```

```java
public void deleteAll() throws SQLException {
	...
	try {
		c = dataSource.getConnection();
		
		StatementStrategy strategy = new DeleteAllStatement();
		ps = strategy.makePreparedStatement(c);
		
		ps.executeUpdate();
	}
	...
```

하지만 이런 식으로 구현한면 DeleteAllStatement()라는 구체적인 구현체 자체를 알고 있어야하기 때문에 단순히 PreparedStatement를 생성하는 기능을 별도에 메소드로 뺀것에 지나지 않는다. 구체적인 내용을 몰라도 되는 공통적인 코드가 하나 있어야 한다는 느낌이 온다.

![](strategy-2.png)

전략 패턴에서는 Context가 어떤 전략을 사용하게 될 것인가는 그 앞단의 Client가 결정하는게 일반적이다. *Client가 구체적인 전략 하나를 선택하고 <U>오브젝트로 만들어서 Context로 전달</U>하는 것*이다.

현재 deleteAll() 메소드에서 
```java
StatementStrategy strategy = new DeleteAllStatement();
```
이 한줄을 제외하고는 모두 Context 코드이기 때문에 별도의 메소드로 분리시켜야 한다.

```java
public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException {
	Connection c = null;
	PreparedStatement ps = null;
	try {
		c = dataSource.getConnection();
		ps = stmt.makePreparedStatement(c);
		ps.executeUpdate();
	} catch(SQLException e) {
		throw e;
	} finally {
		if(ps != null) {
			try {
				ps.close();
			} catch(SQLException e) {}
		}
		if(c != null) {
			try {
				c.close();
			} catch(SQLException e) {}
		}
	}
}
```
jdbcContextWithStatementStrategy는 구체적으로 사용할 전략에 대한 정보는 전혀 모르고, 외부에서 전달해주는 전략을 받아서 수행할 뿐이다. 이 '외부'가 여기서는 deleteAll() 메소드가 된다. DeleteAllStatement라는 하나의 전략 오브젝트를 생성해서 jdbcContextWithStatementStrategy에 전달해주기만 하면 된다.

```java
public void deleteAll() throws SQLException {
	StatementStrategy st = new DeleteAllStatement();
	jdbcContextWithStatementStrategy(st);
}
```

---

동일한 방식으로 add() 메소드에도 전략을 적용해보자. 먼저 <U>변하는 부분</U>을 전략의 구현체로 만들어야한다.

```java
public class AddStatement implements StatementStrategy {
	User user;
	
	public AddStatement(User user) {
		this.user = user;
	}

	@Override
	public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
		PreparedStatement ps = c.prepareStatement(
				"INSERT INTO USERS(id,name,password) VALUES (?, ?, ?)"
		);
		ps.setString(1,  user.getId());
		ps.setString(2,  user.getName());
		ps.setString(3,  user.getPassword());
		
		return ps;
	}

}
```

구현된 전략을 오브젝트로 만들어서 넘겨주기만 하면 된다.

```java
public void add(User user) throws SQLException {
	StatementStrategy st = new AddStatement(user);
	jdbcContextWithStatementStrategy(st);
}
```

---

충분히 깔끔하게 정리된 것 같지만 약간의 불만이 남아있다. 이런 식으로 만들다보면 전략 하나당 하나의 클래스가 발생하게 되는데, 이 때문에 파일이 너무 많아진다. 

또한 add(User user) 메소드 같은 경우 별도의 생성자를 만들어서 user 정보의 객체를 넘겨야 해서 약간의 메모리 낭비가 발생한다. 

클래스 파일이 많아지는 문제는 간단하게 로컬 클래스로 해결할 수 있다. DeleteAllStatement나 AddStatement나 어차피 DAO 내부에서만 사용된다. 그러면 DAO 내부에 로컬 클래스로 선언해두면 그만이다.

```java
public void add(User user) throws SQLException {
	class AddStatement implements StatementStrategy {
		User user;
		
		public AddStatement(User user) {
			this.user = user;
		}

		@Override
		public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
			PreparedStatement ps = c.prepareStatement(
					"INSERT INTO USERS(id,name,password) VALUES (?, ?, ?)"
			);
			ps.setString(1,  user.getId());
			ps.setString(2,  user.getName());
			ps.setString(3,  user.getPassword());
			
			return ps;
		}

	}
	
	StatementStrategy st = new AddStatement(user);
	jdbcContextWithStatementStrategy(st);
}
```

이렇게하면 또다른 장점이 생긴다. AddStatement가 어차피 add(User user) 메소드 안에 있으니 user 정보를 따로 넘겨주지 않아도 user를 알 수 있다는 점이다. 이러면 쓸데없는 생성자나 파라미터를 하나씩 줄일 수 있다.

```java
public void add(final User user) throws SQLException {
	class AddStatement implements StatementStrategy {
		@Override
		public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
			PreparedStatement ps = c.prepareStatement(
					"INSERT INTO USERS(id,name,password) VALUES (?, ?, ?)"
			);
			ps.setString(1,  user.getId());
			ps.setString(2,  user.getName());
			ps.setString(3,  user.getPassword());
			
			return ps;
		}

	}
	
	StatementStrategy st = new AddStatement();
	jdbcContextWithStatementStrategy(st);
}
```

---

더 욕심을 내서 리팩토링을 해보자면, 사실 AddStatement 클래스는 add 메소드 안에서 전략을 생성하는 일 외에는 다른 용도가 없기 때문에 별도의 이름도 필요가 없다. 이런 경우 <U>익명 클래스</U> (안드로이드 하다보면 많이 보게되는)로 만들어버리는 것도 가능하다.

```java
public void add(final User user) throws SQLException {
	StatementStrategy st = new StatementStrategy() {
		@Override
		public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
			PreparedStatement ps = c.prepareStatement(
					"INSERT INTO USERS(id,name,password) VALUES (?, ?, ?)"
			);
			ps.setString(1,  user.getId());
			ps.setString(2,  user.getName());
			ps.setString(3,  user.getPassword());
			
			return ps;
		}
	};
	jdbcContextWithStatementStrategy(st);
}
```

비슷한 이유로 전략 오브젝트도 한번만 사용되고 말 것이기 때문에 따로 변수 선언도 안해주는 수도 있다.

```java
jdbcContextWithStatementStrategy(
	new StatementStrategy() {
		@Override
		public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
			PreparedStatement ps = c.prepareStatement(
					"INSERT INTO USERS(id,name,password) VALUES (?, ?, ?)"
			);
			ps.setString(1,  user.getId());
			ps.setString(2,  user.getName());
			ps.setString(3,  user.getPassword());
			
			return ps;
		}
	}
);
```
---

jdbcContextWithStatementStrategy는 UserDAO가 아닌 다른 DAO도 사용할 수 있기 때문에 외부로 분리시켜서 사용해야한다. JdbcContext 객체를 DAO가 내부가 아닌 외부에 만들게 되면 당연히 DI를 해줘야 한다. 

```java
// JdbcContext.java
public class JdbcContext {
	private DataSource dataSource;
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		try {
			c = dataSource.getConnection();
			
			ps = stmt.makePreparedStatement(c);
			
			ps.executeUpdate();
		} catch(SQLException e) {
			throw e;
		} finally {
			if(ps != null) {
				try {
					ps.close();
				} catch(SQLException e) {}
			}
			if(c != null) {
				try {
					c.close();
				} catch(SQLException e) {}
			}
		}
	}
}
```

```java
public class UserDAO {
	private JdbcContext jdbcContext;
	private DataSource dataSource;
	
	public void setJdbcContext(JdbcContext jdbcContext) {
		this.jdbcContext = jdbcContext;
	}
	
	public void add(final User user) throws SQLException {
		this.jdbcContext.workWithStatementStrategy(

			...

		);
	}
	...
```

```xml
...
<bean id="jdbcContext" class="tobi.user.dao.JdbcContext" >
	<property name="dataSource" ref="dataSource" />
</bean>

<bean id="userDao" class="tobi.user.dao.UserDAO">
	<property name="dataSource" ref="dataSource" /> 
	<property name="jdbcContext" ref="jdbcContext" /> 
</bean>
```

* JdbcContext를 인터페이스가 아닌 구체적인 클래스 자체를 DI 해준 것에 주목

보통 스프링에서 DI를 할때는 클래스 자체를 주입하는 것이 아니라 인터페이스를 주입한다. (UserDAO의 예제에서 DataSource를 주입할때 *SimpleDriverDataSource 클래스*가 아닌 *DataSource 인터페이스*를 주입한 것처럼)

하지만 JdbcContext 주입시 별도의 인터페이스를 만들지 않고 JdbcContext 클래스 자체를 주입했다. 이럴거면 DI 말고 DAO 내부에서 선언해서 쓰는거랑 뭐가 다르냐고 할 수 있지만, 싱글톤 객체로 관리할 수 있고 JdbcContext도 다른 빈을 주입받고 있기 때문에 DI를 하는게 맞다고 한다. 

인터페이스가 없는 이유는 DAO와 JdbcContext가 아주 긴밀하게 결합되어 있기 때문이다. (JdbcContext가 다른 구현체로 바뀔 가능성이 없다는 뜻인듯) 이러한 경우는 클래스 자체를 DI해도 문제는 없지만, 대신 정말 문제가 없는 것인지 잘 판단해야 한다고.

## 템플릿과 콜백

* 콜백

실행되는 것을 목적으로 다른 메소드에 전달되는 오브젝트. 자바에서는 메소드 자체를 파라미터로 넘길 수 없기 때문에 메소드가 담긴 객체를 전달하기 때문에 functional object 라고도 한다.

클라이언트가 템플릿 오브젝트를 호출하면서 콜백 오브젝트를 전달하는 것은 메소드 레벨에서 일어나는 DI이다. JdbcContext에서는 StatementStrategy(functional object)를 넘겨주고 connection이 이뤄지면 makePreparedStatement(콜백 메소드)를 호출하는 방식으로 템플릿/콜백 패턴이 사용됐다.

* 콜백의 분리와 재사용

현재는 StatementStrategy 객체를 익명 클래스로 생성하여 사용하는 방식으로 구현되어 있다. 물론 잘못된 방식은 아니지만 depth가 깊어지면서 읽기가 불편해지기도 한다. 

```java
public void deleteAll() throws SQLException {
	this.jdbcContext.workWithStatementStrategy(
		new StatementStrategy() {
			@Override
			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
				return c.prepareStatement("DELETE FROM USERS");
			}
		}
	);
}
```

위 예제에서 익명 콜백 메소드를 생성하는 데에도 일정한 형식이 있다는 것을 발견했다. *"DELETE FROM USERS"*와 같은 쿼리가 들어가는 부분을 제외하면 모든 메소드에서 거의 동일한 형식으로 콜백 메소드를 만들고 있다. 때문에 쿼리 부분을 제외한 부분을 분리시킬 수 있다.

```java
public void deleteAll() throws SQLException {
	executeSql("DELETE FROM USERS");
}

private void executeSql(final String query) throws SQLException {
	this.jdbcContext.workWithStatementStrategy(
		new StatementStrategy() {
			@Override
			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
				return c.prepareStatement(query);
			}
		}
	);
}
```

더 수정해보면, 쿼리를 실행시키는 기능은 UserDAO만 가능한게 아니라 모든 DAO에서 가능해야하기 때문에, DAO가 아닌 JDBC쪽으로 가는것이 논리적으로 더 맞다.

```java
public void executeSql(final String query) throws SQLException {
	workWithStatementStrategy(
		new StatementStrategy() {
			@Override
			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
				return c.prepareStatement(query);
			}
		}
	);
}
```

하나의 목적(DB 관련 동작)을 위해 서로 긴밀하게 연관되어 동작하는 응집력이 강한 코드들은 한 군데에 모여 있는게 유리하다. 그러면서 구체적인 구현은 최대한 감춰두고, 외부에는 꼭 필요한 기능을 제공하는 단순한 메소드(executeSql)만 노출시켜야한다.

## 스프링에서 제공하는 템플릿/콜백 

스프링이 제공하는 JDBC 코드용 기본 템플릿은 JdbcTemplate이다. 아쉽지만 여태까지 만든 JdbcContext는 버린다..

```java
public class UserDAO {
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataSource = dataSource;
	}
	
	public void add(final User user) throws SQLException {
		this.jdbcTemplate.update(
				"INSERT INTO USERS(id,name,password) VALUES (?, ?, ?)"
				, user.getId(), user.getName(), user.getPassword()
		);
	}

	public void deleteAll() throws SQLException {
		this.jdbcTemplate.update("DELETE FROM USERS");
	}
	...
```

### queryForObject

add나 deleteAll 메소드의 경우 결과값이 필요한 쿼리를 실행하지는 않았다. 하지만 getCount() 같은 메소드를 처리하기 위해서는 결과값을 받아오는 동작이 수행돼야한다. JdbcTemplate에서 템플릿/콜백 방식으로 이를 구현하려면 query() 메소드를 호출하면 된다.

```java
public int getCount() {
	return this.jdbcTemplate.query(
		new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				return con.prepareStatement("SELECT COUNT(*) FROM USERS");
			}
		}, 
		new ResultSetExtractor<Integer>() {
			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				rs.next();
				return rs.getInt(1);
			}
		}
	);
}
```

위 메소드도 변경할 수 있는 부분과 변경되지 않을 부분이 딱 보인다. 쿼리(*"SELECT COUNT(*) FROM USERS"*)를 제외한 부분은 변경되지 않을 고정된 부분이기 때문에 분리할 수 있다. 사실 분리된 메소드가 이미 구현되어 있다.

```java
// 책에는 queryForInt라는 메소드가 소개됐는데, deprecated 정도가 아니라 아예 삭제된듯
public int getCount() {
	return this.jdbcTemplate.queryForObject("SELECT COUNT(*) FROM USERS", int.class);
}
```

get() 메소드는 여태까지 구현한 메소드들중 기능이 가장 복잡하다. 단순히 쿼리를 실행하거나 int 하나를 가져오는게 아니라, 결과를 ResultSet으로 가져와 User 오브젝트로 만들어줘야한다.

```java
public User get(String id) {
	return this.jdbcTemplate.queryForObject(
			"SELECT * FROM USERS WHERE id=?", 
			new Object[] {id}, // 파라미터로 사용될 배열
			new RowMapper<User>() {
				@Override
				public User mapRow(ResultSet rs, int rowNum) throws SQLException {
					User user = new User();
					user.setId(rs.getString("id"));
					user.setName(rs.getString("name"));
					user.setPassword(rs.getString("password"));
					return user;
				}
			}
	); 
}
```

RowMapper를 좀더 활용해보기 위해 현재 등록되어 있는 모든 사용자 정보를 갖고오는 메소드를 추가해보자.

TDD 느낌나게 테스트 코드를 먼저 작성

```java
@Test
public void getAll() {
	dao.deleteAll();
	
	dao.add(user1);
	List<User> users1 = dao.getAll();
	assertThat(dao.getCount(), is(1));
	checkSameUser(user1, users1.get(0));
	
	dao.add(user2);
	List<User> users2 = dao.getAll();
	assertThat(dao.getCount(), is(2));
	checkSameUser(user1, users2.get(0));
	checkSameUser(user2, users2.get(1));
	
	dao.add(user3);
	List<User> users3 = dao.getAll();
	assertThat(dao.getCount(), is(3));
	checkSameUser(user1, users3.get(0));
	checkSameUser(user2, users3.get(1));
	checkSameUser(user3, users3.get(2));
}

private void checkSameUser(User u1, User u2) {
	assertThat(u1.getId(), is(u2.getId()));
	assertThat(u1.getName(), is(u2.getName()));
	assertThat(u1.getPassword(), is(u2.getPassword()));
}
```

쿼리 결과가 1개일때는 queryForObject가 편하지만, 결과가 여러개라면 query() 메소드를 쓴다.

```java
public List<User> getAll() {
	return this.jdbcTemplate.query(
			"SELECT * FROM USERS ORDER BY id",
			new RowMapper<User>() {
				@Override
				public User mapRow(ResultSet rs, int rowNum) throws SQLException {
					User user = new User();
					user.setId(rs.getString("id"));
					user.setName(rs.getString("name"));
					user.setPassword(rs.getString("password"));
					return user;
				}
			}
	);
}
```

## 테스트 보완

getAll 메소드 실행 결과 등록된 유저가 없어서 리턴해줄 내용이 없다면 어떻게 동작해야 할까?  
null 반환? 빈 리스트 반환? Exception 던지기?  
이렇게 한가지 상황에도 대응할 수 있는 방법은 여러가지가 있기 때문에 정확한 기준을 가지고 테스트로 만들어 검증해둬야 한다. 특히 JdbcTemplate 같이 이미 만들어져 있는 클래스를 갖다 쓰는 경우에는 자신이 만든 클래스가 아니기 때문에 '이러한 상황에서 이 클래스는 이렇게 동작한다' 라는 것을 확실히 검증하면서 사용해야 한다.

```java
dao.deleteAll();
List<User> users0 = dao.getAll();
assertThat(users0.size(), is(0));
```

테스트 코드를 위와 같이 구현할 경우 'JdbcTemplate은 쿼리 결과가 없는 경우 빈 리스트를 반환한다'(실제로 그렇게 동작) 라는 가정을 검증할 수 있다. 만약 위 테스트가 실패한다면 null을 반환하거나 Exception을 던지거나 하는 동작에 맞게 테스트 코드를 수정해야 한다.

## 코드 정리

1. DataSource

DataSource를 사용하는 모든 코드를 JdbcTemplate을 활용하는 방식으로 수정했다. JdbcTemplate에 주입해주는 용도 말고는 사용되지 않으니 멤버에서 제거

2. 중복 제거

get() 메소드와 getAll() 메소드에서 쿼리 결과를 User 오브젝트로 변환해주는 RowMapper의 내용이 중복된다. 한번만 사용되는 익명 클래스라면 상관 없지만, 여러번 사용되면 따로 분리하는게 맞다. User 오브젝트를 만들어주는 Mapper니 UserMapper를 만들어보자.

```java
private RowMapper<User> userMapper = new RowMapper<User>() {
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		User user = new User();
		user.setId(rs.getString("id"));
		user.setName(rs.getString("name"));
		user.setPassword(rs.getString("password"));
		return user;
	}
};

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
```

---

최종 UserDAO 코드

```java
public class UserDAO {
	private JdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public void add(final User user) {
		this.jdbcTemplate.update(
				"INSERT INTO USERS(id,name,password) VALUES (?, ?, ?)",
				user.getId(), user.getName(), user.getPassword()
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
			return user;
		}
	};
}
```

---

Image Reference

https://hychul.github.io/spring/2018/04/04/tobis-spring-3.1-vol1-chapter3/
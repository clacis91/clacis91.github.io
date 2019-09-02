---
title: Spring Study - (2)
date: "2019-09-02"
---

Reference Book : [토비의 스프링 3.1](http://acornpub.co.kr/book/toby-spring3-1-vol2), 이일민, 에이콘 출판사

[Spring Study - (1)](../13)

---

## 테스트

내가 의도한대로 코드가 정확히 동작하는지를 확인하며 코드의 결함을 제거해가는 작업.

웹이나 별도의 UI를 통해 기능을 테스트해보는 방법에는 한계가 있을 수 밖에 없다. 또한 한꺼번에 너무 많은 테스트를 하려하면 그 과정이 복잡해지고, 오류의 정확한 원인을 찾기 어려워진다. 테스트는 최대한 작은 단위로 쪼개서 수행해야한다. (여기서 '관심사의 분리'라는 원리가 또 적용된다.) 

이렇게 작은 단위의 코드에 대한 테스트를 단위Unit 테스트라고 한다. 이 단위는 정해진 것은 아니다. 크게는 사용자 관리 기능 자체가 하나의 단위일 수 있고, 메소드 하나가 하나의 단위일 수 있다. 각각의 기능은 잘 동작하는데 합쳐놓으면 안되는 경우도 있기 때문에, 작은 단위로 테스트 하다가도 큰 단위의 테스트도 언젠가는 필요해진다.

UserDAO를 만들면서 한번에 모든 기능을 완벽하게 만들지 않았다. 일단 동작하게 만들고 main() 메소드를 돌려 정상동작을 확인하고, 리팩토링을 거치며 지금의 형태가 되었다. 단위 테스트를 이용하면 간단하고 빠르게 동작을 테스트해볼 수 있고, 이것이 결국 프로그램의 지속적인 개선이 가능하게 만들어준다.

### 기존 Main을 통한 방식의 문제

* 수동 확인의 번거로움  
출력된 결과가 원하는 내용인지 사람이 직접 확인하고 판단하는 과정이 필요하다.

* 실행의 번거로움  
만약 DAO가 수백개고, 그것을 확인해보기 위해 일일히 main()에서 실행해줘야 한다면? 

## Junit 테스트로 전환

1장에서 프레임워크의 기본 동작원리가 IoC라고 했다. 프레임워크는 개발자가 만든 클래스에 대한 제어 권한을 넘겨받아 주도적으로 어플리케이션의 흐름을 제어한다. 때문에 테스트 프레임워크에서 동작하는 코드는 Main()도 필요없고 오브젝트를 만들어서 실행하는 코드를 만들 필요도 없다.

### 테스트 메소드 전환

JUnit 프레임워크가 요구하는 테스트 메소드의 조건은 <U>(1) 메소드가 public으로 선언</U> <U>(2) @Test Annotation</U>을 붙여주는 것이다.

```java
public class UserDAOTest {
	@Test
	public void addAndGet() throws SQLException, ClassNotFoundException {
		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		
		UserDAO dao = context.getBean("userDao", UserDAO.class);
		
		User user = new User();
		user.setId("junit");
		user.setName("tinuj");
		user.setPassword("junitPW");
		dao.add(user);
		
		User user2 = dao.get(user.getId());
		
		assertThat(user2.getName(), is(user.getName()));
		assertThat(user2.getPassword(), is(user.getPassword()));
	}
}
```

현재 테스트 코드는 한번은 성공하지만 두번째부터는 중복 데이터를 추가하려하기 때문에 실패한다. 일관성 있는 결과를 보장하는 테스트를 위해 UserDAO에 새로운 기능을 추가하여야 한다. 데이터를 추가하기 전에 Table 내용을 모두 삭제하는 메소드와, 현재 table 레코드의 갯수를 반환해주는 메소드를 추가했다.

* deleteAll() 과 getCount() 메소드 추가

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

public int getCount() throws SQLException {
	Connection c = dataSource.getConnection();
	PreparedStatement ps = c.prepareStatement(
			"SELECT COUNT(*) FROM USERS"
	);
	
	ResultSet rs = ps.executeQuery();
	rs.next();
	int count = rs.getInt(1);
	
	rs.close();
	ps.close();
	c.close();
	return count;
}
```

* deleteAll() 과 getCount()의 테스트

```java
public class UserDAOTest {
	
	@Test
	public void addAndGet() throws SQLException, ClassNotFoundException {
		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		
		UserDAO dao = context.getBean("userDao", UserDAO.class);
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		User user = new User();
		user.setId("junit");
		user.setName("tinuj");
		user.setPassword("junitPW");

		assertThat(dao.getCount(), is(1));
		
		dao.add(user);
		
		User user2 = dao.get(user.getId());
		
		assertThat(user2.getName(), is(user.getName()));
		assertThat(user2.getPassword(), is(user.getPassword()));
	}
}
```

메소드도 테스트도 잘 동작하지만, 사실 DB를 매번 지우고 다시 쓰고 하는 동작에는 문제가 발생할 가능성도 있다. 스프링에는 DB를 테스트하기 위한 기능을 따로 제공해주고 있다. 다만 현 단계에서는 다루지 않고 당분간은 위와 같이 지우고 다시 쓰는 방식을 사용한다.

### 포괄적인 테스트

테스트가 성공한다고 다 좋은 것은 아니다. 성의 없이 테스트를 만드는 바람에 문제가 있는 코드인데도 테스트가 성공하게 만드는 건 더 위험하다.

getCount() 메소드를 위한 더 꼼꼼한 테스트를 만들어보자. 

```java
@Test
public void count() throws SQLException {
	ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
	
	UserDAO dao = context.getBean("userDao", UserDAO.class);
	User user1 = new User("U1", "U1Name", "pass1");
	User user2 = new User("U2", "U2Name", "pass2");
	User user3 = new User("U3", "U3Name", "pass3");
	
	dao.deleteAll();
	assertThat(dao.getCount(), is(0));
	dao.add(user1);
	assertThat(dao.getCount(), is(1));
	dao.add(user2);
	assertThat(dao.getCount(), is(2));
	dao.add(user3);
	assertThat(dao.getCount(), is(3));
}
```

USER Table의 레코드의 갯수의 다양한 경우에 대응하는 테스트 코드를 모두 만들고, 통과하는 것을 확인할 수 있다.

* 주의할 점은 JUnit에서 테스트 메소드가 여러개 있는 경우 <U>어떤 순서로 진행되는지 알 수 없다</U>는 점이다. 모든 테스트는 순서에 상관없이 독립적으로 동작할 수 있도록 만들어야 한다.

### 예외조건에 대한 테스트

get을 하려는데 DB가 비어있다면? 이러한 경우에 해결방법은 보통 2가지가 있다. 하나는 null 같은 것을 반환해주는 것이고, 하나는 Exception을 던지는 것이다. JUnit은 Exception 테스트를 위한 특별한 방법을 제공해준다. 

```java
@Test(expected=EmptyResultDataAccessException.class)
public void getUserFailure() throws SQLException, ClassNotFoundException {
	ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
	
	UserDAO dao = context.getBean("userDao", UserDAO.class);
	dao.deleteAll();
	dao.get("unknown");
}
```

*@Test(expected=EmptyResultDataAccessException.class)* 와 같이 원하는 Exception이 발생하는 것을 테스트 성공 케이스로 지정해 놓으면 예외 조건을 테스트할 수 있다. (대신 해당 Exception이 발생할 수 있도록 구현해주는 것을 빼놓으면 안된다)

## TDD

테스트 코드를 먼저 만들고, 그 테스트를 통과하기 위한 기능 구현 코드를 만들어라.

## 테스트 코드 개선

어플리케이션 코드 뿐만 아니라 테스트 코드도 리팩토링의 대상이 될 수 있다. 현재 테스트 코드에는 눈에 띄게 중복되는 부분을 쉽게 찾을 수 있다. 스프링 컨텍스트를 만드는 부분이다.

```java
ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
UserDAO dao = context.getBean("userDao", UserDAO.class);
```

JUnit에는 매 테스트 마다 반복되는 준비 작업을 위한 메소드를 먼저 실행시켜주는 기능이 존재한다.

```java
public class UserDAOTest {
	private UserDAO dao;
	
	@Before
	public void setUp() {
		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		this.dao = context.getBean("userDao", UserDAO.class);
	}

	...
```

### 픽스쳐(Fixture)

테스트를 수행하는데 필요한 정보나 오브젝트를 픽스쳐라고 한다. 일반적으로 픽스쳐는 여러 테스트에서 반복적으로 사용되니 @Before에서 미리 생성해두면 편하다.

```java
public class UserDAOTest {
	private UserDAO dao;
	private User user1;
	private User user2;
	private User user3;
	
	@Before
	public void setUp() {
		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		this.dao = context.getBean("userDao", UserDAO.class);
		
		user1 = new User("U1", "U1Name", "pass1");
		user2 = new User("U2", "U2Name", "pass2");
		user3 = new User("U3", "U3Name", "pass3");
	}
	...
```

## Spring Test 적용

현재 테스트 코드에서는 테스트마다 어플리케이션 컨텍스트를 새로 생성하고 있다. 컨텍스트 생성시에는 모든 싱글톤 빈 오브젝트가 초기화되는데, 단순한 빈이라면 상관 없지만 자체적으로 초기화를 한다거나 리소스를 할당하면서 많은 시간을 잡아먹는 등 문제가 발생할 여지가 존재한다. 

테스트 각각은 독립적으로 수행되어야 하는 것이 맞지만, 어플리케이션 컨텍스트는 공유되어도 문제가 되지 않는다. 이를 위해 스프링에서 제공하는 컨텍스트 테스트 지원 기능을 사용한다. 

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/applicationContext.xml")
public class UserDAOTest {
	@Autowired  // Context 자동 주입
	private ApplicationContext context;
	...
	
	@Before
	public void setUp() {
		this.dao = this.context.getBean("userDao", UserDAO.class);
		...
```

테스트 프레임워크가 실행되면서 컨텍스트 오브젝트를 생성하고, 테스트 객체에 컨텍스트를 주입해주는 일종의 DI 방식이라고 생각하면 된다.

### @Autowired

@Autowired는 DI에 사용되는 특별한 Annotation이다. 이 Annotation이 붙어있는 변수가 있으면 컨텍스트는  컨텍스트에 등록되어있는 빈 중에서 변수 타입과 일치하는 빈을 찾아서 변수에 주입해준다. 이렇게 하면 별도의 생성자나 setter 없이도 객체 주입이 가능하다. 

어플리케이션 컨텍스트는 초기화할때 자기 자신도 빈으로 등록하기 때문에 이러한 형태로 주입이 가능하다. 사실 @Autowired를 통해 컨텍스트에 등록된 빈을 주입받을 수 있으니, 컨텍스트 변수 없이도 DAO를 주입 받을 수도 있다.

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/applicationContext.xml")
public class UserDAOTest {
	@Autowired
	private UserDAO dao;
	...
```

테스트나 어플리케이션 코드 뿐만 아니라 설정 클래스에서도 Autowired를 사용할 수 있다

```java
@Configuration
public class DaoFactory {
	@Autowired
	SimpleDriverDataSource dataSource;

	...

	@Bean
	public DataSource dataSource() {
		dataSource.setDriverClass(org.postgresql.Driver.class);
		dataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
		dataSource.setUsername("tobi");
		dataSource.setPassword("1234");
		
		return dataSource;
	}
```

만약 같은 타입의 빈이 2개 이상 설정돼있다면 문제가 될 수 있다. 이 경우 먼저 <U>타입과 변수명이 같은 이름을 가지고 있는지 체크</U>해서 있으면 그 빈을 주입받고, 변수명으로도 찾을 수 없다면 예외를 발생시킨다.

### 테스트를 위한 DI

사실 현재 테스트 코드에는 무시무시한 기능이 들어가있다. deleteAll() 이라는 메소드다. 만약 실제 서비스 용 DB에 연결되어 있는 코드에서 기능 추가를 위해 테스트를 돌리다가 deleteAll이 실행된다면?? 

이러한 불상사를 막기 위해서 테스트 중에는 DAO가 사용할 DataSource 객체를 바꿔주는 방법이 있다. 

```java
@DirtiesContext
public class UserDAOTest {
	@Autowired
	private UserDAO dao;
	
	@Before
	public void setUp() {
		DataSource dataSource = new SingleConnectionDataSource(
				"jdbc:postgresql://localhost:5432/postgres", "testdb", "1111", true
		);
		dao.setDataSource(dataSource);

		...
```

위와 같이 테스트를 위한 새로운 DataSource를 강제로 주입해주는 것이다. 하지만 이 경우 컨텍스트에서 공유하는 DataSource 빈의 내용이 바뀌기 때문에 시스템 전체적으로 봤을때 원하지 않는 동작을 일으킬 수 있다. *@DirtiesContext* Annotation을 달아줄 경우 해당 클래스는 컨텍스트의 상태를 변경한다는 것을 알려주기 때문에 컨텍스트 공유를 허용하지 않고 매번 새로 생성하도록 설정된다. 

하지만 이 때문에 매번 어플리케이션 컨텍스트를 새로 생성하는 것은 찜찜하다. 

아예 테스트에서 사용될 DataSource 클래스가 정의된 테스트 전용 설정 파일을 따로 만들어 두는 방법도 있다.

```xml
<bean id="dataSource" class="org.springframework.jdbc.datasource.SimpleDriverDataSource" >
	<property name="driverClass" value="org.postgresql.Driver" />
	<property name="url" value="jdbc:postgresql://localhost:5432/testdb" />
	<property name="username" value="tobi" />
	<property name="password" value="1111" />
</bean>
```

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-applicationContext.xml")
public class UserDAOTest {
	...
```

### 컨테이너 없는 DI

또 다른 방법은 그냥 컨테이너를 만들지 않고 DAO 객체를 그냥 직접 생성해서 테스트에 사용하는 것이다.

```java
public class UserDAOTest {
	private UserDAO dao;

	@Before
	public void setUp() {
		dao = new UserDAO();
		DataSource dataSource = new SingleConnectionDataSource(
				"jdbc:postgresql://localhost:5432/postgres", "testdb", "1111", true
		);
		dao.setDataSource(dataSource);
```


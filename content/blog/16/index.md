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

### 사용자 수정 기능 추가

DAO에 사용자 정보를 수정하는 메소드 update()를 추가한다. 먼저 update() 메소드를 테스트할 테스트 코드를 먼저 작성한다.

```java
@Test
public void update() {
	dao.deleteAll();
	dao.add(user1);
	
	user1.setName("U1up");
	user1.setPassword("pass1up");
	user1.setLevel(Level.GOLD);
	user1.setLogin(1000);
	user1.setRecommend(999);
	dao.update(user1);
	
	User user1up = dao.get(user1.getId());
	checkSameUser(user1up, user1);
}
```

테스트 코드가 정상적으로 동작할 수 있도록 DAO에 update() 메소드를 추가한다. (이전에 DAO가 인터페이스 형태로 변경됐기 때문에 DAO 인터페이스와 구현체에 모두 update()를 추가해야한다.)

```java
public void update(User user) {
	this.jdbcTemplate.update(
			"UPDATE USERS SET name=?, password=?, level=?, login=?, recommend=? WHERE id=?",
			user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getId()
	);
}
```

### UserService

사용자 정보를 수정하는 단계까지 됐으니, 이제 사용자 정보를 '어떤 기준으로 업데이트 한다' 라는 비즈니스 로직을 추가해야 한다. 비즈니스 로직은 어디에 두어야할까? DAO에 두는 것은 바람직하지 않다. DAO는 단순히 데이터를 어떻게 넣고빼는지에 관한 클래스이지, 비즈니스 로직이 들어가서는 안된다. 비즈니스 로직을 담기 위한 클래스 UserService를 하나 추가한다.

UserService는 UserDAO를 주입받아 사용하는데, UserDAO의 구현에 영향을 받지 않도록 해야한다. 그리고 UserService를 위한 테스트 클래스도 하나 추가한다.

```java
public class UserService {
	UserDAO userDao;
	
	public void setUserDao(UserDAO userDao) {
		this.userDao = userDao;
	}
}
```

```xml
<bean id="userService" class="tobi.user.dao.UserService">
	<property name="userDao" ref="userDao" /> 
</bean>
```

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-applicationContext.xml")
public class UserServiceTest {
	@Autowired
	UserService userService;

	@Test
	public void bean() {
		assertThat(this.userService, is(notNullValue()));
	}
}
```

이제 사용자의 레벨을 변경하는 로직을 추가한다. 위에서 설명한 레벨 상승 로직을 구현한다.

```java
public void upgradeLevels() {
	List<User> users = userDao.getAll();
	
	for(User user : users) {
		Boolean changed = false;
		if(user.getLevel() == Level.BASIC && user.getLogin() >= 50) {
			user.setLevel(Level.SILVER);
			changed = true;
		}
		else if(user.getLevel() == Level.SILVER && user.getRecommend() >= 30) {
			user.setLevel(Level.GOLD);
			changed = true;
		}
		
		if(changed)
			userDao.update(user);
	}
}
```

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-applicationContext.xml")
public class UserServiceTest {
	List<User> users;
	
	@Autowired
	UserDAO userDao;
	@Autowired
	UserService userService;

	@Before
	public void setUp() {
		User u1 = new User("U1", "U1Name", "pass1");
		u1.setLevel(Level.BASIC);
		u1.setLogin(49);
		u1.setRecommend(0);
		User u2 = new User("U2", "U2Name", "pass2");
		u2.setLevel(Level.BASIC);
		u2.setLogin(50);
		u2.setRecommend(0);
		User u3 = new User("U3", "U3Name", "pass3");
		u3.setLevel(Level.SILVER);
		u3.setLogin(60);
		u3.setRecommend(29);
		User u4 = new User("U4", "U4Name", "pass4");
		u4.setLevel(Level.SILVER);
		u4.setLogin(60);
		u4.setRecommend(30);
		User u5 = new User("U5", "U5Name", "pass5");
		u5.setLevel(Level.GOLD);
		u5.setLogin(100);
		u5.setRecommend(100);
		users = Arrays.asList(u1,u2,u3,u4,u5);
	}
	
	@Test
	public void upgradeLevels() {
		userDao.deleteAll();
		for(User user : users) {
			userDao.add(user);
		}
		
		userService.upgradeLevels();
		
		checkLevel(users.get(0), Level.BASIC);
		checkLevel(users.get(1), Level.SILVER);
		checkLevel(users.get(2), Level.SILVER);
		checkLevel(users.get(3), Level.GOLD);
		checkLevel(users.get(4), Level.GOLD);
	}
	
	private void checkLevel(User user, Level expectedLevel) {
		User userUpdate = userDao.get(user.getId());
		assertThat(userUpdate.getLevel(), is(expectedLevel));
	}
}
```

이제 리팩토링의 시간이다.  
일단 if 분기들이 맘에 들지 않는다. 플래그 사용도 맘에 안든다. 그 외에도 매직넘버등 자잘한 문제들이 있다.

가장 먼저 추상적인 레벨에서 보면, 자주 변경될 가능성이 있는 구체적인 내용이 추상적인 흐름과 섞여 있다. 먼저 메소드를 추상적인 흐름으로만 표현해보자.

```java
public void upgradeLevels() {
	List<User> users = userDao.getAll();
	
	for(User user : users) {
		if(canUpgradeLevel(user)) {
			upgradeLevel(user)
		}
	}
}
```

안의 구체적으로 어떻게 구현돼있는지 상관하지 않으면, 논리적인 흐름은 위의 코드와 같다. (1)유저별로 (2)레벨업이 가능하면 (3)레벨업 한다. 이제 논리적인 흐름대로 구체적인 메소드를 구현해보자.

```java
private boolean canUpgradeLevel(User user) {
	Level curLevel = user.getLevel();
	switch(curLevel) {
		case BASIC : return (user.getLogin() >= 50);
		case SILVER : return (user.getRecommend() >= 30);
		case GOLD: return false;
		default :
			throw new IllegalArgumentException("Unknown level : " + curLevel);
	}
}

private void upgradeLevel(User user) {
	if(user.getLevel() == Level.BASIC) user.setLevel(Level.SILVER);
	else if(user.getLevel() == Level.SILVER) user.setLevel(Level.GOLD);
	userDao.update(user);
}
```

이정도로도 충분히 동작은 하지만 upgradeLevel() 메소드는 좀더 개선이 필요해보인다. 우선 예외처리(만약 User가 GOLD 레벨이라면 탈 로직이 없으니 예외임) 부분이 없고, 어차피 각 단계에서 다음 레벨은 정해져 있는데 굳이 Service의 if 문에서 상승될 레벨을 지정해주는 것도 불필요해 보인다. 다음 레벨이 뭔지 알아내는 정도는 Level에서 구현해도 충분하다.

```java
public enum Level {
	GOLD(3, null), SILVER(2, GOLD), BASIC(1, SILVER);
	
	private final int value;
	private final Level next;
	
	Level(int value, Level next) {
		this.value = value;
		this.next = next;
	}
	
	public Level nextLevel() {
		return this.next;
	}
	...
```

이런식으로 enum 내부에서 각 레벨의 다음 레벨을 미리 지정해놓을 수 있다. 이제 Service에서 '특정 레벨로 올려라'라고 할 필요 없이 '다음 레벨로 올려라'라는 요청만 하면 된다.  
(책에서는 User 클래스에 upgradeLevel() 메소드를 또 구현했는지 이게 맞는지 모르겠다. User는 VO같은 오브젝트로 사용중인데 거기에 로직을 넣는건 아닌거같다. 일단은 내 생각대로 Service에서 레벨업 해주는 로직 구현)

```java
private void upgradeLevel(User user) {
	Level nextLevel = user.getLevel().nextLevel();
	user.setLevel(nextLevel);
	userDao.update(user);
}
```

변경된 upgradeLevel() 메소드를 따라 테스트 클래스도 수정해야한다. 기존 테스트 클래스에서는 업그레이드된 레벨을 일일히 파라미터로 명시해줘야했지만, nextLevel을 활용하도록 바꿔야한다.

```java
@Test
public void upgradeLevels() {
	userDao.deleteAll();
	for(User user : users) {
		userDao.add(user);
	}
	
	userService.upgradeLevels();
	
	// Upgrade 대상이면 true, 아니면 false
	checkLevelUpgraded(users.get(0), false);
	checkLevelUpgraded(users.get(1), true);
	checkLevelUpgraded(users.get(2), false);
	checkLevelUpgraded(users.get(3), true);
	checkLevelUpgraded(users.get(4), false);
}

private void checkLevelUpgraded(User user, boolean upgraded) {
	User userUpdate = userDao.get(user.getId());
	
	if(upgraded)
		assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
	else 
		assertThat(userUpdate.getLevel(), is(user.getLevel()));
}
```

매직넘버도 이쯤해서 처리해두자

```java
public class UserService {
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECOMMEND_FOR_GOLD = 30;
	...
	switch(curLevel) {
		case BASIC : return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
		case SILVER : return (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
		...
```


---
title: Spring Study - (5)
date: "2019-09-17"
---

Reference Book : [토비의 스프링 3.1](http://acornpub.co.kr/book/toby-spring3-1-vol2), 이일민, 에이콘 출판사

[Spring Study - (4)](../16)

---

## AOP

AOP(Aspect Oriented Programming)는 DI/IoC와 서비스 추상화와 함께 스프링에서 제공하는 3가지 핵심 기술 중 하나이다. 스프링이 굳이 난해한 AOP를 도입한 이유를 알아보자. 

### 트랜잭션 코드의 분리

서비스 추상화를 통해 트랜잭션 기술에 독립적인 코드를 만들어왔지만 경계설정 부분은 여전히 찜찜하다.

```java
public void upgradeLevels() throws Exception {
	TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
	
	try {
		// ------- 비즈니스 로직 --------
		List<User> users = userDao.getAll();
		for(User user : users) {
			if(canUpgradeLevel(user)) {
				upgradeLevel(user);
			}
		}
		// ---------------------------
		this.transactionManager.commit(status);
	} catch (RuntimeException e) {
		this.transactionManager.rollback(status);
		throw e;
	}
}
```

분명히 레벨업을 시켜주는 메소드인데 트랜잭션 설정에 관련된 코드가 더 많다. 그렇다고 트랜잭션 설정의 위치를 변경하는 것도 불가능하다. 일단은 트랜잭션 관련 코드와 비즈니스 로직 코드를 분리시켜보자.

```java
public void upgradeLevels() throws Exception {
	TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
	
	try {
		upgradeLevelsInternal();
		this.transactionManager.commit(status);
	} catch (RuntimeException e) {
		this.transactionManager.rollback(status);
		throw e;
	}
}

private void upgradeLevelsInternal() {
	List<User> users = userDao.getAll();
	for(User user : users) {
		if(canUpgradeLevel(user)) {
			upgradeLevel(user);
		}
	}
}
```

트랜잭션 코드는 꼭 필요하긴 하지만, 이렇게 분리시켜보면 사실 다른 부분과 정보를 주고 받는 것이 없다. 그렇다면 클래스 밖으로 이 코드를 뽑아내는 것이 가능하지도 않을까? 

![](image-1.png)

이런 경우에 인터페이스를 활용하여 트랜잭션 코드와 비즈니스 로직을 분리할 수 있다. 위 이미지와 같이 기존 UserService에서 직접 로직을 구현하지 않고 인터페이스로 만든 다음, 실제 구현체들을 트랜잭션과 비즈니스 로직 두개의 클래스로 구현하는 방식이다.

```java
public interface UserService {
	public void add(User user);
	public void upgradeLevels();
}
```

```java
public class UserServiceImpl {
	UserDAO userDao;
	MailSender mailSender;
	
	public void upgradeLevels() {
		List<User> users = userDao.getAll();
		for(User user : users) {
			if(canUpgradeLevel(user)) {
				upgradeLevel(user);
			}
		}
	}
	...
```

트랜잭션 관련 코드를 넣기 전 레벨업 기능을 구현했던 모습으로 그대로 돌아왔다. 이 코드는 유저 레벨업 관련 동작만 수행하는 비즈니스 로직에 충실한 코드다. 이제 트랜잭션 처리를 담은 구현체를 만들어야 한다.

```java
public class UserServiceTx implements UserService {
	UserService userService;
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void add(User user) {
		userService.add(user);
	}

	public void upgradeLevels() {
		userService.upgradeLevels();
	}

}
```

트랜잭션 처리 구현체는 다른 UserService 구현체 오브젝트를 DI 받아 모든 기능을 위임해서 동작하는 구조로 되어있다. 이 구조 위에 트랜잭션 경계설정이라는 부가적인 작업을 붙여보자.

```java
public class UserServiceTx implements UserService {
	UserService userService;
	PlatformTransactionManager transactionManager;
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void add(User user) {
		userService.add(user);
	}

	public void upgradeLevels() {
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			userService.upgradeLevels();
			
			this.transactionManager.commit(status);
		} catch (RuntimeException e) {
			this.transactionManager.rollback(status);
			throw e;
		}
	}
}
```

이제 설정 파일을 수정해야 한다. 트랜잭션 구현체가 비즈니스 로직 구현체를 의존하는 구조로 되어 있으니 Client -> UserServiceTx -> UserServiceImpl 순으로 의존하도록 프로퍼티 정보를 분리한다.

```xml
<bean id="userService" class="tobi.user.dao.UserServiceTx">
	<property name="transactionManager" ref="transactionManager" />
	<property name="userService" ref="userServiceImpl" />
</bean>

<bean id="userServiceImpl" class="tobi.user.dao.UserServiceImpl">
	<property name="userDao" ref="userDao" />
	<property name="mailSender" ref="mailSender" />
</bean>
```

테스트코드에서는 *@Autowired*로 UserService를 주입받아 사용하고 있다. 하지만 UserService가 인터페이스로 변경되고, 그 타입의 빈이 2개나 되는 상황에서는 어떤 UserService 빈을 사용해야 할지 모르는 상태가 된다. 이럴땐 id가 userService인 빈이 자동으로 선택돼 주입된다. 이제 클라이언트 입장에서는 UserService에 대해 자세히 모르는 상태에서 인터페이스만 알고있고 컨테이너가 지정해준 빈을 주입받아 사용하는 것이다.

일반적인 코드는 인터페이스만으로 충분할텐데 테스트코드는 Mock 객체를 생성해야해서 별도로 UserServiceImpl 구현체의 빈을 가져와야한다. 또한 기존 테스트 코드는 UserService를 바로 상속하고 있었지만, UserServiceImpl를 상속하도록 변경해야 한다. 

```java
@Autowired
UserServiceImpl userServiceImpl;

@Test
@DirtiesContext
public void upgradeLevels() throws Exception {
	userDao.deleteAll();
	for(User user : users) {
		userDao.add(user);
	}
	
	MockMailSender mockMailSender = new MockMailSender();
	userServiceImpl.setMailSender(mockMailSender);
	
	userServiceImpl.upgradeLevels();
	...
}

upgradeAllOrNothing() 메소드는 트랜잭션 처리를 테스트하기 위한 테스트 메소드였으니, 바뀐 형태로 레벨업 메소드를 수행하도록 수정해준다.

@Test
public void upgradeAllOrNothing() throws Exception {
	TestUserService testUserService = new TestUserService(users.get(3).getId());
	testUserService.setUserDao(this.userDao);
	testUserService.setMailSender(this.mailSender);
	
	UserServiceTx txUserService = new UserServiceTx();
	txUserService.setTransactionManager(this.transactionManager);
	txUserService.setUserService(testUserService);
	
	userDao.deleteAll();
	for(User user : users) {
		userDao.add(user);
	}
	
	try {
		txUserService.upgradeLevels();
		fail("TestUserServiceException expected");
	} 
	catch (TestUserServiceException e) {
	}
	
	checkLevelUpgraded(users.get(1), false);
}
```

이제 비즈니스 로직과 트랜잭션 처리가 완전히 분리되어 UserService 구현체에만 신경쓰면 되는 구조가 되어, 트랜잭션 같은 복잡한 기술을 잘 모르는 사람도 비즈니스 로직만 잘 이해한다면 UserService를 사용하는 것이 가능해졌다.

---

### 고립된 단위 테스트

가장 좋은 테스트 방법은 <U>가능한 작은 단위로 쪼개서 테스트</U>하는 것이다. 테스트 단위를 최대한 잘게 쪼개야 어느 부분에서 에러가 발생했는지 쉽게 파악할 수 있다. 하지만 테스트 대상이 다른 오브젝트에 의존하고 있는 구조가 많아, 테스트 단위를 작게 나누는 것이 쉽지만은 않다.

UserService의 예제를 생각해봐도 사실 UserServic는 JDBC를 이용한 UserDAO, MailSender 등에 의존하고 있다. 우리의 코드만 테스트하는 것처럼 보이지만 사실 그 뒤의 더 많은 오브젝트, 서버, DB, 네트워크와 함께 테스트하고 있는 것이다. DB서버가 다운되거나, 누군가 UserDAO를 건드려서 UserService의 테스트가 실패하게 될 수도 있다. 

그래서 테스트 대상이 주변에 영향 받지 않도록 고립시켜야 한다. 그 방법은 MailSender 에서도 사용했던 테스트용 Mock 오브젝트를 사용하는 것이다.

* UserServiceImpl의 고립된 단위 테스트 만들기

UserService에서는 User 목록 조회와 업데이트를 위해 UserDAO 객체를 주입받아 사용한다. UserServiceTest가 UserDAO에 영향받지 않게 하기 위해 테스트를 위한 UserDAO의 Mock 객체가 필요하다.

```java
public class MockUserDAO implements UserDAO {
	private List<User> user;
	private List<User> updated = new ArrayList();
	
	public MockUserDAO(List<User> user) {
		this.user = user;
	}
	
	public List<User> getUpdated() {
		return this.updated;
	}
	
	@Override
	public List<User> getAll() {
		return this.user;
	}

	@Override
	public void update(User user) {
		updated.add(user);
	}
	...
```

```java
@Test
@DirtiesContext
public void upgradeLevels() throws Exception {
	UserServiceImpl userServiceImpl = new UserServiceImpl();
	
	MockUserDAO mockUserDao = new MockUserDAO(this.users);
	userServiceImpl.setUserDao(mockUserDao);
	
	MockMailSender mockMailSender = new MockMailSender();
	userServiceImpl.setMailSender(mockMailSender);
	
	userServiceImpl.upgradeLevels();
	
	List<User> updated = mockUserDao.getUpdated();
	assertThat(updated.size(), is(2));
	checkUserAndLevel(updated.get(0), "U2", Level.SILVER);
	checkUserAndLevel(updated.get(1), "U4", Level.GOLD);
	
	List<String> request = mockMailSender.getRequest();
	assertThat(request.size(), is(2)); // 레벨업 대상이 2명이라 메일 전송도 2번 예상됨
	assertThat(request.get(0), is(users.get(1).getEmail()));
	assertThat(request.get(1), is(users.get(3).getEmail()));
}
```

원래 UserDAO는 DB에서의 CRUD를 처리하지만 UserService의 레벨업 처리를 테스트만 할 것이라면 사실 DB까지 필요하지 않다. 대강 업데이트를 체크할 수 있는 List를 갖는 Mock 객체로 테스트 내의 UserDAO를 대체해도 무방하다.

### 단위 테스트와 통합 테스트

>단위 테스트 : Mock 오브젝트를 통해 독립적으로 수행되는 테스트  
>통합 테스트 : DB나 외부 서버 등의 리소스가 필요한 테스트

* 항상 단위 테스트가 가능한지 우선적으로 고려

* 단위 테스트를 만들기 어려운 코드(DAO가 대표적)는 통합 테스트로. 대신 이런 경우에는 테스트를 위한 DB 데이터를 별도로 준비해두는게 좋다

* DAO는 통합 테스트로 만들더라도, DAO를 사용하는 코드는 Mock 오브젝트를 활용해 단위 테스트로 만들 수 있다

* 의존 관계가 복잡하게 연결돼있다면 통합 테스트도 반드시 필요하다

### Mock 프레임워크

Mock 오브젝트 생성을 도와주는 프레임워크. <b>Mockito</b> 라는 프레임워크가 인기가 많다. 간단한 메소드 호출만으로 <U>특정 인터페이스를 구현한 테스트용 오브젝트</U>를 만들 수 있다.

Mockito 오브젝트는 다음 네 단계를 거쳐서 사용한다.

* 인터페이스를 이용해 목 오브젝트를 만든다

* 리턴할 값이 있으면 이를 지정해준다

* 테스트 대상 오브젝트에 DI해서 목 오브젝트가 테스트 중에 사용되도록 만든다

* 목 오브젝트의 특정 메소드가 호출됐는지, 어떤 값을 가지고 몇 번 호출 됐는지 검증한다.

```java
// Mock 오브젝트 생성
UserDAO mockUserDao = mock(UserDAO.class);
// Mock 기능 추가 - 특정 메소드가 불릴 때 리턴해줄 대상을 결정
when(mockUserDao.getAll()).thenReturn(this.users);
// Mock 검증
verify(mockUserDao, times(2)).update(any(User.class));
```

> 뭐 이런것도 있다고 알아두고 넘어간다


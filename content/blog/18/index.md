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
	
	userService.upgradeLevels();
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


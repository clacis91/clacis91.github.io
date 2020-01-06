---
title: EhCache 2.x 적용 Example
date: "2020-01-06"
---

## Background
* Spring Boot 버전이 낮은 버전(4.1.x)이라 ```spring-boot-starter-cache``` 패키지를 지원하지 않음  
* 위 패키지가 없어 관련 인터페이스가 존재하지 않아 JSR 규격을 사용하는 EhCache 3.x 사용은 힘들다고 판단함
* EhCache 2.x의 마지막 버전은 2.10.x인데 이건 또 jdk-1.8을 요구해서 jdk-1.7을 사용하는 환경에서 사용이 불가능....
* EhCahe 2.9.1 버전으로 세팅 성공

## pom.xml

* ehcahe는 물론 별도로 ehcache의 인터페이스를 제공해주기 위해 ```spring-context-support```도 추가해줘야 한다.  
* java 표준 cache 인터페이스도 추가

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context-support</artifactId>
</dependency>

<dependency>
    <groupId>net.sf.ehcache</groupId>
    <artifactId>ehcache</artifactId>
    <version>${ehcache.version}</version>
</dependency>

<dependency>
    <groupId>javax.cache</groupId>
    <artifactId>cache-api</artifactId>
</dependency>
```

## Configuration

CacheManager 빈 등록을 위한 Configuration

```@EnableCaching```을 통해 ehcache 기능을 annotation으로 쉽게 사용하는 것(```@Cacheable```, ```@Caching``` 등)이 가능해진다

```java
@Configuration
@EnableCaching
public class CacheConfig {
	
	@Bean
	public EhCacheManagerFactoryBean ehCache() {
		EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();
		factoryBean.setConfigLocation(new ClassPathResource("/ehcache.xml"));
		factoryBean.setShared(true);
		return factoryBean;
	}
	
	@Bean
	public CacheManager cacheManager(EhCacheManagerFactoryBean ehCache) {
		return new EhCacheCacheManager(ehCache.getObject());
	}
}
```

## CacheManager 빈 Test

실제 ehcache 사용을 위한 코드는 아니고 CacheManager 빈이 제대로 생성됐는지 확인하기 위한 Component이다.  
Spring boot가 구동될 때 CacheManager의 실제 구현체(위 경우엔 당연히 ehcahe)의 이름을 출력해준다.

```java
@Component
public class CacheManagerCheck implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CacheManagerCheck.class);

    private final CacheManager cacheManager;

    @Autowired
    public CacheManagerCheck(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void run(String... strings) throws Exception {
        logger.info("\n\n" + "=========================================================\n"
                + "Using cache manager: " + this.cacheManager.getClass().getName() + "\n"
                + "=========================================================\n\n");
    }
}
```

## @Cacheable

Annotation을 사용하면 위에서 설정한 CacheManager를 따로 주입해주지 않아도 쉽게 사용할 수 있다.

```@Cacheable``` Annotation의 value와 key에 일치하는 cache가 존재한다면 *메소드 내부를 호출하지 않고  그냥 cache의 값을 반환*해준다.  
(반대로 일치하는 cache가 없다면 메소드 내부를 호출하여 값을 반환한다.)

```java
@Cacheable(value="test", key="#id")
public String getStoredClientId(String id){
    logger.info("****저장된 cache없음****");
    dummyQuery(); // DB 호출이 오래걸렸다고 가정하기 위해 2초 sleep
    return "testId";
}

private void dummyQuery() {
    try {
        Thread.sleep(2000); // 2 seconds
    } catch (InterruptedException e) {
        throw new IllegalStateException(e);
    }
}
```

(단 **이 버전(4.1.x)**에서는 ```@Cacheable``` annotation이 붙은 메소드는 다른 class에서 직접 호출할때만 동작한다. 동일한 클래스의 다른 메소드에서 @Cacheable 메소드를 호출해도 동작하지 않는다. 이걸 몰라서 삽질을 오래했다.... 4.3.0 부터는 해결됐다고 하는듯? https://brocess.tistory.com/236)

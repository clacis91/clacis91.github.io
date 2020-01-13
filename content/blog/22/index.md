---
title: Spring MVC Interceptor Example
date: "2020-01-13"
---

## Background
* [바로 전 포스팅](../21)에서 기껏 ```@Cacheable```로 ehcache 사용준비를 해놨는데.... ```@Controller```에는 @Cacheable을 붙여줘봤자였다. 
* @Controller는 별도의 Context에서 (Servlet-context) 동작하기 때문에 AOP가 적용되지 않는다고 한다. 
  * 정확히는 <U>Servlet Context -> Application Context</U>의 Advice는 실행 가능한데 역은 불가능
  * https://stackoverflow.com/questions/42733826/spring-aop-aspect-around-doesnt-work
* 애초에 @Controller에서 AOP 방식의 전처리를 사용하는게 잘못된 발상이었던거 같다. 
  * 잘못됐다기 보다는 <U>**Interceptor**</U>가 있는데 *굳이 AOP를* 쓸 필요가 없다는 느낌?
  * https://addio3305.tistory.com/86 
* <U>**Interceptor**</U>를 써보기로 했다.

## Interceptor?

```TBD```  
인터셉터 설명 추가 

## Interceptor 활용

내가 필요한 전처리 기능은 ```@Controller```로 전달된 토큰 내의 특정 value를 DB와 비교하여 유효성 체크 후 불일치 시 에러코드를 반환해주는 것이다.

이 때 DB에서 한번 가져왔던 값은 Cache에 담아놓는 식으로 구현하려고 한다.

### Custom Interceptor

```java
@Component
public class AuthCheckInterceptor extends HandlerInterceptorAdapter {
	
	private CacheManager cacheManager;
	
	@Autowired
	public AuthCheckInterceptor(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws InvalidTokenException {
    	if(validToken() == false) { 
    		throw new InvalidTokenException();
    	}
    	
    	return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception arg3) throws Exception {
    }
    
    private boolean validToken() {
        // VALIDATE TOKEN
    }
    
  	private String getStoredValue(String key) {
  		logger.info("==== Check Value from cache... ====");
  		String cachedValue = getCachedValue(key);
  		
  		if(cachedValue != "") {
  			return cachedValue;
  		}
  		
  		logger.info("==== Not found");
  		logger.info("==== Check Value from DB... ====");
  		String value = getValueFromDB(key);
  		logger.info("from DB ::: " + value);
  		cacheManager.getCache("value").put(key, value);
  		logger.info("to Cache ::: " + cacheManager.getCache("value").get(key).get().toString());
  		
  		return value;
  	}
  	
  	private String getCachedValue(String key) {
  		Cache valueCache = cacheManager.getCache("value");
  		
  		if (valueCache == null) {
  			return "";
  		}
  		
  		ValueWrapper cacheWrapper = valueCache.get(key);
  		
  		if(cacheWrapper == null) {
  			return "";
  		}
  		
  		logger.info("from Cache ::: " + cacheWrapper.get().toString());
  		return cacheWrapper.get().toString(); 
  	}
}
```

### Configuration

```java
@Configuration
public class InterceptorConfiguration extends WebMvcConfigurerAdapter {
	
	@Autowired
	private AuthCheckInterceptor authCheckInterceptor;
	
    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(authCheckInterceptor).addPathPatterns("/**/example/**");
    }
}
```

### Handler

Interceptor Handler에서 토큰 불일치 시 Exception을 발생시키는데, 그 Exception을 처리해줄 Handler를 등록해줘야 한다. (따로 설정으로 빼도 되고, @Controller에 메소드로 등록해줘도 됨)

```java
@ExceptionHandler(InvalidTokenException.class)
private Object handleInvalidTokenException(InvalidTokenException e) {
    logger.info("Invalid Token Exception");
    return setResponseEntity(e, 400, "잘못된 요청입니다.");
}

private Object setResponseEntity(BaseException e, int code, String msg) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    
    BaseResponse baseResponse = new BaseResponse();
    baseResponse.setResponseCode(String.valueOf(code));
    baseResponse.setMessage(msg);
    
    Map<String,Object> resultDataSet = new HashMap<>();
    baseResponse.setData(resultDataSet);
    
    HttpStatus httpStatus = HttpStatus.valueOf(code);
    
    return new ResponseEntity<BaseResponse>(baseResponse, headers, httpStatus);
}
```
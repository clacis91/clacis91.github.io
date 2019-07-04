---
title: Lombok annotations
date: "2019-07-02"
---

lombok
http://wonwoo.ml/index.php/post/1607
https://www.daleseo.com/lombok-popular-annotations/
http://kwonnam.pe.kr/wiki/java/lombok

lombok 사용시 주의점
http://kwonnam.pe.kr/wiki/java/lombok/pitfall

```java
@Getter
@Setter
public class RegisterRequest {
	private String email;
	private String password;
	private String confirmPassword;
	private String name;
    ...
```

```java
@Getter
public class Member {
	@Setter private long id;
	private String email;
	private String password;
	private String name;
    ...
```
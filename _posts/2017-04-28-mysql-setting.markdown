---
layout: post
title:  "MySQL setting"
categories: [setting]
---

 1. *** Command ***
 	
	* MySQL 유저 계정으로 로그인

	# mysql -u user -p
	# password 입력

	* root 패스워드 변경

	# ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';

	* 사용자 추가

	# CREATE USER 'userId'@'localhost' identified by 'userpassword';

	* 권한 추가

	# GRANT ALL privileges on *.* to 'userid'@'localhost';

	* Database 생성

	# CREATE DATABASE testdb;
	(생성된 DB목록보기)
	# SHOW DATABASES;
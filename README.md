## Cyber Security Base 2019-2020 Course Project I
A banking application based on the [project starter template](https://github.com/cybersecuritybase/cybersecuritybase-project). This app uses a SQL database (h2 database engine to exact) and it’s set to in memory only. This means that the database will reset if you restart the app. There are users (customers) with bank accounts. Each user can have multiple bank accounts, and money can be transferred from a bank account to another. There’s also some admin only functionality.
#### The bank has the following users:
> username:password (unknown > password not known)

-	Ted:Ted123
-	Jack:unknown
-	admin:unknown

Your name is “Ted” and the password is “Ted123” (without the quotation marks). You are poor and have decided to solve the problem in not so legal way. There also seems to be an administrator account with the username of “admin”, but the password is nowhere near to be found. Gladly for us, seems like someone didn't do such a great job implementing the login system. There also seems to be some security misconfiguration and access control problems which may help you with your path to the rich life. If you want to have a little challenge, try breaking in before checking out the vulnerabilities! :)

#### Install instructions
You can run the app just like any other course project. Just download the files or clone the repo and open it in your IDE. You can also download the [precompiled .jar file](https://github.com/mmxw11/cybersecuritybase-project/raw/master/cybersecuritybase-project-app.jar) and run it directly from command line. Java 8 is the minimum required version.
1.	Open terminal in the same directory as where the jar is located.
2.	Run “java -jar cybersecuritybase-project-app.jar”

### Vulnerabilities
### #1: A1:2017 - Injection
The application login system is vulnerable to SQL injection attacks. It takes the user input (username and password) and directly appends them to the SQL query without properly escaping them. This makes it possible for attackers to sign in to any account without knowing its password.

**Steps to reproduce**

1.	Open the app in your browser (localhost:8080 default).
2.	Username can be anything.
3.	For the password, write ' OR username='Ted' -- where Ted is the real username of the account you want to sign in.
4.	Click “Sign in”.
5.	Now you have successfully signed into someone’s account and can transfer all their money to yours.

**Fix**

> File: src/main/java/sec/project/configuration/DbAuthenticationProvider.java

Username and password parameters are directly appended to the SQL query.
```
jdbcTemplate.query("SELECT * FROM bank_user WHERE username='" + username + "' AND password ='" + password + "'", new BeanPropertyRowMapper<>(BankUser.class)); 
```
Instead, we should use parametrized queries (aka prepared statements). Replace the values with question marks and pass the values as arguments to the query method.
```
jdbcTemplate.query("SELECT * FROM bank_user WHERE username=? AND password =?", new BeanPropertyRowMapper<>(BankUser.class), username, password);
```
Now Spring will use prepared statements and the vulnerability has been fixed.

**Better Fix**

Even a better way to fix this would be not to use the password for the database query at all. Query the database only with the username and compare the passwords on the server side. There is a proper implementation of this in the unused class
> src/main/java/sec/project/configuration/DbUserDetailsService.java

which can be used as an alternative to replace the faulty DbAuthenticationProvider.

### #2 A7:2017 - Cross-Site Scripting (XSS)
Bank account transaction history uses th:utext for the user specified message. This means anyone who transfers funds to another user’s bank account can inject XSS to the transaction message which is then executed by the target when they view the bank account.

**Steps to reproduce**

1.	Open the app in your browser (localhost:8080 default).
2.	Sign in.
3.	Choose a bank account from the list and open it.
4.	Go to the Transfer funds section
5.	Choose your target’s bank account from the “Transfer to” list.
6.	Use smallest value possible (0.01) for the amount. We don’t want to give our victim free money.
7.	Use e.g. <script >alert("XSS");</script> for the message.
8.	Click “Submit”.

Beware! The script will also be executed on your account, so you must add some logic that it only activates on the victims account.

**Fix**

> File: src/main/resources/templates/bankaccount.html#134 line

Change th:utext to th:text under the Transaction History section and Thymeleaf will automatically escape the text.

### #3 A6:2017 - Security Misconfiguration
The admin account is using default credentials.

**Steps to reproduce**

1.	Open the app in your browser (localhost:8080 default).
2.	Type admin as username
3.	Type admin as password.
4.	Click “Sign in”.

**Fix**

There’s no single way to fix this. For instance, the app may ask system administrator to supply an admin password at first run.

### #4 A3:2017 - Sensitive Data Exposure

#### #1 Insecure HTTP
The application uses HTTP and everything is transmitted in plaintext. This means passwords, cookies and everything else are prone for spooning.

**Steps to reproduce**

1.	Open the app in your browser (localhost:8080 default).
2.	Look at the URL bar.
3.	As you can see, it’s using insecure HTTP and not HTTPS

**Fix**

Enable and force HTTPS for all pages.

#### #2 Plaintext credentials
User credentials are stored in plaintext in the database. This means that if data ever get leaked the passwords can be read by anyone.

**Steps to reproduce**

1.	This can be verified by looking at the 
> src/main/java/sec/project/configuration/DbAuthenticationProvider.java file.
2.	Users and their bank accounts are created in the init method. The passwords are clearly visible.
3. The authenticate method uses the password for the SQL query without any hashing.

**Fix**

> File: src/main/java/sec/project/configuration/SecurityConfiguration.java

Passwords and other sensitive data should be hashed and salted before storing them to the database. In case of a leak, they are impossible read. Adding a salt to each password will make cracking them harder. If two users have same passwords and the app doesn’t salt passwords, they end up having same hash in the database. Spring offers an easy way to fix this. All you need to do is use the aforementioned user details service by registering it in SecurityConfiguration class and adding a password encoder.
```
@Autowired
public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService)
    .passwordEncoder(passwordEncoder());    
}

@Bean 
public PasswordEncoder passwordEncoder() { 
    return new BCryptPasswordEncoder();
}
```
Now passwords will be automatically hashed and salted before they are stored to the database. Accounts that are added to the database with existing passwords should have theirs passwords hashed beforehand.

### #5 A5:2017-Broken Access Control
The application doesn’t properly enforce access control. Authenticated users can access other’s profiles, view their banking accoutnts, and transfer funds from their accounts just by changing the URL regardless of whether they are owner of the account.

**Steps to reproduce**
1.	Open the app in your browser (localhost:8080 default).
2.	Sign in as Ted.
3.	View your own profile, and look at the url address which looks as follows http://localhost:8080/user/ted
5.	Change ted to jack.
6.	Now you can view Jack's banking accounts and tranfer funds from them which you shouldn’t be able to.

**Fix**

> Files: src/main/java/sec/project/controller/UserController.java

>src/main/java/sec/project/configuration/SecurityConfiguration.java

The viewUser method in UserController must check whether the currently authenticated user is the owner of the requested profile or an admin. This can be achieved with the PreAuthorized annotation by comparing the supplied username argument to the currently authenticated user’s name as follows
```
@PreAuthorize("hasRole('ROLE_ADMIN') or authentication.principal.username==#username")
```
To enable the PreAuthorized annotations, you need to add `@EnableGlobalMethodSecurity` annotation to the
> src/main/java/sec/project/configuration/SecurityConfiguration class. 
```
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, proxyTargetClass = true)
```

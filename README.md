# Beauties

Homage to 記帳風月

# Required

For build:

- JDK

- Gradle

For run:

- MySQL

# Setup

1. Build

```sh
gradle fatJar
```
--> `build/libs/beauties.jar

2. Setup MySQL

In case dbname = beauties

```sh
mysql -u <username> -p beauties < mysql/beauties.sql
```

NOTE: also refer to mysql/my.cnf 

3. Edit beauties.properties

[DB] section:

- dbHost
- dbPort
- dbUser
- dbPass
- dbName: default db name
- dbNames={dbName:Window title}

# Run

```sh
java -jar beaties.jar beauties.properties
```

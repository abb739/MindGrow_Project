@echo off
set "JAVA_HOME=C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.2\jbr"
set "PATH=%JAVA_HOME%\bin;%PATH%"

if not exist "target\classes" mkdir target\classes

echo Compiling...
javac -d target/classes -cp "src/main/resources;src/main/java;C:\Users\Maraa\.m2\repository\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar;C:\Users\Maraa\.m2\repository\org\mindrot\jbcrypt\0.4\jbcrypt-0.4.jar" src/main/java/org/example/models/User.java src/main/java/org/example/utils/MyDatabase.java src/main/java/org/example/services/UserService.java src/main/java/org/example/tests/TestCRUD.java

if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b %errorlevel%
)

echo Running TestCRUD...
java -cp "target/classes;C:\Users\Maraa\.m2\repository\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar;C:\Users\Maraa\.m2\repository\org\mindrot\jbcrypt\0.4\jbcrypt-0.4.jar" org.example.tests.TestCRUD

pause

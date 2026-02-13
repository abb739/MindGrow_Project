@echo off
set "JAVA_HOME=C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.2\jbr"
set "MAVEN_HOME=C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.2\plugins\maven\lib\maven3"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

call mvn clean javafx:run
pause

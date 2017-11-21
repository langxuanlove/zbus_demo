REM SET JAVA_HOME=C:\Program Files (x86)\Java\jdk1.7.0_25

SET ZBUS_HOME=.
SET JAVA_OPTS=-server -Xms64m -Xmx1024m -XX:+UseParallelGC
SET MAIN_CLASS=org.zbus.mq.server.MqServer 
REM -ipOrder *>172>192
SET MAIN_OPTS=-h 0.0.0.0 -p 15555 -verbose false -store store -track
SET LIB_OPTS=%ZBUS_HOME%/lib/*;%ZBUS_HOME%/*;

IF NOT EXIST "%JAVA_HOME%" (
    SET JAVA=java
) ELSE (
    SET JAVA=%JAVA_HOME%\bin\java
)
java %JAVA_OPTS% -cp %LIB_OPTS% %MAIN_CLASS% %MAIN_OPTS% 
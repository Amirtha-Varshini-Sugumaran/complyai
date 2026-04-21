@ECHO OFF
SET BASE_DIR=%~dp0
SET WRAPPER_DIR=%BASE_DIR%\.mvn\wrapper
SET JAR=%WRAPPER_DIR%\maven-wrapper.jar

IF NOT EXIST "%JAR%" (
  powershell -Command "Invoke-WebRequest -Uri ((Get-Content '%WRAPPER_DIR%\maven-wrapper.properties' | Select-String 'wrapperUrl').ToString().Split('=')[1]) -OutFile '%JAR%'"
)

java -Dmaven.multiModuleProjectDirectory="%BASE_DIR%" -classpath "%JAR%" org.apache.maven.wrapper.MavenWrapperMain %*

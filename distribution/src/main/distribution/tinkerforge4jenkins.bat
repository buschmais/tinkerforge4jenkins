@ECHO OFF
SET BASEDIR=%~dp0%
java -jar %BASEDIR%\tinkerforge4jenkins-client.jar %*

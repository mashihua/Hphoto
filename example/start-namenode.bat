echo Starting NameNode 
@echo off
if "%OS%"=="Windows_NT" @setlocal
if "%OS%"=="WINNT" @setlocal

@rem %~dp0 is expanded pathname of the current script under NT
set DEFAULT_HOME=%~dp0

if not exist hadoop (java -Dexec.home=%DEFAULT_HOME% -cp conf -jar hadoop-exe.jar namenode -format)

java  -Dexec.home=%DEFAULT_HOME% -cp conf -jar hadoop-exe.jar  namenode
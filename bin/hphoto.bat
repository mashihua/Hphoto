rem setup command to run

if ""%1""=="""" goto usage
set CMD=%1
shift
set CMD_ARGS=%1

set JAVA_HEAP_MAX=-Xmx512m

java %JAVA_HEAP_MAX%  -cp ;hphoto.jar%CLASSPATH%  com.hphoto.server.UserService %CMD% %CMD_ARGS%
goto:EOF


:usage
echo "Usage: hadoop COMMAND"
echo "where COMMAND is one of:"
echo  " satrt             	start server"
echo   "stop	stop server"
goto:EOF

:noSet
echo "please set CLASSPATH%"
goto:EOF
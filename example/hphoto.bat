set JAVA_HEAP_MAX=-Xmx512m

java %JAVA_HEAP_MAX% -Dexec.home=. -cp conf;hphoto.jar;hadoop-exe.jar com.hphoto.server.UserService start --browser=false




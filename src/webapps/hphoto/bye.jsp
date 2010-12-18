<%@ page
  contentType="text/html; charset=UTF-8"
  import="javax.servlet.*"
  import="javax.servlet.http.*"
  import="java.io.*"
  import="java.util.*"
  import="java.net.*"
  import="java.text.*"
  import="org.apache.hadoop.io.*"
  import="org.apache.hadoop.conf.*"
  import="com.hphoto.util.*"
  import="com.hphoto.server.*"
  import="com.hphoto.bean.*"
  import="java.util.regex.*"
%>
<%
request.setCharacterEncoding("UTF-8");
String redir = request.getParameter("redir");
String reffer = request.getHeader("referer");
request.getSession().invalidate();
Cookie   killCookie  =  new Cookie("rememberme",null);  
killCookie.setMaxAge(0);  
killCookie.setPath("/");  
response.addCookie(killCookie);  
if(redir == null || redir.equals("")){
	response.sendRedirect("/");
}else{
	response.sendRedirect(redir);
}
%>
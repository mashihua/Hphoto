<%@ page
  contentType="text/html; charset=UTF-8"
  import="javax.servlet.*"
  import="javax.servlet.http.*"
  import="java.io.*"
  import="java.util.*"
  import="java.net.*"
  import="java.text.DateFormat"
  import="org.apache.hadoop.io.*"
  import="org.apache.hadoop.conf.*"
  import="com.hphoto.util.*"
  import="com.hphoto.server.*"
  import="com.hphoto.bean.*"
  import="java.util.regex.*"
%>

<%

request.setCharacterEncoding("UTF-8");
Configuration conf = (Configuration)application.getAttribute("hphoto.conf");
TableServer server = (TableServer)application.getAttribute("hphoto.tableServer");

String rememberme = WebUtil.getCookieValue(request.getCookies(),"rememberme","false");
String cuser = WebUtil.getCookieValue(request.getCookies(),"name",null);

if(request.getSession().getAttribute("login")!= null && !request.getSession().getAttribute("login").equals("true")){
	response.sendRedirect("/hp/login");
}

if(!rememberme.equals("true")){
	response.sendRedirect("/hp/login");
}

java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat("yyyy-M-dd");
String date = dateFormatter.format(new Date());
%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n" prefix="i18n" %>
<i18n:bundle baseName="com.hphoto.message.web"/>
<!--
Content-type: Preventing XSRF in IE.
-->
<div id="addalbum_container">
<form action="/hp/createAlbum" method="post">
<input type="hidden" name="user" value="<%=cuser%>" />
<input type="hidden" name="redir" value="" />
<h1><i18n:message key="addalbum.title"/></h1>
<h2><i18n:message key="addalbum.input.title.description"/></h2>
<p><input name="title" value="Untitled Album" class="warning" /></p>
<h2><i18n:message key="addalbum.input.date.description"/></h2>
<p><input id="dateinput" name="date" value="<%=date%>" class="warning"/></p>
<h2><i18n:message key="addalbum.input.description.description"/></h2>
<p><textarea class="default" name="description"/></textarea></p>
<h2><i18n:message key="addalbum.input.location.description"/></h2>
<p><input name="location"  class="default" /></p>
<p>
<input id="publicaccess" type="radio" checked="" value="public" name="access"/>
<label for="publicaccess">
<i18n:message key="addalbum.input.public.description"/>
</label>
</p>
<p>
<input id="unlistedaccess" type="radio" value="private" name="access"/>
<label for="unlistedaccess"><i18n:message key="addalbum.input.private.description"/></label>
</p>
<p>
<input type="submit" value="<i18n:message key="addalbum.submit"/>">
<input type="button" class="jqmClose" value="<i18n:message key="addalbum.cancel"/>">
</p>
</form>
</div>


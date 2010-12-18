<%@ page
  contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"
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
%>
<% 

request.setCharacterEncoding("UTF-8");

Configuration conf = (Configuration)application.getAttribute("hphoto.conf");
TableServer server = (TableServer)application.getAttribute("hphoto.tableServer");
String base = "http://" +request.getServerName() + (request.getLocalPort() == 80 ? "" : ":"+Integer.toString(request.getLocalPort()));
ResourceBundle resourceBundle = ResourceBundle.getBundle("com.hphoto.message.web", request.getLocale());
String language = resourceBundle.getLocale().getLanguage();
base += resourceBundle.getString("site.base");



String cuser = WebUtil.getCookieValue(request.getCookies(),"name",null);
String reffer = request.getHeader("referer");
boolean submit = false;

String referrer = request.getHeader("referer");
String owner = request.getParameter("user");
String pwd = request.getParameter("pwd");
String rememberme = request.getParameter("PersistentCookie");
UserProfile[] users = server.getUser(owner,1);

boolean login = false;
if(users!=null && users.length >0){
	if(users[0].getNicename().equals(owner) && users[0].getPassword().equals(StringUtil.MD5Encode(pwd))){
		login = true;
		Calendar c=Calendar.getInstance();
		c.add(Calendar.YEAR,30);
		if(rememberme.equals("yes")){
			WebUtil.CookieValue value = new WebUtil().new CookieValue("rememberme","true");
			value.setPath("/");
			//value.setDomain(request.getLocalAddr());
			value.setExpires(new Date(c.getTimeInMillis()));
			WebUtil.setCookie(response,value);
		}
		WebUtil.CookieValue value = new WebUtil().new CookieValue("name",owner);
		value.setPath("/");
		value.setExpires(new Date(c.getTimeInMillis()));
		WebUtil.setCookie(response,value);	
	}
	submit = true;
}


if(login){
		//set login session
		request.getSession().setAttribute("login","true");
		response.sendRedirect("/home");
}

%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n" prefix="i18n" %>
<i18n:bundle baseName="com.hphoto.message.web" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!--
Content-type: Preventing XSRF in IE.
-->
<html>
<head>
<base href="<%=base %>" />
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<title><i18n:message key="site.title"/> </title>
<meta name="keywords" content="" />
<meta name="description" content="" /> 
<link rel="shortcut icon" href="<%=base %>favicon.ico" type="image/x-icon" /> 
<noscript>
<meta http-equiv="refresh" content="3;url=nojavascript.html" />
<div class="noscript">
<i18n:message key="browser.noscript"/>
</div>
</noscript>
<link href="style/default.css" rel="stylesheet" type="text/css" media="all"/>
<script src="script/jquery.js" type="text/javascript"></script>
<script src="script/cmxforms.js" type="text/javascript"></script>
<script src="script/validate.js" type="text/javascript"></script>
<script src="script/jqModal.js" type="text/javascript"></script>
<script type="text/javascript">
hphoto.tip({element:"#header a:last",cookie:"newTo",title:'<%=resourceBundle.getString("newto.tip")%>',content:'<%=resourceBundle.getString("newto.content")%>'});
</script>
<style type="text/css">
form.hform{
	width:80%;	
	margin:2em 0 2em 4em;
}
#promoWidget h3{
color:#AA0000;
font-weight:bold;
} 
</style>
</head>
<body>
<div id="header">
<table width="100%">
<tr>
<td width="100%"><a href="/"><img src="images/logo-<%=language%>.gif" alt="<i18n:message key="site.title"/>" /></a></td>
<td><nobr> 
<a href="/hp/login"><i18n:message key="user.login.in"/></a> | 
<a href="/hp/register"><i18n:message key="user.login.get"/></a> | 
<a href="/s/help.html"><i18n:message key="user.help"/></a>
</nobr>
</td>
</tr>
</table>
</div>
<div id="contain">	
<div id="mainbg">
<div id="navbar"><!-- nav --> </div> 
<table>
<tr>
<td width="100%">
<div>
<div class="prettybox">
<div id="l_header">
<i18n:message key="login.signtitle"/>
</div>
<% if(submit){%>
<ul class="error">
<li><i18n:message key="login.error"/></li>
</ul>
<%}%>
<form action="/hp/login" class="hform" method="post">
<fieldset>
		<legend><i18n:message key="user.login.title"/></legend>
		<p>
			<label for="user"><i18n:message key="user.login.name"/></label>
			<input id="user" tabindex="1" name="user" value="<%= cuser == null? "" : cuser %>" /> <a href="/hp/register"><i18n:message key="user.getaccount"/></a>
		</p>
		<p>
			<label for="pwd"><i18n:message key="user.login.password"/></label>
			<input type="password" tabindex="2" id="pwd" name="pwd" /> <a href="#"><i18n:message key="user.forgotpwd"/></a>
		</p>
		<p>
			<label></label>
			<input id="PersistentCookie" tabindex="3" type="checkbox" name="PersistentCookie" value="yes" checked="checked" /> <label class="nocmx" for="PersistentCookie"><i18n:message key="user.login.persisten"/></label>
		</p>
		<p>
			<label></label>
			<input class="submit" tabindex="4" type="submit" value="<i18n:message key="login.submit"/>"/>
		</p>
</fieldset>
</form>
</div>
</div>
</td>
<td>
<div>
<div class="widgets paddingLeft">
<div class="widget paddingLeft" id="promoWidget">
<h3><i18n:message key="index.features"/></h3>
<p><i18n:message key="index.features.introduce"/></p>
</div>
</div>
</div>
</td>
</tr>
</table>


</div>

<div id="clear"></div>
</div>
<div id="footer">
<a target="_blank" href="#"><i18n:message key="footer.terms"/></a> -
<a target="_blank" href="#"><i18n:message key="footer.policy"/></a> -
<a target="_blank" href="#"><i18n:message key="footer.contact"/></a> 
</div>
</body>
</html>
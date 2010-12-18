<%@ page
  contentType="text/html; charset=UTF-8"
  import="javax.servlet.*"
  import="javax.servlet.http.*"
  import="java.io.*"
  import="java.util.*"
  import="java.net.*"
  import="java.text.DateFormat"
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



String rememberme = WebUtil.getCookieValue(request.getCookies(),"rememberme","false");
//need session server
//String sid = WebUtil.getCookieValue(request.getCookies(),"sid",null);
String user = WebUtil.getCookieValue(request.getCookies(),"name",null);
boolean login = false;
if(rememberme.equals("true")){
	if(user != null && !user.equals("")){
		UserProfile[] profile = server.getUser(user,1);
		if(profile != null && profile.length != 0 && profile[0].getNicename().equals(user)){
			login = true;
		}
	}
}
if(login){
	request.getSession().setAttribute("login","true");
	response.sendRedirect("/home");
}
%>

<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n" prefix="i18n" %>
<i18n:bundle baseName="com.hphoto.message.web"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!--
Content-type: Preventing XSRF in IE.
-->
<html>
<head>
<base href="<%=base%>" />
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
form.hform {
	margin:1em 0 1em;
	font-size: 1.0em;
	color: #333;
	width:250px;
}
form.hform fieldset{
padding:0.5em 0;
}

form.hform fieldset p{
padding:0.3em 0;
}

form.hform fieldset label{
	margin:0;
	text-align:right;
}

form.hform legend {
	border:1px solid #C1D9FF; 
	border:none;
	font-weight:bold;
	font-size:13px;
}

#promoWidget h3{
color:#AA0000;
font-weight:bold;
} 

form.hform label { width: 4em; line-height:1em;}
</style>
</head>
<body>
<div id="header">
<table width="100%">
<tr>
<td width="100%"><a href="/"><img src="images/logo-<%=language%>.gif" alt="<i18n:message key="site.title"/>" /></a></td>
<td><nobr>
<% if(login){%>
<b><%=user%></b> | <a href="/bye"><i18n:message key="user.login.out"/></a> | 
<%}else{ %>  
<a href="/hp/login"><i18n:message key="user.login.in"/></a> | 
<a href="/hp/register"><i18n:message key="user.login.get"/></a> | 
<%}%>
<a href="/s/help.html"><i18n:message key="user.help"/></a>
</nobr>
</td>
</tr>
</table>
</div>
<div id="contain">
	
<div id="mainbg">
<div id="navbar"> <!--nav--> </div>
<table>
<tr>
<td width="100%">
<div>
	<div class="prettybox">
		<h1 style="text-indent:2em;padding:0.5em;"><i18n:message key="index.welcome"/></h1>
		<div style="text-indent:2em;margin:2em 0 4em;">
		<i18n:message key="index.welcome.intro"/>
		</div>
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
		
		<div class="widget paddingLeft" id="countWidget">
			<p><a href="/hp/login"><i18n:message key="user.login.in"/></a></p>
			<p><a href="/hp/register"><i18n:message key="user.getaccount"/></a></p>
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

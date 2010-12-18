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
  import="org.apache.hadoop.util.*"
  import="com.hphoto.util.*"
  import="com.hphoto.server.*"
  import="com.hphoto.bean.*"
  import="java.util.regex.*"
  import="com.hphoto.*"
%>
<%!
private boolean  invalidName(Configuration conf,String name){
	String[] names = StringUtils.getStrings(conf.get("hhpoto.invalidName","login,home,hp,index,bye,s,api,data,support,service,help"));
	for(String invalid:names){
		if(invalid.equals(name)){
			return true;
		}
	}
	return false;
}
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
if(cuser != null){
	Cookie   nameCookie  =  new Cookie("name",null);  
	nameCookie.setMaxAge(0);  
	nameCookie.setPath("/");  
	response.addCookie(nameCookie); 	
}
ArrayList<String> list = new ArrayList<String>();
String email = request.getParameter("email");
String name = request.getParameter("name");
String pwd = request.getParameter("password");
String re_pwd = request.getParameter("confirm_password");
String PersistentCookie = request.getParameter("PersistentCookie");
String captcha = request.getParameter("captcha");
String sessionKey = (String)request.getSession().getAttribute(Captcha.CAPCHA_SESSION_KEY);
//String reffer = request.getHeader("referer");

boolean submit = false;//(reffer != null && reffer.indexOf("register") != -1);



Pattern pattern = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");

Pattern namePattern = Pattern.compile("^[a-zA-Z._@]+$");

if(captcha != null ){
	if(email == null){
		list.add(resourceBundle.getString("register.email.required"));
	}else{
		Matcher matcher = pattern.matcher(email);
		if(!matcher.find()){
			list.add(resourceBundle.getString("register.email.required"));
		}
	}
	
	if(name == null || name.trim().length() < 1){
		list.add(resourceBundle.getString("register.name.minLength"));
	}else if(invalidName(conf,name)){
		list.add(name + " " + resourceBundle.getString("register.name.invalid"));
	}else if(!namePattern.matcher(name).find()){
		list.add(name + " " + resourceBundle.getString("register.name.character"));
	}else{
		UserProfile[] user = server.getUser(name,1);		
		if(user != null && user.length == 1 && user[0].getNicename().equals(name)){
			list.add(name +  " " + resourceBundle.getString("register.name.used"));
		}
	}
	
	if(pwd == null || pwd.length() < 5 ){
		list.add(resourceBundle.getString("register.password.minLength"));
	}
	
	if(re_pwd == null || !pwd.equals(re_pwd)){
		list.add(resourceBundle.getString("register.confirm_password.equalTo"));
	}
	
	if(captcha == null || !captcha.equals(sessionKey)){
		list.add(resourceBundle.getString("register.captcha.invalid"));
	}
	
	submit =true;
}

if(list.size() == 0 && submit){
	UserProfile user = new UserProfile();
	user.setMail(email);
	user.setNicename(name);
	user.setFirstname("");
	user.setLastname("");
	user.setMailpublic(true);	
	user.setPassword(StringUtil.MD5Encode(pwd));
	user.setImgurl(FConstants.DEFAULT_USER_IMAGE);
	server.setUser(new UserProfile[]{user});
	Calendar c=Calendar.getInstance();
	c.add(Calendar.YEAR,30);
	if(PersistentCookie.equals("yes")){
			WebUtil.CookieValue value = new WebUtil().new CookieValue("rememberme","true");
			value.setPath("/");
			//value.setDomain(request.getLocalAddr());			
			value.setExpires(new Date(c.getTimeInMillis()));
			WebUtil.setCookie(response,value);
	}
	WebUtil.CookieValue value = new WebUtil().new CookieValue("name",name);
	value.setPath("/");
	value.setExpires(new Date(c.getTimeInMillis()));
	WebUtil.setCookie(response,value);	
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
<script type="text/javascript">
$().ready(function() {
	// validate signup form on keyup and submit
	var validator = $("form.hform").validate({
		errorElement: "em",
		errorClass: "invalid",
		onblur:function(ele){
			var ele = $(ele)
			if(validator.element(ele)){
				$(ele).removeClass("default").removeClass("warning").removeClass("error").addClass("ok");
			}else{
				$(ele).removeClass("default").removeClass("warning").removeClass("ok").addClass("error");
			}			
		},
		rules: {
			email: {
				required: true,
				email: true
			},
			name: {
				required: true,
				minLength: 1
			},
			password: {
				required: true,
				minLength: 5
			},
			confirm_password: {
				required: true,
				minLength: 5,
				equalTo: "#password"
			},
			captcha:{
				required: true,
				minLength: 6
			}
		},
		messages: {
			email: '<i18n:message key="register.email.required" />',
			name: {
				required: '<i18n:message key="register.name.required"/>',
				minLength: '<i18n:message key="register.name.minLength"/>'
			},
			password: {
				required: '<i18n:message key="register.password.required"/>',
				minLength: '<i18n:message key="register.password.minLength"/>'
			},
			confirm_password: {
				required: '<i18n:message key="register.confirm_password.required"/>',
				minLength: '<i18n:message key="register.confirm_password.minLength" />',
				equalTo: '<i18n:message key="register.confirm_password.equalTo" />'
			},
			captcha:{
				required:'<i18n:message key="register.captcha.required" />',
				minLength: '<i18n:message key="register.captcha.minLength" />'
			}
		}
	});
	
});
</script>
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
<div id="navbar"> <!-- nav --> </div> 

<table>
<tr>
<td width="100%">
<div>
<div class="prettybox">
<div id="l_header">
<i18n:message key="register.information" />
</div>
<%if(list.size() > 0){ %>
<ul class="error">
<% for(String error:list){%><li><%=error%></li><%} %>
</ul>
<%} %>
<form action="/hp/register" class="hform" method="post">
<fieldset>
		<legend><i18n:message key="register.title" /></legend>
		<p>
			<label for="email"><i18n:message key="register.email" /></label>
			<input id="email" name="email" class="warning" tabindex="1" />
		</p>
		<p>
			<label for="name"><i18n:message key="register.account" /></label>
			<input id="name" name="name" class="warning" tabindex="2"  />
		</p>
		<p>
			<label for="password"><i18n:message key="register.password" /></label>
			<input id="password" name="password" type="password" class="warning" tabindex="3" />
		</p>
		<p>
			<label for="confirm_password"> <i18n:message key="register.confirm_password" /></label>
			<input id="confirm_password" name="confirm_password" type="password" class="warning" tabindex="4" />
		</p>
		<p>
			<label for="captcha"><i18n:message key="register.verification" /></label>
			<input id="captcha" name="captcha"  class="warning" tabindex="5" />
		</p>
		<p>
		<label></label><span><img src="/hp/captcha.jpg" width="140" height="70"/></span>
		</p>
		<p>
			<label></label>
			<input id="PersistentCookie" type="checkbox" name="PersistentCookie" value="yes" checked="checked" tabindex="6"/> <label class="nocmx" for="PersistentCookie"><i18n:message key="user.login.persisten"/></label>
		</p>
		<hr />
		<p>
		<label><i18n:message key="register.terms" /></label>
		<textarea onfocus="this.rows=10" rows="8" cols="45" readonly="readonly" type="_moz"><i18n:message key="register.terms.information" /></textarea>	
		</p>	
		<p>
			<label></label>
			<span><i18n:message key="register.accept" /></span>
		</p>	
		<p>
			<label></label>
			<input class="submit"  type="submit" value="<i18n:message key="register.submit" />" tabindex="7" />
		</p>

	</fieldset>
</form>
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
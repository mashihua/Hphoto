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
String my = resourceBundle.getString("user.public.insert");

String rememberme = WebUtil.getCookieValue(request.getCookies(),"rememberme","false");
String cuser = WebUtil.getCookieValue(request.getCookies(),"name",null);
String user = request.getParameter("user");

boolean login = false;
boolean currentUser = false;

if( user == null || user.equals("")){
	response.setStatus(404);
	request.getRequestDispatcher("/hp/error?u="+request.getRequestURI()).forward(request, response);
	return;
}

if(rememberme.equals("true")){
	login = true;
}

//determine the user is album owner
UserProfile[] profile = server.getUser(user,1);
if(profile == null || profile.length == 0){
	response.setStatus(404);
	request.getRequestDispatcher("/hp/error?u="+request.getRequestURI()).forward(request, response);
	return;
}else if(cuser != null){
	if(profile[0].getNicename().equals(cuser)){
		currentUser = true;
	}
}

Category[] categories = server.getCategories(user,true,0);
int length = categories.length;
String sort = request.getParameter("sort");
boolean sorted = false;
if(sort != null && sort.equals("date")){
	for(int i = 0 ; i < length ; i++){
		categories[i].setSort(1);
	}
	Arrays.sort(categories);
	sorted = true;
}
String userImg = "/image/"+user+"/"+"AHHYILU"+"/s48-c/"+user;
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
<title><i18n:message key="site.title"/> - <%=user%> </title>
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
<div id="navbar"> 
<div class="left">
<%
if(login){ 
%>
<a href="/home"><i18n:message key="user.myhome"/></a>
<span><i18n:message key="user.publicuri"> <i18n:messageArg value="<%= my %>"/></i18n:message></span>
<%}else{%>
<a href="/<%= user %>"><i18n:message key="user.publicuri"> <i18n:messageArg value="<%= user %>"/> </i18n:message></a>
<%}%>
</div>
<div class="right"> </div>	
</div> 
<table>
<tr>
<td width="100%">
<div>
<div class="prettybox">
		<div id="l_header">
			<div class="left">
			<i18n:message key="home.albumnumber">
			<i18n:messageArg value="<%=length%>"/>
			</i18n:message> 			
			<!-- <i18n:message key="home.sortby" /> <%if(sorted){%><a href="/<%=user %>?sort=album" /><% }%><i18n:message key="home.sortbycreate" /> <%if(sorted){%></a><% }%>| <%if(!sorted){%><a href="/<%=user %>?sort=date" /><% }%><i18n:message key="home.sortbyupload" /><%if(!sorted){%></a><% }%>-->
			</div>
			<div class="right"></div>
		</div>
	
		<div class="l_body">
			<div id="albums">
			<% 
			java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat(resourceBundle.getString("data.format"));
			for(Category category:categories){
				String name = category.getName();
				String lablename = category.getLablename();
				String imgurl = category.getImgurl();
				String id = imgurl.substring(imgurl.lastIndexOf('/')+1,imgurl.lastIndexOf('.'));
				String img = "/image/"+user+"/"+lablename+"/s"+160+"-c/"+id+".jpg";
				String date = dateFormatter.format(category.getCreatdate());
				String url = "/"+user+"/"+lablename;
				int count = category.getCount();
				%>
				<fieldset>
				<p class="album"><a href="<%=url%>"> <img src="<%=img%>" width="160" height="160" /> </a></p>
		  		<p><%=name%>(<%=count%>)</p>
		  		<p><%=date%></p>
				</fieldset>
			<%} %>
			</div>
			<%if(categories.length >0){%>
				<div class="rss"><a href="/data/feed/base/user/<%=user%>?kind=album&alt=rss"><i18n:message key="rss" /></a></div>
			<%}%>	
		</div>
		
</div>

</td>
<td>
<div>
	<div class="widgets paddingLeft">
		<div class="widget paddingLeft" id="userWidget">
			<table>
			<tr>
			<td><a href="/<%=user%>"><img src="<%=userImg %>" width="48" height="48" /></a></td>
			<td> <%= user %></td>
			</tr>
			</table>
			<!--
			<%if(currentUser){%>
			<div class="wb_boder"></div>			
			<ul>
			<li><a href="#"><i18n:message key="editprofile" /></a></li>
			</ul>
			<%}%>
			-->
		</div>	
		<div class="widget paddingLeft" id="promoWidget">
		<h3><i18n:message key="index.features" /></h3>
		<p><i18n:message key="index.features.introduce" /></p>
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
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
  import="org.apache.hadoop.util.*"
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
String user = WebUtil.getCookieValue(request.getCookies(),"name",null);
boolean login = true;

if(!rememberme.equals("true")){
	response.sendRedirect("/hp/login");
	return;
}

if(user == null || user.equals("")){
	response.sendRedirect("/hp/login");
	return;
}

UserProfile[] profile = server.getUser(user,1);

if(profile == null){
	response.sendRedirect("/hp/register");
	return;
}else if(profile.length == 0){
	response.sendRedirect("/hp/register");
	return;
}else if(!profile[0].getNicename().equals(user)){
	response.sendRedirect("/hp/register");
	return;
}


Category[] categories = server.getCategories(user);
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

//StringUtils.formatPercent();


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
<link href="style/calendar.css" rel="stylesheet" type="text/css" media="all"/>
<script src="script/jquery.js" type="text/javascript"></script>
<script src="script/cmxforms.js" type="text/javascript"></script>
<script src="script/validate.js" type="text/javascript"></script>
<script src="script/jqModal.js" type="text/javascript"></script>
<script src="script/calendar.js" type="text/javascript"></script>
<script type="text/javascript">
hphoto.tip({element:"#header a:last",cookie:"newTo",title:'<%=resourceBundle.getString("newto.tip")%>',content:'<%=resourceBundle.getString("newto.content")%>'});
$().ready(function() {
	var index = 200;
  $('#ex').jqm({ajax: '/hp/addAlbum', trigger: '#newalbum',
  	zIndex:index,					
  	onLoad:function(){
		<i18n:message key="calendar.setting"/>
  		$('#dateinput').calendar({autoPopUp: 'focus',currentText: 'Now', appendText: '<i18n:message key="calendar.setting.appendText"/>',dateFormat:"YMD-"});
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
	<span><i18n:message key="user.myhome"/></span>
	<a href="/<%= user %>"><i18n:message key="user.publicuri"> <i18n:messageArg value="<%=my%>"/></i18n:message></a>
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
			<i18n:message key="home.sortby" /> 
			<%if(sorted){%><a href="/home?sort=album" /><% }%><i18n:message key="home.sortbycreate" /> <%if(sorted){%></a><% }%>| <%if(!sorted){%><a href="/home?sort=date" /><% }%><i18n:message key="home.sortbyupload" /><%if(!sorted){%></a><% }%>
			</div>
			<div class="right"><a id="newalbum" href="/hp/addAlbum"><i18n:message key="home.newalbum" /></a></div>
		</div>
	
		<div class="l_body">
			<div id="albums">
			<% 
			java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat(resourceBundle.getString("data.format"));
			for(Category category:categories){
				boolean opened = category.isOpened();
				String name = category.getName();
				String lablename = category.getLablename();
				String imgurl = category.getImgurl();
				String id = imgurl.substring(imgurl.lastIndexOf('/')+1,imgurl.lastIndexOf('.'));
				String img = "/image/"+user+"/"+lablename+"/s"+160+"-c/"+id+".jpg";
				String date = dateFormatter.format(category.getCreatdate());
				String url = "/"+user+"/"+lablename+(opened?"":"?authKey="+category.getAuthkey());
				int count = category.getCount();
				%>
				<fieldset>
				<p class="album"><a href="<%=url%>"> <img src="<%=img%>" width="160" height="160" /> </a></p>
		  		<p><%=name%>(<%=count%>)</p>
		  		<p><%=date%><% if(!opened){%> <span class="unlist"><i18n:message key="home.unlisted" /></span><%}%></p>
				</fieldset>
			<%} %>
			</div>
			<% if(categories.length >0){%>
			<div class="rss"><a href="/data/feed/base/user/<%=user%>?kind=album&alt=rss&acess=all"><i18n:message key="rss" /></a></div>
			<%} %>	
		</div>
	
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
			<div class="wb_boder"></div>
			<ul>
			<li><a href="#"><i18n:message key="editprofile" /></a></li>
			</ul>
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
<div id="ex" class="window">
<div id="addalbum_container">
Please wait...
<img alt="loading" src="images/indicator.gif"/>
</div>
</div>
<%
UserProfile up = profile[0];
long totalSpace = up.getAvlidSpace();
long usedSpace = up.getUesdeSpace();
String percent = StringUtil.formatPercent((double)usedSpace/totalSpace,1);
String className = ((usedSpace/totalSpace) > 0.95) ? "red" :"green";
%>
<div id="footer">
<p id="quota" class="<%=className%>"><i18n:message key="footer.available">
  <i18n:messageArg value="<%= StringUtil.humanReadableInt(usedSpace) %>"/>
  <i18n:messageArg value="<%=percent%>"/>
  <i18n:messageArg value="<%= StringUtil.humanReadableInt(totalSpace) %>"/>
</i18n:message></p>
<a target="_blank" href="#"><i18n:message key="footer.terms"/></a> -
<a target="_blank" href="#"><i18n:message key="footer.policy"/></a> -
<a target="_blank" href="#"><i18n:message key="footer.contact"/></a> 
</div>
</body>
</html>
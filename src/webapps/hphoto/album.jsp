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
<%! 
private int[] getWidthAndHeight(int width,int height,int max){
	int[] value = new int[2];
	float rate = (float)width/(float)height;
	if(rate > 1){
				value[0] = max;
				value[1] = (int)(max/rate);
	}else{
				value[0] = (int) (max *rate);
				value[1] = max;
	}
	return value;
}
%>
<%

Configuration conf = (Configuration)application.getAttribute("hphoto.conf");
TableServer server = (TableServer)application.getAttribute("hphoto.tableServer");
String base = "http://" +request.getServerName() + (request.getLocalPort() == 80 ? "" : ":"+Integer.toString(request.getLocalPort()));
request.setCharacterEncoding("UTF-8");
ResourceBundle resourceBundle = ResourceBundle.getBundle("com.hphoto.message.web", request.getLocale());
String language = resourceBundle.getLocale().getLanguage();
base += resourceBundle.getString("site.base");
String my = resourceBundle.getString("user.public.insert");

String rememberme = WebUtil.getCookieValue(request.getCookies(),"rememberme","false");
String cuser = WebUtil.getCookieValue(request.getCookies(),"name",null);
String authKey = request.getParameter("authKey");
String categoryName = request.getParameter("category");
String user = request.getParameter("user");
String sort = request.getParameter("sort");

boolean login = false;
boolean currentUser = false;
UserProfile[] profile = null;

//determine the user is album owner
if(rememberme.equals("true")){
	login = true;
	if(user !=null && !user.equals("")){
		profile = server.getUser(user,1);
		if(profile != null && profile.length >0){
			if(profile[0].getNicename().equals(cuser)){
				currentUser = true;
			}
		}
	}
}

//determine the db have this category
if(categoryName == null || categoryName.equals("")){
	response.setStatus(404);
	request.getRequestDispatcher("/hp/error?u="+request.getRequestURI()).forward(request, response);
	return;
}

if(user == null || user.equals("")){
	response.setStatus(404);
	request.getRequestDispatcher("/hp/error?u="+request.getRequestURI()).forward(request, response);
	return;
}

Category category = server.getCategory(user,categoryName);

if(category == null){
	response.setStatus(404);
	request.getRequestDispatcher("/hp/error?u="+request.getRequestURI()).forward(request, response);
	return;
}


//determine the browser have current auth key
if(!category.isOpened()){
	if(!category.getAuthkey().equals(authKey)){
		response.setStatus(404);
		request.getRequestDispatcher("/hp/error?u="+request.getRequestURI()).forward(request, response);
		return;
	}
}


int length = 0;
Image[] images = server.getImages(user,categoryName);
length = images.length;

boolean sorted = false;
if(sort != null && sort.equals("date")){
	for(int i = 0 ; i < length ; i++){
		images[i].setSort(1);
	}
	Arrays.sort(images);
	sorted = true;
}

String userImg = "/image/"+user+"/"+"AHHYILU"+"/s48-c/"+user;
String imgurl = category.getImgurl();
String id = imgurl.substring(imgurl.lastIndexOf('/')+1,imgurl.lastIndexOf('.'));
String albumImg = "/image/"+user+"/"+categoryName+"/s"+160+"-c/"+id+".jpg";
String name = category.getName();
int count = category.getCount();
java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat(resourceBundle.getString("data.format"));
String date = dateFormatter.format(category.getCreatdate());
boolean opened = category.isOpened();
long usedSpace = category.getUsedSpace();
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
<title><i18n:message key="site.title"/>  - <%=user%>  - <%=name%> </title>
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
#right{
float: right;
margin: 2px 0px 2px 0px;
padding:0px;
width: 79%;
text-align:left;
}
#left{
float: left;
margin: 2px 2px 0px 0px;
padding: 0px;
background: #F2F3F7;
width: 19%;
text-align:left;
}

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
<div id="navbar"> 
<div class="left">
<%
if(login){ 
%>
<a href="/home"><i18n:message key="user.myhome"/></a>
<a href="/<%= user %>"><i18n:message key="user.publicuri"> <i18n:messageArg value="<%= currentUser ? my : user %>"/></i18n:message></a>
<%}else{%>
<a href="/<%= user %>"><i18n:message key="user.publicuri"> <i18n:messageArg value="<%= user %>"/> </i18n:message></a>
<span><%= name %></span>
<%}%>
</div>
<div class="right"> </div>			
</div> 
<table>
<tr>
<td>
<div class="widgets paddingLeft">
	<div id="albumWidget" class="widget paddingLeft">
		<div id="albumdetails">
			<p class="albumthumb"><img src="<%=albumImg%>" width="160" height="160" /></p> 
			<ul>
			<li><%= name %></li>
			<li><i18n:message key="album.photo.count"><i18n:messageArg value="<%= count %>"/><i18n:messageArg value="<%= StringUtil.humanReadableSizeInt(usedSpace)   %>"/></i18n:message></li>
			<li><%= date %></li>
			<li><%if(!opened){%><span class="unlist"><i18n:message key="home.unlisted" /></span><%}else{ %><span class="public"><i18n:message key="home.public" /></span><%} %></li>	
			</ul>
		</div>
	<%if(currentUser){ %>
	<div class="wb_boder"></div>
	<ul>
		<li><a href="/hp/webupload?user=<%=user%>&lable=<%=categoryName%>"><i18n:message key="upload" /></a></li>	
	</ul>
	<%} %>
	</div>

	<div id="userWidget" class="widget paddingLeft">
	<table>
	<tr>
	<td><a href="/<%=user%>"><img src="<%=userImg %>" width="48" height="48" /></a></td>
	<td> <%= user %></td>
	</tr>
	</table>
	</div>

</div>
</td>
<td id="hp_content" width="100%" style="height: 100%;">
<div id="l_header">
			<div class="left">  </div>
			<div class="right"> </div>
</div>
<div class="l_body">
<% if(length > 0){%>
<div id="photos">
<% 
int max = 144;
for(Image image:images){
		String href = "/"+user+"/"+categoryName+"/"+image.getId()+(authKey!=null&&!authKey.equals("")?"?authKey="+authKey:"");
		String imgUri = "/image/"+user+"/"+categoryName+"/s"+max+"/"+image.getId()+".jpg";	
		int[] value = getWidthAndHeight(image.getWidth(),image.getHeight(),max);
		String top ="";
		if(value[1] < max){
			top = "top:"+(max -value[1])+"px;";
		}							

%>
<fieldset>
<p class="photo" style="<%=top %>"><a href="<%=href%>"> <img src="<%=imgUri%>" width="<%= value[0] %>" height="<%= value[1] %>" /></a></p>
</fieldset>
<%} %>
</div>
<div class="rss"><a href="/data/feed/base/user/<%=user%>/album/<%=categoryName%>?kind=photo&alt=rss<%=(category.getAuthkey() != null ? "&authKey="+ category.getAuthkey(): "") %>"><i18n:message key="rss" /></a></div>		
<% 
}else{%>
<div>
<i18n:message key="album.noPhoto"><i18n:messageArg value="<%= user %>"/><i18n:messageArg value="<%= categoryName %>"/></i18n:message>			
</div>		
<%} %>
</div>
</td>
</tr>
</table>

<div id="clear"></div>
</div>
<div id="footer">
<a target="_blank" href="#"><i18n:message key="footer.terms"/></a> -
<a target="_blank" href="#"><i18n:message key="footer.policy"/></a> -
<a target="_blank" href="#"><i18n:message key="footer.contact"/></a> 
</div>
</body>
</html>
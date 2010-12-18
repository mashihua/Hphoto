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
  import="com.hphoto.*"
  import="com.hphoto.util.*"
  import="com.hphoto.server.*"
  import="com.hphoto.bean.*"
  import="java.util.regex.*"
%>
<%
request.setCharacterEncoding("UTF-8");
Configuration conf = (Configuration)application.getAttribute("hphoto.conf");
TableServer server = (TableServer)application.getAttribute("hphoto.tableServer");

String title = request.getParameter("title");
String date = request.getParameter("date");
String description = request.getParameter("description");
String location = request.getParameter("location");
String access = request.getParameter("access");
String user = request.getParameter("user");
String redir = request.getParameter("redir");



String rememberme = WebUtil.getCookieValue(request.getCookies(),"rememberme","false");
String cuser = WebUtil.getCookieValue(request.getCookies(),"name",null);

if(request.getSession().getAttribute("login") == null){
	response.sendRedirect("/hp/login");
}
if(!request.getSession().getAttribute("login").equals("true")){
	response.sendRedirect("/hp/login");
}

if(!rememberme.equals("true")){
	response.sendRedirect("/hp/login");
}

if(user == null || cuser == null || !cuser.equals(user)){
	response.setStatus(404);
	request.getRequestDispatcher("/hp/error?u="+request.getRequestURI()).forward(request, response);
}
if(title == null||title.equals("")){
	response.setStatus(404);
	request.getRequestDispatcher("/hp/error?u="+request.getRequestURI()).forward(request, response);
}
if(date == null || date.equals("")){
	response.setStatus(404);
	request.getRequestDispatcher("/hp/error?u="+request.getRequestURI()).forward(request, response);
}

Date createDate = new Date();
DateFormat df = DateFormat.getDateInstance();
try{
	createDate = df.parse(date);
}catch(ParseException e){
}


Category[] categories = server.getCategories(user);
Category category = new Category();
category.setCount(0);
category.setCreatdate(createDate);
category.setDescription(description);
category.setName(title);
category.setLocation(location);
category.setSort(categories.length);
category.setOwner(user);
category.setImgurl(FConstants.DEFAULT_ALBUMIMAGE);
String lableName = KeyUtil.getKey(title).toString();
category.setLableName(lableName);

for(Category cate : categories){	
		if(cate.getLablename().equals(lableName)){
			category.setLableName(KeyUtil.getKey(title+new Date().toString()).toString());
			break;
		}
}

if(redir == null || redir.trim().equals("")){
	redir = "/"+user+"/"+category.getLablename();
}

boolean opened = access.equals("public")?true:false;
category.setOpened(opened);
if(!opened){
	String authKey = KeyUtil.getAuthKey().toString();
	category.setAuthkey(authKey);
	redir = redir.indexOf("/"+user) == 0 ? redir + "?authKey="+authKey : redir;
}
server.setCategory(user,new Category[]{category});
response.sendRedirect(redir);
%>
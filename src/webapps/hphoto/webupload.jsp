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
  import="com.drew.metadata.exif.ExifDirectory"
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
String rememberme = WebUtil.getCookieValue(request.getCookies(),"rememberme","false");

String user = request.getParameter("user");
String lable = request.getParameter("lable");
String referrer = request.getHeader("referer");
String redir = request.getParameter("redir");
boolean login = false;

boolean currentUser = false;

if(cuser == null){
	response.sendRedirect("/hp/login");
}

if(user==null || user.equals("")){
	response.setStatus(404);	
	request.getRequestDispatcher("/hp/error?u="+request.getRequestURI()).forward(request, response);
}
if(!user.equals(cuser)){
	response.setStatus(404);
	request.getRequestDispatcher("/hp/error?u="+request.getRequestURI()).forward(request, response);
}

if(!"true".equals(request.getSession().getAttribute("login"))){
	response.sendRedirect("/hp/login");
}

UserProfile[] profile = null;
UserProfile up = null;
//determine the user is album owner
if(rememberme.equals("true")){
	login = true;
	if(user !=null && !user.equals("")){
		profile = server.getUser(user,1);
		if(profile != null && profile.length >0){
			if(profile[0].getNicename().equals(cuser)){
				currentUser = true;
				up = profile[0];
			}
		}
	}
}

if(!login || !currentUser){
	response.setStatus(404);	
	request.getRequestDispatcher("/hp/error?u="+request.getRequestURI()).forward(request, response);
}

if(referrer == null||referrer.equals("")){
	referrer = "/home";
}
if(redir != null){
	referrer = redir;
}


Category category = server.getCategory(user,lable);
if(category == null){
	response.setStatus(404);
	request.getRequestDispatcher("/hp/error?u="+request.getRequestURI()).forward(request, response);
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
<title><i18n:message key="site.title"/> - <%=category.getName()%></title>
<meta name="keywords" content="" />
<meta name="description" content="" /> 
<link rel="shortcut icon" href="<%=base%>favicon.ico" type="image/x-icon" /> 
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
#uploadcontent fieldset{
	margin:2em 10em 2em 2em;
	border:1px solid #C1D9FF; 
	background: #E1ECFE ;
}
#uploadcontent fieldset legend{  
background:#E1ECFE none repeat scroll 0%;
border:1px solid #C1D9FF;
font-weight:bold;
margin-left:-0.6em;
margin-top:-1.9em;
padding:0.2em 0.5em;
}
.red {
color:red;
} 
.gray{
color:#7F7F7F;
font-size:1.2em;
font-weight:bold;
}
.black{
color:#333333;
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
	<% 
	long totalSpace = up.getAvlidSpace();
	long usedSpace = up.getUesdeSpace();
	int count = category.getCount();
	int max = 500;
	%>
<div id="contain">
	
<div id="mainbg">
	<div id="navbar">
		<div class="left gray">
		<i18n:message key="upload.nav"/><span class="black"><%= category.getName() %></span>
		</div>
		<div class="right"> </div>	
	</div> 
<table>
<tr>
<td width="100%">
	<div>
		<div class="prettybox">
			<div class="l_body">
				<div style="display:none;">
				<i18n:message key="upload.total.invalid"></i18n:message>
				</div>
				<%
				String perStyle="display:none;";
				String uploadStyle = "";
				if(count > max) 
				{
				perStyle="";
				uploadStyle = "display:none;";
				}
				%>
				<div style="<%=perStyle%>">
				<i18n:message key="upload.per.invalid"><i18n:messageArg value="<%= max %>"/><i18n:messageArg value="<%= referrer %>"/></i18n:message>
				</div>
				<div style="<%=uploadStyle%>">
				<form id="uploadcontent" name="uploadcontent" action="/hp/addPhotos" method="post" enctype="multipart/form-data" > 
				<input type="hidden" name="uname" value="<%=user%>" />
				<input type="hidden" name="category" value="<%=lable%>" />
				<input type="hidden" name="redir" value="<%=referrer%>" />
				<input type="hidden" name="num" id="num"/>
				<fieldset> 
					<legend><i18n:message key="upload.title" /></legend>
					<p class="left bold"><i18n:message key="upload.selectphoto" /></p>  <p class="right"><input type="reset" value="<i18n:message key="upload.clear" />" id="clearall" onclick="resetForm();" />
					<input type="button"
				                     value="<i18n:message key="upload.cancel" />"
				                     id="cancel"
				                     onclick="window.location.href='<%=referrer%>'" /></p>
					<p id="mexd"></p> 
					<p id="files"></p> 
					<p><input type="submit" class="bold" id="startbutton" value="<i18n:message key="upload.submit" />" /></p>
				</fieldset> 
				</form>
				</div>
</div> 
</div>
</div>				
<script type="text/javascript"> 
function get(name){
	return document.getElementById(name);
}

(function() {
var photocount = <%=count%>;
var maxphotos = <%=max%>;
var available_slots = maxphotos - photocount;
var filecount = Math.min(available_slots, 5);
if (filecount > 0) {
    var result = "";
    var id = "";
    for(var i = 0; i < filecount; i++) {
      result += '<input size="32" name="u_file' + i +
          '" type="file" onchange="validateFile(this);"/>' +'<span id="u_file' + i + '" class="red"></span><br />\n';
}
var ele = get("files");
if(ele)
	ele.innerHTML = result;
}
})(); 

function validateFile(field) {
var notwhitespace = field.value.search(/\S+/g);
if (notwhitespace >= 0) {
if (verify(field.value)) {
clearDisplayErrors(field);
get("startbutton").disabled = false;
updateDisplay();
} else {
invalidFileError(field);
updateDisplay();
}
} else {
updateDisplay();
if (get(field.name).innerHTML != "") {
clearDisplayErrors(field);
}
}
}
function updateDisplay() {
var filecount = 0;
var badfilecount = 0;
var f = get("uploadcontent");
for (var i = 0; i < f.elements.length; i++) {
    var field = f.elements[i];
    if (field.type != 'file') continue;
    if (verify(field.value)) { 
      filecount++;
    } else if (field.value.search(/\S+/g) >= 0) {
	badfilecount++;
}
}
if (filecount==0) {
get("startbutton").disabled = true;
if (badfilecount==0) get("clearall").disabled = true;
} else {
}
}
function resetForm() {
var f = get("uploadcontent");
f.reset();
for(var i = 0; i < f.elements.length; i++) {
    e = f.elements[i];
    e.disabled = false;    
    if (e.type == 'file' && e.value != '') {
      location.reload(true);
    }
  }
  
}

function invalidFileError(field) {
  if (get("clearall").disabled) get("clearall").disabled = false;
  field.style.color = "#FF0000";
  $("#"+field.name).html('<i18n:message key="upload.invalid.file" />');
}

function clearDisplayErrors(field) {
  if (field) {
    $("#"+field.name).html("");
    field.style.color = "#000000";
  } else {
    var f = get("uploadcontent");
    for (var i = 0; i < f.elements.length; i++) {
      var field = f.elements[i];
      if (field.type != 'file') continue;    
      if (get(field.name).innerHTML != "") {
        $("#"+field.name).html("");
        field.style.color = "#000000";
      }
    }
  }
}

function validateForm(form) {
  var filecount = 0;
  var badfilecount = 0;
  for (var i = 0; i < form.elements.length; i++) {
    var field = form.elements[i];
    if (field.type != 'file' || field.value.match(/^\s*$/)) continue;
    if (verify(field.value)) {
      filecount++;
      continue;
    } else {
      badfilecount++;
    }
  }
  if (badfilecount) {
    alert('<i18n:message key="upload.invalid.submit" />');
  } else {
    get("lhid_num").value = filecount;
    get("startbutton").value = '<i18n:message key="upload.uploading" />';
    get("startbutton").disabled = true;
    get("clearall").disabled = true;
    //get("cancel").disabled = true;
    return true;
  }
  return false;
}

function verify(str) {
  if (str.search(/\S+/g) >= 0) {
var ext = str.toLowerCase().match(/\.[^.]+$/); // Match file extension.
if ((ext) && (ext.length > 0)) {
ext = ext.toString().slice(1); // Strip the "." character.
var extensions = ["jpe", "jpg", "jpeg"];
for (var i = 0; i < extensions.length; i++) {
        if (ext == extensions[i]) {
          return true;
        }
      }
    }
  } 
  return false;
}
</script>

</td>
<td>

<div>
	

		<div class="widgets paddingLeft">		
				<div class="widget paddingLeft" id="albumWidget">
					<ul>
					<li><i18n:message key="upload.current"><i18n:messageArg value="<%= count %>"/></i18n:message></li>
					<li><i18n:message key="upload.max"><i18n:messageArg value="<%= max %>"/></i18n:message></li>
					</ul>	
				</div>		
				<div class="widget paddingLeft" id="storageWidget">
					<p><i18n:message key="upload.storage" /></p>
					<ul>
					<li><i18n:message key="upload.totalStorage"><i18n:messageArg value="<%= StringUtil.humanReadableInt(totalSpace) %>"/></i18n:message></li>
					<li><i18n:message key="upload.used"><i18n:messageArg value="<%= StringUtil.humanReadableInt(usedSpace) %>"/></i18n:message></li>
					<li><i18n:message key="upload.remaining"><i18n:messageArg value="<%= StringUtil.humanReadableInt(totalSpace - usedSpace) %>"/></i18n:message> </li>
					</ul>
				</div>
		</div>
	</div>
</td>
</tr>
<table>
	

</div>
<div id="clear"></div>
<div id="footer">
<a target="_blank" href="#"><i18n:message key="footer.terms"/></a> -
<a target="_blank" href="#"><i18n:message key="footer.policy"/></a> -
<a target="_blank" href="#"><i18n:message key="footer.contact"/></a> 
</div>
</body>
</html>
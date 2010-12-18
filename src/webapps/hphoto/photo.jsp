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
  import="com.drew.metadata.Directory"
  import="com.drew.metadata.exif.PentaxMakernoteDirectory"
  import="java.text.DecimalFormat"
  import="com.drew.imaging.PhotographicConversions"
  import="com.drew.lang.Rational"
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

public String getFlashDescription(int val,ResourceBundle bundle){
        /*
         * This is a bitmask.
         * 0 = flash fired
         * 1 = return detected
         * 2 = return able to be detected
         * 3 = unknown
         * 4 = auto used
         * 5 = unknown
         * 6 = red eye reduction used
         */
        String value = "";
		switch(val){
			case 0: value = bundle.getString("photo.exif.flash.on"); break;
			case 6: value = bundle.getString("photo.exif.flash.reduction"); break;
			default: value = bundle.getString("photo.exif.flash.off");
		
		}
        return value;
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
String my = resourceBundle.getString("user.public.insert");


String rememberme = WebUtil.getCookieValue(request.getCookies(),"rememberme","false");
String cuser = WebUtil.getCookieValue(request.getCookies(),"name",null);
String authKey = request.getParameter("authKey");
String categoryName = request.getParameter("category");
String user = request.getParameter("user");
String sort = request.getParameter("sort");
String id = request.getParameter("id");

boolean login = false;
boolean currentUser = false;

//determine the user is album owner
if(rememberme.equals("true")){
	login = true;
	if(user !=null && !user.equals("")){
		UserProfile[] profile = server.getUser(user,1);
		if(profile != null && profile.length >0){
			if(profile[0].getNicename().equals(cuser)){
				currentUser = true;
			}
		}
	}
}

//determine the db have this category
if(categoryName==null || categoryName.equals("")){
	response.setStatus(404);
	request.getRequestDispatcher("/hp/error?u="+request.getRequestURI()).forward(request, response);
	return;
}

Category category = server.getCategory(user,categoryName);
if(category==null){
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


Image[] images = server.getImages(user,categoryName);
String[] ids = new String[images.length];
Image image = null;
int now = 0;
int length = images.length;
for(int i = 0;i < length ;i++){
	ids[i] = images[i].getId();
	if(ids[i].equals(id)){
		now = i;
		image = images[i];
	}
}

if(image == null){
	response.setStatus(404);
	request.getRequestDispatcher("/hp/error?u="+request.getRequestURI()).forward(request, response);
	return;
}

Exif exif = server.getExif(user,image);
Comment[] comments = server.getComment(user,image);
Tags[] tags = server.getTags(user,image);
Album[] albums = server.getAlbum(user,image);

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
<title><i18n:message key="site.title"/> - <%=user%> - <%=category.getName()%> - <%= image.getFileName() %></title>
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
<script>
$().ready(function(){
	$('#infoToogle').toggle(
		function(){
			$(this).html('<i18n:message key="photo.hideExif" />');
			$('#exif').show();
		},function(){
			$(this).html('<i18n:message key="photo.showExif" />');
			$('#exif').hide();
		}
	);
});
</script>
<style type="text/css">
#exif{
display:none;
}
#right{
float: right;
margin: 8px 0px 2px 0px;
padding:0px;
width: 19%;
text-align:left;
}
#left{
float: left;
margin: 2px 2px 0px 0px;
padding: 0px;
background: #F2F3F7;
width: 80%;
text-align:left;
}
#p_header{
background:#F0F0F0 none repeat scroll 0%;
border-collapse:collapse;
border-color:#CCCCCC;
border-style:solid;
border-width:0px 0px 1pt;	
}
.p{
margin:8px;
}
.commentform{
margin-top:1em;
width:450px;
}
.commentarea{
border:1px solid #999999;
color:#B3B3B3;
font-family:arial,sans-serif;
font-size:1em;
font-size-adjust:none;
font-stretch:normal;
font-style:normal;
font-variant:normal;
font-weight:bold;
height:5em;
line-height:normal;
margin:0.2em 0pt;
padding:0.2em;
width:100%;
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
		<a href="/<%= user %>"><i18n:message key="user.publicuri"> <i18n:messageArg value="<%= my  %>"/></i18n:message></a>
		<%}else{%>
		<a href="/<%= user %>"><i18n:message key="user.publicuri"> <i18n:messageArg value="<%= user %>"/> </i18n:message></a>
		<span><%= category.getName() %></span>
		<%}%>
		</div>
		<div class="right"> </div>	
	
	</div> 
<% 
int max = 600;
String caption = image.getCaption();
String imgsrc = "/image/"+user+"/"+categoryName+"/"+id+".jpg?max="+max;
int[] value = getWidthAndHeight(image.getWidth(),image.getHeight(),max);
%>
<table>
<tr>
<td width="100%">
<div>	
		<div class="prettybox">		
				<div id="p_header">
					<table width="100%">
					<tr>
					<td width="50%">
						<table>
							<tr>
							<td width="100%"> <a href="/<%=user%>/<%=categoryName+(authKey!=null?"?authKey="+authKey:"")%>"><i18n:message key="photo.viewAlbum"></i18n:message></a></td>
							<td>
							<%if(now == 0) {%>
								<img src="images/left_ghosted.gif" width="87" height="18"/>
							<%}else{ %>
								<a href="<%= "/"+user+"/"+categoryName+"/"+images[now-1].getId()+(authKey!=null?"?authKey="+authKey:"")%>"><img src="images/left_normal.gif" width="87" height="18"/></a>
							<%} %>	
							</td>									
							</tr>
						</table>
					</td>
					<td width="50%">
						<table>
							<tr>
							<td>
							<%if(now == length-1) {%>
							<img src="images/right_ghosted.gif" width="87" height="18"/>
							<%}else{ %>
							<a href="<%="/"+user+"/"+categoryName+"/"+images[now+1].getId()+(authKey!=null?"?authKey="+authKey:"")%>"><img src="images/right_normal.gif" width="87" height="18"/></a>
							<%} %>
							</td>
							<td width="100%"></td>
							</tr>
						</table>
					</td>
					</tr>
					</table>
				</div>		
				<div class="l_body">
					<div id="photo">
						<div style="overflow: hidden; position: relative; min-width:<%=value[0]%>px; height: <%=value[1]%>px; text-align: center;">
						 	<img src="<%= imgsrc %>" width="<%=value[0]%>" height="<%=value[1]%>"/>
						 </div>
						 <% 				 	
							if(caption==null){
								if(currentUser){
								%>
							  	<p style="text-align:center; margin:2em 0 1em;"><a href="#"><i18n:message key="photo.addcaption"/></a></p>
							 	<%	
							 	}
						  	}else{ 
						  	//current user can modify this caption
						  	%>
						 	<p style="text-align:center; margin:2em 0 1em;"><%= caption %></p>
						 	<%} %>
					</div>
				</div>	
		</div>
	<%
	java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat(resourceBundle.getString("data.format"));
	if(comments != null && comments.length >0){ 
		for(Comment comment:comments){
			String url = comment.getLink();
			String owner = comment.getOwner();
			String commenter = comment.getCommenter();
			String info = comment.getComment();
			String cimg = comment.getUserimage();
			Date time = comment.getTimestamp();
	%>
		<table class="hp_comments">
		<tr class="">
		<td class="hp_commentimg" rowspan="2">
		<a href="/<%= commenter %>"><img src="<%= cimg %>"/></a>
		</td>
		<th width="100%"><a href="/<%= commenter %>"><%= commenter %></a></th><%=dateFormatter.format(time)%><th></th><th/>
		</tr>
		<tr><td class="hp_commenttext" colspan="3"><%= info %></td></tr>
		</table>
	<%
		}
	}
	%>
<% if(login){%>		
	<div class="p">
		<form class="commentform"> 
		<p><i18n:message key="photo.addcomment"/></p> 
		<textarea name="comment" class="commentarea"></textarea>
		<table>
		<tr>
		<td width="100%"></td>
		<td><input type="submit" onclick="javascript:alert('Not completed!');return false;" value="<i18n:message key="photo.comment.sumbit"/>" /></td>
		</tr>
		</table>
	 	</form>
	</div>
<%}else{%>		
	<div class="p">
		<i18n:message key="ptoto.addcomment.nologin"/>
	</div>
	
<%} %>
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
		</div>		
	</div>
	
	<div class="widgets paddingLeft">
		<div class="widget paddingLeft" id="exifWidget">
		<% 
		String date = dateFormatter.format(image.getTimestamp());
		int count = category.getCount();
		int height = image.getHeight();
		int width = image.getWidth();
		int size = image.getKbytes();
		String category_Name = category.getName();
		String fileName = image.getFileName();
		%>
		<ul>
		<li><i18n:message key="photo.count"><i18n:messageArg value="<%= now + 1 %>"/><i18n:messageArg value="<%= count %>"/></i18n:message></li>
		<li><%= category_Name %></li>
		<li style="color:#888;"><%= date %></li>
		<li style="color:#888;"><i18n:message key="photo.size"><i18n:messageArg value="<%= width %>"/><i18n:messageArg value="<%= height %>"/><i18n:messageArg value="<%= StringUtil.humanReadableSizeInt(size*1024) %>"/></i18n:message></li>
		</ul>				
		<% 
		java.text.DecimalFormat simpleDecimalFormatter = new DecimalFormat("0.#");			
		
		String make=null,model=null,iso=null,exposure=null,aperture=null,focalLength=null,flashUsed=null;
		if(exif != null && exif.getDirector()!=null){
			Directory  exifDirectory = exif.getDirector();
			if(exifDirectory.containsTag(ExifDirectory.TAG_MAKE))make = exifDirectory.getString(ExifDirectory.TAG_MAKE); 
			if(exifDirectory.containsTag(ExifDirectory.TAG_MODEL))model = exifDirectory.getString(ExifDirectory.TAG_MODEL);
			if(exifDirectory.containsTag(ExifDirectory.TAG_ISO_EQUIVALENT))iso = exifDirectory.getString(ExifDirectory.TAG_ISO_EQUIVALENT);			
			if(exifDirectory.containsTag(ExifDirectory.TAG_FLASH))flashUsed  = getFlashDescription(exifDirectory.getInt(ExifDirectory.TAG_FLASH),resourceBundle);
			if(exifDirectory.containsTag(ExifDirectory.TAG_EXPOSURE_TIME))exposure = exifDirectory.getString(ExifDirectory.TAG_EXPOSURE_TIME) +" sec";
			if(exifDirectory.containsTag(ExifDirectory.TAG_FNUMBER)){
				 double aper = exifDirectory.getDouble(ExifDirectory.TAG_FNUMBER);
				 double fStop = PhotographicConversions.apertureToFStop(aper);
				 aperture = "f" + simpleDecimalFormatter.format(fStop);
			}					
			if(exifDirectory.containsTag(ExifDirectory.TAG_FOCAL_LENGTH)){
			 	 java.text.DecimalFormat formatter = new DecimalFormat("0.0##");
        	 	 Rational fl = exifDirectory.getRational(ExifDirectory.TAG_FOCAL_LENGTH);
				 focalLength = formatter.format(fl.doubleValue()) + "mm";
			}
		}
		%>
		<ul id="exif">
		<li><i18n:message key="photo.fileName"><i18n:messageArg value="<%= fileName %>"/></i18n:message></li>
		<li><i18n:message key="photo.make"><i18n:messageArg value="<%= make == null ? "n/a" : make %>"/></i18n:message></li>
		<li><i18n:message key="photo.model"><i18n:messageArg value="<%= model == null ? "n/a" : model %>"/></i18n:message></li>
		<li><i18n:message key="photo.iso"><i18n:messageArg value="<%= iso == null ? "n/a" : iso %>"/></i18n:message></li>
		<li><i18n:message key="photo.exposure"><i18n:messageArg value="<%= exposure == null ? "n/a" : exposure %>"/></i18n:message></li>
		<li><i18n:message key="photo.aperture"><i18n:messageArg value="<%= aperture == null ? "n/a" : aperture %>"/></i18n:message></li>
		<li><i18n:message key="photo.focalLength"><i18n:messageArg value="<%= focalLength == null ? "n/a" :focalLength%>"/></i18n:message></li>
		<li><i18n:message key="photo.flashUsed"><i18n:messageArg value="<%= flashUsed == null ? "n/a" : flashUsed%>"/></i18n:message></li>
		</ul>
		<div style="text-align: right;"><a href="#" id="infoToogle"> <i18n:message key="photo.showExif" /></a></div>
		<div class="wb_boder"></div>
		<ul>
		<%if(currentUser){%>
		<li><a href="/hp/webupload?user=<%=user%>&lable=<%=categoryName%>&redir=<%=java.net.URLEncoder.encode("/"+user+"/"+categoryName+(category.isOpened()?"":"?authKey="+category.getAuthkey()))%>"><i18n:message key="upload" /></a></li>
		<%} %>
		<%if(image.isAllowdownload()){ %>	
		<li><a href="<%="/image/"+user+"/"+categoryName+"/"+id+".jpg?imgdl=1"%>"><i18n:message key="photo.download" /></a></li>	
		<%} %>
		<%if(currentUser){ %>
		<li><a href="#"><i18n:message key="photo.delete" /></a></li>
		<%} %>	
		</ul>
		</div>	
		
		<div class="widget paddingLeft" id="tagWidget">
		<h4><i18n:message key="photo.tags.title" /></h4>
		<div class="wb_boder"></div>
		<%if(false){ %>
		<ul>
		<li></li>
		</ul>
		<%}%>
		<p style="text-align: right;"><a href="#"><i18n:message key="photo.tags.add" /></a></p>
		</div>
		<div class="widget paddingLeft" id="commentWidget">
		<p><a href="#"><i18n:message key="photo.addcomment" /></a></p>
		</div>
	</div>			
</div>

</td>
</tr>
<table>
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
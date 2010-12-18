/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hphoto.server;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.*;
import org.apache.hadoop.conf.Configuration;
import org.json.JSONException;
import org.w3c.dom.*;

import com.hphoto.bean.Category;
import com.hphoto.bean.Image;
import com.hphoto.bean.UserProfile;
import com.hphoto.util.I18nUtil;



import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


/** Present search results using A9's OpenSearch extensions to RSS, plus a few
 * Nutch-specific extensions. */   
public class ApiServlet extends HttpServlet {
  private static final Map NS_MAP = new HashMap();

  static {
    NS_MAP.put("opensearch", "http://a9.com/-/spec/opensearchrss/1.0/");
    NS_MAP.put("atom", "http://www.w3.org/2005/Atom");
    NS_MAP.put("photo", "http://www.pheed.com/pheed/");
    NS_MAP.put("media", "http://search.yahoo.com/mrss/");
  }
 
  private String key = "com.hphoto.message.api";
  
  private static final Set SKIP_DETAILS = new HashSet();
  static {
    SKIP_DETAILS.add("url");                   // redundant with RSS link
    SKIP_DETAILS.add("title");                 // redundant with RSS title
  }

  private Configuration conf;
  private TableServer server;
  
  public void init(ServletConfig config) throws ServletException {
	if(server != null){
		return;
	}
    try {
    	ServletContext context = config.getServletContext();
      	this.server = (TableServer)context.getAttribute("hphoto.tableServer");
      	this.conf = (Configuration)context.getAttribute("hphoto.conf");
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
  
  
  public void addCategory(Document doc,Element channel,HttpServletRequest request){
	  assert(request.getParameter("user") != null);
	  
	  String requestUrl = request.getRequestURL().toString();
	  String base = "http://" +request.getServerName() + (request.getLocalPort() == 80 ? "" : ":"+Integer.toString(request.getLocalPort())); 
	  Category[] categories = null;
	  UserProfile[] users = null;
	  String owner = request.getParameter("user") ;
	  String feed = request.getParameter("feed");
	  String language = request.getParameter("hl");
	  String kind = request.getParameter("kind");
	  String albumid = request.getParameter("albumid");
	  //Local local = language == null ? request.getLocale() : new Locale(language);
	  boolean all = request.getParameter("acess") != null && request.getParameter("acess").equals("all");
	  boolean api = feed != null && feed.equals("api");
	  
	  
	  try {
		  users = server.getUser(owner,1);
		  categories = server.getCategories(owner);
	  } catch (IOException e) {		 
		  e.printStackTrace();
		  return;
	  }
	  
	  if(users ==null ||  users.length == 0){
		  return;
	  }
	  
	  addNode(doc, channel, "atom","id",base+"/feed/"+feed+"/user/"+owner);
	  addNode(doc, channel, "lastBuildDate",(new Date()).toString());
	  addNode(doc, channel, "title", owner +" "+ I18nUtil.getText(key,"title",request.getLocale()));
      addNode(doc, channel, "description", "");
      //add base,not complete
      addNode(doc, channel, "link", base + "/" + owner);
      addNode(doc, channel, "managingEditor" , owner);
      addNode(doc, channel, "generator", "hphoto.com");
      addNode(doc, channel, "opensearch", "totalResults", ""+(categories != null?categories.length:0));
      //addNode(doc, channel, "opensearch", "startIndex", ""+1);
      //addNode(doc, channel, "opensearch", "itemsPerPage", ""+1000);
      Element image = addNode(doc, channel, "image");
      addNode(doc, image, "url", base + "/image/"+owner+"/"+"AHHYILU"+"/s48-c/"+owner);
      addNode(doc, image, "title", owner + " " + I18nUtil.getText(key,"title",request.getLocale()));
      addNode(doc, image, "link",base + "/" + owner);
      
	  //channel
	  if(categories != null){
		  for(Category category:categories){
			  if(!all){
				  if(!category.isOpened() || category.getCount() < 1){
					  continue;
				  }
			  }
			  Element item = addNode(doc, channel, "item");
	    	  addNode(doc, item, "pubDate" , category.getCreatdate().toString());
	    	  addNode(doc, item, "atom", "updated", category.getLastupload().toString() );
	    	  addNode(doc, item, "title", category.getName());
	    	  addNode(doc, item, "description", getDescription(api,base,owner,category,I18nUtil.getLoacl(request)));
	    	  addNode(doc, item, "link", "");
	    	  addNode(doc, item, "author",owner);
	    	  if(api){
	    		  //api data here
	    	  }	    	  
	    	  Element media = addNode(doc,item,"media:group");
	    	  Element title = addNode(doc, media, "media", "title", category.getName());
	    	  addAttribute(doc, title, "type" , "plain");
	    	  
	    	  Element descript = addNode(doc, media, "media", "descript", "");
	    	  addAttribute(doc, descript, "type" , "plain");
	    	  
	    	  addNode(doc, media, "media", "keywords", "");
	    	  
			  String lablename = category.getLablename();
			  String imgurl = category.getImgurl();
			  String id = imgurl.substring(imgurl.lastIndexOf('/')+1,imgurl.lastIndexOf('.'));
			  String img = "/image/"+owner+"/"+lablename+"/"+id+".jpg";
			  String thumbnailImg = "/image/"+owner+"/"+lablename+"/s"+160+"-c/"+id+".jpg";
	    	  Element content = addNode(doc, media, "media", "content", "");
	    	  addAttribute(doc, content, "url" , base+img);
	    	  addAttribute(doc, content, "type", "image/jpeg");
	    	  addAttribute(doc, content, "medium", "image");
	    	  
	    	  Element thum = addNode(doc, media, "media", "thumbnail", "");
	    	  addAttribute(doc, thum, "url" , base+thumbnailImg);
	    	  addAttribute(doc, thum, "height", "160");
	    	  addAttribute(doc, thum, "width", "160");
	    	  
	    	  addNode(doc, media, "media", "credit", owner);
			  
		  }	    	  
	    	 
	  }
	  
  }
  
  public void addPhoto(Document doc,Element channel,HttpServletRequest request){
	  
	  assert(request.getParameter("user") != null);
	  assert(request.getParameter("album") != null);
	  
	  String requestUrl = request.getRequestURL().toString();
	  String base = "http://" +request.getServerName() + (request.getLocalPort() == 80 ? "" : ":"+Integer.toString(request.getLocalPort())); 
	  
	  Image[] images = null;
	  UserProfile[] users = null;
	  Category category = null;
	  String owner = request.getParameter("user") ;
	  String album = request.getParameter("album");
	  String authKey = request.getParameter("authKey");
	  
	  boolean api = request.getParameter("feed") != null && request.getParameter("feed").equals("api");	  
	  
	  try {
		  users = server.getUser(owner,1);
		  images = server.getImages(owner,album);
		  category = server.getCategory(owner,album);
	  } catch (IOException e) {
		  return;
	  }
	  
	  if(users ==null ||  users.length == 0){
		  return;
	  }
	  if(category==null){
		  return;
	  }
	  if(!category.isOpened()){
		  if(!category.getAuthkey().equals(authKey)){
			  return;
		  }
	  }
	  
	  addNode(doc, channel, "atom","id",base+"/feed/"+request.getParameter("feed")+"/user/"+owner+"/album/"+album);
	  addNode(doc, channel, "lastBuildDate",(new Date()).toString());
	  addNode(doc, channel, "title", owner  +" "+ I18nUtil.getText(key,"title",I18nUtil.getLoacl(request)));
      addNode(doc, channel, "description", "");
      //add base,not complete
      addNode(doc, channel, "link", base + "/" + owner +"/" + album +(category.getAuthkey() != null ? "?authKey="+ category.getAuthkey(): ""));
      addNode(doc, channel, "managingEditor" , owner);
      addNode(doc, channel, "generator", "hphoto.com");
      addNode(doc, channel, "opensearch", "totalResults", ""+(images != null?images.length:0));
      addNode(doc, channel, "opensearch", "startIndex", ""+1);
      addNode(doc, channel, "opensearch", "itemsPerPage", ""+1000);
      
      
      	String lablename = category.getLablename();
		String imgurl = category.getImgurl();
		String id = imgurl.substring(imgurl.lastIndexOf('/')+1,imgurl.lastIndexOf('.'));
		String img = "/image/"+owner+"/"+lablename+"/s"+160+"-c/"+id+".jpg";
		
      Element timage = addNode(doc, channel, "image");
      addNode(doc, timage, "url", base + img);
      addNode(doc, timage, "title", category.getName());
      addNode(doc, timage, "link",base + "/" + owner +"/" + lablename + (category.getAuthkey() != null ? "?authKey="+ category.getAuthkey(): ""));
      
      
      
      if(api){
    	  //api data here
      }
      
	  if(images != null){
		  //channel
	      for(Image image:images){
	    	  
	    	  Element item = addNode(doc, channel, "item");
	    	  addNode(doc, item, "pubDate" , image.getTimestamp().toString());
	    	  addNode(doc, item, "atom", "updated", image.getTimestamp().toString() );
	    	  addNode(doc, item, "title", image.getFileName());
	    	  addNode(doc, item, "description", getDescription(api,base,owner,category,image,I18nUtil.getLoacl(request)));
	    	  addNode(doc, item, "link", "");
	    	  addNode(doc, item, "author",owner);
	    	  if(api){
	    		  //api data here
	    	  }	    	  
	    	  Element media = addNode(doc,item,"media:group");
	    	  Element title = addNode(doc, media, "media", "title", image.getFileName());
	    	  addAttribute(doc, title, "type" , "plain");
	    	  
	    	  Element descript = addNode(doc, media, "media", "descript", "");
	    	  addAttribute(doc, descript, "type" , "plain");	    
	    	  
	    	  addNode(doc, media, "media", "keywords", "");
	    	  String img1 = base+"/image/"+owner+"/"+lablename+"/"+id+".jpg";
	    	  String thumbnailImg = base+"/image/"+owner+"/"+lablename+"/s"+288+"/"+image.getId()+".jpg";	
	    	  Element content = addNode(doc, media, "media", "content", "");
	    	  addAttribute(doc, content, "url" ,img1);
	    	  addAttribute(doc, content, "type", "image/jpeg");
	    	  addAttribute(doc, content, "medium", "image");
	    	  int[] value=getWidthAndHeight(image.getWidth(),image.getHeight(),288);
	    	  Element thum = addNode(doc, media, "media", "thumbnail", "");
	    	  addAttribute(doc, thum, "url" , thumbnailImg);
	    	  addAttribute(doc, thum, "height", Integer.toString(value[1]));
	    	  addAttribute(doc, thum, "width", Integer.toString(value[0]));
	    	  
	    	  addNode(doc, media, "media", "credit", owner);
	      }
	  }
  }
  
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

  
  private String getDescription(boolean isApi,String base,String owner,Category category,Locale locale){
	  if(isApi)
		  return "";
	  StringBuilder sb = new StringBuilder();
	  String lablename = category.getLablename();
	  String imgurl = category.getImgurl();
	  String id = imgurl.substring(imgurl.lastIndexOf('/')+1,imgurl.lastIndexOf('.'));
	  String thumbnailImg = base + "/image/"+owner+"/"+lablename+"/s"+160+"-c/"+id+".jpg";
	  java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat(I18nUtil.getText(key,"data.format",locale));
	  sb.append("<table><tr><td style=\"padding: 0 5px\">");
	  sb.append("<a href=\""+base+"/"+owner+"/"+category.getLablename()+(category.getAuthkey() != null ? "?authKey="+ category.getAuthkey(): "")+"\"><img src=\""+thumbnailImg+"\" style=\"border:1px solid #5C7FB9\" src=\"\" alt=\""+category.getName()+"\"/></a></td>");
	  sb.append("<td valign=\"top\"><p><font color=\"#333333\"></font></p>");
	  sb.append("<font color=\"#6B6B6B\">"+I18nUtil.getText(key,"date",locale)+ "</font><font color=\"#333333\">"+dateFormatter.format(category.getCreatdate())+"</font><br/>");
	  sb.append("<font color=\"#6B6B6B\"> "+I18nUtil.getText(key,"album.number",locale)+"</font><font color=\"#333333\">"+category.getCount()+"</font><br/>");
	  sb.append("<p><a href=\""+base+"/"+owner+"/"+category.getLablename()+(category.getAuthkey() != null ? "?authKey="+ category.getAuthkey(): "")+"\"><font color=\"#112ABB\">"+I18nUtil.getText(key,"view",locale)+"</font></a></p></td></tr></table>");
	  
	  return sb.toString();
  }
  
  private String getDescription(boolean isApi,String base,String owner,Category category,Image image,Locale locale){
	  if(isApi)
		  return "";
	  String imgUri = base + "/image/"+owner+"/"+category.getLablename()+"/s"+288+"/"+image.getId()+".jpg";	
	  StringBuilder sb = new StringBuilder();
	  java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat(I18nUtil.getText(key,"data.format",locale));
	  sb.append("<table><tr><td style=\"padding: 0 5px\">");
	  sb.append("<a href=\""+base+"/"+owner+"/"+category.getLablename()+"/"+image.getId()+(category.getAuthkey() != null ? "?authKey="+ category.getAuthkey(): "")+"\"><img src=\""+imgUri+"\" style=\"border:1px solid #5C7FB9\" src=\"\" alt=\""+image.getFileName()+"\"/></a></td>");
	  sb.append("<td valign=\"top\"><p><font color=\"#333333\"></font></p><font color=\"#6B6B6B\">"+I18nUtil.getText(key,"date",locale)+"</font><font color=\"#333333\">"+dateFormatter.format(image.getTimestamp())+"</font><br/>");
	  sb.append("<p><a href=\""+base+"/"+owner+"/"+category.getLablename()+"/"+image.getId()+(category.getAuthkey() != null ? "?authKey="+ category.getAuthkey(): "")+"\"><font color=\"#112ABB\">"+I18nUtil.getText(key,"view.photo",locale)+"</font></a></p></td></tr></table>");
	  return sb.toString();
  }
  
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    // get parameters from request
    request.setCharacterEncoding("UTF-8");

    // the query language
    String lang = request.getParameter("hl");   
    String kind = request.getParameter("kind");      
    String alt = request.getParameter("alt");    
	String owner = request.getParameter("user") ;
	String feed = request.getParameter("feed");
	String albumid = request.getParameter("album");
	if(lang != null){
		if(lang.indexOf('_') == -1){
			//throw
		}
		String language = lang.substring(0, lang.indexOf('_'));
		String count = lang.substring(lang.indexOf('_')+1);
	}
      

    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      Document doc = factory.newDocumentBuilder().newDocument();
 
      Element rss = addNode(doc, doc, "rss");
      addAttribute(doc, rss, "version", "2.0");
      addAttribute(doc, rss, "xmlns:opensearch",
                   (String)NS_MAP.get("opensearch"));
      addAttribute(doc, rss, "xmlns:atom", (String)NS_MAP.get("atom"));
      addAttribute(doc, rss, "xmlns:photo", (String)NS_MAP.get("photo"));
      addAttribute(doc, rss, "xmlns:media", (String)NS_MAP.get("media"));

      Element channel = addNode(doc, rss, "channel");

      if(kind.equals("album")){
    	  addCategory(doc,channel,request);
      }else if(kind.equals("photo")){
    	  addPhoto(doc,channel,request);
      }else{
    	  response.getOutputStream().println("Invalid paramenter.");
    	  return;
      }
      if(alt.equals("json")){
    	  String value = null;
		try {
			value = org.json.XML.toJSONObject(doc.toString()).toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 if(value != null)
    	  response.getOutputStream().print(value);
    	 return;
      }
      // dump DOM tree

      DOMSource source = new DOMSource(doc);
      TransformerFactory transFactory = TransformerFactory.newInstance();
      Transformer transformer = transFactory.newTransformer();
      transformer.setOutputProperty("indent", "yes");
      StreamResult result = new StreamResult(response.getOutputStream());
      response.setContentType("text/xml");
      transformer.transform(source, result);
      
    } catch (javax.xml.parsers.ParserConfigurationException e) {
      throw new ServletException(e);
    } catch (javax.xml.transform.TransformerException e) {
      throw new ServletException(e);
    }
      
  }

  private static Element addNode(Document doc, Node parent, String name) {
    Element child = doc.createElement(name);
    parent.appendChild(child);
    return child;
  }

  private static Element addNode(Document doc, Node parent,
                              String name, String text) {
    Element child = doc.createElement(name);
    child.appendChild(doc.createTextNode(getLegalXml(text)));
    parent.appendChild(child);
    return child;
  }

  private static Element addNode(Document doc, Node parent,
                              String ns, String name, String text) {
    Element child = doc.createElementNS((String)NS_MAP.get(ns), ns+":"+name);
    child.appendChild(doc.createTextNode(getLegalXml(text)));
    parent.appendChild(child);
    return child;
  }

  private static void addAttribute(Document doc, Element node,
                                   String name, String value) {
    Attr attribute = doc.createAttribute(name);
    attribute.setValue(getLegalXml(value));
    node.getAttributes().setNamedItem(attribute);
  }
  
  private static String parserText(final String text){
	  StringBuilder sb = new StringBuilder(text.length());
	  for(int i = 0 ; i < text.length() ;i++){		  
	  /*
	  & &amp;
	  < &it;
	  > &gt;
	  " &quot;
	  ' &apos;
	  */
		  char s = text.charAt(i);
		  switch(s){
			  case 38: 	sb.append("&amp;");
			  			break;
			  case 60:	sb.append("&it;");
			  			break;
			  case 62:	sb.append("&gt;");
			  			break;
			  case 34:	sb.append("&quot;");
			  			break;
			  case 39:	sb.append("&apos;");
			  			break;
			  default : sb.append(s);
			  
		  }
	  }
	  return sb.toString();
  }
  /*
   * Ensure string is legal xml.
   * @param text String to verify.
   * @return Passed <code>text</code> or a new string with illegal
   * characters removed if any found in <code>text</code>.
   * @see http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char
   */
  protected static String getLegalXml(final String text) {
      if (text == null) {
          return null;
      }
      StringBuffer buffer = null;
      for (int i = 0; i < text.length(); i++) {
        char c = text.charAt(i);
        if (!isLegalXml(c)) {
			  if (buffer == null) {
		              // Start up a buffer.  Copy characters here from now on
		              // now we've found at least one bad character in original.
			      buffer = new StringBuffer(text.length());
		              buffer.append(text.substring(0, i));
		          }
        	} else {
	           if (buffer != null) {
	             buffer.append(c);
	           }
        }
      }
      return (buffer != null)? buffer.toString(): text;
  }
 
  private static boolean isLegalXml(final char c) {
    return c == 0x9 || c == 0xa || c == 0xd || (c >= 0x20 && c <= 0xd7ff)
        || (c >= 0xe000 && c <= 0xfffd) || (c >= 0x10000 && c <= 0x10ffff);
  }

}

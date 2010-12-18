package com.hphoto.server;

import java.io.IOException;
import java.util.Date;

import org.apache.hadoop.hbase.HBaseClusterTestCase;

import com.hphoto.bean.Image;
import com.hphoto.bean.Category;
import com.hphoto.bean.Tags;
import com.hphoto.bean.UserProfile;
import com.hphoto.server.TableServer;
import com.hphoto.server.UidServer;
import com.hphoto.util.KeyUtil;
import com.hphoto.util.StringUtil;

public class TestTableServer extends HBaseClusterTestCase{
	private static String user = "beijing.josh@gmail.com";
	private static TableServer server;
	private static Image info;
	private static Category category;
	
	 public void setUp() throws Exception{	
		 super.setUp();	
		 (new Thread(new UidServer(conf))).start();				 
		 server = new TableServer(conf);
	 }
	 

	 public void testTableServer() throws IOException{		 
		 userTest();
		 imageCategoryTest();
		 imageInfoTest();
		 tagsTest();
		 imageTest();		 
	 }
	 
	 public void userTest() throws IOException{
		 
		 UserProfile[] ups = new UserProfile[3];
		 for(int i = 0 ; i < ups.length ; i++){
			 ups[i] = new UserProfile();
			 ups[i].setFirstname("josh");
			 ups[i].setLastname("song");
			 ups[i].setMail(i+"2@163.com");
			 ups[i].setPassword(StringUtil.MD5Encode("josh"+1));
			 ups[i].setImgurl("/u/image?u=josh"+i);
			 ups[i].setMailpublic(false);
			 ups[i].setNicename("hello");
		 }
		 
		 server.setUser(ups);
		 UserProfile[] upo = server.getUser("hello",1);
		 assertEquals("user lenght",1,upo.length);
		 System.out.println(upo[0].getImgurl());
		 //assertEquals("user get","/u/image?u=josh"+0,upo[0].getImgurl());
		 
	 }

	 public void imageCategoryTest() throws IOException{
		 Category[] images = new Category[5];
		 for(int i = 0 ; i < images.length ;i++){
			 images[i] = new Category();
			 images[i].setName("josh"+i);
			 images[i].setOpened(false);
			 images[i].setCreatdate(new Date(new Date().getTime() - i * 10000));
			 images[i].setLastupload(new Date(new Date().getTime() - i * 10000));
			 images[i].setImgurl("/u/image?p="+i);
			 images[i].setDescription("");
			 images[i].setLocation("");
			 images[i].setAuthkey(KeyUtil.getKey(images[i].getName(),9).toString());
			 
		 }
		 
		 server.setCategory(user, images);

		 Category[] other = server.getCategories(user);	

		 assertEquals("Image Category lenght:",5,other.length);

		 assertEquals("get category:","josh2",other[2].getName());
		 
		 for(Category i:other){
			 System.out.println(i);
			 i.setSortType(2);
			 i.setSort((int)(Math.random()*5));
		 }

		 Category[] o = new Category[1];
		 System.arraycopy(other,4, o, 0, 1);
		 server.deleteCategory(user, o);
		 other = server.getCategories(user);
		 assertEquals("Image Category lenght:",4,other.length);
		 category = other[0];	
	 }
	 
	 public void imageInfoTest() throws IOException{
			 
		 Image[] infos = new Image[5];
		 for(int i = 0 ;i < infos.length ; i++){
			 infos[i] = new Image();
			 infos[i].setCaption("image"+i);
			 infos[i].setHeight(800);
			 infos[i].setWidth(600);
			 infos[i].setKbytes((int)Math.random()*100);
			 infos[i].setFileName("image"+i);
			 infos[i].setCategory(category.getLablename());
			 infos[i].setOwner(user);
			 infos[i].setTimestamp(new Date());
			 infos[i].setDescription("");
			 infos[i].setImgsrc("imgsrc?"+i);
		 }
		 server.setImages(user,infos);
		 Image[] othe = server.getImages(user, category.getLablename());
		 assertEquals("Image info lenght:",5,othe.length);
		 Image[] o1 = new Image[1];
		 System.arraycopy(othe, 4, o1, 0, 1);
		 server.deleteImages(user, o1);
		 othe = server.getImages(user, category.getLablename());
		 assertEquals("Image info lenght:",4,othe.length);
		 info = othe[0];
		 
	 }
	 
	 
	 public void tagsTest() throws IOException{

		 Tags[] tags = new Tags[5];
		 for(int i = 0 ;i < tags.length;i++){
			 tags[i] = new Tags();
			 tags[i].setTag("tag"+i);
			 tags[i].setSort(i+1);
			 tags[i].setWidth(1);			 
		 }
		 server.setTags(user, info, tags);
		 Tags[] o2 = server.getTags(user, info);
		 assertEquals("Tags lenght:",5,o2.length);
		 Tags d = tags[4];
		 server.deleteTags(user, info, d);
		 o2 = server.getTags(user, info);
		 assertEquals("Tags lenght:",4,o2.length);
		 server.deleteAllTags(user, info);
		 o2 = server.getTags(user, info);
		 assertEquals("Tags lenght:",0,o2.length);
		 
		
	 }
	 
	 public void imageTest() throws IOException{
		 Tags[]  tags = new Tags[5];
		 for(int i = 0 ;i < tags.length;i++){
			 tags[i] = new Tags();
			 tags[i].setTag("tag"+i);
			 tags[i].setSort(i+1);
			 tags[i].setWidth(1);			 
		 }		 
		 server.setTags(user, info, tags);
		 
		 String id = Long.toString(Long.MAX_VALUE>>6 + 4);
		 
		 Image image = server.getImage(user,info.getId()); 
		 
		 //assertEquals("Image id:",image.getId(),id);
		 assertEquals("Tags length:",image.getTags().length,5);
		 assertEquals("Tags length:",image.getComments(),null);
	 }
}


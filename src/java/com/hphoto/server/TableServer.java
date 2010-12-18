package com.hphoto.server;


import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;
import org.apache.commons.beanutils.BeanUtils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseAdmin;
import org.apache.hadoop.hbase.HScannerInterface;
import org.apache.hadoop.hbase.HStoreKey;
import org.apache.hadoop.hbase.HTable;
import org.apache.hadoop.hbase.RemoteExceptionHandler;
import org.apache.hadoop.hbase.filter.PageRowFilter;
import org.apache.hadoop.hbase.filter.RegExpRowFilter;
import org.apache.hadoop.hbase.filter.RowFilterInterface;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableFactories;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.log4j.Logger;

import com.hphoto.FConstants;
import com.hphoto.bean.Album;
import com.hphoto.bean.Category;
import com.hphoto.bean.Comment;
import com.hphoto.bean.Exif;
import com.hphoto.bean.Image;
import com.hphoto.bean.Tags;
import com.hphoto.bean.UserProfile;
import com.hphoto.util.KeyUtil;



public class TableServer implements FConstants{
	
	private static Logger LOG = Logger.getLogger(TableServer.class.getName());
	
	private UidInterface uisServer;
	
	private static String SEPARATOR = "-";
	
	
	private Configuration conf;
	private HTable userTable;
	private HTable imageTable;
	private HBaseAdmin admin;

	public TableServer(Configuration conf) throws IOException{
		this.conf = conf;
		try {
			InitTable.init(new HBaseAdmin(conf));
		} catch (IOException e) {
			LOG.info("Can not init table");
			throw e;
		}
		userTable = new HTable(conf,UESR_TABLE);
		imageTable = new HTable(conf,IMAGE_TABLE);
		String address = conf.get(UID_SERVER_ADDRESS,"0.0.0.0");
    	int port = conf.getInt(UID_SERVER_PORT,UID_SERVER_PORT_DEFAULT);
    	InetSocketAddress scoket = new InetSocketAddress(address,port);   		
    	try {
        	this.uisServer = (UidInterface) RPC.waitForProxy(
    				UidInterface.class, UidInterface.versionID,
    					scoket,
    		         conf);
        } catch (IOException e) {        		
        		if (e instanceof RemoteException) {
        	        e = RemoteExceptionHandler.decodeRemoteException((RemoteException) e);
        	      }
        	      throw e;
    	}
    	
	}
	
	
	//user attribute
	public UserProfile[] getUser(String user,int length) throws IOException{
		if(user=="" || user == null){
			return null;
		}else{
			PageRowFilter rf = new PageRowFilter(length);
			Text row = new Text(user);
			UserProfile[] profile = (UserProfile[]) scanTable(userTable,row,USER_FAMILYS,rf,UserProfile.class);	 
			if(length == 1){
				if(profile != null && profile.length > 0 && profile[0].getNicename().equals(user)){				
				}else{
					return null;
				}
				
			}			
			return profile;
		}
	}

	public void setUser(UserProfile[] ups) throws IOException{
		if(ups == null)
			return;
		for (UserProfile up :ups){			
			if(up.getNicename() == null)
					continue;
			Text row = new Text(up.getNicename());
			DataOutputBuffer out = new DataOutputBuffer();
			up.write(out);
			out.flush();
			long id = userTable.startUpdate(row);
			userTable.put(id, USER_FAMILY, out.getData());
			userTable.commit(id);
			out.close();
		}
	}
	
	
	/**
	 * 
	 * @param user		user who own the album
	 * @param category	tha album name
	 * @return	Category or null
	 * @throws IOException
	 */
	//image category attribute
	public Category getCategory(String user,String category) throws IOException{
		if(user == null || user.trim().equals("")){
			return null;
		}else{
			PageRowFilter rf = new PageRowFilter(1);
			Text row = new Text(user);
			Text[] column = new Text[]{
					new Text(IMAGE_CATEGORY + user + SEPARATOR + category)
			};
			Category[] categories = (Category[]) scanTable(imageTable,row,column,rf,Category.class);
			if(categories != null && categories.length > 0 ){
				return categories[0];
				/*
				if(categories[0].getLablename().equals(category)&&categories[0].getName().equals(user)){
					return categories[0];
				}
				*/
			}
			return null;
		}
	}
	
	/**
	 * 
	 * @param user	user who own the album
	 * @return	an array containing the Category of the list
	 * @throws IOException
	 */
	
	public Category[] getCategories(String user) throws IOException{
		if(user == null || user.trim().equals("")){
			return null;
		}else{
			PageRowFilter rf = new PageRowFilter(1);
			Text row = new Text(user);
			Text[] column = new Text[]{
					new Text(IMAGE_CATEGORY + user + SEPARATOR + ".*")
			};
			Category[]  categories = (Category[]) scanTable(imageTable,row,column,rf,Category.class);
			return categories;
			/*
			ArrayList<Category> list = new ArrayList<Category>();
			if(categories != null && categories.length > 0){
				for(Category category:categories){
					if(category.getOwner().equals(user)){
						list.add(category);
					}
				}
				return list.toArray(new Category[0]);
			}					
			return new Category[0];
			*/
			
		}
	}
	/**
	 * 
	 * @param user		user who own the album
	 * @param opened	the album is open
	 * @param count		least count the album contain photos
	 * @return	an array containing the Category of the list
	 * @throws IOException
	 */
	
	public Category[] getCategories(String user,boolean opened,int count) throws IOException{
		if(user == null || user.trim().equals("")){
			return null;
		}else{
			PageRowFilter rf = new PageRowFilter(1);
			Text row = new Text(user);
			Text[] column = new Text[]{
					new Text(IMAGE_CATEGORY + user + SEPARATOR + ".*")
			};
			Category[]  categories = (Category[]) scanTable(imageTable,row,column,rf,Category.class);
			ArrayList<Category> list = new ArrayList<Category>();
			if(categories != null && categories.length > 0){
				for(Category category:categories){
					if(category.getOwner().equals(user) && category.isOpened() == opened && category.getCount( ) > count ){
						list.add(category);
					}
				}
			}
			return list.toArray(new Category[0]);
		}
	}
	
	
	public void setCategory(String user,Category[] categories) throws IOException{
		if(user == null || user.trim().equals(""))
			return;
		Text row = new Text(user);
		long id = imageTable.startUpdate(row);
		for(Category category : categories){
			DataOutputBuffer out = new DataOutputBuffer();
			if(category.getLablename() == null || category.getLablename().trim().equals("")){				
				Text key = KeyUtil.getKey(category.getName());
				category.setLableName(key.toString());
			}
			category.write(out);
			out.flush();
			imageTable.put(id, new Text(IMAGE_CATEGORY  + user + SEPARATOR + category.getLablename()), out.getData());
			out.close();
		}
		imageTable.commit(id);
	}
	
	
	
	public void deleteCategory(String user,Category[] categories) throws IOException{
		if(user == null || user.trim().equals(""))
			return;
		Text row = new Text(user);		
		for(Category category: categories){
			long id = imageTable.startUpdate(row);
			String lable = category.getLablename();
			//int length = category.getCount();
			imageTable.delete(id, new Text(IMAGE_CATEGORY  + lable));	
			imageTable.commit(id);
			//we also delete the image in table
			Image[] images = getImages(user,lable);
			deleteImages(user,images);	
			for(Image image : images){
				deleteExif(user,image);
				deleteAllTags(user,image);
				deleteAllComment(user,image);
				deleteAllAlbum(user,image);
			}

		}
	
	}
	
	
	public Image getImage(String user,String imageid) throws IOException{
		Image image = new Image();
		if(user == null || user.trim().equals("")
				||imageid ==null || imageid.trim().equals(""))
			return null;
		Text row = new Text(user);
		RegExpRowFilter rf = new RegExpRowFilter(".*" +SEPARATOR + imageid);
		Text[] column = new Text[]{
				new Text(IMAGE_INFO + ".*" + SEPARATOR + imageid)	
				,new Text(IMAGE_EXIF + ".*" + SEPARATOR + imageid)
				,new Text(IMAGE_TAGS + ".*" + SEPARATOR + imageid)
				,new Text(IMAGE_COMMENT + ".*" + SEPARATOR + imageid)
				,new Text(IMAGE_ALBUM + ".*" + SEPARATOR + imageid)
		};		
		getImageData(row,column,image);
		return image.getOwner() == null ? null : image;
	}
	/*
	public Image[] getImages(String user,String id) throws IOException{
		if(user == null || user.trim().equals("")
				||id ==null || id.trim().equals(""))
			return null;
		client.openTable(IMAGE_TABLE);
		Text row = new Text(user);
		PageRowFilter rf = new PageRowFilter(1);
		Text[] column = new Text[]{
				new Text(IMAGE_INFO +  ".*" + id)
		};
		Image[] infos = (Image[]) scanTable(row,column,rf,Image.class);
		return infos.length > 0 ? infos[0] : null;
	}
	*/
	//image info attribute
	//image lable is IMAGE_INFO + category + SEPARATOR + imageid ;
	public Image[] getImages(String user,String category) throws IOException{
		if(user == null || user.trim().equals("")
				||category ==null || category.trim().equals(""))
			return null;
		Text row = new Text(user);
		PageRowFilter rf = new PageRowFilter(1);
		Text[] column = new Text[]{
				new Text(IMAGE_INFO + user + SEPARATOR + category + SEPARATOR + ".*")
		};
		return (Image[]) scanTable(imageTable,row,column,rf,Image.class);
	}

	
	public void setImages(String user,Image[] images) throws IOException{
		if(user == null || user.trim().equals(""))
			return;
		Text row = new Text(user);
		long id = imageTable.startUpdate(row);
		for(Image image : images){
			DataOutputBuffer out = new DataOutputBuffer();
			if(image.getId() == null || image.getId().trim().equals("")){
				image.setId(Long.toString(this.uisServer.getUid()));
			}
			image.write(out);
			out.flush();			
			imageTable.put(id, new Text(IMAGE_INFO + user + SEPARATOR + image.getCategory() + SEPARATOR + image.getId()), out.getData());
			out.close();
		}
		imageTable.commit(id);
	}
	
	
	public void deleteImages(String user,Image[] images) throws IOException{
		if(user == null || user.trim().equals(""))
			return;
		Text row = new Text(user);
		
		for(Image image: images){
			long id = imageTable.startUpdate(row);
			imageTable.delete(id, new Text(IMAGE_INFO + user + SEPARATOR + image.getCategory() + SEPARATOR + image.getId()));		
			imageTable.commit(id);			
			// we allso delete tag,comment,album,exif			
			deleteExif(user,image);
			deleteAllTags(user,image);
			deleteAllComment(user,image);
			deleteAllAlbum(user,image);
		}
		
	}

	
	
	//exif
	public Exif getExif(String user,Image info) throws IOException{
		return getExif(user,info.getCategory(),info.getId());
	}
	
	
	public Exif getExif(String user,String category,String imageid) throws IOException{		
		if(user == null || user.trim().equals("")
				||category ==null || category.trim().equals(""))
			return null;
		Text row = new Text(user);
		PageRowFilter rf = new PageRowFilter(1);
		Text[] column = new Text[]{
				new Text(IMAGE_EXIF + user + SEPARATOR + category + SEPARATOR + imageid)
		};
		Exif[] exifs = ((Exif[]) scanTable(imageTable,row,column,rf,Exif.class));
		return exifs != null && exifs.length > 0 ? exifs[0] :null;
	}
	
	
	//label is IMAGE_EXIF + category + SEPARATOR + imageId 
	public void setExif(String user,Image image,Exif exif) throws IOException{
		setExif(user,image.getCategory(),image.getId(),exif);
	}
	
	
	public void setExif(String user,String category,String imageid,Exif exif) throws IOException{
		if(user == null || user.trim().equals("")
				||category ==null || category.trim().equals(""))
			return;
		Text row = new Text(user);
		long id = imageTable.startUpdate(row);
		DataOutputBuffer out = new DataOutputBuffer();
		exif.write(out);
		out.flush();
		imageTable.put(id, new Text(IMAGE_EXIF + user + SEPARATOR + category + SEPARATOR + imageid), out.getData());
		imageTable.commit(id);
		out.close();
	}

	
	public void deleteExif(String user,Image info) throws IOException{
		if(user == null || user.trim().equals(""))
			return;
		Text row = new Text(user);
		long id = imageTable.startUpdate(row);
		imageTable.delete(id, new Text(IMAGE_EXIF + user + SEPARATOR + info.getCategory() + SEPARATOR + info.getId()));		
		imageTable.commit(id);
	}
	
	
	//tags	
	public Tags[] getTags(String user,Image info) throws IOException{
		return getTags(user,info.getCategory(),info.getId());
	}
	
	
	public Tags[] getTags(String user,String category,String imageid) throws IOException{
		if(user == null || user.trim().equals("")
				||category ==null || category.trim().equals(""))
			return null;
		Text row = new Text(user);
		byte[][] bytes = imageTable.get(row, new Text(IMAGE_TAGS + user + SEPARATOR + category + SEPARATOR + imageid), 1);
		if(bytes != null){
			Tags[] tags = (Tags[])getWritableClass(Tags.class,bytes[0]);
			return tags != null && tags.length > 0 ? tags : null;	
		}
		return null;
	}
	
	
	public void setTags(String user,Image info,Tags[] tags) throws IOException{
		setTags(user,info.getCategory(),info.getId(),tags);
	}
		
	
	public void setTags(String user,String category,String imageid,Tags[] tags) throws IOException{
		if(user == null || user.trim().equals(""))
			return;
		Text row = new Text(user);
		long id = imageTable.startUpdate(row);
		imageTable.put(id, new Text(IMAGE_TAGS + user + SEPARATOR + category + SEPARATOR + imageid),getByteData(Tags.class,tags));		
		imageTable.commit(id);
	}
	
	public void deleteTags(String user,Image info,Tags tag) throws IOException{
		if(user == null || user.trim().equals(""))
			return;
		Tags[] tags = getTags(user,info);
		ArrayList<Tags> list = new ArrayList<Tags>();
		for(Tags t:tags){
			if(t.equals(tag)){
				continue;
			}
			list.add(t);
		}
		setTags(user,info,(Tags[])list.toArray(new Tags[0]));
	}
		
	public void deleteAllTags(String user,Image info) throws IOException{
		if(user == null || user.trim().equals(""))
			return;
		setTags(user,info,new Tags[0]);
	}
	
	
	//comment	
	public Comment[] getComment(String user,Image info) throws IOException{
		return getComment(user,info.getCategory(),info.getId());
	}
	
	public Comment[] getComment(String user,String category,String imageid) throws IOException{
		if(user == null || user.trim().equals("")
				||category == null || category.trim().equals(""))
			return null;
		Text row = new Text(user);
		byte[][] bytes = imageTable.get(row, new Text(IMAGE_COMMENT + user + SEPARATOR + category + SEPARATOR + imageid), 1);
		if(bytes != null){
			Comment[] comments = (Comment[])getWritableClass(Comment.class,bytes[0]);
			return comments; //!= null && comments.length > 0 ;
		}
		return null;
	}
	
	
	public void setComment(String user,Image info,Comment[] comments) throws IOException{
		SetComment(user,info.getCategory(),info.getId(),comments);
	}
	
	
	public void SetComment(String user,String category,String imageid,Comment[] comments) throws IOException{
		if(user == null || user.trim().equals(""))
			return;
		Text row = new Text(user);		
		long id = imageTable.startUpdate(row);
		imageTable.put(id, new Text(IMAGE_COMMENT + user + SEPARATOR + category + SEPARATOR + imageid),getByteData(Comment.class,comments));		
		imageTable.commit(id);
	}
	
	public void deleteComment(String user,Image info,Comment comment) throws IOException{
		if(user == null || user.trim().equals(""))
			return;
		Comment[] comments = getComment(user,info);
		ArrayList<Comment> list = new ArrayList<Comment>();
		for(Comment t:comments){
			if(t.equals(comment)){
				continue;
			}
			list.add(t);
		}
		setComment(user,info,(Comment[])list.toArray(new Comment[0]));
	}
	
	public void deleteAllComment(String user,Image info) throws IOException{
		if(user == null || user.trim().equals(""))
			return;
		setComment(user,info,new Comment[0]);
	}
	
	
	//album	
	public Album[] getAlbum(String user,Image info) throws IOException{
		return getAlbum(user,info.getCategory(),info.getId());
	}
	
	public Album[] getAlbum(String user,String category,String imageid) throws IOException{
		if(user == null || user.trim().equals("")
				||category == null || category.trim().equals(""))
			return null;
		Text row = new Text(user);
		byte[][] bytes = imageTable.get(row, new Text(IMAGE_ALBUM + user + SEPARATOR + category + SEPARATOR + imageid), 1);
		if(bytes != null){
			return (Album[])getWritableClass(Album.class,bytes[0]);
		}
		return null;
	}
	
	public void setAlbum(String user,Image info,Album[] albums) throws IOException{
		setAlbum(user,info.getCategory(),info.getId(),albums);
	}
	
	public void setAlbum(String user,String category,String imageid,Album[] albums) throws IOException{
		if(user == null || user.trim().equals(""))
			return;
		Text row = new Text(user);		
		long id = imageTable.startUpdate(row);
		imageTable.put(id, new Text(IMAGE_ALBUM + user + SEPARATOR + category + SEPARATOR + imageid),getByteData(Album.class,albums));		
		imageTable.commit(id);
	}
	
	public void deleteAlbum(String user,Image info,Album album) throws IOException{
		if(user == null || user.trim().equals(""))
			return;
		Album[] albums = getAlbum(user,info);
		ArrayList<Album> list = new ArrayList<Album>();
		for(Album t:albums){
			if(t.equals(album)){
				continue;
			}
			list.add(t);
		}
		setAlbum(user,info,(Album[])list.toArray(new Album[0]));
	}
	
	public void deleteAllAlbum(String user,Image info) throws IOException{
		if(user == null || user.trim().equals(""))
			return;
		setAlbum(user,info,new Album[0]);
	}
	
	
	private byte[] getByteData(Class valueClass,Writable[] value) throws IOException{
		DataOutputBuffer out = new DataOutputBuffer();
		ArrayWritable aw = new  ArrayWritable(valueClass,value);
		aw.write(out);
		out.flush();
		byte[] value1 = out.getData();		
		out.close();
		return value1;
	}
	
	private Object[] getWritableClass(Class valueClass,byte[] bytes) throws IOException{
		DataInputBuffer in = new DataInputBuffer();
		in.reset(bytes, bytes.length);
		ArrayWritable aw = new  ArrayWritable(valueClass);
		aw.readFields(in);
		in.close();
		Object[] result = (Object[]) aw.toArray();
		//if can comparable,sort it;
		if(WritableComparable.class.isAssignableFrom(valueClass)){
		    	Arrays.sort(result);
		 }
		return result;
	}
	
	
	private Object[] getImageData(Text row, Text[] column,Image image) throws IOException{
		PageRowFilter rf = new PageRowFilter(1);
		HScannerInterface s = imageTable.obtainScanner(column,row,rf);
		try{
	    	 HStoreKey curKey = new HStoreKey();
	         TreeMap<Text, byte[]> curVals = new TreeMap<Text, byte[]>();
	         while(s.next(curKey, curVals)) {
	        	 for(Iterator<Text> it = curVals.keySet().iterator(); it.hasNext(); ) {	
	        		 Text lable = it.next();
	        		 Writable writeClass = null;
	        		 LOG.info("lable:" + lable);
	        		 byte[] bytes =  curVals.get(lable);
	        		 if(lable.toString().startsWith(IMAGE_INFO.toString())){
	        			 writeClass  = WritableFactories.newInstance(Image.class);
	        		 }else if(lable.toString().startsWith(IMAGE_EXIF.toString())){
	        			 writeClass  = WritableFactories.newInstance(Exif.class);
	        		 }else if(lable.toString().startsWith(IMAGE_TAGS.toString())){
	        			 Tags[] tags = (Tags[]) getWritableClass(Tags.class,bytes);
	        			 image.setTags(tags);
	        		 }else if(lable.toString().startsWith(IMAGE_COMMENT.toString())){
	        			 Comment[] comments = (Comment[]) getWritableClass(Comment.class,bytes);
	        			 image.setComments(comments);
	        		 }else if(lable.toString().startsWith(IMAGE_ALBUM.toString())){
						 Album[] albums = (Album[]) getWritableClass(Album.class,bytes);
						 image.setAlbum(albums);
					 }
	        		 if(writeClass != null){
						 DataInputBuffer in = new DataInputBuffer();
						 in.reset(bytes, bytes.length);
						 writeClass.readFields(in);
						 in.close();
						 if(writeClass instanceof Image){
							 try {
								BeanUtils.copyProperties(image,(Image)writeClass);
							} catch (IllegalAccessException e) {
							} catch (InvocationTargetException e) {
							}
						 }else if(writeClass instanceof Exif){
							 image.setExif((Exif)writeClass);
						 }
					 }
	        	 }
	        	 
	         }
	      }finally {
	 	     s.close();
	 	  }
		
		return null;
	}
	
	private Object[] scanTable(HTable table,Text row, Text[] column,RowFilterInterface rf,Class valueClass) throws IOException{
		ArrayList<Writable> list = new ArrayList<Writable>();
		HScannerInterface s = table.obtainScanner(column,row,rf);
		Writable writeClass = WritableFactories.newInstance(valueClass);
	    try{
	    	 HStoreKey curKey = new HStoreKey();
	         TreeMap<Text, byte[]> curVals = new TreeMap<Text, byte[]>();
	         boolean next = s.next(curKey, curVals);
	         while(next) {
	        	 for(Iterator<Text> it = curVals.keySet().iterator(); it.hasNext(); ) {	
	        		 writeClass = WritableFactories.newInstance(valueClass);	  
	        		 byte[] bytes =  curVals.get(it.next());
	        		 DataInputBuffer inbuf = new DataInputBuffer();
	        		 inbuf.reset(bytes, bytes.length);
	        		 writeClass.readFields(inbuf);
	        		 inbuf.close(); 	        		
	            	 list.add(writeClass);
	             }
	             curVals.clear();
	             next = s.next(curKey, curVals);
	         }
	    }finally {
	        s.close();
	    }

	    
	   Object[] result = (Object[]) Array.newInstance(valueClass, list.size());
	   for (int i = 0; i < list.size(); i++) {
	      Array.set(result, i, list.get(i));
	   }	  
	   
	   //if can comparable,sort it;
	   if(WritableComparable.class.isAssignableFrom(valueClass)){
			Arrays.sort(result);
		}
		
	   return result;

	}
}

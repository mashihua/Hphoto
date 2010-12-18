
package com.hphoto.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.hbase.HConstants;

import com.hphoto.ClassWriteale;




public class Convertion {
	
	
	/**
	 * convert the byte value that hbase given to a class object. 
	 * @param type
	 * @param value
	 * @return
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws Exception
	 */
	
	  private static final Map<String, Class<?>> PRIMITIVE_NAMES = new HashMap<String, Class<?>>();
	  static {
	    PRIMITIVE_NAMES.put("boolean", Boolean.TYPE);
	    PRIMITIVE_NAMES.put("byte", Byte.TYPE);
	    PRIMITIVE_NAMES.put("char", Character.TYPE);
	    PRIMITIVE_NAMES.put("short", Short.TYPE);
	    PRIMITIVE_NAMES.put("int", Integer.TYPE);
	    PRIMITIVE_NAMES.put("long", Long.TYPE);
	    PRIMITIVE_NAMES.put("float", Float.TYPE);
	    PRIMITIVE_NAMES.put("double", Double.TYPE);
	    PRIMITIVE_NAMES.put("void", Void.TYPE);
	  }

	  
	public static Object encode(Class type,byte[] value) throws IOException, ClassNotFoundException {
		if (type == String.class) {
			 
			 return new String(value,HConstants.UTF8_ENCODING);
			 
		}
		if (type == Boolean.TYPE || type == Boolean.class) {
			 
			return Boolean.valueOf(new String(value,HConstants.UTF8_ENCODING));
			 
		}
		if (type == Integer.TYPE || type == Integer.class) {
			 
			 return Integer.valueOf(new String(value,HConstants.UTF8_ENCODING));
			 
		}
		if(type == Short.TYPE || type == Short.class){
			 
			 return Short.valueOf(new String(value,HConstants.UTF8_ENCODING));	
			 
		}
		if (type == Long.TYPE || type == Long.class) {
			 
			 return Long.valueOf(new String(value,HConstants.UTF8_ENCODING));
			 
		}
		if (type == Float.TYPE || type == Float.class) {
			 
			 return Float.valueOf(new String(value,HConstants.UTF8_ENCODING));
			 
		}
		 if (type == Double.TYPE || type == Double.class) {
			 
			 return Double.valueOf(new String(value,HConstants.UTF8_ENCODING));
			 
		 } 
		 if(type == Byte.TYPE || type == Byte.class){			 
			 return value[0];			 
		 }
		 if(type == Character.TYPE || type == Character.class){			 
			 return (char) value[0];			 
		 }
		 if(type == Date.class){			 
			 long v = Long.valueOf(new String(value,HConstants.UTF8_ENCODING));			 
			 return new Date(v);
			 
		 }
		 if (type == java.lang.Class.class) {
			 ByteArrayInputStream bin = new ByteArrayInputStream(value);
			 ObjectInputStream in = new ObjectInputStream(bin);
			 return in.readObject();
			 
		 }
		 return null;
	 }
	
	/**
	 * decode a class object to byte[],the byte[] save to hbase
	 * @param type
	 * @param value
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	
	public static byte[] decode(Class type, Object value) throws UnsupportedEncodingException{
		if (type == String.class) {
			
			return ((String) value).getBytes(HConstants.UTF8_ENCODING);
			
		}
		if (type == Boolean.TYPE ||type == Boolean.class) {
			
			boolean v = ((Boolean) value).booleanValue();
			return  Boolean.toString(v).getBytes(HConstants.UTF8_ENCODING);
			
		}
		if (type == Integer.TYPE || type == Integer.class) {
			
			 int i = ((Integer)value).intValue();
			 return Integer.toString(i).getBytes(HConstants.UTF8_ENCODING);
			 
		}
		if(type == Short.TYPE || type == Short.class){
			 short v = ((Short) value).shortValue();
			 return Short.toString(v).getBytes(HConstants.UTF8_ENCODING);
			 
		}
		if (type == Long.TYPE || type == Long.class) {	
			
			 long v = (long) ((Number) value).doubleValue();
			 return Long.toString(v).getBytes(HConstants.UTF8_ENCODING);
			 
		}
		if (type == Float.TYPE || type == Float.class) {
			
			 float v = (float) ((Number) value).doubleValue();
			 return Float.toString(v).getBytes(HConstants.UTF8_ENCODING);
			 
		}
		if (type == Double.TYPE || type==Double.class) {
			 
			 double v = ((Number) value).doubleValue();
			 return Double.toString(v).getBytes(HConstants.UTF8_ENCODING);
			 
		}
		if(type == Byte.TYPE || type == Byte.class){			 
			 byte[] v = new byte[1];
			 v[0] = ((Byte)value).byteValue();
			 return v;
			 
		}
		if(type == Character.TYPE || type == Character.class){			 
			char v = ((Character)value).charValue();
			byte[] b = new byte[1];
			b[0] = (byte)v;
			return b;			
		}
		if( type == Date.class){		
			 
			long v = (long) ((Date) value).getTime();
			return Long.toString(v).getBytes(HConstants.UTF8_ENCODING);
			
		}
		if(type.isArray()){
			System.out.println("cought it");
		}
		if (type == java.lang.Class.class) {			 
			 ByteArrayOutputStream bot = new ByteArrayOutputStream();
			 try {
				 /*
				 ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				 DataOutputStream out = new DataOutputStream(bytes);
				 ((Writable)value).write(out);
				 return bytes.toByteArray();
				 */
				 //class.isAssignableFrom()
				if(ClassWriteale.class.cast(value) != null){
					ObjectOutputStream os = new ObjectOutputStream(bot);
				    os.writeObject(value);
				    os.flush();
				    byte [] v = bot.toByteArray();
				    bot.close();
				    return v;
				}
			} catch (IOException e) {
			} catch(RuntimeException re){
				throw new NotSupportClassException("this class can not save to database,you must implements ClassWriteale");
			}
			
		}		
		return null;
	}
	

    public static Class classOrNull(String className)
    {
        try {
            return Class.forName(className);
        } catch  (ClassNotFoundException ex) {
        } catch  (SecurityException ex) {
        } catch  (LinkageError ex) {
        } catch (IllegalArgumentException e) {
            // Can be thrown if name has characters that a class name
            // can not contain
        }
        return null;
    }

    public static Class classOrNull(ClassLoader loader, String className)
    {
        try {
            return loader.loadClass(className);
        } catch (ClassNotFoundException ex) {
        } catch (SecurityException ex) {
        } catch  (LinkageError ex) {
        } catch (IllegalArgumentException e) {
            // Can be thrown if name has characters that a class name
            // can not contain
        }
        return null;
    }

    static Object newInstanceOrNull(Class cl)
    {
        try {
            return cl.newInstance();
        } catch (SecurityException x) {
        } catch  (LinkageError ex) {
        } catch (InstantiationException x) {
        } catch (IllegalAccessException x) {
        }
        return null;
    }
}

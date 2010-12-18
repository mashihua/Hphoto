package com.hphoto.util;


import java.security.MessageDigest;
import java.text.DecimalFormat;


 public class StringUtil {

   private final static String[] hexDigits = {
       "0", "1", "2", "3", "4", "5", "6", "7",
       "8", "9", "a", "b", "c", "d", "e", "f"};

   /**
    * 转换字节数组为16进制字串
    * @param b 字节数组
    * @return 16进制字串
    */

   public static String byteArrayToHexString(byte[] b) {
     StringBuffer resultSb = new StringBuffer();
     for (int i = 0; i < b.length; i++) {
       resultSb.append(byteToHexString(b[i]));
     }
     return resultSb.toString();
   }

   private static String byteToHexString(byte b) {
     int n = b;
     if (n < 0)
       n = 256 + n;
     int d1 = n / 16;
     int d2 = n % 16;
     return hexDigits[d1] + hexDigits[d2];
   }

   public static String MD5Encode(String origin) {
     String resultString = null;

     try {
       resultString=new String(origin);
       MessageDigest md = MessageDigest.getInstance("MD5");
       resultString=byteArrayToHexString(md.digest(resultString.getBytes()));
     }
     catch (Exception ex) {

     }
     return resultString;
   }
   

   private static DecimalFormat oneDecimal = new DecimalFormat("###0");
   private static DecimalFormat dotDecimal = new DecimalFormat("###.#");
   /**
    * Given an integer, return a string that is in an approximate, but human 
    * readable format. 
    * It uses the bases 'k', 'm', and 'g' for 1024, 1024**2, and 1024**3.
    * @param number the number to format
    * @return a human readable form of the integer
    */
   public static String humanReadableInt(long number) {
     long absNumber = Math.abs(number);
     double result = number;
     String suffix = "";
     if (absNumber <= 1024) {
    	 result = 0;
    	 suffix = " MB";
     }else if (absNumber <= 1024 * 1024) {
    	 result = 1;
    	 suffix = " MB";
     }
     else if (absNumber <= 1024 * 1024 * 1024) {
       result = number / (1024.0 * 1024);
       suffix = " MB";
     } else {
       result = number / (1024.0 * 1024 * 1024);
       suffix = " G";
     }
     return oneDecimal.format(result) + suffix;
   }
   
   
   public static String humanReadableSizeInt(long number) {
	     long absNumber = Math.abs(number);
	     double result = number;
	     String suffix = "";
	     if (absNumber < 1024 * 1024) {
	       result = number / 1024.0;
	       suffix = " KB";
	     }
	     else if (absNumber <= 1024 * 1024 * 1024) {
	       result = number / (1024.0 * 1024);
	       suffix = " MB";
	     } else {
	       result = number / (1024.0 * 1024 * 1024);
	       suffix = " G";
	     }
	     return dotDecimal.format(result) + suffix;
	   }
   
   /**
    * Format a percentage for presentation to the user.
    * @param done the percentage to format (0.0 to 1.0)
    * @param digits the number of digits past the decimal point
    * @return a string representation of the percentage
    */
   public static String formatPercent(double done, int digits) {
     DecimalFormat percentFormat = new DecimalFormat("0.0%");
     double scale = Math.pow(10.0, digits+2);
     double rounded = Math.floor(done * scale);
     percentFormat.setDecimalSeparatorAlwaysShown(false);
     percentFormat.setMinimumFractionDigits(digits);
     percentFormat.setMaximumFractionDigits(digits);
     return percentFormat.format(rounded / scale);
   }
   
   public static void main(String [] args){
	   System.out.println(humanReadableSizeInt((long)(1024*1024 + 1024*360L)));
	   System.out.println(humanReadableSizeInt((long)(1024*360L)));
	   
   }
   

 } 


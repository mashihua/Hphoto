package com.hphoto.util;

import java.io.IOException;
import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;

public class Crypto {
	
	private static byte[] rawKeyData;

	private static byte[] getRowKey() throws NoSuchAlgorithmException{
		
		SecureRandom sr = new SecureRandom();
		// 为我们选择的DES算法生成一个KeyGenerator对象
		KeyGenerator kg = KeyGenerator.getInstance ("DES");
		kg.init(sr);
		// 生成密钥
		SecretKey key = kg.generateKey();
		return key.getEncoded ();
	}
	
	public String Encrypt(String value) throws Exception{
		SecureRandom sr = new SecureRandom();
		//从原始密钥数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(getRowKey());
		// 创建一个密钥工厂，然后用它把DESKeySpec转换成Secret Key对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey key = keyFactory.generateSecret( dks );
		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance("DES");
		//用密钥初始化Cipher对象
		cipher.init( Cipher.ENCRYPT_MODE, key, sr );
		//通过读类文件获取需要加密的数据
		//执行加密操作
		byte[] encryptedClassData = cipher.doFinal(value.getBytes("UTF8"));
		
		return new String(encryptedClassData,"UTF8");
	}
	
	public String Decrypt(String value) throws Exception{
		//生成一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		// 创建一个DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(getRowKey());
		// 创建一个密钥工厂，然后用它把DESKeySpec对象转换成Secret Key对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey key = keyFactory.generateSecret( dks );
		//Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance("DES");
		//用密钥初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, key, sr);
		//执行解密操作
		byte[] decryptedData = cipher.doFinal(value.getBytes("UTF8"));
		return new String(decryptedData,"UTF8");
	}
}

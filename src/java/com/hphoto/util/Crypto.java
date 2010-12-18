package com.hphoto.util;

import java.io.IOException;
import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;

public class Crypto {
	
	private static byte[] rawKeyData;

	private static byte[] getRowKey() throws NoSuchAlgorithmException{
		
		SecureRandom sr = new SecureRandom();
		// Ϊ����ѡ���DES�㷨����һ��KeyGenerator����
		KeyGenerator kg = KeyGenerator.getInstance ("DES");
		kg.init(sr);
		// ������Կ
		SecretKey key = kg.generateKey();
		return key.getEncoded ();
	}
	
	public String Encrypt(String value) throws Exception{
		SecureRandom sr = new SecureRandom();
		//��ԭʼ��Կ���ݴ���DESKeySpec����
		DESKeySpec dks = new DESKeySpec(getRowKey());
		// ����һ����Կ������Ȼ��������DESKeySpecת����Secret Key����
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey key = keyFactory.generateSecret( dks );
		// Cipher����ʵ����ɼ��ܲ���
		Cipher cipher = Cipher.getInstance("DES");
		//����Կ��ʼ��Cipher����
		cipher.init( Cipher.ENCRYPT_MODE, key, sr );
		//ͨ�������ļ���ȡ��Ҫ���ܵ�����
		//ִ�м��ܲ���
		byte[] encryptedClassData = cipher.doFinal(value.getBytes("UTF8"));
		
		return new String(encryptedClassData,"UTF8");
	}
	
	public String Decrypt(String value) throws Exception{
		//����һ�������ε������Դ
		SecureRandom sr = new SecureRandom();
		// ����һ��DESKeySpec����
		DESKeySpec dks = new DESKeySpec(getRowKey());
		// ����һ����Կ������Ȼ��������DESKeySpec����ת����Secret Key����
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey key = keyFactory.generateSecret( dks );
		//Cipher����ʵ����ɽ��ܲ���
		Cipher cipher = Cipher.getInstance("DES");
		//����Կ��ʼ��Cipher����
		cipher.init(Cipher.DECRYPT_MODE, key, sr);
		//ִ�н��ܲ���
		byte[] decryptedData = cipher.doFinal(value.getBytes("UTF8"));
		return new String(decryptedData,"UTF8");
	}
}

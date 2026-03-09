package common;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class encryptor {
	
	private static byte[] spices = new String("alfanak").getBytes();
	private static int itterations = 500;
	private static int key_length = 128;
	
	static public String encrypt(String data, String key_password)
	{
		SecretKeySpec key = generate_key(key_password);
		
		return encrypt(data, key);
	}
	
	public static String encrypt(String data, SecretKeySpec key)
	{
		try
		{
			Cipher pbe_cipher = Cipher.getInstance("AES");
		
			pbe_cipher.init(Cipher.ENCRYPT_MODE, key);
		
			byte[] crypto_text = pbe_cipher.doFinal(data.getBytes());
			
			return base64encode(crypto_text);
		}
		catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex)
		{
			ex.printStackTrace();
		}
		return "";
	}
	
	public static String decrypt(String crypted_data, String key_password)
	{
		SecretKeySpec key = encryptor.generate_key(key_password);
		
		return decrypt(crypted_data, key);
	}
	
	public static String decrypt(String crypted_data, SecretKeySpec key)
	{
		try
		{
			byte[] encrypted_data_bytes = base64decode(crypted_data);
			
			Cipher cipher = Cipher.getInstance("AES");
			
			cipher.init(Cipher.DECRYPT_MODE, key);
			
			byte[] decrypted_data_bytes = cipher.doFinal(encrypted_data_bytes);
			
			return new String(decrypted_data_bytes);
		
		}
		catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex)
		{
			ex.printStackTrace();
		}
		return "";
	}
	
	private static SecretKeySpec generate_key(String password)
	{
		return generate_key(password.toCharArray(), spices, itterations, key_length);
	}
	private static SecretKeySpec generate_key(char[] password, byte[] spices, int itterations, int length)
	{
		try
		{
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
			PBEKeySpec keyspec = new PBEKeySpec(password, spices, itterations, length);
			SecretKey tmp_key = skf.generateSecret(keyspec);
			return new SecretKeySpec(tmp_key.getEncoded(), "AES");
		}
		catch (NoSuchAlgorithmException | InvalidKeySpecException ex)
		{
			ex.printStackTrace();
		}
		return null; 
	}
	
	private static String base64encode(byte[] bytes)
	{
		return Base64.getEncoder().encodeToString(bytes);
	}
	
	private static byte[] base64decode(String encrypted_data)
	{
		return Base64.getDecoder().decode(encrypted_data);
	}
}

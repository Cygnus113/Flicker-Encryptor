package cygnus.flicker;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class Encryption {
	
	private static final int IVLength = 16;
	private static final String hashKey = "PBKDF2WithHmacSHA256";
	private static final String cipherPadding = "AES/CBC/PKCS5Padding";
	private static final int iterationCount = 65536;
	
	public static void main(String[] args) {
		new GUImanager();
	}
	
		// AES Encrypt a file with a password and bit depth
	public static void encryptFile(String filePath, String password, int bitLevel)  {
	
		final Path inputPath = Paths.get(filePath);
		String newPath = new String(filePath + ".aes");
		File outputFile = new File(newPath);
		
		byte[] salt = new byte[8];
		SecureRandom random = new SecureRandom ();
	    random.nextBytes (salt);
	    
	    CipherOutputStream encryptionStream = null;
	    FileOutputStream normalStream = null;
	    
		try {
			
		    byte[] data = Files.readAllBytes(inputPath);
		    
			SecretKey key = generateKey(password,salt, bitLevel);
			Cipher cipher = generateEncryptCipher(key);
			byte[] IV = generateIV(cipher);	
			
			int maxKeyLen = Cipher.getMaxAllowedKeyLength("AES");
			if (maxKeyLen < bitLevel)
				{	
					GUImanager.infoBox("Encryption with a bit depth greater than 128 requires the Java Cryptography Extension (JCE)\nto be installed on your system.","Error: Encryption strength is limited");
					return;
				}
		
			encryptionStream = new CipherOutputStream(new FileOutputStream(outputFile, true),cipher);  
			normalStream = new FileOutputStream(outputFile, true);

			// Do not encrypt the IV or Salt so we are able to decrypt the file later.
			normalStream.write(salt);
			normalStream.write(IV);
			encryptionStream.write(data);
		
			} 
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					try {
						encryptionStream.close();
						normalStream.close();
						} 	
							catch (IOException e) {
							// Die quietly;
							}
		}
	} 

		// AES Decrypt a file with a password and bit depth
	public static void decryptFile(String filePath, String password, int bitLevel){
		
		String oldExt = filePath.substring(0, filePath.lastIndexOf("."));
		final Path inputPath = Paths.get(filePath);
		File outputFile = new File(oldExt);
		
		try{
			 byte[] allData = Files.readAllBytes(inputPath);
			 byte[] salt = Arrays.copyOfRange(allData, 0, 8);
			 byte[] IV = Arrays.copyOfRange(allData, 8, 8+IVLength);
			 byte[] data = Arrays.copyOfRange(allData, 8+IVLength, allData.length);

			 
			SecretKey key = generateKey(password,salt, bitLevel);
			Cipher cipher = generateDecryptCipher(key, IV);
			
			CipherOutputStream decryptionStream = new CipherOutputStream(new FileOutputStream(outputFile, true),cipher); 
			decryptionStream.write(data);
			decryptionStream.close();
			
		} 	
			catch (IOException e) {
				GUImanager.infoBox("Something went wrong", "Error");
				e.printStackTrace();
		}
	}

	/*PRIVATE METHODS*/
		// Derive the key for encryption
	private static SecretKey generateKey(String passwordString, byte[] salt, int bitLevel){
		try{
			char[] password = passwordString.toCharArray();
			SecretKeyFactory factory = SecretKeyFactory.getInstance(hashKey);
			KeySpec spec = new PBEKeySpec(password, salt, iterationCount, bitLevel);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
		return secret;
		} catch(Exception ex) {
	        ex.printStackTrace();
	        return null;
	    }
	}
	
		// Derive the Cipher for encryption
	private static Cipher generateEncryptCipher(SecretKey key){
		try{
			Cipher cipher = Cipher.getInstance(cipherPadding);
			cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher;
		} catch(Exception ex) {
	        ex.printStackTrace();
	        return null;
	    }
		
	
	}
		
		// Derive the Cipher for decryption 
	private static Cipher generateDecryptCipher(SecretKey key, byte[] iv){
		
		try{
		Cipher cipher = Cipher.getInstance(cipherPadding);
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
		return cipher;
		} catch(Exception ex) {
	        ex.printStackTrace();
	        return null;
		}
	}
	
		// Derive the Initiation Vector given Cipher
	private static byte[] generateIV(Cipher cipher){
		try{
			AlgorithmParameters params = cipher.getParameters();
		byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
		return iv;
		} catch(Exception ex) {
	        ex.printStackTrace();
	        return null;
	    }
	}

	
}
	


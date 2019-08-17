package Util;

import java.util.Base64;
import java.util.Random;

public class Password {
	//Gercek md5 ýn decompile ý yok, o yüzden burada gerçek md5 kullanýlmadý.
  	public static String MD5(String md5) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
		}
		return null;
	}
	
	public static String sifrele(String data, int i) {
		byte[] dizi = null;
		Random rd = new Random();
		int ri = rd.nextInt(899) + 100;
		for (int j = 0; j < i; j++) {
			dizi = Base64.getEncoder().encode(data.getBytes());
			data = new String(dizi);
		}
		String sifrelenmis = new String(dizi) + MD5("" + ri);
		System.out.println("sifrelenmis" + sifrelenmis);
		return sifrelenmis;
	}

	public static String sifreCoz(String data, int i) {
		byte[] dizi = null;
		data = data.substring(0, data.length() - 32);
		for (int j = 0; j < i; j++) {
			dizi = Base64.getDecoder().decode(data.getBytes());
			data = new String(dizi);
		}
		String cozulmus = new String(dizi);
		System.out.println("cozulmus" + cozulmus);
		return cozulmus;
	}
}

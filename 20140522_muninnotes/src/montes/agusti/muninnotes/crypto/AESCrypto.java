package montes.agusti.muninnotes.crypto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class AESCrypto {
	private static String pass;
	static SecretKey sKey;
	private static FileInputStream in;
	private static FileOutputStream outFile;

	public static void wipe(File f) throws IOException {
		RandomAccessFile rndAccFile = new RandomAccessFile(f, "rw");
		for (int i = 0; i < rndAccFile.length(); i++) {
			rndAccFile.write(0);
		}
		rndAccFile.close();
	}

	public static SecretKey generateSecretKey(String text) throws Exception {
		byte[] data = text.getBytes("UTF-8");
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] aux = md.digest(data);
		byte[] clau = Arrays.copyOf(aux, 192 / 8);
		return new SecretKeySpec(clau, "AES");
	}

	public static void encode(File origen, String desti) throws Exception {
		sKey = generateSecretKey(pass);

		Cipher xifrat = Cipher.getInstance("AES/ECB/PKCS5Padding");
		xifrat.init(Cipher.ENCRYPT_MODE, sKey);

		byte[] dades = new byte[1024];
		byte[] resultat;

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		in = new FileInputStream(origen);
		int read = in.read(dades);
		while (read > 0) {
			resultat = xifrat.update(dades, 0, read);
			out.write(resultat);
			read = in.read(dades);
		}

		resultat = xifrat.doFinal();
		out.write(resultat);
		out.close();

		resultat = out.toByteArray();
		outFile = new FileOutputStream(desti);
		outFile.write(resultat);
		

		sKey = null;
	}

	public static void decode(File origen, String desti) throws Exception {
		sKey = generateSecretKey(pass);

		Cipher xifrat = Cipher.getInstance("AES/ECB/PKCS5Padding");
		xifrat.init(Cipher.ENCRYPT_MODE, sKey);

		byte[] dades = new byte[1024];
		byte[] resultat;

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		in = new FileInputStream(origen);
		int read = in.read(dades);
		while (read > 0) {
			resultat = xifrat.update(dades, 0, read);
			out.write(resultat);
			read = in.read(dades);
		}

		resultat = xifrat.doFinal();
		out.write(resultat);
		out.close();

		resultat = out.toByteArray();
		outFile = new FileOutputStream(desti);
		outFile.write(resultat);
		

		sKey = null;
	}

	public static void borrar(File f) throws Exception {
		wipe(f);
		f.delete();
	}

	public static void setPass(String newPass) throws Exception {
		pass = newPass;

	}
}

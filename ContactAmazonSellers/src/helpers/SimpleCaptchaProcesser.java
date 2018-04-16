package helpers;
import java.io.File;


import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

import javax.imageio.spi.IIORegistry;

import net.sourceforge.tess4j.Tesseract;
import sys.*;


public class SimpleCaptchaProcesser {
	static{
		IIORegistry registry = IIORegistry.getDefaultInstance();
		registry.registerServiceProvider(new
		com.sun.media.imageioimpl.plugins.tiff.TIFFImageWriterSpi());
		registry.registerServiceProvider(new 
		com.sun.media.imageioimpl.plugins.tiff.TIFFImageReaderSpi());
	}
	private String captchaFileName = SystemConfig.capchaDealingFolder+"captcha.jpg";;
	public void downloadCaptchaImg(String src) throws Exception {
		URL httpurl = new URL(src);
		HttpURLConnection con = (HttpURLConnection) httpurl.openConnection();
		con.setConnectTimeout(5 * 1000);
		InputStream is = con.getInputStream();
		byte[] bs = new byte[1024];
		int len;
		File f = new File(captchaFileName);
		if (!f.exists())
			f.createNewFile();
		OutputStream os = new FileOutputStream(f);
		while ((len = is.read(bs)) != -1) {
			os.write(bs, 0, len);
		}
		os.close();
		is.close();
	}
	
	public String recognize() throws Exception {
		Tesseract instance = new Tesseract();
		instance.setDatapath(SystemConfig.capchaDealingFolder);
		String result = instance.doOCR(new File(captchaFileName));
		Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");
		String iden = "";
		for (int i = 0; i < result.length(); i++) {
			if (pattern.matcher(String.valueOf(result.charAt(i))).matches())
				iden += String.valueOf(result.charAt(i));
		}
		if (iden.length() == 6)
			return iden;
		return null;
	}
	public String recognize(String path) throws Exception {
		Tesseract instance = new Tesseract();
		instance.setDatapath(SystemConfig.capchaDealingFolder);
		String result = instance.doOCR(new File(path));
		Pattern pattern = Pattern.compile("^[A-Z0-9]+$");
		String iden = "";
		for (int i = 0; i < result.length(); i++) {
			if (pattern.matcher(String.valueOf(result.charAt(i))).matches())
				iden += String.valueOf(result.charAt(i));
		}
//		if (iden.length() == 6)
			return iden;
//		return null;
	}
	
	public static void main(String[] args) {
		SimpleCaptchaProcesser p = new SimpleCaptchaProcesser();
		try {
			String s = p.recognize("D:/Captcha_mbtogsdsjl.jpg");
			System.out.println(s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

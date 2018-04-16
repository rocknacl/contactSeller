package helpers;

import java.net.InetAddress;
import java.util.Properties;
import java.util.Set;

public class LocalhostInfo {
	public static void getSystemProperties() {
		// InetAddress ad = InetAddress.getLocalHost();
		Properties prop = System.getProperties();
		Set<String> set = prop.stringPropertyNames();
		for (String name : set) {

			System.out.println(name + ":" + prop.getProperty(name));

		}

	}
	public static void main(String[] args){
		System.out.println(LocalhostInfo.getLocalHostName());
		System.out.println(LocalhostInfo.getLocalHostIP());
	}

	 public static String getLocalHostName(){
	        try{
	            InetAddress addr=InetAddress.getLocalHost();
	            return addr.getHostName();
	        }catch(Exception e){
	            return "";
	        }
	    }
	 
	 public static String getLocalHostIP(){
	        try{
	            InetAddress addr=InetAddress.getLocalHost();
	            return addr.getHostAddress();
	        }catch(Exception e){
	            return "";
	        }
	    }
}

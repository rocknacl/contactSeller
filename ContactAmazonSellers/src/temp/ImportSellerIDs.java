package temp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.io.IOUtils;

import helpers.ConnectionPool;

public class ImportSellerIDs {
	public static void main(String[] args){
		File file = new File("D:/CA");
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for(File f : files){
				System.out.println(f.getName());
				List<String> lines;
				try {
					lines = IOUtils.readLines(new FileInputStream(f),"utf-8");
					saveIDs(lines);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		}
	}
	private static void saveIDs(List<String> sellers){
		Connection conn = null;
		try {
			conn = ConnectionPool.getConnectionPool().getConnection();
			PreparedStatement pstmt = conn.prepareStatement("insert ignore into contactseller_seller_feedback_stastics_ca(seller_name,seller_id,country) values(?,?,'CA')");
			for(String line : sellers){
				String[] data = line.split("\t");
				pstmt.setString(1, data[0]);
				pstmt.setString(2, data[1]);
				pstmt.addBatch();
			}
			pstmt.executeBatch();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

}

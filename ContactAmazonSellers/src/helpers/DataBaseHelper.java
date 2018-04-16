package helpers;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DataBaseHelper {
	public static ArrayList<String[]> query(String sql, Connection connection)
			throws SQLException {
		Connection conn = connection;
		ArrayList<String[]> lines = new ArrayList<String[]>();

		Statement stmt = conn.createStatement();
		ResultSet result = stmt.executeQuery(sql);
		//System.out.println("Executing sql :" + sql);
		ResultSetMetaData md = result.getMetaData();
		int columnCount = md.getColumnCount();
		String[] head = new String[columnCount];
		for (int i = 0; i < columnCount; i++) {
			head[i] = md.getColumnLabel(i + 1);
		}		
		lines.add(head);
		while (result.next()) {
			String[] line = new String[columnCount];
			for (int i = 0; i < columnCount; i++) {
				line[i] = result.getString(i + 1);
			}
			lines.add(line);
		}

		return lines;
	}

	public static ArrayList<String[]> callProcedureLikeQuery(String sql,
			Connection conn) throws SQLException {
		ArrayList<String[]> lines = new ArrayList<String[]>();

		CallableStatement cStmt = conn.prepareCall(sql);
		ResultSet result = cStmt.executeQuery();
		//System.out.println("Executing sql :" + sql);
		ResultSetMetaData md = result.getMetaData();
		int columnCount = md.getColumnCount();
		String[] head = new String[columnCount];
		for (int i = 0; i < columnCount; i++) {
			head[i] = md.getColumnName(i + 1);
		}
		lines.add(head);
		while (result.next()) {
			String[] line = new String[columnCount];
			for (int i = 0; i < columnCount; i++) {
				line[i] = result.getString(i + 1);
			}
			lines.add(line);
		}

		return lines;
	}

	public static boolean execute(String sql, Connection connection)
			throws SQLException {
		Connection conn = connection;
		Statement stmt = conn.createStatement();
		boolean result = stmt.execute(sql);
		System.out.println("Executing sql :" + sql);
		stmt.close();
		return result;
	}
	
	public static void callProcedure(String sql, Connection conn) throws SQLException {
		CallableStatement cStmt = conn.prepareCall(sql);
		cStmt.executeQuery();
		System.out.println("Executing sql :" + sql);
	}


}

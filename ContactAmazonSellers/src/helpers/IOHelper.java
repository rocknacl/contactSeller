package helpers;

import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
//import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class IOHelper {
	public static void printStringList(ArrayList<String[]> lines) {
		for (String[] line : lines) {
			for (int i = 0; i < line.length; i++) {
				System.out.print(line[i] + "\t");
			}
		}
	}
//	public static void readExcel(String filename, ArrayList<String[]> data) {
//		try {
//			InputStream stream = new FileInputStream(filename);
//			Workbook wb = new XSSFWorkbook(stream);
//			Sheet sheet = wb.getSheetAt(0);
//			for (Row row : sheet) {
//				ArrayList<String> temp = new ArrayList<String>();
//				for (Cell cell : row) {
//					cell.setCellType(Cell.CELL_TYPE_STRING);
//					temp.add(cell.getStringCellValue().replace("\n", ";"));
//				}
//				String[] t = new String[temp.size()];
//				for (int i = 0; i < temp.size(); i++)
//					t[i] = temp.get(i);
//				data.add(t);
//			}
//			wb.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
	
	public static ArrayList<String[]> readcsv(String path){
		ArrayList<String> head = new ArrayList<String>();
		ArrayList<String[]> data = new ArrayList<String[]>();
		ArrayList<String[]> result = new ArrayList<String[]>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(path), "UTF-8"));
			String temp = reader.readLine();
			if(temp.charAt(0)!='('){
				temp = temp.substring(1);
			}
			String[] array = temp.substring(1, temp.length() - 1)
					.split("\",\"");
			for (int i = 0; i < array.length; i++) {
				head.add(array[i]);
			}
			while ((temp = reader.readLine()) != null) {
				array = temp.substring(1, temp.length() - 1).split("\",\"");
				data.add(array);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String[] firstRow = new String[head.size()];
		head.toArray(firstRow);
		result.add(firstRow);
		result.addAll(data);
		return result;
	}

	public static ArrayList<String[]> readtxt(String path) {
		ArrayList<String[]> result = new ArrayList<String[]>();
		String line;

		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(
					new FileInputStream(path), "UTF-8"));

			while ((line = bf.readLine()) != null) {
				String[] words = line.split("\t");
				result.add(words);
			}
			bf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;

	}

	public static ArrayList<String> getHead(ArrayList<String[]> wholetxt) {
		ArrayList<String> head = new ArrayList<String>();
		for (String s : wholetxt.get(0)) {
			head.add(s);
		}
		return head;
	}

	public static int getColumnIndexOfAHead(String columnName,
			ArrayList<String> head) {
		int i = 0;
		for (; i < head.size(); i++) {
			if (head.get(i).equalsIgnoreCase(columnName)) {
				return i;
			}
		}
		return -1;
	}

	public static ArrayList<String[]> getData(ArrayList<String[]> wholetxt) {
		@SuppressWarnings("unchecked")
		ArrayList<String[]> data = (ArrayList<String[]>) wholetxt.clone();
		data.remove(0);
		return data;
	}

	// Get column names of a ResultSet
	public static ArrayList<String> getColumnNamesFromResultSet(ResultSet rs)
			throws SQLException {
		ArrayList<String> columnNames = new ArrayList<String>();
		ResultSetMetaData md = rs.getMetaData();
		int count = md.getColumnCount();
		for (int i = 0; i < count; i++) {
			columnNames.add(md.getColumnName(i + 1));
		}
		return columnNames;
	}

	// Generate txt data with column head from ResultSet
	public static ArrayList<String[]> ResultSetToStringList(ResultSet rs)
			throws SQLException {
		ArrayList<String[]> lines = new ArrayList<String[]>();
		ArrayList<String> headList = getColumnNamesFromResultSet(rs);
		try {
			String[] head = new String[headList.size()];
			for (int i = 0; i < headList.size(); i++) {
				head[i] = headList.get(i);

			}
			lines.add(head);
			while (rs.next()) {
				String[] line = new String[headList.size()];

				for (int i = 0; i < headList.size(); i++) {
					if (rs.getObject(i + 1) != null)
						line[i] = rs.getObject(i + 1).toString();

				}
				lines.add(line);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public static void writetxt(String filename, ArrayList<String[]> data){
		try{
			File f = new File(filename);
			if (f.getParentFile() != null && !f.getParentFile().exists())
				f.getParentFile().mkdirs();
			if (!f.exists())	f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			FileChannel fc = fos.getChannel();
			FileLock lock = fc.lock();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
			if(data!=null&&!data.isEmpty()){
			for (int i = 0; i < data.size(); i++) {
				for (int j = 0; j < data.get(i).length; j++)
					writer.append(data.get(i)[j] + "\t");
				writer.append("\r\n");
				writer.flush();
			}}
			lock.release();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<String[]> trimFile(String fileName,
			String tableHeadIdentifier) {
		ArrayList<String[]> lines = IOHelper.readtxt(fileName);
		ArrayList<String[]> postProcessed = (ArrayList<String[]>) lines.clone();
		IOHelper.printStringList(postProcessed);
		for (int n = 0; n < lines.size(); n++) {
			String[] line = lines.get(n);
			boolean containsMerchantSKU = false;
			for (int i = 0; i < line.length; i++) {
				if (line[i].equals(tableHeadIdentifier)) {
					containsMerchantSKU = true;
					break;
				}
			}
			if (containsMerchantSKU == true) {
				break;
			} else {
				postProcessed.remove(0);
			}
		}
		// try {
		// IOHelper.writetxt("postProcessed.txt", postProcessed);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		return postProcessed;
	}

	public static String getCurrentFormattedDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String formattedDate = df.format(new Date());
		return formattedDate;
	}

	public static boolean isGood(String s) {
		if (s == null || s.isEmpty() || s.equals("null"))
			return false;
		else
			return true;
	}

	public static boolean isGood(Object s) {
		if (s == null || s.toString().isEmpty() || s.equals("null"))
			return false;
		else
			return true;
	}

	public static boolean isIntString(String str) {
		if (str == null) {
			return false;
		}
		if (isGood(str))
			return str.matches("^-?\\d+$");
		else
			return false;
	}
	public static boolean isDecimal(String str){
		if(str==null){
			return false;
		}
		if(isGood(str))
			return str.matches("^-?\\d+.?\\d*$");
		else
			return false;
	}

	public static void selectAndAddStoreInformation(String oldFilename, String newFilename, String[] oldColumns, String[] newColumns, String store) throws IOException{
		ArrayList<String[]> data = readtxt(oldFilename);
		int[] index = new int[oldColumns.length];
		for(int i=0; i<oldColumns.length; i++){
			int j=0;
			for(;j<data.get(0).length;j++){
				if(oldColumns[i].equals(data.get(0)[j])){
					index[i] = j;
					break;
				}
			}
			if(j==data.get(0).length)	index[i]=-1;
		}
		ArrayList<String[]> newData = new ArrayList<String[]>();
		newData.add(newColumns);
		for(int i=1;i<data.size();i++){
			String[] temp = new String[oldColumns.length];
			for(int j=0;j<oldColumns.length;j++){
				if(index[j]!=-1)	temp[j] = data.get(i)[index[j]];
				else if(oldColumns[j].equals("Store_Name"))	temp[j] = store;
			}
			newData.add(temp);
		}
		writetxt(newFilename, newData);
	}
	
//	public static void failureRecord(Exception e){
//		try{
//			String failFile = PathManager.logPath+"fails.txt";
//			FileOutputStream output = new FileOutputStream(failFile, true);
//			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
//			writer.write(e.getClass().getName() + e.getMessage() + "\r\n");
//			for(StackTraceElement s: e.getStackTrace()){
//				writer.write("\t" + s + "\r\n");
//			}
//			writer.flush();
//			writer.close();
//		}catch(Exception e1){
//			e1.printStackTrace();
//		}
//	}
	
	public static void getFileName(ArrayList<String> filename, String filepath) {
		File f = new File(filepath);
        File[] file = f.listFiles();           
        if(file!=null){
        	for(int i=0;i<file.length;i++){   
                if(file[i].isFile()){  
                   filename.add(file[i].getPath());   
                }else if(file[i].isDirectory()){   
                    getFileName(filename, file[i].getPath());   
                } 
            } 
        }
	}
	
	public static void copyFile(String oldFilename, String newFilename){
		try{
			File f = new File(newFilename);
			if(!f.getParentFile().exists())	f.getParentFile().mkdirs();
			if(!f.exists())	f.createNewFile();
			FileInputStream input = new FileInputStream(oldFilename);
			FileOutputStream output = new FileOutputStream(newFilename);
			FileChannel fc = output.getChannel();
			FileLock lock = fc.lock();
			int in = input.read();
			while(in!=-1){
				output.write(in);
				in = input.read();
			}
			lock.release();
			input.close();
			output.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void moveFile(String oldFilename, String newFilename){
		copyFile(oldFilename, newFilename);
		File file = new File(oldFilename);
		file.delete();
	}
	
	public static void addFile(String oldFilename, String newFilename, boolean addFirstLine){
		try{
			File f = new File(newFilename);
			if(!f.getParentFile().exists())	f.mkdirs();
			if(!f.exists())	f.createNewFile();
			FileInputStream input = new FileInputStream(oldFilename);
			BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			FileOutputStream output = new FileOutputStream(newFilename, true);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
			FileChannel fc = output.getChannel();
			FileLock lock = fc.lock();
			String temp = null;
			if(!addFirstLine)	temp = reader.readLine();
			while((temp = reader.readLine())!=null){
				writer.write(temp + "\r\n");
				writer.flush();
			}
			lock.release();
			reader.close();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void addString(String filename, String content){
		File f = new File(filename);
		if(!f.getParentFile().exists())	f.mkdirs();
		try{
			if(!f.exists())	f.createNewFile();
			ArrayList<String[]> data = readtxt(filename);
			for(String[] d: data){
				for(String l: d){
					if(content.equals(l))	return;
				}
			}
			FileOutputStream output = new FileOutputStream(filename, true);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
			writer.write(content + "\t");
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}

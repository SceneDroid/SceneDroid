package main.java.analyze.utils.output;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.google.common.io.Files;

/**
 * File related IO
 * 
 * @author 79940
 */
public class FileUtils {

	/**
	 * judge whether a file exist or not
	 * 
	 * @param folder
	 */
	public static boolean isFileExist(String file) {
		File f = new File(file);
		return f.exists();
	}

	/**
	 * create folder if it does not exist
	 * 
	 * @param folder
	 */
	public static void createFolder(String folder) {
		File fd = new File(folder);
		if (!fd.exists())
			fd.mkdirs();
	}

	/**
	 * create file if it does not exist
	 * 
	 * @param file
	 */
	public static void createFile(String file) {
		File f = new File(file);
		if (!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	/**
	 * copy dir from sourcePath to newPath
	 * 
	 * @param sourcePath
	 * @param newPath
	 */
	public static void copyDir(String sourcePath, String newPath) {
		File file = new File(sourcePath);
		String[] filePath = file.list();

		createFolder(newPath);
		for (int i = 0; i < filePath.length; i++) {
			if ((new File(sourcePath + File.separator + filePath[i])).isDirectory()) {
				copyDir(sourcePath + File.separator + filePath[i], newPath + File.separator + filePath[i]);
			}

			if (new File(sourcePath + File.separator + filePath[i]).isFile()) {
				copyFile(sourcePath + File.separator + filePath[i], newPath + File.separator + filePath[i]);
			}

		}
	}

	/**
	 * copy file from oldf to newf
	 * 
	 * @param oldf
	 * @param newf
	 */
	public static void copyFile(String oldf, String newf) {
		File f1 = new File(oldf);
		File f2 = new File(newf);
		try {
			Files.copy(f1, f2);
		} catch (IOException e) {
			System.err.println(oldf + " not exist.");
		}
	}

	/**
	 * move file from oldf to newf
	 * 
	 * @param oldf
	 * @param newf
	 */
	public static void moveFile(String oldf, String newf) {
		File f1 = new File(oldf);
		File f2 = new File(newf);
		try {

			Files.move(f1, f2);
		} catch (IOException e) {
			System.err.println(oldf);
			System.err.println(newf);
			e.printStackTrace();
		}
	}

	/**
	 * delete folder
	 * 
	 * @param folderPath
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // ɾ����������������
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // ɾ�����ļ���
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * delete file
	 * 
	 * @param folderPath
	 */
	public static void delFile(String filePath) {
		File f = new File(filePath);
		f.delete();
	}

	/**
	 * delete folder
	 * 
	 * @param folderPath
	 */
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// ��ɾ���ļ���������ļ�
				delFolder(path + "/" + tempList[i]);// ��ɾ�����ļ���
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * get String From File
	 * 
	 * @param filename
	 * @return
	 */
	public static String getStringFromFile(String filename) {
		String res = "";
		File file = new File(filename);
		InputStream instream = null;
		InputStreamReader inputreader = null;
		BufferedReader buffreader = null;
		try {
			instream = new FileInputStream(file);
			if (instream != null) {
				inputreader = new InputStreamReader(instream, "gbk");
				buffreader = new BufferedReader(inputreader);
				String line;
				while ((line = buffreader.readLine()) != null) {
					res += line + "\n";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				buffreader.close();
				inputreader.close();
				instream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	/**
	 * get String From Tcas File
	 * 
	 * @param filename
	 * @return
	 */
	public static String getStringFromTcas(String filename, Set<String> historyICC) {
		String res = "";
		File file = new File(filename);
		InputStream instream = null;
		InputStreamReader inputreader = null;
		BufferedReader buffreader = null;
		try {
			instream = new FileInputStream(file);
			if (instream != null) {
				inputreader = new InputStreamReader(instream, "gbk");
				buffreader = new BufferedReader(inputreader);
				String line;
				while ((line = buffreader.readLine()) != null) {
					if (line.startsWith("#"))
						continue;
					if (!historyICC.contains(line)) {
						res += line + "\n";
						historyICC.add(line);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				buffreader.close();
				inputreader.close();
				instream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	/**
	 * get set from file
	 * 
	 * @param filename
	 * @return
	 */
	public static Set<String> getSetFromFile(String filename) {
		Set<String> mySet = new HashSet<String>();
		File file = new File(filename);
		InputStream instream = null;
		InputStreamReader inputreader = null;
		BufferedReader buffreader = null;
		try {
			instream = new FileInputStream(file);
			if (instream != null) {
				inputreader = new InputStreamReader(instream, "gbk");
				buffreader = new BufferedReader(inputreader);
				String line;
				while ((line = buffreader.readLine()) != null) {
					mySet.add(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (buffreader != null) {
					buffreader.close();
					inputreader.close();
					instream.close();
				} else {
					return null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return mySet;
	}

	/**
	 * get list from file
	 * 
	 * @param filename
	 * @return
	 */
	public static List<String> getListFromFile(String filename) {
		List<String> list = new ArrayList<String>();
		File file = new File(filename);
		if (!file.exists())
			return list;
		InputStream instream = null;
		InputStreamReader inputreader = null;
		BufferedReader buffreader = null;
		try {
			instream = new FileInputStream(file);
			if (instream != null) {
				inputreader = new InputStreamReader(instream, "gbk");
				buffreader = new BufferedReader(inputreader);
				String line;
				while ((line = buffreader.readLine()) != null) {
					// line = line.replace("\n", "");
					list.add(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (buffreader != null) {
					buffreader.close();
					inputreader.close();
					instream.close();
				} else {
					return null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	public static Map<String, Integer> getMapFromFile(String filename) {
		Map<String, Integer> myMap = new HashMap<String, Integer>();
		File file = new File(filename);
		InputStream instream = null ;
		InputStreamReader inputreader = null;
		BufferedReader buffreader = null;
		try {
			instream = new FileInputStream(file);
			if (instream != null) {
				inputreader = new InputStreamReader(instream, "gbk"); 
				buffreader = new BufferedReader(inputreader);
				String line;
				while ((line = buffreader.readLine()) != null) {
					line = line.replace("\n", "");
					String ss[] = line.split("\t\t");
					myMap.put(ss[2], Integer.parseInt(ss[0]));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				buffreader.close();
				inputreader.close();
				instream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return myMap;
	}
	/**
	 * output List to file
	 * 
	 * @param sta_res_dir
	 * @param name
	 * @param resList
	 */
	public static void writeList2File(String sta_res_dir, String name, List<String> resList, boolean flag) {
		String filename = sta_res_dir + File.separator + name;
		File f = new File(filename);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(f,flag));
			for (String s : resList) {
				writer.write(s + "\n");
			}
			writer.write("\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * deserialize file to obj
	 * 
	 * @param file_name
	 * @return
	 */
	public static Object deserialize(String file_name) {
		Object obj = null;
		try {
			FileInputStream fis = new FileInputStream(file_name);
			ObjectInputStream ois = new ObjectInputStream(fis);
			obj = ois.readObject();
			ois.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(file_name + " deserialize fail");
		}
		return obj;
	}

	/**
	 * serialize obj to file_name
	 * 
	 * @param file_name
	 * @param obj
	 */
	public static void serialize(String file_name, Object obj) {
		try {
			FileOutputStream fos = new FileOutputStream(file_name);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(file_name + " searilize fail");
		}
	}

	public static String readJsonFile(String fileName) {
		String jsonStr = "";
		try {
			File jsonFile = new File(fileName);
			if (!jsonFile.exists())
				return "";
			FileReader fileReader = new FileReader(jsonFile);

			Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
			int ch = 0;
			StringBuffer sb = new StringBuffer();
			while ((ch = reader.read()) != -1) {
				sb.append((char) ch);
			}
			fileReader.close();
			reader.close();
			jsonStr = sb.toString();
			return jsonStr;
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * end for xml write
	 * 
	 * @param dir
	 * @param file
	 * @param document
	 * @throws IOException
	 */
	public static void xmlWriteEnd(String dir, String file, Document document) throws IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		File f = new File(dir + file);
		XMLWriter writer = new XMLWriter(new FileOutputStream(f), format);
		writer.setEscapeText(true);
		writer.write(document);
		writer.close();

	}

	/**
	 * prepare for xml write
	 * 
	 * @param dir
	 * @param file
	 * @param appendToExist
	 * @return
	 * @throws DocumentException
	 */
	public static Document xmlWriterBegin(String dir, String file, boolean appendToExist) throws DocumentException {
		Document document = null;
		if (FileUtils.isFileExist(dir + file) && appendToExist) {
			File xmlFile = new File(dir + file);
			SAXReader reader = new SAXReader();
			document = reader.read(xmlFile);
		} else {
			FileUtils.createFolder(dir);
			FileUtils.createFile(dir + file);
			document = DocumentHelper.createDocument();
			document.addElement("root");
		}
		return document;
	}

	/**
	 * write content to filename, flag is true--append to file
	 * 
	 * @param filename
	 * @param content
	 * @param flag
	 */
	public static synchronized void writeText2File(String filename, String content, boolean flag) {
		File f = new File(filename);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(f, flag));
			writer.write(content);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("can not write");
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}

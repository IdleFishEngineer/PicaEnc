import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
public class SQLMain {

	static String dbName = "";
	static String dbPath = "";	//"E:\\PICA_SQL\\databases\\com_picacomic_fregata.db"
	static String dbUrl = "" ;	//E:\\%TOOL0\\test.db ->修改默认为当前目录下
	static Connection conn = null;
	static String comicId = "";
	static String episodeId = "";
	static String folderName = "";	//"5d6d31aa4539c939892cf861" ,"5e7be52ae98fb9063c5d3a5f"
	static String pdfName = "";
	static String comicName = "";
	
	final static String Title = "TITLE";
	final static String ComicId = "COMIC_ID";
	final static String EpisodeId = "EPISODE_ID";
	public static final String ComicPageId = "COMIC_PAGE_ID";
	public static final String MediaOriginalName = "MEDIA_ORIGINAL_NAME";
	public static final String MediaPath = "MEDIA_PATH";
	public static final String ComicTitle = "";
	public static final String EpisodeTitle = "";
	
	final static String Directory_sim = "-d";	//executed at file manage
	final static String Directory = "-directory";
	
	final static String AllFile_sim = "-a";	//executed at file manage
	final static String AllFile = "-all";
	
	final static String Fast_sim = "-f";//
	final static String Fast = "-fast";
	
	final static String Encrypt_sim = "-e";
	final static String Encrypt = "-encrypt";
	
	final static String Rename_sim = "-r";	//use union search(COMIC.TITLE+EPISODE.TITLE)
	final static String Rename	= "-rename";
	
	final static String Help_sim = "-h";
	final static String Help = "-help";
	
	final static String Pdf = "-pdf";
	final static String PDF = ".pdf";
	
	final static String EnableLog_sim = "-l";
	final static String EnableLog = "-log";
	
	final static String Info_sim = "-i";
	final static String Info = "-info";
	
	static boolean pdf = false;
	static boolean allFile = false;
	static boolean directory = false;
	static boolean fastMode = false;
	static boolean enableLog = false;
	static boolean rename = false;
	
public static void main(String args[]) throws ClassNotFoundException, IOException {

	if(args!=null && args.length>0) {
		if(args[0] != null && args[0].endsWith(".db")) {
			dbName = args[0];
			File file = new File(dbName);
			if(file.exists()) {
				dbPath = file.getAbsolutePath().substring(0,file.getAbsoluteFile().toString().lastIndexOf("\\"));
				folderName = getFolderName(file);
				dbUrl = JdbcUtil.createDbUrl(dbPath, dbName);
			}
			Log.V(SQLMain.class, "Folder's name is "+folderName+",database located at " + dbUrl);
		}else {
			Log.V(SQLMain.class, args[0]+" is not a database file.");
		}
		
	}

	JdbcUtil<ComicBean> jdbcUtil = new JdbcUtil<ComicBean>(new OnValueFormatParse<ComicBean>() {
		@Override
		public ComicBean onDataParse(String... string) {
			if(string.length == 6) {
				return new ComicBean(string[0],string[1],string[2],string[3],string[4],string[5]);
			}else if(string.length == 4) {
				return new ComicBean(string[0],string[1],string[2],string[3]);
			}else if(string.length == 2) {
				return new ComicBean(string[0],string[1]);
			}else {
				return new ComicBean();
			}
		}

		@Override
		public void onDataCollect(String data) {
			setComicName(eliminateFileNameSenstive(data));
		}});
	

	checkParameters(args);
	
	if(dbUrl.trim().length()==0||dbUrl=="") {
		Log.V(SQLMain.class, "Try to find "+JdbcUtil.DFTDB_NAME+" in current folder.");
		try {
			File file = new File(JdbcUtil.DFTDB_NAME);
			
			if(!file.exists()) {	
				//TODO input file name directly.
				Log.V(SQLMain.class,"File not exists,please input database's file name.(print \"quit\" or \"exit\" to quit inputing.)");
				Scanner scanner = new Scanner(System.in);
				while(!file.exists()) {//|| print some key to exit
					String nextLine = scanner.nextLine();
					if(nextLine.equals("exit") || nextLine.equals("quit")) break;
					if(new File(nextLine).exists()) {
						file = new File(nextLine);
						break;
					}else {
						Log.V(SQLMain.class,nextLine+" is not exists!");
					}
				}
				
				if(!file.exists()) {
					Log.V(SQLMain.class, "No file was selected,exit.");
					System.exit(0);
				}
			}
			
			dbName = file.getName();
			dbPath = getDirectory(file);
			if(!directory) {	//folderName!=null && 
				folderName = getFolderName(file);
			}
			dbUrl = JdbcUtil.createDbUrl(dbPath, dbName);
			Log.V(SQLMain.class, "Folder's name is "+folderName+",database located at " + dbUrl);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	ArrayList<ComicAssembleBean> comicList = new ArrayList<>();

	try {	
		conn = JdbcUtil.createConnection(dbUrl);
		
		//if allFile mode for(...)
		if(allFile) {	//Dead code
			ArrayList<String> fileNameList = new ArrayList();
			File folder = new File("");
			String eid,cid;
			for(File file:folder.listFiles()) {
				ComicAssembleBean oneComic = new ComicAssembleBean();
				if(file.isDirectory()) {
					String fileName = file.getName();
					fileNameList.add(file.getName());
					if(isEpisodeId(fileName)) {
						eid = fileName;
						cid = getComicId(jdbcUtil,fileName);
						
						//创建一个以ComicTitle命名的文件夹，把Episode全放里面
						
						oneComic.setEpisodeName(getEpisodeName(eid));
						oneComic.setComicName(getComicName(cid));
					}else if(isComicId(fileName)){
						eid="";
						cid = fileName;
						oneComic.setComicName(getComicName(cid));
					}else continue;
					Log.V(SQLMain.class, file.getName()+"is not an episode_id or a comic_id");
					
					oneComic.setComicList(getComicList(eid,cid));
				}
			}
		}else {
			
			
		}
		
		

		if(jdbcUtil.executeQuery_find(conn, querySql(JdbcUtil.COMIC_DETAIL,Title),	
				ComicId +" = \'"+folderName+"\'",Title)) {
				comicId = folderName;
				Log.V(SQLMain.class, folderName +" is a comicId,comic'name is "+comicName);
			
		}else {
			Log.V(SQLMain.class,"FolderName maybe is a episodeId,try to match Comic Id at database.");
			comicId = getComicId(jdbcUtil,folderName);
			episodeId = folderName;
			Log.V(SQLMain.class,"Episode Id is"+folderName+",Comic Id is "+comicId );
		}
		
		if(comicId.trim().length()>0) {
			ArrayList<ComicBean> comics = new ArrayList();
			if(episodeId.trim().length()>0) {
				comics = jdbcUtil.executeQuery(conn,querySql(JdbcUtil.DOWNLOAD_COMIC_PAGE,ComicId,EpisodeId,MediaPath,MediaOriginalName),
						"COMIC_ID = \'"+comicId+"\' AND EPISODE_ID = \'"+episodeId+"\'",
						ComicId,EpisodeId,MediaPath,MediaOriginalName
						);
			}else {
				 comics = jdbcUtil.executeQuery(conn,querySql(JdbcUtil.DOWNLOAD_COMIC_PAGE,ComicId,EpisodeId,MediaPath,MediaOriginalName),
						 ComicId + " = \'"+comicId+"\'",
						ComicId,EpisodeId,MediaPath,MediaOriginalName
						);
			}
				
				for(int i=0;i<comics.size();i++) 
					Log.V(SQLMain.class,""+comics.get(i)+"\n",enableLog);
				
				//show progress
				
				//judge -d & -a
				
				if(directory) {
					FileManage.rename(comics,folderName);
					if(rename && new File(new File(folderName).getAbsolutePath()).renameTo(new File(comicName))) {
						Log.V(SQLMain.class, "Folder's name has been renamed to "+comicName);
						if(comicName!="") pdfName = comicName+PDF;
						if(pdf) FileManage.generatePdfDocument(new File(comicName).getAbsolutePath()+File.separator, pdfName);
					}else {
						Log.E(SQLMain.class, "Fail to rename folder's name.(MayBe there are SENSITIVE symbols in the comic's name)");
						new File(comicName).mkdir();
					}
				}else if(allFile) {
					Log.W(SQLMain.class, "I AM TOO BUSY TO FINISH ALL_FILE MODE,SORRY ABOUT THAT.(YOU CAN CONTACT ME IF YOU REALY NEED IT,EMAIL ME+ 1504416626@qq.com)");
				}else {
					FileManage.rename(comics);
					if(rename&&comicName!="") pdfName = comicName+PDF;
					if(pdf) FileManage.generatePdfDocument(new File("").getAbsolutePath()+File.separator, pdfName);
				}
		//TODO show analystic
		}else{
			Log.V(SQLMain.class, "Unable to get comic_id.");
			//TODO 提供其他获取文件漫画名方式
		}
		
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		Log.V(SQLMain.class, "Data-base's URL is "+dbUrl);
		e.printStackTrace();
		System.exit(0);
	} finally {
		try {
			if(conn != null) {
				conn.close();
			}	
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
}

@Deprecated
public static String getCurrentFilePath(Class<?> clazz) {
	return new File(clazz.getResource("/").getPath()).toString();
}

//TODO TEST
public static String getComicId(JdbcUtil<ComicBean> jdbcUtil,String episodeId) throws SQLException {
	String comicId;
	ArrayList<ComicBean> comics = jdbcUtil.executeQuery(conn,querySql(JdbcUtil.DOWNLOAD_COMIC_EPISODE,ComicId,EpisodeId),
			EpisodeId + " = \'" + episodeId + "\'",
			ComicId,EpisodeId);
	if(comics.size()>0) {
		for(@SuppressWarnings("unused") ComicBean comic:comics) Log.V(SQLMain.class, comics.toString(),enableLog);
		comicId = comics.get(0).getComic_id();
		
		//TODO get comicName
		if(jdbcUtil.executeQuery_find(conn, 
				"SELECT " + 
				"comic.TITLE," + "episode.TITLE" + 
				" FROM " + 
				JdbcUtil.COMIC_DETAIL+ " as comic ," + JdbcUtil.DOWNLOAD_COMIC_EPISODE +" as episode" ,//THERE MUST BE A SPACE BEFORE "ON"
				" comic.COMIC_ID = episode.COMIC_ID "+	//base sql
				" AND comic.COMIC_ID = \""+comicId+"\" "+ "AND episode.EPISODE_ID = \""+episodeId+"\" "
				,2,"_")) {
			Log.V(SQLMain.class, "Comic name is "+comicName);
		}else {
			Log.E(SQLMain.class, "Unable to getComicName");
		}
		
		return comicId;
		}
	return "";
}


public static String getDirectory(File file) {
	return file.getAbsolutePath().substring(0,file.getAbsoluteFile().toString().lastIndexOf("\\"));
}

public static String getFolderName(File file) {
	String directory = getDirectory(file);
	Log.V(SQLMain.class, "Directory is "+ directory+"\n",enableLog);
	/**Use \\\\ to reprsents \ in regrex*/
	return directory.split("\\\\")[directory.split("\\\\").length-1];
	
}

public static String querySql(String tableName,String... paramName) {
	StringBuffer sb = new StringBuffer();
	sb.append("select ");
	for(String str:paramName) {
		sb.append(str);
		if(!str.equals(paramName[paramName.length-1])) {
			sb.append(",");
		}
	}
	sb.append(" from " + tableName);
	Log.V(SQLMain.class, sb.toString()+"\n",enableLog);
	return sb.toString();
}

@Deprecated
public static boolean setFolderName(String directory) {
	File file = new File(directory);
	if(file.exists()) folderName = getDirectory(file);
	
	return false;
}

public static String getComicName() {
	return comicName;
}

public static void setComicName(String string) {
	comicName = string;
}



//Add
public static boolean isEpisodeId(String fileName) {
	try {
		return JdbcUtil.executeQuery_find(conn,querySql(JdbcUtil.DOWNLOAD_COMIC_EPISODE,EpisodeId),EpisodeId + " = \'"+ fileName+"\'");
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return false;
}

//Add
public static boolean isComicId(String fileName) {
	try {
		return JdbcUtil.executeQuery_find(conn,querySql(JdbcUtil.COMIC_DETAIL,ComicId),ComicId + " = \'"+ fileName+"\'");
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return false;
}


//Add
public static ComicAssembleBean getComic(String comicId,String episodeId) {
	
	return null;
}

//获取漫画信息
public static String getComicInfo(String comicId) throws SQLException {
	return JdbcUtil.executeQuery_info(conn, 
									querySql(JdbcUtil.COMIC_DETAIL,ComicId), 
									ComicId + " = \'"+ comicId+"\'"
									);
}

//通过章节名获取漫画名
public static String getComicId(String episodeId) throws SQLException {
	return JdbcUtil.executeQuery_find(conn,
								querySql(JdbcUtil.DOWNLOAD_COMIC_EPISODE,"EPISODE_ID"), 
								"EPISODE_ID = \'" + episodeId + "\'", 
								1, 
								"", 
								ComicId);
}

//获取章节名
public static String getEpisodeName(String episodeId) throws SQLException {
	return JdbcUtil.executeQuery_find(conn,
								querySql(JdbcUtil.DOWNLOAD_COMIC_EPISODE,Title), 
								"EPISODE_ID = \'"+ episodeId + "\'", 
								1, 
								"", 
								Title);
}

//获取漫画名
public static String getComicName(String comicId) throws SQLException {
	return JdbcUtil.executeQuery_find(conn, 								//数据库连接
								querySql(JdbcUtil.COMIC_DETAIL,Title),		//搜索语句，相当于： SELECT TITLE FROM DB_COMIC_OBJECT_DETAIL
								ComicId + "= \'"+ comicId+"\'", 				//条件语句,相当于 WHERE COMIC_ID = 'comicId'
								1, 											//数据长度，1
								"", 										//多条数据拼接分隔
								Title);										//结果的排序
	
}

//
public static ArrayList<ComicBean> getComicList(String episodeId,String comicId){
	
	ArrayList<ComicBean> comicList = new ArrayList();
	
	try {
		comicList = getComicList(conn,
								"",
								"");
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return comicList;
}




public static ArrayList<ComicBean> getComicList(Connection conn,String comicId,String episodeId) throws SQLException {
	ArrayList<ComicBean> comics = new ArrayList<>();
	Statement statement = conn.createStatement();
	statement.execute(querySql());
	
	
	
	return comics;
}


public static String eliminateFileNameSenstive(String fileName) {
	String newName = fileName.length()>=255?fileName.substring(0,255):fileName;
	
	return newName.replace('?', '!')
			.replace('\\', '_')
			.replace('/', '%')
			.replace('*', '^')
			.replace('<', '(')
			.replace('>', ')')
			.replace('|', '$');
}


//use enum to replace
public static boolean isParameter(String text) {
	if(text.equals(Help)||text.equals(Help_sim)
			||text.equals(Directory_sim)||text.equals(Directory)
			||text.equals(AllFile)||text.equals(AllFile_sim)
			||text.equals(Fast)||text.equals(Fast_sim)
			||text.equals(pdf)
			||text.equals(EnableLog)||text.equals(EnableLog_sim)
			||text.equals(Rename)||text.equals(Rename_sim)
			)
	return true;
	return false;
}

public static void checkParameters(String[] args) {
	if(args.length==0) Log.V(SQLMain.class, "You can add running-parameter -h or -help to get usage info.(this.jar -h | this.jar -help,\"this\" is the filename)");
	
	for(int i=0;i<args.length;i++) {
		switch(args[i]) {
		case Help:
		case Help_sim:
	Log.V(SQLMain.class, "Usage of <this.jar>(\"this\" is how you named this jar.):\n"
			+ "xx.db\t to set attached database,MUST BE SETED AS THE FIRST PARAMETER. \n"
			+ ".e.g \t this.jar com_picacomic_fregata.db \n"
			+ "-d or -directory folder's name \t to decrypt files under this folder,the folder's name should be an EPISODE_ID or a COMIC_ID.) \n"
			+ ".e.g \t this.jar xx.db -d 123456 \n"
			+ "-a or -all \t to decrypt all folder'file under this folder. \n"
			+ ".e.g \t this.jar xx.db -a \n"
			+ "-pdf pdf's name \t generate pdf files while decrypt files.	\n"
			+ ".e.g \t this.jar xx.db -pdf \n"
			+ "-r or -rename \t to rename folder's name to original comic's name.It can be only works at DIRECTORY AND ALL_FILE MODE\n"
			+ "because it's not legical to rename a folder while you was operating in it.\n"
			+ "In Windows OS,File' name can't contains character below:? \\ / * < > |,so be replaced by ! _ % ^ ( ) $ \n"
			+ "-l or -log \t to see all logs and details.\n"
			+ "some parameters can be used at the same time;such as -d fileAddress -pdf -r -l -f \n"
			);
	//Log.V(SQLMain.class, "-----------------------------------------------------------------------------------------------------------");
			System.exit(0);
			break;
		case Directory:
		case Directory_sim:
			if(allFile) {
				Log.E(SQLMain.class, "Directory mode and AllFile mode can't be set at the same time.(Still run in AllFile mode)");
				return;
				}
				directory = true;
				if(i+1==args.length||isParameter(args[i+1])) {
					Log.W(SQLMain.class, "Directory is not setted!");
					System.exit(0);
				}else {
					File file = new File(args[i+1]);
					if(file.exists()) {
						folderName = file.getName();
						Log.W(SQLMain.class, "Directory has been setted to "+ folderName);
					}else {
					Log.W(SQLMain.class,"File"+args[i+1]+" is not exist.");	
					}
				}
				
				break;
		case AllFile:
		case AllFile_sim:
			if(directory) {
				Log.E(SQLMain.class, "Directory mode and AllFile mode can't be set at the same time.(Still run in Directory mode)");
				return;
				}
				allFile = true;
				break;
				
		case Fast_sim:
		case Fast:
			FileManage.setFastMode();
			break;
				
		case Pdf:
				pdf = true;
				if(i+1==args.length||isParameter(args[i+1])) {
					Log.W(SQLMain.class,"Pdf name is not setted",enableLog);
					pdfName = folderName+PDF;
				}else {
					if(args[i+1].endsWith(PDF))
						pdfName = args[i+1];
					else
						pdfName = args[i+1]+PDF;
				}
				break;
				
		case EnableLog_sim:		
		case EnableLog:	
				enableLog = true;
				JdbcUtil.setEnableLog();
				FileManage.setEnableLog();
				break;
				
		case Rename:
		case Rename_sim:
			if(!directory&&!allFile) Log.W(SQLMain.class,"You should set this program run as directory or all-file mode first.(only work on pdf file.)");
				rename = true;
				break;		
				
		}
	}
}

}

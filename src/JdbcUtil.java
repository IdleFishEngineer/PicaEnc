import java.awt.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class JdbcUtil<T> {
	
	static boolean enableLog  = false;
	
	OnValueFormatParse<T> onValueFormatParse;
	
	
	public JdbcUtil() {
		
	}
	
	public JdbcUtil(OnValueFormatParse<T> onValueFormatParse) {
		this.onValueFormatParse = onValueFormatParse;
	}
	
	enum ComicInfo{
		Title("TITLE","����"),
		Author("AUTHOR","����"),
		Description("DESCRIPTION","����"),
		Categories("CATEGORIES","���"),
		Tags("TAGS","��ǩ"),
		Chinease("CHINEASE_TEAM","����"),
		CoverImage("THUMB_ORIGINAL_NAME","����"),
		EpisodeCount("EPISODE_COUNT","�½���Ŀ"),
		Pages("PAGES","ҳ��"),
		CreatorName("CREATOR_NAME","����������"),
		Create("CREATED_AT","����ʱ��"),
		CreatorGender("CREATOR_GENDER","�������Ա�"),
		DownloadStatus("DOWNLOAD_STATUS","����״̬"),
		DownloadedAt("DOWNLOADED_AT","����ʱ�䣨����"),
		IsFavourite("IS_FAVOURITE","�Ƿ�ϲ��"),
		LastViewTime("LAST_VIEW_TIMESTAMP","�ϴ��Ķ�ʱ��"),
		Views("VIEWS","�ۿ���"),
		LikeCount("LIKES_COUNT","ϲ����"),
		Comments("COMMENTS_COUNT","������"),
		UpdatedAt("UPDATED_AT","����ʱ��");
		
		
		final String property;
		final String translation;
		ComicInfo(String property,String translation){
			this.property = property;
			this.translation = translation;
		}
	}
	
	
	public static final String DFTDB_NAME ="com_picacomic_fregata.db";
	public static final String COMIC_DETAIL= "DB_COMIC_DETAIL_OBJECT";
	public static final String COMIC_VIEW_RECORD = "DB_COMIC_VIEW_RECORD_OBJECT";
	public static final String DOWNLOAD_COMIC_EPISODE = "DOWNLOAD_COMIC_EPISODE_OBJECT";
	public static final String DOWNLOAD_COMIC_PAGE = "DOWNLOAD_COMIC_PAGE_OBJECT";
	
	public static Connection createConnection(String url) throws SQLException,ClassNotFoundException{
		Class.forName("org.sqlite.JDBC");
		return DriverManager.getConnection(url);
	}

	//public static void executeUpdate(Connection conn,String sql) throws SQLException{
	//	Statement statment = conn.createStatement();
	//}

	public static String createDbUrl(String directory) {
		return "jdbc:sqlite:"+directory;
	}
	
	public static String createDbUrl(String dbPath,String dbName) {
		return "jdbc:sqlite:"+dbPath+"\\"+dbName;
}
	
	public static void executeQuery_all(Connection conn,String sql,String... param)  throws SQLException {
		Statement statment = conn.createStatement();
		ResultSet rs = statment.executeQuery(sql);
		Log.V(JdbcUtil.class, sql+"\n",enableLog);
		StringBuffer stringBuffer;
		while(rs.next()) {
			stringBuffer = new StringBuffer();
			for(String str:param) {
			stringBuffer.append(str+":"+rs.getString(str)+",");
			}
			Log.V(JdbcUtil.class, stringBuffer.toString()+"\n",enableLog);
		}
	}
	
	public static void executeQuery_normal(Connection conn,String sql,String condition,String... param)  throws SQLException {
		Statement statment = conn.createStatement();
		ResultSet rs = statment.executeQuery(sql + " where " + condition + ";");
		Log.V(JdbcUtil.class, sql + " where " + condition +";");
		StringBuffer stringBuffer;
		while(rs.next()) {
			stringBuffer = new StringBuffer();
			for(String str:param) {
			stringBuffer.append(str+":"+rs.getString(str)+",");
			}
			Log.V(JdbcUtil.class, stringBuffer.toString());
		}
	}

	public ArrayList<T> executeQuery(Connection conn,String sql,String condition,String... param) throws SQLException {
		ArrayList<T> objList = new ArrayList();
		ArrayList<String> vals = new ArrayList();
		Statement statment = conn.createStatement();
		ResultSet rs = statment.executeQuery(sql + " where " + condition + ";");
		Log.V(JdbcUtil.class, sql + " where " + condition +";");
		StringBuffer stringBuffer;
		while(rs.next()) {
			stringBuffer = new StringBuffer();
			vals.clear();
			for(String str:param) {
				vals.add(rs.getString(str));
				stringBuffer.append(str+":"+rs.getString(str)+",");
			}
			objList.add(onValueFormatParse.onDataParse(convertLs2As(vals)));
			//Log.V(JdbcUtil.class,"Data:"+stringBuffer);
		}
		return objList;
	}
	
	//��ȡ������Ϣ
	public static String executeQuery_info(Connection conn,String sql,String condition) throws SQLException {
		Log.V(JdbcUtil.class, sql + " where " + condition +";",enableLog);
		return getComicInfo(conn.createStatement().executeQuery(sql + " where " + condition + ";"));
	} 
	
	//TODO Test
	public static String getComicInfo(ResultSet rs) throws SQLException {
		StringBuffer stringBuffer = new StringBuffer();
		for(ComicInfo comicInfo:ComicInfo.values()) {
			stringBuffer.append(comicInfo.translation+" : "+rs.getString(comicInfo.property)+"\n");
		}
		return stringBuffer.toString();
	}

	
	public static String executeQuery_find(Connection conn,String sql,String condition,int len,String fix,String... params) throws SQLException {
		Statement statment = conn.createStatement();
		StringBuffer stringBuffer = new StringBuffer();
		Log.V(JdbcUtil.class, sql + " where " + condition +";");
		ResultSet rs = statment.executeQuery(sql + " where " + condition + ";");
		if(rs.next()) {
			stringBuffer = new StringBuffer();
				for(int i=1;i<=len;i++) {
					for(String str:params) {
						if(rs.getString(str).equals(rs.getString(i))) {
							stringBuffer.append(rs.getString(i));
							if(i!=len) stringBuffer.append(fix);
						}
					}
				}
			Log.V(JdbcUtil.class,"Data:"+stringBuffer,enableLog);
		}
		return stringBuffer.toString();
	}
	
	public static boolean executeQuery_find(Connection conn,String sql,String condition) throws SQLException {
		if(conn.createStatement().executeQuery(sql + " where " + condition + ";").next()) 
			return true;
		else return false;
	}
	
	public boolean executeQuery_find(Connection conn,String sql,String condition,String... param) throws SQLException {
		Statement statment = conn.createStatement();
		Log.V(JdbcUtil.class, sql + " where " + condition +";");
		ResultSet rs = statment.executeQuery(sql + " where " + condition + ";");
		if(rs.next()) {
			StringBuffer stringBuffer = new StringBuffer();
				if(param.length!=1) for(String str:param) stringBuffer.append(str+":"+rs.getString(str)+",");
				else stringBuffer.append(rs.getString(param[0]));	//get single data with single param
			Log.V(JdbcUtil.class,"Data:"+stringBuffer,enableLog);
			onValueFormatParse.onDataCollect(stringBuffer.toString());
			return true;
		}
		return false;
	}
	
	public boolean executeQuery_find(Connection conn,String sql,String condition,int len,String fix) throws SQLException {
		Statement statment = conn.createStatement();
		Log.V(JdbcUtil.class, sql + " where " + condition +";");
		ResultSet rs = statment.executeQuery(sql + " where " + condition + ";");
		if(rs.next()) {
			StringBuffer stringBuffer = new StringBuffer();
				for(int i=1;i<=len;i++) {
					stringBuffer.append(rs.getString(i));
					if(i!=len) stringBuffer.append(fix);
				}
			Log.V(JdbcUtil.class,"Data:"+stringBuffer,enableLog);
			onValueFormatParse.onDataCollect(stringBuffer.toString());
			return true;
		}
		return false;
	}
	
	
	public static String[] convertLs2As(ArrayList<String> stringList){
	    String[] arrayValue = new String[stringList.size()];
	    for(int i=0;i<stringList.size();i++){
	        arrayValue[i] = stringList.get(i);
	    }
	    return arrayValue;
	}
	
	public static void setEnableLog() {
		enableLog = true;
	}
	
}

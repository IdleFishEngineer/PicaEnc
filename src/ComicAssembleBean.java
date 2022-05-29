import java.util.ArrayList;

public class ComicAssembleBean {
	
	private String episodeName;
	private String comicName;
	private String categories;
	private String chineseTeam;
	private String createdAt;
	private int episodeCount;
	private int pages;
	private String creatorName;
	private String creatorGender;
	/**
	 * Create("CREATED_AT","����ʱ��"),
		CreatorGender("CREATOR_GENDER","�������Ա�"),
		DownloadStatus("DOWNLOAD_STATUS","����״̬"),
		DownloadedAt("DOWNLOADED_AT","����ʱ�䣨����"),
		IsFavourite("IS_FAVOURITE","�Ƿ�ϲ��"),
		LastViewTime("LAST_VIEW_TIMESTAMP","�ϴ��Ķ�ʱ��"),
		Views("VIEWS","�ۿ���"),
		LikeCount("LIKES_COUNT","ϲ����"),
		Comments("COMMENTS_COUNT","������"),
		UpdatedAt("UPDATED_AT","����ʱ��");
	 */
	
	private ArrayList<ComicBean> mComicList;
	
	public ComicAssembleBean() {
		
	}

	public ComicAssembleBean(String episodeName, String comicName, ArrayList<ComicBean> mComicList) {
		super();
		this.episodeName = episodeName;
		this.comicName = comicName;
		this.mComicList = mComicList;
	}

	public String getEpisodeName() {
		return episodeName;
	}

	public void setEpisodeName(String episodeName) {
		this.episodeName = episodeName;
	}

	public String getComicName() {
		return comicName;
	}

	public void setComicName(String comicName) {
		this.comicName = comicName;
	}

	public ArrayList<ComicBean> getComicList() {
		return mComicList;
	}

	public void setComicList(ArrayList<ComicBean> comicList) {
		this.mComicList = comicList;
	}	

}

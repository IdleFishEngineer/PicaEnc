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
	 * Create("CREATED_AT","创建时间"),
		CreatorGender("CREATOR_GENDER","创建者性别"),
		DownloadStatus("DOWNLOAD_STATUS","下载状态"),
		DownloadedAt("DOWNLOADED_AT","下载时间（戳）"),
		IsFavourite("IS_FAVOURITE","是否喜欢"),
		LastViewTime("LAST_VIEW_TIMESTAMP","上次阅读时间"),
		Views("VIEWS","观看数"),
		LikeCount("LIKES_COUNT","喜欢数"),
		Comments("COMMENTS_COUNT","讨论数"),
		UpdatedAt("UPDATED_AT","更新时间");
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

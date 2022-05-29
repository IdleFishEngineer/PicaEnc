import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ComicBean {

	private String comic_id;
	private String episode_id;
	private String media_path;
	private String media_original_name;
	private String comic_name;
	private String epiode_name;
	
	public ComicBean() {
		
	}
	
	public ComicBean(String comic_id,String episode_id) {
		super();
		this.comic_id = comic_id;
		this.episode_id = episode_id;
	}
	
	public ComicBean(String comic_id, String media_path, String media_original_name) {
		super();
		this.comic_id = comic_id;
		this.media_path = media_path;
		this.media_original_name = media_original_name;
	}
	
	public ComicBean(String comic_id, String episode_id, String media_path, String media_original_name) {
		super();
		this.comic_id = comic_id;
		this.episode_id = episode_id;
		this.media_path = media_path;
		this.media_original_name = media_original_name;
	}

	public ComicBean(String comic_id, String episode_id, String media_path, String media_original_name,
			String comic_name, String epiode_name) {
		super();
		this.comic_id = comic_id;
		this.episode_id = episode_id;
		this.media_path = media_path;
		this.media_original_name = media_original_name;
		this.comic_name = comic_name;
		this.epiode_name = epiode_name;
	}

	public String getComic_id() {
		return comic_id;
	}
	
	public void setComic_id(String comic_id) {
		this.comic_id = comic_id;
	}
	
	public String getEpisode_id() {
		return episode_id;
	}

	public void setEpisode_id(String episode_id) {
		this.episode_id = episode_id;
	}

	public String getMedia_path() {
		return media_path;
	}

	public void setMedia_path(String media_path) {
		this.media_path = media_path;
	}

	public String getMedia_original_name() {
		return media_original_name;
	}
	
	public void setMedia_original_name(String media_original_name) {
		this.media_original_name = media_original_name;
	}

	
	public String getComic_name() {
		return comic_name;
	}

	public void setComic_name(String comic_name) {
		this.comic_name = comic_name;
	}

	public String getEpiode_name() {
		return epiode_name;
	}

	public void setEpiode_name(String epiode_name) {
		this.epiode_name = epiode_name;
	}

	@Override
	public String toString() {
		return "ComicBean [comic_id=" + comic_id + ", episode_id=" + episode_id + ", media_path=" + media_path
				+ ", media_original_name=" + media_original_name + ", comic_name=" + comic_name + ", epiode_name="
				+ epiode_name + "]";
	}
	


}

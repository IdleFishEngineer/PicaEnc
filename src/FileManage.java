import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;



public class FileManage {

	protected static boolean enableLog = false;
	protected static boolean fastMode = false;
	
	public static void rename(ArrayList<ComicBean> mComicList) {
		File file;
		int success = 0;
		Log.V(FileManage.class, mComicList.size() + " data in table of database. ");
		for(ComicBean comic:mComicList) {
		if(!fastMode) {
				System.out.append("*");
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			file = new File(comic.getMedia_path());
			if(file.exists()) {
				file.renameTo(new File(comic.getMedia_original_name()));
				success ++ ;
			}else {
				Log.E(FileManage.class,comic.getMedia_path()+" not exists.",enableLog);
			}
		}
		System.out.append("\n");
		Log.V(FileManage.class,success+" files have been renamed successfuly.");
	}
	
	public static void rename(ArrayList<ComicBean> mComicList,String folderName) {
		File file;
		int success = 0;
		Log.V(FileManage.class, mComicList.size() + " data in table of database. ");
		for(ComicBean comic:mComicList) {
			if(!fastMode) {
				System.out.append("*");
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			file = new File(folderName+File.separator+comic.getMedia_path());
			if(file.exists()) {
			file.renameTo(new File(folderName+File.separator+comic.getMedia_original_name()));
			success ++ ;
			}else {
				Log.E(FileManage.class,folderName + "\\" +comic.getMedia_path()+" is not exists.",enableLog);
			}
		}
		Log.V(FileManage.class,success+" files have been renamed successfuly.");
	}
	
	public static void generatePdfDocument(String folderName,String savePath) {
		boolean result = true;
		String imagePath = null;
		Document doc = new Document(null, 0, 0, 0, 0);
		BufferedImage img = null;
		Image image = null;
		FileInputStream fis = null;
		
		try {
			FileOutputStream fos = new FileOutputStream(savePath);
			PdfWriter.getInstance(doc, fos);
	        File[] files = new File(folderName).listFiles();
	        
	        for (File file1 : files) {
	            if (file1.getName().endsWith(".png")
	                    || file1.getName().endsWith(".jpg")
	                    || file1.getName().endsWith(".gif")
	                    || file1.getName().endsWith(".jpeg")
	                    || file1.getName().endsWith(".tif")) {
	            	  imagePath = folderName + file1.getName();
	            	  Log.V(FileManage.class,"image name:"+file1.getAbsolutePath(),enableLog );
	            	  fis = new FileInputStream(new File(imagePath));
	            	  img = ImageIO.read(fis);
	            	  image = Image.getInstance(imagePath);
	            	  doc.setPageSize(new Rectangle(img.getWidth(), img
	                          .getHeight()));
	            	  doc.open();
	                  doc.add(image);
	            }
	            }
		}catch(DocumentException|IOException e) {
			result = false;
			e.printStackTrace();
		}finally{
			try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			doc.close();
			if(result) Log.V(FileManage.class,"Create pdf document successed!");
			else Log.V(FileManage.class,"Create pdf document failed!");
		}
	}
	
	
	public static void setEnableLog() {
		enableLog = true;
	}
	
	public static void setFastMode() {
		fastMode = true;
	}
}

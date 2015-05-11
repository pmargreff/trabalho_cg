

public class TImageData {

	public String path;
	public String conf_file;
	public String extension;
	public int imageHeight;
	public int imageWidth;

	public TImageData(String p, String c,String e, int w, int h ) {
		this.path 		 = p;
		this.conf_file 	 = c;
		this.extension 	 = e;
		this.imageWidth  = w;
		this.imageHeight = h;
	}
}
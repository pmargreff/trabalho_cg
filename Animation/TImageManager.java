/*
	@docs: http://www.ocpsoft.org/opensource/guide-to-regular-expressions-in-java-part-1/
*/

import java.io.*;
import java.util.ArrayList;
import java.awt.geom.*;

public class TImageManager {

	private ArrayList<TriangulatedImage> tImages;
	private int size;

	private final short N_ANCHOR_POINTS = 10;

	public TImageManager (String dir) {

		/*
		File d = new File(dir);
		for (File f: d.listFiles()) {
			if (f.isFile() && (f.getName().endsWith(".jpg") || f.getName().endsWith(".png") ) ) {
				System.out.println("file name: " + f.getName());
			}
		} */

		this.tImages = new ArrayList<TriangulatedImage>();

		try {

			BufferedReader pointFile = new BufferedReader(new FileReader(dir));
			String line = "";

			while( (line = pointFile.readLine()) != null) {

				if (line.matches("<anchors>")) {
					while((line = pointFile.readLine()) != null) {
						if (line.matches("</anchors>")) break;
						System.out.println(line);
					}
				} else if (line.matches("<(\\w)+>")) {
					
					String image_name = line.substring(1, line.length() - 1);

					System.out.println(image_name);

					ArrayList<Point2D> points = new ArrayList<Point2D>(); 

					while((line = pointFile.readLine()) != null) {
						if (line.matches("<(/)(\\w)+>")) {

							if (points.size() > 0) {
								System.out.println(points);
							}

							break;
						}
							
						String inPoints[] = line.split(" ");
						points.add( new Point2D.Double(Integer.parseInt(inPoints[0]), Integer.parseInt(inPoints[1])) );			// acho que ta faltando cuidar as dimensoes
					}



					

				} else {
						System.out.println("dorgassss");
				} 
					

			}

		} catch( IOException e) {
			e.printStackTrace();
		}



	}

	/*
	private TriangulatedImage buildTImage() {

	}*/


	public static void main(String argv[]) {

		if (argv.length == 1) new TImageManager(argv[0]);
	}

}
/*
	@docs: http://www.ocpsoft.org/opensource/guide-to-regular-expressions-in-java-part-1/
*/

import java.io.*;
import java.util.ArrayList;
import java.awt.geom.*;
import java.awt.*;
import java.util.*;
import java.awt.image.BufferedImage;


public class TImageManager {

	private ArrayList<TriangulatedImage> tImages;
	private int imageHeight;
	private int imageWidth;
	private String fextension;
	private String dir_path;
	private String data_file;
	private int size;

	private short N_ANCHOR_POINTS = 0;

	public TImageManager (String dp, String df, String ext, int w, int h) {

		this.dir_path    = dp;
		this.data_file   = df;
		this.fextension  = ext;
		this.imageWidth  = w;
		this.imageHeight = h;
		this.tImages = new ArrayList<TriangulatedImage>();

		try {

			BufferedReader pointFile = new BufferedReader(new FileReader(data_file));
			String line = "";

			while( (line = pointFile.readLine()) != null) {

				if (line.matches("<anchors>")) {
					while((line = pointFile.readLine()) != null) {
						if (line.matches("</anchors>")) break;
						System.out.println(line);
					}
				} else if (line.matches("<(\\w)+>")) {
					
					// TODO: daqui ateh a linha 61 deveria virar uma funcao
					String image_name = line.substring(1, line.length() - 1);
					System.out.println(image_name);

					// nao pode passar com "." (ver)
					Image loadedImage = new javax.swing.ImageIcon(image_name + "." + fextension).getImage();

					tImages.add(0, new TriangulatedImage());
					tImages.get(0).bi = new BufferedImage(imageWidth,imageHeight, BufferedImage.TYPE_INT_RGB);

					Graphics2D img_loader = tImages.get(0).bi.createGraphics();
					img_loader.transform(normalizedCoords(imageHeight));  // coloca as imagens em sua coordenada correta;
					img_loader.drawImage(loadedImage, 0,0,null);		  // Desenha na imagem bufferizada a imagem carregada	


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
public AffineTransform normalizedCoords(int height) {

	AffineTransform normalizer = new AffineTransform();
  	normalizer.setToScale(1, -1);
  	AffineTransform translate = new AffineTransform();
  	translate.setToTranslation(0, height);
  	normalizer.preConcatenate(translate);

  	return normalizer;
}


	public static void main(String argv[]) {

		if (argv.length == 5) new TImageManager(argv[0], argv[1], argv[2], 150, 125 );
	}

}
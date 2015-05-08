/*
	TODO: Implementar tratamento de pontos ancora;
	TODO: Criar metodo para melhorar construcao das imagens trianguladas;
	TODO: Testar;

	@docs: http://www.ocpsoft.org/opensource/guide-to-regular-expressions-in-java-part-1/
*/

import java.io.*;
import java.util.ArrayList;
import java.awt.geom.*;
import java.awt.*;
import java.util.*;
import java.awt.image.BufferedImage;


public class TImageManager {

	private int imageHeight;
	private int imageWidth;
	private int size;
	private int N_ANCHOR_POINTS;
	private String fextension;
	private String dir_path;
	private String data_file;
	private ArrayList<Point2D> custom_points;
	private ArrayList<Point2D> anchor_points;
	private ArrayList<TriangulatedImage> tImages;


public TImageManager (String dp, String df, String ext, int w, int h) {

	this.size            = 0;
	this.N_ANCHOR_POINTS = 0;
	this.dir_path    	 = dp;
	this.data_file   	 = df;
	this.fextension  	 = ext;
	this.imageWidth  	 = w;
	this.imageHeight 	 = h;
	this.tImages         = new ArrayList<TriangulatedImage>();

	buildTImageList();
}


public TriangulatedImage getNext(int index) {
	return tImages.get( (index + 1) % this.size );		// walk thru array of timgs circularly
}

private TriangulatedImage createTImage(String imgName) {

	Image loadedImage = new javax.swing.ImageIcon(imgName).getImage();

	TriangulatedImage tmp = new TriangulatedImage();
	tmp.bi = new BufferedImage(imageWidth,imageHeight, BufferedImage.TYPE_INT_RGB);

	Graphics2D img_loader = tmp.bi.createGraphics();
	img_loader.transform(normalizedCoords(imageHeight));  // coloca as imagens em sua coordenada correta;
	img_loader.drawImage(loadedImage, 0,0,null);		  // Desenha na imagem bufferizada a imagem carregada	

	return tmp;
}

private void buildTImageList() {

	try {

		BufferedReader pointFile = new BufferedReader(new FileReader(data_file));
		String line = "";

		while( (line = pointFile.readLine()) != null) {

			if (line.matches("<anchors>")) {

				this.anchor_points = new ArrayList<Point2D>();

				while((line = pointFile.readLine()) != null) {
					if (line.matches("</anchors>")) break;
					System.out.println(line);
					String aPoints[] = line.split(" ");
					this.anchor_points.add( new Point2D.Double( Integer.parseInt(aPoints[0]),
														   Integer.parseInt(aPoints[1]) ));
				}
			} else if (line.matches("<(\\w)+>")) {

				String image_name = line.substring(1, line.length() - 1);
				System.out.println(image_name);

				this.tImages.add(createTImage(image_name + "." + fextension));		// nao testado					

				this.custom_points = new ArrayList<Point2D>(); 

				while((line = pointFile.readLine()) != null) {
					if (line.matches("<(/)(\\w)+>")) {

						int n_points = this.custom_points.size();

						if (n_points > 0) {

							int anch_size  = this.anchor_points.size();
							int total_size = anch_size + n_points;

							//tImages.get(size).tPoints = new Point2D[n_points];	usei a ideia de alias, pode dar pau
							TriangulatedImage current_img = this.tImages.get(size);

							current_img.tPoints = new Point2D[total_size];	// mais os pontos ancora
							
							for (int i = 0; i < anch_size; i ++) {
								current_img.tPoints[i] = anchor_points.get(i);
							}

							for (int i = anch_size; i < total_size; i++) {
								current_img.tPoints[i] = custom_points.get(i);	// pode dar pau por causa da atribs
							}


							// aqui acho que sao os triangulos que ele mapeou;
							current_img.triangles = new int[22][3];		// nao sei se eh n_points : nao entendi isso aqui
							// preenche aqui com os triangulos padroes que seram carregados uma vez


						}

						this.size++;	// Prepara para a proxima imagem/triangulacao

						break;
					}
						
					String inPoints[] = line.split(" ");
					custom_points.add( new Point2D.Double(Integer.parseInt(inPoints[0]), Integer.parseInt(inPoints[1])) );			// acho que ta faltando cuidar as dimensoes
				}

			} else {
					System.out.println("dorgassss");
			}		

		}

	} catch( IOException e) {
		e.printStackTrace();
	}
}

public AffineTransform normalizedCoords(int height) {

	AffineTransform normalizer = new AffineTransform();
  	normalizer.setToScale(1, -1);
  	AffineTransform translate = new AffineTransform();
  	translate.setToTranslation(0, height);
  	normalizer.preConcatenate(translate);

  	return normalizer;
}

public int getSize() {
	return this.size;
}


	public static void main(String argv[]) {

		if (argv.length == 5) new TImageManager(argv[0], argv[1], argv[2], 150, 125 );
	}

}
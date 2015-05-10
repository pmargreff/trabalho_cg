/*
	
	Medir a produtividade olhando horarios de commit em python:
		print datetime.strptime('17:23', '%H:%M') - datetime.strptime('15:29', '%H:%M')

	TODO: Implementar matris de triangulos dinamicos;

	TODO: 1. Testar a construcao das tabelas de pontos e imagens [Importante];

	@docs:
		- http://www.ocpsoft.org/opensource/guide-to-regular-expressions-in-java-part-1/
		- http://stackoverflow.com/questions/12008986/sublime-text-2-how-to-delete-blank-empty-lines

	TODO: Preciso de um vetor de vetores para os triangulos;	
		
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
	private int bg_width;
	private int bg_height;
	private int size;
	private int[][] mapped_triangles;
	private String fextension;
	private String dir_path;
	private String data_file;
	private BufferedImage bg;
	private ArrayList<Point2D> custom_points;
	private ArrayList<Point2D> anchor_points;
	private ArrayList<TriangulatedImage> tImages;


// TODO: Usar TImageData para simplificar o construtor
public TImageManager (String dp, String df, String ext) {

	this.size             = 0;
	this.dir_path    	  = dp;
	this.data_file   	  = df;
	this.fextension  	  = ext;
	this.imageWidth  	  = 0;
	this.imageHeight 	  = 0;
	this.bg_width		  = 0;
	this.bg_height        = 0;	
	this.tImages          = new ArrayList<TriangulatedImage>();
	/*
		Por enquanto mapped_triangles esta estatico, logo, no arquivo de configuracao, tem que ter 22 obrigatoriamento; 
	*/
	this.mapped_triangles = new int[22][3];

	buildTImageList();
}

public BufferedImage getBG() {
	return this.bg;
}


public int getBGHeight() {
	return this.bg_height;
}

public int getBGWidth() {
	return this.bg_width;
}


public TriangulatedImage getNext(int index) {
	return tImages.get( (index + 1) % this.size );		// walk thru array of timgs circularly
}

public TriangulatedImage get(int index) {
		
	if (index >= 0 && index <= this.size) {
		return tImages.get(index);
	}
	return null;
}

private TriangulatedImage createTImage(String imgName) {

	Image loadedImage = new javax.swing.ImageIcon(imgName).getImage();

	TriangulatedImage tmp = new TriangulatedImage();
	tmp.bi = new BufferedImage(imageWidth,imageHeight, BufferedImage.TYPE_INT_RGB);
	tmp.g2dbi = tmp.bi.createGraphics();
	tmp.g2dbi.transform(normalizedCoords(imageHeight));  // coloca as imagens em sua coordenada correta;
	tmp.g2dbi.drawImage(loadedImage, 0,0,null);		  // Desenha na imagem bufferizada a imagem carregada	

	return(tmp);

}

private void buildTImageList() {

	try {

		BufferedReader pointFile = new BufferedReader(new FileReader(data_file));
		String line = "";

		while( (line = pointFile.readLine()) != null) {

			if (line.matches("<anchors>")) {				// Ta funcionando;

				this.anchor_points = new ArrayList<Point2D>();

				while((line = pointFile.readLine()) != null) {
					if (line.matches("</anchors>")) break;
					String aPoints[] = line.split(" ");
					this.anchor_points.add( new Point2D.Double( Integer.parseInt(aPoints[0]),
														   Integer.parseInt(aPoints[1]) ));
					
				}

			} else if (line.matches("<(\\w)+>") && !line.equals("<mapped>") && !line.equals("<anchors>") && !line.equals("<metadata>") ) {

				String image_name = line.substring(1, line.length() - 1);

				/*
					TODO:
					Como nao adicionada o path, dava problema. Aqui deveria ser feito verificacoes
					acerca das validades do path, do nome e caso exista "." ou "/" nos nomes;
				*/

				this.tImages.add( createTImage( dir_path + image_name + "." + fextension) );						

				this.custom_points = new ArrayList<Point2D>(); 

				while((line = pointFile.readLine()) != null) {
					if (line.matches("<(/)(\\w)+>")) {

						int n_points = this.custom_points.size();

						if (n_points > 0) {

							int anch_size  = this.anchor_points.size();
							int total_size = anch_size + n_points;

							TriangulatedImage current_img = this.tImages.get(size);

							current_img.tPoints = new Point2D[total_size];	// mais os pontos ancora
							
							for (int i = 0; i < anch_size; i ++) {
								current_img.tPoints[i] = anchor_points.get(i);
							}

							int continuation = 0;
							for (int i = anch_size; i < total_size; i++) {
								current_img.tPoints[i] = custom_points.get(continuation);	// pode dar pau por causa da atribs
								continuation++;
							}

							current_img.triangles = new int[22][3];		// nao sei se eh n_points : nao entendi isso aqui
							
							for (int j = 0; j < 22; j++) {
								current_img.triangles[j][0] = mapped_triangles[j][0];
								current_img.triangles[j][1] = mapped_triangles[j][1];
								current_img.triangles[j][2] = mapped_triangles[j][2];
							}

						}

						this.size++;	// Prepara para a proxima imagem/triangulacao

						break;
					}
						
					String inPoints[] = line.split(" ");
					custom_points.add( new Point2D.Double(Integer.parseInt(inPoints[0]), Integer.parseInt(inPoints[1])) );			// acho que ta faltando cuidar as dimensoes
				}

			} else if(line.matches("<mapped>")) {							
				int p_line = 0;
				while((line = pointFile.readLine()) != null) {
					if (line.matches("</mapped>")) break;
					
					String mPoints[] = line.split(" ");

					this.mapped_triangles[p_line][0] = Integer.parseInt(mPoints[0]);
					this.mapped_triangles[p_line][1] = Integer.parseInt(mPoints[1]);
					this.mapped_triangles[p_line][2] = Integer.parseInt(mPoints[2]);

					p_line++;
				}

			} else if(line.matches("<metadata>")) {
				while((line = pointFile.readLine()) != null) {
					if (line.matches("</metadata>")) break;
					
					String mdata[] = line.split(":");

					switch(mdata[0]) {
						case "bg":
							// carrega o bd;
							String bg_name = mdata[1];
							bg_width   	   = Integer.parseInt(mdata[2]);
							bg_height      = Integer.parseInt(mdata[3]);

							this.bg = new BufferedImage(bg_width,bg_height,BufferedImage.TYPE_INT_RGB);
							Image theImage = new javax.swing.ImageIcon(dir_path + bg_name + "." + fextension).getImage();
							Graphics2D g2dBackGround = this.bg.createGraphics();
							g2dBackGround.drawImage(theImage, 0, 0, null);

						break;
						
						case "iWidth":
							// Atualiza o width das imagens - deve ocorrer antes de carregar as imagens
							imageWidth = Integer.parseInt(mdata[1]);
						break;

						case "iHeight":
							// Atualiza o width das imagens - deve ocorrer antes de carregar as imagens	
							imageHeight = Integer.parseInt(mdata[1]);						
						break;

					}



				}

			} else {
				System.out.println("Erro de sintaxe no arquivo de mapeamento dos triangulos.");
				break;
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

	if (argv.length == 5) {

		TImageManager ti = new TImageManager(argv[0], argv[1], argv[2]);
		
		BufferedImageDrawer imgd = new BufferedImageDrawer(ti.getBG(), 324,155);	

	}
}

}
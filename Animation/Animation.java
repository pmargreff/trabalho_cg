/*
	TODO: Verificar por que a imagem se desloca/pula quando a animacao inicia;
	(parece que sai de 0,0 e vai para as coordenadas inicias)

	TODO: Implementar uma estrutura otimizada para utilizar as imagens trianguladas e carregar as informacoes dos pontos de
		  triangulacao;

	TODO: Implementar logica de voltar/selecionar as imagens durante a tragetoria para o caso onde ha mais semi-circulos que
		  imagens;

	TODO: Verificar a necessidade de uma logica para parar o metodo run da classe animation (aparentemente necessario);
	
	TODO: Se der tempo, tentar centralizar a imagem do fundo e ver se eh possivel trabalhar com png para tirar o fundo da imagem;		

	Docs:
		Image tutorial: https://docs.oracle.com/javase/tutorial/2d/images/

*/

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.awt.image.BufferedImage;


public class Animation extends TimerTask {
  //TODO list: 
  //corrigir erro do primeiro ponto
  //alterar imagens para passar todas
	//recalcular pontos
    //The window in which everything is shown.
	private BufferedImageDrawer buffid;

  	//The background image
	private BufferedImage bg;

  	//Variáveis para cálculo do bresenham
	private Point pi;
	private Point pf;
	private int jump;

  	//Flag para marcar a rotação, e posteriormente a troca de imagens
	Flag turn = new Flag(false);

  	//parametros que serão recebidos
	private int radius;     
	private int segments;   
	private int repetitions;
	private int x_0;
	private int y_0;

  	//lista que conterá o trajeto  
	private ArrayList<Point> point_list;

  //The triangulated images.
	private TriangulatedImage capitain1;
	private TriangulatedImage capitain2;
	private TriangulatedImage ironMan1;
	private TriangulatedImage ironMan2;
	private TriangulatedImage hulk1;
	private TriangulatedImage hulk2;
	private TriangulatedImage hulk3;

  //Size of images
	private int imageWidth;
	private int imageHeight;

    //This is used for generating/storing the intermediate images.
	private BufferedImage mix;

	private double alpha;
	private double deltaAlpha;
	private int steps;
  // Image loadedImage;

	private int repetition_controller;

  /**
  * Constructor
  * @param bid          The buffered image to be drawn
  * @param backGround   The background (buffered) image
  */
 public Animation(int r, int s, int n, int x, int y , BufferedImageDrawer bid, BufferedImage backGround,int width, int height,int delay) {
  	buffid = bid;

  	this.radius      = r;
  	this.repetitions = n;
  	this.x_0         = x;  
  	this.y_0         = y;
  	this.repetition_controller = 0;



  	buffid.g2dbi.setStroke(new BasicStroke(1.5f));
  	buffid.g2dbi.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON); 

  	//Transforma as coordenadas
  	buffid.g2dbi.transform(normalizedCoords(height));

  	buffid.g2dbi.setPaint(Color.black);

  	bg = backGround;

  	//calcula os pontos
  	pi = new Point();
  	pf = new Point();
  	point_list = calculateBrasenham();
  	
  	this.segments = normalizedSegement(s);

  	pi.set_x(point_list.get(0).get_x());
  	pi.set_y(point_list.get(0).get_y());

  	//calcula os pontos para a triângulação da imagem
  	imageWidth = 150;
  	imageHeight = 125;

  	calculateTriangles(imageWidth, imageHeight);

  	steps = 70;
  	deltaAlpha = 1.0/steps;
  	alpha = 0;
  }
  
private ArrayList<Point> calculateBrasenham() {

  	ArrayList<Point> left_half  = new ArrayList<Point>(); 
  	ArrayList<Point> rigth_half = new ArrayList<Point>();

  	int x = 0;
  	int y = radius;
  	int decision_var = 1 - radius;

  	rigth_half.add( new Point(x , y  ) );

  	while ( y > x ) {

  		x++;

  		if (decision_var < 0) {
  			decision_var += 2 * x + 3;
  		} else {

  			decision_var += 2 * ( x - y ) + 5;
  			y--;
  		}

  		rigth_half.add( new Point(x , y ) );      
  	}

  	for (Point tmp : rigth_half ) {
  		left_half.add( 0,  new Point( tmp.get_y() , tmp.get_x() ));
  	}

  	ArrayList<Point> final_list = new ArrayList<Point>();

  	for (Point tmp : left_half) {
  		final_list.add(0, new Point( -tmp.get_x(), tmp.get_y()) );
  	}

  	int size = rigth_half.size() - 1;

  	for (int i = size; i > 0; i-- ) {
  		final_list.add( new Point( -rigth_half.get(i).get_x() , rigth_half.get(i).get_y() ) );
  	}

  	rigth_half.addAll(left_half);

  	final_list.addAll(rigth_half);

  	for (int i = 0; i < final_list.size(); i++ ) {

  		Point tmp = final_list.get(i);
  		final_list.get(i).set_x(tmp.get_x() + radius + x_0);
  		final_list.get(i).set_y(tmp.get_y() + y_0);
  	}
  	return final_list;
}

private void calculateInterpolationPoints(Point pf, int i ,int r ) {
  	int size = point_list.size() - 1;

  	if ( (i * this.jump  + this.jump) > size ) {
  		pf.set_x(point_list.get(size).get_x() + r);
  		pf.set_y(point_list.get(size).get_y());

  	} else {
  		pf.set_x(point_list.get((i * this.jump ) + this.jump ).get_x() + r);
  		pf.set_y(point_list.get((i * this.jump ) + this.jump ).get_y()); 
  	}
}

/*
	Calcula uma aproximacao para o segmento baseado no 
	numero de pontos no brasenham e ja calcula o jump (eh ruim fazer isso mas ..) ;
*/
public int normalizedSegement(int s) {

	int newSegment = 0;

	double size = (double)point_list.size();

  	double jump_delta = size / s;

  	double remainder = size % s;
  	double jump_alfa = (int)(remainder/jump_delta);

  	this.jump = (int) jump_delta;

  	newSegment = s + (int)jump_alfa;

  	return newSegment;

}

public AffineTransform normalizedCoords(int height) {

	AffineTransform normalizer = new AffineTransform();
  	normalizer.setToScale(1, -1);
  	AffineTransform translate = new AffineTransform();
  	translate.setToTranslation(0, height);
  	normalizer.preConcatenate(translate);

  	return normalizer;

}

public void run() {

	/*
		Acho que o problema da interpolacao pode ser corrigido assim:
		devo calcular o ponto inicial antes de começar a interpolaçao, usando o pi;
	*/

	
  	if (repetitions > 0) {
  		repetitions--;

  		int shift = repetition_controller * radius * 2;

		Point initialPoint = new Point();
		initialPoint.set_x(pi.get_x() + shift);
		initialPoint.set_y(pi.get_y());

  		for (int j = 0; j < segments; j++) {

  			calculateInterpolationPoints(pf, j, shift);

  			Point interpolatedPoint = new Point();
  			
  			for (double step = 0; step < 70; step++ ) {
  				if (alpha >= 0 && alpha <= 1) {
  					int castAux = (int) ((1 - alpha) * initialPoint.get_x() + alpha * pf.get_x());
  					interpolatedPoint.set_x(castAux);
  					
  					castAux = (int) ((1 - alpha) * initialPoint.get_y() + alpha * pf.get_y());
  					interpolatedPoint.set_y(castAux);
  				    
  				    /* Calcula a interpolacao entre a imagem atual e a proxima (fechando, assim, um segmento), 
  				    respeitando o numero de imagens que existem para interpolar por meio do 
  				    */ 

  					mix = capitain1.mixWith(capitain2,alpha);	

  				    //Draw the interpolated image on the BufferedImage.
  					//buffid.g2dbi.drawImage(bg,0,0,null);
  					buffid.g2dbi.drawImage(mix,interpolatedPoint.get_x(),interpolatedPoint.get_y(),null);
  				}
  				
  				buffid.repaint();
  				alpha = alpha+deltaAlpha;
  			}

  			initialPoint.set_x(pf.get_x());
  			initialPoint.set_y(pf.get_y());

  			alpha = 0;
  		}

  		repetition_controller++;


  	} else {
  		// Termina a excecucao quando chega no numero maximo de repeticoes;
  		this.cancel();
  	}

}

private void calculateTriangles(int imageWidth, int imageHeight){
	Image loadedImage;

	  //Define primeiro pontos ancoras das bordas
	  //depois os pontos do rosto

		//Carrega Steve Rogers
		//-----------------------------------------------------------------------------------------
	capitain1 = new TriangulatedImage();
	capitain1.bi = new BufferedImage(imageWidth,imageHeight,BufferedImage.TYPE_INT_RGB);

	Graphics2D g2dcapitain1 = capitain1.bi.createGraphics();

	  //Load the image and draw it on the corresponding BufferedImage.
	loadedImage = new javax.swing.ImageIcon("capitain1.jpg").getImage();

	//g2dcapitain1.transform(normalizedCoords(imageHeight));	// para corrigir as imagens que ficaram de cabeca para baixo;

	AffineTransform tr = new AffineTransform();
	tr.setToTranslation(pi.get_x(), pi.get_y());

	g2dcapitain1.transform(tr);

	g2dcapitain1.drawImage(loadedImage, 0,0,null);

	capitain1.tPoints = new Point2D[17];

	//ancoras
	capitain1.tPoints[0] = new Point2D.Double(150 - 0,125 -0);
	capitain1.tPoints[1] = new Point2D.Double(150 - 150,125 -0);
	capitain1.tPoints[2] = new Point2D.Double(150 - 0,125 -40);
	capitain1.tPoints[3] = new Point2D.Double(150 - 150,125 -40);
	capitain1.tPoints[4] = new Point2D.Double(150 - 0,125 -70);
	capitain1.tPoints[5] = new Point2D.Double(150 - 150,125 -70);
	capitain1.tPoints[6] = new Point2D.Double(150 - 0,125 -125);
	capitain1.tPoints[7] = new Point2D.Double(150 - 150,125 -125);
	capitain1.tPoints[8] = new Point2D.Double(150 - 45,125 -125);
	capitain1.tPoints[9] = new Point2D.Double(150 - 110,125 -125);

	  //pontos do rosto
	capitain1.tPoints[10] = new Point2D.Double(150 - 75,125 -2);
	capitain1.tPoints[11] = new Point2D.Double(150 - 65,125 -50);
	capitain1.tPoints[12] = new Point2D.Double(150 - 80,125 -50);
	capitain1.tPoints[13] = new Point2D.Double(150 - 75,125 -70);
	capitain1.tPoints[14] = new Point2D.Double(150 - 75,125 -85);
	capitain1.tPoints[15] = new Point2D.Double(150 - 20,125 -95);
	capitain1.tPoints[16] = new Point2D.Double(150 - 125,125 -95);

	//Definition of the triangles.
	capitain1.triangles = new int[22][3];

	capitain1.triangles[0][0] = 0;
	capitain1.triangles[0][1] = 10;
	capitain1.triangles[0][2] = 1;

	capitain1.triangles[1][0] = 0;
	capitain1.triangles[1][1] = 2;
	capitain1.triangles[1][2] = 10;

	capitain1.triangles[2][0] = 10;
	capitain1.triangles[2][1] = 1;
	capitain1.triangles[2][2] = 3;

	capitain1.triangles[3][0] = 2;
	capitain1.triangles[3][1] = 10;
	capitain1.triangles[3][2] = 11;

	capitain1.triangles[4][0] = 10;
	capitain1.triangles[4][1] = 11;
	capitain1.triangles[4][2] = 12;

	capitain1.triangles[5][0] = 10;
	capitain1.triangles[5][1] = 12;
	capitain1.triangles[5][2] = 3;

	capitain1.triangles[6][0] = 2;
	capitain1.triangles[6][1] = 11;
	capitain1.triangles[6][2] = 4;

	capitain1.triangles[7][0] = 4;
	capitain1.triangles[7][1] = 11;
	capitain1.triangles[7][2] = 13;

	capitain1.triangles[8][0] = 11;
	capitain1.triangles[8][1] = 12;
	capitain1.triangles[8][2] = 13;

	capitain1.triangles[9][0] = 12;
	capitain1.triangles[9][1] = 13;
	capitain1.triangles[9][2] = 5;

	capitain1.triangles[10][0] = 12;
	capitain1.triangles[10][1] = 3;
	capitain1.triangles[10][2] = 5;

	capitain1.triangles[11][0] = 4;
	capitain1.triangles[11][1] = 6;
	capitain1.triangles[11][2] = 15;

	capitain1.triangles[12][0] = 15;
	capitain1.triangles[12][1] = 4;
	capitain1.triangles[12][2] = 13;

	capitain1.triangles[13][0] = 13;
	capitain1.triangles[13][1] = 14;
	capitain1.triangles[13][2] = 15;

	capitain1.triangles[14][0] = 13;
	capitain1.triangles[14][1] = 14;
	capitain1.triangles[14][2] = 16;

	capitain1.triangles[15][0] = 13;
	capitain1.triangles[15][1] = 5;
	capitain1.triangles[15][2] = 16;

	capitain1.triangles[16][0] = 16;
	capitain1.triangles[16][1] = 5;
	capitain1.triangles[16][2] = 7;

	capitain1.triangles[17][0] = 6;
	capitain1.triangles[17][1] = 15;
	capitain1.triangles[17][2] = 8;

	capitain1.triangles[18][0] = 15;
	capitain1.triangles[18][1] = 14;
	capitain1.triangles[18][2] = 8;

	capitain1.triangles[19][0] = 8;
	capitain1.triangles[19][1] = 9;
	capitain1.triangles[19][2] = 14;

	capitain1.triangles[20][0] = 14;
	capitain1.triangles[20][1] = 16;
	capitain1.triangles[20][2] = 9;

	capitain1.triangles[21][0] = 16;
	capitain1.triangles[21][1] = 7;
	capitain1.triangles[21][2] = 9;

			//Carrega Capitão América
			//-----------------------------------------------------------------------------------------
	capitain2 = new TriangulatedImage();

	capitain2.bi = new BufferedImage(imageWidth,imageHeight,BufferedImage.TYPE_INT_RGB);
	Graphics2D g2dcapitain2 = capitain2.bi.createGraphics();

	loadedImage = new javax.swing.ImageIcon("capitain2.jpg").getImage();

	g2dcapitain2.transform(tr);

	g2dcapitain2.drawImage(loadedImage, 0,0,null);

	capitain2.tPoints = new Point2D[17];

		  //ancoras
	capitain2.tPoints[0] = new Point2D.Double(150 - 0,125 -0);
	capitain2.tPoints[1] = new Point2D.Double(150 - 150,125 -0);
	capitain2.tPoints[2] = new Point2D.Double(150 - 0,125 -40);
	capitain2.tPoints[3] = new Point2D.Double(150 - 150,125 -40);
	capitain2.tPoints[4] = new Point2D.Double(150 - 0,125 -70);
	capitain2.tPoints[5] = new Point2D.Double(150 - 150,125 -70);
	capitain2.tPoints[6] = new Point2D.Double(150 - 0,125 -125);
	capitain2.tPoints[7] = new Point2D.Double(150 - 150,125 -125);
	capitain2.tPoints[8] = new Point2D.Double(150 - 45,125 -125);
	capitain2.tPoints[9] = new Point2D.Double(150 - 110,125 -125);

		  //pontos do rosto
	capitain2.tPoints[10] = new Point2D.Double(150 - 65,125 -10);
	capitain2.tPoints[11] = new Point2D.Double(150 - 55,125 -45);
	capitain2.tPoints[12] = new Point2D.Double(150 - 72,125 -45);
	capitain2.tPoints[13] = new Point2D.Double(150 - 62,125 -62);
	capitain2.tPoints[14] = new Point2D.Double(150 - 75,125 -85);
	capitain2.tPoints[15] = new Point2D.Double(150 - 20,125 -96);
	capitain2.tPoints[16] = new Point2D.Double(150 - 115,125 -90);

		  //Definition of the triangles.
	capitain2.triangles = new int[22][3];

	for (int i = 0 ;i < 22 ; i++ ) {

		capitain2.triangles[i][0] = capitain1.triangles[i][0];
		capitain2.triangles[i][1] = capitain1.triangles[i][1];
		capitain2.triangles[i][2] = capitain1.triangles[i][2];

	}

			  	//Carrega Tony Stark
			  	//-----------------------------------------------------------------------------------------
	ironMan1 = new TriangulatedImage();
	ironMan1.bi = new BufferedImage(imageWidth,imageHeight,BufferedImage.TYPE_INT_RGB);

	Graphics2D g2dironMan1 = ironMan1.bi.createGraphics();

			    //Load the image and draw it on the corresponding BufferedImage.
	loadedImage = new javax.swing.ImageIcon("ironMan1.jpg").getImage();
	g2dironMan1.drawImage(loadedImage, 0,0,null);

	ironMan1.tPoints = new Point2D[17];

			    //ancoras
	ironMan1.tPoints[0] = new Point2D.Double(150 - 0,0);
	ironMan1.tPoints[1] = new Point2D.Double(150 - 150,0);
	ironMan1.tPoints[2] = new Point2D.Double(150 - 0,40);
	ironMan1.tPoints[3] = new Point2D.Double(150 - 150,40);
	ironMan1.tPoints[4] = new Point2D.Double(150 - 0,70);
	ironMan1.tPoints[5] = new Point2D.Double(150 - 150,70);
	ironMan1.tPoints[6] = new Point2D.Double(150 - 0,125);
	ironMan1.tPoints[7] = new Point2D.Double(150 - 150,125);
	ironMan1.tPoints[8] = new Point2D.Double(150 - 45,125);
	ironMan1.tPoints[9] = new Point2D.Double(150 - 110,125);

			    //pontos do rosto
	ironMan1.tPoints[10] = new Point2D.Double(150 - 75,15);
	ironMan1.tPoints[11] = new Point2D.Double(150 - 65,35);
	ironMan1.tPoints[12] = new Point2D.Double(150 - 85,35);
	ironMan1.tPoints[13] = new Point2D.Double(150 - 75,50);
	ironMan1.tPoints[14] = new Point2D.Double(150 - 72,65);
	ironMan1.tPoints[15] = new Point2D.Double(150 - 40,80);
	ironMan1.tPoints[16] = new Point2D.Double(150 - 110,85);

			    //Definition of the triangles.
	ironMan1.triangles = new int[22][3];

	for (int i = 0 ;i < 22 ; i++ ) {

		ironMan1.triangles[i][0] = capitain1.triangles[i][0];
		ironMan1.triangles[i][1] = capitain1.triangles[i][1];
		ironMan1.triangles[i][2] = capitain1.triangles[i][2];

	}


			  	//Carrega Homem de ferro
			  	//-----------------------------------------------------------------------------------------
	ironMan2 = new TriangulatedImage();
	ironMan2.bi = new BufferedImage(imageWidth,imageHeight,BufferedImage.TYPE_INT_RGB);

	Graphics2D g2dironMan2 = ironMan2.bi.createGraphics();

			    //Load the image and draw it on the corresponding BufferedImage.
	loadedImage = new javax.swing.ImageIcon("ironMan2.jpg").getImage();
	g2dironMan2.drawImage(loadedImage, 0,0,null);

	ironMan2.tPoints = new Point2D[17];

			//ancoras
	ironMan2.tPoints[0] = new Point2D.Double(150 - 0,0);
	ironMan2.tPoints[1] = new Point2D.Double(150 - 150,0);
	ironMan2.tPoints[2] = new Point2D.Double(150 - 0,40);
	ironMan2.tPoints[3] = new Point2D.Double(150 - 150,40);
	ironMan2.tPoints[4] = new Point2D.Double(150 - 0,70);
	ironMan2.tPoints[5] = new Point2D.Double(150 - 150,70);
	ironMan2.tPoints[6] = new Point2D.Double(150 - 0,125);
	ironMan2.tPoints[7] = new Point2D.Double(150 - 150,125);
	ironMan2.tPoints[8] = new Point2D.Double(150 - 45,125);
	ironMan2.tPoints[9] = new Point2D.Double(150 - 110,125);

			    //pontos do rosto
	ironMan2.tPoints[10] = new Point2D.Double(150 - 95,20);
	ironMan2.tPoints[11] = new Point2D.Double(150 - 85,40);
	ironMan2.tPoints[12] = new Point2D.Double(150 - 100,40);
	ironMan2.tPoints[13] = new Point2D.Double(150 - 90,60);
	ironMan2.tPoints[14] = new Point2D.Double(150 - 90,75);
	ironMan2.tPoints[15] = new Point2D.Double(150 - 25,70);
	ironMan2.tPoints[16] = new Point2D.Double(150 - 125,90);

			    //Definition of the triangles.
	ironMan2.triangles = new int[22][3];

	for (int i = 0 ;i < 22 ; i++ ) {

		ironMan2.triangles[i][0] = capitain1.triangles[i][0];
		ironMan2.triangles[i][1] = capitain1.triangles[i][1];
		ironMan2.triangles[i][2] = capitain1.triangles[i][2];

	}

			  	//Carrega Bruce Banne
			  	//-----------------------------------------------------------------------------------------
	hulk1 = new TriangulatedImage();
	hulk1.bi = new BufferedImage(imageWidth,imageHeight,BufferedImage.TYPE_INT_RGB);

	Graphics2D g2dhulk1 = hulk1.bi.createGraphics();

			    //Load the image and draw it on the corresponding BufferedImage.
	loadedImage = new javax.swing.ImageIcon("hulk1.jpg").getImage();
	g2dhulk1.drawImage(loadedImage, 0,0,null);

	hulk1.tPoints = new Point2D[17];

			    //ancoras
	hulk1.tPoints[0] = new Point2D.Double(150 - 0,0);
	hulk1.tPoints[1] = new Point2D.Double(150 - 150,0);
	hulk1.tPoints[2] = new Point2D.Double(150 - 0,40);
	hulk1.tPoints[3] = new Point2D.Double(150 - 150,40);
	hulk1.tPoints[4] = new Point2D.Double(150 - 0,70);
	hulk1.tPoints[5] = new Point2D.Double(150 - 150,70);
	hulk1.tPoints[6] = new Point2D.Double(150 - 0,125);
	hulk1.tPoints[7] = new Point2D.Double(150 - 150,125);
	hulk1.tPoints[8] = new Point2D.Double(150 - 45,125);
	hulk1.tPoints[9] = new Point2D.Double(150 - 110,125);

			    //pontos do rosto
	hulk1.tPoints[10] = new Point2D.Double(150 - 70,10);
	hulk1.tPoints[11] = new Point2D.Double(150 - 60,45);
	hulk1.tPoints[12] = new Point2D.Double(150 - 80,50);
	hulk1.tPoints[13] = new Point2D.Double(150 - 75,60);
	hulk1.tPoints[14] = new Point2D.Double(150 - 70,80);
	hulk1.tPoints[15] = new Point2D.Double(150 - 25,100);
	hulk1.tPoints[16] = new Point2D.Double(150 - 125,95);

			    //Definition of the triangles.
	hulk1.triangles = new int[22][3];

	for (int i = 0 ;i < 22 ; i++ ) {

		hulk1.triangles[i][0] = capitain1.triangles[i][0];
		hulk1.triangles[i][1] = capitain1.triangles[i][1];
		hulk1.triangles[i][2] = capitain1.triangles[i][2];

	}

			  	//Carrega Hulk - Monstro
			  	//-----------------------------------------------------------------------------------------
	hulk2 = new TriangulatedImage();
	hulk2.bi = new BufferedImage(imageWidth,imageHeight,BufferedImage.TYPE_INT_RGB);

	Graphics2D g2dhulk2 = hulk2.bi.createGraphics();

			    //Load the image and draw it on the corresponding BufferedImage.
	loadedImage = new javax.swing.ImageIcon("hulk2.jpg").getImage();
	g2dhulk2.drawImage(loadedImage, 0,0,null);

	hulk2.tPoints = new Point2D[17];

			    //ancoras
	hulk2.tPoints[0] = new Point2D.Double(150 - 0,0);
	hulk2.tPoints[1] = new Point2D.Double(150 - 150,0);
	hulk2.tPoints[2] = new Point2D.Double(150 - 0,40);
	hulk2.tPoints[3] = new Point2D.Double(150 - 150,40);
	hulk2.tPoints[4] = new Point2D.Double(150 - 0,70);
	hulk2.tPoints[5] = new Point2D.Double(150 - 150,70);
	hulk2.tPoints[6] = new Point2D.Double(150 - 0,125);
	hulk2.tPoints[7] = new Point2D.Double(150 - 150,125);
	hulk2.tPoints[8] = new Point2D.Double(150 - 45,125);
	hulk2.tPoints[9] = new Point2D.Double(150 - 110,125);

			    //pontos do rosto
	hulk2.tPoints[10] = new Point2D.Double(150 - 75,10);
	hulk2.tPoints[11] = new Point2D.Double(150 - 65,80);
	hulk2.tPoints[12] = new Point2D.Double(150 - 90, 80);
	hulk2.tPoints[13] = new Point2D.Double(150 - 80,95);
	hulk2.tPoints[14] = new Point2D.Double(150 - 80,120);
	hulk2.tPoints[15] = new Point2D.Double(150 - 25,85);
	hulk2.tPoints[16] = new Point2D.Double(150 - 120,90);

			    //Definition of the triangles.
	hulk2.triangles = new int[22][3];

	for (int i = 0 ;i < 22 ; i++ ) {

		hulk2.triangles[i][0] = capitain1.triangles[i][0];
		hulk2.triangles[i][1] = capitain1.triangles[i][1];
		hulk2.triangles[i][2] = capitain1.triangles[i][2];

	}


			  	//Carrega Hulk - Monstro
			  	//-----------------------------------------------------------------------------------------
	hulk3 = new TriangulatedImage();
	hulk3.bi = new BufferedImage(imageWidth,imageHeight,BufferedImage.TYPE_INT_RGB);

	Graphics2D g2dhulk3 = hulk3.bi.createGraphics();

			    //Load the image and draw it on the corresponding BufferedImage.
	loadedImage = new javax.swing.ImageIcon("hulk3.jpg").getImage();
	g2dhulk3.drawImage(loadedImage, 0,0,null);

	hulk3.tPoints = new Point2D[17];

			    //ancoras
	hulk3.tPoints[0] = new Point2D.Double(150 - 0,0);
	hulk3.tPoints[1] = new Point2D.Double(150 - 150,0);
	hulk3.tPoints[2] = new Point2D.Double(150 - 0,40);
	hulk3.tPoints[3] = new Point2D.Double(150 - 150,40);
	hulk3.tPoints[4] = new Point2D.Double(150 - 0,70);
	hulk3.tPoints[5] = new Point2D.Double(150 - 150,70);
	hulk3.tPoints[6] = new Point2D.Double(150 - 0,125);
	hulk3.tPoints[7] = new Point2D.Double(150 - 150,125);
	hulk3.tPoints[8] = new Point2D.Double(150 - 45,125);
	hulk3.tPoints[9] = new Point2D.Double(150 - 110,125);

			    //pontos do rosto
	hulk3.tPoints[10] = new Point2D.Double(150 - 70,15);
	hulk3.tPoints[11] = new Point2D.Double(150 - 60,50);
	hulk3.tPoints[12] = new Point2D.Double(150 - 80, 50);
	hulk3.tPoints[13] = new Point2D.Double(150 - 70,60);
	hulk3.tPoints[14] = new Point2D.Double(150 - 70,80);
	hulk3.tPoints[15] = new Point2D.Double(150 - 30,95);
	hulk3.tPoints[16] = new Point2D.Double(150 - 120,95);

			    //Definition of the triangles.
	hulk3.triangles = new int[22][3];

	for (int i = 0 ;i < 22 ; i++ ) {

		hulk3.triangles[i][0] = capitain1.triangles[i][0];
		hulk3.triangles[i][1] = capitain1.triangles[i][1];
		hulk3.triangles[i][2] = capitain1.triangles[i][2];

	}

}
  //o main tem que ficar aqui, 
  //não vai rodar em arquivo próprio por conflito na classe TaskTimer
  //contador_de_horas_tentando_deixar_o_main_em_um_arquivo_separado: +-8 horas
public static void main(String[] argv) {
	if ( argv.length == 5 ) {

		int radius    	= Integer.parseInt(argv[0]);
		int segments  	= Integer.parseInt(argv[1]);
		int repetitions = Integer.parseInt(argv[2]);
		int x_center  	= Integer.parseInt(argv[3]);
		int y_center  	= Integer.parseInt(argv[4]);

		int width  = radius * 2 * repetitions + 150 + x_center;
		int height = radius + y_center + 150;

		x_center = x_center - radius;

		if ( segments < 2 ) segments = 2;
		if ( repetitions < 1 ) repetitions = 1;
		if ( radius < 2 ) radius = 2;


    //Specifies (in milliseconds) when the quad should be updated.
		int delay = 10;

    //The BufferedImage to be drawn in the window.

    //The background.
		BufferedImage backGround = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		Image theImage = new javax.swing.ImageIcon("fundo.jpg").getImage();

		Graphics2D g2dBackGround = backGround.createGraphics();

    //The lines should have a thickness of 3.0 instead of 1.0.
		g2dBackGround.setStroke(new BasicStroke(2.0f));

    //The background is painted white first.
		g2dBackGround.setPaint(Color.white);
		g2dBackGround.fill(new Rectangle(0,0,width,height));
		g2dBackGround.drawImage(theImage,-10,height - 155,null);	// - 155 ?
    
    //The window in which everything is drawn.
		BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		BufferedImageDrawer bid = new BufferedImageDrawer(bi,width,height);



    //The TimerTask in which the repeated computations drawing take place.
		Animation scene = new Animation(radius, segments, repetitions, x_center, y_center, bid,backGround,width, height, delay);

		Timer t = new Timer();
		t.scheduleAtFixedRate(scene,0,delay);  
	}
	else {

		System.out.println("Parametros invalidos.\nDigite: java Main < raio > < segmentos > < repeticoes > < x_0 >  < y_0 >\n");
	}

}

}



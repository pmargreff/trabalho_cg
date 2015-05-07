import java.awt.*;
import java.awt.geom.*;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.image.BufferedImage;

//TODO: jogar isso no Animaiton
//TODO: diminuir em loops para os pontos em comum entre ambas imagens
//TODO: Corrigir as animações que não estão muito exatas 
public class Morphing extends TimerTask
{

	private BufferedImageDrawer buffid;
	private int width;
	private int height;
	private int steps;

  //The triangulated images.
	private TriangulatedImage capitain1;
	private TriangulatedImage capitain2;
	private TriangulatedImage ironMan1;
	private TriangulatedImage ironMan2;
	private TriangulatedImage hulk1;
	private TriangulatedImage hulk2;
	private TriangulatedImage hulk3;

  //This is used for generating/storing the intermediate images.
	private BufferedImage mix;

	private double alpha;
	private double deltaAlpha;
  /**
   * Constructor
   *
   * @param bid    The window in which the transformation is shown.
   */
  Morphing(BufferedImageDrawer bid)
  {
  	buffid = bid;

  	width = 150;
  	height = 130;

  	steps = 100;
  	deltaAlpha = 1.0/steps;
  	alpha = 0;

  	Image loadedImage;

    //Define primeiro pontos ancoras das bordas
    //depois os pontos do rosto

  	//Carrega Steve Rogers
  	//-----------------------------------------------------------------------------------------
  	capitain1 = new TriangulatedImage();
  	capitain1.bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

  	Graphics2D g2dcapitain1 = capitain1.bi.createGraphics();

    //Load the image and draw it on the corresponding BufferedImage.
  	loadedImage = new javax.swing.ImageIcon("capitain1.jpg").getImage();
  	g2dcapitain1.drawImage(loadedImage,0,10,null);

  	capitain1.tPoints = new Point2D[17];

    //ancoras
  	capitain1.tPoints[0] = new Point2D.Double(0,0);
  	capitain1.tPoints[1] = new Point2D.Double(150,0);
  	capitain1.tPoints[2] = new Point2D.Double(0,40);
  	capitain1.tPoints[3] = new Point2D.Double(150,40);
  	capitain1.tPoints[4] = new Point2D.Double(0,70);
  	capitain1.tPoints[5] = new Point2D.Double(150,70);
  	capitain1.tPoints[6] = new Point2D.Double(0,125);
  	capitain1.tPoints[7] = new Point2D.Double(150,125);
  	capitain1.tPoints[8] = new Point2D.Double(45,125);
  	capitain1.tPoints[9] = new Point2D.Double(110,125);

    //pontos do rosto
  	capitain1.tPoints[10] = new Point2D.Double(75,2);
  	capitain1.tPoints[11] = new Point2D.Double(65,50);
  	capitain1.tPoints[12] = new Point2D.Double(80,50);
  	capitain1.tPoints[13] = new Point2D.Double(75,70);
  	capitain1.tPoints[14] = new Point2D.Double(75,85);
  	capitain1.tPoints[15] = new Point2D.Double(20,95);
  	capitain1.tPoints[16] = new Point2D.Double(125,95);

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

  	capitain2.bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
  	Graphics2D g2dcapitain2 = capitain2.bi.createGraphics();

  	loadedImage = new javax.swing.ImageIcon("capitain2.jpg").getImage();

  	g2dcapitain2.drawImage(loadedImage,0,10,null);

  	capitain2.tPoints = new Point2D[17];

    //ancoras
  	capitain2.tPoints[0] = new Point2D.Double(0,0);
  	capitain2.tPoints[1] = new Point2D.Double(150,0);
  	capitain2.tPoints[2] = new Point2D.Double(0,40);
  	capitain2.tPoints[3] = new Point2D.Double(150,40);
  	capitain2.tPoints[4] = new Point2D.Double(0,70);
  	capitain2.tPoints[5] = new Point2D.Double(150,70);
  	capitain2.tPoints[6] = new Point2D.Double(0,125);
  	capitain2.tPoints[7] = new Point2D.Double(150,125);
  	capitain2.tPoints[8] = new Point2D.Double(45,125);
  	capitain2.tPoints[9] = new Point2D.Double(110,125);

    //pontos do rosto
  	capitain2.tPoints[10] = new Point2D.Double(65,10);
  	capitain2.tPoints[11] = new Point2D.Double(55,45);
  	capitain2.tPoints[12] = new Point2D.Double(72,45);
  	capitain2.tPoints[13] = new Point2D.Double(62,62);
  	capitain2.tPoints[14] = new Point2D.Double(75,85);
  	capitain2.tPoints[15] = new Point2D.Double(20,96);
  	capitain2.tPoints[16] = new Point2D.Double(115,90);

    //Definition of the triangles.
  	capitain2.triangles = new int[22][3];

  	capitain2.triangles[0][0] = 0;
  	capitain2.triangles[0][1] = 10;
  	capitain2.triangles[0][2] = 1;

  	capitain2.triangles[1][0] = 0;
  	capitain2.triangles[1][1] = 2;
  	capitain2.triangles[1][2] = 10;

  	capitain2.triangles[2][0] = 10;
  	capitain2.triangles[2][1] = 1;
  	capitain2.triangles[2][2] = 3;

  	capitain2.triangles[3][0] = 2;
  	capitain2.triangles[3][1] = 10;
  	capitain2.triangles[3][2] = 11;

  	capitain2.triangles[4][0] = 10;
  	capitain2.triangles[4][1] = 11;
  	capitain2.triangles[4][2] = 12;

  	capitain2.triangles[5][0] = 10;
  	capitain2.triangles[5][1] = 12;
  	capitain2.triangles[5][2] = 3;

  	capitain2.triangles[6][0] = 2;
  	capitain2.triangles[6][1] = 11;
  	capitain2.triangles[6][2] = 4;

  	capitain2.triangles[7][0] = 4;
  	capitain2.triangles[7][1] = 11;
  	capitain2.triangles[7][2] = 13;

  	capitain2.triangles[8][0] = 11;
  	capitain2.triangles[8][1] = 12;
  	capitain2.triangles[8][2] = 13;

  	capitain2.triangles[9][0] = 12;
  	capitain2.triangles[9][1] = 13;
  	capitain2.triangles[9][2] = 5;

  	capitain2.triangles[10][0] = 12;
  	capitain2.triangles[10][1] = 3;
  	capitain2.triangles[10][2] = 5;

  	capitain2.triangles[11][0] = 4;
  	capitain2.triangles[11][1] = 6;
  	capitain2.triangles[11][2] = 15;

  	capitain2.triangles[12][0] = 15;
  	capitain2.triangles[12][1] = 4;
  	capitain2.triangles[12][2] = 13;

  	capitain2.triangles[13][0] = 13;
  	capitain2.triangles[13][1] = 14;
  	capitain2.triangles[13][2] = 15;

  	capitain2.triangles[14][0] = 13;
  	capitain2.triangles[14][1] = 14;
  	capitain2.triangles[14][2] = 16;

  	capitain2.triangles[15][0] = 13;
  	capitain2.triangles[15][1] = 5;
  	capitain2.triangles[15][2] = 16;

  	capitain2.triangles[16][0] = 16;
  	capitain2.triangles[16][1] = 5;
  	capitain2.triangles[16][2] = 7;

  	capitain2.triangles[17][0] = 6;
  	capitain2.triangles[17][1] = 15;
  	capitain2.triangles[17][2] = 8;

  	capitain2.triangles[18][0] = 15;
  	capitain2.triangles[18][1] = 14;
  	capitain2.triangles[18][2] = 8;

  	capitain2.triangles[19][0] = 8;
  	capitain2.triangles[19][1] = 9;
  	capitain2.triangles[19][2] = 14;

  	capitain2.triangles[20][0] = 14;
  	capitain2.triangles[20][1] = 16;
  	capitain2.triangles[20][2] = 9;

  	capitain2.triangles[21][0] = 16;
  	capitain2.triangles[21][1] = 7;
  	capitain2.triangles[21][2] = 9;


  	  	//Carrega Tony Stark
  	  	//-----------------------------------------------------------------------------------------
  	ironMan1 = new TriangulatedImage();
  	ironMan1.bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

  	Graphics2D g2dironMan1 = ironMan1.bi.createGraphics();

  	    //Load the image and draw it on the corresponding BufferedImage.
  	loadedImage = new javax.swing.ImageIcon("ironMan1.jpg").getImage();
  	g2dironMan1.drawImage(loadedImage,0,10,null);

  	ironMan1.tPoints = new Point2D[17];

  	    //ancoras
  	ironMan1.tPoints[0] = new Point2D.Double(0,0);
  	ironMan1.tPoints[1] = new Point2D.Double(150,0);
  	ironMan1.tPoints[2] = new Point2D.Double(0,40);
  	ironMan1.tPoints[3] = new Point2D.Double(150,40);
  	ironMan1.tPoints[4] = new Point2D.Double(0,70);
  	ironMan1.tPoints[5] = new Point2D.Double(150,70);
  	ironMan1.tPoints[6] = new Point2D.Double(0,125);
  	ironMan1.tPoints[7] = new Point2D.Double(150,125);
  	ironMan1.tPoints[8] = new Point2D.Double(45,125);
  	ironMan1.tPoints[9] = new Point2D.Double(110,125);

  	    //pontos do rosto
  	ironMan1.tPoints[10] = new Point2D.Double(75,15);
  	ironMan1.tPoints[11] = new Point2D.Double(65,35);
  	ironMan1.tPoints[12] = new Point2D.Double(85,35);
  	ironMan1.tPoints[13] = new Point2D.Double(75,50);
  	ironMan1.tPoints[14] = new Point2D.Double(72,65);
  	ironMan1.tPoints[15] = new Point2D.Double(40,80);
  	ironMan1.tPoints[16] = new Point2D.Double(110,85);

  	    //Definition of the triangles.
  	ironMan1.triangles = new int[22][3];

  	ironMan1.triangles[0][0] = 0;
  	ironMan1.triangles[0][1] = 10;
  	ironMan1.triangles[0][2] = 1;

  	ironMan1.triangles[1][0] = 0;
  	ironMan1.triangles[1][1] = 2;
  	ironMan1.triangles[1][2] = 10;

  	ironMan1.triangles[2][0] = 10;
  	ironMan1.triangles[2][1] = 1;
  	ironMan1.triangles[2][2] = 3;

  	ironMan1.triangles[3][0] = 2;
  	ironMan1.triangles[3][1] = 10;
  	ironMan1.triangles[3][2] = 11;

  	ironMan1.triangles[4][0] = 10;
  	ironMan1.triangles[4][1] = 11;
  	ironMan1.triangles[4][2] = 12;

  	ironMan1.triangles[5][0] = 10;
  	ironMan1.triangles[5][1] = 12;
  	ironMan1.triangles[5][2] = 3;

  	ironMan1.triangles[6][0] = 2;
  	ironMan1.triangles[6][1] = 11;
  	ironMan1.triangles[6][2] = 4;

  	ironMan1.triangles[7][0] = 4;
  	ironMan1.triangles[7][1] = 11;
  	ironMan1.triangles[7][2] = 13;

  	ironMan1.triangles[8][0] = 11;
  	ironMan1.triangles[8][1] = 12;
  	ironMan1.triangles[8][2] = 13;

  	ironMan1.triangles[9][0] = 12;
  	ironMan1.triangles[9][1] = 13;
  	ironMan1.triangles[9][2] = 5;

  	ironMan1.triangles[10][0] = 12;
  	ironMan1.triangles[10][1] = 3;
  	ironMan1.triangles[10][2] = 5;

  	ironMan1.triangles[11][0] = 4;
  	ironMan1.triangles[11][1] = 6;
  	ironMan1.triangles[11][2] = 15;

  	ironMan1.triangles[12][0] = 15;
  	ironMan1.triangles[12][1] = 4;
  	ironMan1.triangles[12][2] = 13;

  	ironMan1.triangles[13][0] = 13;
  	ironMan1.triangles[13][1] = 14;
  	ironMan1.triangles[13][2] = 15;

  	ironMan1.triangles[14][0] = 13;
  	ironMan1.triangles[14][1] = 14;
  	ironMan1.triangles[14][2] = 16;

  	ironMan1.triangles[15][0] = 13;
  	ironMan1.triangles[15][1] = 5;
  	ironMan1.triangles[15][2] = 16;

  	ironMan1.triangles[16][0] = 16;
  	ironMan1.triangles[16][1] = 5;
  	ironMan1.triangles[16][2] = 7;

  	ironMan1.triangles[17][0] = 6;
  	ironMan1.triangles[17][1] = 15;
  	ironMan1.triangles[17][2] = 8;

  	ironMan1.triangles[18][0] = 15;
  	ironMan1.triangles[18][1] = 14;
  	ironMan1.triangles[18][2] = 8;

  	ironMan1.triangles[19][0] = 8;
  	ironMan1.triangles[19][1] = 9;
  	ironMan1.triangles[19][2] = 14;

  	ironMan1.triangles[20][0] = 14;
  	ironMan1.triangles[20][1] = 16;
  	ironMan1.triangles[20][2] = 9;

  	ironMan1.triangles[21][0] = 16;
  	ironMan1.triangles[21][1] = 7;
  	ironMan1.triangles[21][2] = 9;


  	  	//Carrega Homem de ferro
  	  	//-----------------------------------------------------------------------------------------
  	ironMan2 = new TriangulatedImage();
  	ironMan2.bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

  	Graphics2D g2dironMan2 = ironMan2.bi.createGraphics();

  	    //Load the image and draw it on the corresponding BufferedImage.
  	loadedImage = new javax.swing.ImageIcon("ironMan2.jpg").getImage();
  	g2dironMan2.drawImage(loadedImage,0,10,null);

  	ironMan2.tPoints = new Point2D[17];

  	//ancoras
  	ironMan2.tPoints[0] = new Point2D.Double(0,0);
  	ironMan2.tPoints[1] = new Point2D.Double(150,0);
  	ironMan2.tPoints[2] = new Point2D.Double(0,40);
  	ironMan2.tPoints[3] = new Point2D.Double(150,40);
  	ironMan2.tPoints[4] = new Point2D.Double(0,70);
  	ironMan2.tPoints[5] = new Point2D.Double(150,70);
  	ironMan2.tPoints[6] = new Point2D.Double(0,125);
  	ironMan2.tPoints[7] = new Point2D.Double(150,125);
  	ironMan2.tPoints[8] = new Point2D.Double(45,125);
  	ironMan2.tPoints[9] = new Point2D.Double(110,125);

  	    //pontos do rosto
  	ironMan2.tPoints[10] = new Point2D.Double(95,20);
  	ironMan2.tPoints[11] = new Point2D.Double(85,40);
  	ironMan2.tPoints[12] = new Point2D.Double(100,40);
  	ironMan2.tPoints[13] = new Point2D.Double(90,60);
  	ironMan2.tPoints[14] = new Point2D.Double(90,75);
  	ironMan2.tPoints[15] = new Point2D.Double(25,70);
  	ironMan2.tPoints[16] = new Point2D.Double(125,90);

  	    //Definition of the triangles.
  	ironMan2.triangles = new int[22][3];

  	ironMan2.triangles[0][0] = 0;
  	ironMan2.triangles[0][1] = 10;
  	ironMan2.triangles[0][2] = 1;

  	ironMan2.triangles[1][0] = 0;
  	ironMan2.triangles[1][1] = 2;
  	ironMan2.triangles[1][2] = 10;

  	ironMan2.triangles[2][0] = 10;
  	ironMan2.triangles[2][1] = 1;
  	ironMan2.triangles[2][2] = 3;

  	ironMan2.triangles[3][0] = 2;
  	ironMan2.triangles[3][1] = 10;
  	ironMan2.triangles[3][2] = 11;

  	ironMan2.triangles[4][0] = 10;
  	ironMan2.triangles[4][1] = 11;
  	ironMan2.triangles[4][2] = 12;

  	ironMan2.triangles[5][0] = 10;
  	ironMan2.triangles[5][1] = 12;
  	ironMan2.triangles[5][2] = 3;

  	ironMan2.triangles[6][0] = 2;
  	ironMan2.triangles[6][1] = 11;
  	ironMan2.triangles[6][2] = 4;

  	ironMan2.triangles[7][0] = 4;
  	ironMan2.triangles[7][1] = 11;
  	ironMan2.triangles[7][2] = 13;

  	ironMan2.triangles[8][0] = 11;
  	ironMan2.triangles[8][1] = 12;
  	ironMan2.triangles[8][2] = 13;

  	ironMan2.triangles[9][0] = 12;
  	ironMan2.triangles[9][1] = 13;
  	ironMan2.triangles[9][2] = 5;

  	ironMan2.triangles[10][0] = 12;
  	ironMan2.triangles[10][1] = 3;
  	ironMan2.triangles[10][2] = 5;

  	ironMan2.triangles[11][0] = 4;
  	ironMan2.triangles[11][1] = 6;
  	ironMan2.triangles[11][2] = 15;

  	ironMan2.triangles[12][0] = 15;
  	ironMan2.triangles[12][1] = 4;
  	ironMan2.triangles[12][2] = 13;

  	ironMan2.triangles[13][0] = 13;
  	ironMan2.triangles[13][1] = 14;
  	ironMan2.triangles[13][2] = 15;

  	ironMan2.triangles[14][0] = 13;
  	ironMan2.triangles[14][1] = 14;
  	ironMan2.triangles[14][2] = 16;

  	ironMan2.triangles[15][0] = 13;
  	ironMan2.triangles[15][1] = 5;
  	ironMan2.triangles[15][2] = 16;

  	ironMan2.triangles[16][0] = 16;
  	ironMan2.triangles[16][1] = 5;
  	ironMan2.triangles[16][2] = 7;

  	ironMan2.triangles[17][0] = 6;
  	ironMan2.triangles[17][1] = 15;
  	ironMan2.triangles[17][2] = 8;

  	ironMan2.triangles[18][0] = 15;
  	ironMan2.triangles[18][1] = 14;
  	ironMan2.triangles[18][2] = 8;

  	ironMan2.triangles[19][0] = 8;
  	ironMan2.triangles[19][1] = 9;
  	ironMan2.triangles[19][2] = 14;

  	ironMan2.triangles[20][0] = 14;
  	ironMan2.triangles[20][1] = 16;
  	ironMan2.triangles[20][2] = 9;

  	ironMan2.triangles[21][0] = 16;
  	ironMan2.triangles[21][1] = 7;
  	ironMan2.triangles[21][2] = 9;

  	  	//Carrega Bruce Banne
  	  	//-----------------------------------------------------------------------------------------
  	hulk1 = new TriangulatedImage();
  	hulk1.bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

  	Graphics2D g2dhulk1 = hulk1.bi.createGraphics();

  	    //Load the image and draw it on the corresponding BufferedImage.
  	loadedImage = new javax.swing.ImageIcon("hulk1.jpg").getImage();
  	g2dhulk1.drawImage(loadedImage,0,10,null);

  	hulk1.tPoints = new Point2D[17];

  	    //ancoras
  	hulk1.tPoints[0] = new Point2D.Double(0,0);
  	hulk1.tPoints[1] = new Point2D.Double(150,0);
  	hulk1.tPoints[2] = new Point2D.Double(0,40);
  	hulk1.tPoints[3] = new Point2D.Double(150,40);
  	hulk1.tPoints[4] = new Point2D.Double(0,70);
  	hulk1.tPoints[5] = new Point2D.Double(150,70);
  	hulk1.tPoints[6] = new Point2D.Double(0,125);
  	hulk1.tPoints[7] = new Point2D.Double(150,125);
  	hulk1.tPoints[8] = new Point2D.Double(45,125);
  	hulk1.tPoints[9] = new Point2D.Double(110,125);

  	    //pontos do rosto
  	hulk1.tPoints[10] = new Point2D.Double(70,10);
  	hulk1.tPoints[11] = new Point2D.Double(60,45);
  	hulk1.tPoints[12] = new Point2D.Double(80,50);
  	hulk1.tPoints[13] = new Point2D.Double(75,60);
  	hulk1.tPoints[14] = new Point2D.Double(70,80);
  	hulk1.tPoints[15] = new Point2D.Double(25,100);
  	hulk1.tPoints[16] = new Point2D.Double(125,95);

  	    //Definition of the triangles.
  	hulk1.triangles = new int[22][3];

  	hulk1.triangles[0][0] = 0;
  	hulk1.triangles[0][1] = 10;
  	hulk1.triangles[0][2] = 1;

  	hulk1.triangles[1][0] = 0;
  	hulk1.triangles[1][1] = 2;
  	hulk1.triangles[1][2] = 10;

  	hulk1.triangles[2][0] = 10;
  	hulk1.triangles[2][1] = 1;
  	hulk1.triangles[2][2] = 3;

  	hulk1.triangles[3][0] = 2;
  	hulk1.triangles[3][1] = 10;
  	hulk1.triangles[3][2] = 11;

  	hulk1.triangles[4][0] = 10;
  	hulk1.triangles[4][1] = 11;
  	hulk1.triangles[4][2] = 12;

  	hulk1.triangles[5][0] = 10;
  	hulk1.triangles[5][1] = 12;
  	hulk1.triangles[5][2] = 3;

  	hulk1.triangles[6][0] = 2;
  	hulk1.triangles[6][1] = 11;
  	hulk1.triangles[6][2] = 4;

  	hulk1.triangles[7][0] = 4;
  	hulk1.triangles[7][1] = 11;
  	hulk1.triangles[7][2] = 13;

  	hulk1.triangles[8][0] = 11;
  	hulk1.triangles[8][1] = 12;
  	hulk1.triangles[8][2] = 13;

  	hulk1.triangles[9][0] = 12;
  	hulk1.triangles[9][1] = 13;
  	hulk1.triangles[9][2] = 5;

  	hulk1.triangles[10][0] = 12;
  	hulk1.triangles[10][1] = 3;
  	hulk1.triangles[10][2] = 5;

  	hulk1.triangles[11][0] = 4;
  	hulk1.triangles[11][1] = 6;
  	hulk1.triangles[11][2] = 15;

  	hulk1.triangles[12][0] = 15;
  	hulk1.triangles[12][1] = 4;
  	hulk1.triangles[12][2] = 13;

  	hulk1.triangles[13][0] = 13;
  	hulk1.triangles[13][1] = 14;
  	hulk1.triangles[13][2] = 15;

  	hulk1.triangles[14][0] = 13;
  	hulk1.triangles[14][1] = 14;
  	hulk1.triangles[14][2] = 16;

  	hulk1.triangles[15][0] = 13;
  	hulk1.triangles[15][1] = 5;
  	hulk1.triangles[15][2] = 16;

  	hulk1.triangles[16][0] = 16;
  	hulk1.triangles[16][1] = 5;
  	hulk1.triangles[16][2] = 7;

  	hulk1.triangles[17][0] = 6;
  	hulk1.triangles[17][1] = 15;
  	hulk1.triangles[17][2] = 8;

  	hulk1.triangles[18][0] = 15;
  	hulk1.triangles[18][1] = 14;
  	hulk1.triangles[18][2] = 8;

  	hulk1.triangles[19][0] = 8;
  	hulk1.triangles[19][1] = 9;
  	hulk1.triangles[19][2] = 14;

  	hulk1.triangles[20][0] = 14;
  	hulk1.triangles[20][1] = 16;
  	hulk1.triangles[20][2] = 9;

  	hulk1.triangles[21][0] = 16;
  	hulk1.triangles[21][1] = 7;
  	hulk1.triangles[21][2] = 9;

  	  	//Carrega Hulk - Monstro
  	  	//-----------------------------------------------------------------------------------------
  	hulk2 = new TriangulatedImage();
  	hulk2.bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

  	Graphics2D g2dhulk2 = hulk2.bi.createGraphics();

  	    //Load the image and draw it on the corresponding BufferedImage.
  	loadedImage = new javax.swing.ImageIcon("hulk2.jpg").getImage();
  	g2dhulk2.drawImage(loadedImage,0,10,null);

  	hulk2.tPoints = new Point2D[17];

  	    //ancoras
  	hulk2.tPoints[0] = new Point2D.Double(0,0);
  	hulk2.tPoints[1] = new Point2D.Double(150,0);
  	hulk2.tPoints[2] = new Point2D.Double(0,40);
  	hulk2.tPoints[3] = new Point2D.Double(150,40);
  	hulk2.tPoints[4] = new Point2D.Double(0,70);
  	hulk2.tPoints[5] = new Point2D.Double(150,70);
  	hulk2.tPoints[6] = new Point2D.Double(0,125);
  	hulk2.tPoints[7] = new Point2D.Double(150,125);
  	hulk2.tPoints[8] = new Point2D.Double(45,125);
  	hulk2.tPoints[9] = new Point2D.Double(110,125);

  	    //pontos do rosto
  	hulk2.tPoints[10] = new Point2D.Double(75,10);
  	hulk2.tPoints[11] = new Point2D.Double(65,80);
  	hulk2.tPoints[12] = new Point2D.Double(90, 80);
  	hulk2.tPoints[13] = new Point2D.Double(80,95);
  	hulk2.tPoints[14] = new Point2D.Double(80,120);
  	hulk2.tPoints[15] = new Point2D.Double(25,85);
  	hulk2.tPoints[16] = new Point2D.Double(120,90);

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
  	hulk3.bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

  	Graphics2D g2dhulk3 = hulk3.bi.createGraphics();

  	    //Load the image and draw it on the corresponding BufferedImage.
  	loadedImage = new javax.swing.ImageIcon("hulk3.jpg").getImage();
  	g2dhulk3.drawImage(loadedImage,0,10,null);

  	hulk3.tPoints = new Point2D[17];

  	    //ancoras
  	hulk3.tPoints[0] = new Point2D.Double(0,0);
  	hulk3.tPoints[1] = new Point2D.Double(150,0);
  	hulk3.tPoints[2] = new Point2D.Double(0,40);
  	hulk3.tPoints[3] = new Point2D.Double(150,40);
  	hulk3.tPoints[4] = new Point2D.Double(0,70);
  	hulk3.tPoints[5] = new Point2D.Double(150,70);
  	hulk3.tPoints[6] = new Point2D.Double(0,125);
  	hulk3.tPoints[7] = new Point2D.Double(150,125);
  	hulk3.tPoints[8] = new Point2D.Double(45,125);
  	hulk3.tPoints[9] = new Point2D.Double(110,125);

  	    //pontos do rosto
  	hulk3.tPoints[10] = new Point2D.Double(70,15);
  	hulk3.tPoints[11] = new Point2D.Double(60,50);
  	hulk3.tPoints[12] = new Point2D.Double(80, 50);
  	hulk3.tPoints[13] = new Point2D.Double(70,60);
  	hulk3.tPoints[14] = new Point2D.Double(70,80);
  	hulk3.tPoints[15] = new Point2D.Double(30,95);
  	hulk3.tPoints[16] = new Point2D.Double(120,95);

  	    //Definition of the triangles.
  	hulk3.triangles = new int[22][3];

  	hulk3.triangles[0][0] = 0;
  	hulk3.triangles[0][1] = 10;
  	hulk3.triangles[0][2] = 1;

  	hulk3.triangles[1][0] = 0;
  	hulk3.triangles[1][1] = 2;
  	hulk3.triangles[1][2] = 10;

  	hulk3.triangles[2][0] = 10;
  	hulk3.triangles[2][1] = 1;
  	hulk3.triangles[2][2] = 3;

  	hulk3.triangles[3][0] = 2;
  	hulk3.triangles[3][1] = 10;
  	hulk3.triangles[3][2] = 11;

  	hulk3.triangles[4][0] = 10;
  	hulk3.triangles[4][1] = 11;
  	hulk3.triangles[4][2] = 12;

  	hulk3.triangles[5][0] = 10;
  	hulk3.triangles[5][1] = 12;
  	hulk3.triangles[5][2] = 3;

  	hulk3.triangles[6][0] = 2;
  	hulk3.triangles[6][1] = 11;
  	hulk3.triangles[6][2] = 4;

  	hulk3.triangles[7][0] = 4;
  	hulk3.triangles[7][1] = 11;
  	hulk3.triangles[7][2] = 13;

  	hulk3.triangles[8][0] = 11;
  	hulk3.triangles[8][1] = 12;
  	hulk3.triangles[8][2] = 13;

  	hulk3.triangles[9][0] = 12;
  	hulk3.triangles[9][1] = 13;
  	hulk3.triangles[9][2] = 5;

  	hulk3.triangles[10][0] = 12;
  	hulk3.triangles[10][1] = 3;
  	hulk3.triangles[10][2] = 5;

  	hulk3.triangles[11][0] = 4;
  	hulk3.triangles[11][1] = 6;
  	hulk3.triangles[11][2] = 15;

  	hulk3.triangles[12][0] = 15;
  	hulk3.triangles[12][1] = 4;
  	hulk3.triangles[12][2] = 13;

  	hulk3.triangles[13][0] = 13;
  	hulk3.triangles[13][1] = 14;
  	hulk3.triangles[13][2] = 15;

  	hulk3.triangles[14][0] = 13;
  	hulk3.triangles[14][1] = 14;
  	hulk3.triangles[14][2] = 16;

  	hulk3.triangles[15][0] = 13;
  	hulk3.triangles[15][1] = 5;
  	hulk3.triangles[15][2] = 16;

  	hulk3.triangles[16][0] = 16;
  	hulk3.triangles[16][1] = 5;
  	hulk3.triangles[16][2] = 7;

  	hulk3.triangles[17][0] = 6;
  	hulk3.triangles[17][1] = 15;
  	hulk3.triangles[17][2] = 8;

  	hulk3.triangles[18][0] = 15;
  	hulk3.triangles[18][1] = 14;
  	hulk3.triangles[18][2] = 8;

  	hulk3.triangles[19][0] = 8;
  	hulk3.triangles[19][1] = 9;
  	hulk3.triangles[19][2] = 14;

  	hulk3.triangles[20][0] = 14;
  	hulk3.triangles[20][1] = 16;
  	hulk3.triangles[20][2] = 9;

  	hulk3.triangles[21][0] = 16;
  	hulk3.triangles[21][1] = 7;
  	hulk3.triangles[21][2] = 9;
  	 
  	 //The indexing for the triangles must be the same as in the
    //the first image.
  	//capitain2.triangles = capitain1.triangles;
  	//hulk3.triangles = ironMan2.triangles;
  }

  //This method is called in regular intervals. This method computes
  //the updated image/frame and calls the repaint method to draw the
  //updated image on the window.
  public void run()
  {
    //Since this method is called arbitrarily often, interpolation must only
    //be carred out while alpha is between 0 and 1.
  	if (alpha>=0 && alpha<=1)
  	{
      //Generate the interpolated image.
  		mix = hulk2.mixWith(hulk3,alpha);

      //Draw the interpolated image on the BufferedImage.
  		buffid.g2dbi.drawImage(mix,0,0,null);

      //Call the method for updating the window.
  		buffid.repaint();
  	}

    //Increment alpha.
  	alpha = alpha+deltaAlpha;

  }


  public static void main(String[] argv)
  {

    //Width of the window.
  	int width = 150;
    //Height of the window.
  	int height = 130;

    //Specifies (in milliseconds) when the frame should be updated.
  	int delay = 30;

    //The BufferedImage to be drawn in the window.
  	BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);


    //The window in which everything is drawn.
  	BufferedImageDrawer bid = new BufferedImageDrawer(bi,width,height);
  	bid.setTitle("Transforming shape and colour");

    //The TimerTask in which the repeated computations for drawing take place.
  	Morphing mcs = new Morphing(bid);


  	Timer t = new Timer();
  	t.scheduleAtFixedRate(mcs,0,delay);

  }

}


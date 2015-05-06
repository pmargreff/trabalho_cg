import java.awt.*;
import java.awt.geom.*;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.image.BufferedImage;


//TODO: O resto das imagens
//TODO: jogar isso no Animaiton
/**
* A simple example for transforming one triangulated image into another one.
* For the animation, the doube buffering technique is applied in the same way 
* as in the clock example in the class DoubeBufferingClockExample.
*
* @author Frank Klawonn
* Last change 31.05.2005
*
* @see TriangulatedImage
* @see BufferImageDrawer
* @see DoubeBufferingClockExample
*/
public class Morphing extends TimerTask
{

  //The window in which the transformation is shown.
  private BufferedImageDrawer buffid;

  //The two images to be transformed into each other will be scaled to 
  //this size.
  private int width;
  private int height;

  //The number of steps (frames) for the transformation.
  private int steps;

  //The first triangulated image.
  private TriangulatedImage capitain1;

  //The second triangulated image.
  private TriangulatedImage capitain2;

  //This is used for generating/storing the intermediate images.
  private BufferedImage mix;

  //A variable which is increased stepwise from 0 to 1. It is needed
  //for the computation of the convex combinations.
  private double alpha;

  //The change of alpha in each step: deltAlpha = 1.0/steps
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

    //This object is used for loading the two images.
    Image loadedImage;
    //testa = 75 , 2
    //olho osquerdo = 65 , 50
    //olho direito = 80 , 50
    //busto = 75 , 70
    //queixo = 75 , 85
    //ombro esquerdo = 20 , 95
    //ombro direito = 125 , 95

    //Generating the first triangulated image:
    capitain1 = new TriangulatedImage();

    //Define the size.
    capitain1.bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
    //Generate the Graphics2D object.
    Graphics2D g2dcapitain1 = capitain1.bi.createGraphics();

    //Load the image and draw it on the corresponding BufferedImage.
    loadedImage = new javax.swing.ImageIcon("capitain1.jpg").getImage();
    g2dcapitain1.drawImage(loadedImage,0,10,null);

    //Definition of the points for the triangulation.
    //define primeiro pontos ancoras das bordas
    //e depois os pontos do rosto
    
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




    //The same for the second image.
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



    //The indexing for the triangles must be the same as in the
    //the first image.
    capitain2.triangles = capitain1.triangles;


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
      mix = capitain1.mixWith(capitain2,alpha);

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
    int delay = 50;

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


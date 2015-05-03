
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;


public class Animation extends TimerTask
{
  //TODO 
  //Fazer uma lista com todos os pontos ao invés de deslocar
  //refatorar o código
  //tirar as funções que não são usadas
  //ENCONTRAR PONTO DE PARADA
  //na verdade acho que preciso concertar a maneira de se mover, não apenas o ponte de parada ...
  
  //The window in which everything is shown.
  private BufferedImageDrawer buffid;
  //The background image
  private BufferedImage bg;
  //Length/width of the  quad
  private double quadSize;
  //The quad of the  centred in the origin
  private Rectangle2D.Double quad;
  //Rotação horária
  private AffineTransform hRotation;
  //Rotação Anti-horária
  private AffineTransform aRotation;
  private AffineTransform accumulatedRotation;
  private AffineTransform singleTranslation;
  private AffineTransform accumulatedTranslation;
  private AffineTransform quadTransform;

  Point pi ;
  Point pf ;
//This transformation is to combine the single transformations
  //(rotation and translation) of the second quad.
  AffineTransform origem;
  Flag turn = new Flag(false);

  private int radius = 100;     
  private int segments = 50;   
  private int repetitions = 4;
  private int x_0 = 130;
  private int y_0 = 20;
    
  ArrayList<Point> point_list;

  double size ;

  double jump_delta;

  double remainder ;
  double jump_alfa ;

  int jump;

  AffineTransform tr;
  int iterador; //vamos ter que dar um jeito de controlar ele sei lá
  /**
  * Constructor
  *
  * @param bid          The buffered image to be drawn
  * @param backGround   The background (buffered) image
  * @param height       Width of the window
  * @param delay        Defines after how many milliseconds the image/quad is 
  *                     is updated (needed for the synchronisation of the ).
  */
  Animation(  int r, int s, int n, int x, int y , BufferedImageDrawer bid, BufferedImage backGround,int width, int height,int delay) {
    buffid = bid;

    this.radius      = r;
    this.segments    = s;
    this.repetitions = n;
    this.x_0         = x;  
    this.y_0         = y;  

    //The lines should have a thickness of 3.0 instead of 1.0.
    buffid.g2dbi.setStroke(new BasicStroke(2.0f));

    //Use of antialiasing to have nicer lines.
    buffid.g2dbi.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON);

    /*yUp defines a translation allowing the specification of objects in "real"
      coordinates where the y-axis points upwards and the origin of the coordinate
      system is located in the lower left corner of the window.
    */
      AffineTransform yUp = new AffineTransform();
      yUp.setToScale(1, -1);
      AffineTransform translate = new AffineTransform();
      translate.setToTranslation(0, height);
      yUp.preConcatenate(translate);

    //Apply the transformation to the Graphics2D object to draw everything
    //in "real" coordinates.
      buffid.g2dbi.transform(yUp);

      buffid.g2dbi.setPaint(Color.black);

    bg = backGround;

    pi = new Point();
    pf = new Point();
    point_list = calculateBrasenham();
    size = (double)point_list.size();

    jump_delta = size / segments;

    remainder = size % segments;
    jump_alfa = (int)(remainder/jump_delta);

    jump = (int) jump_delta;

    segments = segments + (int)jump_alfa;

    pi.set_x(point_list.get(0).get_x());
    pi.set_y(point_list.get(0).get_y());
      quadSize = 30;

      quad = new Rectangle2D.Double(-quadSize/2,-quadSize/2,
        quadSize,quadSize);

      hRotation = new AffineTransform();
      hRotation.setToRotation(-delay*Math.PI/5000);

      aRotation = new AffineTransform();
      aRotation.setToRotation(delay*Math.PI/5000);

      accumulatedRotation = new AffineTransform();

      singleTranslation = new AffineTransform();
      //singleTranslation.setToTranslation(point_list.get_x(), point_list.get_y());
      accumulatedTranslation = new AffineTransform();
      accumulatedTranslation.setToTranslation(x_0 + pi.get_x(), y_0 + pi.get_y());  


    //In order to position the  inside the window in the beginning
    //of the animation, the translation incorporates already a shift 
    //to the right and upwards.
      accumulatedTranslation.setToTranslation(width/2,height/2);

      quadTransform = new AffineTransform();
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
              final_list.get(i).set_x(tmp.get_x() + radius);
              final_list.get(i).set_y(tmp.get_y() );
          }


          return final_list;

      }

    private void calculateTransformations(Point pf, Flag turn, AffineTransform t) {

      AffineTransform tr = new AffineTransform();
      
      tr.setToTranslation(pf.get_x(), pf.get_y());      

      if ( turn.get_value() == true ) {

        origem = new AffineTransform();
        origem.setToTranslation(x_0,y_0);
        
        AffineTransform at = AffineTransform.getRotateInstance(-Math.PI / 4,pf.get_x() ,pf.get_y() );
      
        t.concatenate(origem);
        t.concatenate(at);
        t.concatenate(tr);
        t.scale(1.5,1.5);

          turn.set_value(false); 
       
      } else {
        origem.setToTranslation(x_0,y_0);
        
        AffineTransform at = AffineTransform.getRotateInstance(0,pf.get_x() ,pf.get_y() );
        t.concatenate(origem);
        t.concatenate(at);
        t.concatenate(tr);

        turn.set_value(true);
      }
    }

    private void calculateInterpolationPoints(Point pf, int jump, int i ,int r ) {

        int size = point_list.size() - 1;

        if ( (i * jump  + jump) > size ) {
          pf.set_x(point_list.get(size).get_x() + r);
          pf.set_y(point_list.get(size).get_y());
           
        } else {
          pf.set_x(point_list.get((i * jump ) + jump ).get_x() + r);
          pf.set_y(point_list.get((i * jump ) + jump ).get_y()); 
        }
    }

    public void run()
    {

      // quadTransform.preConcatenate(accumulatedTranslation);
      

     //Draw the background.
      buffid.g2dbi.drawImage(bg,0,0,null);

     //Draw the quad of the .

     //This will update the image/quad in the window.
      buffid.repaint();

      quadTransform.setToTranslation(x_0 + point_list.get(iterador).get_x(), y_0 + point_list.get(iterador).get_y());  

     //Computation of the accumulated translation of the .
     //accumulatedTranslation.preConcatenate(singleTranslation);
      //fui tomar banho, estava por aqui tentando resolver o problema da rotação <<<<<<<<<<<<<<<<<<<<
     //Computation of the accumulated rotation of the second quad.
     //accumulatedRotation.preConcatenate(hRotation);
      //tr.setToTranslation(pf.get_x(), pf.get_y());      


      if ( turn.get_value() == true ) {
       quadTransform.preConcatenate(hRotation);
       if (iterador > 250){
         turn.set_value(false);
         iterador = 0;
       }  
     } else {
       quadTransform.preConcatenate(aRotation);
       if (iterador > 250){
         turn.set_value(true);
         iterador = 0;
       }

     }
     iterador++;
     System.out.println(iterador);
      buffid.g2dbi.draw(quadTransform.createTransformedShape(quad));
      quadTransform.setTransform(accumulatedRotation);
   }

//devolvel o main tem que ficar aqui, 
   //não vai rodar por conflito na classe TaskTimer
   //contador_de_horas_tentando_deixar_o_main_em_um_arquivo_separado: +-8 horas
   public static void main(String[] argv)
   {
    if ( argv.length == 5 ) {

      int radius    = Integer.parseInt(argv[0]);
      int segments  = Integer.parseInt(argv[1]);
      int repetitions = Integer.parseInt(argv[2]);
      int x_center  = Integer.parseInt(argv[3]);
      int y_center  = Integer.parseInt(argv[4]);

      int width  = radius * 2 * repetitions + 100 + x_center;
      int height = radius + y_center + 100;
      
      x_center = x_center - radius;

      if ( segments < 2 ) segments = 2;
      if ( repetitions < 1 ) repetitions = 1;
      if ( radius < 2 ) radius = 2;

  
    //Specifies (in milliseconds) when the quad should be updated.
    int delay = 10;

    //The BufferedImage to be drawn in the window.
    BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

    //The background.
    BufferedImage backGround = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
    Graphics2D g2dBackGround = backGround.createGraphics();

    //The lines should have a thickness of 3.0 instead of 1.0.
    g2dBackGround.setStroke(new BasicStroke(2.0f));

    //The background is painted white first.
    g2dBackGround.setPaint(Color.white);
    g2dBackGround.fill(new Rectangle(0,0,width,height));


    //The window in which everything is drawn.
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



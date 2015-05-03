import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;


public class Animation extends TimerTask
{
  //TODO list: parar a thread ao invés de fazer um loop
  //The window in which everything is shown.
  private BufferedImageDrawer buffid;
  
  //The background image
  private BufferedImage bg;
  
  //Length/width of the  quad
  private double quadSize;
  
  //The quad of the  centred in the origin
  private Rectangle2D.Double quad;
  
  //Transformações aplicadas aos objetos
  private AffineTransform accumulatedTranslation;
  private AffineTransform quadTransform;
  private AffineTransform origem;
  private AffineTransform tracking;
  private AffineTransform inicialTranformation;
  private AffineTransform transfIntermed;

  //Variáveis para cálculo do bresenham
  private Point pi;
  private Point pf;
  private double size;
  private double jump_delta;
  private double remainder;
  private double jump_alfa;
  private int jump;
  private int shift = 0; //variávél responsável pelo controle do shift no @run  

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

  //tracking para as transformações sobre o quadrado
  private AffineTransform tr;
  
  //shape que irá sofrer as transformações
  private Shape s;
  
  private double[] matrizInicial;
  private double[] matrizFinal;


  /**
  * Constructor
  * @param bid          The buffered image to be drawn
  * @param backGround   The background (buffered) image
  */
  Animation(int r, int s, int n, int x, int y , BufferedImageDrawer bid, BufferedImage backGround,int width, int height,int delay) {
    buffid = bid;

    this.radius      = r;
    this.segments    = s;
    this.repetitions = n;
    this.x_0         = x;  
    this.y_0         = y;  

    //The lines should have a thickness of 3.0 instead of 1.0.
    buffid.g2dbi.setStroke(new BasicStroke(1.5f));

    //Use of antialiasing to have nicer lines.
    buffid.g2dbi.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON); 

    AffineTransform normalizer = new AffineTransform();
    normalizer.setToScale(1, -1);
    AffineTransform translate = new AffineTransform();
    translate.setToTranslation(0, height);
    normalizer.preConcatenate(translate);

    buffid.g2dbi.transform(normalizer);

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
    quadSize = 10;

    quad = new Rectangle2D.Double(-quadSize/2,-quadSize/2, quadSize,quadSize);

    accumulatedTranslation = new AffineTransform();
    accumulatedTranslation.setToTranslation(x_0 + pi.get_x(), y_0 + pi.get_y());  
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

public double[] interpola(double[] inicial,double[] terminal, double alfa) {
  double[] intermed = new double[inicial.length];

  for (int i=0; i<intermed.length; i++)
  {
    intermed[i] = (1-alfa)*inicial[i] + alfa*terminal[i];
  }
  return(intermed);
}

public void run()
{
  matrizInicial = new double[6];
  matrizFinal   = new double[6];

  inicialTranformation = new AffineTransform();
  tracking = new AffineTransform();

  inicialTranformation.setToTranslation(x_0 + pi.get_x(), y_0 + pi.get_y());
  inicialTranformation.getMatrix(matrizInicial); 

  Flag turn = new Flag(true);

  for ( int r = 0; r < repetitions; r++ ) {

    int shift = r * radius * 2;

    for (int j = 0; j < segments; j++) {

      calculateInterpolationPoints(pf, jump, j, shift);

      calculateTransformations( pf, turn, tracking);

      tracking.getMatrix(matrizFinal);

      Shape aux = null;

      for (double step = 0; step < 350; step++ ) {
        transfIntermed = new AffineTransform( interpola( matrizInicial, matrizFinal, step/350) );
        s = transfIntermed.createTransformedShape(quad);
        buffid.g2dbi.drawImage(bg,0,0,null);
        buffid.repaint();
        buffid.g2dbi.draw(quadTransform.createTransformedShape(s));
        aux = s;
      }

      tracking.setToIdentity();

      for (int i = 0; i < 6; i++ ) matrizInicial[i] = matrizFinal[i];        
    }

}
    
    //Thread.currentThread().interrupted();

int i = 0;
while(i == i){}
}

  //o main tem que ficar aqui, 
  //não vai rodar em arquivo próprio por conflito na classe TaskTimer
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
    int delay = 100;

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



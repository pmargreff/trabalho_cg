package Animation;

import java.awt.*;
import java.awt.geom.*;
import java.util.Date; 
import java.util.ArrayList;
import Point.Point;
import java.util.Collections;


public class Animation extends Frame {

public Animation(  int r, int s, int n, int x, int y ) {

    this.radius      = r;
    this.segments    = s;
    this.repetitions = n;
    this.x_0         = x;  
    this.y_0         = y;  

    point_list = calculateBrasenham();

    addWindowListener(new MyFinishWindow());
    
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
          final_list.get(i).set_x(tmp.get_x() + x_0 + radius);
          final_list.get(i).set_y(tmp.get_y() + y_0);
      }


      return final_list;

  }

private void calculateTransformations(Point pf, Flag turn, AffineTransform t) {

  AffineTransform tr = new AffineTransform();
  
  tr.setToTranslation(pf.get_x(), pf.get_y());      

  if ( turn.get_value() == true ) {

    AffineTransform origem = new AffineTransform();
    origem.setToTranslation(x_0,y_0);
    
    AffineTransform at = AffineTransform.getRotateInstance(-Math.PI / 4,pf.get_x() ,pf.get_y() );
    
      AffineTransform scaling = new AffineTransform();
      t.concatenate(origem);
      t.concatenate(at);
      t.concatenate(tr);
      t.scale(1.5,1.5);

      turn.set_value(false); 
   
  } else {

    AffineTransform scaling = new AffineTransform();
    scaling.setToScale(10.0/15.0,10.0/15.0);
    
    AffineTransform origem = new AffineTransform();
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

    System.out.format("(%d, %d)\t%d\n",pf.get_x(), pf.get_y(), i );

}

public void paint(Graphics g) {

    Graphics2D g2d = (Graphics2D) g;

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
  
    Point pi = new Point();
    Point pf = new Point();

    double size = (double)point_list.size();


    double jump_delta = size / segments;


    double remainder = size % segments;
    double jump_alfa = jump_delta / remainder;

    int jump = (int) (jump_alfa + jump_delta);

    //segments = (int) Math.ceil( size / (jump_alfa + jump_delta));


    System.out.format("points %d seg: %d  jump: %d  the_jump: %f\n", point_list.size(), segments, jump, jump_alfa + jump_delta);

    pi.set_x(point_list.get(0).get_x());
    pi.set_y(point_list.get(0).get_y());

    Rectangle2D.Double quad = new Rectangle2D.Double(0, 0, 10 , 10);

    AffineTransform normalizer = new AffineTransform();
    normalizer.setToScale(1, -1);

    AffineTransform translate = new AffineTransform();
    translate.setToTranslation(0, y_0 + radius + 100);
    normalizer.preConcatenate(translate);

    g2d.transform(normalizer);

    Shape s;
    
    double[] matrizInicial = new double[6];
    double[] matrizFinal   = new double[6];

    AffineTransform inicialTranformation = new AffineTransform();

    AffineTransform tracking = new AffineTransform();

    AffineTransform  transfIntermed;

    inicialTranformation.setToTranslation(x_0 + pi.get_x(), y_0 + pi.get_y());
    inicialTranformation.getMatrix(matrizInicial); 

    Flag turn = new Flag(true);

     g2d.setPaint(Color.white);
     g2d.fill(new Rectangle(0,0,radius * 2 * repetitions + 100 + x_0,radius + y_0 + 100));

    for ( int r = 0; r < repetitions; r++ ) {

      int shift = r * radius * 2;

      for (int j = 0; j < segments; j++) {

        calculateInterpolationPoints(pf, jump, j, shift);

        calculateTransformations( pf, turn, tracking);
       
        tracking.getMatrix(matrizFinal);

        Shape aux = null;

        for (double step = 0; step < 35; step++ ) {

          transfIntermed = new AffineTransform( interpola( matrizInicial, matrizFinal, step/35) );
          s = transfIntermed.createTransformedShape(quad);
          
          limpaJanela(g2d, aux);
          g2d.setPaint(Color.black);          
          g2d.draw(s);
          congela(10);
          aux = s;
        }
        
        tracking.setToIdentity();

        for (int i = 0; i < 6; i++ ) matrizInicial[i] = matrizFinal[i];
        
      }
   
      System.out.format("Repts: %d\n", repetitions); 
           
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

//método que limpa a janela
public void limpaJanela(Graphics2D g, Shape a)
 {
    if (a != null) {

      Rectangle2D.Double tmp = (Rectangle2D.Double)a.getBounds2D();

     double x = tmp.getX();
     double y = tmp.getY();
     g.setPaint(Color.white);
     g.fill(new Rectangle2D.Double(x - 5, y - 5, 30, 30));

    }
}

  //recebe o tempo em milissegundos
  public void congela(long t)
  {
    long finish = (new Date()).getTime() + t;
    while( (new Date()).getTime() < finish ){}
  }

  private int radius;     
  private int segments;   
  private int repetitions;
  private int x_0, y_0;
  private ArrayList<Point> point_list;
}
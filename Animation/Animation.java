
package Animation;

import java.awt.*;
import java.awt.geom.*;
import java.util.Date; 
import java.util.ArrayList;
import Point.Point;
import java.util.*;


//Animation(cc, tracker, turn, delay, origin, semiCircle.getNextSegmentPoint(j, shift)


// TODO: ta uma bosta o codigo, refatorar os nomes;

public class Animation extends TimerTask {


  private CCanvas stage;

  private AffineTransform rotation;
  private AffineTransform translation;
  private AffineTransform tracker;
  private Point o, n;
  private Flag b;


  public Animation( CCanvas c, AffineTransform t, Flag b, Point o, Point n ) {

    this.stage   = c;  
    this.tracker = t;
    this.o = new Point(o.get_x(),o.get_y() );
    this.n = new Point(n.get_x(),n.get_y() );
    this.b = new Flag(b.get_value());


    //c.normalizeCoords(); // nao ta implementado;


    /* modificar usando o objeto canvas no lugar apropriado

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);    chamado no canvas
  
    */
    
  }


/*

  // the valores pequenos:
    
    Scala com 50 de delay
    >>> 15 / (0.03 * 50.0)
    10.0
    >>> 10 * (0.03 * 50.0)
    15.0

   logo: ScaleValueInTurn 


   r_{i + 1} = r_{i} / (factor), if turn == true and let factor be 0.03 * delay
   r_{i + 1} = r_{i} * (factor), if turn == false and let factor be 0.03 * delay
  

  rotacao com 50 de delay
  radians(-45)/delay, if turn == true
  radians(45)/delay, if turn == false


  //valores quebrados e pequenos
  // Group A
  scale_10;
  rotate_45;

  //valores quebrados e pequenos
  // Group B
  scale_15;
  rotate_45R;

  accumlator;

  if ( turn )
    acumulator.concatane ( Group A)
  else
    acumulator.concatane ( Group B)  



   for (repetitions)
    for (segments)
    Timer t = new Timer();
    // passa repetitions e calcular shitfts (constroi a animacao para uma repeticao detarminada por 'repetition' e escalona)
    t.scheduleAtFixedRate(new DoubleBufferingClockExample(bid, backGround, height, delay),0,delay); 


*/



  @Override
  public void run() {
      //this.rotation    = new AffineTransform();
    this.translation = new AffineTransform();
    
    this.translation.setToTranslation(n.get_x(), n.get_y());  // concatenar por ultimo

    
    AffineTransform origem = new AffineTransform();
    origem.setToTranslation(o.get_x(), o.get_y());


    tracker.concatenate(origem); // primeira concatenacao

    if ( b.get_value() == true ) {
    
      this.rotation = AffineTransform.getRotateInstance((-Math.PI/4), n.get_x(), n.get_y() );

      tracker.concatenate(rotation);
      tracker.concatenate(translation);
      tracker.scale(1.5, 1.5);
  
      b.set_value(false);

    } else {

      this.rotation = AffineTransform.getRotateInstance( 0, n.get_x(), n.get_y() );
      tracker.concatenate(rotation);

      b.set_value(true);
    }

    // chamar atributos do canvas para imprimir o quadrado

    stage.g2dbi.setPaint(Color.black);
    stage.g2dbi.fill(tracker.createTransformedShape(new Rectangle2D.Double(0,0, 10, 10)));

    stage.repaint();

  }
}
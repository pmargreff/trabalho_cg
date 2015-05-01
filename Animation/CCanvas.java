package Animation;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;


/**
* A class for drawing a BufferedImage object. It can be used in 
* connection with double buffering.
*
* @author Frank Klawonn
* Last change 27.05.2005
*/

/*
  TODO: Implementar a logica de mapeamento de pontos e carregamento das imagens na classe canvas;

*/ 

public class CCanvas extends Frame
{
  //These image is drawn when the paint method is called.
  public BufferedImage bi;

  //This Graphics2D object can be used to draw on bi.
  public Graphics2D g2dbi;

  //The Graphics2D object used in the paint method.
  public Graphics2D g2d;

  /**
  * Constructor
  */
  public CCanvas(BufferedImage buffIm,  int width, int height) {
    bi = buffIm;
    g2dbi = bi.createGraphics();


    AffineTransform yUp = new AffineTransform();
    yUp.setToScale(1, -1);
    AffineTransform translate = new AffineTransform();
    translate.setToTranslation(0, height);
    yUp.preConcatenate(translate);

    //Apply the transformation to the Graphics2D object to draw everything
    //in "real" coordinates.
    g2dbi.transform(yUp);

    g2dbi.setPaint(Color.white);


    //Enables the closing of the window.
    addWindowListener(new MyFinishWindow());

    this.setTitle("\tTrabalho II - Bruno e Pablo");
    this.setSize(width,height);
    this.setVisible(true);

  }


  public void paint(Graphics g) {
    update(g);
  }



  public void update(Graphics g) {

    //System.out.format("To dentro do update\n");
    g2d = (Graphics2D) g;
    g2d.setPaint(Color.white);
    g2d.drawImage(bi,0,0,null);
  }


}

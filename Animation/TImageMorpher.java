import java.util.*;
import java.awt.*;
//import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class TImageMorpher extends TimerTask {

	private int segIndex;
	private double alpha;
	private double delta_alpha;
	private Point pi;
	private Point pf;
	private TImageManager tImages;
	private BufferedImageDrawer bid;
	private BufferedImage mixedImage;
	private TriangulatedImage currentImage;


public TImageMorpher(BufferedImageDrawer  b, TImageManager tim, Point i, Point f, int step, int index) {

	this.bid 		 = b;
	this.tImages 	 = tim;
	this.alpha 		 = 0;
	this.delta_alpha = 1/step;
	this.segIndex 	 = index;

	this.pi = new Point(i.get_x(), i.get_y());
	this.pf = new Point(f.get_x(), f.get_y());

	int size = tImages.getSize();

	this.currentImage = tImages.get(segIndex % size);
}


@Override
public void run() {

	System.out.println("im Here");

	if (alpha >= 0 && alpha <= 1) {

		int interpolated_x = (int) ((1 - alpha) * pi.get_x() + alpha * pf.get_x()); // tenho que calcular o pf
      
     	int interpolated_y = (int) ((1 - alpha) * pi.get_y() + alpha * pf.get_y());
        
       // Calcula a interpolacao entre a imagem atual e a proxima (fechando, assim, um segmento), 
       //respeitando o numero de imagens que existem para interpolar por meio do        

     	mixedImage = currentImage.mixWith(tImages.getNext(segIndex), alpha);

       	//Draw the interpolated image on the BufferedImage.
    	//buffid.g2dbi.drawImage(bg,0,0,null);
     	bid.g2dbi.drawImage(mixedImage, interpolated_x, interpolated_y, null);
     	bid.repaint();

     	alpha += delta_alpha;

	} else {
		alpha = 0;
	}

}

}
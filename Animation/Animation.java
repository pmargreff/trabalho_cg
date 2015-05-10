/*
	TODO: Nao sei por que, mas o programa custa a terminar. Creio que seja pelo problema do delay;
	TODO: 1. Resolver a questao do numero de imagens e da aplicacao do delay [Importante]
	TODO: Se der tempo, tentar centralizar a imagem do fundo e ver se eh possivel trabalhar com png para tirar o fundo da imagem;		

	Docs:
		Image tutorial: https://docs.oracle.com/javase/tutorial/2d/images/

*/

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.awt.image.BufferedImage;


public class Animation extends TimerTask {


  private int radius;     
  private int segments;   
  private int repetitions;
  private int x_0;
  private int y_0;
  private int jump;
  private int height;
  private int shift;
  private int segment_index;
  private int repetition_controller;

  private double alpha;
  private double delta_alpha;

  private Point pi;
  private Point pf;
  //lista que conterá o trajeto  
  private ArrayList<Point> point_list;

  // new try
  private BufferedImage mixedImage;
  private BufferedImage background;
  private BufferedImageDrawer bid;
  private TImageManager tImages;
  private TriangulatedImage current_image;


  /**
  * Constructor
  * @param bid          The buffered image to be drawn
  * @param backGround   The background (buffered) image
  */
public Animation(int r, int s, int n, int x, int y, int h, BufferedImageDrawer b, BufferedImage bg) {

  	this.radius        = r;
  	this.repetitions   = n;
  	this.x_0           = x;  
  	this.y_0           = y;
    this.height        = h;
    this.shift         = 0;
    this.segment_index = 0;
    this.alpha         = 0;
    this.delta_alpha   = 1f/100f;     //mudar dps
    this.repetition_controller = 0;

    this.bid = b;
    this.background = bg;

    bid.g2dbi.setStroke(new BasicStroke(1.5f));
    bid.g2dbi.setPaint(Color.black);
    bid.g2dbi.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON); 
    bid.g2dbi.transform(normalizedCoords(h));

    pi = new Point();
    pf = new Point();
    point_list = calculateBrasenham();    
    this.segments = normalizedSegement(s);
    pi.set_x(point_list.get(0).get_x());
    pi.set_y(point_list.get(0).get_y()); 

    this.tImages = new TImageManager("data/","data/point_info" ,"jpg" , 150, 125 );  

    this.current_image = this.tImages.get(0);
  	
}



@Override
public void run() {

    if (repetitions > 0) {

        calculateInterpolationPoints(segment_index, shift);

        alpha = 0;  // nao sei

        for (int i = 0; i < 70; i++) {
            if (alpha >= 0f && alpha <= 1f) {
              // Esse arredondamento ta dando problema
              int interpolated_x = (int) ((1 - alpha) * pi.get_x() + alpha * pf.get_x());
              int interpolated_y = (int) ((1 - alpha) * pi.get_y() + alpha * pf.get_y());

               mixedImage = current_image.mixWith(tImages.getNext(segment_index), alpha);
                // background aqui
               bid.g2dbi.drawImage(background,0 ,0, null);
               bid.g2dbi.drawImage(mixedImage,interpolated_x ,interpolated_y, null);
               bid.repaint();

            } else {
                alpha = 0;
            }

            alpha += delta_alpha;

        }

        current_image = tImages.getNext(segment_index);
        pi.set_x(pf.get_x());
        pi.set_y(pf.get_y());
        segment_index = (segment_index + 1) % this.segments;


        if (segment_index == 0) {
            repetition_controller++;
            repetitions--;
            shift = repetition_controller * 2 * this.radius;
        } 

    } else {
        this.cancel();
    }



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

// pode dar pau
// Atualiza o pf;
public void calculateInterpolationPoints(int i ,int r ) {

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

public static AffineTransform normalizedCoords(int height) {

	AffineTransform normalizer = new AffineTransform();
  	normalizer.setToScale(1, -1);
  	AffineTransform translate = new AffineTransform();
  	translate.setToTranslation(0, height);
  	normalizer.preConcatenate(translate);

  	return normalizer;

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
		int delay = 200;

    //The BufferedImage to be drawn in the window.

    //The background.
		BufferedImage backGround = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		Image theImage = new javax.swing.ImageIcon("data/fundo.jpg").getImage();
		Graphics2D g2dBackGround = backGround.createGraphics();
		g2dBackGround.setStroke(new BasicStroke(2.0f));
	    //The background is painted white first.
		g2dBackGround.setPaint(Color.white);
		
		g2dBackGround.transform(Animation.normalizedCoords(height));

		g2dBackGround.fill(new Rectangle(0,0,width,height));
		g2dBackGround.drawImage(theImage, (width/2) - 162 , (height/2) - 72,null);	// - 155 ?
	    


	    BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

	    //The window in which everything is drawn.
		BufferedImageDrawer bid = new BufferedImageDrawer(bi,width,height);

	    Animation scene = new Animation(radius, segments, repetitions, x_center, y_center,height, bid, backGround);
	    Timer t = new Timer();
	  	t.scheduleAtFixedRate( scene, 0, delay);


	}
	else {

		System.out.println("Parametros invalidos.\nDigite: java Main < raio > < segmentos > < repeticoes > < x_0 >  < y_0 >\n");
	}

}

}



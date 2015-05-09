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
  private int imageHeight;
  private int imageWidth;
  //The background image
  private Point pi;
  private Point pf;
  private BufferedImage bg;
  //This is used for generating/storing the intermediate images.
  private BufferedImage mix;
  //The window in which everything is shown.
  private BufferedImageDrawer buffid;
  //lista que conterá o trajeto  
  private ArrayList<Point> point_list;

  //Size of images

	private double alpha;
	private double deltaAlpha;
	private int steps;
  // Image loadedImage;

	private TImageManager tImages;

	private TriangulatedImage currentImage;
	private TriangulatedImage nextImage;

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

  	//calculateTriangles(imageWidth, imageHeight);
  	this.tImages = new TImageManager("data/","data/point_info" ,"jpg" , 150, 125 );

	currentImage = tImages.get(0);

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

/*
   Acho que cada vez que o run for chamado ele tem que calcular um segmento.
   Portanto, o estado do calculo deve ser mantido e o proximo estado calculado no final;
   Talvez, como usamos shifts para o calculo da animaçao, isso seja um problema. Pensar em uma alternativa, ou
   cirar um vetorzao com os pontos do bresenham;
   PS: 
      (1) O metodo mixWith() ja calcula a interpolacao entre as duas imagens bufferizadas (ver  MorphingCandS.java);
      (2) O alfa talvez tenha que depender do delay;
*/
public void run() {
  
  if (repetitions > 0) {
      repetitions--;

  } else {
    this.cancel();
  }

}


/*
  150 * 125 * 8 * 70 * 20 = 210000000 (200MB)

  Esse run() ta errado. Precisa ser calculado utilizando o delay, e a 
  classe TriangulatedImage ja calcula a interpolaçao convexao;

*/


// public void run() {

//   	if (repetitions > 0) {
//   		repetitions--;

//   		int shift = repetition_controller * radius * 2;

// 		Point initialPoint = new Point();
// 		initialPoint.set_x(pi.get_x() + shift);
// 		initialPoint.set_y(pi.get_y());

//   		for (int j = 0; j < segments; j++) {

//   			calculateInterpolationPoints(pf, j, shift);
//   			Point interpolatedPoint = new Point();
  			
//   			for (double step = 0; step < 70; step++ ) {
//   				if (alpha >= 0 && alpha <= 1) {
//   					int castAux = (int) ((1 - alpha) * initialPoint.get_x() + alpha * pf.get_x());
//   					interpolatedPoint.set_x(castAux);
  					
//   					castAux = (int) ((1 - alpha) * initialPoint.get_y() + alpha * pf.get_y());
//   					interpolatedPoint.set_y(castAux);
  				    
//   				     Calcula a interpolacao entre a imagem atual e a proxima (fechando, assim, um segmento), 
//   				    respeitando o numero de imagens que existem para interpolar por meio do 
  				     

//   					//mix = capitain1.mixWith(capitain2,alpha);
//   					mix = currentImage.mixWith(tImages.getNext(j), alpha);

//   				    //Draw the interpolated image on the BufferedImage.
//   					//buffid.g2dbi.drawImage(bg,0,0,null);
//   					buffid.g2dbi.drawImage(mix,interpolatedPoint.get_x(),interpolatedPoint.get_y(),null);
//   				}
  				
//   				buffid.repaint();
//   				alpha = alpha+deltaAlpha;
//   			}

//   			currentImage = tImages.getNext(j);	// faz a ultima anterior virar a primeira.

//   			initialPoint.set_x(pf.get_x());
//   			initialPoint.set_y(pf.get_y());

//   			alpha = 0;
//   		}

//   		repetition_controller++;


//   	} else {
//   		// Termina a excecucao quando chega no numero maximo de repeticoes;
//   		this.cancel();
//   	}

// }

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



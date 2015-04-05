package Animation;

import java.awt.*;
import java.awt.geom.*;
import java.util.Date; 
import java.util.ArrayList;
import Point.Point;
import java.util.Collections;


// TODO: Falta implementar as repeticoes (shiftar);
// TODO: Arrumar a identacao e refatorar o codigo;
// NOTE: Ta dando pau nas interpolacoes (debuggar);
// NOTE: O Bresenham aparentemente esta correto, porem aparece virado;
// COMPILE: javac Animation/Animation.java Main.java

/*	trabalho para a avaliação de computação gráfica
*	link : https://drive.google.com/drive/u/0/#folders/0B5YsmRIBzCHWT0wwWjFOc0pIdm8
*	data máxima da apresentação 13/04
*	@autor: Pablo Margreff
*	última atualização: 28/03
*/


public class Animation extends Frame {

  //Construtor
public Animation(  int r, int s, int n, int x, int y ) {

    this.radius      = r;
    this.segments    = s;
    this.repetitions = n;
    this.x_0         = x;  
    this.y_0         = y;  

    point_list = calculateBrasenham();

    addWindowListener(new MyFinishWindow());
    
  }

  // acho que essa classe ponto pode dar pau dps. Nao sei se precisa implementar o metodo tostring e 
  //tals pra usar com o arraylist 
  private ArrayList<Point> calculateBrasenham() {

      ArrayList<Point> left_half  = new ArrayList<Point>(); 
      ArrayList<Point> rigth_half = new ArrayList<Point>();

      int x = 0;
      int y = radius;
      int decision_var = 1 - radius;
      
      rigth_half.add( new Point(x , y  ) ); // eu acho que tem que ter ponto negativo.

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

      // NOTE: Aparece um "erro", ou seja alguns pontos retrocedem e nao sao coerentes;

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

//private double[] calculateTransformations(Point pf, int i, AffineTransform t , AffineTransform ro, AffineTransform sc) {
private void calculateTransformations(Point pi,Point pf, int i, AffineTransform t , AffineTransform ro, AffineTransform sc) {

  //double[] matriz = new double[6];

  AffineTransform tr = new AffineTransform();
  
  tr.setToTranslation(pf.get_x(), pf.get_y());      

  if ((i % 2) == 0) {
    
    // double rx = pf.get_x() * Math.cos(-45) - pf.get_y() * Math.sin(-45); 
    // double ry = pf.get_x() * Math.sin(-45) + pf.get_y() * Math.cos(-45); 

    // AffineTransform r = new AffineTransform();
    // r.rotate(rx, ry);
    AffineTransform origem = new AffineTransform();
    origem.setToTranslation(x_0,y_0);
    
    AffineTransform at = AffineTransform.getRotateInstance(-Math.PI / 4,pf.get_x() ,pf.get_y() );
    
      AffineTransform scaling = new AffineTransform();
      // scaling.setToScale(1.5,1.5);
    //sc.scale(1.5, 1.5);
    //ro.rotate(Math.toRadians(45));
    t.concatenate(origem);
    t.concatenate(at);
    t.concatenate(tr);
    
    t.scale(1.5,1.5);
    //t.concatenate(mudaTamanho(pf.get_x(),pf.get_x(),1.5,1.5));

    
  } else {

    //AffineTransform at = AffineTransform.getRotateInstance(0,pf.get_x() + 5,pf.get_y() + 5);
    //double rx = pf.get_x() * Math.cos(315) - pf.get_y() * Math.sin(315); 
    //double ry = pf.get_x() * Math.sin(315) + pf.get_y() * Math.cos(315); 
    AffineTransform scaling = new AffineTransform();
    scaling.setToScale(10.0/15.0,10.0/15.0);
    
    AffineTransform origem = new AffineTransform();
    origem.setToTranslation(x_0,y_0);
    
    //sc.scale(10f/15f, 10f/15f);
    //ro.rotate(Math.toRadians(315));
    //t.concatenate(sc);

    AffineTransform at = AffineTransform.getRotateInstance(0,pf.get_x() ,pf.get_y() );
    t.concatenate(origem);
    t.concatenate(at);
    t.concatenate(tr);

    //t.scale(10.0/15.0,10.0/15.0);
    //t.concatenate(mudaTamanho(pf.get_x(),pf.get_x(),10.0/15.0,10.0/15.0));        
  }

  //t.getMatrix(matriz);
  
  //return matriz;
}


private void calculateInterpolationPoints(Point pf, int jump, int i ,int r) {


    int size = point_list.size();

    if ( (i * jump  + jump) > size ) {
      pf.set_x(point_list.get(size).get_x() + r);
      pf.set_y(point_list.get(size).get_y());
       
    } else {
      pf.set_x(point_list.get((i * jump ) + jump ).get_x() + r);
      pf.set_y(point_list.get((i * jump ) + jump ).get_y()); 
    }

    System.out.format("(x %d ,y %d)\t%d\n", pf.get_x(), pf.get_y(), i);
}


// sobro pontos [note]
  public void debugg() {

    int i = 0;

    for (Point x : point_list) {
      System.out.format("(x,y) = (%d, %d) \t %d\n", x.get_x(), x.get_y(), i++ );
    }


    int jump = (point_list.size() / segments);

    System.out.format("Jump = %d\n", jump);


    int x_jump_inicial = 0;
    int y_jump_inicial = 0;

    int x_jump_final   = 0;
    int y_jump_final   = 0;

    for ( int r = 0; r < repetitions; r++ ) {

      for (int j = 0; j < segments; j++) {
        x_jump_inicial = point_list.get(j * (jump ) ).get_x();  
        y_jump_inicial = point_list.get(j * (jump ) ).get_y();

        if ( (j * jump  + jump) > point_list.size() ) {
          x_jump_final = point_list.get(point_list.size()).get_x();
          y_jump_final = point_list.get(point_list.size()).get_y(); 

        } else {
            x_jump_final = point_list.get((j * jump ) + jump ).get_x();  
            y_jump_final = point_list.get((j * jump ) + jump).get_y();  
        }
        
        System.out.format("(x_i = %d , y_i = %d) --> (x_f = %d , y_f = %d)\n", x_jump_inicial, y_jump_inicial, x_jump_final, y_jump_final);
      }      
    }
  }


// agora a giripoca vai pia

// Ok, vai se lavar. vou mijar e comer; voltamos ok; avisa no fb
public void paint(Graphics g) {

    Graphics2D g2d = (Graphics2D) g;
    //Ativa o antialiasing
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
  
    Point pi = new Point();
    Point pf = new Point();

    int jump = (point_list.size() / segments);


    pi.set_x(point_list.get(0).get_x());
    pi.set_y(point_list.get(0).get_y());

    Rectangle2D.Double quad = new Rectangle2D.Double(0, 0, 10 , 10);


    AffineTransform normalizer = new AffineTransform();
    normalizer.setToScale(1, -1);


    AffineTransform translate = new AffineTransform();
    translate.setToTranslation(0, y_0 + radius + 100);
    normalizer.preConcatenate(translate);

    g2d.transform(normalizer);


    //Shape usado para aplicar a transformação.
    Shape s;

    //double[] matrizFinal   = new double[6];
    double[] matrizInicial = new double[6];


    AffineTransform inicialTranformation = new AffineTransform();

    //objeto que irá receber todos os passos intermediários da transformação

    AffineTransform tracking = new AffineTransform();
    AffineTransform rotation = new AffineTransform();
    AffineTransform scaling  = new AffineTransform(); 

    AffineTransform  transfIntermed; 
    
    for ( int r = 0; r < repetitions; r++ ) {
      int shift = r * radius * 2;
    inicialTranformation.setToTranslation(x_0 + pi.get_x() + shift, y_0 + pi.get_y());
    inicialTranformation.getMatrix(matrizInicial);

      for (int j = 0; j < segments; j++) { 


        calculateInterpolationPoints(pf, jump, j, shift);
        calculateTransformations(pi,  pf, j, tracking, rotation, scaling);
       
        double [] matrizFinal = new double[6];
        tracking.getMatrix(matrizFinal);

        for (double step = 0; step < 10; step++ ) {
          transfIntermed = new AffineTransform( interpola( matrizInicial, matrizFinal , step/10) );
          s = transfIntermed.createTransformedShape(quad);
          congela(50);
          limpaJanela(g2d);
          g2d.draw(s);
        }

        tracking.setToIdentity();

        for (int i = 0; i < 6; i++ ) matrizInicial[i] = matrizFinal[i];
        
      }           
    }


}
  //calcula a interpolação
  // alfa sempre será estara no intervalo de [0,1]
  public double[] interpola(double[] inicial,double[] terminal, double alfa) {
    double[] intermed = new double[inicial.length]; //matriz que vai conter os valores váriaveis da interpolação

    for (int i=0; i<intermed.length; i++)
    {
      intermed[i] = (1-alfa)*inicial[i] + alfa*terminal[i];
    }

    return(intermed);
  }
  //método que limpa a janela
  public void limpaJanela(Graphics2D g)
  {
    g.setPaint(Color.white); //define o pincel como branco
    g.fill(new Rectangle(0,0,radius * 2 * repetitions + 100 + x_0,radius + y_0 + 100)); //cria um retangulo preenchido da primeira cor
    g.setPaint(Color.black); //faz o pincel ser preto novamente
  }


  //recebe como parametros as dimenções do objeto original e as novas 
  //escalas em x e y que cada um deve receber
  public AffineTransform mudaTamanho(int x, int y,
                                             double novaEscalaX, double novaEscalaY)
  {
    AffineTransform mt = new AffineTransform();

    //mt.translate(x_0,y_0);
    mt.scale(novaEscalaX,novaEscalaY);
    //mt.translate(x,y); //perguntar para Marilton por que a translação está negativada !!!!

    return(mt);
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


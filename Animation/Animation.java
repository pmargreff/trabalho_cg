package Animation;

import java.awt.*;
import java.awt.geom.*;
import java.util.Date; 
import java.util.ArrayList;
import Point.Point;



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
//public class Animation {

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

private double[] calculateTransformations(Point pi, Point pf, int i) {


  double[] matriz = new double[6];

  AffineTransform tranformation = new AffineTransform();
  tranformation.setToTranslation(pf.get_x(), pf.get_y());

  int turn = i % 2;

  double mx, my;
  
  if (turn != 0) {
    mx = (double)pi.get_x() + 5.0;
    my = (double)pi.get_y() + 5.0;
    tranformation.concatenate( mudaTamanho(mx, my, 1.5, 1.5) );
    tranformation.rotate( Math.toRadians(-45), mx, my );
  } else {
    mx = (double)pi.get_x() + 7.5;
    my = (double)pi.get_y() + 7.5;
    tranformation.concatenate( mudaTamanho(mx, my, 0.666, 0.666) );
    tranformation.rotate( Math.toRadians(45), mx, my);
  }

  tranformation.getMatrix(matriz);


  return matriz;

}


private void calculateInterpolationPoints(Point pi, Point pf, int jump, int i ) {

    int size = point_list.size();

    pi.set_x(point_list.get( i * jump ).get_x());
    pi.set_y(point_list.get( i * jump ).get_y());

    if ( (i * jump  + jump) > size ) {
      pf.set_x(point_list.get(size).get_x());
      pf.set_y(point_list.get(size).get_y());
       
    } else {
      pf.set_x(point_list.get((i * jump ) + jump ).get_x());
      pf.set_y(point_list.get((i * jump ) + jump ).get_x()); 
    }
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
 
    int coordIniX, coordIniY;
    coordIniX = x_0 - radius;       //coordenada inicial no X
    coordIniY = y_0;                //coordenada inicial do Y
    //cria a elipse de acordo com as coordenadas iniciais e tamanho

    Rectangle2D.Double quad = new Rectangle2D.Double(coordIniX, coordIniY,10 , 10);

// ---------------------------------------------------------------------
    double xMed = coordIniX + 5.0;
    double yMed = coordIniY + 5.0;

    Point pi = new Point();
    Point pf = new Point();

    int jump = (point_list.size() / segments);

    //Shape usado para aplicar a transformação.
    Shape s;

    double[] matrizFinal   = new double[6];
    double[] matrizInicial = new double[6];

    AffineTransform inicialTranformation = new AffineTransform();
    inicialTranformation.setToRotation(0, xMed, yMed);

    inicialTranformation.getMatrix(matrizInicial);

    // --- PARAMOS AQUI TENTANDO RESOLVER OS PROBLEMAS DA INTERPOLACAO ---- 
    //objeto que irá receber todos os passos intermediários da transformação
    AffineTransform transfIntermed;

    /*
    for ( int r = 0; r < repetitions; r++ ) {

      for (int j = 0; j < segments; j++) {

        calculateInterpolationPoints(pi, pf, jump, j);
        matrizFinal = calculateTransformations(pi, pf, j);

        
        for (double step = 0; step < 200; step++ ) {
           //calcula a etapa da interpolação
             
          transfIntermed = new AffineTransform( interpola(matrizInicial, matrizFinal , step/200) );
          s = transfIntermed.createTransformedShape(quad);
          congela(50);
          limpaJanela(g2d);
          g2d.fill(s);
        }

        matrizInicial = matrizFinal; // sera que vai funcionar ?
      }           
    }

    */

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
    g.fill(new Rectangle(0,0,600,300)); //cria um retangulo preenchido da primeira cor
    g.setPaint(Color.black); //faz o pincel ser preto novamente
  }


  //recebe como parametros as dimenções do objeto original e as novas 
  //escalas em x e y que cada um deve receber
  public AffineTransform mudaTamanho(double x, double y,
                                             double novaEscalaX, double novaEscalaY)
  {
    AffineTransform mt = new AffineTransform();

    mt.translate(x,y);
    mt.scale(novaEscalaX,novaEscalaY);
    mt.translate(-x,-y); //perguntar para Marilton por que a translação está negativada !!!!

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


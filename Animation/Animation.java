package Animation;

import java.awt.*;
import java.awt.geom.*;
import java.util.Date; 
import java.util.ArrayList;

import Point.Point; // eu odeio java...


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

  public void debugg() {

    int i = 0;

    for (Point x : point_list) {
      System.out.format("(x,y) = (%d, %d) \t %d\n", x.get_x(), x.get_y(), i++ );
    }

  }


// agora a giripoca vai pia

// Ok, vai se lavar. vou mijar e comer; voltamos ok; avisa no fb
public void paint(Graphics g) {

    Graphics2D g2d = (Graphics2D) g;
    //Ativa o antialiasing
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);


    // ver deposi ...
    int coordIniX, coordIniY, tamanhoFinal;
  

    coordIniX = x_0 - radius;       //coordenada inicial no X
    coordIniY = y_0;                //coordenada inicial do Y
    //cria a elipse de acordo com as coordenadas iniciais e tamanho
    Rectangle2D.Double quad = new Rectangle2D.Double(coordIniX,coordIniY,10 , 10);


    //Ponto médio dos lados do quadrado
    //é usado para definir o ponto central para a rotação
    // ver depois


    //Cria a rotação inical 0 graus a partir do ponto central.
    //AffineTransform transfInicial = new AffineTransform();
    //transfInicial.setToRotation(0,xMed,yMed); //rotação inicial



    //objetofinal
    // AffineTransform transfFinal = new AffineTransform();
    // transfFinal.setToTranslation(0,0); //posicao final, será um vetor
    // transfFinal.concatenate(mudaTamanho(xMed,yMed,1.5,1.5));//faz a mudança da escaĺa
    // transfFinal.rotate(Math.PI/4,xMed,yMed); //faz a rotação do objeto




   // double passos = 200; //Number of passos as a Double-value in order 
                                //to avoid repeated casting in the loop.


// ---------------------------------------------------------------------


    double xMed = coordIniX + 5;
    double yMed = coordIniY + 5;

    int jump = (point_list.size() / segments);


    //Shape usado para aplicar a transformação.
    Shape s;


    //objeto que irá receber todos os passos intermediários da transformação
    AffineTransform transfIntermed;

    for ( int r = 0; r < repetitions; r++ ) {

      for (int j = 0; j < segments; j++) {


      int x_jump = point_list.get(j * jump).get_x();  
      int y_jump = point_list.get(j * jump).get_y();  

      AffineTransform rotation  = new AffineTransform(); // realmente preciso iniciar aqui?
      AffineTransform translate = new AffineTransform();
      translate.setToTranslation(x_jump , y_jump);

      if ( (j % 2) == 0 )  {
        rotation.setToRotation( Math.toRadians(45), xMed, yMed );
        translate.concatenate( mudaTamanho(x_jump, y_jump, 1.5, 1.5) );
      } else {
        translate.setToRotation(Math.toRadians(-45), xMed, yMed );
        translate.concatenate( mudaTamanho(x_jump, y_jump, 0.6666, 0.6666) );
      } 


       //Cria e o objeto inicial
      double[] matrizInicial = new double[6];
      rotation.getMatrix(matrizInicial);

      //matriz para a transformação final
      double[] matrizFinal = new double[6];
      translate.getMatrix(matrizFinal);

      for (double step = 0; step < 10; step++ ) {
         //calcula a etapa da interpolação
        transfIntermed = new AffineTransform( interpola(matrizInicial, matrizFinal , step/200) );

        //transforma a o quadrado para o tipo shape, para poder aplicar as operações nessárias
        s = transfIntermed.createTransformedShape(quad);

        //delay para redesenhar
        congela(50);

        limpaJanela(g2d);

        //redesenha
        g2d.fill(s);
      }

      // talvez seja dentro do if para a atual transf;  
      xMed = x_jump + 5;  
      yMed = y_jump + 5;  

      //point_list.get(j * jump).get_x(), point_list.get(j * jump).get_x()  

      }
        
    }



// ---------------------------------------------------------------------

    // for (double i=0; i<=passos; i++)
    // {
    //   //calcula a etapa da interpolação
    //   transfIntermed = new AffineTransform(
    //               interpola(matrizInicial,matrizFinal,i/passos));

    //   //transforma a o quadrado para o tipo shape, para poder aplicar as operações nessárias
    //   s = transfIntermed.createTransformedShape(quad);

    //   //delay para redesenhar
    //   congela(50);

    //   limpaJanela(g2d);

    //   //redesenha
    //   g2d.fill(s);
    // }
  }


  //calcula a interpolação
  // alfa sempre será estara no intervalo de [0,1]
  public double[] interpola(double[] inicial,double[] terminal, double alfa)
  {
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


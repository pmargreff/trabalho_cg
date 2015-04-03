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


//public class Animation extends Frame
public class Animation {

  //Construtor
  public Animation(  int r, int s, int n  ) {

    this.radius      = r;
    this.segments    = s;
    this.repetitions = n;

    point_list = calculateBrasenham();

    //addWindowListener(new MyFinishWindow());
    
  }


  // acho que essa classe ponto pode dar pau dps. Nao sei se precisa implementar o metodo tostring e 
  //tals pra usar com o arraylist 
  private ArrayList<Point> calculateBrasenham() {

      ArrayList<Point> left_half  = new ArrayList<Point>(); 
      ArrayList<Point> rigth_half = new ArrayList<Point>();

      int x = 0;
      int y = radius;
      int decision_var = 1 - radius;
      
      rigth_half.add( new Point(x, y) ); // eu acho que tem que ter ponto negativo.

      while ( y > x ) {

        x++;

        if (decision_var < 0) {
          decision_var += 2 * x + 3;
        } else {

          decision_var += 2 * ( x - y ) + 5;
          y--;
        }

        rigth_half.add( new Point(x , y ) );  // entao ..      
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


      return final_list;

  }

  public void debugg() {

    int i = 0;

    for (Point x : point_list) {
      System.out.format("(x,y) = (%d, %d) \t %d\n", x.get_x(), x.get_y(), i++ );
    }

  }

  // public void paint(Graphics g)
  // {

  //   Graphics2D g2d = (Graphics2D) g;
  //   //Ativa o antialiasing
  //   g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);


  //   int coordIniX, coordIniY, tamanhoQuad, tamanhoInicial, tamanhoFinal;
  //   tamanhoInicial = 10;

  //   coordIniX = 300;     //coordenada inicial no X
  //   coordIniY = 150;     //coordenada inicial do Y
  //   tamanhoQuad = tamanhoInicial;  //tamanho de quadrado recebe o tamanho inicial
  //   //cria a elipse de acordo com as coordenadas iniciais e tamanho
  //   Rectangle2D.Double quad = new Rectangle2D.Double(coordIniX,coordIniY,tamanhoQuad,tamanhoQuad);


  //   //Ponto médio dos lados do quadrado
  //   //é usado para definir o ponto central para a rotação
  //   double xMed = coordIniX+tamanhoQuad/2.0;
  //   double yMed = coordIniY+tamanhoQuad/2.0;


  //   //Cria a rotação inical 0 graus a partir do ponto central.
  //   AffineTransform transfInicial = new AffineTransform();
  //   transfInicial.setToRotation(0,xMed,yMed); //rotação inicial

  //   //Cria e o objeto inicial
  //   double[] matrizInicial = new double[6];
  //   transfInicial.getMatrix(matrizInicial);


  //   //objetofinal
  //   AffineTransform transfFinal = new AffineTransform();
  //   transfFinal.setToTranslation(0,0); //posicao final, será um vetor
  //   transfFinal.concatenate(mudaTamanho(xMed,yMed,1.5,1.5));//faz a mudança da escaĺa
  //   transfFinal.rotate(Math.PI/4,xMed,yMed); //faz a rotação do objeto

  //   //matriz para a transformação final
  //   double[] matrizFinal = new double[6];
  //   transfFinal.getMatrix(matrizFinal);


  //   //Shape usado para aplicar a transformação.
  //   Shape s;


  //   //objeto que irá receber todos os passos intermediários da transformação
  //   AffineTransform transfIntermed;

  //   double passos = 200; //Number of passos as a Double-value in order 
  //                               //to avoid repeated casting in the loop.
  //   for (double i=0; i<=passos; i++)
  //   {
  //     //calcula a etapa da interpolação
  //     transfIntermed = new AffineTransform(
  //                 interpola(matrizInicial,matrizFinal,i/passos));

  //     //transforma a o quadrado para o tipo shape, para poder aplicar as operações nessárias
  //     s = transfIntermed.createTransformedShape(quad);

  //     //delay para redesenhar
  //     congela(50);

  //     limpaJanela(g2d);

  //     //redesenha
  //     g2d.fill(s);
  //   }
  // }


  //calcula a interpolação
  // alfa sempre será estara no intervalo de [0,1]
  public static double[] interpola(double[] inicial,double[] terminal, double alfa)
  {
    double[] intermed = new double[inicial.length]; //matriz que vai conter os valores váriaveis da interpolação

    for (int i=0; i<intermed.length; i++)
    {
      intermed[i] = (1-alfa)*inicial[i] + alfa*terminal[i];
    }

    return(intermed);
  }


  //método que limpa a janela
  public static void limpaJanela(Graphics2D g)
  {
    g.setPaint(Color.white); //define o pincel como branco
    g.fill(new Rectangle(0,0,600,300)); //cria um retangulo preenchido da primeira cor
    g.setPaint(Color.black); //faz o pincel ser preto novamente
  }


  //recebe como parametros as dimenções do objeto original e as novas 
  //escalas em x e y que cada um deve receber
  public static AffineTransform mudaTamanho(double x, double y,
                                             double novaEscalaX, double novaEscalaY)
  {
    AffineTransform mt = new AffineTransform();

    mt.translate(x,y);
    mt.scale(novaEscalaX,novaEscalaY);
    mt.translate(-x,-y); //perguntar para Marilton por que a translação está negativada !!!!

    return(mt);
  }

  //recebe o tempo em milissegundos
  public static void congela(long t)
  {
    long finish = (new Date()).getTime() + t;
    while( (new Date()).getTime() < finish ){}
  }


  private int radius;     
  private int segments;   
  private int repetitions;
  private ArrayList<Point> point_list;

  // public static void main(String[] argv)
  // {
  //   Animacao tela = new Animacao();
  //   tela.setTitle("Animation do quadrado mudando de forma por interpolacao de pontos");
  //   tela.setSize(600,300);
  //   tela.setVisible(true);
  // }

}


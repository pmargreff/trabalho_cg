import java.awt.*;
import java.awt.geom;
import java.util.Date;

/*	trabalho para a avaliação de computação gráfica
*	link : https://drive.google.com/drive/u/0/#folders/0B5YsmRIBzCHWT0wwWjFOc0pIdm8
*	data máxima da apresentação 13/04
*	@autor: Pablo Margreff
*	última atualização: 28/03
*/

public class Animacao extends Frame {

	private int alturaJanela;
	private int larguraJanela;
	private int diametro; //diametro do circulo
	
	Animacao(int raio){
		addWindowListener(new MyFinishWindow());

		alturaJanela = raio;
		larguraJanela = (raio * 2);
		diametro = (raio * 2);
	}

	public void paint(Graphics g) {
		Graphics2D tela = (Graphics2D) g;

		int[] vetorCirculo = new int[diametro];
		vetorCirculo = defineCirculo(raio);



	}

	public void defineCirculo(int raio){
		int[] vetorCirculo = new int[diametro];
		int p; //variável usada para fazer o calculo referente à linha traçada
		int rep; //o número de repetições necessárias para desenhar um circulo
		rep = raio / 2;
		for(int i = 0; i < rep; i++){
			

		}

	}

	public void limparTela(Graphics2D g){
		g.setPaint(Color.white); //cor do fundo da janela
		g.fill(new Rectangle(0,0, larguraJanela, alturaJanela));
		g.setPaint(Color.black); //cor da borda
	}
}
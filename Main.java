import Animation.*;

public class Main {

	public static void main(String[] argv) {

		if ( argv.length == 5 ) {

			int radius 	 	= Integer.parseInt(argv[0]);
			int segments 	= Integer.parseInt(argv[1]);
			int repetitions = Integer.parseInt(argv[2]);
			int x_center	= Integer.parseInt(argv[3]);
			int y_center	= Integer.parseInt(argv[4]);

			int width  = radius * 2 * repetitions + 100 + x_center;
			int height = radius + y_center + 100;
			
			x_center = x_center - radius;

			if ( segments < 2 ) segments = 2;
			if ( repetitions < 1 ) repetitions = 1;
			if ( radius < 2 ) radius = 2;
			
			Animation scene = new Animation(radius, segments, repetitions, x_center, y_center);

	    	scene.setTitle("CG Trabalho I - Bruno e Pablo");
	    	scene.setSize(width, height);
	   		scene.setVisible(true);

 
		} else {

			System.out.println("Parametros invalidos.\nDigite: java Main < raio > < segmentos > < repeticoes > < x_0 >  < y_0 >\n");
		}

  }
}
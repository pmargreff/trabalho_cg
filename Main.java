import Animation.*;

public class Main {

	public static void main(String[] argv) {

		if ( argv.length == 5 ) {

			int radius 	 	= Integer.parseInt(argv[0]);
			int segments 	= Integer.parseInt(argv[1]);
			int repetitions = Integer.parseInt(argv[2]);
			int x_0			= Integer.parseInt(argv[3]);
			int y_0			= Integer.parseInt(argv[4]);


			if ( segments < 2 ) segments = 2;

			Animation scene = new Animation( radius, segments, repetitions, x_0, y_0 );

			//scene.debugg();
	    	scene.setTitle("CG Trabalho I - Bruno e Pablo");
	    	scene.setSize( 600, 300);
	   		scene.setVisible(true);

 
		} else {

			System.out.println("Erro com parametros"); // todo: arrumar msg
		}

  }
}
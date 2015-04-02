import Animation.*;

public class Main {

	public static void main(String[] argv) {

		if ( argv.length == 4 ) {

			int radius 	 	= Integer.parseInt(argv[1]);
			int segments 	= Integer.parseInt(argv[2]);
			int repetitions = Integer.parseInt(argv[3]);

			if ( segments < 2 ) segments = 2;

			Animation scene = new Animation( radius, segments, repetitions );
	    	scene.setTitle("CG Trabalho I - Bruno e Pablo");
	    	scene.setSize(600,300);
	   		scene.setVisible(true);

 
		} else {

			System.out.println("Passagem invalida de parametros.");
		}


		// crtl + /      --> serve pra co/descomentar

	    
  }
}
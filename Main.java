import Animation.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import Point.Point;
import java.util.Collections;
import java.awt.image.*;


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
			


			Bresenham semiCircle = new Bresenham(radius, segments);

			segments = semiCircle.getNormalizedSegment();

			CCanvas cc = new CCanvas(new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB), width, height);


			AffineTransform tracker = new AffineTransform();

			Flag turn = new Flag(true);

			Point origin = new Point(x_center, y_center);


			// Animation <- Canvas, tracker (tranformation), Flag, delay, Origin Point (center), Point to go;

			int delay = 50;

			for (int r = 0; r < repetitions; r++ ) {
				int shift = r * radius * 2;
				Timer taskRunner = new Timer();
				for (int j = 0; j < segments; j++) {
					taskRunner.scheduleAtFixedRate(new Animation(cc, tracker, turn, origin, semiCircle.getNextSegmentPoint(j, shift)), 0, delay);		// talvez tenha que definir um delay
				}
			}

			//

	   		/*
				Constroi o canvas primeiro

				// animacao	
				for (repetitions)
    			for (segments)
    				Timer t = new Timer();
    				// passa repetitions e calcular shitfts (constroi a animacao para uma repeticao detarminada por 'repetition' e escalona)
			    	t.scheduleAtFixedRate(new DoubleBufferingClockExample(bid, backGround, height, delay),0,delay); 

	   		*/


 
		} else {

			System.out.println("Parametros invalidos.\nDigite: java Main < raio > < segmentos > < repeticoes > < x_0 >  < y_0 >\n");
		}

  }
}
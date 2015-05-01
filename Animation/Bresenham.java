package Animation;

import java.util.ArrayList;
import Point.Point;
import java.util.Collections;



public class Bresenham {

	public Bresenham (int r, int s) {

		this.radius     = r;
		this.segments   = s;
		this.point_list = calculateBrasenham();
	}

	public Point getNextSegmentPoint(int i ,int r ) {

		Point pf = new Point();

	    int size = point_list.size() - 1;

	    if ( (i * this.jump  + this.jump) > size ) {
	      pf.set_x(point_list.get(size).get_x() + r);
	      pf.set_y(point_list.get(size).get_y());
	       
	    } else {
	      pf.set_x(point_list.get((i * this.jump ) + this.jump ).get_x() + r);
	      pf.set_y(point_list.get((i * this.jump ) + this.jump ).get_y()); 
	    }

	    return pf;
	}



	public int getNormalizedSegment() {
			
		double size = (double)point_list.size();
    	double jump_delta = size / segments;

    	double remainder = size % segments;
    	double jump_alfa = (int)(remainder/jump_delta);

    	this.jump = (int) jump_delta;

    	this.segments = this.segments + (int)jump_alfa;

	   	return this.segments;		    
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
          final_list.get(i).set_x(tmp.get_x() + radius); //  todo: descobrir pq soma raio aqui;
          final_list.get(i).set_y(tmp.get_y() );
      }


      return final_list;

  }



  private int radius;     
  private int segments;
  private int jump;
  private ArrayList<Point> point_list;

}
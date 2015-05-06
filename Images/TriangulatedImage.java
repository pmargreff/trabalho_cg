import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;



/**
* This class is for defining triangulated images and for interpolation 
* between two triangulated images with corresponding triangulations.
* 
* @author Frank Klawonn
* Last change 31.05.2005
* 
* @see MorphingCandS
*/
public class TriangulatedImage
{

  //The initial image.
  BufferedImage bi;

  //The points needed for the triangulation. 
  Point2D[] tPoints;

  //The triangles defining the triangulation. Each row of the arrays contains
  //indices of three points in the array tPoints. The i-th triangle of the
  //triangulation is defined by the three points in array tPoints with the indices
  //triangles[i][0], triangles[i][1], triangles[i][2].
  int[][] triangles;



  /**
  * A method that computes for a point v and three additional noncollinear points 
  * a representation of v as a linear combination of the three points. The three coefficents
  * for the representation of v are returned.
  * 
  * @param v          point to be represented.
  * @param triangle   an array contain three noncollinear points.
  * 
  * @return           the three coefficients for the representation of v.
  */
  public static double[] triangleCoordinates(Point2D v, Point2D[] triangle)
  {
    //The array for the three coefficients.
  	double[] result = new double[3];

  	//Some values that are needed to solve the system of linear equations for
  	//determining the coefficients.
    double d13x = triangle[0].getX() - triangle[2].getX();
    double d23x = triangle[1].getX() - triangle[2].getX();
    double dx3 =  v.getX()           - triangle[2].getX();
    double d13y = triangle[0].getY() - triangle[2].getY();
    double d23y = triangle[1].getY() - triangle[2].getY();
    double dy3 =  v.getY()           - triangle[2].getY();

    double delta = d13x*d23y - d23x*d13y;

    //When the three points are (almost) collinear, division by zero is avoided and
    //coefficients are returned which at least do not represent a convex combinaion.
    //Otherwise the coefficients cannot be computed.
    if (Math.abs(delta)<0.00000001)
    {
      result[0] = 10;
    }
    else
    {
      result[0] = (dx3*d23y - d23x*dy3)/delta;
      result[1] = (d13x*dy3 - dx3*d13y)/delta;
    }


    result[2] = 1 - result[0] - result[1];

    return(result);
  }



  /**
  * Test, whether a given array of coefficienten represents a convex combination,
  * i.e. it is checked, whether all elements in the array are between 0 and 1 
  * and whether they sum up to 1.
  * 
  * @param t          array with the coefficients.
  * 
  * @return           true if and only if the coefficients represent a convex combination.
  */
  public static boolean isConvexCombination(double[] t)
  {
    boolean result;
    double sum;
    //According to round-off errors, the coefficients will almost never sum up to 1 exactly.
    //The following value specifies how much deviation from the exact value 1 is still considered
    //as a convex combination.
    double tolerance = 0.000001;

    result = true;
    sum = 0;
    for (int i=0; i<t.length; i++)
    {
      if (t[i]<0 || t[i]>1) {result = false;}
      sum = sum + t[i];
    }
    
    if (Math.abs(sum-1)>tolerance) {result = false;}
    
    return(result);
  }



  
  /**
  * Generates and interpolated image from two triangulated images (this and ti).
  * ti contribued to the interpolated image by alpha*100%. The two images must have the 
  * same size, the same number of points for the triangulation and the number of triangles.
  * Furthermore, the triangles for both images must consist of the points with the same 
  * indices. Only the position of the points does need to coincide for the two images.
  * 
  * @param ti         second triangulated image.
  * @param alpha      proportion that ti should contribute to the interpolated image.
  * 
  * @return           the interpolated image.
  */
  public BufferedImage mixWith(TriangulatedImage ti, double alpha)
  {
    TriangulatedImage mix = new TriangulatedImage();

    //Here the interpolated image will be stored.
    mix.bi = new BufferedImage(this.bi.getWidth(),this.bi.getHeight(),
                                            BufferedImage.TYPE_INT_RGB);

    
    //Define the points for the triangulation points in the interpolated image as 
    //convex combinations of the corresponding points in the images to be  
    //interpolated.
    mix.tPoints = new Point2D[this.tPoints.length];
    for (int i=0; i<mix.tPoints.length; i++)
    {
      mix.tPoints[i] = new Point2D.Double((1-alpha)*this.tPoints[i].getX()
                                          + alpha*ti.tPoints[i].getX(),
                                          (1-alpha)*this.tPoints[i].getY()
                                          + alpha*ti.tPoints[i].getY());
    }

    //The triangles for the triangulation must be defined using the same points (indices) 
    //as in the original images.
    mix.triangles = this.triangles;



    int rgbValueThis;
    int rgbValueTi;
    int rgbValueMix;
    Color thisColour;
    Color tiColour;
    Color pixelColour;
    int rMix;
    int gMix;
    int bMix;
    int xInt;
    int yInt;
    double[] t = new double[3];
    double aux;
    Point2D[] threePoints = new Point2D[3];;
    Point2D.Double pixel = new Point2D.Double();
    int tNo;
    boolean notFound;

    
    //Determine for each pixel in the interpolated image the colour as a 
    //convex combination of the RGB-values of the corresponding pixels in the
    //two images to be interpolated.
    for (int i=0; i<mix.bi.getWidth(); i++)
    {
      for (int j=0; j<mix.bi.getHeight(); j++)
      {
        //For this pixel the colour must be determined.
        pixel.setLocation(i,j);

        //tNo is used to compute the index of the triangle in which the 
        //considered pixel lies.
        tNo = 0;

        //When the triangle has been found, notFound changes to false.
        notFound = true;
        while(tNo<mix.triangles.length && notFound)
        {
        	
          //Determine the three points of the triangle no. tNo.
          for(int k=0; k<3; k++)
          {
            threePoints[k] = mix.tPoints[mix.triangles[tNo][k]];
          }

          //Determine the coordinates of the pixels w.r.t. to the triangle no. tNo.
          t = triangleCoordinates(pixel,threePoints);

          //Check whether the pixel lies inside the triangle.
          if (isConvexCombination(t))
          {
            notFound = false;
          }
          else
          {
            tNo++;
          }
        }//endWhile
        //The while-loop terminates when an index tNo of a triangle containing the
        //pixel has been found or when no triangle containing the pixel has been found.
        
        
        //If a triangle containing the pixel has been found, the colour for the pixel
        //can be computed.
        if (!notFound)
        {
          //Determine the x-coordinate of the pixel in the first image (this) as 
          //a convex combination of the vertices of the corresponding triangle.
          aux = 0;
          for (int k=0; k<3; k++)
          {
            aux = aux + t[k]*this.tPoints[this.triangles[tNo][k]].getX();
          }
          xInt = (int) Math.round(aux);
          
          //We have to make sure that round-off errors will not lead to a pixel
          //outside the image.
          xInt = Math.max(0,xInt);
          xInt = Math.min(this.bi.getWidth()-1,xInt);


          //The same for the y-coordinate.
          aux = 0;
          for (int k=0; k<3; k++)
          {
            aux = aux + t[k]*this.tPoints[this.triangles[tNo][k]].getY();
          }
          yInt = (int) Math.round(aux);
          yInt = Math.max(0,yInt);
          yInt = Math.min(this.bi.getHeight()-1,yInt);


          //Determine the colour of the pixel in the first image.
          rgbValueThis = this.bi.getRGB(xInt,yInt);
          thisColour = new Color(rgbValueThis);



          //Do the same as above for the second image ti.
          aux = 0;
          for (int k=0; k<3; k++)
          {
            aux = aux + t[k]*ti.tPoints[ti.triangles[tNo][k]].getX();
          }
          xInt = (int) Math.round(aux);

          aux = 0;
          for (int k=0; k<3; k++)
          {
            aux = aux + t[k]*ti.tPoints[ti.triangles[tNo][k]].getY();
          }
          yInt = (int) Math.round(aux);

          rgbValueTi = ti.bi.getRGB(xInt,yInt);
          tiColour = new Color(rgbValueTi);



          //Now that we have the two colours, we can compute their
          //convex combimation for the R-,G- and B-value.
          //Problems caused by round-off errors are quite unlikely here.
          rMix = (int) Math.round((1-alpha)*thisColour.getRed()+
                                        alpha*tiColour.getRed());

          gMix = (int) Math.round((1-alpha)*thisColour.getGreen()+
                                        alpha*tiColour.getGreen());

          bMix = (int) Math.round((1-alpha)*thisColour.getBlue()+
                                        alpha*tiColour.getBlue());


          //Generate the interpolated colour as a convex combination.
          pixelColour = new Color(rMix, gMix, bMix);

          //Use the interpolated colour to draw the pixel in the interpolated image
          mix.bi.setRGB(i,j,pixelColour.getRGB());

        }//endif (!notFound)

      }//endfor j
    }//endfor i



    return(mix.bi);
  }



}


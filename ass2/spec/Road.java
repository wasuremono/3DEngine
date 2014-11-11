package ass2.spec;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

/**
 * COMMENT: Comment Road 
 *
 * @author malcolmr
 */
public class Road {
	 private static final double SEGMENT_COUNT = 100;
    private List<Double> myPoints;
    private double myWidth;
    
    /** 
     * Create a new road starting at the specified point
     */
    public Road(double width, double x0, double y0) {
        myWidth = width;
        myPoints = new ArrayList<Double>();
        myPoints.add(x0);
        myPoints.add(y0);
    }

    /**
     * Create a new road with the specified spine 
     *
     * @param width
     * @param spine
     */
    public Road(double width, double[] spine) {
        myWidth = width;
        myPoints = new ArrayList<Double>();
        for (int i = 0; i < spine.length; i++) {
            myPoints.add(spine[i]);
        }
    }

    /**
     * The width of the road.
     * 
     * @return
     */
    public double width() {
        return myWidth;
    }

    /**
     * Add a new segment of road, beginning at the last point added and ending at (x3, y3).
     * (x1, y1) and (x2, y2) are interpolated as bezier control points.
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     */
    public void addSegment(double x1, double y1, double x2, double y2, double x3, double y3) {
        myPoints.add(x1);
        myPoints.add(y1);
        myPoints.add(x2);
        myPoints.add(y2);
        myPoints.add(x3);
        myPoints.add(y3);        
    }
    
    /**
     * Get the number of segments in the curve
     * 
     * @return
     */
    public int size() {
        return myPoints.size() / 6;
    }

    /**
     * Get the specified control point.
     * 
     * @param i
     * @return
     */
    public double[] controlPoint(int i) {
        double[] p = new double[2];
        p[0] = myPoints.get(i*2);
        p[1] = myPoints.get(i*2+1);
        return p;
    }
    
    /**
     * Get a point on the spine. The parameter t may vary from 0 to size().
     * Points on the kth segment take have parameters in the range (k, k+1).
     * 
     * @param t
     * @return
     */
    public double[] point(double t) {
        int i = (int)Math.floor(t);
        t = t - i;
        
        i *= 6;
        
        double x0 = myPoints.get(i++);
        double y0 = myPoints.get(i++);
        double x1 = myPoints.get(i++);
        double y1 = myPoints.get(i++);
        double x2 = myPoints.get(i++);
        double y2 = myPoints.get(i++);
        double x3 = myPoints.get(i++);
        double y3 = myPoints.get(i++);
        
        double[] p = new double[2];

        p[0] = b(0, t) * x0 + b(1, t) * x1 + b(2, t) * x2 + b(3, t) * x3;
        p[1] = b(0, t) * y0 + b(1, t) * y1 + b(2, t) * y2 + b(3, t) * y3;        
        
        return p;
    }
    public double[] norm(double t){
        int i = (int)Math.floor(t);
        t = t - i;
        
        i *= 6;
        
        double x0 = myPoints.get(i++);
        double y0 = myPoints.get(i++);
        double x1 = myPoints.get(i++);
        double y1 = myPoints.get(i++);
        double x2 = myPoints.get(i++);
        double y2 = myPoints.get(i++);
        double x3 = myPoints.get(i++);
        double y3 = myPoints.get(i++);
        
        double[] p = new double[2];
        
        p[1] = db(0, t) * x0 + db(1, t) * x1 + db(2, t) * x2 + db(3, t) * x3;
        p[0] = db(0, t) * y0 + db(1, t) * y1 + db(2, t) * y2 + db(3, t) * y3;
        double d = Math.sqrt(p[1] * p[1] + p[0] * p[0]);
        p[1] = (p[1] / d);
        p[0] = (p[0] /d);
		return p;
    }
    
    /**
     * Calculate the Bezier coefficients
     * 
     * @param i
     * @param t
     * @return
     */
    private double b(int i, double t) {
        
        switch(i) {
        
        case 0:
            return (1-t) * (1-t) * (1-t);

        case 1:
            return 3 * (1-t) * (1-t) * t;
            
        case 2:
            return 3 * (1-t) * t * t;

        case 3:
            return t * t * t;
        }
        
        // this should never happen
        throw new IllegalArgumentException("" + i);
    }
    private double db(int i, double t) {
        
        switch(i) {
        
        case 0:
            return -3 * (1-t) * (1-t);

        case 1:
            return (-6 * (1-t) * t )+ (3 * (1-t) * (1-t));
            
        case 2:
            return (6 * (1-t) * t) - ( 3 * t * t);

        case 3:
            return 3 * t * t;
        }
        
        // this should never happen
        throw new IllegalArgumentException("" + i);
    }
    public void draw(GL2 gl) {
    	gl.glPushMatrix();
    	//Points are points along the parametrised spine
    	double[] p0 = point(0);
    	//Normal to point on spine
    	double[] n0 = norm(0);
    	gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
    	gl.glBindTexture(GL.GL_TEXTURE_2D, 5);
    	gl.glBegin(GL2.GL_QUAD_STRIP);     	
    	double w = (this.width()/2);
    	//taking Points at normal on either side of the spine we extrude a mesh for road
    	gl.glNormal3d(0,0,1);
   		gl.glTexCoord2d(1, 1);
		gl.glVertex3d(p0[0] - (n0[0]*w),Terrain.altitude(p0[0],p0[1])+0.01,p0[1] + (n0[1]*w));
		gl.glTexCoord2d(0, 1);
		gl.glVertex3d(p0[0] + (n0[0]*w),Terrain.altitude(p0[0],p0[1])+0.01,p0[1] - (n0[1]*w));
    	for(int i = 1;(double) (i/(SEGMENT_COUNT))  < this.size(); i++)
    	{
    		double t = (double)(i/(SEGMENT_COUNT));
    		double[] p1 = point(t);
    		double[] n1 = norm(t);   
    		gl.glTexCoord2d(1, 1 - i/(SEGMENT_COUNT));
    		gl.glVertex3d(p1[0] - (n1[0]*w),Terrain.altitude(p1[0],p1[1])+0.01,p1[1] + (n1[1]*w));
    		gl.glTexCoord2d(0,1 - i/(SEGMENT_COUNT));
    		gl.glVertex3d(p1[0] + (n1[0]*w),Terrain.altitude(p1[0],p1[1])+0.01,p1[1] - (n1[1]*w));
    		p0 = p1;
    		n0 = n1;
    	
    	}   
    	p0[0] = myPoints.get(myPoints.size()-2);
    	p0[1] = myPoints.get(myPoints.size()-1);
    	gl.glTexCoord2d(1, 0);
    	gl.glVertex3d(p0[0] - (n0[0]*w),Terrain.altitude(p0[0],p0[1])+0.01,p0[1] + (n0[1]*w));
    	gl.glTexCoord2d(0, 0);
		gl.glVertex3d(p0[0] - (n0[0]*w),Terrain.altitude(p0[0],p0[1])+0.01,p0[1] - (n0[1]*w)); 
    	gl.glEnd();
    	gl.glLineWidth(1);
    	gl.glPopMatrix();
    }
}

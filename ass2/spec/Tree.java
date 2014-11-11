package ass2.spec;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * COMMENT: Comment Tree 
 *
 * @author malcolmr
 */
public class Tree {

    private double[] myPos;
    
    public Tree(double x, double y, double z) {
        myPos = new double[3];
        myPos[0] = x;
        myPos[1] = y;
        myPos[2] = z;
    }
    
    public double[] getPosition() {
        return myPos;
    }
    
    public void draw(GL2 gl) { 
    		//Model a tree as a sphere on a cylinder
    	    GLUT glut = new GLUT();
    	    GLU glu = new GLU();
    		gl.glPushMatrix();
    		float[] rgba = {1f, 1f, 1f};
    	    gl.glTranslated( myPos[0], myPos[1] +0.7,  myPos[2]);    	    
    	    gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
            gl.glEnable(GL2.GL_TEXTURE_GEN_S);
    	    gl.glEnable(GL2.GL_TEXTURE_GEN_T);
            gl.glBindTexture(GL.GL_TEXTURE_2D, 3);
    	    glut.glutSolidSphere(0.3, 20, 20);
    	    gl.glTranslated( 0,-0.7,0);
    	    gl.glRotated(270, 1, 0, 0);           
    	    gl.glBindTexture(GL.GL_TEXTURE_2D, 4);    	    
    	    glut.glutSolidCylinder(0.1,0.7,20,20);  
    	    gl.glDisable(GL2.GL_TEXTURE_GEN_S); 
    	    gl.glDisable(GL2.GL_TEXTURE_GEN_T);
    	    gl.glPopMatrix();
    }
}

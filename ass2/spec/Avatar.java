package ass2.spec;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;

public class Avatar{
	private static double[] myPos;
	private static double[] myTarget = {0,1};
	private double myAngle = 135;
    public Avatar() {
        myPos = new double[3];
        myPos[0] = 0;
        myPos[1] = 0;
        myPos[2] = Terrain.altitude(0,0);        
    }
    public static double[] avatarPos(){
    	return myPos;
    }
    public static double[] targetPos(){
    	return myTarget;
    }
    public void draw(GL2 gl) {
    	//Pathing restrictions
    	if(myPos[0] < 0) {
    		myPos[0] = 0;
    	}
    	if(myPos[1] < 0){
    		myPos[1] = 0;
    	}
    	if(myPos[0] > Terrain.size().getWidth() - 1){
    		myPos[0] = Terrain.size().getWidth() - 1;
    	}
    	if(myPos[1] > Terrain.size().getHeight() - 1){
    		myPos[1] = Terrain.size().getHeight() - 1;
    	}
    	myPos[2] = Terrain.altitude(myPos[0],myPos[1]);  
    	GLUT glut = new GLUT();
    	
    	gl.glPushMatrix();
		//Draw girl(easier :))
		gl.glTranslated(myPos[0],myPos[2]+ 0.5, myPos[1]);
		gl.glRotated(Math.toDegrees(135-myAngle),0,1,0);		
		glut.glutSolidSphere(0.1, 20, 20);
		gl.glTranslated(0, -0.13, 0);
		gl.glRotated(90, 0, 1,0 );
		glut.glutSolidCylinder(0.03,0.2,20,20);
		gl.glRotated(180, 0, 1,0 );
		glut.glutSolidCylinder(0.03,0.2,20,20);
		gl.glRotated(90, 0, 1, 0);
		gl.glTranslated(0, -0.22, 0);
		gl.glRotated(270, 1, 0, 0); 		
		glut.glutSolidCone(0.15,0.35,20,20);
		gl.glRotated(-270, 1, 0, 0); 
		gl.glTranslated(-0.05, -0.15, 0);		
		gl.glRotated(270, 1, 0, 0);
		glut.glutSolidCylinder(0.03,0.15,20,20);
		gl.glRotated(-270, 1, 0, 0); 
		gl.glTranslated(0.1, 0, 0);
		gl.glRotated(270, 1, 0, 0);
		glut.glutSolidCylinder(0.03,0.15,20,20);
		gl.glPopMatrix();
    }
    //Move in direction determined by square it's facing
	public void Move(double fraction) {
		myPos[0] += myTarget[0] * fraction;
		myPos[1] += myTarget[1] * fraction;
		
	}
	//The square the avatar can face is limited to +-1 in z,y direction from where it's standing
	public void Rotate(double angle) {
		myAngle += angle;
		myTarget[0] = Math.sin(myAngle);
		myTarget[1] = -Math.cos(myAngle);
		
	}
}

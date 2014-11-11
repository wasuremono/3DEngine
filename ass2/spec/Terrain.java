package ass2.spec;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import java.util.Calendar;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.jogamp.opengl.util.gl2.GLUT;



/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain implements KeyListener{
	private static final int MAX_PARTICLES = 1000;
	private static boolean sun = false;
	public int  i = 0;
    private static Dimension mySize;
    private static double[][] myAltitude;
    private static double maxAlt;
    private List<Tree> myTrees;
    private List<Road> myRoads;
    private float[] mySunlight;
	private Avatar myAvatar;
	private ParticleSystem myParticleSystem;

    /**
     * Create a new terrain
     *
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth) {
        mySize = new Dimension(width, depth);
        myAltitude = new double[width][depth];
        myTrees = new ArrayList<Tree>();
        myRoads = new ArrayList<Road>();
        mySunlight = new float[4];
        myAvatar = new Avatar();
        myParticleSystem = new ParticleSystem(1000);
        maxAlt = 0;
    }
    
    public Terrain(Dimension size) {
        this(size.width, size.height);
    }

    public static Dimension size() {
        return mySize;
    }
    public static double getMaxAlt() {
        return maxAlt;
    }
    public List<Tree> trees() {
        return myTrees;
    }

    public List<Road> roads() {
        return myRoads;
    }

    public float[] getSunlight() {
        return mySunlight;
    }

    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        mySunlight[0] = -dx;
        mySunlight[1] = -dy;
        mySunlight[2] = -dz; 
        mySunlight[3] = 0;
    }
    
    /**
     * Resize the terrain, copying any old altitudes. 
     * 
     * @param width
     * @param height
     */
    public void setSize(int width, int height) {
        mySize = new Dimension(width, height);
        double[][] oldAlt = myAltitude;
        myAltitude = new double[width][height];
        
        for (int i = 0; i < width && i < oldAlt.length; i++) {
            for (int j = 0; j < height && j < oldAlt[i].length; j++) {
                myAltitude[i][j] = oldAlt[i][j];
                if(myAltitude[i][j] > maxAlt){
                	maxAlt = myAltitude[i][j];
                }
            }
        }
    }

    /**
     * Get the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public static double getGridAltitude(int x, int z) {
        return myAltitude[x][z];
    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, double h) {
        myAltitude[x][z] = h;
    }

    /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points
     * 
     * TO BE COMPLETED
     * 
     * @param x
     * @param z
     * @return
     */
    //Use Bilinear interpolating to calculate heights
    public static double altitude(double x, double z) {
    	if(x == mySize.getWidth() -1){
    		x -= 1;
    	}
    	if(z == mySize.getHeight() -1){
    		z -= 1;
    	}
    	int x1 = (int) Math.floor(x);
    	int x2 = x1+1;
    	int z1 = (int) Math.floor(z);
    	int z2 = z1+1;
    	double h1 = getGridAltitude(x1,z1);
    	double h2 = getGridAltitude(x2,z1);
    	double h3 = getGridAltitude(x1,z2);
    	double h4 = getGridAltitude(x2,z2);
    	double a1 = h1 * ((x2 - x)/(x2 - x1));
    	double a2 = h2 * ((x - x1)/(x2 - x1));
    	double a3 = h3 * ((x2 - x)/(x2 - x1));
    	double a4 = h4 * ((x - x1)/(x2 - x1));
        double altitude = (a1+a2) * ((z2 - z)/(z2 - z1)) + (a3+a4) * ((z - z1)/(z2 - z1));  
        return altitude;
    }

    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z
     */
    public void addTree(double x, double z) {
        double y = altitude(x, z);
        Tree tree = new Tree(x, y, z);
        myTrees.add(tree);
    }


    /**
     * Add a road. 
     * 
     * @param x
     * @param z
     */
    public void addRoad(double width, double[] spine) {
        Road road = new Road(width, spine);
        myRoads.add(road);        
    }
    public void draw(GL2 gl) {   
    	GLUT glut = new GLUT();
    	gl.glMatrixMode(GL2.GL_MODELVIEW);
    	gl.glPushMatrix();  
    	//Color of sun modelled from orange/yellow at morning/dusk to white at midday
    	float timeC = (float) (Math.sin(getSunPos()));
    	//Position of sun modelled to travel from +x towards -x
    	float timeP = (float) (-Math.cos(getSunPos()));
        float[] lightColorAmbient = {0.2f, 0.2f, 0.2f, 1f};
        float[] lightColorDiffuse = {0.8f, 0.8f, 0.8f, 0.8f};
        float[] lightColorSpecular = {0.8f, 0.8f, 0.8f, 1f}; 
        float[] sunColor = {1.0f,(float) (0.45+(timeC*0.55)),1.0f,1.0f};   
        float[] sunPos = {(float) (timeP*mySize.getWidth()),timeC*5,(float)(mySize.getHeight()/2),0};
        if(timeC < 0.5){
        	sunColor[2] = 0.0f;
        }
        // Set light parameters(Use torch mode or sun mode)
        if(!sun){
        	gl.glDisable(GL2.GL_LIGHT0);
        	gl.glDisable(GL2.GL_LIGHT1);
        	gl.glEnable(GL2.GL_LIGHT2);
        	gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_POSITION, getSunlight(), 0);        
        	gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_AMBIENT, lightColorAmbient, 0);
        	gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_DIFFUSE, lightColorDiffuse, 0);
        	gl.glLightfv(GL2.GL_LIGHT2, GL2.GL_SPECULAR, lightColorSpecular, 0);
        }else if(!Camera.torch){  
        	//Using sun mode
        	//We put a directional light where the sun is and use it to light our environment
        	//Sun is modelled as a textured sphere
        	gl.glDisable(GL2.GL_LIGHT1);
        	gl.glDisable(GL2.GL_LIGHT2);
        	gl.glEnable(GL2.GL_LIGHT0);
	        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, sunPos, 0);  
	        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lightColorAmbient, 0);
	        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, sunColor, 0);
	        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, lightColorSpecular, 0);
	        gl.glPushMatrix(); 
	        gl.glTranslatef(sunPos[0], sunPos[1], sunPos[2]);
	        gl.glEnable(GL2.GL_TEXTURE_GEN_S); 
    	    gl.glEnable(GL2.GL_TEXTURE_GEN_T);
            gl.glBindTexture(GL.GL_TEXTURE_2D, 6);
	        glut.glutSolidSphere(0.2, 20, 20);
	        gl.glDisable(GL2.GL_TEXTURE_GEN_S); 
    	    gl.glDisable(GL2.GL_TEXTURE_GEN_T);
    	    gl.glPopMatrix();
        }
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        gl.glBindTexture(GL.GL_TEXTURE_2D, 3);
        gl.glBegin(GL2.GL_QUADS);
        //Model the terrain mesh a quads
        {
        	for(int x = 0;x < mySize.width - 1 ; x++){
        		for(int z = 0; z < mySize.height - 1;z++){        			
        			double[] U = new double[3];
        			double[] V = new double[3];
        			U[0] = 1;
        			U[1] = getGridAltitude(x+1,z) - getGridAltitude(x,z) ;
        			U[2] = 0;
        			V[0] = 0;
        			V[1] = getGridAltitude(x,z+1) - getGridAltitude(x,z) ;
        			V[2] = 1;
        			double[] N = new double[3];
        			N[0] = -1 *((U[1] * V[2]) - (U[2] * V[1]));
        			N[1] = -1 *((U[2] * V[0]) - (U[0] * V[2]));
        			N[2] = -1 *((U[0] * V[1]) - (U[1] * V[0]));
        			gl.glNormal3d(N[0],N[1],N[2]);      
        			gl.glTexCoord2d(0, 0);
		            gl.glVertex3d(x, getGridAltitude(x,z), z);
		            gl.glNormal3d(N[0],N[1],N[2]);
		            gl.glTexCoord2d(1, 0);
		            gl.glVertex3d(x+1, getGridAltitude(x+1,z), z);
		            gl.glNormal3d(N[0],N[1],N[2]);
		            gl.glTexCoord2d(1, 1);
		            gl.glVertex3d(x+1, getGridAltitude(x+1,z+1), z+1);
		            gl.glNormal3d(N[0],N[1],N[2]);
		            gl.glTexCoord2d(0, 1);
		            gl.glVertex3d(x, getGridAltitude(x,z+1), z+1);
		        }
        	}
      
        }
        gl.glEnd(); 
        //Wireframe + FIll mode - turn on if nessecary
        /**        
        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glDisable(GL2.GL_LIGHT0);
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);          
        
        gl.glBegin(GL2.GL_QUADS);  
        {
        	for(int x = 0;x < mySize.width - 1 ; x++){
        		for(int z = 0; z < mySize.height - 1;z++){  
        			float black[] = {0.0f, 0.0f, 0.0f, 1f};
        			gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, black,1);           			
		            gl.glVertex3d(x, getGridAltitude(x,z), z);
		            gl.glVertex3d(x+1, getGridAltitude(x+1,z), z);
		            gl.glVertex3d(x+1, getGridAltitude(x+1,z+1), z+1);
		            gl.glVertex3d(x, getGridAltitude(x,z+1), z+1);
		        }
        	}
      
        }
        gl.glEnd();
        gl.glEnable(GL.GL_TEXTURE_2D);
        if(!Camera.torch()){
        	gl.glEnable(GL2.GL_LIGHT0);
        }
        */        
        for(Road r : myRoads){
        	r.draw(gl);
        }
        for(Tree t : myTrees){
        	t.draw(gl);
        }
        myAvatar.draw(gl);
        myParticleSystem.draw(gl);
        gl.glPopMatrix();
    }

	private float getSunPos() {
		Calendar calendar = Calendar.getInstance();	
		float minute =  (float) (180*(calendar.get(Calendar.SECOND)*1000 + calendar.get(Calendar.MILLISECOND)) /60000.0);		
		minute = (float) Math.toRadians(minute);
		return minute;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		double fraction = 0.1;
		double angle  = 0.1;
		switch(e.getKeyCode()) {
        case KeyEvent.VK_UP:
        	myAvatar.Move(fraction);
			break;
		case KeyEvent.VK_DOWN:
			myAvatar.Move(-fraction);
			break;
		case KeyEvent.VK_LEFT:
			myAvatar.Rotate(-angle);
			break;
		case KeyEvent.VK_RIGHT:
			myAvatar.Rotate(angle);
			break;
		case KeyEvent.VK_T:
			Camera.torch();
			break;
		case KeyEvent.VK_S:
			sun = !sun;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}

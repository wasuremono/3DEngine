package ass2.spec;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.Buffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import com.jogamp.opengl.util.FPSAnimator;
import ass2.spec.LevelIO;

/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */
public class Game extends JFrame implements GLEventListener{
    private static final double CAMERA_DIST = 5;
    private static final float FOV = 90;
    private Camera myCamera;
    private double myAngle;
    private Texture grassTexture = null;
    private Texture treeTexture = null;
    private Texture roadTexture = null;
    private Texture sunTexture = null;
    private Terrain myTerrain;

    public Game(Terrain terrain,Camera camera) {
        myTerrain = terrain;
        myCamera = camera;
    }
    
    /** 
     * Run the game.
     *
     */
    public void run() {
    	
    	GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities(glprofile);
        GLJPanel panel = new GLJPanel(glcapabilities);
        panel.addGLEventListener(this);
        panel.addKeyListener(myTerrain);
        panel.setFocusable(true);
        panel.requestFocus();        
        // Add an animator to call 'display' at 60fps        
        FPSAnimator animator = new FPSAnimator(60);
        animator.add(panel);
        animator.start();
        getContentPane().add(panel, BorderLayout.CENTER);
        setSize(800, 800);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE); 
    }


    
    /**
     * Load a level file and display it.
     * 
     * @param args - The first argument is a level file in JSON format
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Terrain terrain = LevelIO.load(new File(args[0]));
        Camera camera = new Camera();
        Game game = new Game(terrain,camera);        
        game.run();
    }

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_LIGHT1);
        gl.glEnable(GL2.GL_LIGHT2);
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAX_LEVEL,2);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_GENERATE_MIPMAP,GL2.GL_TRUE);
        grassTexture = new Texture(GLProfile.getDefault(), gl, "Grass.png", "png");        
        gl.glGenerateMipmap(grassTexture.getTextureID());
        treeTexture = new Texture(GLProfile.getDefault(), gl, "tree.png", "png");
        gl.glGenerateMipmap(treeTexture.getTextureID());
        roadTexture = new Texture(GLProfile.getDefault(), gl, "Road.png", "png");
        gl.glGenerateMipmap(roadTexture.getTextureID());
        sunTexture = new Texture(GLProfile.getDefault(), gl, "sun.png", "png");
        gl.glGenerateMipmap(sunTexture.getTextureID());
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int arg1, int arg2, int width,
			int height) {
        GL2 gl = drawable.getGL().getGL2();
        
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        
//        gl.glOrtho(-8, 8, -8, 8, -8, 8);
        
        myCamera.setAspect(1.0 * width / height);
		
	}

	@Override
	public void display(GLAutoDrawable drawable) {

        	
	        GL2 gl = drawable.getGL().getGL2();
	        gl.glClearColor(0, 0, 0, 1);
	        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);	        
	        gl.glMatrixMode(GL2.GL_MODELVIEW);
	        gl.glLoadIdentity();      
	        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
	        myTerrain.draw(gl);	        
	        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
	        myCamera.draw(gl);
	}
   
}

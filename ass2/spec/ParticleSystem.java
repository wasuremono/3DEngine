package ass2.spec;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

public class ParticleSystem {
	private ArrayList<Particle> myParticles;
	
	public ParticleSystem(int i){
		myParticles = new ArrayList<Particle>();
		for(int num = 0; num < i ; num++){
			Particle newParticle = new Particle(num);
			myParticles.add(newParticle);
		}
		
	}
	
	public void draw(GL2 gl){
		   double[] pos;		   
		   for (int i = 0; i < myParticles.size() - 1 ; i++){
			 Particle p = myParticles.get(i);
			 pos = p.getPos();
		     if(pos[1] < 0.0){
		    	 p.setLifetime(0.0f);
		     }
		     if((p.isAlive()) && (p.getLifetime()>0.0)){	
		    	 //Model each raindrop as a small cube
		    	 gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
		    	 gl.glBegin(GL2.GL_QUADS);{
		    	 	// top y = 1		    	 
		            gl.glNormal3d(0,1,0);
		            gl.glVertex3d(pos[0]-0.008,pos[1]+ 0.008, pos[2]-0.008);
		            gl.glVertex3d(pos[0]+0.008, pos[1]+0.008, pos[2]-0.008);
		            gl.glVertex3d(pos[0]+0.008, pos[1]+0.008, pos[2]+0.008);
		            gl.glVertex3d(pos[0]-0.008,pos[1] +0.008, pos[2] +0.008);

		            // bottom y = -1
		            gl.glNormal3d(0,-1,0);
		            gl.glVertex3d(pos[0]-0.008, pos[1]-0.008, pos[2]-0.008);
		            gl.glVertex3d(pos[0]-0.008, pos[1]-0.008,pos[2] +0.008);
		            gl.glVertex3d(pos[0]+0.008, pos[1]-0.008, pos[2]+0.008);
		            gl.glVertex3d(pos[0]+0.008, pos[1]-0.008, pos[2]-0.008);

		            // left x = -1
		            gl.glNormal3d(-1,0,0);
		            gl.glVertex3d(pos[0]-0.008,pos[1] -0.008,pos[2] -0.008);
		            gl.glVertex3d(pos[0]-0.008,pos[1] +0.008, pos[2]-0.008);
		            gl.glVertex3d(pos[0]-0.008,pos[1] +0.008,pos[2] +0.008);
		            gl.glVertex3d(pos[0]-0.008,pos[1] -0.008, pos[2]+0.008);
		            
		            // right x = 1
		            gl.glNormal3d(1,0,0);
		            gl.glVertex3d(pos[0]+0.008,pos[1] -0.008,pos[2] -0.008);
		            gl.glVertex3d(pos[0]+0.008,pos[1] -0.008,pos[2]+0.008);
		            gl.glVertex3d(pos[0]+0.008,pos[1] +0.008,pos[2] +0.008);
		            gl.glVertex3d(pos[0]+0.008,pos[1] +0.008, pos[2]-0.008);

		            // front z = 0.008
		            gl.glNormal3d(0,0,1);
		            gl.glVertex3d(pos[0]-0.008,pos[1] -0.008,pos[2] +0.008);
		            gl.glVertex3d(pos[0]+0.008,pos[1] -0.008, pos[2]+0.008);
		            gl.glVertex3d(pos[0]+0.008,pos[1] +0.008, pos[2]+0.008);
		            gl.glVertex3d(pos[0]-0.008,pos[1] +0.008, pos[2]+0.008);

		            // back z = -1
		            gl.glNormal3d(0,0,-1);
		            gl.glVertex3d(pos[0]-0.008,pos[1] -0.008, pos[2]-0.008);
		            gl.glVertex3d(pos[0]-0.008,pos[1] +0.008,pos[2]-0.008);
		            gl.glVertex3d(pos[0]+0.008,pos[1] +0.008, pos[2]-0.008);
		            gl.glVertex3d(pos[0]+0.008,pos[1] -0.008,pos[2] -0.008);  
		    	 }
		       gl.glEnd();
		      } else {		
		    	//If the particle has expired/hit the ground make a new one
		    	myParticles.set(i, new Particle(i));
		      }
		     //update the speed and position of the particle
		     p.updateParticle();
		    }		   	
	}

}



package ass2.spec;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Random;

public class Particle {
	private float lifetime;                     
	private float decay;   
	private double[] pos = {0,0,0};                 
	private float[] speed = {0,0,0};           		
	private float gravity;				
	private boolean alive;                       
	private int id;
	private int mapWidth = (int) Math.floor(Terrain.size().getWidth());
	private	int mapHeight = (int) Math.floor(Terrain.size().getHeight());
	
	//Using recommened values to model some water droplets
	public Particle(int i) {
		double[] avatar = Avatar.avatarPos(); 	
		Random generator = new Random();
        setLifetime((float) (generator.nextInt(500000)/500000.0));
        decay = (float) 0.001;
        pos[0] = avatar[0] - 10  + generator.nextInt(20);
        pos[1] = avatar[1] + 10;
        pos[2] = avatar[2] - 10  + generator.nextInt(20);;
        speed[0] = (float) (0.0005-(float)generator.nextInt(100)/100000.0);
        speed[1] = (float) (0.01-(float)generator.nextInt(100)/100000.0);
        speed[2] = (float) (0.0005-(float) generator.nextInt(100)/100000.0);
        setAlive(true);
        this.id = i;
    }
	
	//Each particle falls faster and faster downwards due to gravity, position being altered by speed
	public void updateParticle(){		
		     setLifetime(getLifetime() - decay);
		     pos[0] += speed[0];
		     pos[1] += speed[1];
		     pos[2] += speed[2];
		     speed[1] -= 0.00007;
	}

	public double[] getPos() {		
		return pos;
	}


	public float getLifetime() {
		return lifetime;
	}

	public void setLifetime(float lifetime) {
		this.lifetime = lifetime;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}
}

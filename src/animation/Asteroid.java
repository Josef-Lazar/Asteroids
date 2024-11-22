package animation;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;

import animation.AbstractAnimation;
import animation.AnimatedObject;

import java.lang.Math.*;

/**
 * A demo of what an animated object might look like. In this case, we have a
 * small black circle that moves from left to right across the window. When it
 * reaches the right edge of the window, it bounces and goes to the left edge.
 * At the left edge, it bounces and goes right.
 *
 */

public class Asteroid implements AnimatedObject {
	//size of asteroid
	private int size;
	
	//coordinates of asteroid
	private double x;
	private double y;
	/*
	private int[] xArr = {(int) (5 * Math.pow(2, size)), (int) (0 * Math.pow(2, size)),
	                      (int) (-5 * Math.pow(2, size)), (int) (0 * Math.pow(2, size))};
	private int[] yArr = {(int) (0 * Math.pow(2, size)), (int) (5 * Math.pow(2, size)),
	                      (int) (0 * Math.pow(2, size)), (int) (-5 * Math.pow(2, size))};
	*/
	
	//velocity of asteroid
	private double xVel;
	private double yVel;
	
    // The animation that this object is part of.
    private AbstractAnimation animation;
    
    // The asteroid shape
    private Polygon p;
	
	/**
     * Creates the asteroid
     * 
     * @param animation the animation this object is part of
	 * @param x the initial x coordinate of the asteroid
	 * @param y the initial y coordinate of the asteroid
	 * @param xVel the speed of the asteroid along the x axis
	 * @param yVel the speed of the asteroid along the y axis
	 * @param size the size of the asteroid
     */
    public Asteroid(AbstractAnimation animation, double x, double y, double xVel, double yVel, int size) {
        this.animation = animation;
		this.x = x;
		this.y = y;
		this.xVel = xVel;
		this.yVel = yVel;
		this.size = size;
        p = new Polygon();
        
        int corners = (int) (8 + Math.random() * 5);
        double intervalLen = 2 * Math.PI / corners;
        for (int i = 0; i < corners; i++) {
        	double angle = i * intervalLen + (Math.random() * intervalLen - intervalLen / 2);
        	double length = (3 + Math.random() * 4) * Math.pow(2, size);
        	double[] pointXY = Game.polarToCartesian(angle, length);
        	p.addPoint((int) (pointXY[0]), (int) (pointXY[1]));
        }
        /*
		p.addPoint((int) (5 * Math.pow(2, size)), (int) (0 * Math.pow(2, size)));
		p.addPoint((int) (3 * Math.pow(2, size)), (int) (3 * Math.pow(2, size)));
		p.addPoint((int) (0 * Math.pow(2, size)), (int) (5 * Math.pow(2, size)));
		p.addPoint((int) (-3 * Math.pow(2, size)), (int) (3 * Math.pow(2, size)));
		p.addPoint((int) (-5 * Math.pow(2, size)), (int) (0 * Math.pow(2, size)));
		p.addPoint((int) (-3 * Math.pow(2, size)), (int) (-3 * Math.pow(2, size)));
		p.addPoint((int) (0 * Math.pow(2, size)), (int) (-5 * Math.pow(2, size)));
		p.addPoint((int) (3 * Math.pow(2, size)), (int) (-3 * Math.pow(2, size)));
		*/
		p.translate((int) (x),  (int) (y));
		
    }
	
	/**
     * Draws the asteroid at its current location.
     * 
     * @param g the graphics context to draw on.
     */
    public void paint(Graphics2D g) {
        g.setColor(Color.GRAY);
        g.fill(p);
        //g.drawPolygon(xArr, yArr, pointCount);
    }

    /**
     * Moves the asteroid. If it reaches the left, right, top, or bottom edge,
	 * it moves to the other side of the screen.
     */
    public void nextFrame() {
        
        //resets location of asteroid
        p.translate((int) (- x),  (int) (- y));
        
        // Update the x value to move in the current direction
        x += xVel;
		y += yVel;

        // Check if the left edge of the asteroid is beyond the right
        // edge of the window. If it is, move it to the left edge.
        if (x - 7 * Math.pow(2, size) > animation.getWidth()) {
            x = - 7 * Math.pow(2, size);
        }

        // Check if the right edge of the asteroid is beyond the left
        // edge of the window. If it is, move it to the right edge.
        else if (x + 7 * Math.pow(2, size) < 0) {
			x = animation.getWidth() + 7 * Math.pow(2, size);
        }
		
		// Check if the bottom edge of the asteroid is beyond the top
		// edge of the window. If it is, move it to the bottom edge.
		if (y + 7 * Math.pow(2, size) < 0) {
			y = (int) (animation.getHeight() + 7 * Math.pow(2, size));
		}
		
		// Check if the top edge of the asteroid is beyond the bottom
		// edge of the window. If it is, move it to the top edge.
		else if (y - 7 * Math.pow(2, size) > animation.getHeight()) {
			y = - 7 * Math.pow(2, size);
		}
        
		//moves asteroid to new location
		p.translate((int) (x),  (int) (y));
		
    }
    
    /**
     * Returns the shape of the asteroid
     * @return the shape of the asteroid
     */
    public Shape getShape() {
    	return p;
    }
    
    
    /**
     * Calculates the direction of the asteroids that should form when this
     * asteroid is destroyed. Uses the same speed as current asteroid, with
     * some random increase/decrease. Makes two new asteroids with size
     * decremented by one and returns them. 
     * @return two asteroids that should form when this asteroid is destroyed
     */
    public Asteroid[] split() {
    	int asteroidsSize = this.size - 1;
    	if (asteroidsSize >= 1) {
        	double angle = getDirectionAngle();
    		double asteroid1Angle = angle + (Math.PI / 5) + (Math.random() * Math.PI / 6);
    		double asteroid2Angle = angle - (Math.PI / 5) - (Math.random() * Math.PI / 6);
    		double speed = getSpeed();
    		double asteroid1Speed = speed * (Math.random() + 0.5);
    		double asteroid2Speed = speed * (Math.random() + 0.5);
    		double[] asteroid1VelXY = Game.polarToCartesian(asteroid1Angle, asteroid1Speed);
    		double[] asteroid2VelXY = Game.polarToCartesian(asteroid2Angle, asteroid2Speed);
    		Asteroid asteroid1 = new Asteroid(this.animation, this.x, this.y, asteroid1VelXY[0], asteroid1VelXY[1], asteroidsSize);
    		Asteroid asteroid2 = new Asteroid(this.animation, this.x, this.y, asteroid2VelXY[0], asteroid2VelXY[1], asteroidsSize);
    		Asteroid[] asteroids = new Asteroid[2];
    		asteroids[0] = asteroid1;
    		asteroids[1] = asteroid2;
    		return asteroids;
    	} else {
    		return null;
    	}
    }
    
    /**
     * Returns the angle that this asteroid is traveling in
     * @return angle that the asteroid is traveing in
     */
    public double getDirectionAngle() {
    	double[] polarCoordinates = Game.cartesianToPolar(xVel, yVel);
    	return polarCoordinates[0];
    }
    
    public double getSpeed() {
    	double speed = Math.sqrt(Math.pow(xVel, 2) + Math.pow(yVel, 2));
    	return speed;
    }
    
    public int getSize() {
    	return this.size;
    }
    
    
    
}





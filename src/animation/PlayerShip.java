package animation;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;

import animation.AbstractAnimation;
import animation.AnimatedObject;

import java.lang.Math.*;

public class PlayerShip implements AnimatedObject {
    // Attributes
	private static final int MAX_VELOCITY = 10;
	AbstractAnimation animation;
    private double x, y; // Position
    private double direction; // Direction in radians
    private double velocity;
    private double acceleration;
    private int lives;
    private Polygon shipOriginal;
    private Polygon shipRotated;
    private Polygon shipDisplay;
    private Polygon shipOriginal2;
    private int score;
    private int level;
    
    /**
     * Constructor for PlayerShip    
     * @param x the x coordinate that the PlayerShip is initialized at
     * @param y the y coordinate that the PlayerShip is initialized at
     */
    public PlayerShip(AbstractAnimation animation, double x, double y) {
    	this.animation = animation;
        this.x = x;
        this.y = y;
        this.direction = 0; // Start facing right
        this.velocity = 0;
        this.acceleration = 0;
        this.lives = 3; // Start with 3 lives
        this.score = 0;
        this.level = 1;
        this.shipOriginal = new Polygon();
        this.shipOriginal.addPoint(-20, -10);
        this.shipOriginal.addPoint(20, 0);
        this.shipOriginal.addPoint(-20, 10);
        
        this.shipOriginal2 = new Polygon();
        //left ball
        this.shipOriginal2.addPoint(-17, 0);
        this.shipOriginal2.addPoint(-22, -5);
        this.shipOriginal2.addPoint(-22, -12);
        this.shipOriginal2.addPoint(-17, -17);
        this.shipOriginal2.addPoint(-10, -17);
        this.shipOriginal2.addPoint(-7, -14);
        this.shipOriginal2.addPoint(-7, -12);
        this.shipOriginal2.addPoint(-9, -7);
        //shaft and head
        this.shipOriginal2.addPoint(15, -7);
        this.shipOriginal2.addPoint(15, -9);
        this.shipOriginal2.addPoint(17, -11);
        this.shipOriginal2.addPoint(19, -11);
        this.shipOriginal2.addPoint(27, -7);
        this.shipOriginal2.addPoint(29, -2);
        this.shipOriginal2.addPoint(25, 0);
        this.shipOriginal2.addPoint(29, 2);
        this.shipOriginal2.addPoint(27, 7);
        this.shipOriginal2.addPoint(19, 11);
        this.shipOriginal2.addPoint(17, 11);
        this.shipOriginal2.addPoint(15, 9);
        this.shipOriginal2.addPoint(15, 7);
        //right ball
        this.shipOriginal2.addPoint(-9, 7);
        this.shipOriginal2.addPoint(-7, 12);
        this.shipOriginal2.addPoint(-7, 14);
        this.shipOriginal2.addPoint(-10, 17);
        this.shipOriginal2.addPoint(-17, 17);
        this.shipOriginal2.addPoint(-22, 12);
        this.shipOriginal2.addPoint(-22, 5);

        //shipOriginal = shipOriginal2; //uncomment for fun Easter Egg!!
        this.shipRotated = new Polygon(shipOriginal.xpoints.clone(), shipOriginal.ypoints.clone(), shipOriginal.npoints);
        this.shipDisplay = new Polygon(shipOriginal.xpoints.clone(), shipOriginal.ypoints.clone(), shipOriginal.npoints);
        this.shipDisplay.translate((int) (x), (int) (y));
    }
    
    // Method to move the ship up
    public void moveForward(double length) {
    	double[] deltaXY = Game.polarToCartesian(direction, length);
    	x += deltaXY[0];
    	y += deltaXY[1];
    	if (x > animation.getWidth()) {
    		x -= animation.getWidth();
    	} else if (x < 0) {
    		x += animation.getWidth();
    	}
    	if (y > animation.getHeight()) {
    		y -= animation.getHeight();
    	} else if (y < 0) {
    		y += animation.getHeight();
    	}
    	this.shipDisplay = new Polygon(shipRotated.xpoints.clone(), shipRotated.ypoints.clone(), shipRotated.npoints);
    	this.shipDisplay.translate((int) (x), (int) (y));
    }
    
    public void applyForce(double force) {
    	this.acceleration += force;
    }
    
    public void hyperspace() {
    	this.x = Math.random() * animation.getWidth();
    	this.y = Math.random() * animation.getHeight();
    	this.acceleration = 0;
    	this.velocity = 0;
    }
    
    
    // Method to rotate the ship clockwise
    public void rotate(double angle) {
        direction += angle;
        //make angle not negative
        while (direction < 0) {
        	direction += 2 * Math.PI;
        }
        //make angle less than 360 deg
        direction = direction % (2 * Math.PI);
        int[] rotatedXpoints = new int[shipOriginal.npoints];
        int[] rotatedYpoints = new int[shipOriginal.npoints];
        for (int point = 0; point < shipOriginal.npoints; point++) {
        	int xpoint = shipOriginal.xpoints[point];
        	int ypoint = shipOriginal.ypoints[point];
        	double[] polarCoordinates = Game.cartesianToPolar(xpoint, ypoint);
        	polarCoordinates[0] += direction;
        	double[] rotatedCartesianCoordinates = Game.polarToCartesian(polarCoordinates[0], polarCoordinates[1]);
        	rotatedXpoints[point] = (int) (rotatedCartesianCoordinates[0]);
        	rotatedYpoints[point] = (int) (rotatedCartesianCoordinates[1]);
        }
        this.shipRotated = new Polygon(rotatedXpoints, rotatedYpoints, shipOriginal.npoints);
        this.shipDisplay = new Polygon(rotatedXpoints, rotatedYpoints, shipOriginal.npoints);
        this.shipDisplay.translate((int) (x), (int) (y));
    }
    
    public void nextFrame() {
    	velocity += acceleration;
    	velocity = Math.min(velocity, MAX_VELOCITY);
    	acceleration = 0;
    	moveForward(velocity);
    	velocity *= 0.95;
        return;
    }
    
    // Method to draw the ship on the screen
    public void paint(Graphics2D g) {
        g.setColor(Color.RED);
        g.fill(shipDisplay);
    }
    
    public void displayLives(Graphics2D g) {
    	for (int i = 0; i < lives; i++) {
    		Polygon shipLife = new Polygon();
    		shipLife.addPoint(-5, 10);
    		shipLife.addPoint(0, -10);
    		shipLife.addPoint(5, 10);
    		shipLife.translate(50 + i * 15 , 50);
    		g.setColor(Color.RED);
    		g.fill(shipLife);
    	}
    }
    
    public void displayScore(Graphics2D g) {
    	g.setColor(Color.RED);
    	g.setFont(new Font("Impact", Font.PLAIN, 15));
    	g.drawString("Score: " + this.score, 35, 30);
    }
    
    public void displayLevel(Graphics2D g) {
    	g.setColor(Color.RED);
    	g.setFont(new Font("Impact", Font.PLAIN, 15));
    	g.drawString("Level: " + this.level, 35, 80);
    }
    
    public void decrementLives() {
    	this.lives--;
    }
    
    public void incrementScore() {
    	this.score += 10;
    }
    
    
    public void updateLevel() {
    	if (this.score < 250) {
    		this.level = 1;
    	} else if (this.score < 500) {
    		this.level = 2;
    	} else {
    		this.level = 3 + (int) (this.score / 1_000);
    	}
    }
    
    public void reset() {
    	this.lives = 3;
    	this.score = 0;
    	this.x = animation.getWidth() / 2;
    	this.y = animation.getHeight() / 2;
    	this.direction = 0;
    	this.acceleration = 0;
    	this.velocity = 0;
    }
    
    /**
     * Returns the polygon of the ship
     * @return the polygon of the ship
     */
    public Shape getShape() {
    	return shipDisplay;
    }
    
    public void setShipOriginal(Polygon shipOriginal) {
    	this.shipOriginal = shipOriginal;
    }
    
    public Polygon getShipOriginal() {
    	return shipOriginal;
    }
    
    public Polygon getShipOriginal2() {
    	return shipOriginal2;
    }
    
    public Polygon getShipRotated() {
    	return shipRotated;
    }
    
    public double getX() {
    	return x;
    }
    
    public double getY() {
    	return y;
    }
    
    public void setX(double x) {
    	this.x = x;
    }
    
    public void setY(double y) {
    	this.y = y;
    }
    
    public double getDirection() {
    	return direction;
    }
    
    public void setVelocity(double velocity) {
    	this.velocity = velocity;
    }
    
    public void setDirection(double direction) {
    	this.direction = direction;
    }
    
    public int getLives() {
    	return lives;
    }
    
    public int getScore() {
    	return score;
    }
    
    public void setScore(int score) {
    	this.score = score;
    }
    
    public int getLevel() {
    	return this.level;
    }
    
    
}

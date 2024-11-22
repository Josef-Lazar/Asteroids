package animation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
/**
 * Represents a bullet fired by the ship in the asteroid game.
 * The bullet moves in a straight line at a constant speed.
 * After reaching a certain distance or colliding with an asteroid it disappears. 
 */
public class Bullet implements AnimatedObject {

	/**
	 * Properties: Position, direction, and velocity
	 * Interaction: Collision with asteroid leads to breaking it smaller ones or destruction of asteroid. 
	 * Actions: Move, Hit
	 * Class Relationships: interact with ship to handle hit detection and asteroid destruction.
	 */
	
	private AbstractAnimation animation;
	private double x; // x-coordinate of the bullet's position. 
	private double y; // y-coordinate of the bullet's position.
	private double xVel; // Horizontal value of the bullet's velocity
	private double yVel; // Vertical value of the bullet's velocity
	private static final int Diameter = 5 ; //  angle of fire, setting the bullets trajectory.
	private Ellipse2D.Double shape; // 
	private double distanceTraveled ; // The distance the bullet has traveled
	private static final double maxDistance = 600; // Maximum distance the bullet can travel. 
	private boolean active; // Indicates whether the bullet is active. 
	
	
	/**
	 * Constructs a new Bullet with specified parameters.
	 * @param x the initial x-coordinate
	 * @param y the initial y-coordinate
	 * @param angle the firing angle in radians
	 * @param speed the speed of the bullet
	 * @param maxDistance the maximum distance the bullet can travel. 
	 */
	public Bullet(AbstractAnimation animation, double x, double y, double angle, int speed) {
		this.animation = animation;
		this.x = x;
		this.y = y;
		// Calculating the horizontal velocity using speed and cosine of the angle. 
		this.xVel = speed * Math.cos(angle);
		// Calculating the vertical velocity using speed and sine of the angle. 
		this.yVel = speed * Math.sin(angle);
		//
		this.shape = new Ellipse2D.Double(x,y, Diameter, Diameter);
		// Initially, the bullet is active
		this.active = true;
	}
	
	/**
	 * Updates the bullet's position and reduces its lifetime.
	 * The bullet is marked for removal if its lifetime reaches zero.
	 */
	public void nextFrame(){
		if(active) {
			// Calculate the distance moved in this frame
			double frameDistance = Math.sqrt(Math.pow(xVel, 2) + Math.pow(yVel, 2));
			
			x += xVel; // Update the bullet's x-coordinate.
			y += yVel; // Updates the bullet's y-coordinate. 
			
	        // Check if the left edge of the bullet is beyond the right
	        // edge of the window. If it is, move it to the left edge.
	        if (x - Diameter / 2 > animation.getWidth()) {
	            x -= animation.getWidth() + Diameter / 2;
	        }

	        // Check if the right edge of the bullet is beyond the left
	        // edge of the window. If it is, move it to the right edge.
	        else if (x + Diameter / 2 < 0) {
				x += animation.getWidth() + Diameter / 2;
	        }
			
			// Check if the bottom edge of the bullet is beyond the top
			// edge of the window. If it is, move it to the bottom edge.
			if (y + Diameter / 2 < 0) {
				y += animation.getHeight() + Diameter / 2;
			}
			
			// Check if the top edge of the bullet is beyond the bottom
			// edge of the window. If it is, move it to the top edge.
			else if (y - Diameter / 2 > animation.getHeight()) {
				y -= animation.getHeight() + Diameter / 2;
			}
	        
			
			shape.setFrame(x, y, Diameter, Diameter);
			
			// Update the total distance traveled
			distanceTraveled += frameDistance;
			
			
			// Check if the bullet has traveled beyond its maximum distance
			if(distanceTraveled >= maxDistance) {
				active = false; // Deactivate the bullet
			}
		}

	}
	
	/**
	 * Draws the bullet on the Graphics2D context. 
	 * @param g the graphics context to use for drawing. 
	 */
	public void paint(Graphics2D g) {
		if(active) {
			// Set the color of the bullet as white.
			g.setColor(Color.CYAN);
			// Drawing a small dot (Bullet) centered as x and y.
			g.fill(shape);
			g.draw(shape);
		}
	}
	
	public Shape getShape() {
		return shape;
	}
	
	/**
	 * Returns the x-coordinate of the bullet
	 * @return the x-coordinate
	 */
	public double getX() {
		return x;
	}
	
	/**
	 * Returns the y-coordinate of the bullet
	 * @return the y-coordinate
	 */
	public double getY() {
		return y;
	}
	
	/**
	 * Returns whether the bullet is still active. 
	 * Bullets that have exceeded their lifetime are considered inactive. 
	 * @return	true if the bullet is active, false otherwise. 
	 */
	public boolean isActive() {
		return active;
	}
	
}

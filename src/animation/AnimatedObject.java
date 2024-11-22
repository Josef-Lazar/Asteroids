package animation;
import java.awt.Graphics2D;
import java.awt.Shape;

/**
 * An object that changes during animation.
 *
 */
public interface AnimatedObject {
    /**
     * Updates the object's state as you want it to appear on 
     * the next frame of the animation.
     */
    public void nextFrame();
    
    /**
     * Draws the object
     * @param g the graphics context to draw on
     */
    public void paint(Graphics2D g);
    
    /**
     * Returns the shape of an AnimatedObject
     */
    public Shape getShape();
}

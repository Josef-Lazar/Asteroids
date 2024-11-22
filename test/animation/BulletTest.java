import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.awt.geom.Ellipse2D;

public class BulletTest {
    Bullet bullet;
    MockAnimation animation; // Assuming you'll implement or mock this

    @Before
    public void setUp() {
        animation = new MockAnimation(800, 600); // Assuming these screen dimensions
        bullet = new Bullet(animation, 100, 100, Math.PI / 4, 10); // Bullet fired at a 45 degree angle with speed 10
    }

    @Test
    public void testInitialization() {
        assertEquals(100, bullet.getX(), 0.01);
        assertEquals(100, bullet.getY(), 0.01);
        assertTrue(bullet.isActive());
    }

    @Test
    public void testMovement() {
        bullet.nextFrame();
        // Check correct movement based on speed and angle
        assertEquals(100 + 10 * Math.cos(Math.PI / 4), bullet.getX(), 0.01);
        assertEquals(100 + 10 * Math.sin(Math.PI / 4), bullet.getY(), 0.01);
    }

    @Test
    public void testBoundaryWrapping() {
        // Position bullet near the edge
        bullet = new Bullet(animation, 798, 100, 0, 10); // Right edge, moving right
        bullet.nextFrame();
        assertTrue(bullet.getX() < 800);
    }

    @Test
    public void testDistanceLimit() {
        // Simulate moving the bullet until it should deactivate
        for (int i = 0; i < 70; i++) { // 70 frames should cover more than 600 pixels at speed 10
            bullet.nextFrame();
        }
        assertFalse(bullet.isActive());
    }

    @Test
    public void testActiveState() {
        while (bullet.isActive()) {
            bullet.nextFrame();
        }
        assertFalse(bullet.isActive());
        assertEquals(600, bullet.distanceTraveled, 1); // Allow some rounding error
    }
}

// Mock or stub of AbstractAnimation, providing screen dimensions
class MockAnimation extends AbstractAnimation {
    private int width, height;
    
    public MockAnimation(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public int getWidth() {
        return width;
    }
    
    @Override
    public int getHeight() {
        return height;
    }
}

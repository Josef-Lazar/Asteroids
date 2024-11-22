import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.awt.Polygon;

public class PlayerShipTest {
    PlayerShip ship;
    MockAnimation animation; // You'll need a mock or a stub of the AbstractAnimation class for testing.

    @Before
    public void setUp() {
        animation = new MockAnimation(800, 600); // Assuming these dimensions, customize as needed
        ship = new PlayerShip(animation, 400, 300);
    }

    @Test
    public void testInitialization() {
        assertEquals(400, ship.getX(), 0.01);
        assertEquals(300, ship.getY(), 0.01);
        assertEquals(0, ship.getDirection(), 0.01);
        assertEquals(3, ship.getLives());
        assertEquals(0, ship.getScore());
    }

    @Test
    public void testMoveForward() {
        ship.setDirection(Math.PI / 2); // Set direction to "up"
        ship.moveForward(100);
        assertEquals(300, ship.getX(), 0.01);
        assertEquals(200, ship.getY(), 0.01); // Assuming no wrapping in this example
    }

    @Test
    public void testRotate() {
        ship.rotate(Math.PI / 2); // Rotate 90 degrees
        assertEquals(Math.PI / 2, ship.getDirection(), 0.01);
    }

    @Test
    public void testApplyForce() {
        ship.applyForce(5);
        ship.nextFrame();
        assertTrue(ship.getVelocity() > 0);
    }

    @Test
    public void testHyperspace() {
        ship.hyperspace();
        assertTrue(ship.getX() >= 0 && ship.getX() <= animation.getWidth());
        assertTrue(ship.getY() >= 0 && ship.getY() <= animation.getHeight());
        assertEquals(0, ship.getVelocity(), 0.01);
    }

    @Test
    public void testIncrementAndDecrementLives() {
        ship.decrementLives();
        assertEquals(2, ship.getLives());
        ship.decrementLives();
        ship.decrementLives();
        assertEquals(0, ship.getLives());
        ship.reset();
        assertEquals(3, ship.getLives());
    }

    @Test
    public void testScoreHandling() {
        ship.incrementScore();
        assertEquals(10, ship.getScore());
        ship.incrementScore();
        ship.incrementScore();
        assertEquals(30, ship.getScore());
    }

    @Test
    public void testReset() {
        ship.moveForward(50); // Move to change state
        ship.incrementScore();
        ship.reset();
        assertEquals(0, ship.getScore());
        assertEquals(3, ship.getLives());
        assertEquals(0, ship.getVelocity(), 0.01);
        assertEquals(400, ship.getX(), 0.01);
        assertEquals(300, ship.getY(), 0.01);
    }
}

// MockAnimation class, assumed to provide getWidth and getHeight
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

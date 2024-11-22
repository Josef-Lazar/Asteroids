import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.awt.Polygon;

public class AsteroidTest {
    Asteroid asteroid;
    MockAnimation animation;

    @Before
    public void setUp() {
        animation = new MockAnimation(800, 600);
        asteroid = new Asteroid(animation, 400, 300, 2, 2, 3);
    }

    @Test
    public void testInitialization() {
        assertEquals(400, asteroid.getX(), 0.01);
        assertEquals(300, asteroid.getY(), 0.01);
        assertEquals(3, asteroid.getSize());
        assertNotNull(asteroid.getShape());
    }

    @Test
    public void testMovementAndBoundaryHandling() {
        asteroid.nextFrame();
        assertEquals(402, asteroid.getX(), 0.01);
        assertEquals(302, asteroid.getY(), 0.01);
        // Simulate reaching the boundary
        asteroid = new Asteroid(animation, 799, 599, 2, 2, 3);
        asteroid.nextFrame();
        assertTrue(asteroid.getX() < 800);
        assertTrue(asteroid.getY() < 600);
    }

    @Test
    public void testSplitting() {
        Asteroid[] newAsteroids = asteroid.split();
        assertNotNull(newAsteroids);
        assertEquals(2, newAsteroids[0].getSize());
        assertEquals(2, newAsteroids[1].getSize());
        assertNotEquals(asteroid.getDirectionAngle(), newAsteroids[0].getDirectionAngle(), 0.1);
        assertNotEquals(asteroid.getDirectionAngle(), newAsteroids[1].getDirectionAngle(), 0.1);
    }

    @Test
    public void testGetDirectionAngleAndSpeed() {
        double angle = asteroid.getDirectionAngle();
        double speed = asteroid.getSpeed();
        assertEquals(Math.atan2(2, 2), angle, 0.01);
        assertEquals(Math.sqrt(4 + 4), speed, 0.01);
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

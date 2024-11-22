import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class PlayerShipTest {
    private Game game;
    private PlayerShip playerShip;

    @Before
    public void setUp() {
        game = new Game(); // Assuming Game initializes PlayerShip
        playerShip = game.getPlayerShip();
    }

    @Test
    public void testInitialization() {
        assertEquals("Initial X coordinate should be at center", Game.WINDOW_WIDTH / 2, playerShip.getX(), 0.01);
        assertEquals("Initial Y coordinate should be at center", Game.WINDOW_HEIGHT / 2, playerShip.getY(), 0.01);
        assertEquals("Initial lives should be 3", 3, playerShip.getLives());
    }

    @Test
    public void testMoveForward() {
        double initialX = playerShip.getX();
        double initialY = playerShip.getY();
        playerShip.moveForward(5); // Assume moveForward changes position based on current direction
        double newX = playerShip.getX();
        double newY = playerShip.getY();
        // Check that position has changed correctly
        assertNotEquals("X position should change after moving", initialX, newX);
        assertNotEquals("Y position should change after moving", initialY, newY);
    }
}

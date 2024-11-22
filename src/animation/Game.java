//navigate to src in terminal and type: java animation.demo.AnimationDemo

package animation;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.Font;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JLabel;

import animation.AbstractAnimation;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import java.io.File;
import java.io.IOException;

import java.util.Scanner; 
  
import javax.sound.sampled.AudioInputStream; 
import javax.sound.sampled.AudioSystem; 
import javax.sound.sampled.Clip; 
import javax.sound.sampled.LineUnavailableException; 
import javax.sound.sampled.UnsupportedAudioFileException; 

import java.util.*;

import java.time.*;

/**
 * Properties: lives, score, and levels
 * Interaction: 
 * Changes:
 * Actions: 
 * Functionalities: Keeps track of scores and lives.
 * Class Relationships: Manages instances of PlayerShip, Asteroid, and Bullet
 */
public class Game extends AbstractAnimation implements KeyListener {
    
    // Width and height of game window
    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 600;
    
    //stores pressed keys
    private final Set<Integer> pressedKeys = new HashSet<>();
    
    private static int gameState; // 0 <- start screen, 1 <- in game, 2 <- score board, 3 <- enter score
    
    private PlayerShip playerShip = new PlayerShip(this, WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2);
    private boolean moveShipForward = false;
    private boolean rotateShipLeft = false;
    private boolean rotateShipRight = false;
    private boolean triggerReleased = true;
    private boolean respawning = false;
    private static final long RESPAWN_TIME = 1_000;
    
    private static List<Asteroid> asteroids;
    private static List<Bullet> bullets;
    
    Clock clock = Clock.systemDefaultZone();
    long nextAsteroidSpawnTime;
    private long crashTime = clock.millis();

	// Images
	private Image gameBackgroundImage;
	private Image startBackgroundImage;
	
	
	private Clip clip;
	private Clip clipBackgroundMusic;
	AudioInputStream audioInputStream;
	static String shotAudioFilePath;
	static String asteroidExplosionAudioFilePath;
	static String deathAudioFilePath;
	static String gameOverAudioFilePath;
	static String startScreenAudioFilePath; //zero guidance needed
	static String gameScreenAudioFilePath; //an OMFO song?
	static String gameOverScreenAudioFilePath; //Manchurian Waltz? Noise in Sepher1?
	
    
    /**
     * Constructs an animation and initializes it to be able to accept
     * keyboard inputs.
     */
    public Game() {
        setFocusable(true);
        addKeyListener(this);
        asteroids = new ArrayList<>();
        bullets = new ArrayList<>();
        gameState = 0;
        //asteroids.add(new Asteroid(this, 150, 200, 1.5, 2.5, 3));// testing asteroids TODO: delete later
        //asteroids.add(new Asteroid(this, 400, 300, -2, 1.5, 2));
        for (int i = 0; i < 5; i++) {
        	spawnAsteroid();
        }
        nextAsteroidSpawnTime = clock.millis() + 2_000 + (long) (Math.random() * 7_000);
        
        try {
        	gameBackgroundImage = ImageIO.read(new File("animation/bgImage.jpg"));
        	startBackgroundImage = ImageIO.read(new File("animation/startScreen.jpg"));
		} catch (IOException e) {
			System.err.println("Unable to load background image: " + e);
			gameBackgroundImage = null;
			startBackgroundImage = null;
		}
        
        shotAudioFilePath = "animation/blaster.wav";
        //shotAudioFilePath = "animation/ak-47.wav";
        asteroidExplosionAudioFilePath = "animation/asteroid_explosion.wav";
        deathAudioFilePath = "animation/ship_dead.wav";
        gameOverAudioFilePath = "animation/game_over.wav";
        startScreenAudioFilePath = "animation/zero_guidance_needed.wav";
        gameScreenAudioFilePath = "animation/dagistan_and_noise_in_sepher_1.wav";
    	gameOverScreenAudioFilePath = "animation/manchurian_waltz.wav";
        
        try {
        	audioInputStream = AudioSystem.getAudioInputStream(new File(startScreenAudioFilePath).getAbsoluteFile());
            clipBackgroundMusic = AudioSystem.getClip();
            clipBackgroundMusic.open(audioInputStream);
            clipBackgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            clipBackgroundMusic.start();
        } catch (UnsupportedAudioFileException e) {
        	System.err.println("Unable to load auido file: " + e);
        	audioInputStream = null;
        	clipBackgroundMusic = null;
        } catch (IOException e) {
        	System.err.println("Unable to load auido file: " + e);
        	audioInputStream = null;
        	clipBackgroundMusic = null;
        } catch (LineUnavailableException e) {
        	System.err.println("Unable to load auido file: " + e);
        	audioInputStream = null;
        	clipBackgroundMusic = null;
        }
        
    }
    
    /**
     * Updates playerShip, asteroids, and bullets by calling their nextFrame
     * methods.
     */
    protected void nextFrame() {
    	switch (gameState) {
    		case 0:
    			// TODO
    			break;
    		case 1:
    			if (!respawning) {
			    	if (moveShipForward) {
			    		playerShip.applyForce(1);
			    	}
			    	if (rotateShipLeft) {
			    		playerShip.rotate(-0.15);
			    	}
			    	if (rotateShipRight) {
			    		playerShip.rotate(0.15);
			    	}
			    	playerShip.nextFrame();
		    	} else {
		    		if (clock.millis() > crashTime + RESPAWN_TIME) {
		    			boolean inAsteroid = false;
		    			for (Asteroid asteroid : asteroids) {
		    				if (checkCollision(playerShip, asteroid)) {
		    					inAsteroid = true;
		    				}
		    			}
		    			if (!inAsteroid) {
		    				respawning = false;
		    			}
		    		}
		    	}
		        for (Asteroid asteroid : asteroids) {
		            asteroid.nextFrame();
		            if (!respawning && checkCollision(playerShip, asteroid)) {
		        		playerShip.decrementLives();
		        		if (playerShip.getLives() < 0) {
		        			playSound(gameOverAudioFilePath);
		        			gameState = 2;
		        			switchBackgroundMusic(gameOverScreenAudioFilePath);
		        		} else {
		        			playSound(deathAudioFilePath);
		        		}
		        		respawning = true;
		        		crashTime = clock.millis();
		        		playerShip.setVelocity(0.0);
		        		playerShip.setDirection(0.0);
		        		playerShip.setX(WINDOW_WIDTH / 2);
		        		playerShip.setY(WINDOW_HEIGHT / 2);
		        		playerShip.rotate(-playerShip.getDirection());
		        	}
		        }
		        /*
		        for (int asteroidIndex = asteroids.size() - 1; asteroidIndex >= 0; asteroidIndex--) {
		        	asteroids.get(asteroidIndex).nextFrame();
		        	if (!respawning && checkCollision(playerShip, asteroids.get(asteroidIndex))) {
		        		playerShip.decrementLives();
		        		if (playerShip.getLives() < 0) {
		        			gameState = 2;
		        		}
		        		respawning = true;
		        		crashTime = clock.millis();
		        		playerShip.setVelocity(0.0);
		        		playerShip.setDirection(0.0);
		        		playerShip.setX(WINDOW_WIDTH / 2);
		        		playerShip.setY(WINDOW_HEIGHT / 2);
		        		playerShip.rotate(-playerShip.getDirection());
		        	}
		        	for (int bulletIndex = bullets.size() - 1; bulletIndex >= 0; bulletIndex--) {
		        		//System.out.print(bullets.get(bulletIndex));
		        		//System.out.print(asteroids.get(asteroidIndex));
		        		if (checkCollision(asteroids.get(asteroidIndex), bullets.get(bulletIndex))) {
		        			bullets.remove(bulletIndex);
		        			asteroidShot(asteroidIndex);
			        		break;
		        		}
		        	}
		        }
    			*/
		        for (int bulletIndex = bullets.size() - 1; bulletIndex >= 0; bulletIndex--) {
		        	if (bullets.get(bulletIndex).isActive()) {
		        		bullets.get(bulletIndex).nextFrame();
		        		for (int asteroidIndex = asteroids.size() - 1; asteroidIndex >= 0; asteroidIndex--) {
		        			if (checkCollision(bullets.get(bulletIndex), asteroids.get(asteroidIndex))) {
		        				bullets.remove(bulletIndex);
		        				asteroidShot(asteroidIndex);
		        				playerShip.incrementScore();
		        				playerShip.updateLevel();
		        				break;
		        			}
		        		}
		        	} else {
		        		bullets.remove(bulletIndex);
		        	}
		        }
		        
		        /*
		        for (Bullet bullet : bullets) {
		        	bullet.nextFrame();
		        }
		        */
		        
		        if (clock.millis() > nextAsteroidSpawnTime) {
			        spawnAsteroid();
			        nextAsteroidSpawnTime += Math.max(5_000 - (playerShip.getLevel() * 1_000), 500) +
			        						 Math.max(Math.random() * 10_000 - (playerShip.getLevel() * 2_000), 1_000);
		        }
    			break;
    		case 2:
    			// TODO
    			break;
    		case 3:
    			// TODO
    			break;
    		default:
    			assert false; //gameMode should be 0, 1, 2, or 3
    	}
    				
    }
    
    /**
     * TODO
     */
    //private boolean checkColisions(AnimatedObject object1, AnimatedObject object2) {
    //    return object1.getShape().intersects(object2.getShape().getBounds2D());
    //}
    
    /**
     * Paint the animation by painting the objects in the animation
     */
    public void paintComponent(Graphics g) {
        super.paintComponents(g);
        switch (gameState) {
        	case 0:
        		drawStartScreen((Graphics2D) g);
        		break;
        	case 1:
        		drawGameScreen((Graphics2D) g);
		        for (Bullet bullet : bullets) {
		            bullet.paint((Graphics2D) g);
		        }
		        if (!respawning) {
		        	playerShip.paint((Graphics2D) g);
		        }
		        for (Asteroid asteroid : asteroids) {
		            asteroid.paint((Graphics2D) g);
		        }
		        playerShip.displayLives((Graphics2D) g);
		        playerShip.displayScore((Graphics2D) g);
		        playerShip.displayLevel((Graphics2D) g);
		        break;
        	case 2:
        		drawGameOverScreen((Graphics2D) g);
        		break;
        }
    }
    
    private void drawStartScreen(Graphics2D g) {

		if (startBackgroundImage == null) {
			try {
				startBackgroundImage = ImageIO.read(new File("animation/startScreen.jpg"));
			} catch (IOException e) {
				System.err.println("Unable to load background image: " + e);
				startBackgroundImage = null;
			}
		}

		if (startBackgroundImage != null) {
			g.drawImage(startBackgroundImage, 0, 0, getWidth(), getHeight(), this);
		} else {
			g.setColor(Color.BLUE);
			g.fillRect(0, 0, getWidth(), getHeight());
		}

		g.setFont(new Font("Arial", Font.BOLD, 26));
		g.setColor(Color.WHITE);
		g.drawString("PRP CATS PRESENTS", 150, 200);
		g.drawString("ASTEROIDS", 220, 250);
		g.setFont(new Font("Arial", Font.PLAIN, 20));
		g.drawString("PRESS ENTER TO PLAY THE GAME", 150, 400);
	}
    
    private void drawGameScreen(Graphics2D g) {

    	if (startBackgroundImage != null) {
    		g.drawImage(gameBackgroundImage, 0, 0, getWidth(), getHeight(), this);
    	} else {
    		g.setColor(Color.BLUE);
    		g.fillRect(0, 0, getWidth(), getHeight());
    	}
    	
    }
    
    private void drawGameOverScreen(Graphics2D g) {
        // Clear the screen or draw the game over background
        if (startBackgroundImage != null) {
            g.drawImage(startBackgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Set font and color for text
        g.setFont(new Font("Arial", Font.BOLD, 35));
        g.setColor(Color.WHITE);

        // Draw game over text
        g.drawString("Game Over", WINDOW_WIDTH / 2 - 80, WINDOW_HEIGHT / 2 - 80);

        // Display final score
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Your Score: " + playerShip.getScore(), WINDOW_WIDTH / 2 - 30, WINDOW_HEIGHT / 2 + 60);

        // Ask to press a enter to restart or exit
        g.drawString("Press ENTER to restart or ESC to go to Welcome Screen", WINDOW_WIDTH / 2 - 250, WINDOW_HEIGHT / 2 + 100);
    }
    

    private boolean checkCollision(AnimatedObject object1,
    							   AnimatedObject object2) {
    	Area object1area = new Area(object1.getShape());
    	Area object2area = new Area(object2.getShape());
    	object1area.intersect(object2area);
    	return !object1area.isEmpty();
    	//return object1.getShape().intersects(object2.getShape());
    }
    
    /**
     * Takes Catesian coordinates as input, returns polar coordinates
     * @param x catesian x coordinate
     * @param y catesian y coordinate
     * @return an array of doubles, where the first element is the angle, and
     * the second is the length
     */
    public static double[] cartesianToPolar(double x, double y) {
    	double angle;
    	if (x == 0.0) {
    		angle = y >= 0 ? Math.PI / 2 : - Math.PI / 2;
    	} else if (x > 0) {
    		if (y >= 0) {
    			angle = Math.atan(y / x);
    		} else {
    			angle = 2*Math.PI - Math.atan(Math.abs(y) / Math.abs(x));
    		}
    	} else {
    		if (y >= 0) {
    			angle = Math.PI - Math.atan(y / Math.abs(x));
    		} else {
    			angle = Math.atan(Math.abs(y) / Math.abs(x)) + Math.PI;
    		}
    	}
    	double length = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    	double[] polarCoordinates = new double[2];
    	polarCoordinates[0] = angle;
    	polarCoordinates[1] = length;
    	return polarCoordinates;
    }
    
    /**
     * Takes polar coordinates as input, returns Cartesian coordinates
     * @param angle the angle of the polar coodinate
     * @param length the length of the polar coordinate
     * @return an array of doubles, where the first element is the x
     * coordinate, and the second is the y coordinate
     */
    public static double[] polarToCartesian(double angle, double length) {
    	double x = length * Math.cos(angle);
    	double y = length * Math.sin(angle);
    	double[] cartesianCoordinates = new double[2];
    	cartesianCoordinates[0] = x;
    	cartesianCoordinates[1] = y;
    	return cartesianCoordinates;
    }
    

    private void asteroidShot(int asteroidIndex) {
    	playSound(asteroidExplosionAudioFilePath);
    	Asteroid asteroid;
    	try {
    		asteroid = asteroids.get(asteroidIndex);
    		asteroids.remove(asteroidIndex);
    		if (asteroid.getSize() > 1) {
    			Asteroid[] childAsteroids = asteroid.split();
        		asteroids.add(childAsteroids[0]);
        		asteroids.add(childAsteroids[1]);
    		}
    		
    	} catch (NullPointerException e) {
    		e.printStackTrace();
    	}
    }
    
    
    private void shoot() {
    	playSound(shotAudioFilePath);
    	Bullet bullet = new Bullet(this, playerShip.getX(), playerShip.getY(), playerShip.getDirection(), 15);
    	bullets.add(bullet);
    }
    
    private void spawnAsteroid() {
    	double asteroidX = 0;
        double asteroidY = 0;
        double asteroidXVel = Math.random() * 6 - 3;
        double asteroidYVel = Math.random() * 6 - 3;
        int asteroidSize = (int) (1 + Math.random() * 3);
        if (Math.random() < 0.5) {
        	asteroidX = asteroidXVel > 0 ? - 7 * Math.pow(2, asteroidSize) : WINDOW_WIDTH + 7 * Math.pow(2, asteroidSize);
        	asteroidY = Math.random() * WINDOW_HEIGHT;
        } else {
        	asteroidX = Math.random() * WINDOW_WIDTH;
        	asteroidY = asteroidYVel > 0 ? - 7 * Math.pow(2, asteroidSize) : WINDOW_HEIGHT + 7 * Math.pow(2, asteroidSize);
        }
    	Asteroid asteroid = new Asteroid(this, asteroidX, asteroidY, asteroidXVel, asteroidYVel, asteroidSize);
    	asteroids.add(asteroid);
    }
    
    
    public void reset() {
    	playerShip.reset();
    	nextAsteroidSpawnTime = clock.millis();
        this.asteroids.clear();
        for (int i = 0; i < 5; i++) {
        	spawnAsteroid();
        }
    }
    
    public void playSound(String audioFilePath) {
    	try {
        	audioInputStream = AudioSystem.getAudioInputStream(new File(audioFilePath).getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException e) {
        	System.err.println("Unable to load auido file: " + e);
        	audioInputStream = null;
        } catch (IOException e) {
        	System.err.println("Unable to load auido file: " + e);
        	audioInputStream = null;
        } catch (LineUnavailableException e) {
        	System.err.println("Unable to load auido file: " + e);
        	audioInputStream = null;
        }
    }
    
    public void switchBackgroundMusic(String audioFilePath) {
    	clipBackgroundMusic.stop();
    	try {
        	audioInputStream = AudioSystem.getAudioInputStream(new File(audioFilePath).getAbsoluteFile());
            clipBackgroundMusic = AudioSystem.getClip();
            clipBackgroundMusic.open(audioInputStream);
            clipBackgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            clipBackgroundMusic.start();
        } catch (UnsupportedAudioFileException e) {
        	System.err.println("Unable to load auido file: " + e);
        	audioInputStream = null;
        } catch (IOException e) {
        	System.err.println("Unable to load auido file: " + e);
        	audioInputStream = null;
        } catch (LineUnavailableException e) {
        	System.err.println("Unable to load auido file: " + e);
        	audioInputStream = null;
        }
    }
    
    
    /**
     * Responds to keyboard inputs. Up key moves playerShip forward, right and
     * left keys rotate playerShip, space bar teleports playerShip to random
     * location (possibly into asteroid!). 
     * @param e information about the key pressed
     */
    public synchronized void keyPressed(KeyEvent e) {
    	/*
        int key = e.getKeyCode();
        switch (key) {
        case KeyEvent.VK_UP:
        	playerShip.moveForward(5);
            break;
        case KeyEvent.VK_RIGHT:
        	playerShip.rotate(0.1);
        	//System.out.println("right key pressed");
            //TODO
            break;
        case KeyEvent.VK_LEFT:
        	playerShip.rotate(-0.1);
            //TODO
            break;
        case KeyEvent.VK_SPACE:
        	System.out.println("space key pressed");
            asteroidShot(0);
            //TODO
            break;
        case KeyEvent.VK_P:
        	playerShip.setShipOriginal(playerShip.getShipOriginal2());
        	break;
        case KeyEvent.VK_S:
        	shoot();
        	break;
        default:
            // Ignore all other keys
        }
        */
        pressedKeys.add(e.getKeyCode());
        if (!pressedKeys.isEmpty()) {
            for (Iterator<Integer> it = pressedKeys.iterator(); it.hasNext();) {
                switch (it.next()) {
                    case KeyEvent.VK_W:
                    case KeyEvent.VK_UP:
                    	//playerShip.moveForward(5);
                    	moveShipForward = true;
                        break;
                    case KeyEvent.VK_A:
                    case KeyEvent.VK_LEFT:
                    	//playerShip.rotate(-0.15);
                    	rotateShipLeft = true;
                        break;
                    case KeyEvent.VK_S:
                    case KeyEvent.VK_DOWN:
                        break;
                    case KeyEvent.VK_D:
                    case KeyEvent.VK_RIGHT:
                    	//playerShip.rotate(0.15);
                    	rotateShipRight = true;
                        break;
                    case KeyEvent.VK_P:
                    	//playerShip.setShipOriginal(playerShip.getShipOriginal2());
                    	break;
                    case KeyEvent.VK_SHIFT:
                    	if (triggerReleased) {
                    		if (gameState == 1) {
                    			shoot();
                    		}
                    		triggerReleased = false;
                    	}
                    	break;
                    case KeyEvent.VK_SPACE:
                    	playerShip.hyperspace();
                    	break;
                    case KeyEvent.VK_ENTER:
                    	if (gameState == 0 || gameState == 2) {
                    		reset();
                    		switchBackgroundMusic(gameScreenAudioFilePath);
                    		gameState = 1;
                    	}
                    	break;
                    case KeyEvent.VK_ESCAPE:
                    	if (gameState == 2) {
                    		switchBackgroundMusic(startScreenAudioFilePath);
                    		gameState = 0;
                    	}
                    	break;
                    case KeyEvent.VK_Z:
                    	//clipBackgroundMusic.stop();
                    	break;
                    default:
                    	//dont' do anything
                }
            }
        }
        
    }
    
    /**
     * This is called when the user releases the key after pressing it.
     * It does nothing.
     * @param e information about the key released
     */
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
    	pressedKeys.remove(key);
    	switch (key) {
    		case KeyEvent.VK_W:
    		case KeyEvent.VK_UP:
    			moveShipForward = false;
    			break;
    		case KeyEvent.VK_A:
    		case KeyEvent.VK_LEFT:
    			rotateShipLeft = false;
    			break;
    		case KeyEvent.VK_D:
    		case KeyEvent.VK_RIGHT:
    			rotateShipRight = false;
    			break;
    		case KeyEvent.VK_SHIFT:
    			triggerReleased = true;
    			break;
    	}
    }
    
    public void keyTyped(KeyEvent e) {
        //event not used
    }
    
    
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// TODO: move to test class later
		/*
		double[] polarCoordinates = cartesianToPolar(1, 1);
		System.out.println("For x = 1, y = 1");
		System.out.println("angle: " + polarCoordinates[0]); //should be pi/4, which is approx. 0.785398163
		System.out.println("length: " + polarCoordinates[1]); //should be sqrt(2), which is approx. 1.41421356

		polarCoordinates = cartesianToPolar(-1.0/2.0, Math.sqrt(3)/2);
		System.out.println("\nFor x = -1/2, y = sqrt(3)/2");
		System.out.println("angle: " + polarCoordinates[0]); //should be (2pi)/3, which is approx. 2.0943951
		System.out.println("length: " + polarCoordinates[1]); //should be 1 because the point is on the unit circle
		
		polarCoordinates = cartesianToPolar(-1.0/2.0, -Math.sqrt(3)/2);
		System.out.println("\nFor x = -1/2, y = -sqrt(3)/2");
		System.out.println("angle: " + polarCoordinates[0]); //should be (4pi)/3, which is approx. 4.1887902
		System.out.println("length: " + polarCoordinates[1]); //should be 1 because the point is on the unit circle
		
		polarCoordinates = cartesianToPolar(Math.sqrt(3)/2, -1.0/2.0);
		System.out.println("\nFor x = sqrt(3)/2, y = -1/2");
		System.out.println("angle: " + polarCoordinates[0]); //should be (11pi)/6, which is approx. 5.75958653
		System.out.println("length: " + polarCoordinates[1]); //should be 1 because the point is on the unit circle
		
		double[] cartesianCoordinates = polarToCartesian(Math.PI / 4, Math.sqrt(2));
		System.out.println("\nFor angle = 45deg, len = sqrt(2)");
		System.out.println("x: " + cartesianCoordinates[0]); //should be 1
		System.out.println("y: " + cartesianCoordinates[1]); //should be 1
		
		cartesianCoordinates = polarToCartesian(5 * Math.PI / 3, 2);
		System.out.println("\nFor angle = 300deg, len = 2");
		System.out.println("x: " + cartesianCoordinates[0]); //should be 1
		System.out.println("y: " + cartesianCoordinates[1]); //should be -sqrt(3), which is approx. -1.73205081
		*/
		
		
		// JFrame is the class for a window. Create the window,
	    // set the window' title and its size
	    JFrame f = new JFrame();
	    f.setTitle("PRP Cat's ASTEROIDS");
	    f.setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
	    f.getContentPane().setBackground(Color.BLUE);
	    //f.setContentPane(new JLabel(new ImageIcon(ImageIO.read(new File("background_image.jpg")))));
	    
	    // This says that when the user closes the window, the
	    // entire program should exit.
	    Game game = new Game();
	    
	    // Add the animation to the window
	    Container contentPane = f.getContentPane();
	    contentPane.add(game, BorderLayout.CENTER);
	    
	    // Display the window.
	    f.setVisible(true);
	
	    game.start();
	    
	}

}


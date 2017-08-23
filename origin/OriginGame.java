package origin;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.Vector;
import java.util.Random;

import particle.*;
import displayPanel.*;

public class OriginGame extends Canvas implements Runnable {

    // The main class running
    private final Origin origin;
    // The menu instance
    private MenuDisplay menu;
    // For Game Over/Win dialogs and Score/Instructions panels
    private LoseDialog loseDialog;
    private WinDialog winDialog;
    private ScoreDisplay scoreDisplay;
    private InstDisplay instDisplay;

    // Canvas size
    public static final int CWIDTH = 800;
    public static final int CHEIGHT = 600;
    // Width and height of the game field in which particles are contained
    private static final int BOUNDX = 1600;
    private static final int BOUNDY = 1200;
    // Background width and height
    private static final int BGX = BOUNDX + CWIDTH;
    private static final int BGY = BOUNDY + CHEIGHT;

    // Time between each game loop with 50 fps, implies 20ms delay
    private static final int LOOP_DELAY = 1000 / 50;

    // Runs the game
    private Thread gameThread;

    // Initialises BufferStrategy
    BufferStrategy buffer;
    // The image buffer to be rendered to
    BufferedImage dbImage;
    // The graphics object for displaying renders
    Graphics g;
    // The graphics object for rendering
    Graphics2D g2d;

    // The game state
    private State state;
    // Indicates interrupted State
    private State pointer;

    // Vector to hold Particle objects
    Vector<Particle> v;
    // The player Particle
    Player player;

    // Timer for calculating score
    private long gameStartTimer;
    // Ensures timer does nto count pause time
    private long gamePausedOffset;
    final static int NUM_SCORES = 5;
    private String[][] scores;
    private File file;

    // Current active mouseEvent
    private MouseEvent mouseEvent;

    // Create a random number generator
    Random rnd = new Random();

    /***************
     * Constructor *
     ***************/
    public OriginGame(Origin origin) {
        this.origin = origin;
        setSize(CWIDTH, CHEIGHT);
        setIgnoreRepaint(true);
        setBackground(Color.white);
        setFocusable(true);
        requestFocusInWindow();    // Asks for Canvas focus to recieve events
    }


    // The game states
    public enum State {
        PLAYING, NEW, MAIN, PAUSED, LOSE, WIN, WRITE, SCORES, INSTRUCTIONS;
    }

    /*******************************************
     * Returns when component is added to pane *
     *******************************************/
    @Override
    public void addNotify() {
        super.addNotify();   // creates the peer
    }

    /***********************************************
     * Initialise game state called from main class*
     ***********************************************/
    protected void startGameEngine() {
        menu = origin.getMenuDisplay();
        loseDialog = origin.getLoseDialog();
        winDialog = origin.getWinDialog();
        scoreDisplay = origin.getScoreDisplay();
        instDisplay = origin.getInstDisplay();

        // Enables gameCanvas to receive these events
        enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK |
                AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);

        // Create buffer
        this.createBufferStrategy(2);
        buffer = this.getBufferStrategy();

        // Dimensions array for top 5 names and scores and extra space for new
        // one added to be sorted
        scores = new String[NUM_SCORES+1][2];
        // Creates file if it does not exist and fills it with undefined scores
        fileCreator();
        sortScores();
        saveScores();

        if (gameThread == null) {
            setState(State.MAIN);          // Initialises the game state
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    /****************************************************
     * Performs operations upon the state being changed *
     ****************************************************/
    public void setState(State state) {
        this.state = state;
        switch(state) {
            case PLAYING:
                if (gamePausedOffset > 0) {
                    gameStartTimer += System.currentTimeMillis()-gamePausedOffset;
                    gamePausedOffset = 0;
                }
                this.requestFocusInWindow();
                break;
            case PAUSED:
                gamePausedOffset = System.currentTimeMillis();
                menu.activate(true);
                break;
            case MAIN:
                newGame();
                menu.activate(true);
                break;
            case NEW:
                newGame();
                v.insertElementAt(player, 0);
                break;
            case WIN:
                scores[5][1] = calculateScore();
                winDialog.activate(true);
                break;
            case LOSE:
                loseDialog.activate(true);
                break;
            case WRITE:
                scores[5][0] = winDialog.getUser();
                sortScores();
                saveScores();
                break;
            case SCORES:
                fileCreator();
                scoreDisplay.activate(true);
                break;
            case INSTRUCTIONS:
                instDisplay.activate(true);
                break;
        }
    }

    // Pointer used to memorise previous state so the menu knows what state to return to
    public void setPointer() { pointer = state; }

    public State getState() { return state; }
    public State getPointer() { return pointer; }


    /*************************************
     * Thread start method and game loop *
     *************************************/
    @Override
    public void run() {
        long startTime;
        long timeTaken;

        // Main game loop
        while (true) {
            // Time the current loop starts at
            startTime = System.currentTimeMillis();

            // Performs different operations according to the current game state
            switch(state) {
                case PLAYING:
                    updateGame();
                    renderGame();
                    displayGame();
                    break;
                case PAUSED:
                    renderGame();
                    displayGame();
                    menu.paintImmediately(0, 0, MenuDisplay.MWIDTH, MenuDisplay.MHEIGHT);
                    break;
                case MAIN:
                    updateGame();
                    renderGame();
                    displayGame();
                    menu.paintImmediately(0, 0, MenuDisplay.MWIDTH, MenuDisplay.MHEIGHT);
                    break;
                case LOSE:
                    updateGame();
                    renderGame();
                    displayGame();
                    loseDialog.paintImmediately(0, 0, LoseDialog.DWIDTH, LoseDialog.DHEIGHT);
                   break;
                case WIN:
                    renderGame();
                    displayGame();
                    winDialog.paintImmediately(0, 0, WinDialog.DWIDTH, WinDialog.DHEIGHT);
                    break;
                case NEW:
                    setState(State.PLAYING);
                    break;
                case WRITE:
                    setState(State.MAIN);
                    break;
                case SCORES:
                    renderGame();
                    displayGame();
                    scoreDisplay.paintImmediately(0, 0, ScoreDisplay.DWIDTH, ScoreDisplay.DHEIGHT);
                    break;
                case INSTRUCTIONS:
                    renderGame();
                    displayGame();
                    instDisplay.paintImmediately(0, 0, InstDisplay.DWIDTH, InstDisplay.DHEIGHT);
                    break;
            }
            // Time taken for the game loop
            timeTaken = System.currentTimeMillis() - startTime;
            try {
                // If the time taken for loop is less than time specified for a frame then make
                // the thread sleep for the remainder of the time
                if (timeTaken < LOOP_DELAY) {
                    Thread.sleep(LOOP_DELAY - timeTaken);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /*******************************************************************
     * Creaets objects a new game - called upon activation of new game *
     *******************************************************************/
    private void newGame() {
        // Creates Vector v with 400 capacity and an increase rate of 4
        v = new Vector<Particle>(400, 4);

        // Creates player Particle set to point (0,0) with radius 20
        player = new Player(0, 0, 0, 0, 20);

        // Loop to create 399 other Particles
        // Adds Particles to Vector
        for (int i = 0; i < 400; i++) {
            v.add(i, new Particle());
            Particle p = v.get(i);
            p.setCentreX(rnd.nextInt(BOUNDX) - BOUNDX / 2);
            p.setCentreY(rnd.nextInt(BOUNDY) - BOUNDY / 2);
            if (Math.abs(p.getCentreX()) < 60 && Math.abs(p.getCentreY()) < 60) {
                i--;
                continue;
            }
            p.setVelX(rnd.nextDouble() * 5 - 2.5); //7-3.5
            p.setVelY(rnd.nextDouble() * 5 - 2.5);
            p.setRadius(rnd.nextDouble() * 21.5); //22
        }
        gameStartTimer = System.currentTimeMillis();
        gamePausedOffset = 0;
    }

    /*********************************************************
     * Move and collide game objects - called from game loop *
     *********************************************************/
    private void updateGame() {

        if ( (mouseEvent != null) && (state != State.MAIN) ) {
            movePlayer();
        }

        double biggestR = 0;
        Particle biggestP = null;
        int i = 0;
        int j;
        Particle pi;
        Particle pj;
        // Loop for each particle instance(i) in the Vector
        iLoop:
        while (elementExists(i)) {    // Loop again if Vector element i exists
            pi = v.get(i);    // Get current iteration of Particle instance
            j = i + 1;

            pi.updatePosition(BOUNDX, BOUNDY);
            // If current particle is the biggest set the pointers to it
            if (pi.getRadius() > biggestR) {
                biggestR = pi.getRadius();
                biggestP = pi;
            }
            

            // Loop for each particle instance(j) ahead of i
            jLoop:
            while (elementExists(j)) {    // Loop again if Vector element i exists
                pj = v.get(j);    // Get current iteration of particle instance

                // Checks if pi & pj have an intersection
                if (checkCollision(pi, pj)) {    // true returned indicates a collision has taken place
                    // Check to see if one particle was absorbed
                    if (particleIdead(pi)) {
                        continue iLoop;
                    } else if (particleJdead(pj)) {
                        continue jLoop;
                    }
                }
                j++;
            }
            i++;
        }
        v.trimToSize();
        if (biggestP == player) setState(State.WIN);
    }

    /*****************************************************
     * Create graphics construct - called from game loop *
     *****************************************************/
    private void renderGame() {
        dbImage = new BufferedImage(BGX, BGY, BufferedImage.TYPE_INT_RGB);
        g2d = dbImage.createGraphics();    // Initialise the Graphics2D object
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.getBackground();    // Color
        g2d.fillRect(0, 0, BGX, BGY);    // Draws background as rectangle

        g2d.setColor(Color.black);
        g2d.drawRect(BGX / 2 - BOUNDX / 2, BGY / 2 - BOUNDY / 2, BOUNDX, BOUNDY);    // Draw game field

        g2d.translate(BGX / 2, BGY / 2);    // Translate to centre of game field
        // Draw a cross
        g2d.drawLine(-2, 0, 2, 0);
        g2d.drawLine(0, -2, 0, 2);

        int i = 0;
        if (state == State.PLAYING) {
            i=1;
            player.drawPlayer(g2d);
        }
        Particle pi;
        while (elementExists(i)) {    // For each particle instance after player in Vector v
            pi = v.get(i);
            pi.draw(g2d, player);
            i++;
        }
        // Draw title
        g2d.setColor(Color.black);
        g2d.setFont(new Font("sansserif", Font.PLAIN, 50));
        g2d.drawString("O", (int)player.getCentreX() - 50,
                (int)player.getCentreY() - CHEIGHT / 2 + 45);
        g2d.setFont(new Font("sansserif", Font.BOLD, 20));
        g2d.drawString("rigin", (int)player.getCentreX() - 12,
                (int)player.getCentreY() - CHEIGHT / 2 + 40);
        // Draw authour name
        g2d.setColor(Color.black);
        g2d.setFont(new Font("monospaced", Font.BOLD, 10));
        g2d.drawString("by Calum Frame", (int)player.getCentreX() + CWIDTH/2 - 100,
                (int)player.getCentreY() + CHEIGHT/2 - 15);
        g2d.dispose();    // Frees memory
    }

    /***************************************************
     * Draw graphics to canvas - called from game loop *
     ***************************************************/
    private void displayGame() {
        g = buffer.getDrawGraphics();    // Initialise the Graphics object

        // Works out where to display the background in relation to the player particle
        int imgX = BGX / 2 + (int)player.getCentreX() - CWIDTH / 2;
        int imgY = BGY / 2 + (int)player.getCentreY() - CHEIGHT / 2;

        do {
            g.drawImage(dbImage, -imgX, -imgY, null);    // Draws render to buffer
            if (!buffer.contentsLost()) {
                buffer.show();    // Displays buffer
            }
        } while (buffer.contentsLost());    // If buffer was lost in memory, redraw
        g.dispose();    // Frees memory
    }

    /**********************************************
     * Checks for collision between p[i] and p[j] *
     * Called from updateGame()                   *
     **********************************************/
    private boolean checkCollision(Particle pi, Particle pj) {
        // Check to see they are not the same particle
        if (pi != pj) {
            double totalR = pi.getRadius() + pj.getRadius();    // Combined radii
            double x = pi.getCentreX() - pj.getCentreX();    // Particle x position
            double y = pi.getCentreY() - pj.getCentreY();    // Particle y position
            double distSQ = x * x + y * y;    // Distance (squared) in game between centres

            if (distSQ >= totalR * totalR) {
                return false;    // No collision
            } else {
                // How much the particles intersect by
                double intersect = totalR - Math.sqrt(distSQ);
                resolveCollision(pi, pj, intersect);
                return true;
            }
        } else {
            return false;
        }
    }

    /***************************************************
     * Absorb particles - called from checkCollision() *
     ***************************************************/
    private void resolveCollision(Particle pi, Particle pj, double intersect) {
        // Larger Particle absorbs the 'mass' smaller Particle loses
        if (pi.getRadius() > pj.getRadius()) {
            pi.incRadius(intersect/9);
            pj.incRadius(-intersect/2);
        } else if (pj.getRadius() >= pi.getRadius()) {
            pj.incRadius(intersect/9);
            pi.incRadius(-intersect/2);
        }
        // Slows player Particle during collisions
        if (pi == player) {
            player.setVelX(player.getVelX() * 0.96);
            player.setVelY(player.getVelY() * 0.96);
        }
    }

    private boolean elementExists(int i) {
        try {
            v.get(i);
            // If element does not exist an error is thrown and caught
        } catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
        return true;
    }

    private boolean particleIdead(Particle pi) {
        if (pi.getRadius() < Particle.MIN_RADIUS) {
            pi.setDead();
            // If player Particle dies: game is over
            if (pi == player) {
                setState(State.LOSE);
            }
            v.remove(pi);    // Remove pi from the Vector
            return true;
        }
        else return false;
    }

    private boolean particleJdead(Particle pj) {
        if (pj.getRadius() < Particle.MIN_RADIUS) {
            pj.setDead();
            v.remove(pj);    // Remove pj from the Vector
            return true;
        }
        else return false;
    }

    // Called from updateGame
    private void movePlayer() {
        player.move(mouseEvent);
        // Creates impulse stream of Particles
        v.add(player.impulseParticles(mouseEvent));
    }

    // Intercepts the processing of mouseEvent to a listener
    @Override
    protected void processMouseEvent(MouseEvent e) {
        if ((e.getID() == MouseEvent.MOUSE_PRESSED) || (e.getID() == MouseEvent.MOUSE_DRAGGED)) {
            // Sets the global mouseEvent variable to the current MouseEvent
            mouseEvent = e;
        } else if (e.getID() == MouseEvent.MOUSE_RELEASED) {
            // Sets the global mouseEvent variable to null
            mouseEvent = null;
        }
    }
    @Override
    protected void processMouseMotionEvent(MouseEvent e) {
        // If mouse is moved calls the MouseEvent method
        processMouseEvent(e);
    }

    @Override
    protected void processKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            switch (e.getKeyCode()) {
                // Pauses game if Esc or P is pressed
                case KeyEvent.VK_ESCAPE:
                case KeyEvent.VK_P:
                    switch (state) {
                        case PLAYING:
                            setState(State.PAUSED);
                            break;
                    }
                    break;
            }
        }
    }

    private String calculateScore() {
        int score = 0;
        int time = (int)(System.currentTimeMillis()-gameStartTimer)/1000;
        score = 360-time*3;
        return Integer.toString(score);
    }

    /***************************************
     * Creates and/or reads the scores.txt *
     ***************************************/
    private void fileCreator() {
        // Where the file will be stored
        String path = System.getProperty("user.home");
        File fileDir = new File(path, "Origin");
        fileDir.mkdir();
        file = new File(fileDir, "scores.txt");
        try {
            if (file.createNewFile()) {     // Create file if it doesn't exist
                // Write 5 blank scores if new file was created
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                for (int i=0; i<NUM_SCORES; i++) {
                    for (int j=0; j<2; j++) {
                        if (j==0) writer.write("undefined");
                        else writer.write("0");
                        writer.newLine();
                    }
                }
                writer.close();
                file.setReadOnly();
            }
            // Read to scores[] the 5 scores
            BufferedReader reader = new BufferedReader(new FileReader(file));
            for (int i=0; i<NUM_SCORES; i++) {
                for (int j=0; j<2; j++) {
                    scores[i][j] = reader.readLine();
                }
            }
            scores[NUM_SCORES][0] = "undefined";
            scores[NUM_SCORES][1] = "0";
        }
        catch(IOException ex) { }
    }

    /*************************
     * Sort scores ascending *
     *************************/
    private void sortScores() {
        boolean swaps;
        // Performs a bubble sort to sort the scores in descending order
        do {
            swaps = false;
            for (int i=NUM_SCORES; i>0; i--) {
                int j = i-1;
                if (Integer.parseInt(scores[i][1]) > Integer.parseInt(scores[j][1])) {
                    String tempNum = scores[j][1];
                    String tempName = scores[j][0];
                    scores[j][1] = scores[i][1];
                    scores[j][0] = scores[i][0];
                    scores[i][1] = tempNum;
                    scores[i][0] = tempName;
                    swaps = true;
                }
            }
        } while (swaps == true);
    }

    /***********************************
     * Writes scores[][] to scores.txt *
     ***********************************/
    private void saveScores() {
        try {
            file.setWritable(true);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (int i=0; i<NUM_SCORES; i++) {
                for (int j=0; j<2; j++) {
                    writer.write(scores[i][j]);
                    writer.newLine();
                }
            }
            writer.close();
            file.setReadOnly();
        }
        catch (IOException ex) { }
        catch (NumberFormatException ex) {
            System.out.println("File Corrupt");
        }
    }

    // Returns scores String array - called from ScoreDisplay
    public String[][] getScores() { return scores; }
}
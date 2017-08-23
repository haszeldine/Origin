package origin;

import java.awt.*;
import java.awt.event.*;
import displayPanel.*;
import javax.swing.*;

public class Origin extends JFrame {

    // game objects Origin creates
    private OriginGame game;
    private MenuDisplay menu;
    private LoseDialog loseDialog;
    private WinDialog winDialog;
    private ScoreDisplay scoreDisplay;
    private InstDisplay instDisplay;

    /***************
     * Constructor *
     ***************/
    public Origin() {
        super("Origin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createGame();
        pack();
        setResizable(false);
        setIgnoreRepaint(true);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        setLocationRelativeTo(null);
        setVisible(true);
        game.startGameEngine();    // calls the method to start the game in OriginGame
    }

    /***********************************************************
     * Main method - first method called upon program start-up *
     ***********************************************************/
    public static void main(String[] args) {
        // Creates instance of Origin
        new Origin();
    }

    /******************************************************
     * Creates the game objects - called from constructor *
     ******************************************************/
    public void createGame() {
        // JLayeredPane allows panels to be on top of each other: needed for menus etc
        JLayeredPane lp = new JLayeredPane();
        lp.setPreferredSize(new Dimension(OriginGame.CWIDTH, OriginGame.CHEIGHT));

        game = new OriginGame(this);
        menu = new MenuDisplay(this);
        loseDialog = new LoseDialog(this);
        winDialog = new WinDialog(this);
        scoreDisplay = new ScoreDisplay(this);
        instDisplay = new InstDisplay(this);

        lp.add(game, new Integer(0));
        lp.add(menu, new Integer(100));
        lp.add(loseDialog, new Integer(100));
        lp.add(winDialog, new Integer(100));
        lp.add(scoreDisplay, new Integer(100));
        lp.add(instDisplay, new Integer(100));
        // Adds everything to the JFrame
        setContentPane(lp);
    }

    // Accessor methods
    public MenuDisplay getMenuDisplay() { return menu; }
    public OriginGame getGame() { return game; }
    public LoseDialog getLoseDialog() { return loseDialog; }
    public WinDialog getWinDialog() { return winDialog; }
    public ScoreDisplay getScoreDisplay() { return scoreDisplay; }
    public InstDisplay getInstDisplay() { return instDisplay; }

    @Override
    protected void processWindowEvent(WindowEvent e) {
         switch(e.getID()) {
             case WindowEvent.WINDOW_DEACTIVATED:
             // Useful in case user navigates to a different window; pauses the game automatically
             case WindowEvent.WINDOW_ICONIFIED:
                 super.processWindowEvent(e);
                 if (game.getState() == OriginGame.State.PLAYING) {
                    game.setState(OriginGame.State.PAUSED);
                 }
                 break;
             default:
                 super.processWindowEvent(e);
                 break;
         }
     }
}
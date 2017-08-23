package displayPanel;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import origin.Origin;
import origin.OriginGame;
import origin.OriginGame.State;

// Subclass of DisplayPanel
public class MenuDisplay extends DisplayPanel {

    Origin origin;

    public static final int MWIDTH = 120;
    public static final int MHEIGHT = 140;
    static final Border lineBorder = BorderFactory.createLineBorder(Color.black, 2);
    static final Border border = BorderFactory.createTitledBorder(lineBorder,
            "Menu", TitledBorder.CENTER, TitledBorder.TOP);

    JButton bt1, bt2, bt3, bt4;

    /***************
     * Constructor *
     ***************/
    public MenuDisplay(Origin origin) {
        // Call to superclass' constructor
        super(OriginGame.CWIDTH/2-MWIDTH/2,
                OriginGame.CHEIGHT/2-MHEIGHT/2, MWIDTH, MHEIGHT, border);
        this.origin = origin;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        bt1 = new JButton();
        bt1.setBorderPainted(false);
        bt1.setFocusPainted(false);
        bt1.setBackground(Color.white);
        bt1.setAlignmentX(CENTER_ALIGNMENT);
        bt1.addActionListener(this);
        this.add(bt1);

        bt2 = new JButton("Scores");
        bt2.setBorderPainted(false);
        bt2.setFocusPainted(false);
        bt2.setBackground(Color.white);
        bt2.setAlignmentX(CENTER_ALIGNMENT);
        bt2.addActionListener(this);
        this.add(bt2);

        bt3 = new JButton("Instructions");
        bt3.setBorderPainted(false);
        bt3.setFocusPainted(false);
        bt3.setBackground(Color.white);
        bt3.setAlignmentX(CENTER_ALIGNMENT);
        bt3.addActionListener(this);
        this.add(bt3);

        bt4 = new JButton();
        bt4.setBorderPainted(false);
        bt4.setFocusPainted(false);
        bt4.setBackground(Color.white);
        bt4.setAlignmentX(CENTER_ALIGNMENT);
        bt4.addActionListener(this);
        this.add(bt4);

        setVisible(false);
        setFocusable(true);
    }

    /*********************************************************************************
     * Activates the display panel - called from originGame.setState(MAIN or PAUSED) *
     *********************************************************************************/
    @Override
    public void activate(boolean b) {
        if (b == true) {
            // Menu has a different appearence depending upon what state the game is in
            switch(origin.getGame().getState()) {
                case MAIN:
                    Border mainBorder = BorderFactory.createTitledBorder(lineBorder,
                            "Main", TitledBorder.CENTER, TitledBorder.TOP);
                    setBorder(mainBorder);
                    bt1.setText("New");
                    bt4.setText("Quit");
                    this.validate();
                    setVisible(true);
                    requestFocusInWindow();
                    break;
                case PAUSED:
                    Border pauseBorder = BorderFactory.createTitledBorder(lineBorder,
                            "Paused", TitledBorder.CENTER, TitledBorder.TOP);
                    setBorder(pauseBorder);
                    bt1.setText("Resume");
                    bt4.setText("End");
                    this.validate();
                    setVisible(true);
                    requestFocusInWindow();
                    break;
            }
        }
        else setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bt1) {
            // Button performs different actions depending on current game state
            switch (origin.getGame().getState()) {
                case MAIN:
                    // Creates a new game if bt1 is clicked
                    setVisible(false);
                    origin.getGame().setState(State.NEW);
                    break;
                case PAUSED:
                    // Resumes the game if bt1 was clicked
                    setVisible(false);
                    origin.getGame().setState(State.PLAYING);
                    break;
            }
        }
        // Opens the high scores window if bt2 was clicked
        else if (e.getSource() == bt2) {
            // Sets a pointer to the game state preceding SCORES so it can return to it
            origin.getGame().setPointer();
            setVisible(false);
            origin.getGame().setState(State.SCORES);
        }
        else if (e.getSource() == bt3) {
            // Sets a pointer to the game state preceding INSTRUCTIONS so it can return to it
            origin.getGame().setPointer();
            setVisible(false);
            origin.getGame().setState(State.INSTRUCTIONS);
        }
        else if (e.getSource() == bt4) {
            // Button performs different actions depending on current game state
            switch (origin.getGame().getState()) {
                case PAUSED:
                    // Sets game state to MAIN if bt4 was clicked
                    setVisible(false);
                    origin.getGame().setState(State.MAIN);
                    break;
                case MAIN:
                    // Ends the program and removes it from active memory
                    System.exit(0);
                    break;
            }
        }
    }
}
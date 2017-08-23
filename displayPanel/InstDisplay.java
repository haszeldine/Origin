package displayPanel;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import origin.Origin;
import origin.OriginGame;

// Subclass of DisplayPanel
public class InstDisplay extends DisplayPanel {

    private final Origin origin;

    public static final int DWIDTH = 300;
    public static final int DHEIGHT = 300;
    static final Border lineBorder = BorderFactory.createLineBorder(Color.black, 2);
    static final Border border = BorderFactory.createTitledBorder(lineBorder,
            "Instructions", TitledBorder.CENTER, TitledBorder.TOP);

    JButton bt;
    String instructions;
    JTextArea textArea;

    /***************
     * Constructor *
     ***************/
    public InstDisplay(Origin origin) {
        // Call to superclass' constructor
        super(OriginGame.CWIDTH/2-DWIDTH/2,
                OriginGame.CHEIGHT/2-DHEIGHT/2, DWIDTH, DHEIGHT, border);
        this.origin = origin;
        new BoxLayout(this, BoxLayout.Y_AXIS);

        instructions = "THE MENU\n\n"
                + "The menu can be accessed during game play by pressing the 'escape' key "
                + "on your keyboard. There are two different menus for the pause screen and main "
                + "respectively."
                + "\n\nTHE INTERFACE\n\n"
                + "On the screen you will see the player particle in the centre of the "
                + "screen. The player is coloured purple. Other particles will float around "
                + "and change colour depending on their size relative to the player. "
                + "Particles larger than the player will appear in red, those smaller in blue. "
                + "The game has rectangluar boundaries which the player will encounter. These "
                + "are displayed as black lines."
                + "\n\nHOW TO PLAY\n\n"
                + "The user places their mouse pointer in the direction they wish their particle "
                + "to travel in and clicks. You may hold on to the mouse button to increase "
                + "acceleration. Your particle will decrease in size proportional to how often "
                + "and fast you move. Smaller particles will be expelled to propel your particle "
                + "forwards."
                + "\n\nWhen you come into contact with a particle smaller than you (coloured blue) "
                + "you will absorb it and hence grow larger. If your particle collides with a "
                + "larger particle (coloured red) however, it will absorb you and you will shrink."
                + "The object of the game is to become the largest particle in the game field. "
                + "If your particle becomes too small you will lose the game.";

        textArea = new JTextArea(instructions, 5, 20);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(230, 230));
        textArea.setEditable(false);
        this.add(scrollPane);

        bt = new JButton("Return");
        bt.setBorderPainted(false);
        bt.setFocusPainted(false);
        bt.setAlignmentX(CENTER_ALIGNMENT);
        bt.setBackground(Color.white);
        bt.addActionListener(this);
        this.add(bt);

        setVisible(false);
    }

    /*******************************************************************************
     * Activates the display panel - called from originGame.setState(INSTRUCTIONS) *
     *******************************************************************************/
    @Override
    public void activate(boolean b) {
        if (b == true) {
            setVisible(true);
            textArea.requestFocusInWindow();
        }
        else setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Return to the menu state the game was on previously upon button click
        if (e.getSource() == bt) {
            setVisible(false);
            origin.getGame().setState(origin.getGame().getPointer());
        }
    }
}

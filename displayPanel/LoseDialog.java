package displayPanel;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.border.*;

import origin.*;
import origin.OriginGame.State;

// Subclass of DisplayPanel
public class LoseDialog extends DisplayPanel {

    private final Origin origin;

    public static final int DWIDTH = 150;
    public static final int DHEIGHT = 90;
    static final Border lineBorder = BorderFactory.createLineBorder(Color.black, 2);
    static final Border border = BorderFactory.createTitledBorder(lineBorder,
            "Game Over!", TitledBorder.CENTER, TitledBorder.TOP);

    private JLabel label;
    JButton yes, no;
    JPanel answerPane;

    /***************
     * Constructor *
     ***************/
    public LoseDialog(Origin origin) {
        // Call to superclass' constructor
        super(OriginGame.CWIDTH/2-DWIDTH/2,
                OriginGame.CHEIGHT/2-DHEIGHT/2, DWIDTH, DHEIGHT, border);
        this.origin = origin;
        new BoxLayout(this, BoxLayout.Y_AXIS);

        label = new JLabel("Play again?");
        label.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        yes = new JButton("Yes");
        yes.setBorderPainted(false);
        yes.setFocusPainted(false);
        yes.setBackground(Color.white);
        yes.addActionListener(this);

        no = new JButton("No");
        no.setBorderPainted(false);
        no.setFocusPainted(false);
        no.setBackground(Color.white);
        no.addActionListener(this);

        answerPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
        answerPane.setAlignmentX(CENTER_ALIGNMENT);
        answerPane.setBackground(Color.white);
        answerPane.add(yes);
        answerPane.add(no);

        add(label);
        add(answerPane);
        setVisible(false);
    }

    /***********************************************************************
     * Activates the display panel - called from originGame.setState(LOSE) *
     ***********************************************************************/
    @Override
    public void activate(boolean b) {
        if (b == true) {
            setVisible(true);
            yes.requestFocusInWindow();
        }
        else setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Starts a new game if the 'yes' button was clicked
        if (e.getSource() == yes) {
             setVisible(false);
             origin.getGame().setState(State.NEW);
         }
        // Sets the game state to MAIN if 'no' was clicked
         else if (e.getSource() == no) {
             setVisible(false);
             origin.getGame().setState(State.MAIN);
         }
    }
}

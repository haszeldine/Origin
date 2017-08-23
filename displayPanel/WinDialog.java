package displayPanel;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import origin.*;
import origin.OriginGame.State;

// Subclass of DisplayPanel
public class WinDialog extends DisplayPanel {

    private final Origin origin;

    public static final int DWIDTH = 200;
    public static final int DHEIGHT = 90;
    static final Border lineBorder = BorderFactory.createLineBorder(Color.black, 2);
    static final Border border = BorderFactory.createTitledBorder(lineBorder,
            "You Won", TitledBorder.CENTER, TitledBorder.TOP);

    private JLabel label;
    private final JTextField tField;
    private final JButton submit;
    private final JPanel subPane;
    private String user;

    /***************
     * Constructor *
     ***************/
    public WinDialog(Origin origin) {
        // Call to superclass' constructor
        super(OriginGame.CWIDTH/2-DWIDTH/2,
                OriginGame.CHEIGHT/2-DHEIGHT/2, DWIDTH, DHEIGHT, border);
        this.origin = origin;
        new BoxLayout(null, BoxLayout.Y_AXIS);

        label = new JLabel("Enter name...");
        label.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        tField = new JTextField(8);
        tField.setEditable(true);
        tField.addActionListener(this);

        submit = new JButton("Submit");
        submit.setBorderPainted(false);
        submit.setFocusPainted(false);
        submit.setBackground(Color.white);
        submit.addActionListener(this);

        subPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
        subPane.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        subPane.setBackground(Color.white);
        subPane.add(tField);
        subPane.add(submit);

        add(label);
        add(subPane);
        setVisible(false);
    }

    // Used to return the current user from the storing variable - called from orginGame
    public String getUser() { return user; }

    /**********************************************************************
     * Activates the display panel - called from originGame.setState(WIN) *
     **********************************************************************/
    @Override
    public void activate(boolean b) {
        if (b == true) {
            setVisible(true);
            tField.setText("");
            tField.requestFocusInWindow();
        }
        else setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // If the 'submit' button is pressed Enter is pressed on the keyboard with
        // tField being the focussed object
        if ( (e.getSource() == submit) || (e.getSource() == tField) ) {
            // Sets the store variable user to the text that was entered
            user = tField.getText();
            setVisible(false);
            // Sets the game state to WRITE
            origin.getGame().setState(State.WRITE);
        }
    }
}
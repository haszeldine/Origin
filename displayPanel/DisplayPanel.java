package displayPanel;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

public abstract class DisplayPanel extends JPanel implements ActionListener {

    /***************
     * Constructor *
     ***************/
    DisplayPanel(int x, int y, int width, int height, Border border) {
        setLocation(x, y);
        setSize(width, height);
        setBackground(Color.white);
        setBorder(border);
        setIgnoreRepaint(true);
        setVisible(false);
        setFocusable(true);
    }

    @Override
    public void addNotify() {
        super.addNotify();   // creates the peer
    }

    // Abstract method for subclass to implement
    public abstract void activate(boolean b);

    // Abstract method for subclass to implement
    public abstract void actionPerformed(ActionEvent e);
}
package displayPanel;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import origin.Origin;
import origin.OriginGame;

// Subclass of DisplayPanel
public class ScoreDisplay extends DisplayPanel {

    private final Origin origin;

    public static final int DWIDTH = 200;
    public static final int DHEIGHT = 280;
    static final Border lineBorder = BorderFactory.createLineBorder(Color.black, 2);
    static final Border border = BorderFactory.createTitledBorder(lineBorder,
            "Scores", TitledBorder.CENTER, TitledBorder.TOP);

    JLabel nameL, scoreL;
    JPanel labelP, pane0, pane1, pane2, pane3, pane4;
    JTextField name0, score0, name1, score1, name2, score2, name3, score3, name4, score4;
    JButton bt;
    String[][] scores;

    /***************
     * Constructor *
     ***************/
    public ScoreDisplay(Origin origin) {
        super(OriginGame.CWIDTH/2-DWIDTH/2,
                OriginGame.CHEIGHT/2-DHEIGHT/2, DWIDTH, DHEIGHT, border);
        this.origin = origin;
        new BoxLayout(this, BoxLayout.Y_AXIS);

        nameL = new JLabel("Name       ");
        scoreL = new JLabel("       Score");
        labelP = new JPanel(new FlowLayout(FlowLayout.CENTER));
        labelP.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        labelP.setBackground(Color.white);
        labelP.add(nameL);
        labelP.add(scoreL);

        name0 = new JTextField(8);
        name0.setEditable(false);
        score0 = new JTextField(3);
        score0.setEditable(false);
        pane0 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pane0.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        pane0.setBackground(Color.white);
        pane0.add(name0);
        pane0.add(score0);

        name1 = new JTextField(8);
        name1.setEditable(false);
        score1 = new JTextField(3);
        score1.setEditable(false);
        pane1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pane1.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        pane1.setBackground(Color.white);
        pane1.add(name1);
        pane1.add(score1);

        name2 = new JTextField(8);
        name2.setEditable(false);
        score2 = new JTextField(3);
        score2.setEditable(false);
        pane2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pane2.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        pane2.setBackground(Color.white);
        pane2.add(name2);
        pane2.add(score2);

        name3 = new JTextField(8);
        name3.setEditable(false);
        score3 = new JTextField(3);
        score3.setEditable(false);
        pane3 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pane3.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        pane3.setBackground(Color.white);
        pane3.add(name3);
        pane3.add(score3);

        name4 = new JTextField(8);
        name4.setEditable(false);
        score4 = new JTextField(3);
        score4.setEditable(false);
        pane4 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pane4.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        pane4.setBackground(Color.white);
        pane4.add(name4);
        pane4.add(score4);

        bt = new JButton("Return");
        bt.setBorderPainted(false);
        bt.setFocusPainted(false);
        bt.setAlignmentX(CENTER_ALIGNMENT);
        bt.setBackground(Color.white);
        bt.addActionListener(this);

        add(labelP);
        add(pane0);
        add(pane1);
        add(pane2);
        add(pane3);
        add(pane4);
        add(bt);

        setVisible(false);
    }

    /*************************************************************************
     * Activates the display panel - called from originGame.setState(SCORES) *
     *************************************************************************/
    @Override
    public void activate(boolean b) {
        if (b == true) {
            // Gets the current score String array
            scores = origin.getGame().getScores();

            // Sets the labels' text to the scores
            name0.setText(scores[0][0]);
            score0.setText(scores[0][1]);
            name1.setText(scores[1][0]);
            score1.setText(scores[1][1]);
            name2.setText(scores[2][0]);
            score2.setText(scores[2][1]);
            name3.setText(scores[3][0]);
            score3.setText(scores[3][1]);
            name4.setText(scores[4][0]);
            score4.setText(scores[4][1]);

            validate();
            setVisible(true);
            bt.requestFocusInWindow();
        }
        else setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Return to previous game state if clicked
        if (e.getSource() == bt) {
            setVisible(false);
            origin.getGame().setState(origin.getGame().getPointer());
        }
    }
}

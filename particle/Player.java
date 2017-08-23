package particle;

import origin.*;
import java.awt.event.MouseEvent;

public class Player extends Particle {

    public static final double IMPULSE_SHRINK = 0.998;
    private static final double VEL_INC = 0.4;

    public Player(double x, double y, double xx, double yy, double r) {
        super(x, y, xx, yy, r);
    }

    /**************************************************************************
     * Sets particle movement variables - called from originGame.movePlayer() *
     **************************************************************************/
    public void move(MouseEvent e) {
        double clickX = (double)e.getX() - OriginGame.CWIDTH/2;
        double clickY = (double)e.getY() - OriginGame.CHEIGHT/2;
        double hyp = Math.sqrt(clickX*clickX + clickY*clickY);

        double xRatio = clickX/hyp;
        this.incVelX(xRatio*VEL_INC);

        double yRatio = clickY/hyp;
        this.incVelY(yRatio*VEL_INC);

        this.setRadius((this.getRadius() * Player.IMPULSE_SHRINK));
    }

    /*********************************************************************************
     * Creates a new particle as expelled from parent particle and return the object *
     * Called from originGame.movePlayer()                                           *
     *********************************************************************************/
    public Particle impulseParticles(MouseEvent e) {
        // Translates click position to an origin in the centre of the canvas
        // Therefore in sync with player particle's co-ordinate system
        double clickX = (double)e.getX() - OriginGame.CWIDTH/2;
        double clickY = (double)e.getY() - OriginGame.CHEIGHT/2;

        double hyp = Math.sqrt(clickX*clickX+clickY*clickY);    // hypotenuse of p to click
        double k = hyp/this.getRadius();    // scale of p radius in relation to click hypotenuse

        // New particle's (x,y) position based on parent particle's direction of travel and radius
        double newPx;
        double newPy;
        if(clickX > this.getCentreX()) {
            newPx = (this.getCentreX() - clickX/k)-(1+this.getRadius()*0.03);
        } else {
            newPx = (this.getCentreX() - clickX/k)+(1+this.getRadius()*0.03);
        }

        if(clickY > this.getCentreY()) {
            newPy = (this.getCentreY() - clickY/k)-(1+this.getRadius()*0.03);
        } else {
            newPy = (this.getCentreY() - clickY/k)+(1+this.getRadius()*0.03);
        }

        // New particle's velocity is inverse of parent's
        double newPVx = -(this.velX);
        double newPVy = -(this.velY);

        // Returns the new particle object
        // Radius is 3% of the parent particle
        return new Particle(newPx, newPy, newPVx, newPVy, this.getRadius()*0.03);
    }
}
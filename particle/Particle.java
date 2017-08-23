package particle;

import java.awt.*;
import java.awt.geom.Ellipse2D;


public class Particle {

    // Class Variable
    public static final double MIN_RADIUS = 2.0;

    // Instance variables
    protected double velX, velY;
    protected double centreX, centreY;
    private double radius;

    /****************
     * Constructors *
     ****************/
    public Particle() {
        // Creates empty default Particle
    }
    public Particle(double x, double y, double xx, double yy, double r) {
        // Creates Particle with set attributes
        centreX = x;
        centreY = y;
        velX = xx;
        velY = yy;
        radius = r;
    }


    // Accessor methods
    public double getVelX() { return velX; }
    public double getVelY() { return velY; }
    public double getCentreX() { return centreX; }
    public double getCentreY() { return centreY; }
    public double getRadius() { return radius; }

    // Mutator methods
    public void setVelX(double velX) { this.velX = velX; }
    public void incVelX(double i) { this.velX += i; }
    public void setVelY(double velY) { this.velY = velY; }
    public void incVelY(double i) { this.velY += i; }
    public void setCentreX(double centreX) { this.centreX = centreX; }
    public void setCentreY(double centreY) { this.centreY = centreY; }
    public void setRadius(double radius) { this.radius = radius; }
    public void incRadius(double i) { this.radius += i; }

    public void setDead() {
        this.setCentreX(0);
        this.setCentreY(0);
        this.setRadius(0);
        this.setVelX(0);
        this.setVelY(0);
    }

    /*****************************
     * Updates particle position *
     * ***************************/
    public void updatePosition(int boundX, int boundY) {
        // Moves position based on velocity
        this.centreX += this.velX;
        this.centreY += this.velY;

        // Makes Particle bounce if it reaches x bounds of game field
        if(this.getCentreX()-this.getRadius() <= -(boundX/2)) {
            this.setCentreX(-(boundX/2) + this.getRadius());
            this.setVelX(-(this.getVelX()));
        }
        if(this.getCentreX()+this.getRadius() >= boundX/2) {
            this.setCentreX(boundX/2 - this.getRadius());
            this.setVelX(-(this.getVelX()));
        }

        // Makes Particle bounce if it reaches y bounds of game field
        if(this.getCentreY()-this.getRadius() <= -(boundY/2)) {
            this.setCentreY(-(boundY/2) + this.getRadius());
            this.setVelY(-(this.getVelY()));
        }
        if(this.getCentreY()+this.getRadius() >= boundY/2) {
            this.setCentreY(boundY/2 - this.getRadius());
            this.setVelY(-(this.getVelY()));
        }
    }

    /****************************
     * Draws computer particles *
     * **************************/
    public void draw(Graphics2D g2d, Player player) {
        // Particles larger than player are red
        if(this.getRadius() >= player.getRadius()) {
            g2d.setColor(Color.red);
            g2d.draw( new Ellipse2D.Double(this.getCentreX()-this.getRadius(),
                    this.getCentreY()-this.getRadius(),
            Math.rint(this.getRadius()*2), Math.rint(this.getRadius()*2)) );
        }
        // Particles smaller than the player are blue
        else {
            g2d.setColor(Color.blue);
            g2d.draw( new Ellipse2D.Double(this.getCentreX()-this.getRadius(),
                    this.getCentreY()-this.getRadius(),
            Math.rint(this.getRadius()*2), Math.rint(this.getRadius()*2)) );
        }
    }

    // Player particle is purple
    public void drawPlayer(Graphics2D g2d) {
        Color purple = new Color(200, 0, 255);
        g2d.setColor(purple);
        g2d.draw( new Ellipse2D.Double(this.getCentreX()-this.getRadius(),
                this.getCentreY()-this.getRadius(),
                Math.rint(this.getRadius()*2) , Math.rint(this.getRadius()*2)) );
    }
}

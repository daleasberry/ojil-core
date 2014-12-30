/*
 * Quad.java
 *
 * Created on December 10, 2007, 1:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.github.ojil.core;

/**
 * Quad represents a quadrilateral. The quadrilteral is specified using its four
 * corners.
 * 
 * @author webb
 */
public class Quad {
    Point p[];
    
    public Quad() {
        // default constructor used for overriding class
    }
    
    /**
     * Create a new Quad specifying the four corners.
     * 
     * @param p1
     *            first corner
     * @param p2
     *            second corner
     * @param p3
     *            third corner
     * @param p4
     *            fourth corner
     */
    public Quad(final Point p1, final Point p2, final Point p3, final Point p4) {
        p = new Point[4];
        p[0] = p1;
        p[1] = p2;
        p[2] = p3;
        p[3] = p4;
    }
    
    /**
     * Get bottom-most extent of quadrilateral
     * 
     * @return bottom-most corner y position
     */
    public int getBottom() {
        return Math.max(p[0].getY(), Math.max(p[1].getY(), Math.max(p[2].getY(), p[3].getY())));
    }
    
    /**
     * Return designated corner of quadrilateral
     * 
     * @param nCorner
     *            corner to return, from 0-3
     * @return designated corner of the quadrilateral
     * @throws ImageError
     *             if nCorner &lt; 0 or &gt; 3
     */
    public Point getCorner(final int nCorner) throws ImageError {
        if ((nCorner < 0) || (nCorner > 3)) {
            throw new ImageError(ImageError.PACKAGE.CORE, ErrorCodes.ILLEGAL_PARAMETER_VALUE, new Integer(nCorner).toString(), "0", "3");
            
        }
        return p[nCorner];
    }
    
    /**
     * Return height of quadrilateral
     * 
     * @return height of quadrilateral
     */
    public int getHeight() {
        return getBottom() - getTop();
    }
    
    /**
     * Get leftmost extent of quadrilateral
     * 
     * @return leftmost corner x position
     */
    public int getLeft() {
        return Math.min(p[0].getX(), Math.min(p[1].getX(), Math.min(p[2].getX(), p[3].getX())));
    }
    
    /**
     * Get rightmost extent of quadrilateral
     * 
     * @return rightmost corner x position
     */
    public int getRight() {
        return Math.max(p[0].getX(), Math.max(p[1].getX(), Math.max(p[2].getX(), p[3].getX())));
    }
    
    /**
     * Get top-most extent of quadrilateral
     * 
     * @return top-most corner y position
     */
    public int getTop() {
        return Math.min(p[0].getY(), Math.min(p[1].getY(), Math.min(p[2].getY(), p[3].getY())));
    }
    
    /**
     * Returns maximum width of the quadrilateral
     * 
     * @return width of the quadrilateral
     */
    public int getWidth() {
        return getRight() - getLeft();
    }
    
    public Quad offset(final int nX, final int nY) {
        for (int i = 0; i < 4; i++) {
            p[i].offset(nX, nY);
        }
        return this;
    }
    
    /**
     * Implement toString
     * 
     * @return class name followed by (p1,p2,p3,p4)
     */
    @Override
    public String toString() {
        return super.toString() + "(" + p[0].toString() + "," + p[1].toString() + "," + p[2].toString() + "," + p[3].toString() + ")";
        
    }
}

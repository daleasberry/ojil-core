/*
 * Rect.java
 *
 * Created on December 10, 2007, 1:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.github.ojil.core;

/**
 * Rect represents a rectangular region. The rectangle is specified using its
 * upper left coordinate and size or the upper left and lower right coordinates.
 * Methods allow the addition of a new point to the rectangle, merging
 * rectangles, computing rectangle size, etc.
 * 
 * @author webb
 */
public class Rect {
    protected int nTlx = 0, nTly = 0, nWidth = 0, nHeight = 0;
    
    /** Creates a new instance of Rect */
    public Rect() {
    }
    
    /**
     * Create a new Rect specifying the upper left coordinate and size.
     * 
     * @param nTlx
     *            the upper left x (horizontal) coordinate
     * @param nTly
     *            the upper left y (vertical) coordinate
     * @param nWidth
     *            the width
     * @param nHeight
     *            the height
     */
    public Rect(final int nTlx, final int nTly, final int nWidth, final int nHeight) {
        this.nTlx = nTlx;
        this.nTly = nTly;
        this.nWidth = nWidth;
        this.nHeight = nHeight;
    }
    
    /**
     * Create a new Rect specifying two corners.
     * 
     * @param p1
     *            the first corner.
     * @param p2
     *            the second corner.
     */
    public Rect(final Point p1, final Point p2) {
        nTlx = Math.min(p1.getX(), p2.getX());
        nTly = Math.min(p1.getY(), p2.getY());
        nWidth = Math.max(p1.getX(), p2.getX()) - nTlx;
        nHeight = Math.max(p1.getY(), p2.getY()) - nTly;
    }
    
    /**
     * Create a new Rect (0 width and height) from a single point.
     * 
     * @param p
     *            the point.
     */
    public Rect(final Point p) {
        nTlx = p.getX();
        nTly = p.getY();
        nWidth = 0;
        nHeight = 0;
    }
    
    /**
     * Add a new point to the Rect, extending it if necessary.
     * 
     * @param p
     *            the new Point
     */
    public Rect add(final Point p) {
        if (p.getX() < nTlx) {
            nTlx = p.getX();
        }
        if (p.getY() < nTly) {
            nTly = p.getY();
        }
        nWidth = Math.max(nWidth, p.getX() - nTlx);
        nHeight = Math.max(nHeight, p.getY() - nTly);
        return this;
    }
    
    /**
     * Test a point for inclusion in a rectangle, including boundaries.
     * 
     * @param p
     *            the point to test
     * @return true iff the point is in the rectangle
     */
    public boolean contains(final Point p) {
        return (p.getX() >= nTlx) && (p.getX() <= (nTlx + nWidth)) && (p.getY() >= nTly) && (p.getY() <= (nTly + nHeight));
    }
    
    /**
     * Expands the rectangle by certain amounts vertically and horizontally
     * 
     * @param nLeft
     *            Amount to add on the left
     * @param nTop
     *            Amount to add on the top
     * @param nRight
     *            Amount to add on the right
     * @param nBottom
     *            Amount to add on the left
     * @return
     */
    public Rect expand(final int nLeft, final int nTop, final int nRight, final int nBottom) {
        nTlx -= nLeft;
        nTly -= nTop;
        nWidth += nLeft + nRight;
        nHeight += nTop + nBottom;
        return this;
    }
    
    /**
     * Return area of the rectangle.
     * 
     * @return the Rect's area.
     */
    public int getArea() {
        return nWidth * nHeight;
    }
    
    public int getBottom() {
        return nTly + nHeight;
    }
    
    public Point getBottomRight() {
        return new Point(getRight(), getBottom());
    }
    
    /**
     * Return the left (horizontal) position of the rectangle.
     * 
     * @return returns the left edge of the rectangle.
     */
    public int getLeft() {
        return nTlx;
    }
    
    /**
     * Return the height of the rectangle.
     * 
     * @return the rectangle's height.
     */
    public int getHeight() {
        return nHeight;
    }
    
    /**
     * Returns rectangle perimeter
     * 
     * @return rectangle perimeter
     */
    public int getPerimeter() {
        return 2 * (getWidth() + getHeight());
    }
    
    public int getRight() {
        return nTlx + nWidth;
    }
    
    /**
     * Return the top (vertical) position of the rectangle.
     * 
     * @return the top (vertical) edge of the rectangle.
     */
    public int getTop() {
        return nTly;
    }
    
    public Point getTopLeft() {
        return new Point(getLeft(), getTop());
    }
    
    /**
     * Return the width of the rectangle.
     * 
     * @return the width of the rectangle.
     */
    public int getWidth() {
        return nWidth;
    }
    
    public Rect offset(final int nX, final int nY) {
        nTlx += nX;
        nTly += nY;
        return this;
    }
    
    public boolean overlaps(final Rect r) {
        if (contains(new Point(r.getLeft(), r.getTop()))) {
            return true;
        }
        if (contains(new Point(r.getRight(), r.getTop()))) {
            return true;
        }
        if (contains(new Point(r.getLeft(), r.getBottom()))) {
            return true;
        }
        if (contains(new Point(r.getRight(), r.getBottom()))) {
            return true;
        }
        if (r.contains(new Point(getLeft(), getTop()))) {
            return true;
        }
        if (r.contains(new Point(getRight(), getTop()))) {
            return true;
        }
        if (r.contains(new Point(getLeft(), getBottom()))) {
            return true;
        }
        if (r.contains(new Point(getRight(), getBottom()))) {
            return true;
        }
        return false;
    }
    
    public Rect scale(final int nX, final int nY) {
        nTlx *= nX;
        nTly *= nY;
        nHeight *= nX;
        nWidth *= nY;
        return this;
    }
    
    @Override
    public String toString() {
        return super.toString() + "(" + new Integer(nTlx).toString() + "," + new Integer(nTly).toString() + ";" + new Integer(nWidth).toString() + "x" + new Integer(nHeight).toString() + ")";
        
    }
}

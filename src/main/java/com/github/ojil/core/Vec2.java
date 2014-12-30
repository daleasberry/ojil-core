/*
 * Copyright 2008 by Jon A. Webb
 *     This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the Lesser GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.ojil.core;

import java.io.Serializable;

/**
 * Implementation of 2-dimensional vector.
 * 
 * @author webb
 */
public class Vec2 implements Serializable {
    private static final long serialVersionUID = -9045538016956571364L;
    
    private int mnX, mnY;
    
    public Vec2(final Point Last) {
        mnX = Last.getX();
        mnY = Last.getY();
    }
    
    /**
     * Create a new Vec2, specifying x and y values
     * 
     * @param nX
     *            x value
     * @param nY
     *            y value
     */
    public Vec2(final int nX, final int nY) {
        mnX = nX;
        mnY = nY;
    }
    
    /**
     * Copy constructor.
     * 
     * @param v
     *            vector to copy.
     */
    public Vec2(final Vec2 v) {
        mnX = v.mnX;
        mnY = v.mnY;
    }
    
    /**
     * Create a new Vec2 extending from one Point (p1) to another (p2).
     * 
     * @param p1
     *            starting Point
     * @param p2
     *            ending Point
     */
    public Vec2(final Point p1, final Point p2) {
        mnX = p2.getX() - p1.getX();
        mnY = p2.getY() - p1.getY();
    }
    
    /**
     * Add one Vec2 to this Vec2, modifying and returning this Vec2.
     * 
     * @param v
     *            Vec2 to add
     * @return modified Vec2
     */
    public Vec2 add(final Vec2 v) {
        mnX += v.mnX;
        mnY += v.mnY;
        return this;
    }
    
    /**
     * Add a vector to a point, returning the point
     * 
     * @param p
     *            point to adjust by this vector
     * @return new point, offset by this Vec2
     */
    public Vec2 add(final Point p) {
        mnX += p.getX();
        mnY += p.getY();
        return this;
    }
    
    /**
     * Add (x,y) to a vector
     * 
     * @param nX
     *            x value to add
     * @param nY
     *            y value to add
     * @return modified vector
     */
    public Vec2 add(final int nX, final int nY) {
        mnX += nX;
        mnY += nY;
        return this;
    }
    
    @Override
    public Vec2 clone() {
        return new Vec2(mnX, mnY);
    }
    
    public int crossMag(final Vec2 v) {
        return (mnX * v.mnY) - (mnY * v.mnX);
    }
    
    /**
     * Divide a Vec2 by a scalar.
     * 
     * @param n
     *            divisor
     * @return modified Vec2.
     */
    public Vec2 div(final int n) {
        mnX /= n;
        mnY /= n;
        return this;
    }
    
    /**
     * Form the scalar dot product of two Vec2's.
     * 
     * @param v
     *            second Vec2.
     * @return dot product of this and the second Vec2.
     */
    public int dot(final Vec2 v) {
        return (mnX * v.mnX) + (mnY * v.mnY);
    }
    
    /**
     * Returns true iff this vector equals the argument
     * 
     * @param o
     *            Vec2 object to compare
     * @return true iff the two vectors are equal
     */
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Vec2)) {
            return false;
        }
        final Vec2 v = (Vec2) o;
        return (mnX == v.mnX) && (mnY == v.mnY);
    }
    
    /**
     * Get X component of the vector
     * 
     * @return x component
     */
    public int getX() {
        return mnX;
    }
    
    /**
     * Get Y component of the vector
     * 
     * @return y component
     */
    public int getY() {
        return mnY;
    }
    
    /**
     * Implement hashcode
     * 
     * @return code which can be used in hash tables
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = (37 * hash) + mnX;
        hash = (37 * hash) + mnY;
        return hash;
    }
    
    /**
     * Calculate length of this Vec2.
     * 
     * @return sqrt(mnX<sup>2</sup> + mnY<sup>2</sup>)
     * @throws ImageError
     *             if sqrt does, due to coding error
     */
    public int length() throws ImageError {
        return MathPlus.sqrt((mnX * mnX) + (mnY * mnY));
    }
    
    public int lengthSqr() {
        return (mnX * mnX) + (mnY * mnY);
    }
    
    public Vec2 lsh(final int n) {
        mnX <<= n;
        mnY <<= n;
        return this;
    }
    
    public Vec2 max(final Vec2 v) {
        mnX = Math.max(mnX, v.mnX);
        mnY = Math.max(mnY, v.mnY);
        return this;
    }
    
    public Vec2 min(final Vec2 v) {
        mnX = Math.min(mnX, v.mnX);
        mnY = Math.min(mnY, v.mnY);
        return this;
    }
    
    public Vec2 rlsh(final int n) {
        mnX >>>= n;
        mnY >>>= n;
        return this;
    }
    
    public Vec2 rsh(final int n) {
        mnX >>= n;
        mnY >>= n;
        return this;
    }
    
    public Vec2 setXY(final int nX, final int nY) {
        mnX = nX;
        mnY = nY;
        return this;
    }
    
    public Vec2 sub(final int x, final int y) {
        mnX -= x;
        mnY -= y;
        return this;
    }
    
    public Vec2 sub(final Vec2 v) {
        mnX -= v.mnX;
        mnY -= v.mnY;
        return this;
    }
    
    /**
     * Multiply a Vec2 by a scalar
     * 
     * @param n
     *            multiplicand
     * @return modified Vec2
     */
    public Vec2 times(final int n) {
        mnX *= n;
        mnY *= n;
        return this;
    }
    
    public Vec2 times(final Vec2 v) {
        mnX *= v.mnX;
        mnY *= v.mnY;
        return v;
    }
}

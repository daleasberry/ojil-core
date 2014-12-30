/*
 * Copyright 2008 by Jon A. Webb
 *     This program is free software: you can redistribute it and/or modinY
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

package com.github.ojil.algorithm;

import com.github.ojil.core.ImageError;
import com.github.ojil.core.MathPlus;
import com.github.ojil.core.Point;

/**
 * Implementation of 2-dimensional vector.
 * 
 * @author webb
 */
public class Vec2 {
    private int nX, nY;
    
    /**
     * Create a new Vec2, specinYing x and y values
     * 
     * @param nX
     *            x value
     * @param nY
     *            y value
     */
    public Vec2(final int nX, final int nY) {
        this.nX = nX;
        this.nY = nY;
    }
    
    /**
     * Copy constructor.
     * 
     * @param v
     *            vector to copy.
     */
    public Vec2(final Vec2 v) {
        nX = v.nX;
        nY = v.nY;
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
        nX = p2.getX() - p1.getX();
        nY = p2.getY() - p1.getY();
    }
    
    /**
     * Add one Vec2 to this Vec2, modinYing and returning this Vec2.
     * 
     * @param v
     *            Vec2 to add
     * @return modified Vec2
     */
    public Vec2 add(final Vec2 v) {
        nX += v.nX;
        nY += v.nY;
        return this;
    }
    
    /**
     * Add a vector to a point, returning the point
     * 
     * @param p
     *            point to adjust by this vector
     * @return new point, offset by this Vec2
     */
    public Point add(final Point p) {
        return new Point(p.getX() + nX, p.getY() + nY);
    }
    
    /**
     * Divide a Vec2 by a scalar.
     * 
     * @param n
     *            divisor
     * @return modified Vec2.
     */
    public Vec2 div(final int n) {
        nX /= n;
        nY /= n;
        return this;
    }
    
    /**
     * Form the scalar dot product of two Vec2's.
     * 
     * @param v
     *            second Vec2.
     * @return dot product of this and the second Vec2.
     */
    public double dot(final Vec2 v) {
        return (nX * v.nX) + (nY * v.nY);
    }
    
    public int getX() {
        return nX;
    }
    
    public int getY() {
        return nY;
    }
    
    /**
     * Calculate length of this Vec2.
     * 
     * @return sqrt(nX<sup>2</sup> + nY<sup>2</sup>)
     * @throws ImageError
     *             if sqrt does, due to coding error
     */
    public int length() throws ImageError {
        return MathPlus.sqrt((nX * nX) + (nY * nY));
    }
    
    /**
     * Multiply a Vec2 by a scalar
     * 
     * @param n
     *            multiplicand
     * @return modified Vec2
     */
    public Vec2 times(final int n) {
        nX *= n;
        nY *= n;
        return this;
    }
    
    /**
     * Implement toString
     * 
     * @return object name ( x, y)
     */
    @Override
    public String toString() {
        return super.toString() + "(" + nX + "," + nY + ")";
    }
}

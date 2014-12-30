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

/**
 * Manage three points in a triangle.
 * 
 * @author webb
 */
public class Triangle {
    private final Point p1, p2, p3;
    private final int l1, l2;
    private final Vec2 v1, v2;
    
    /**
     * Create a new triangle, specifying the corners.
     * 
     * @param p1
     *            first vertex
     * @param p2
     *            second vertex
     * @param p3
     *            third vertex
     */
    public Triangle(final Point p1, final Point p2, final Point p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        v1 = new Vec2(p1, p2);
        l1 = v1.dot(v1);
        v2 = new Vec2(p1, p3);
        l2 = v2.dot(v2);
    }
    
    /**
     * Returns true iff the triangle contains the given point
     * 
     * @param p
     *            Point to test
     * @return true iff p is in the interior of the triangle
     */
    public boolean contains(final Point p) {
        final Vec2 v = new Vec2(p1, p);
        return (v.dot(v1) <= l1) && (v.dot(v2) <= l2);
    }
    
    /**
     * Return first vertex
     * 
     * @return first vertex
     */
    public Point getP1() {
        return p1;
    }
    
    /**
     * Return second vertex
     * 
     * @return second vertex
     */
    public Point getP2() {
        return p2;
    }
    
    /**
     * Return third vertex
     * 
     * @return third vertex
     */
    public Point getP3() {
        return p3;
    }
    
    /**
     * Implement toString
     * 
     * @return String including the triangle vertices
     */
    @Override
    public String toString() {
        return super.toString() + "(" + p1.toString() + "," + p2.toString() + "," + p3.toString() + ")";
    }
}

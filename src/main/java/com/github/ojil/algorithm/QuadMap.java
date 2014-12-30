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

package com.github.ojil.algorithm;

import com.github.ojil.core.ImageError;
import com.github.ojil.core.Point;
import com.github.ojil.core.Quad;
import com.github.ojil.core.Triangle;

/**
 * Maps points in one quadtrilateral into another.
 * <p>
 * 
 * @author webb
 */
public class QuadMap {
    private final TriangleMap t[];
    private final Triangle t1, t2;
    
    /**
     * Create a map for mapping points in one quadrilateral into another. The
     * points of the quadrilaterals must be specified in corresponding order.
     * The quadrilateral map is broken down into two triangular maps and the two
     * maps are not optimized.
     * 
     * @param q1
     *            source map
     * @param q2
     *            target map
     * @throws ImageError
     *             if Triangle does, due to colinearity.
     */
    public QuadMap(final Quad q1, final Quad q2) throws ImageError {
        // initialize the triangle maps.
        t = new TriangleMap[2];
        t1 = new Triangle(q1.getCorner(0), q1.getCorner(1), q1.getCorner(2));
        t[0] = new TriangleMap(t1, new Triangle(q2.getCorner(0), q2.getCorner(1), q2.getCorner(2)));
        t2 = new Triangle(q1.getCorner(2), q1.getCorner(3), q1.getCorner(0));
        t[1] = new TriangleMap(t2, new Triangle(q2.getCorner(2), q2.getCorner(3), q2.getCorner(0)));
    }
    
    /**
     * Map a point in the source quadrilateral into the target quadrilateral.
     * 
     * @param p
     *            Point to map
     * @return mapped Point
     */
    public Point map(final Point p) {
        // first we have to find out if p is in the first triangle or the second
        if (t1.contains(p)) {
            return t[0].map(p);
        } else {
            // we're just assuming its in the second triangle, not testing --
            // should test
            return t[1].map(p);
        }
    }
}

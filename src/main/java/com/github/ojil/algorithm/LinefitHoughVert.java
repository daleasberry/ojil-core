/*
 * LinefitHoughVert.java
 *
 * Created on September 9, 2006, 1:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 *
 * Copyright 2007 by Jon A. Webb
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
 *
 */

package com.github.ojil.algorithm;

import java.util.Vector;

import com.github.ojil.core.ImageError;
import com.github.ojil.core.Point;

/**
 * Finds a line in an array of points using Hough transform. Not a pipeline
 * stage. Returns the most likely line as y-slope and x-intercept through member
 * access functions. The line search for should be oriented more or less
 * vertically (within the slope range specified).
 * <p>
 * Lines are traditionally represented in terms of slope m and x-intercept b,
 * i.e., y = m * x + b. In this code we are concerned with vertical or nearly
 * vertical lines so we change the representation to x = m * y + b. b is then
 * the x-intercept (y = 0 gives x = b) and m is the "y-slope". m = 0 gives a
 * vertical line at x = b.
 * 
 * @author webb
 */
public class LinefitHoughVert {
    /** @var cHoughAccum the Hough accumulator array */
    Integer[][] cHoughAccum;
    /** @var cCount the number of points on the line that was found */
    int cCount = 0;
    /** @var cMaxSlope the maximum allowable slope, times 256 */
    final int cMaxSlope;
    /** @var cMaxX the maximum allowable x-intercept */
    final int cMaxX;
    /** @var cMinSlope the minimum allowable slope, times 256 */
    final int cMinSlope;
    /** @var cMinX the minimum allowable x-intercept */
    final int cMinX;
    /** @var cSlope the slope of the line that was found, times 256 */
    int cSlope;
    /** @var cSteps the number of steps to take from cMinSlope to cMaxSlope */
    final int cSteps;
    /** @var cXInt the x-intercept of the line that was found */
    int cXInt;
    
    /**
     * Creates a new instance of LinefitHoughVert
     *
     * @param cMinX
     *            minimum X value
     * @param cMaxX
     *            maximum X value
     * @param cMinSlope
     *            minimum slope (multiplied by 256)
     * @param cMaxSlope
     *            maximum slope (multiplied by 256)
     * @param cSteps
     *            steps taken in Hough accumulator between minimum and maximum
     *            slope.
     * @throws ImageError
     *             if X or slope range is empty, or cSteps is not positive.
     */
    public LinefitHoughVert(final int cMinX, final int cMaxX, final int cMinSlope, final int cMaxSlope, final int cSteps) throws ImageError {
        if (cMaxX < cMinX) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.PARAMETER_RANGE_NULL_OR_NEGATIVE, new Integer(cMinX).toString(), new Integer(cMaxX).toString(), null);
        }
        this.cMinX = cMinX;
        this.cMaxX = cMaxX;
        if (cMaxSlope < cMinSlope) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.PARAMETER_RANGE_NULL_OR_NEGATIVE, new Integer(cMinSlope).toString(), new Integer(cMaxSlope).toString(), null);
        }
        if (cSteps <= 0) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.PARAMETER_OUT_OF_RANGE, new Integer(cSteps).toString(), new Integer(1).toString(),
                    new Integer(Integer.MAX_VALUE).toString());
        }
        this.cMinSlope = cMinSlope;
        this.cMaxSlope = cMaxSlope;
        this.cSteps = cSteps;
    }
    
    /**
     * Add a new point to the Hough accumulator array. We increment along the
     * line in the array from (cMinSlope>>8, xIntStart) to (cMaxSlope>>8,
     * xIntEnd), where xIntStart is the x-intercept assuming the slope is at the
     * minimum, and xIntEnd is the x-intercept assuming the slope is maximal.
     *
     * @param p
     *            the point to add to the accumulator array
     */
    private void addPoint(final Point p) {
        // Remember the line we are fitting is
        // x = slope * x + intercept
        // compute initial intercept. cMinSlope is the real slope minimum
        // * 256.
        final int xIntStart = ((p.getX() * 256) - (p.getY() * cMinSlope)) / 256;
        // compute final intercept. cMaxSlope is the real slope maximum
        // * 256.
        final int xIntEnd = ((p.getX() * 256) - (p.getY() * cMaxSlope)) / 256;
        /**
         * work along the line from (0,xIntStart) to (cSteps,xIntEnd),
         * incrementing the Hough accumulator.
         */
        for (int slope = 0; slope < cSteps; slope++) {
            final int xInt = (((xIntEnd - xIntStart) * slope) / cSteps) + xIntStart;
            /**
             * check if the current position falls inside the Hough accumulator.
             */
            if ((xInt >= cMinX) && (xInt < cMaxX)) {
                cHoughAccum[slope][xInt - cMinX]++;
            }
        }
        ;
    }
    
    /**
     * Find the peak in the Hough array. Updates cCount, cSlope, and cXInt.
     */
    private void findPeak() {
        cCount = Integer.MIN_VALUE;
        for (int slope = 0; slope < cSteps; slope++) {
            for (int x = 0; x < (cMaxX - cMinX); x++) {
                if (cHoughAccum[slope][x] > cCount) {
                    cCount = cHoughAccum[slope][x];
                    cSlope = ((slope * (cMaxSlope - cMinSlope)) / cSteps) + cMinSlope;
                    cXInt = x + cMinX;
                }
            }
        }
    }
    
    /**
     * Returns the count of points on the line that was found.
     *
     * @return the point count.
     */
    public int getCount() {
        return cCount;
    }
    
    /**
     * Returns the y-slope of the line that was found.
     *
     * @return the line slope (*256)
     */
    public int getSlope() {
        return cSlope;
    }
    
    /**
     * Returns the x-intercept of the line that was found.
     *
     * @return the x-intercept.
     */
    public int getX() {
        return cXInt;
    }
    
    /**
     * Finds the most likely line passing through the points in the Vector.
     * 
     * @param points
     *            the input Vector of point positions
     * @throws ImageError
     *             if points is not a Vector of point objects.
     */
    public void push(final Vector<?> points) throws ImageError {
        /* create Hough accumulator */
        cHoughAccum = new Integer[cSteps][cMaxX - cMinX];
        /*
         * fill the Hough accumulator
         */
        for (final Object o : points) {
            if (!(o instanceof Point)) {
                throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.OBJECT_NOT_EXPECTED_TYPE, o.toString(), "Point", null);
            }
            final Point p = (Point) o;
            addPoint(p);
        }
        findPeak(); // sets cXInt, cSlope, cCount for access by caller
        cHoughAccum = null; // free memory
    }
    
    /**
     * Return a string describing the current instance, giving the values of the
     * constructor parameters.
     *
     * @return the string describing the current instance.
     */
    @Override
    public String toString() {
        return super.toString() + "(" + cMinX + "," + cMaxX + "," + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                cMinSlope + "," + cMaxSlope + "," + cSteps + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
}

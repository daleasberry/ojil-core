/*
 * LinefitHoughHoriz.java
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
 * stage. Returns the most likely line as slope and Y-intercept through member
 * access functions. The line search for must be oriented more or less
 * horizontally (within the slope range specified).
 * 
 * @author webb
 */
public class LinefitHoughHoriz {
    /** @var cHoughAccum the Hough accumulator array */
    Integer[][] cHoughAccum;
    /** @var cCount the number of points on the line that was found */
    int cCount = 0;
    /** @var cMaxSlope the maximum allowable slope, times 256 */
    final int cMaxSlope;
    /** @var cMaxY the maximum allowable y-intercept */
    final int cMaxY;
    /** @var cMinSlope the minimum allowable slope, times 256 */
    final int cMinSlope;
    /** @var cMinY the minimum allowable y-intercept */
    final int cMinY;
    /** @var cSlope the slope of the line that was found, times 256 */
    int cSlope;
    /** @var cSteps the number of steps to take from cMinSlope to cMaxSlope */
    final int cSteps;
    /** @var cYInt the y-intercept of the line that was found */
    int cYInt;
    
    /**
     * Creates a new instance of LinefitHoughHoriz
     *
     * @param cMinY
     *            minimum Y value
     * @param cMaxY
     *            maximum Y value
     * @param cMinSlope
     *            minimum slope (multiplied by 256)
     * @param cMaxSlope
     *            maximum slope (multiplied by 256)
     * @param cSteps
     *            steps taken in Hough accumulator between minimum and maximum
     *            slope.
     * @throws ImageError
     *             if Y or slope range is empty, or cSteps is not positive.
     */
    public LinefitHoughHoriz(final int cMinY, final int cMaxY, final int cMinSlope, final int cMaxSlope, final int cSteps) throws ImageError {
        if (cMaxY < cMinY) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.PARAMETER_RANGE_NULL_OR_NEGATIVE, new Integer(cMinY).toString(), new Integer(cMaxY).toString(), null);
        }
        this.cMinY = cMinY;
        this.cMaxY = cMaxY;
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
     * line in the array from (cMinSlope>>8, yIntStart) to (cMaxSlope>>8,
     * yIntEnd), where yIntStart is the y-intercept assuming the slope is at the
     * minimum, and yIntEnd is the y-intercept assuming the slope is maximal.
     *
     * @param p
     *            the point to add to the accumulator array
     */
    private void addPoint(final Point p) {
        /**
         * work along the line from (0,yIntStart) to (cSteps,yIntEnd),
         * incrementing the Hough accumulator.
         */
        final int nStep = (cMaxSlope - cMinSlope) / cSteps;
        int nSlopePos = 0;
        for (int slope = cMinSlope; nSlopePos < cSteps; slope += nStep, nSlopePos++) {
            final int yInt = ((p.getY() * 256) - (p.getX() * slope)) / 256;
            /**
             * check if the current position falls inside the Hough accumulator.
             */
            if ((yInt >= cMinY) && (yInt < cMaxY)) {
                cHoughAccum[nSlopePos][yInt - cMinY]++;
            }
        }
        ;
    }
    
    /**
     * Find the peak in the Hough array. Updates cCount, cSlope, and cYInt.
     */
    private void findPeak() {
        cCount = Integer.MIN_VALUE;
        for (int slope = 0; slope < cSteps; slope++) {
            for (int y = 0; y < (cMaxY - cMinY); y++) {
                if (cHoughAccum[slope][y] > cCount) {
                    cCount = cHoughAccum[slope][y];
                    cSlope = ((slope * (cMaxSlope - cMinSlope)) / cSteps) + cMinSlope;
                    cYInt = y + cMinY;
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
     * Returns the slope of the line that was found.
     *
     * @return the line slope (*256)
     */
    public int getSlope() {
        return cSlope;
    }
    
    /**
     * Returns the y-intercept of the line that was found.
     *
     * @return the y-intercept.
     */
    public int getY() {
        return cYInt;
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
        cHoughAccum = new Integer[cSteps][cMaxY - cMinY];
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
        findPeak(); // sets cYInt, cSlope, cCount for access by caller
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
        return super.toString() + "(" + cMinY + "," + cMaxY + "," + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                cMinSlope + "," + cMaxSlope + "," + cSteps + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
}

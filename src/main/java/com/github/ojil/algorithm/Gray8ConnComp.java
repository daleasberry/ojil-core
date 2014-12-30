/*
 * Gray8ConnComp.java
 *
 * Created on September 9, 2006, 10:25 AM
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

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import com.github.ojil.core.ErrorCodes;
import com.github.ojil.core.Gray16Image;
import com.github.ojil.core.Gray8Image;
import com.github.ojil.core.Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.PipelineStage;
import com.github.ojil.core.Point;
import com.github.ojil.core.Rect;
import com.github.ojil.core.RgbImage;
import com.github.ojil.core.RgbVal;

/**
 * Gray connected components. Input is a Gray8Image. Pixels with value
 * Byte.MIN_VALUE are taken to be background. Other connected pixels are labeled
 * with unique labels. The connected component image can be retrieved, as can
 * the connected component bounding rectangles, sorted by area.
 * 
 * @author webb
 */
public class Gray8ConnComp extends PipelineStage {
    // class variables
    private boolean bComponents = false;
    private Gray16Image imLabeled = null;
    private int nSortedLabels = -1;
    private PriorityQueue pqLabels = null;
    Random random = new Random();
    private EquivalenceClass reClasses[];
    private Integer[] rnPerimeters;
    private short sClasses = 0;
    private Label rSortedLabels[] = null;
    
    private class Label implements ComparableJ2me {
        private int nLabel = 0;
        private int nPixelCount = 0;
        private final Rect rectBounding;
        
        public Label(final Point p, final int nLabel) {
            rectBounding = new Rect(p);
            this.nLabel = nLabel;
            nPixelCount = 1;
        }
        
        public void add(final Point p) {
            rectBounding.add(p);
            nPixelCount++;
        }
        
        @Override
        public int compareTo(final Object o) throws ImageError {
            if (o == null) {
                throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.CONN_COMP_LABEL_COMPARETO_NULL, null, null, null);
            }
            if (!(o instanceof Label)) {
                throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.OBJECT_NOT_EXPECTED_TYPE, o.toString(), "Label", null);
            }
            final Label l = (Label) o;
            if (l.nPixelCount == nPixelCount) {
                return 0;
            }
            return (l.nPixelCount < nPixelCount) ? -1 : 1;
        }
        
        public int getLabel() {
            return nLabel;
        }
        
        public int getPixelCount() {
            return nPixelCount;
        }
        
        public Rect getRect() {
            return rectBounding;
        }
    }
    
    /**
     * Creates a new instance of Gray8ConnComp.
     * 
     */
    public Gray8ConnComp() {
    }
    
    /**
     * Calculate the perimeter of all the components in the labeled image.
     */
    private void calculatePerimeters() {
        if (rnPerimeters != null) {
            return;
        }
        rnPerimeters = new Integer[EquivalenceClass.getLabels()];
        for (int i = 0; i < rnPerimeters.length; i++) {
            rnPerimeters[i] = 0;
        }
        final Short[] sData = imLabeled.getData();
        for (int i = 0; i < imLabeled.getHeight(); i++) {
            for (int j = 0; j < imLabeled.getWidth(); j++) {
                final short sCurr = sData[(i * imLabeled.getWidth()) + j];
                final short sUp = (i > 0) ? sData[((i - 1) * imLabeled.getWidth()) + j] : 0;
                final short sLeft = (j > 0) ? sData[((i * imLabeled.getWidth()) + j) - 1] : 0;
                final short sRight = (j < (imLabeled.getWidth() - 1)) ? sData[(i * imLabeled.getWidth()) + j + 1] : 0;
                final short sDown = (i < (imLabeled.getHeight() - 1)) ? sData[((i + 1) * imLabeled.getWidth()) + j] : 0;
                if (sCurr != sUp) {
                    rnPerimeters[sCurr]++;
                }
                if (sCurr != sLeft) {
                    rnPerimeters[sCurr]++;
                }
                if (sCurr != sRight) {
                    rnPerimeters[sCurr]++;
                }
                if (sCurr != sDown) {
                    rnPerimeters[sCurr]++;
                }
            }
        }
    }
    
    /**
     * Returns the nComponent'th bounding rectangle in order by size. Sorts only
     * as many components as are necessary to reach the requested component.
     * Does this by observing the state of rSortedLabels. If it is null, it has
     * to be allocated. If the nComponent'th element is null, more need to be
     * copied from pqLabels. This is done by copying and deleting the minimum
     * element until we reach the requested component.
     * 
     * @return the nComponent'th bounding rectangle, ordered by pixel count,
     *         largest first
     * @param nComponent
     *            the number of the component to return.
     * @throws ImageError
     *             if nComponent is greater than the number of components
     *             available.
     */
    public Rect getComponent(final int nComponent) throws ImageError {
        // see if we've created the sorted labels array
        // allocate it if we haven't.
        // If the components haven't been computed getComponentCount()
        // will compute them.
        if (rSortedLabels == null) {
            rSortedLabels = new Label[getComponentCount()];
            nSortedLabels = -1;
        }
        // see if the requested component is out of bounds
        if (nComponent >= rSortedLabels.length) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.CONN_COMP_LABEL_OUT_OF_BOUNDS, new Integer(nComponent).toString(), rSortedLabels.toString(), null);
        }
        // now see if we've figured out what the nComponent'th
        // component is. If not compute it by finding and
        // deleting min until we reach it.
        if (nSortedLabels < nComponent) {
            while (nSortedLabels < nComponent) {
                rSortedLabels[++nSortedLabels] = (Label) pqLabels.findMin();
                pqLabels.deleteMin();
            }
        }
        return rSortedLabels[nComponent].getRect();
    }
    
    /**
     * Get the number of connected components in the labeled image. Computes the
     * components from the labeled image if necessary.
     * 
     * @return the number of connected components.
     * @throws ImageError
     *             if BinaryHeap returns jjil.core.Error due to coding error.
     */
    public int getComponentCount() throws ImageError {
        // see if we've already calculated the components
        if (bComponents) {
            return pqLabels.size();
        }
        // no, we need to calculate it.
        // determine the pixel count and bounding rectangle
        // of all the components in the image
        final Short sData[] = imLabeled.getData();
        final Label vLabels[] = new Label[sClasses + 1];
        int nComponents = 0;
        for (int i = 0; i < imLabeled.getHeight(); i++) {
            final int nRow = i * imLabeled.getWidth();
            for (int j = 0; j < imLabeled.getWidth(); j++) {
                if (sData[nRow + j] != 0) {
                    final int nLabel = sData[nRow + j];
                    // has this label been seen before?
                    if (vLabels[nLabel] == null) {
                        // no, create a new label
                        vLabels[nLabel] = new Label(new Point(j, i), nLabel);
                        nComponents++;
                    } else {
                        // yes, extend its bounding rectangle
                        vLabels[nLabel].add(new Point(j, i));
                    }
                }
            }
        }
        // set up priority queue
        // first create a new array of the labels
        // with all the null elements eliminated
        final Label vCompressLabels[] = new Label[nComponents];
        int j = 0;
        for (final Label vLabel : vLabels) {
            if (vLabel != null) {
                vCompressLabels[j++] = vLabel;
            }
        }
        // now create the priority queue from the array
        pqLabels = new BinaryHeap(vCompressLabels);
        // clear the sorted labels array, it has to be recomputed.
        rSortedLabels = null;
        // we're done
        bComponents = true;
        return pqLabels.size();
    }
    
    public int getComponentLabel(final int n) {
        return rSortedLabels[n].getLabel();
    }
    
    public Enumeration<Point> getComponentPixels(final int n) throws ImageError {
        final Rect r = getComponent(n);
        // build a Vector of all points in the component
        final Vector<Point> vPoints = new Vector<>();
        final Short[] sData = imLabeled.getData();
        final int nLabel = rSortedLabels[n].nLabel;
        for (int i = r.getTop(); i <= r.getBottom(); i++) {
            for (int j = r.getLeft(); j <= r.getRight(); j++) {
                if (sData[(i * imLabeled.getWidth()) + j] == nLabel) {
                    vPoints.addElement(new Point(j, i));
                }
            }
        }
        return vPoints.elements();
    }
    
    /**
     * Override getFront. This is necessary because we don't actually compute
     * the output image when we are computing the components. The output is an
     * RgbImage with colors randomly assigned to the components. It is intended
     * to be used for debugging, to make it easy to see how the components of
     * the image are connected.
     * 
     * @return an RgbImage<?> with colors randomly assigned to the components
     * @throws ImageError
     *             if no components were found in the input image
     */
    @Override
    public Image<?> getFront() throws ImageError {
        if ((getComponentCount() == 0) || (imLabeled == null)) {
            throw new ImageError(ImageError.PACKAGE.CORE, ErrorCodes.NO_RESULT_AVAILABLE, null, null, null);
            
        } else {
            final RgbImage<?> rgbOutput = new RgbImage<>(imLabeled.getWidth(), imLabeled.getHeight());
            final Integer[] rgbData = rgbOutput.getData();
            final int nMaxLabel = EquivalenceClass.getLabels();
            final Short[] grayData = imLabeled.getData();
            final Integer[] rgbLabels = new Integer[nMaxLabel + 1];
            for (int i = 0; i < rgbLabels.length; i++) {
                rgbLabels[i] = RgbVal.toRgb((byte) ((random.nextInt() & 0xff) + Byte.MIN_VALUE), (byte) ((random.nextInt() & 0xff) + Byte.MIN_VALUE),
                        (byte) ((random.nextInt() & 0xff) + Byte.MIN_VALUE));
            }
            for (int i = 0; i < rgbData.length; i++) {
                try {
                    rgbData[i] = rgbLabels[grayData[i]];
                } catch (final Exception ex) {
                    ex.printStackTrace();
                }
            }
            return rgbOutput;
        }
    }
    
    /**
     * Get the labeled image.
     * 
     * @return a Gray16Image with final labels assigned to every pixel
     */
    public Gray16Image getLabeledImage() {
        return imLabeled;
    }
    
    /**
     * Returns the perimeter of the n'th largest component
     * 
     * @param n
     *            number of component to return (not its label)
     * @return perimeter of the component
     * @throws ImageError
     *             if the requested component doesn't exist
     */
    public int getPerimeter(final int n) throws ImageError {
        // first calculate all the perimeters
        calculatePerimeters();
        // next make sure we've figured out what the n'th largest
        // component is
        getComponent(n);
        // get the label of that ocmponent
        final int nLabel = rSortedLabels[n].getLabel();
        // look up the perimeter of that component
        return rnPerimeters[nLabel];
    }
    
    /**
     * Returns the pixel count of the n'th largest component
     * 
     * @param n
     *            number of component to return (not label)
     * @return number of pixels in the component (not bounding rectangle area)
     * @throws ImageError
     *             if the call to getComponent() does, say if there aren't that
     *             many components
     */
    public int getPixelCount(final int n) throws ImageError {
        // first make sure the n'th component is figured out
        getComponent(n); // retult discarded, used for side effect
        return rSortedLabels[n].getPixelCount();
    }
    
    @Override
    public boolean isEmpty() {
        return imLabeled == null;
    }
    
    /**
     * Compute connected components of input gray image using a union-find
     * algorithm.
     * 
     * @param image
     *            the input image.
     * @throws ImageError
     *             if the image is not a gray 8-bit image.
     */
    @Override
    public void push(final Image<?> image) throws ImageError {
        if (!(image instanceof Gray8Image)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_GRAY8IMAGE, image.toString(), null, null);
        }
        // initialize the label lookup array
        EquivalenceClass.reset();
        reClasses = new EquivalenceClass[image.getWidth() * image.getHeight()];
        
        // note that we've not computed the final labels or
        // the sorted components yet
        bComponents = false;
        
        final Gray8Image gray = (Gray8Image) image;
        final Byte[] bData = gray.getData();
        // for each pixel in the input image assign a label,
        // performing equivalence operations when two labels
        // are adjacent (8-connected)
        for (int i = 0; i < gray.getHeight(); i++) {
            final int nRow = i * gray.getWidth();
            // we use sUpLeft to refer to the pixel up and to
            // the left of the current, etc.
            EquivalenceClass eUpLeft = null, eUp = null, eUpRight = null;
            // after first row, initialize pixels above and
            // to the right
            if (i > 0) {
                eUp = reClasses[nRow - gray.getWidth()];
                eUpRight = reClasses[(nRow - gray.getWidth()) + 1];
            }
            // starting a new row the pixel to the left is 0
            EquivalenceClass eLeft = null;
            // nBitPatt encodes the state of the pixels around the
            // current pixel. The pattern is
            // 8 4 2
            // 1 current
            int nBitPatt = ((eUp != null) ? 4 : 0) + ((eUpRight != null) ? 2 : 0);
            // (at left column eLeft and eUpLeft will always be 0)
            for (int j = 0; j < gray.getWidth(); j++) {
                if (bData[nRow + j] != Byte.MIN_VALUE) {
                    switch (nBitPatt) {
                    // the cases below are derived from the bit
                    // pattern illustrated above. The general
                    // rule is to choose the most recently-scanned
                    // label when copying a label. Of course, we
                    // also do unions only as necessary
                        case 0:
                            // 0 0 0
                            // 0 X
                            reClasses[nRow + j] = new EquivalenceClass();
                            sClasses++;
                            break;
                        case 1:
                            // 0 0 0
                            // X X
                            reClasses[nRow + j] = eLeft.find();
                            break;
                        case 2:
                            // 0 0 X
                            // 0 X
                            reClasses[nRow + j] = eUpRight.find();
                            break;
                        case 3:
                            // 0 0 X
                            // X X
                            eLeft.union(eUpRight);
                            reClasses[nRow + j] = eLeft.find();
                            break;
                        case 4:
                            // 0 X 0
                            // 0 X
                            reClasses[nRow + j] = eUp.find();
                            break;
                        case 5:
                            // 0 X 0
                            // X X
                            // we must already have union'ed
                            // eLeft and eUp
                            reClasses[nRow + j] = eLeft.find();
                            break;
                        case 6:
                            // 0 X X
                            // 0 X
                            // we must already have union'ed
                            // eUp and eUpRight
                            reClasses[nRow + j] = eUpRight.find();
                            break;
                        case 7:
                            // 0 X X
                            // X X
                            // we must already have union'ed
                            // eLeft and eUp, and eUp and eUpRight
                            reClasses[nRow + j] = eLeft.find();
                            break;
                        case 8:
                            // X 0 0
                            // 0 X
                            reClasses[nRow + j] = eUpLeft.find();
                            break;
                        case 9:
                            // X 0 0
                            // X X
                            // we must already have union'ed
                            // eLeft and eUpLeft
                            reClasses[nRow + j] = eLeft.find();
                            break;
                        case 10:
                            // X 0 X
                            // 0 X
                            eUpLeft.union(eUpRight);
                            reClasses[nRow + j] = eUpLeft.find();
                            break;
                        case 11:
                            // X 0 X
                            // X X
                            // we must already have union'ed
                            // eLeft and eUpLeft
                            eLeft.union(eUpRight);
                            reClasses[nRow + j] = eLeft.find();
                            break;
                        case 12:
                            // X X 0
                            // 0 X
                            // we must already have union'ed
                            // eUpLeft and eUp
                            reClasses[nRow + j] = eUp.find();
                            break;
                        case 13:
                            // X X 0
                            // X X
                            // we must already have union'ed
                            // eLeft and eUpLeft, and eUpLeft and eUp
                            reClasses[nRow + j] = eLeft.find();
                            break;
                        case 14:
                            // X X X
                            // 0 X
                            // we must already have union'ed
                            // eUpLeft, eUp, and eUpRight
                            reClasses[nRow + j] = eUpRight.find();
                            break;
                        case 15:
                            // X X X
                            // X X
                            // we must already have union'ed
                            // eLeft, eUpLeft, eUp, and eUpRight
                            reClasses[nRow + j] = eLeft.find();
                            break;
                    }
                }
                // shift right to next pixel
                eUpLeft = eUp;
                eUp = eUpRight;
                eLeft = reClasses[nRow + j];
                // if we're not at the right column and after the first
                // row read a new right pixel
                if ((i > 0) && (j < (gray.getWidth() - 1))) {
                    eUpRight = reClasses[(nRow - gray.getWidth()) + j + 2];
                } else {
                    eUpRight = null;
                }
                
                // compute the new bit pattern. This is the old pattern
                // with eUpLeft and eLeft and'ed off (& 6), shifted left,
                // with the new eLeft and eUpRight or'ed in
                nBitPatt = ((nBitPatt & 6) << 1) + ((eLeft != null) ? 1 : 0) + ((eUpRight != null) ? 2 : 0);
            }
        }
        // initialize the labeled image
        imLabeled = new Gray16Image(gray.getWidth(), gray.getHeight(), (short) 0);
        final Short[] sLabels = imLabeled.getData();
        // assign label pixels their final values
        for (int i = 0; i < sLabels.length; i++) {
            if (reClasses[i] != null) {
                sLabels[i] = (short) reClasses[i].getLabel();
            }
        }
        // free memory for reClasses
        reClasses = null;
    }
}

/*
 * RgbClip.java
 *
 * Created on May 13, 2006, 2:38 PM
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

import com.github.ojil.core.Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.PipelineStage;
import com.github.ojil.core.RgbImage;
import com.github.ojil.core.RgbVal;

/**
 * Pipeline stage performs color clipping, setting all pixels that do not meet
 * the threshold test to 0, otherwise leaving them alone.
 * <p>
 * The test is abs(pixel.R - red) + abs(pixel.G - green) + abs(pixel.B - blue) <
 * limit.
 * <p>
 * The test direction can be reversed using the dir parameter.
 * 
 * @author webb
 */
public class RgbClip extends PipelineStage {
    boolean bDir; /* clipping direction */
    byte nB; /* blue value */
    byte nG; /* green value */
    int nLimit; /* the clipping limit */
    byte nR; /* red value */
    
    /**
     * Creates a new instance of RgbClip. The clip test is defined here.
     * 
     * 
     * @param r
     *            red value
     * @param g
     *            green value
     * @param b
     *            value
     * @param l
     *            the threshold
     * @param dir
     *            if true pixels that fail test are set to 0; if false pixels
     *            that pass test are set to 0.
     */
    public RgbClip(final byte r, final byte g, final byte b, final int l, final boolean dir) {
        nR = r;
        nG = g;
        nB = b;
        nLimit = l;
        bDir = dir;
    }
    
    /**
     * Creates a new instance of RgbClip. The clip test is defined here.
     * 
     * 
     * @param rgb
     *            int value containg packed RGB
     * @param l
     *            the threshold
     * @param dir
     *            if true pixels that fail test are set to 0; if false pixels
     *            that pass test are set to 0.
     */
    public RgbClip(final int rgb, final int l, final boolean dir) {
        nR = RgbVal.getR(rgb);
        nG = RgbVal.getG(rgb);
        nB = RgbVal.getB(rgb);
        nLimit = l;
        bDir = dir;
    }
    
    /**
     * Clips the RGB image and sets all pixels that fail/pass the test
     * (according to bDir) to 0.
     *
     * @param image
     *            the input image.
     * @throws ImageError
     *             if the cropping window extends outside the input image, or
     *             the input image is not an RgbImage.
     */
    @Override
    public void push(final Image<?> image) throws ImageError {
        if (!(image instanceof RgbImage)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_RGBIMAGE, image.toString(), null, null);
        }
        final RgbImage<?> rgbImage = (RgbImage<?>) image;
        final Integer[] src = rgbImage.getData();
        final int nWidth = rgbImage.getWidth();
        for (int i = 0; i < rgbImage.getHeight(); i++) {
            for (int j = 0; j < rgbImage.getWidth(); j++) {
                final int nColorPixel = src[(i * nWidth) + j];
                final int nRed = RgbVal.getR(nColorPixel);
                final int nGreen = RgbVal.getG(nColorPixel);
                final int nBlue = RgbVal.getB(nColorPixel);
                final int nDiff = Math.abs(nRed - nR) + Math.abs(nGreen - nG) + Math.abs(nBlue - nB);
                if ((nDiff < nLimit) != bDir) {
                    src[(i * nWidth) + j] = 0;
                }
            }
        }
        super.setOutput(image);
    }
    
    /**
     * Change the threshold parameters.
     * 
     * @param r
     *            red value
     * @param g
     *            green value
     * @param b
     *            value
     * @param l
     *            the threshold
     * @param dir
     *            if true pixels that fail test are set to 0; if false pixels
     *            that pass test are set to 0.
     */
    public void setParameters(final byte r, final byte g, final byte b, final int l, final boolean dir) {
        nR = r;
        nG = g;
        nB = b;
        nLimit = l;
        bDir = dir;
    }
    
    /**
     * Return a string describing the clipping operation.
     *
     * @return the string describing the clipping operation.
     */
    @Override
    public String toString() {
        return super.toString() + " (" + nR + "," + nG + //$NON-NLS-1$ //$NON-NLS-2$
                "," + nB + "," + nLimit + "," + bDir + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }
}

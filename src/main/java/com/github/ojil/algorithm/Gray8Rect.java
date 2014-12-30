/*
 * Gray8Rect.java
 *
 * Created on September 9, 2006, 2:52 PM
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

import com.github.ojil.core.Gray8Image;
import com.github.ojil.core.Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.PipelineStage;

/**
 * Pipeline stage assigns a constant value to a rectangle in an input Gray8Image
 * to produce an output Gray8Image.
 * 
 * @author webb
 */
public class Gray8Rect extends PipelineStage {
    private int cX, cY, nWidth, nHeight;
    private final byte bValue;
    
    /**
     * Creates a new instance of Gray8Rect.
     * 
     * @param cX
     *            The horizontal offset of the rectangle.
     * @param cY
     *            the vertical offset of the rectangle.
     * @param nWidth
     *            the width of the rectangle.
     * @param nHeight
     *            the height of the rectangle.
     * @param bValue
     *            the value to be assigned to the rectangle.
     * @throws ImageError
     *             if the height or width of the rectange is negative or zero.
     */
    public Gray8Rect(final int cX, final int cY, final int nWidth, final int nHeight, final byte bValue) throws ImageError {
        setWindow(cX, cY, nWidth, nHeight);
        this.bValue = bValue;
    }
    
    /**
     * Assigns a constant rectangle to the input Gray8Image, replacing values in
     * the image.
     * 
     * @param image
     *            the input image (output replaces input).
     * @throws ImageError
     *             if the input is not a Gray8Image.
     */
    @Override
    public void push(final Image<?> image) throws ImageError {
        if (!(image instanceof Gray8Image)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_GRAY8IMAGE, image.toString(), null, null);
        }
        final Gray8Image input = (Gray8Image) image;
        final Byte[] data = input.getData();
        final int nLimitY = Math.min(input.getHeight(), cY + nHeight);
        final int nLimitX = Math.min(input.getWidth(), cX + nWidth);
        for (int i = cY; i < nLimitY; i++) {
            final int nStart = i * image.getWidth();
            for (int j = cX; j < nLimitX; j++) {
                data[nStart + j] = bValue;
            }
        }
        super.setOutput(input);
    }
    
    /**
     * 
     * @param cX
     *            Top-left horizontal coordinate of the rectangle.
     * @param cY
     *            top-left vertical coordinate of the rectangle.
     * @param nWidth
     *            Width of the rectangle.
     * @param nHeight
     *            Height of the rectangle.
     * @throws ImageError
     *             if the width or height is negative or zero.
     */
    public void setWindow(final int cX, final int cY, final int nWidth, final int nHeight) throws ImageError {
        if ((nWidth <= 0) || (nHeight <= 0)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.OUTPUT_IMAGE_SIZE_NEGATIVE, new Integer(nWidth).toString(), new Integer(nHeight).toString(), null);
        }
        this.cX = cX;
        this.cY = cY;
        this.nWidth = nWidth;
        this.nHeight = nHeight;
    }
}

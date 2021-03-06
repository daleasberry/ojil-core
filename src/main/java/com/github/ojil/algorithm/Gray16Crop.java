/*
 * Gray16Crop.java
 *
 * Created on August 27, 2006, 2:316 PM
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

import com.github.ojil.core.ErrorCodes;
import com.github.ojil.core.Gray16Image;
import com.github.ojil.core.Image;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.PipelineStage;
import com.github.ojil.core.Rect;

/**
 * Pipeline stage crops a Gray16Image to a given rectangular cropping window.
 * <p>
 * 
 * @author webb
 */
public class Gray16Crop extends PipelineStage {
    int cHeight; /* height of cropping window */
    int cWidth; /* width of cropping window */
    int cX; /* left edge of cropping window */
    int cY; /* top of cropping window */
    
    /**
     * Creates a new instance of Gray16Crop. The cropping window is specified
     * here.
     *
     * @param x
     *            left edge of cropping window
     * @param y
     *            top edge of cropping window
     * @param width
     *            width of cropping window
     * @param height
     *            height of cropping window
     * @throws com.github.ojil.core.ImageError
     *             if the top left corner of the window is negative, or the
     *             window area is non-positive.
     */
    public Gray16Crop(final int x, final int y, final int width, final int height) throws com.github.ojil.core.ImageError {
        setWindow(x, y, width, height);
    }
    
    /**
     * Creates a new instance of Gray16Crop. The cropping window is specified
     * here.
     *
     * @param r
     *            Rect to crop to.
     * @throws com.github.ojil.core.ImageError
     *             if the top left corner of the window is negative, or the
     *             window area is non-positive.
     */
    public Gray16Crop(final Rect r) throws com.github.ojil.core.ImageError {
        setWindow(r.getLeft(), r.getTop(), r.getWidth(), r.getHeight());
    }
    
    /**
     * Crops the input gray image to the cropping window that was specified in
     * the constructor.
     *
     * @param image
     *            the input image.
     * @throws com.github.ojil.core.ImageError
     *             if the cropping window extends outside the input image, or
     *             the input image is not a Gray16Image.
     */
    @Override
    public void push(final Image<?, ?> image) throws com.github.ojil.core.ImageError {
        if (!(image instanceof Gray16Image)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_GRAY16IMAGE, image.toString(), null, null);
        }
        final Gray16Image<?> imageInput = (Gray16Image<?>) image;
        if (((cX + cWidth) > image.getWidth()) || ((cY + cHeight) > image.getHeight())) {
            throw new ImageError(ImageError.PACKAGE.CORE, ErrorCodes.BOUNDS_OUTSIDE_IMAGE, image.toString(), toString(), null);
        }
        final Gray16Image<?> imageResult = new Gray16Image<>(cWidth, cHeight);
        final Short[] src = imageInput.getData();
        final Short[] dst = imageResult.getData();
        for (int i = 0; i < cHeight; i++) {
            System.arraycopy(src, ((i + cY) * image.getWidth()) + cX, dst, i * cWidth, cWidth);
        }
        super.setOutput(imageResult);
    }
    
    /**
     * Gets the cropping window height
     * 
     * @return the cropping window height
     */
    public int getHeight() {
        return cHeight;
    }
    
    /**
     * Gets the cropping window left edge
     * 
     * @return the cropping window left edge
     */
    public int getLeft() {
        return cX;
    }
    
    /**
     * Gets the cropping window top
     * 
     * @return the cropping window top
     */
    public int getTop() {
        return cY;
    }
    
    /**
     * Gets the cropping window width
     * 
     * @return the cropping window width
     */
    public int getWidth() {
        return cWidth;
    }
    
    /**
     * Change the cropping window.
     *
     * @param x
     *            left edge of cropping window
     * @param y
     *            top edge of cropping window
     * @param width
     *            width of cropping window
     * @param height
     *            height of cropping window
     * @throws com.github.ojil.core.ImageError
     *             if the top left corner of the window is negative, or the
     *             window area is non-positive.
     */
    public void setWindow(final int x, final int y, final int width, final int height) throws com.github.ojil.core.ImageError {
        if ((x < 0) || (y < 0)) {
            throw new ImageError(ImageError.PACKAGE.CORE, ErrorCodes.BOUNDS_OUTSIDE_IMAGE, new Integer(x).toString(), new Integer(y).toString(), null);
        }
        if ((width <= 0) || (height <= 0)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.INPUT_IMAGE_SIZE_NEGATIVE, new Integer(width).toString(), new Integer(height).toString(), null);
        }
        cX = x;
        cY = y;
        cWidth = width;
        cHeight = height;
    }
    
    /**
     * Change the cropping window.
     * 
     * @param r
     *            new Rect to crop to
     * @throws com.github.ojil.core.ImageError
     *             if the top left corner of the window is negative, or the
     *             window area is non-positive.
     */
    public void setWindow(final Rect r) throws com.github.ojil.core.ImageError {
        this.setWindow(r.getLeft(), r.getTop(), r.getWidth(), r.getHeight());
    }
    
    /**
     * Return a string describing the cropping operation.
     *
     * @return the string describing the cropping operation.
     */
    @Override
    public String toString() {
        return super.toString() + " (" + cX + "," + cY + "," + cWidth + "," + cHeight + ")";
    }
}

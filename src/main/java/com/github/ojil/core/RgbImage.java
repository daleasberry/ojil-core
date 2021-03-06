/*
 * RgbImage.java
 *
 * Created on August 27, 2006, 12:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 *
 * Copyright 2006 by Jon A. Webb
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

package com.github.ojil.core;

/**
 * RgbImage is the type used to hold an RGB image, which is stored as an ARGB
 * image type (32-bits) with the A byte ignored.
 * <p>
 * Implementation-specific libraries define methods that allow the creation of
 * an RgbImage from a native image type. RgbImage is therefore the first and
 * last jjil.core object used after capture and before display of an image.
 * 
 * @author webb
 */
public class RgbImage<T extends Object> extends Image<Integer, T> {
    /**
     * Creates a new instance of RgbImage
     *
     * @param theWidth
     *            the image width
     * @param theHeight
     *            the image height
     */
    public RgbImage(final int theWidth, final int theHeight) {
        super(theWidth, theHeight, ImageType.INT_RGB, new Integer[theWidth * theHeight], null);
    }
    
    public RgbImage(final int theWidth, final int theHeight, final Integer[] theImageData) {
        super(theWidth, theHeight, ImageType.INT_RGB, theImageData, null);
    }
    
    public RgbImage(final int theWidth, final int theHeight, final Integer[] theImageData, final T thePlatformImage) {
        super(theWidth, theHeight, ImageType.INT_RGB, theImageData, thePlatformImage);
    }
    
    /**
     * Creates a new instance of RgbImage, assigning a constant value
     * 
     * @param bR
     *            the red color value to be assigned.
     * @param bG
     *            the green color value to be assigned.
     * @param bB
     *            the blue color value to be assigned.
     * @param theWidth
     *            the image width
     * @param theHeight
     *            the image height
     */
    public RgbImage(final int theWidth, final int theHeight, final byte bR, final byte bG, final byte bB) {
        super(theWidth, theHeight, ImageType.INT_RGB);
        imageData = new Integer[getWidth() * getHeight()];
        final int nRgb = RgbVal.toRgb(bR, bG, bB);
        for (int i = 0; i < (getWidth() * getHeight()); i++) {
            imageData[i] = nRgb;
        }
    }
    
    /**
     * Creates a new instance of RgbImage, assigning a constant value
     * 
     * @param nRgb
     *            the packed RGB value to assign
     * @param theWidth
     *            the image width
     * @param theHeight
     *            the image height
     */
    public RgbImage(final int theWidth, final int theHeight, final int nRgb) {
        super(theWidth, theHeight, ImageType.INT_RGB);
        imageData = new Integer[getWidth() * getHeight()];
        for (int i = 0; i < (getWidth() * getHeight()); i++) {
            imageData[i] = nRgb;
        }
    }
    
    /**
     * Creates a new RgbImage backed by the platform-specific object and the
     * data contained by that platform-specific object.
     * 
     * @param thePlatformImage
     * @param theImageData
     */
    public RgbImage(T thePlatformImage, Integer[] theImageData) {
        super(thePlatformImage);
        imageData = theImageData;
    }
    
    /**
     * Creates a shallow copy of this image
     *
     * @return the image copy.
     */
    @Override
    public Object clone() {
        final RgbImage<?> image = new RgbImage<>(getWidth(), getHeight());
        System.arraycopy(getData(), 0, image.getData(), 0, getWidth() * getHeight());
        return image;
    }
    
    /**
     * Fill a rectangle in an RgbImage with a given value
     * 
     * @param r
     *            the Rect to fill
     * @param rgb
     *            the color to assign
     * @return the modified RgbImage (i.e., this)
     * @throws ImageError
     *             if the bounds are outside the image
     */
    public RgbImage<?> fill(final Rect r, final int rgb) throws ImageError {
        if ((r.getTop() < 0) || (r.getBottom() > getHeight()) || (r.getLeft() < 0) || (r.getRight() > getWidth())) {
            throw new ImageError(ImageError.PACKAGE.CORE, ErrorCodes.BOUNDS_OUTSIDE_IMAGE, r.toString(), null, null);
        }
        for (int i = r.getTop(); i < r.getBottom(); i++) {
            for (int j = r.getLeft(); j < r.getRight(); j++) {
                imageData[(i * getWidth()) + j] = rgb;
            }
        }
        return this;
    }
}

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
 * @author webb
 */

package com.github.ojil.core;

/**
 * An RgbMaskedImage works just like an RgbImage except we use the top A byte of
 * the ARGB field as a mask value. If that byte is zero the pixel is considered
 * to be masked.
 * 
 */
public class RgbMaskedImage<T> extends RgbImage<Object> {
    /**
     * Create a new RgbMaskedImage with the mask set to 0 (not masked)
     * 
     * @param nWidth
     *            width of image
     * @param nHeight
     *            height of image
     */
    public RgbMaskedImage(final int nWidth, final int nHeight) {
        super(nWidth, nHeight);
        // masked area is set to zero automatically since we
        // use unused A byte in ARGB field
    }
    
    /**
     * Create a new RgbMaskedImage from an existing RgbImage, copying the pixel
     * values and initializing it to unmasked.
     * 
     * @param rgb
     *            input RgbImage to use as a source.
     */
    public RgbMaskedImage(final RgbImage<?> rgb) {
        super(rgb.getWidth(), rgb.getHeight());
        // simply ocpy the RGB values into my array. The
        // A value will always be set to 0 in an RgbImage so
        // the image is automatically not masked.
        System.arraycopy(rgb.getData(), 0, super.getData(), 0, super.getWidth() * super.getHeight());
        // ensure that all pixels are initially unmasked.
        for (int i = 0; i < (getWidth() * getHeight()); i++) {
            rgb.getData()[i] |= 0xFF000000;
        }
    }
    
    /**
     * Create a new RgbMaskedImage from an existing RgbImage and a Gray8Image
     * mask, copying the pixel values and initializing the mask to the mask
     * image. The a pixel in the Gray8Image mask image is considered to be
     * masked if is is not equal to Byte.MIN_VALUE.
     * 
     * @param rgb
     *            input RgbImage to use as a source.
     */
    public RgbMaskedImage(final RgbImage<?> rgb, final Gray8Image<?> grayMask) {
        super(rgb.getWidth(), rgb.getHeight());
        // simply ocpy the RGB values into my array. The
        // A value will always be set to 0 in an RgbImage so
        // the image is automatically not masked.
        System.arraycopy(rgb.getData(), 0, super.getData(), 0, super.getWidth() * super.getHeight());
        // set mask
        final Integer[] rgbData = getData();
        final Byte[] maskData = grayMask.getData();
        for (int i = 0; i < (getWidth() * getHeight()); i++) {
            if (maskData[i] != Byte.MIN_VALUE) {
                // mask pixel
                rgbData[i] &= 0x00FFFFFF;
            } else {
                // unmask pixel
                rgbData[i] |= 0xFF000000;
            }
        }
    }
    
    /**
     * Returns true if the given pixel is masked.
     * 
     * @param nRow
     *            row of pixel
     * @param nCol
     *            column of pixel
     * @return true iff the pixel is masked.
     */
    public boolean isMasked(final int nRow, final int nCol) {
        return (super.getData()[(nRow * super.getWidth()) + nCol] & 0xff000000) == 0;
    }
    
    /**
     * Mask the given pixel.
     * 
     * @param nRow
     *            row of pixel
     * @param nCol
     *            column of pixel
     */
    public void setMask(final int nRow, final int nCol) {
        super.getData()[(nRow * super.getWidth()) + nCol] &= 0x00ffffff;
    }
    
    /**
     * Unmask the given pixel.
     * 
     * @param nRow
     *            row of pixel
     * @param nCol
     *            column of pixel
     */
    public void unsetMask(final int nRow, final int nCol) {
        super.getData()[(nRow * super.getWidth()) + nCol] |= 0xff000000;
        
    }
}
/*
 * Gray32MaskedImage.java
 *   Describes an 32-bit image together with its mask, in which any non-zero
 *   value is masked.
 *
 * Created on August 27, 2006, 12:48 PM
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
 * Gray32MaskedImage is the image type used to store a 32-bit integer image and
 * its associated 8-bit mask. Mask value = Byte.MIN_VALUE is considered to be
 * unmasked; all other values are masked.
 * 
 * @author webb
 */
public class Gray32MaskedImage<T extends Object> extends Gray32Image<T> {
    private final Gray8Image<?> imMask;
    
    /**
     * Creates a new instance of Gray32MaskedImage
     * 
     * @param cWidth
     *            Width of the image (columns).
     * @param cHeight
     *            Height of the image (rows)
     */
    public Gray32MaskedImage(final int cWidth, final int cHeight) {
        super(cWidth, cHeight);
        imMask = new Gray8Image<>(cWidth, cHeight);
    }
    
    /**
     * Creates a new instance of Gray32MaskedImage from an existing image and
     * mask.
     * 
     * @param imData
     *            the data image.
     * @param imMask
     *            the mask
     * @throws ImageError
     *             If either input is not a Gray8Image or the sizes are not the
     *             same.
     */
    public Gray32MaskedImage(final Gray32Image<?> imData, final Gray8Image<?> imMask) throws ImageError {
        super(imData.getWidth(), imData.getHeight());
        if ((imData.getWidth() != imMask.getWidth()) || (imData.getHeight() != imMask.getHeight())) {
            throw new ImageError(ImageError.PACKAGE.CORE, ErrorCodes.IMAGE_MASK_SIZE_MISMATCH, imData.toString(), imMask.toString(), null);
        }
        System.arraycopy(imData.getData(), 0, getData(), 0, getWidth() * getHeight());
        this.imMask = imMask;
    }
    
    /**
     * Copy this image
     * 
     * @return the image copy.
     */
    @Override
    public Object clone() {
        final Gray32MaskedImage<?> image = new Gray32MaskedImage<>(getWidth(), getHeight());
        System.arraycopy(getData(), 0, image.getData(), 0, getWidth() * getHeight());
        System.arraycopy(getMask().getData(), 0, image.getMask().getData(), 0, getWidth() * getHeight());
        return image;
    }
    
    /**
     * Get the input Gray8Image mask.
     * 
     * @return the input mask
     */
    public Gray8Image<?> getMask() {
        return imMask;
    }
    
    /**
     * Return a pointer to the mask data.
     *
     * @return the data pointer.
     */
    public Byte[] getMaskData() {
        return imMask.getData();
    }
    
    /**
     * Returns true iff the given pixel is masked.
     * 
     * @param nRow
     *            row of pixel
     * @param nCol
     *            column of pixel
     * @return true iff pixel (nRow, nCol) is masked
     */
    public boolean isMasked(final int nRow, final int nCol) {
        return imMask.getData()[(nRow * imMask.getWidth()) + nCol] != Byte.MIN_VALUE;
    }
}
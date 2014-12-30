/*
 * Gray16Image.java
 *
 * Created on August 27, 2006, 1:49 PM
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
 * Gray16Image is the image type used to store a 16-bit signed gray image.
 *
 * @author webb
 */
public final class Gray16Image<T extends Object> extends Image<Short, T> {
    /**
     * Creates a new instance of Gray16Image
     *
     * @param cWidth
     *            the image width
     * @param cHeight
     *            the image height
     */
    public Gray16Image(final int cWidth, final int cHeight) {
        super(cWidth, cHeight, ImageType.BYTE_GRAY, new Short[cWidth*cHeight]);
    }
    
    /**
     * Creates a new instance of Gray16Image, assigning a constant value.
     * 
     * @param cWidth
     *            Width of the image (columns).
     * @param cHeight
     *            Height of the image (rows)
     * @param wValue
     *            constant value to be assigned to the image
     */
    public Gray16Image(final int cWidth, final int cHeight, final Short wValue) {
        super(cWidth, cHeight);
        imageData = new Short[getWidth() * getHeight()];
        for (int i = 0; i < (getWidth() * getHeight()); i++) {
            imageData[i] = wValue;
        }
    }
    
    /**
     * Copy this image
     *
     * @return the image copy.
     */
    @Override
    public Object clone() {
        final Gray16Image<?> image = new Gray16Image<>(getWidth(), getHeight());
        System.arraycopy(getData(), 0, image.getData(), 0, getWidth() * getHeight());
        return image;
    }
}

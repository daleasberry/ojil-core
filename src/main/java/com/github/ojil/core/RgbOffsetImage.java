/*
 * RgbOffsetImage.java
 *
 * Created on June 29, 2007, 5:27 PM
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

package com.github.ojil.core;

/**
 * RgbOffsetImage is used to represent an image with an implicit offset from the
 * top left.
 * 
 * @author webb
 */
public class RgbOffsetImage<T> extends RgbImage<Object> {
    int cX;
    int cY;
    
    /**
     * Creates a new instance of RgbOffsetImage
     * 
     * @param cWidth
     *            Image width.
     * @param cHeight
     *            Image height.
     * @param cX
     *            Horizontal position of top-left corner of subimage.
     * @param cY
     *            Vertical position of top-left corner of subimage.
     */
    public RgbOffsetImage(final int cWidth, final int cHeight, final int cX, final int cY) {
        super(cWidth, cHeight);
        this.cX = cX;
        this.cY = cY;
    }
    
    /**
     * Creates a new instance of RgbOffsetImage
     * 
     * @param cWidth
     *            Image width.
     * @param cHeight
     *            Image height.
     * @param cX
     *            Horizontal position of top-left corner of subimage.
     * @param cY
     *            Vertical position of top-left corner of subimage.
     * @param bValue
     *            the value to assign to the image
     */
    public RgbOffsetImage(final int cWidth, final int cHeight, final int cX, final int cY, final int nValue) {
        super(cWidth, cHeight, nValue);
        this.cX = cX;
        this.cY = cY;
    }
    
    /**
     * Creates a new instance of RgbOffsetImage
     * 
     * @param gray
     *            the input image providing the image data
     * @param cX
     *            Horizontal position of top-left corner of subimage.
     * @param cY
     *            Vertical position of top-left corner of subimage.
     */
    public RgbOffsetImage(final RgbImage<?> rgb, final int cX, final int cY) {
        super(rgb.getWidth(), rgb.getHeight());
        System.arraycopy(rgb.getData(), 0, getData(), 0, getWidth() * getHeight());
        this.cX = cX;
        this.cY = cY;
    }
    
    /**
     * Copy this image
     *
     * @return the image copy.
     */
    @Override
    public Object clone() {
        final RgbOffsetImage<?> image = new RgbOffsetImage<>(getWidth(), getHeight(), getXOffset(), getYOffset());
        System.arraycopy(getData(), 0, image.getData(), 0, getWidth() * getHeight());
        return image;
    }
    
    /**
     * Return horizontal position of top-left corner of subimage.
     * 
     * @return the horizontal position of the top-left corner of the subimage.
     */
    public int getXOffset() {
        return cX;
    }
    
    /**
     * Return vertical position of top-left corner of subimage.
     * 
     * @return vertical position of top-left corner of subimage.
     */
    public int getYOffset() {
        return cY;
    }
    
    /**
     * Change horizontal position of subimage.
     * 
     * @param nX
     *            New horizontal position of top-left corner of subimage.
     */
    public void setXOffset(final int nX) {
        cX = nX;
    }
    
    /**
     * Change vertical psotion of subimage.
     * 
     * @param nY
     *            New vertical position of top-left corner of subimage.
     */
    public void setYOffset(final int nY) {
        cY = nY;
    }
    
    /**
     * Return a string describing the image.
     *
     * @return the string.
     */
    @Override
    public String toString() {
        return super.toString() + " (" + getWidth() + "x" + getHeight() + //$NON-NLS-1$ //$NON-NLS-2$
                "," + getXOffset() + "," + getYOffset() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    
}

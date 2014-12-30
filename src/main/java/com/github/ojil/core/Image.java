/*
 * Image.java
 *
 * Created on August 27, 2006, 1:40 PM
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
 * Image is the fundamental abstract class for holding images. It is used for
 * passing images between pipeline stages. The image dimensions are stored here;
 * the image format and the actual image data are defined in the derived
 * classes.
 * 
 * @author webb
 */
public abstract class Image<T extends Object> {
    
    /**
     * The image height.
     */
    protected int height;
    /**
     * The image width.
     */
    protected int width;
    
    protected ImageType imageType;
    private T platformImage;
    
    /**
     * Creates a new instance of Image
     *
     * @param theWidth
     *            the image width
     * @param theHeight
     *            the image height
     */
    public Image(final int theWidth, final int theHeight) {
        width = theWidth;
        height = theHeight;
    }
    
    /**
     * Creates a new instance of Image
     *
     * @param theWidth
     *            the image width
     * @param theHeight
     *            the image height
     */
    public Image(final int theWidth, final int theHeight, final ImageType theImageType) {
        width = theWidth;
        height = theHeight;
        imageType = theImageType;
    }
    
    public Image(final T thePlatformImage) {
        platformImage = thePlatformImage;
    }
    
    /**
     * Makes a copy of the image
     * 
     * @return the image copy
     */
    @Override
    public abstract Object clone();
    
    /**
     * Returns the image height
     *
     * @return the image height (rows)
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * Returns a Point object giving the size of this image (width x height)
     * 
     * @return a Point indicating the image size
     */
    public Point getSize() {
        return new Point(width, height);
    }
    
    /**
     * Returns the image width
     *
     * @return the image width (columns)
     */
    public int getWidth() {
        return width;
    }
    
    public ImageType getType() {
        return imageType;
    }
    
    public abstract Number[] getData();
    
    public T getPlatformImage() {
        return platformImage;
    }
    
    public static final Integer[] arraycopy(final int[] data) {
        final Integer[] copy = new Integer[data.length];
        for (int i = 0; i < copy.length; i++) {
            copy[i] = Integer.valueOf(data[i]);
        }
        return copy;
    }
    
    public static final int[] arraycopy(final Integer[] data) {
        final int[] copy = new int[data.length];
        for (int i = 0; i < copy.length; i++) {
            copy[i] = data[i].intValue();
        }
        return copy;
    }
}

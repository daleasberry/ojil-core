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
 * Image is the fundamental abstract class for holding images.
 * It is used for passing images between pipeline stages.
 * The image dimensions are stored here; the image format
 * and the actual image data are defined in the derived classes.
 * @author webb
 */
public abstract class Image {
	public static final int TYPE_CUSTOM         =  0,
                            TYPE_INT_RGB        =  1,
                            TYPE_INT_ARGB       =  2,
                            TYPE_INT_ARGB_PRE   =  3,
                            TYPE_INT_BGR        =  4,
                            TYPE_3BYTE_BGR      =  5,
                            TYPE_4BYTE_ABGR     =  6,
                            TYPE_4BYTE_ABGR_PRE =  7,
                            TYPE_USHORT_565_RGB =  8,
                            TYPE_USHORT_555_RGB =  9,
                            TYPE_BYTE_GRAY      = 10,
                            TYPE_USHORT_GRAY    = 11,
                            TYPE_BYTE_BINARY    = 12,
                            TYPE_BYTE_INDEXED   = 13;
	
    /**
     * The image height.
     */
    private final int mnHeight;
    /**
     * The image width.
     */
    private final int mnWidth;
    
    /** Creates a new instance of Image 
     *
     * @param mnWidth   the image width
     * @param mnHeight  the image height
     */
    public Image(int mnWidth, int mnHeight) {
        this.mnWidth = mnWidth;
        this.mnHeight = mnHeight;
    }
    
    /**
     * Makes a copy of the image
     * @return the image copy
     */
    public abstract Object clone();
    
    /** Returns the image height 
     *
     * @return the image height (rows)
     */
    public int getHeight()
    {
        return this.mnHeight;
    }
    
    /**
     * Returns a Point object giving the size of this image
     * (width x height)
     * @return a Point indicating the image size
     */
    public Point getSize() {
    	return new Point(this.mnWidth, this.mnHeight);
    }
    
    /** Returns the image width
     *
     * @return the image width (columns)
     */
    public int getWidth()
    {
        return this.mnWidth;
    }
    
    public abstract Number[] getData();
}

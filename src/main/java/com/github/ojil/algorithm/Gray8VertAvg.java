/*
 * Gray8VertAvg.java
 *
 * Created on September 9, 2006, 2:17 PM
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

/**
 * Forms the one-dimensional vector that is the vertical average of the input
 * gray image. Not a pipeline stage. The class may not be instantiated; all
 * members are static.
 *
 * @author webb
 */
public class Gray8VertAvg {
    
    /** Creates a new instance of Gray8VertAvg. May not be used. */
    private Gray8VertAvg() {
    }
    
    /**
     * Form the one-dimensional byte vector that is the vertical average of the
     * input gray image.
     *
     * @param image
     *            the input image.
     * @return a byte vector of length equal to the image width. element i of
     *         this vector is the average of column i in the image.
     */
    public static Byte[] push(final Gray8Image<?> image) {
        final Integer[] sum = new Integer[image.getWidth()];
        final Byte[] data = image.getData();
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                sum[j] += data[(i * image.getWidth()) + j];
            }
        }
        final Byte[] result = new Byte[image.getWidth()];
        for (int i = 0; i < image.getWidth(); i++) {
            result[i] = (byte) (sum[i] / image.getHeight());
        }
        return result;
    }
}

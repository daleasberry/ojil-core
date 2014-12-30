/*
 * Gray8Lookup.java
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
 * Pipeline stage applies a lookup table to an image. The lookup table can be
 * supplied through the constructor or by the setTable procedure. This pipeline
 * stage modifies its input.
 *
 * @author webb
 */
public class Gray8Lookup extends PipelineStage {
    private Byte[] table;
    
    /**
     * Creates a new instance of Gray8Lookup.
     * 
     * @param table
     *            The mapping table. Element i maps gray value Byte.MinValue + i
     *            to table[i].
     * @throws ImageError
     *             when table is not a 256-element array.
     */
    public Gray8Lookup(final Byte[] table) throws ImageError {
        setTable(table);
    }
    
    /**
     * Return the lookup table currently being used.
     * 
     * @return the lookup table.
     */
    public Byte[] getTable() {
        final Byte[] result = new Byte[256];
        System.arraycopy(table, 0, result, 0, table.length);
        return result;
    }
    
    /**
     * Maps input Gray8Image through the lookup table, replacing values in the
     * image.
     * 
     * @param image
     *            the input image (output replaces input).
     * @throws ImageError
     *             if image is not a Gray8Image.
     */
    @Override
    public void push(final Image<?> image) throws ImageError {
        if (!(image instanceof Gray8Image)) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.IMAGE_NOT_GRAY8IMAGE, image.toString(), null, null);
        }
        final Gray8Image input = (Gray8Image) image;
        final Byte[] data = input.getData();
        for (int i = 0; i < data.length; i++) {
            data[i] = table[data[i] + 128];
        }
        super.setOutput(input);
    }
    
    /**
     * Assign a new lookup table. Images passed to push() after setTable is
     * called will be mapped by the new image.
     * 
     * @param table
     *            The lookup table. Input image value g is mapped to table[g +
     *            Byte.MinValue]
     * @throws ImageError
     *             if table is not a 256-element array.
     */
    public void setTable(final Byte[] table) throws ImageError {
        if (table.length != 256) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.LOOKUP_TABLE_LENGTH_NOT_256, table.toString(), null, null);
        }
        this.table = new Byte[256];
        System.arraycopy(table, 0, this.table, 0, this.table.length);
    }
}

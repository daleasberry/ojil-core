/*
 * Sequence.java
 *
 * Created on August 27, 2006, 4:33 PM
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
 * Sequence is used to construct a sequence of image processing pipeline stages.
 *
 * Each Sequence object contains a PipelineStage, which can be null, and a
 * Sequence object, which can also be null. The PipelineStage is the head of the
 * list of PipelineStage's starting here, and the Sequence object is the rest of
 * the list. The Sequence object can be non-null only if the PipelineStage
 * object is non-null.
 * 
 * @author webb
 */
public class Sequence extends PipelineStage {
    private PipelineStage pFirst;
    private Sequence pNext;
    
    /** Creates a new instance of Sequence with no pipeline. */
    public Sequence() {
    }
    
    /**
     * Creates a new instance of Sequence with a single PipelineStage.
     *
     * @param p
     *            the PipelineStage.
     */
    public Sequence(final PipelineStage p) {
        pFirst = p;
    }
    
    /**
     * add an additional PipelineStage at the end of the current Sequence.
     *
     * @param p
     *            the PipelineStage to be added.
     */
    public void add(final PipelineStage p) {
        if (pFirst == null) {
            pFirst = p;
        } else {
            if (pNext == null) {
                pNext = new Sequence(p);
            } else {
                pNext.add(p);
            }
        }
    }
    
    /**
     * Returns true iff the pipeline has no image available
     *
     * @return true iff the pipeline has no image available.
     */
    @Override
    public boolean isEmpty() {
        if (pNext == null) {
            return pFirst.isEmpty();
        } else {
            return pNext.isEmpty();
        }
    }
    
    /**
     * Returns the Image produced by the last stage in the pipeline. Overrides
     * PipelineStage.getFront.
     * 
     * @return the Image produced by the pipeline.
     * @throws ImageError
     *             if no image is available.
     */
    @Override
    public Image<?, ?> getFront() throws ImageError {
        if (pNext == null) {
            return pFirst.getFront();
        } else {
            return pNext.getFront();
        }
    }
    
    /**
     * Process an image by the pipeline. The image is pushed onto the beginning
     * of the pipeline, and then each stage's output is passed to the next
     * stage, until the end of the pipeline is reached. Overrides
     * PipelineStage.push(Image).
     * 
     * @param i
     *            the image to be pushed.
     * @throws ImageError
     *             if the pipeline is empty.
     */
    @Override
    public void push(final Image<?, ?> i) throws ImageError {
        if (pFirst == null) {
            throw new ImageError(ImageError.PACKAGE.CORE, ErrorCodes.PIPELINE_EMPTY_PUSH, toString(), null, null);
        }
        pFirst.push(i);
        if (pFirst.isEmpty()) {
            throw new ImageError(ImageError.PACKAGE.CORE, ErrorCodes.NO_RESULT_AVAILABLE, pFirst.toString(), null, null);
        }
        if (pNext != null) {
            pNext.push(pFirst.getFront());
        }
    }
    
    /**
     * Return a string describing the pipeline in fully parenthesized list
     * notation. E.g., a pipeline consisting of three stages A, B, and C will be
     * represented as "(A (B C))".
     *
     * @return the string describing the pipeline.
     */
    @Override
    public String toString() {
        if (pFirst == null) {
            return "(null)"; //$NON-NLS-1$
        } else {
            if (pNext == null) {
                return "(" + pFirst.toString() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            } else {
                return "(" + pFirst.toString() + //$NON-NLS-1$
                        " " + pNext.toString() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
    }
}

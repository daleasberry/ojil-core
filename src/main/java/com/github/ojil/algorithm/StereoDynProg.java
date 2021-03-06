/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 * 
 *  You should have received a copy of the Lesser GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.ojil.algorithm;

/**
 * This is an implementation of stereo matching using dynamic programming.
 * Copyright 2008 by Jon A. Webb
 * 
 * @author webb
 */
public class StereoDynProg {
    private final Integer[] rnRight, rnLeft;
    
    public StereoDynProg(final Integer[] rnLeft, final Integer[] rnRight) {
        this.rnLeft = rnLeft;
        this.rnRight = rnRight;
    }
    
    public Integer[] doMatch() {
        // first build an array matching every column of rnLeft with every
        // column of rnRight
        final Integer[][] rnCost = new Integer[rnLeft.length][rnRight.length];
        for (int i = 0; i < rnLeft.length; i++) {
            for (int j = 0; j < rnRight.length; j++) {
                rnCost[i][j] = Math.abs(rnLeft[i] - rnRight[i]);
            }
        }
        // calculate minimal cost at each node
        final Integer[][] rnMin = new Integer[rnLeft.length][rnRight.length];
        // initialize first row and column
        for (int i = 0; i < rnLeft.length; i++) {
            rnMin[i][0] = rnCost[i][0];
        }
        for (int j = 0; j < rnRight.length; j++) {
            rnMin[0][j] = rnCost[0][j];
        }
        // calculate interior of the array
        for (int i = 1; i < rnLeft.length; i++) {
            for (int j = 1; j < rnRight.length; j++) {
                rnMin[i][j] = rnCost[i][j] + Math.min(Math.min(rnMin[i - 1][j - 1], rnMin[i - 1][j]), rnMin[i][j - 1]);
            }
        }
        // backtrack from terminal node to get match
        final Integer[] rnDepth = new Integer[rnRight.length];
        int nMatchLeft = rnLeft.length - 1;
        int nMatchRight = rnRight.length - 1;
        while (nMatchRight >= 0) {
            rnDepth[nMatchRight] = nMatchLeft;
            // calculate the cost we should match
            final int nCost = rnMin[nMatchLeft][nMatchRight] - rnCost[nMatchLeft][nMatchRight];
            // check if next node is to the left, up, or up and to the left
            if ((nMatchRight > 0) && (nCost == rnMin[nMatchLeft][nMatchRight - 1])) {
                // nMatchLeft stays the same
                nMatchRight--;
            } else if ((nMatchLeft > 0) && (nCost == rnMin[nMatchLeft - 1][nMatchRight])) {
                // nMatchRight stays the same
                nMatchLeft--;
            } else {
                // nMatchRight > 0, nMatchLeft > 0
                // nCost == rnMin[nMatchLeft-1][nMatchRight-1]
                nMatchRight--;
                nMatchLeft--;
            }
        }
        return rnDepth;
    }
}

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

import java.util.Vector;

import com.github.ojil.core.RgbVal;

/**
 * Cluster a vector of RGB values using a simple means-based algorithm.
 * Copyright 2008 by Jon A. Webb
 * 
 * @author webb
 */
public class RgbKCluster {
    public static class RgbCluster {
        int nRedMean, nGreenMean, nBlueMean;
        int nPixels;
        
        public RgbCluster(final int nRed, final int nGreen, final int nBlue, final int nPixels) {
            nRedMean = nRed;
            nGreenMean = nGreen;
            nBlueMean = nBlue;
            this.nPixels = nPixels;
        }
        
        public RgbCluster add(final RgbCluster c) {
            nRedMean = ((nRedMean * nPixels) + (c.nRedMean * c.nPixels)) / (nPixels + c.nPixels);
            nGreenMean = ((nGreenMean * nPixels) + (c.nGreenMean * c.nPixels)) / (nPixels + c.nPixels);
            nBlueMean = ((nBlueMean * nPixels) + (c.nBlueMean * c.nPixels)) / (nPixels + c.nPixels);
            return this;
        }
        
        public int getPixels() {
            return nPixels;
        }
        
        public int getDiff(final RgbCluster c) {
            return Math.abs(nRedMean - c.nRedMean) + Math.abs(nGreenMean - c.nGreenMean) + Math.abs(nBlueMean - c.nBlueMean);
        }
        
        public int getRgb() {
            return RgbVal.toRgb((byte) Math.max(Byte.MIN_VALUE, Math.min(Byte.MAX_VALUE, nRedMean)), (byte) Math.max(Byte.MIN_VALUE, Math.min(Byte.MAX_VALUE, nGreenMean)),
                    (byte) Math.max(Byte.MIN_VALUE, Math.min(Byte.MAX_VALUE, nBlueMean)));
        }
    }
    
    private final int nClusters;
    private final int nTolerance;
    
    public RgbKCluster(final int nClusters, final int nTolerance) {
        this.nClusters = nClusters;
        this.nTolerance = nTolerance;
    }
    
    public Vector<?> cluster(Vector<?> vRgbClusters) {
        final Vector<RgbCluster> vResult = new Vector<>();
        do {
            // find the largest cluster
            RgbCluster cLarge = null;
            for (final Object name : vRgbClusters) {
                final RgbCluster c = (RgbCluster) name;
                if ((cLarge == null) || (cLarge.getPixels() < c.getPixels())) {
                    cLarge = c;
                }
            }
            vRgbClusters.removeElement(cLarge);
            // group all the remaining clusters together with the largest
            // cluster
            // if they fall within a tolerance
            final Vector<RgbCluster> vRemaining = new Vector<>();
            for (final Object name : vRgbClusters) {
                final RgbCluster c = (RgbCluster) name;
                if (cLarge.getDiff(c) < nTolerance) {
                    cLarge.add(c);
                } else {
                    vRemaining.addElement(c);
                }
            }
            vResult.addElement(cLarge);
            vRgbClusters = vRemaining;
        } while ((vResult.size() < nClusters) && (vRgbClusters.size() > 0));
        return vResult;
    }
}

/**
 * 1D Ftt.
 *   Transforms a complex array to a complex array.
 *   The maximum array width is set in the constructor and is used to
 *   precompute the complex roots of unity.
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

import java.util.Vector;

import com.github.ojil.core.Complex;
import com.github.ojil.core.ImageError;
import com.github.ojil.core.MathPlus;

/**
 * Computes 1-dimensional FFT of a complex array.
 */
public class Fft1d {
    private final Vector<Complex[]> cxCoeffs = new Vector<>();
    private int nMaxWidth = 0;
    
    /**
     * Creates a new Fft1d object capable of computing FFTs up to a given
     * maximum width.
     * 
     * @param nMaxWidth
     *            The maximum width to compute FFTs for. This is used to
     *            precompute the complex roots of unity used in the FFT
     *            calculation. Must be a power of 2.
     * @throws com.github.ojil.core.ImageError
     *             if width parameter is not a power of two.
     */
    public Fft1d(final int nMaxWidth) throws com.github.ojil.core.ImageError {
        setMaxWidth(nMaxWidth);
    }
    
    /**
     * Computes forward FFT of the complex array.
     * 
     * @param x
     *            The complex array to compute the FFT of.
     * @return The FFT of the input.
     * @throws com.github.ojil.core.ImageError
     *             if image size is not a power of two or is larger than maximum
     *             width set in the constructor.
     */
    public Complex[] fft(final Complex[] x) throws com.github.ojil.core.ImageError {
        final int N = x.length;
        if ((N & (N - 1)) != 0) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.FFT_SIZE_NOT_POWER_OF_2, new Integer(N).toString(), null, null);
            
        }
        if (N > nMaxWidth) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.FFT_SIZE_LARGER_THAN_MAX, new Integer(N).toString(), null, null);
        }
        // compute log2 N
        int nLog = 1;
        int nTwoExp = 2; // nTwoExp = 2**(nLog)
        while (nTwoExp < N) {
            nLog++;
            nTwoExp <<= 1;
        }
        return fft(x, nLog);
    }
    
    // compute the FFT of x[], for lengths a power of 2
    // nLog is the logarithm of the length of x to base 2
    private Complex[] fft(final Complex[] x, final int nLog) throws com.github.ojil.core.ImageError {
        final int N = x.length;
        
        // base case
        if (N == 1) {
            return new Complex[] { new Complex(x[0]) };
        }
        
        // fft of even terms
        final Complex[] even = new Complex[N / 2];
        for (int k = 0; k < (N / 2); k++) {
            even[k] = new Complex(x[2 * k]);
        }
        final Complex[] q = fft(even, nLog - 1);
        
        // fft of odd terms
        final Complex[] odd = even; // reuse the array
        for (int k = 0; k < (N / 2); k++) {
            odd[k] = new Complex(x[(2 * k) + 1]);
        }
        final Complex[] r = fft(odd, nLog - 1);
        
        // combine
        final Complex[] y = new Complex[N];
        for (int k = 0; k < (N / 2); k++) {
            // read complex root of unity from pre-computed array
            final Complex wk = new Complex(cxCoeffs.elementAt(nLog - 1)[k]);
            // since we're multiplying two numbers scaled by 2**16 we must
            // divide
            // by 2**16 = 256 * 256, carefully to reduce loss of precision
            final Complex cxProd = wk.rsh(8).times(new Complex(r[k]).rsh(8));
            // code below is somewhat strange because complex arithmetic
            // modifies the left argument, always
            y[k] = new Complex(q[k]).plus(cxProd);
            y[k + (N / 2)] = new Complex(q[k]).minus(cxProd);
        }
        return y;
    }
    
    // compute the inverse FFT of x[], for length a power of 2
    /**
     * Computes inverse FFT of the input complex array.
     * 
     * @return The FFT of the input.
     * @param x
     *            The input complex array.
     * @throws com.github.ojil.core.ImageError
     *             if the input size is not a power of two or is larger than the
     *             maximum set in the constructor.
     */
    public Complex[] ifft(final Complex[] x) throws com.github.ojil.core.ImageError {
        final int N = x.length;
        Complex[] y = new Complex[N];
        
        // take conjugate
        for (int i = 0; i < N; i++) {
            y[i] = x[i].conjugate();
        }
        
        // compute forward FFT
        y = fft(y);
        
        // take conjugate again and divide by N
        for (int i = 0; i < N; i++) {
            y[i].conjugate().div(N);
        }
        
        return y;
    }
    
    /**
     * Sets a new maximum width. If smaller than what has been specified before,
     * no effect. Otherwise computes some more complex roots of unity.
     * 
     * @param N
     *            The new maximum width.
     * @throws com.github.ojil.core.ImageError
     *             If N is not a power of 2.
     */
    public void setMaxWidth(final int N) throws com.github.ojil.core.ImageError {
        if ((N & (N - 1)) != 0) {
            throw new ImageError(ImageError.PACKAGE.ALGORITHM, AlgorithmErrorCodes.FFT_SIZE_NOT_POWER_OF_2, new Integer(N).toString(), null, null);
        }
        // we precompute the coefficients (complex roots of unity)
        // used in the FFT calculation
        // for all n up to the size we'll need for the FFT
        int nLog = 0;
        int nTwoExp = 1; // nTwoExp = 2**(nLog)
        while (nTwoExp <= N) {
            // check to see if we've already filled in the array for this
            // length
            if (cxCoeffs.size() < nLog) {
                // we didn't fill it in, add it now
                final Complex[] cx = new Complex[nTwoExp / 2];
                for (int k = 0; k < (nTwoExp / 2); k++) {
                    // kth is scaled by 2**16 because of the use of MathPlus.PI
                    final int kth = (-2 * k * MathPlus.PI) / nTwoExp;
                    // compute root of unity
                    cx[k] = MathPlus.expImag(kth);
                }
                cxCoeffs.addElement(cx);
            }
            nLog++;
            nTwoExp <<= 1;
        }
        nMaxWidth = Math.max(nMaxWidth, N);
    }
    
    /**
     * String describing current FFT instance.
     * 
     * @return The name and maximum width of this instance.
     */
    @Override
    public String toString() {
        return super.toString() + "(" + nMaxWidth + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }
}
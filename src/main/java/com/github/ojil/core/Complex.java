/*
 * Complex.java
 *   Implementation of complex numbers, for use in FFT etc.
 *   This uses integers to store the real and imaginary components.
 *   Scale arguments to the constructor appropriately.
 *   All operations are done in-place so, e.g., x.div(y) modifies x.
 *
 * Created on October 29, 2007, 12:53 PM
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
 * A simple implementation of complex numbers for use in FFT, etc.
 * 
 * @author webb
 */
public class Complex extends Number {
    private static final long serialVersionUID = 777755552694276640L;
    
    private int nImag;
    private int nReal;
    
    /**
     * Default constructor.
     */
    public Complex() {
        nReal = 0;
        nImag = 0;
    }
    
    /**
     * Copy constructor.
     * 
     * @param cx
     *            the complex number to copy.
     */
    public Complex(final Complex cx) {
        nReal = cx.nReal;
        nImag = cx.nImag;
    }
    
    /**
     * Creates a new instance of Complex from real and imaginary arguments.
     * 
     * @param nReal
     *            Real component.
     * @param nImag
     *            Imaginary component.
     */
    public Complex(final int nReal, final int nImag) {
        this.nReal = nReal;
        this.nImag = nImag;
    }
    
    /**
     * Create a new Complex number from a real number. The imaginary component
     * will be 0.
     * 
     * @param nReal
     *            The real component.
     */
    public Complex(final int nReal) {
        this.nReal = nReal;
        nImag = 0;
    }
    
    /**
     * Complex conjugate
     * 
     * @return the complex conjugate of this.
     */
    public Complex conjugate() {
        nImag = -nImag;
        return this;
    }
    
    /**
     * Divide the complex number by a real ineger.
     * 
     * @param n
     *            the divisor.
     * @return the Complex number resulting from the division (replaces this).
     * @throws ImageError
     *             if n = 0
     */
    public Complex div(final int n) throws ImageError {
        if (n == 0) {
            throw new ImageError(ImageError.PACKAGE.CORE, ErrorCodes.MATH_DIVISION_ZERO, toString(), new Integer(n).toString(), null);
        }
        nReal /= n;
        nImag /= n;
        return this;
    }
    
    /**
     * Divides one complex number by another
     * 
     * @param cx
     *            The complex number to divide by.
     * @return the result of dividing this number by cx.
     * @throws ImageError
     *             If division by 0 is attempted, i.e., cx.square() is 0.
     */
    public Complex div(Complex cx) throws ImageError {
        int nShift = 0;
        if ((Math.abs(cx.real()) >= MathPlus.SCALE) || (Math.abs(cx.imag()) >= MathPlus.SCALE)) {
            cx = new Complex(cx).rsh(MathPlus.SHIFT);
            nShift = MathPlus.SHIFT;
        }
        final int nSq = cx.square();
        if (nSq == 0) {
            throw new ImageError(ImageError.PACKAGE.CORE, ErrorCodes.MATH_PRODUCT_TOO_LARGE, toString(), cx.toString(), null);
        }
        // cx is right shifted by SHIFT bits. So multiplying by it and
        // dividing by its square shifts left by SHIFT bits. We shift back to
        // compensate
        final int nR = (((nReal * cx.nReal) + (nImag * cx.nImag)) / nSq) >> nShift;
        final int nI = (((nImag * cx.nReal) - (nReal * cx.nImag)) / nSq) >> nShift;
        nReal = nR;
        nImag = nI;
        return this;
    }
    
    /**
     * Equality test.
     * 
     * @param cx
     *            the Complex number to compare with.
     * @return true iff the two Complex numbers are equal.
     */
    public boolean equals(final Complex cx) {
        return (nReal == cx.nReal) && (nImag == cx.nImag);
    }
    
    /**
     * The imaginary component of the complex number.
     * 
     * @return the imaginary component.
     */
    public int imag() {
        return nImag;
    }
    
    /**
     * Shifts a complex number left a certain number of bits.
     * 
     * @param n
     *            The number of bits to shift by.
     * @return the result of shifting the complex number left the number of
     *         bits.
     */
    public Complex lsh(final int n) {
        nReal <<= n;
        nImag <<= n;
        return this;
    }
    
    /**
     * Complex magnitude.
     * 
     * @return the magnitude of this number, i.e., sqrt(real**2 + imag**2)
     * @throws ImageError
     *             if the square value computed is too large.
     */
    public int magnitude() throws ImageError {
        // special case when one component is 0
        if ((nReal == 0) || (nImag == 0)) {
            return Math.abs(nReal) + Math.abs(nImag);
        }
        // try to extend the range of numbers we can take the magnitude of
        // beyond
        // 2**16
        if ((Math.abs(nReal) > (MathPlus.SCALE >> 1)) || (Math.abs(nImag) > (MathPlus.SCALE >> 1))) {
            // squaring the number will result in overflow
            // so we shift right first instead
            final int nR = nReal >> MathPlus.SHIFT;
            final int nI = nImag >> MathPlus.SHIFT;
            return MathPlus.sqrt((nR * nR) + (nI * nI)) << MathPlus.SHIFT;
        } else {
            return MathPlus.sqrt(square());
        }
    }
    
    /**
     * Subtracts one complex number from another.
     * 
     * @param cx
     *            the complex number to subtract.
     * @return the difference of the two complex numbers.
     */
    public Complex minus(final Complex cx) {
        nReal -= cx.nReal;
        nImag -= cx.nImag;
        return this;
    }
    
    /**
     * Adds two complex numbers.
     * 
     * @param cx
     *            the complex number to add.
     * @return the sum of the two complex numbers.
     */
    public Complex plus(final Complex cx) {
        nReal += cx.nReal;
        nImag += cx.nImag;
        return this;
    }
    
    /**
     * The real component of the complex number.
     * 
     * @return the real component of the complex number.
     */
    public int real() {
        return nReal;
    }
    
    /**
     * Shifts a complex number right a certain number of bits.
     * 
     * @param n
     *            The number of bits to shift by.
     * @return the result of shifting the complex number the number of bits.
     */
    public Complex rsh(final int n) {
        nReal >>= n;
            nImag >>= n;
            return this;
    }
    
    /**
     * Computes the absolute square.
     * 
     * @return The absolute square, i.e, real**2 + imag**2.
     * @throws ImageError
     *             if Complex value is too large.
     */
    public int square() throws ImageError {
        if ((Math.abs(nReal) > MathPlus.SCALE) || (Math.abs(nImag) > MathPlus.SCALE)) {
            throw new ImageError(ImageError.PACKAGE.CORE, ErrorCodes.MATH_SQUARE_TOO_LARGE, toString(), null, null);
        }
        return (nReal * nReal) + (nImag * nImag);
    }
    
    /**
     * Multiplies two complex numbers.
     * 
     * @param cx
     *            The complex number to multiply by.
     * @return The product of the two numbers.
     */
    public Complex times(final Complex cx) {
        final int nR = (nReal * cx.nReal) - (nImag * cx.nImag);
        final int nI = (nReal * cx.nImag) + (nImag * cx.nReal);
        nReal = nR;
        nImag = nI;
        return this;
    }
    
    /**
     * Multiplies a complex number by a real number.
     * 
     * @param nX
     *            The complex number to multiply by.
     * @return The product of the two numbers.
     */
    public Complex times(final int nX) {
        final int nR = nReal * nX;
        final int nI = nReal * nX;
        nReal = nR;
        nImag = nI;
        return this;
    }
    
    /**
     * Returns a String representation of the complex number
     * 
     * @return the string (real, imag)
     */
    @Override
    public String toString() {
        return "(" + nReal + ", " + nImag + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    
    @Override
    public double doubleValue() {
        return nReal;
    }
    
    @Override
    public float floatValue() {
        return nReal;
    }
    
    @Override
    public int intValue() {
        return nReal;
    }
    
    @Override
    public long longValue() {
        return nReal;
    }
}

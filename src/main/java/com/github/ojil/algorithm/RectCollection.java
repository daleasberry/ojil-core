/**
 * Copyright 2008 by Jon A. Webb
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

import java.util.Enumeration;
import java.util.Vector;

import com.github.ojil.core.ImageError;
import com.github.ojil.core.Point;
import com.github.ojil.core.Rect;

/**
 * RectCollection includes a data structure and algorithms to efficiently
 * represent a collection of axis-aligned rectangles and allow the fast (O(log
 * n)) determination of whether a given point is in one of them.
 * 
 * @author webb
 */
public class RectCollection {
    
    /**
     * Vector of all rectangles in the collection.
     */
    private Vector<Rect> vAllRect = new Vector<>();
    
    /**
     * treeHoriz is a collection of all the rectangles, projected horizontally.
     * Each node in the tree contains the rectangles at that y-coordinate. There
     * is one y-coordinate for each unique top or bottom position in a
     * rectangle.
     */
    private ThreadedBinaryTree treeHoriz = null;
    /**
     * treeVert is a collection of all the rectangles, projected vertically.
     * Each node in the tree contains the rectangles at that x-coordinate. There
     * is one x-coordinate for each unique left and right position in a
     * rectangle
     */
    private ThreadedBinaryTree treeVert = null;
    
    /**
     * Default constructor.
     */
    public RectCollection() {
    }
    
    /**
     * Add a new rectangle to the collection. This includes adding its top and
     * bottom coordinates to the horizontal projection collection and its left
     * and right coordinates to the vertical projection collection, and adding
     * the rectangle itself to every rectangle list between its starting and
     * ending coordinates.
     * 
     * @param r
     *            the rectangle to add
     * @throws ImageError
     *             in the case of type error (key wrong type)
     */
    public void add(final Rect r) throws ImageError {
        vAllRect.addElement(r);
        treeHoriz = addRect(r, r.getLeft(), r.getTop() + r.getHeight(), treeHoriz);
        treeVert = addRect(r, r.getLeft(), r.getTop() + r.getWidth(), treeVert);
    }
    
    /**
     * Add a rectangle to all rectangle lists between a starting and ending
     * coordinate. First we get pointers to the nodes in the trees for the
     * starting and ending coordinates. Then we add the rectangle to all the
     * lists in the inorder traversal from the start to the end node.
     * 
     * @param r
     *            the rectangle to add
     * @param start
     *            starting coordinate
     * @param end
     *            ending coordinate
     * @param tbtRoot
     *            the root of the ThreadedBinaryTree that we are modifying
     * @return the modified binary tree (= tbtRoot if it already exists,
     *         otherwise it will be created)
     * @throws ImageError
     *             in the case of type error (key wrong type)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private ThreadedBinaryTree addRect(final Rect r, final int start, final int end, ThreadedBinaryTree tbtRoot) throws ImageError {
        // get lists of Rectangles enclosing the given rectangle start and end
        ThreadedBinaryTree tbtFind = null;
        if (tbtRoot != null) {
            tbtFind = tbtRoot.findNearest(new BinaryHeap.ComparableInt(start));
        }
        Vector<Rect> vExistingStart = null;
        if (tbtFind != null) {
            vExistingStart = (Vector<Rect>) tbtFind.getValue();
        }
        Vector<Rect> vExistingEnd = null;
        if (tbtRoot != null) {
            tbtFind = tbtRoot.findNearest(new BinaryHeap.ComparableInt(end));
        }
        if (tbtFind != null) {
            vExistingEnd = (Vector<Rect>) tbtFind.getValue();
        }
        // now add the new points to the tree
        ThreadedBinaryTree tbtStart = null;
        // the ThreadedBinaryTree may not already exist. Create it if necessary
        if (tbtRoot == null) {
            tbtRoot = tbtStart = new ThreadedBinaryTree(new BinaryHeap.ComparableInt(start));
        } else {
            // already exists, add or get the start node
            tbtStart = tbtRoot.add(new BinaryHeap.ComparableInt(start));
            // add existing enclosing rectangles to this node's list
            // if it doesn't already exist
            if (vExistingStart != null) {
                if (tbtStart.getValue() == null) {
                    final Vector<Rect> v = new Vector<>();
                    for (final Rect rect : vExistingStart) {
                        v.addElement(rect);
                    }
                    tbtStart.setValue(v);
                }
            }
        }
        // add or get the end node
        final ThreadedBinaryTree tbtEnd = tbtRoot.add(new BinaryHeap.ComparableInt(end));
        // add existing enclosing rectangles to this node's list
        // if it doesn't already exist
        if (vExistingEnd != null) {
            if (tbtEnd.getValue() == null) {
                final Vector<Rect> v = new Vector<>();
                for (final Rect rect : vExistingEnd) {
                    v.addElement(rect);
                }
                tbtEnd.setValue(v);
            }
        }
        // now traverse the path from tbtStart to tbtEnd and add r to all
        // rectangle lists
        final Vector<Rect> vTrav = tbtStart.inorderTraverse(tbtEnd);
        // for eacn node in the inorder traversal, create the Vector of
        // rectangles if necessary, and put this rectangle on it
        for (final Object name : vTrav) {
            final ThreadedBinaryTree tbt = (ThreadedBinaryTree) name;
            // Vector doesn't exist
            if (tbt.getValue() == null) {
                final Vector v = new Vector();
                v.addElement(r);
                // set value
                tbt.setValue(v);
            } else {
                // already exists, modify value
                ((Vector) tbt.getValue()).addElement(r);
            }
        }
        // return modified / created tree
        return tbtRoot;
    }
    
    /**
     * Clear collection.
     */
    public void clear() {
        vAllRect = new Vector<>();
        treeHoriz = null;
        treeVert = null;
    }
    
    /**
     * Determines whether a point is in the collection by intersecting the lists
     * of rectangles that the point projects into horizontally and vertically,
     * and then determining if the point lies in one of the rectangles in the
     * intersection.
     * 
     * @param p
     *            the point to test
     * @return a Rect that contains p if it is contained by any Rect, otherwise
     *         null
     * @throws ImageError
     *             in the case of type error (key wrong type)
     */
    public Rect contains(final Point p) throws ImageError {
        // if no rectangles are in collection answer is null
        if ((treeHoriz == null) || (treeVert == null)) {
            return null;
        }
        final ThreadedBinaryTree tbtHorizProj = treeHoriz.findNearest(new BinaryHeap.ComparableInt(p.getY()));
        final ThreadedBinaryTree tbtVertProj = treeVert.findNearest(new BinaryHeap.ComparableInt(p.getX()));
        // if no tree node is <= this point the return null
        if ((tbtHorizProj == null) || (tbtVertProj == null)) {
            return null;
        }
        // check for intersection between two lists; if non-empty check
        // for contains
        final Vector<?> vHoriz = (Vector<?>) tbtHorizProj.getValue();
        final Vector<?> vVert = (Vector<?>) tbtVertProj.getValue();
        for (final Object name : vHoriz) {
            final Rect r = (Rect) name;
            for (final Object name2 : vVert) {
                final Rect s = (Rect) name2;
                if (r == s) {
                    if (s.contains(p)) {
                        return s;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Returns an enumeration of all rectangles in the collection
     * 
     * @return Enumeration of all rectangles in order they were added to the
     *         collection.
     */
    public Enumeration<Rect> elements() {
        return vAllRect.elements();
    }
    
    /**
     * Implement toString
     */
    @Override
    public String toString() {
        String szRes = super.toString() + "(all=" + vAllRect.toString() + ",horiz=";
        if (treeHoriz != null) {
            szRes += treeHoriz.toString();
        } else {
            szRes += "null";
        }
        szRes += ",vert=";
        ;
        if (treeVert != null) {
            szRes += treeVert.toString();
        } else {
            szRes += "null";
        }
        szRes += ")";
        return szRes;
    }
}

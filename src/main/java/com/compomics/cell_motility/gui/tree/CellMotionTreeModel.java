/*
 * Copyright (C) Lennart Martens
 * 
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * 
 * 
 * Contact: lennart.martens AT UGent.be (' AT ' to be replaced with '@')
 */

/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 22-sep-2005
 * Time: 7:51:13
 */
package com.compomics.cell_motility.gui.tree;
/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2005/12/19 12:44:32 $
 */

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2005/12/19 12:44:32 $
 */

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;

/**
 * This class implements a tree model for cell motion paths, based on a Collection.
 *
 * @author Lennart Martens
 * @version $Id: CellMotionTreeModel.java,v 1.3 2005/12/19 12:44:32 lennart Exp $
 */
public class CellMotionTreeModel implements TreeModel {

    /**
     * This Collection will hold all the clusters.
     */
    private ArrayList iCellMotionPaths = null;

    /**
     * This constructor takes a list of clusters.
     *
     * @param aCellMotionPaths ArrayList with the cell motion paths.
     */
    public CellMotionTreeModel(ArrayList aCellMotionPaths) {
        iCellMotionPaths = aCellMotionPaths;
    }

    /**
     * Adds a listener for the <code>TreeModelEvent</code>
     * posted after the tree changes.
     *
     * @param l the listener to add
     * @see #removeTreeModelListener
     */
    public void addTreeModelListener(TreeModelListener l) {
        // Not implemented.
    }

    /**
     * Returns the child of <code>parent</code> at index <code>index</code>
     * in the parent's
     * child array.  <code>parent</code> must be a node previously obtained
     * from this data source. This should not return <code>null</code>
     * if <code>index</code>
     * is a valid index for <code>parent</code> (that is <code>index >= 0 &&
     * index < getChildCount(parent</code>)).
     *
     * @param parent a node in the tree, obtained from this data source
     * @return the child of <code>parent</code> at index <code>index</code>
     */
    public Object getChild(Object parent, int index) {
        Object result = null;
        if("Cell Motion Path Files".equals(parent)) {
            result = iCellMotionPaths.get(index);
        } else if(iCellMotionPaths.contains(parent)) {
            // Not implemented yet.
        }
        return result;
    }

    /**
     * Returns the number of children of <code>parent</code>.
     * Returns 0 if the node
     * is a leaf or if it has no children.  <code>parent</code> must be a node
     * previously obtained from this data source.
     *
     * @param parent a node in the tree, obtained from this data source
     * @return the number of children of the node <code>parent</code>
     */
    public int getChildCount(Object parent) {
        int count = 0;
        if("Cell Motion Path Files".equals(parent)) {
            count = iCellMotionPaths.size();
        } else if(iCellMotionPaths.contains(parent)) {
            // Not implemented yet.
        }
        return count;
    }

    /**
     * Returns the index of child in parent.  If <code>parent</code>
     * is <code>null</code> or <code>child</code> is <code>null</code>,
     * returns -1.
     *
     * @param parent a note in the tree, obtained from this data source
     * @param child  the node we are interested in
     * @return the index of the child in the parent, or -1 if either
     *         <code>child</code> or <code>parent</code> are <code>null</code>
     */
    public int getIndexOfChild(Object parent, Object child) {
        int index = 0;
        if(iCellMotionPaths.contains(parent)) {
            // Not implemented yet.
        }
        return index;
    }

    /**
     * Returns the root of the tree.  Returns <code>null</code>
     * only if the tree has no nodes.
     *
     * @return the root of the tree
     */
    public Object getRoot() {
        return "Cell Motion Path Files";
    }

    /**
     * Returns <code>true</code> if <code>node</code> is a leaf.
     * It is possible for this method to return <code>false</code>
     * even if <code>node</code> has no children.
     * A directory in a filesystem, for example,
     * may contain no files; the node representing
     * the directory is not a leaf, but it also has no children.
     *
     * @param node a node in the tree, obtained from this data source
     * @return true if <code>node</code> is a leaf
     */
    public boolean isLeaf(Object node) {
        boolean leaf = false;
        if(!"Cell Motion Path Files".equals(node) && !iCellMotionPaths.contains(node)) {
            leaf = true;
        }
        return leaf;
    }

    /**
     * Removes a listener previously added with
     * <code>addTreeModelListener</code>.
     *
     * @param l the listener to remove
     * @see #addTreeModelListener
     */
    public void removeTreeModelListener(TreeModelListener l) {
        // not implemented.
    }

    /**
     * Messaged when the user has altered the value for the item identified
     * by <code>path</code> to <code>newValue</code>.
     * If <code>newValue</code> signifies a truly new value
     * the model should post a <code>treeNodesChanged</code> event.
     *
     * @param path     path to the node that the user has altered
     * @param newValue the new value from the TreeCellEditor
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
        // Not implemented.
    }
}

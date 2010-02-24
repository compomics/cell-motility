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
 * Time: 16:21:47
 */
package com.compomics.cell_motility.gui.tree.renderers;
/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2005/12/19 12:44:32 $
 */

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.HashMap;

/**
  * This class provides a TreeCellRenderer that show the .
 *
 * @author Lennart Martens
 * @version $Id: CellMotionTreeCellRenderer.java,v 1.2 2005/12/19 12:44:32 lennart Exp $
 */
public class CellMotionTreeCellRenderer extends DefaultTreeCellRenderer {

    /**
     * The HashMap in which the String representation of the tree leaf serves
     * as as key, and the Identification as value. If this is 'null', this
     * renderer behaves exactly like the default renderer.
     */
    private HashMap iIDLookup = null;

    /**
     * This constructor initializes the renderer.
     *
     * @param aIDLookup HashMap in which the String representation of the tree leaf serves
     *                  as as key, and the Identification as value. If this is 'null', this
     *                  renderer behaves exactly like the default renderer.
     */
    public CellMotionTreeCellRenderer(HashMap aIDLookup) {
        this.iIDLookup = aIDLookup;
    }

    /**
     * Sets the value of the current tree cell to <code>value</code>.
     * If <code>selected</code> is true, the cell will be drawn as if
     * selected. If <code>expanded</code> is true the node is currently
     * expanded and if <code>leaf</code> is true the node represets a
     * leaf and if <code>hasFocus</code> is true the node currently has
     * focus. <code>tree</code> is the <code>JTree</code> the receiver is being
     * configured for.  Returns the <code>Component</code> that the renderer
     * uses to draw the value.
     *
     * @return	the <code>Component</code> that the renderer uses to draw the value
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(
                                tree, value, selected,
                                expanded, leaf, row,
                                hasFocus);
        if(iIDLookup != null && leaf && (iIDLookup.get(value) == null)) {
            this.setForeground(Color.red);
        }

        return this;
    }
}

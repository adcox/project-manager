package adcox.projectmanager.tools;

import e.util.GuiUtilities;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;


public class ETree extends JTree {
    //Variables
    private static final Color[] rowColors = {GuiUtilities.MAC_OS_ALTERNATE_ROW_COLOR, Color.white};
    private RendererEditorWrapper wrapper = null;
    
    /** Constructs a new tree with the given root. */
    public ETree(DefaultTreeModel model) {
        super(model);
    }

    /** Expands all the nodes in this tree. */
    public void expandAll() {
        expandOrCollapsePath(new TreePath(getModel().getRoot()), true);
    }

    /** Collapses all the nodes in this tree. */
    public void collapseAll() {
        expandOrCollapsePath(new TreePath(getModel().getRoot()), false);
    }
    
    /** Expands or collapses all nodes beneath the given path represented as an array of nodes. */
    public void expandOrCollapsePath(TreeNode[] nodes, boolean expand) {
        expandOrCollapsePath(new TreePath(nodes), expand);
    }
    
    /** Expands or collapses all nodes beneath the given path. */
    private void expandOrCollapsePath(TreePath parent, boolean expand) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandOrCollapsePath(path, expand);
            }
        }
        if (expand) {
            expandPath(parent);
        } else {
            collapsePath(parent);
        }
    }
    
    /**
     * Selects the nodes matching the given string. The matching is
     * a case-insensitive substring match. The selection is not cleared
     * first; you must do this yourself if it's the behavior you want.
     * 
     * If ensureVisible is true, the first selected node in the model
     * will be made visible via scrollPathToVisible.
     */
    public void selectNodesMatching(String string, boolean ensureVisible) {
        TreePath path = new TreePath(getModel().getRoot());
        selectNodesMatching(path, string.toLowerCase());
        if (ensureVisible) {
            scrollPathToVisible(getSelectionPath());
        }
    }
    
    private void selectNodesMatching(TreePath parent, String string) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                selectNodesMatching(path, string);
            }
        }
        if (node.toString().toLowerCase().contains(string)) {
            addSelectionPath(parent);
        }
    }
    
    /** Scrolls the path to the middle of the scroll pane. */
    public void scrollPathToVisible(TreePath path) {
        if (path == null) {
            return;
        }
        makeVisible(path);
        Rectangle pathBounds = getPathBounds(path);
        if (pathBounds != null) {
            Rectangle visibleRect = getVisibleRect();
            if (getHeight() > visibleRect.height) {
                int y = pathBounds.y - visibleRect.height / 2;
                visibleRect.y = Math.min(Math.max(0, y), getHeight() - visibleRect.height);
                scrollRectToVisible(visibleRect);
            }
        }
    }
    
    /**
     * Makes JTree's implementation less width-greedy. Left to JTree, we'll
     * grow to be wide enough to show our widest node without using a scroll
     * bar. While this is seemingly widely acceptable (ho ho), it's no good
     * in Evergreen's "Find in Files" dialog. If long lines match, next time you
     * open the dialog, it can be so wide it doesn't fit on the screen. Here,
     * we go for the minimum width, and assume that an ETree is never packed
     * on its own (in which case, it might end up rather narrow by default).
     */
    public Dimension getPreferredScrollableViewportSize() {
        Dimension size = super.getPreferredScrollableViewportSize();
        size.width = getMinimumSize().width;
        return size;
    }
    
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//====Add/Remove Nodes==========================================================
    
    /** Remove the currently selected node. */
    public void removeCurrentNode() {
        TreePath currentSelection = getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)(currentSelection.getLastPathComponent());
            MutableTreeNode parent = (MutableTreeNode)(currentNode.getParent());
            if (parent != null) {
                DefaultTreeModel model = (DefaultTreeModel)getModel();
                model.removeNodeFromParent(currentNode);
                return;
            }
        } 
    }//====================================

    /** Add child to the currently selected node. */
    public DefaultMutableTreeNode addObject(Object child) {
        DefaultMutableTreeNode parentNode = null;
        TreePath parentPath = getSelectionPath();

        if(parentPath == null){
            parentNode = (DefaultMutableTreeNode)this.getModel().getRoot();
        }else{
            parentNode = (DefaultMutableTreeNode)(parentPath.getLastPathComponent());
        }

        return addObject(parentNode, child, true);
    }//============================================

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child){
        return addObject(parent, child, false);
    }//=====================================================

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child, 
                                            boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode = 
                new DefaultMutableTreeNode(child);

        if (parent == null) {
            parent = (DefaultMutableTreeNode)this.getModel().getRoot();
        }
	
	//It is key to invoke this on the TreeModel, and NOT DefaultMutableTreeNode
        ((DefaultTreeModel)getModel()).insertNodeInto(childNode, parent, 
                                 parent.getChildCount());

        //Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        return childNode;
    }//=========================================================
    
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//====Line Coloring=============================================================
    
    /** Add zebra stripes to the background. */
    public void paintComponent(Graphics g){
        if(!isOpaque()){
            super.paintComponent( g );
            return;
        }
 
        // Paint zebra background stripes
        final Insets insets = getInsets();
        final int w = getWidth()  - insets.left - insets.right;
        final int h = getHeight() - insets.top  - insets.bottom;
        final int x = insets.left;
        int y = insets.top;
        int nRows = 0;
        int startRow = 0;
        
        if (rowHeight > 0)
            nRows = h / rowHeight;
        else{
            // Paint non-uniform height rows first
            final int nItems = getRowCount();
            rowHeight = 17; // A default for empty trees
            
            for(int i = 0; i < nItems; i++, y +=rowHeight){
                rowHeight = getRowBounds( i ).height;
                g.setColor( rowColors[i&1] );
                g.fillRect( x, y, w, rowHeight );
            }
            
            // Use last row height for remainder of tree area
            nRows    = nItems + (insets.top + h - y) / rowHeight;
            startRow = nItems;
        }
        
        for (int i = startRow; i < nRows; i++, y += rowHeight){
            g.setColor( rowColors[i&1] );
            g.fillRect( x, y, w, rowHeight );
        }
        
        final int remainder = insets.top + h - y;
        
        if(remainder > 0){
            g.setColor( rowColors[nRows&1] );
            g.fillRect( x, y, w, remainder );
        }
 
        // Paint component
        setOpaque( false );
        super.paintComponent( g );
        setOpaque( true );
    }//==========================================
 
    /** Wrap cell renderer and editor to add zebra background stripes. */
    private class RendererEditorWrapper implements TreeCellRenderer, TreeCellEditor{
        public TreeCellRenderer ren = null;
        public TreeCellEditor   ed  = null;
 
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                        boolean selected, boolean expanded,boolean leaf, int row, boolean hasFocus ){
            
            final Component c = ren.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus );
 
            if (selected)
                return c;
            if ( !(c instanceof DefaultTreeCellRenderer) )
                c.setBackground( rowColors[row&1] );
            else
                ((DefaultTreeCellRenderer)c).setBackgroundNonSelectionColor( rowColors[row&1] );
            return c;
        }//=======================================
 
        public Component getTreeCellEditorComponent(JTree tree, Object value,
                    boolean selected, boolean expanded, boolean leaf, int row ){
            
            final Component c = ed.getTreeCellEditorComponent(tree, value, 
                    selected, expanded, leaf, row );
            
            if (!selected)
                c.setBackground( rowColors[row&1] );
            
            return c;
        }//==========================================
 
        public void addCellEditorListener(CellEditorListener l ){
            ed.addCellEditorListener( l );
        }//=======================================
        
        public void cancelCellEditing( ){
            ed.cancelCellEditing( );
        }//=======================================
        
        public Object getCellEditorValue( ){
            return ed.getCellEditorValue( );
        }//=======================================
        
        public boolean isCellEditable(java.util.EventObject anEvent ){
            return ed.isCellEditable( anEvent );
        }//=======================================
        
        public void removeCellEditorListener(javax.swing.event.CellEditorListener l ){
            ed.removeCellEditorListener( l );
        }//=======================================
        
        public boolean shouldSelectCell(java.util.EventObject anEvent ){
            return ed.shouldSelectCell( anEvent );
        }//=======================================
        
        public boolean stopCellEditing( ){
            return ed.stopCellEditing( );
        }//=======================================
    }//=======================================
 
    /** Return the wrapped cell renderer. */
    public javax.swing.tree.TreeCellRenderer getCellRenderer( ){
        final javax.swing.tree.TreeCellRenderer ren = super.getCellRenderer( );
        if ( ren == null )
            return null;
        if ( wrapper == null )
            wrapper = new RendererEditorWrapper( );
        wrapper.ren = ren;
        return wrapper;
    }//==========================================
 
    /** Return the wrapped cell editor. */
    public javax.swing.tree.TreeCellEditor getCellEditor( ){
        final javax.swing.tree.TreeCellEditor ed = super.getCellEditor( );
        if ( ed == null )
            return null;
        if ( wrapper == null )
            wrapper = new RendererEditorWrapper( );
        wrapper.ed = ed;
        
        return wrapper;
    }//==================================
    
    public Object getSelectedObject(){
        return ((DefaultMutableTreeNode)getSelectionPath().getLastPathComponent()).getUserObject();
    }//==========================================
    
    public DefaultMutableTreeNode getSelectedNode(){
        return (DefaultMutableTreeNode)getSelectionPath().getLastPathComponent();
    }//====================================
 
}

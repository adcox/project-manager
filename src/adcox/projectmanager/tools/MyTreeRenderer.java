/**
 *  Project Manager: A program that assists you in managing your programming
 *  projects.
 * 
 *  Copyright (C) 2011 Andrew Cox
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  To contact me, send an email to <andrewandstuff@gmail.com>
 */
package adcox.projectmanager.tools;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;

import adcox.projectmanager.objects.Project;

/**
 *
 * @author Andrew
 * @version Jul 24, 2011
 */
public class MyTreeRenderer extends DefaultTreeCellRenderer{
    ImageIcon projectIcon;
    
    public MyTreeRenderer(ImageIcon icon) {
        projectIcon = icon;
    }//============================

    @Override
    public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {

        super.getTreeCellRendererComponent(
                        tree, value, sel,
                        expanded, leaf, row,
                        hasFocus);
        if (isProject(value)) {
            setIcon(projectIcon);
            Object userObject = ((DefaultMutableTreeNode)value).getUserObject();
            if(((Project)userObject).isMain()){
                setFont(new Font(getFont().getName(), Font.BOLD, getFont().getSize()));
            }
            else{
                setFont(new Font(getFont().getName(), Font.PLAIN, getFont().getSize()));
            }
        }
        else{
            //make sure other leaves aren't bold
            setFont(new Font(getFont().getName(), Font.PLAIN, getFont().getSize()));
        }

        return this;
    }//=========================================

    protected boolean isProject(Object value) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        if(node.getUserObject() instanceof Project){
            //System.out.println(node.toString() + " is a project");
            return true;
        }
        return false;
    }//============================================
}

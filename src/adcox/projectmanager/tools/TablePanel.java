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
import java.awt.*;
/**
 *
 * @author Andrew
 * @version Jul 21, 2011
 */
public class TablePanel extends JPanel implements Scrollable{
    private JTable table;
    
    public TablePanel(JTable table){
        super(new BorderLayout());
        this.table = table;
        if (table.getTableHeader() != null)
            add(table.getTableHeader(), BorderLayout.NORTH);
        add(table);
    }//==================================
    
    public Dimension getPreferredScrollableViewportSize(){
        return table.getPreferredScrollableViewportSize();
    }//==================================
    
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction){
        return table.getScrollableBlockIncrement(visibleRect, orientation, direction);
    }//==================================
    
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction){
        return table.getScrollableUnitIncrement(visibleRect, orientation, direction);
    }//==================================
    
    public boolean getScrollableTracksViewportHeight(){
        return table.getScrollableTracksViewportHeight();
    }//==================================
    
    public boolean getScrollableTracksViewportWidth(){
        return table.getScrollableTracksViewportWidth();
    }//==================================
}

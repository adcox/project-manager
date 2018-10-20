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

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Andrew
 * @version Jul 7, 2011
 */
public class IconRenderer extends DefaultTableCellRenderer {
    
        public IconRenderer(){super();}

        public void setValue(Icon value) {
            setIcon(value);
        }
        
        public Component getTableCellRendererComponent(JTable table, Object color,
                            boolean isSelected, boolean hasFocus, int row, int column) {
            
            if(row % 2 == 0){
                this.setBorder(UIManager.getBorder("List.evenRowBackgroundPainter"));
                setBackground(Color.blue);
            }
            else{
                this.setBorder(UIManager.getBorder("List.oddRowBackgroundPainter"));
            }
            return this;
        }
    }//==================================

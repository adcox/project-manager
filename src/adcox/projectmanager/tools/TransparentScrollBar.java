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
import javax.swing.JScrollBar;

/**
 *
 * @author Andrew
 * @version Jul 28, 2011
 * 
 * Using these drawing techniques, the scrollbar seems to draw in spurts...
 */
public class TransparentScrollBar extends JScrollBar{
    
    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Composite old = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        
        super.paintComponent(g2);
        
        g2.setComposite(old);
        g2.dispose();
    }//====================================
}

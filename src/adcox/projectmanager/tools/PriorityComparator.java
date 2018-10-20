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

import adcox.projectmanager.objects.Issue;
import adcox.projectmanager.objects.Issue.*;
import java.util.Comparator;

/**
 *
 * @author Andrew
 * @version Jul 18, 2011
 */
public class PriorityComparator implements Comparator{
    IssuePriority[] priorityOrder = {IssuePriority.Tiny, IssuePriority.Small, 
        IssuePriority.Medium, IssuePriority.Large, IssuePriority.ShowStopper};
    
    
    @Override
    public int compare(Object priority1, Object priority2){
        int index1 = -1, index2 = -1;
        //find the index of each priority in the ordered list
        for(int i = 0; i < priorityOrder.length; i++){
            //if the two priorities are the same
            if(((String)priority1).equals(Issue.priorityToString(priorityOrder[i]))){
                index1 = i;
            }
            if(((String)priority2).equals(Issue.priorityToString(priorityOrder[i]))){
                index2 = i;
            }
        }
        
        if(index1 == -1 || index2 == -1)
            System.err.println("The two Priorities were not sorted correctly.");
        
        //System.out.println(priority1 + " (Index = " + index1 + ")");
        //System.out.println(priority2 + " (Index = " + index2 + ")");
        
        if(index1 > index2){
            //System.out.println(String.format("%s is greater than %s", priority1, priority2));
            return 1;
        }
        else if(index2 > index1){
            //System.out.println(String.format("%s is greater than %s", priority2, priority1));
            return -1;
        }
        else{
            //System.out.println(String.format("%s is equal to %s", priority1, priority2));
            return 0;
        }
    }//=====================================
}

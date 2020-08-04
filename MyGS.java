/*	
 * Netta Paulson
 * CMSC 401
 * 3/31/20
 * 
 * This program performs the Graham's Scan on a set of 
 * user inputed coordinates to generate a convex hull.
 * 
 */

import java.util.EmptyStackException;
import java.util.Scanner;
import java.lang.Math;

public class MyGS extends Point {
	
	public static void main(String[] args) {
        	try {
        		//Parse the string input and assign x and y coordinates for each point
	            int length = args.length;
	            int j = 0;
	            Point[] points = new Point[length/2];
	            for(int i = 0; i < length; i++) {
	            	Point coord = new Point();
	            	coord.x = Double.parseDouble(args[i]);
	            	i++;
	            	coord.y = Double.parseDouble(args[i]);
	            	points[j] = coord;
	            	j++;
	            }
	            //find the min and max y values
	            Point min = points[0];
	            Point ymax = points[0];
	            double maxx;
	            int found = 0;
	            for(int k = 1; k < points.length; k++) {
	            	if(points[k].y < min.y) {
	            		min = points[k];      
	            		found = k;
	            	}
	            	if(points[k].y == min.y){
	            		if(points[k].x < min.x) {
	            			min = points[k];
	            		}
	            	}  
	            	if(points[k].y > ymax.y) {
	            		ymax = points[k];
	            	}	            		
	            }
	            //place the min value into the first index of the array
	            maxx = ymax.x;
	        	Double temp1 = points[0].x;            
	        	Double temp2 = points[0].y;
	        	points[0].x = points[found].x;
	        	points[0].y = points[found].y;
	        	points[found].x = temp1;
	        	points[found].y = temp2;
	        	min = points[0];	        	
	        	//find polar angles of the the points in relation to the min point
	            for(int m = 0; m < points.length; m++) {
	            	points[m].angle = Math.atan((points[m].y-min.y)/(points[m].x-min.x));
	            	if(points[m].angle < 0) {
	            		points[m].angle = 2*Math.PI+points[m].angle;
	            	}
	            }
	            //set min angle to 10 so that it will be sort to the end of the points array
	            min.angle = 10;	            
	            //sort the angles in ascending order
	            Quicksort(points, 0, points.length-1);            
	            //remove angles that are the same, only keep the point that is farthest out
	            int l = 0;
	            int max = 0;
	            length = points.length-1;
	            double maxDist = 0.0;
	            double currentAng = 0.0;
	            int newCount = 0;
	            Point[] newPoints = new Point[length];
	            for(int w = 0; w < length; w++) {
	            	currentAng = points[w].angle;
	                maxDist = getDistance(min.x,min.y,points[w].x,points[w].y);
	                max = w;
	                l = w+1;
	            	while(currentAng == points[l].angle) {
	            		double dist1 = getDistance(min.x,min.y,points[l].x,points[l].y);
	                	if(dist1 > maxDist ) {
	                		maxDist = dist1;
	                		max = l;
	                	}
	                	w = l;
	            		l++;
	            	}           	
	            	newPoints[newCount] = points[max];
	            	newCount++;        	
	            }	            
	            //create the stack and add the first three points
	            length = l;
	            Stack stack = new Stack();
	            stack.push(min, stack);
	            stack.push(newPoints[0], stack);
	            stack.push(newPoints[1], stack);
	            //add and remove points from the stack by comparing the angle created between
	            //the three points and PI (i.e 180 degrees)
	        	for(int x = 2; x < newCount; x++) {
	        		while(getAngle(stack.nextToTop(stack), stack.top(stack), newPoints[x], maxx) <= Math.PI) {
	        			stack.pop(stack);
	        		}
	        		stack.push(newPoints[x], stack);
	        	}
	        	//print the coordinates of the convex hull
	        	stack.printStack(stack);
	        }
        	catch(Exception e) {
        		System.out.print("error\n");
        	}
      //  }
      //  sc.close();
     //   */
	}
	
	// This method recursively sorts the array and checks with the partition 
	static void Quicksort(Point[] A, int p, int r) {
        if (p < r) {
            int q = Partition(A, p, r);
            Quicksort(A, p, q - 1);
            Quicksort(A, q + 1, r);
        }
    }
    
   // This method compares the array to the partitioned value
   static int Partition(Point[] A, int p, int r){
        Double x = A[r].angle;
        int i = p - 1;
        Point temp;
        for (int j = p; j <= r - 1; j++) {
        	if (A[j].angle <= x) {
                i++;
                temp = A[i];
    	        A[i] = A[j];
    	        A[j] = temp;
            }
        }
        temp = A[i+1];
        A[i+1] = A[r];
        A[r] = temp;
        return i + 1;
   }
   
   //This method gets the distance between two points
   static Double getDistance(Double p1x, Double p1y, Double p2x, Double p2y) {
	   return Math.sqrt(((p2x-p1x)*(p2x-p1x))+((p2y-p1y)*(p2y-p1y)));
   }
   
   //This method determines of the angle between three points is greater than, less than, or equal to PI
   static Double getAngle(Point p1, Point p2, Point p3, double maxx) {
	   Double dist_12 = getDistance(p1.x, p1.y, p2.x, p2.y);
	   Double dist_23 = getDistance(p2.x, p2.y, p3.x, p3.y);
	   Double dist_31 = getDistance(p3.x, p3.y, p1.x, p1.y);
	   
	   Double angle_31 = ((dist_12*dist_12) + (dist_23*dist_23) - (dist_31*dist_31)) / (2*dist_12*dist_23);
	  
	   if(p2.x >= maxx){ //  going up 
		   if(p2.y > p1.y){
			   if(p3.x >= p2.x) {
				   return Math.acos(angle_31);
			   }
			   return Math.acos(2*(Math.PI)-angle_31);
		   }
		   else if(p2.y < p1.y){
			   if(p3.x > p2.x) {
				   return Math.acos(2*(Math.PI)-angle_31);
			   }
			   return Math.acos(angle_31);
		   }
		   else {
			   if(p3.x > p2.x) {
				   return Math.acos(angle_31);
			   }
			   return Math.acos(2*(Math.PI)-angle_31);
		   }
	   }
	   else {  //going down 
		   if(p2.y < p1.y){
			   if(p3.x >= p2.x) {
				   return Math.acos(2*(Math.PI)-angle_31);
			   }
			   return Math.acos(angle_31);
		   }
		   else if(p2.y > p1.y) {
			   if(p3.x > p2.x) {
				   return Math.acos(2*(Math.PI)-angle_31);
			   }
			   return Math.acos(angle_31);
		   }
		   else {
			   if(p3.x > p2.x) {
				   return Math.acos(2*(Math.PI)-angle_31);
			   }
			   return Math.acos(angle_31);
		   }
	   } 
   }
}

//Stores the x-y coordinates and angle of a point
class Point{
	double x;
	double y;
	double angle;
}

//Data structure stack to keep track of the convex hull being created
class Stack{
	 Node top;
	 int numElements;
	
	public void push(Point p1, Stack S){
		if (isEmpty(S)) {
			Node newNode = new Node(p1);
			S.top = newNode;
		} else {
			Node newNode = new Node(p1, S.top);
			newNode.setNextNode(S.top);
			S.top = newNode;

		}
		numElements++;
	}
	public Point pop(Stack S) {
		if (isEmpty(S)) {
			throw new EmptyStackException();
		} else {
			Point tempValue = S.top.data;
			S.top = S.top.next;
			S.numElements--;
			return tempValue;
		}
	}
	public Point top(Stack S) {
		return S.top.data;
	}
	public Point nextToTop(Stack S) {
		return S.top.next.data;
	}
	public boolean isEmpty(Stack S) {
		if ((S.numElements == 0) || (S.top == null)) {
			return true;
		}
		return false;
	}
	
	public void printStack(Stack S) {
		Point[] reverse = new Point[numElements];
		for(int i = numElements-1; i >= 0; i--) {
			reverse[i] = pop(S);
		}
		for(int j = 0; j < reverse.length; j++) {
        System.out.print(reverse[j].x + " " + reverse[j].y + " ");
		}
		System.out.print("\n");
	}
	
	class Node{
		Point data;
		Node next;
		
		public Node(Point newEntry) {
			data = newEntry;
			next = null;
		}
		
		public Node(Point dataPortion, Node nextNode) {
			data = dataPortion;
			next = nextNode;
		}
		
		public void setNextNode(Node nextNode) {
			next = nextNode;
		}
	}
}

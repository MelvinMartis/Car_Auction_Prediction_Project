//package com.thinkjs.io;

public class ABC implements Comparable<ABC>{
	
		int index;
		int cl;
		Double dist;
		
		ABC(int a , int b, double c){
			this.index = a;
			this.cl = b;
			this.dist = c;
		}
		
		public int getIndex(){
			return index;
		}
		
		public int getClassLabel(){
			return cl;
		}
		
		public double getDist(){
			return dist;
		}
//		public int compareTo(ABC o) {
//			return this.dist.compareTo(o.dist);
//		}

//		public int compareTo(ABC o) {
//			// TODO Auto-generated method stub
//			return this.dist.compareTo(o.dist);
//		}

		@Override
		public int compareTo(ABC other) {
			// TODO Auto-generated method stub
			return this.dist.compareTo(other.dist);
			//return 0;
		}
		
		public boolean equals(ABC other){
			return dist == other.dist;
		}
	
}

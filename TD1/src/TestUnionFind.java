import java.awt.Point;


// INF555. 2011. Frank Nielsen

class TestUnionFind {
	
	public static void main(String[] args) {
	
		PPMUnionFind image1 = new PPMUnionFind("forfloodfilling.ppm");
		image1.show();
		image1.colorUnions(false);
		image1.show();
		System.out.println("forfloodfilling.ppm > " + image1.countComps() + " connec components");

		PPMUnionFind image2 = new PPMUnionFind("polytechnique.ppm", 20);
		image2.show();
		image2.colorUnions(true);
		image2.show();
		System.out.println("forfloodfilling.ppm > " + image2.countComps() + " connec components");
	}
	
}


class UnionFind{

	int[] rank; 
	int[] parent; 

	
	// Create an UF for n elements
	UnionFind(int n) {
		int k;

		parent = new int[n]; 
		rank   = new int[n];

		for (k = 0; k < n; k++) {
			parent[k] = k;
			rank[k]   = 0;
		}
	}
	
	// Find root
	int find(int k) {     // Recursive version cannot be used because causes Stack Overflow Error
		while (k != parent[k]) k = parent[k];
		return k;
	}
	
	// Union
	void union(int x, int y) {  
		x = find(x);
		y = find(y);
		if (x == y) return;  // Save some time
		unionRoot(x, y);
	}
	
	// Union for two roots
	void unionRoot(int x, int y) {
		if (rank[x] > rank[y])
			parent[x] = y;
		else {
			parent[y] = x;
			if (rank[x] == rank[y]) rank[x]++;
		}
	}
	
	// Count different components
	public int count() {
		int n = parent.length;
		int res = 0;
		boolean[] toCount = new boolean[n];
		for (int i = 0; i < n; i++) toCount[i]=true;
		for (int i = 0; i < n; i++) {
			int p = i;
			while (toCount[p]) {      // On remonte jusqu'à un pixel déjà croisé
				toCount[p] = false;
				if (p == parent[p]) { // Si on atteint la racine, c'est une nouvelle composente
					res++;
					break;
				}
				p = parent[p];
			}
		}
		return res;
	}
	
	
	
}

class PPMUnionFind extends PPM {
	
	UnionFind uf;
	
	
	// Create a PPM from file name and find connex components of same color
	public PPMUnionFind(String name) {
		super(name);
		uf = new UnionFind(width*height);
		union();
	}
	
	// Create a PPM from file name and find connex components with precised threshold
	public PPMUnionFind(String name, int threshold) {
		super(name);
		uf = new UnionFind(width*height);
		union(threshold);
	}
	
	
	public void union() {
		for (int x = 0; x < width-1; x++) {
			for (int y = 0; y < height-1; y++) {
				unionPixels(x, y, x+1, y);
				unionPixels(x, y, x, y+1);
			}
		}
		for (int x = 0; x < width-1; x++)
			unionPixels(x, height-1, x+1, height-1);
		for (int y = 0; y < height-1; y++)
			unionPixels(width-1, y, width-1, y+1);
	}

	public void union(int threshold) {
		for (int x = 0; x < width-1; x++) {
			for (int y = 0; y < height-1; y++) {
				unionPixels(x, y, x+1, y, threshold);
				unionPixels(x, y, x, y+1, threshold);
			}
		}
		for (int x = 0; x < width-1; x++)
			unionPixels(x, height-1, x+1, height-1, threshold);
		for (int y = 0; y < height-1; y++)
			unionPixels(width-1, y, width-1, y+1, threshold);
	}

	// Compare (x1, y1) and (x2, y2) pixels and union if they have the same color
	public void unionPixels(int x1, int y1, int x2, int y2) {
		if ((r[y1][x1] == r[y2][x2]) && (g[y1][x1] == g[y2][x2]) && (b[y1][x1] == b[y2][x2]))
			uf.union(x1*height+y1,x2*height+y2);
	}

	public void unionPixels(int x1, int y1, int x2, int y2, int threshold) {
		if (getColor(x1, y1).dist(getColor(x2, y2)) < threshold)
			uf.union(x1*height+y1,x2*height+y2);
	}

	
	// Return number of connex composent according to the UF
	public int countComps() {
		return uf.count();
	}
	
	
	public void shuffle() {
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				setColor(x, y, new Color());
	}
	
	public void colorUnions(boolean rootsColors) {
		if (!rootsColors) shuffle();
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
			{
				int root = uf.find(x*height+y);
				setColor(x, y, getColor(root/height, root%height));
			}
	}
	
	
}


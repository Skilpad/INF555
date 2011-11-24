

public class List<T> {
	
	public T       head;
	public List<T> tail;
	public int     size;
	
	public List() {
		head = null;
		tail = null;
		size = 0;
	}
	
	public List(List<T> L, T x) {
		head = x;
		tail = L;
		size = L.size+1;
	}
	
	public List<T> plus(T x) {
		return new List<T>(this, x);
	}

}

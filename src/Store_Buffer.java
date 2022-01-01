public class Store_Buffer {

	int address;
	float V;
	String Q;
	boolean busy;
	int cycles;

	public Store_Buffer(int address, float V, String Q, boolean busy , int cycles) {
		this.address = address;
		this.V = V;
		this.Q = Q;
		this.busy = busy;
		this.cycles = cycles;
	}
	public void update () {
		this.cycles = cycles -1 ;
	}
	public String display() {
		String s="";
		return s;
	}

}
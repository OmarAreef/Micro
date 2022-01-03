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
		if (Q.equals("") )
			this.cycles = cycles - 1;
		
	}
	@Override
	public String toString() {
		return "Store_Buffer [address=" + address + ", V=" + V + ", " + (Q != null ? "Q=" + Q + ", " : "") + "busy="
				+ busy + ", cycles=" + cycles + "] \n";
	}
	public boolean checkCycles(){
		return cycles==0?true:false;
	}
	public String display() {
		String s="";
		return s;
	}

}
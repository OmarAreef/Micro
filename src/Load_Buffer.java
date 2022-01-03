public class Load_Buffer {
	int address;
	int cycles ;
	boolean busy;


	public Load_Buffer (int address, boolean busy , int cycles) {
		this.address = address;
		this.cycles = cycles;
		this.busy = busy;
	}
	public void update () {
		this.cycles = cycles -1 ;
	}
	public boolean checkCycles(){
		return cycles==0?true:false;
	}
	@Override
	public String toString() {
		return "Load_Buffer [address=" + address + ", cycles=" + cycles + ", busy=" + busy + "] \n";
	}
	public String display() {
		String s="";
		return s;
	}
}
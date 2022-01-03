public class ALU_Operations {
	String op;
	float Vj;
	float Vk;
	String Qj;
	String Qk;
	boolean busy;
	int cycles;

	public ALU_Operations(String op, float Vj, float Vk, String Qj, String Qk, boolean busy, int cycles) {
		this.op = op;
		this.Vj = Vj;
		this.Vk = Vk;
		this.Qj = Qj;
		this.Qk = Qk;
		this.cycles = cycles;
		this.busy = busy;
	}

	public void update() {
		if (Qj.equals("") && Qk.equals(""))
			this.cycles = cycles - 1;
	}

	public boolean checkCycles() {
		return cycles == 0 ? true : false;
	}

	@Override
	public String toString() {
		return "[ "+(op != null ? "op=" + op + " | " : "") + "Vj=" + Vj + ", Vk=" + Vk + " | "
				+ (Qj != null ? "Qj=" + Qj + " | " : "") + (Qk != null ? "Qk=" + Qk + " | " : "") + "busy=" + busy
				+ ", cycles=" + cycles + "] \n";
	}
}
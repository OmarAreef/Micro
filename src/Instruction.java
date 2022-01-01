public class Instruction {
	// enum op{LD, SD, ADD,SUB, MUL,DIV};
	String op;
	String d; // destination reg
	String j;
	String k; // empty if load or store
	int issue;
	String execute;
	String tag;
	int writeResult;
	int latency;

	public Instruction(String op, String d, String j, String k) {
		this.op = op;
		this.d = d;
		this.j = j;
		this.k = k;
		// get latency from user or file
	}

	public String toString() {
		String s = "";
		if (this.op.equals("LD")) {
			s = "LD" + " " + this.d + " | " + this.j + " | " + this.issue + " | " + this.execute + " | " + this.writeResult;
		} else {
			s = this.op + " " + this.d + " | " + this.j + " | " + this.k + " | " + this.issue + " | " + this.execute + " | "
					+ this.writeResult;
		}
		return s+"\n";
	}
}

public class RegFile {
	int location;
	String q;
	float RegValue;

	public RegFile(int i) {
		this.q = "0";
		this.location = i;
		this.RegValue = (float) 2.3;
	}

	@Override
	public String toString() {
		return "Register [location=" + location + " | " + (q != null ? "q=" + q + " | " : "") + "RegValue=" + RegValue
				+ "] \n";
	}

	public RegFile(String q, float RegValue ) {
		this.q = q;
		this.location = 0 ;
		this.RegValue = RegValue;
	}

	public String display() {
		String s = "";
		return s;
	}
}
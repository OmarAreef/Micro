import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Tomasulo {
	static ALU_Operations[] ADD_SUB = new ALU_Operations[2];
	static ALU_Operations[] MUL_DIV = new ALU_Operations[2];
	static Load_Buffer[] lBuffer = new Load_Buffer[2];
	static Store_Buffer[] sBuffer = new Store_Buffer[2];
	static RegFile[] regFile = new RegFile[33];
	static Queue<Instruction> instQueue = new LinkedList<Instruction>();
	static Map<String, Integer> penalties = new HashMap<>();
	static int curCycle = -1;

	public static void startTomasulo() throws IOException {
		curCycle++;
		init(new File("src/input.txt"), new File("src/penalties.txt"));
		while (!instQueue.isEmpty()) {
			checkCycles () ;
			Instruction inst = instQueue.peek();
			String opCode = inst.op;
			int penalty = penalties.get(opCode);
			System.out.println(Arrays.toString(ADD_SUB));
			updateCycles();
			switch (opCode) {
			case "ADD":
			case "SUB":
				// System.out.println(checkStation(opCode));
				if (checkStation(opCode) != -1) {
					ALU_Operations operation = new ALU_Operations(opCode, (float) 0.0, (float) 0.0, "", "", true,
							penalty);
					inst.issue = curCycle;
					int address1 = Integer.parseInt(inst.j.split("")[1]);
					int address2 = Integer.parseInt(inst.k.split("")[1]);
					if (regFile[address1].q.equals("0")) {
						operation.Vj = regFile[address1].RegValue;
					} else {
						operation.Qj = regFile[address1].q;
					}
					if (regFile[address2].q.equals("0")) {
						operation.Vk = regFile[address2].RegValue;
					} else {
						operation.Qk = regFile[address2].q;
					}
					int placee = checkStation(opCode);
					inst.tag = opCode + placee;
					ADD_SUB[placee] = operation;
					regFile[Integer.parseInt(inst.d.split("")[1])].q = inst.tag;
					instQueue.poll();
				}
				break;
			case "MUL":
			case "DIV":
				// System.out.println(checkStation(opCode));
				if (checkStation(opCode) != -1) {
					ALU_Operations operation = new ALU_Operations(opCode, (float) 0.0, (float) 0.0, "", "", true,
							penalty);
					inst.issue = curCycle;
					int address1 = Integer.parseInt(inst.j.split("")[1]);
					int address2 = Integer.parseInt(inst.k.split("")[1]);
					if (regFile[address1].q.equals("0")) {
						operation.Vj = regFile[address1].RegValue;
					} else {
						operation.Qj = regFile[address1].q;
					}
					if (regFile[address2].q.equals("0")) {
						operation.Vk = regFile[address2].RegValue;
					} else {
						operation.Qk = regFile[address2].q;
					}
					int placee = checkStation(opCode);
					inst.tag = opCode + placee;
					regFile[Integer.parseInt(inst.d.split("")[1])].q = inst.tag;
					MUL_DIV[placee] = operation;
					instQueue.poll();
				}
				break;
			case "LD":
				if (checkStation(opCode) != -1) {
					Load_Buffer operation = new Load_Buffer(0, true, penalty);
					inst.issue = curCycle;
					int address1 = Integer.parseInt(inst.d.split("")[1]) - 1;
					int memoryAddress = Integer.parseInt(inst.j);
					operation.address = memoryAddress;
					int placee = checkStation(opCode);
					inst.tag = opCode + placee;
					regFile[Integer.parseInt(inst.d.split("")[1])].q = inst.tag;
					lBuffer[placee] = operation;
					instQueue.poll();
				}
				break;
			case "SD":
				// System.out.println(checkStation(opCode));
				if (checkStation(opCode) != -1) {
					Store_Buffer operation = new Store_Buffer(0, (float) 0.0, "", true, penalty);
					inst.issue = curCycle;
					int address1 = Integer.parseInt(inst.d.split("")[1]);
					if (regFile[address1].q.equals("0")) {
						operation.V = regFile[address1].RegValue;
					} else {
						operation.Q = regFile[address1].q;
					}
					int memoryAddress = Integer.parseInt(inst.j);
					operation.address = memoryAddress;
					int placee = checkStation(opCode);
					inst.tag = opCode + placee;
					regFile[Integer.parseInt(inst.d.split("")[1])].q = inst.tag;
					sBuffer[placee] = operation;
					instQueue.poll();
				}
				break;
			}
		}
	}

	private static void checkCycles() {
		for (int i = 0; i < 2; i++) {
			// check ADD_SUB
			if (ADD_SUB[i].checkCycles()) {
				// remove from station+do operation
				String tag = ADD_SUB[i].op + i ;
			}
			// check MUL_DIV
			if (MUL_DIV[i].checkCycles()) {
				// remove from station+do operation
				String tag = MUL_DIV[i].op + i ;
			}
			// check lBUffer
			if (lBuffer[i].checkCycles()) {
				// remove from buffer+do operation
				String tag = "LD" + i ;
			}
			// check sBUffer
			if (sBuffer[i].checkCycles()) {
				// remove from buffer+do operation
				String tag = "SD"+ i ;
			}
		}
	}
	private static void updateReg (String location , float value) {
		for (int i = 0 ; i < regFile.length ; i ++ ) {
			if (regFile[i].q.equals(location)) {
				regFile[i].RegValue = value ; 
				regFile[i].q = "0" ;
			}
		}
		
	}

	private static void updateCycles() {
		// TODO Auto-generated method stub
		List<ALU_Operations> ar = Arrays.asList(ADD_SUB);
		ar.stream().forEach(ss -> {
			if (ss != null) {
				ss.update();
			}
		});
		ADD_SUB = ar.toArray(new ALU_Operations[0]);
		List<ALU_Operations> md = Arrays.asList(MUL_DIV);
		md.stream().forEach(ss -> {
			if (ss != null) {
				ss.update();
			}
		});
		MUL_DIV = md.toArray(new ALU_Operations[0]);
		List<Load_Buffer> ld = Arrays.asList(lBuffer);
		ld.stream().forEach(ss -> {
			if (ss != null) {
				ss.update();
			}
		});
		lBuffer = ld.toArray(new Load_Buffer[0]);
		List<Store_Buffer> sd = Arrays.asList(sBuffer);
		sd.stream().forEach(ss -> {
			if (ss != null) {
				ss.update();
			}
		});
		sBuffer = ld.toArray(new Store_Buffer[0]);
	}

	public static int checkStation(String op) {
		if (op.equals("ADD") || op.equals("SUB")) {
			if (ADD_SUB[0] == null) {
				return 0;
			}
			if (ADD_SUB[1] == null) {
				return 1;
			}
		}
		if (op.equals("MUL") || op.equals("DIV")) {
			if (MUL_DIV[0] == null) {
				return 0;
			}
			if (MUL_DIV[1] == null) {
				return 1;
			}
		}
		if (op.equals("SD")) {
			if (sBuffer[0] == null) {
				return 0;
			}
			if (sBuffer[1] == null) {
				return 1;
			}
		}
		if (op.equals("LD")) {
			if (lBuffer[0] == null) {
				return 0;
			}
			if (lBuffer[1] == null) {
				return 1;
			}
		}
		return -1;
	}

	public static void init(File input, File penalty) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(input));
		String st;
		while ((st = br.readLine()) != null) {
			String[] stSplit = st.split(" ");
			String op = stSplit[0];
			if (op.equals("LD") || op.equals("SD")) {
				Instruction newInst = new Instruction(op, stSplit[1], stSplit[2], "0");
				instQueue.add(newInst);
			} else {
				Instruction newInst = new Instruction(op, stSplit[1], stSplit[2], stSplit[3]);
				instQueue.add(newInst);
			}
		}
		br.close();
		br = new BufferedReader(new FileReader(penalty));
		st = "";
		while ((st = br.readLine()) != null) {
			// populate instQueue
			String[] stPenalty = st.split(" ");
			penalties.put(stPenalty[0], Integer.parseInt(stPenalty[1]));
		}
		br.close();
		for (int i = 0; i < regFile.length; i++) {
			regFile[i] = new RegFile();
		}
	}

	public static void main(String[] args) {
		try {
			startTomasulo();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(instQueue.toString());
		System.out.println(penalties.toString());
	}
}

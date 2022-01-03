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
	static float[] Memory = new float[200];
	static Queue<Instruction> instQueue = new LinkedList<Instruction>();
	static Map<String, Integer> penalties = new HashMap<>();
	static int curCycle = -1;

	public static void startTomasulo() throws IOException {
		init(new File("src/input.txt"), new File("src/penalties.txt"));
		while (!instQueue.isEmpty()) {
			curCycle++;
			display();
			checkCycles();
			Instruction inst = instQueue.peek();
			String opCode = inst.op;
			int penalty = penalties.get(opCode);
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
					// regFile[Integer.parseInt(inst.d.split("")[1])].q = inst.tag;
					sBuffer[placee] = operation;
					instQueue.poll();
				}
				break;
			}
		}
		finishExecution();
	}

	private static void finishExecution() {
		while (ADD_SUB[0] != null || ADD_SUB[1] != null || MUL_DIV[0] != null || MUL_DIV[1] != null
				|| sBuffer[0] != null || lBuffer[0] != null || sBuffer[1] != null || lBuffer[1] != null) {
			curCycle++;
			checkCycles();
			updateCycles();
			display();
		}
	}

	private static void checkCycles() {
		boolean update = false;
		String tag = "";
		float value = 0;
		for (int i = 0; i < 2; i++) {
			if (ADD_SUB[i] != null && ADD_SUB[i].checkCycles()) {
				// remove from station+do operation
				update = true;
				tag = ADD_SUB[i].op + i;
				if (ADD_SUB[i].op.equals("ADD")) {
					value = ADD_SUB[i].Vj + ADD_SUB[i].Vk;
				}
				if (ADD_SUB[i].op.equals("SUB")) {
					value = ADD_SUB[i].Vj - ADD_SUB[i].Vk;
				}
				ADD_SUB[i] = null;
				updateReg(tag, value);
				updateBus(tag, value);
			}
			// check MUL_DIV
			if (MUL_DIV[i] != null && MUL_DIV[i].checkCycles()) {
				// remove from station+do operation
				update = true;
				tag = MUL_DIV[i].op + i;
				if (MUL_DIV[i].op.equals("MUL")) {
					value = MUL_DIV[i].Vj * MUL_DIV[i].Vk;
					System.out.println("MUL IS" + value);
				}
				if (MUL_DIV[i].op.equals("DIV")) {
					value = MUL_DIV[i].Vj / MUL_DIV[i].Vk;
				}
				MUL_DIV[i] = null;
				updateReg(tag, value);
				updateBus(tag, value);
			}
			if (sBuffer[i] != null && sBuffer[i].checkCycles()) {
				// remove from buffer+do operation
				// write back to memory
				tag = "SD" + i;
				int address = sBuffer[i].address;
				value = sBuffer[i].V;
				Memory[address] = value;
				sBuffer[i] = null;
				// updateReg(tag, value);
				// updateBus(tag, value);
			}
			// check lBUffer
			if (lBuffer[i] != null && lBuffer[i].checkCycles()) {
				// remove from buffer+do operation
				update = true;
				tag = "LD" + i;
				int address = lBuffer[i].address;
				value = Memory[address];
				lBuffer[i] = null;
				updateReg(tag, value);
				updateBus(tag, value);
			}
			// check sBUffer
		}
	}

	private static void updateReg(String location, float value) {
		for (int i = 0; i < regFile.length; i++) {
			if (regFile[i].q.equals(location)) {
				regFile[i].RegValue = value;
				regFile[i].q = "0";
			}
		}
	}

	private static void updateBus(String location, float value) {
		for (int i = 0; i < 2; i++) {
			if (ADD_SUB[i] != null) {
				if (ADD_SUB[i].Qj.equals(location)) {
					ADD_SUB[i].Qj = "";
					ADD_SUB[i].Vj = value;
				}
				if (ADD_SUB[i].Qk.equals(location)) {
					ADD_SUB[i].Qk = "";
					ADD_SUB[i].Vk = value;
				}
			}
			if (MUL_DIV[i] != null) {
				if (MUL_DIV[i].Qj.equals(location)) {
					MUL_DIV[i].Qj = "";
					MUL_DIV[i].Vj = value;
				}
				if (MUL_DIV[i].Qk.equals(location)) {
					MUL_DIV[i].Qk = "";
					MUL_DIV[i].Vk = value;
				}
			}
			if (sBuffer[i] != null && sBuffer[i].Q.equals(location)) {
				sBuffer[i].Q = "";
				sBuffer[i].V = value;
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
		sBuffer = sd.toArray(new Store_Buffer[0]);
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
		regFile[0] = new RegFile("0" , 0);
		for (int i = 1; i < regFile.length; i++) {
			regFile[i] = new RegFile(i);
		}
	}

	public static void display() {
		System.out.println("\t*-*-*-*-*-*- Cycle:" + curCycle + " -*-*-*-*-*-*");
		if (!instQueue.isEmpty()) {
			System.out.println("------ Instruction Queue ------");
			System.out.println(Arrays.toString(instQueue.toArray()));
		}
		System.out.println("------ ADD/SUB station ------");
		System.out.println(Arrays.toString(ADD_SUB));
		System.out.println("------ MUL/DIV station ------");
		System.out.println(Arrays.toString(MUL_DIV));
		System.out.println("------ Load buffer ------");
		System.out.println(Arrays.toString(lBuffer));
		System.out.println("------Store buffer------");
		System.out.println(Arrays.toString(sBuffer));
		System.out.println("------ Register File ------");
		System.out.println(Arrays.toString(regFile));
	}

	public static void main(String[] args) {
		try {
			startTomasulo();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

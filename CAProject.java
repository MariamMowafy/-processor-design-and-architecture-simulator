import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;

class CAProject {
    static Byte[] registerFile = new Byte[64];
    short[] instructionMemory = new short[1024];
    Byte[] dataMemory = new Byte[2048];
    Byte sreg = 0;
    short PC = 0;
    int opcode;
    Byte imm;
    int r1, r2;
    Byte valueR1;
    Byte valueR2;
    short instructionToDecode;
    int numOfInstructions = 0;

    public void fetch() {
        instructionToDecode = instructionMemory[PC];
        System.out.println("stage fetch info :");
        System.out.println("PC = " + PC);
        PC++;
    }

    public void decode(short instruction) {
        opcode = (instruction & (0b1111000000000000)) >> 12;
        r1 = (instruction & (0b0000111111000000)) >> 6;
        r2 = (instruction & (0b0000000000111111));
        Integer imm1 = (instruction & (0b0000000000111111));
        imm = imm1.byteValue();
        valueR1 = registerFile[r1];
        valueR2 = registerFile[r2];
        System.out.println("stage decode info :");
        System.out.println("opcode = " + opcode);
        System.out.println("R" + r1 + " = " + valueR2 + ", R" + r2 + " = " + valueR2);
        System.out.println("Immediate = " + imm);
    }

    public void execute() {
        Byte value1, value2;
        if (opcode == 0 || opcode == 1 || opcode == 2 || opcode == 6 || opcode == 7) {
            value1 = registerFile[r1];
            value2 = registerFile[r2];
        } else {
            value1 = registerFile[r1];
            value2 = imm;
        }
        ALU(opcode, r1, value1, value2);
    }

    public void ALU(int opcode, int r1, Byte value1, Byte value2) {
        Integer testNeg;
        Integer output;
        Integer cflag;
        Integer zflag;
        Integer vflag;
        Integer sflag;
        Integer nflag;
        System.out.println("stage execute info :");
        switch (opcode) {// -128 & 127
            case 0: // add
                output = value1 + value2;
                registerFile[r1] = output.byteValue();
                System.out.println("ADD :");
                System.out.println("R" + r1 + " =" + value1);
                System.out.println("R" + r2 + " =" + value2);
                System.out.println("R" + r1 + " new value is :" + output);

                cflag = Integer.valueOf(sreg);
                if (output > Byte.MAX_VALUE)
                    cflag = cflag | (0b00010000);
                else
                    cflag = cflag & (0b11101111);
                sreg = cflag.byteValue();

                nflag = Integer.valueOf(sreg);
                if (output < 0)
                    nflag = nflag | (0b00000100);
                else
                    nflag = nflag & (0b11111011);
                sreg = nflag.byteValue();

                zflag = Integer.valueOf(sreg);
                if (output == 0)
                    zflag = zflag | (0b00000001);
                else
                    zflag = zflag & (0b11111110);
                sreg = zflag.byteValue();

                vflag = Integer.valueOf(sreg);
                if (value1 > 0 && value2 > 0 && output < 0)
                    vflag = vflag | (0b00001000);
                else if (value1 < 0 && value2 < 0 && output > 0)
                    vflag = vflag | (0b00001000);
                else
                    vflag = vflag & (0b11110111);
                sreg = vflag.byteValue();

                sflag = ((Integer.valueOf(sreg) >> 3) ^ (Integer.valueOf(sreg) >> 2));
                if (sflag == 1)
                    sflag = sreg | (0b00000010);
                else
                    sflag = sreg & (0b11111101);
                sreg = sflag.byteValue();

                System.out.println("Z flag =" + (sreg & (0b00000001)));
                System.out.println("S flag =" + ((sreg & ((0b00000010))) >> 1));
                System.out.println("N flag =" + ((sreg & ((0b00000100))) >> 2));
                System.out.println("V flag =" + ((sreg & ((0b00001000))) >> 3));
                System.out.println("C flag =" + ((sreg & ((0b00010000))) >> 4));

                break;
            case 1:// sub
                output = value1 - value2;
                registerFile[r1] = output.byteValue();
                System.out.println("SUB :");
                System.out.println("R" + r1 + " =" + value1);
                System.out.println("R" + r2 + " =" + value2);
                System.out.println("R" + r1 + " new value is :" + output);

                cflag = Integer.valueOf(sreg);
                if (output > Byte.MAX_VALUE)
                    cflag = cflag | (0b00010000);
                else
                    cflag = cflag & (0b11101111);
                sreg = cflag.byteValue();

                nflag = Integer.valueOf(sreg);
                if (output < 0)
                    nflag = nflag | (0b00000100);
                else
                    nflag = nflag & (0b11111011);
                sreg = nflag.byteValue();

                zflag = Integer.valueOf(sreg);
                if (output == 0)
                    zflag = zflag | (0b00000001);
                else
                    zflag = zflag & (0b11111110);
                sreg = zflag.byteValue();

                vflag = Integer.valueOf(sreg);
                if (value1 > 0 && value2 < 0 && output < 0)
                    vflag = vflag | (0b00001000);
                else if (value1 < 0 && value2 > 0 && output > 0)
                    vflag = vflag | (0b00001000);
                else
                    vflag = vflag & (0b11110111);
                sreg = vflag.byteValue();

                sflag = ((Integer.valueOf(sreg) >> 3) ^ (Integer.valueOf(sreg) >> 2));
                if (sflag == 1)
                    sflag = sreg | (0b00000010);
                else
                    sflag = sreg & (0b11111101);
                sreg = sflag.byteValue();

                System.out.println("Z flag =" + (sreg & (0b00000001)));
                System.out.println("S flag =" + ((sreg & ((0b00000010))) >> 1));
                System.out.println("N flag =" + ((sreg & ((0b00000100))) >> 2));
                System.out.println("V flag =" + ((sreg & ((0b00001000))) >> 3));
                System.out.println("C flag =" + ((sreg & ((0b00010000))) >> 4));

                break;
            case 2:// mult
                output = value1 * value2;
                registerFile[r1] = output.byteValue();
                System.out.println("MUL :");
                System.out.println("R" + r1 + " =" + value1);
                System.out.println("R" + r2 + " =" + value2);
                System.out.println("R" + r1 + " new value is :" + output);

                cflag = Integer.valueOf(sreg);
                if (output > Byte.MAX_VALUE)
                    cflag = cflag | (0b00010000);
                else
                    cflag = cflag & (0b11101111);
                sreg = cflag.byteValue();

                nflag = Integer.valueOf(sreg);
                if (output < 0)
                    nflag = nflag | (0b00000100);
                else
                    nflag = nflag & (0b11111011);
                sreg = nflag.byteValue();

                zflag = Integer.valueOf(sreg);
                if (output == 0)
                    zflag = zflag | (0b00000001);
                else
                    zflag = zflag & (0b11111110);
                sreg = zflag.byteValue();

                System.out.println("Z flag =" + (sreg & (0b00000001)));
                System.out.println("N flag =" + ((sreg & ((0b00000100))) >> 2));
                System.out.println("C flag =" + ((sreg & ((0b00010000))) >> 4));

                break;
            case 3:// move imm
                testNeg = (value2 & (0b00100000)) >> 5;
                if (testNeg == 1) {
                    testNeg = (value2 | (0b11111111111111111111111111000000));
                    value2 = testNeg.byteValue();
                }
                registerFile[r1] = value2;
                System.out.println("MOVI :");
                System.out.println("R" + r1 + " =" + value1);
                System.out.println("IMM" + " =" + value2);
                System.out.println("R" + r1 + " new value is :" + value2);
                break;
            case 4:// branch ==0
                System.out.println("BEQZ :");
                System.out.println("PC =" + (PC - 1));
                System.out.println("R" + r1 + " =" + value1);
                System.out.println("IMM" + " =" + value2);
                if (value1 == 0) {
                    PC = (short) (PC + value2);
                    System.out.println("PC " + "new value is :" + PC);
                }
                break;
            case 5:// and imm
                testNeg = (value2 & (0b00100000)) >> 5;
                if (testNeg == 1) {
                    testNeg = (value2 | (0b11111111111111111111111111000000));
                    value2 = testNeg.byteValue();
                }
                output = value1 & value2;
                registerFile[r1] = output.byteValue();
                System.out.println("ANDI :");
                System.out.println("R" + r1 + " =" + value1);
                System.out.println("IMM" + " =" + value2);
                System.out.println("R" + r1 + " new value is :" + output);

                nflag = Integer.valueOf(sreg);
                if (output < 0)
                    nflag = nflag | (0b00000100);
                else
                    nflag = nflag & (0b11111011);
                sreg = nflag.byteValue();

                zflag = Integer.valueOf(sreg);
                if (output == 0)
                    zflag = zflag | (0b00000001);
                else
                    zflag = zflag & (0b11111110);
                sreg = zflag.byteValue();

                System.out.println("Z flag =" + (sreg & (0b00000001)));
                System.out.println("N flag =" + ((sreg & ((0b00000100))) >> 2));

                break;
            case 6:// exclusive or
                output = (value1 ^ value2);
                registerFile[r1] = output.byteValue();
                System.out.println("EOR :");
                System.out.println("R" + r1 + " =" + value1);
                System.out.println("R" + r2 + " =" + value2);
                System.out.println("R" + r1 + " new value is :" + output);

                nflag = Integer.valueOf(sreg);
                if (output < 0)
                    nflag = nflag | (0b00000100);
                else
                    nflag = nflag & (0b11111011);
                sreg = nflag.byteValue();

                zflag = Integer.valueOf(sreg);
                if (output == 0)
                    zflag = zflag | (0b00000001);
                else
                    zflag = zflag & (0b11111110);
                sreg = zflag.byteValue();

                System.out.println("Z flag =" + (sreg & (0b00000001)));
                System.out.println("N flag =" + ((sreg & ((0b00000100))) >> 2));

                break;
            case 7:// branch register
                System.out.println("BR :");
                System.out.println("R" + r1 + " =" + value1);
                System.out.println("R" + r2 + " =" + value2);
                System.out.println("PC =" + (PC - 1));
                int j = (value2 & (0b00000011));
                PC = Integer.valueOf((value1 << 2 | j)).shortValue();
                System.out.println("PC " + "new value is :" + PC);

                break;
            case 8:// Shift Arithmetic Left
                output = value1 << value2;
                registerFile[r1] = output.byteValue();
                System.out.println("SAL :");
                System.out.println("R" + r1 + " =" + value1);
                System.out.println("IMM" + " =" + value2);
                System.out.println("R" + r1 + " new value is :" + output);

                nflag = Integer.valueOf(sreg);
                if (output < 0)
                    nflag = nflag | (0b00000100);
                else
                    nflag = nflag & (0b11111011);
                sreg = nflag.byteValue();

                zflag = Integer.valueOf(sreg);
                if (output == 0)
                    zflag = zflag | (0b00000001);
                else
                    zflag = zflag & (0b11111110);
                sreg = zflag.byteValue();

                System.out.println("Z flag =" + (sreg & (0b00000001)));
                ;
                System.out.println("N flag =" + ((sreg & ((0b00000100))) >> 2));

                break;
            case 9:// Shift Arithmetic Right
                testNeg = (value2 & (0b00100000)) >> 5;
                if (testNeg == 1) {
                    testNeg = (value2 | (0b11111111111111111111111111000000));
                    value2 = testNeg.byteValue();
                }
                int i = Integer.valueOf(value2);
                if (i > 0) {
                    do {
                        output = ((0b10000000) & value1) | (value1 >> 1);
                        i--;
                    } while (i > 0);
                    registerFile[r1] = output.byteValue();
                    System.out.println("SAR :");
                    System.out.println("R" + r1 + " =" + value1);
                    System.out.println("IMM" + " =" + value2);
                    System.out.println("R" + r1 + " new value is :" + output);
                } else
                    output = Integer.valueOf(value1);

                nflag = Integer.valueOf(sreg);
                if (output < 0)
                    nflag = nflag | (0b00000100);
                else
                    nflag = nflag & (0b11111011);
                sreg = nflag.byteValue();

                zflag = Integer.valueOf(sreg);
                if (output == 0)
                    zflag = zflag | (0b00000001);
                else
                    zflag = zflag & (0b11111110);
                sreg = zflag.byteValue();

                System.out.println("Z flag =" + (sreg & (0b00000001)));
                System.out.println("N flag =" + ((sreg & ((0b00000100))) >> 2));

                break;
            case 10:// Load to Register
                registerFile[r1] = dataMemory[value2];
                System.out.println("LDR :");
                System.out.println("R" + r1 + " =" + value1);
                System.out.println("Address =" + value2);
                System.out.println("R" + r1 + " new value is :" + dataMemory[value2]);

                break;
            case 11:// store in reg
                dataMemory[value2] = value1;
                System.out.println("SDR :");
                System.out.println("R" + r1 + " =" + value1);
                System.out.println("Address =" + value2);
                System.out.println("Index " + value2 + " in the data memory new value is" + value1);

                break;
        }

    }

    public short InstructionHandler(short value, String[] splitArray) {
        Byte R1Index = Byte.parseByte(splitArray[1].replaceAll("[^0-63]", ""));
        Byte R2Index = 0;
        try {
            if (R1Index > 63 || R1Index < 0)
                throw new Exception("Make sure you entered the correct register number(s)");
            try {
                Integer i = Integer.valueOf(splitArray[2]);
                if (i > 63 || i < -31)
                    throw new Exception("Make sure that the immediate value is within proper range");
                i = (i & (0b0000000000111111));
                R2Index = i.byteValue();
            } catch (Exception e) {
                R2Index = Byte.parseByte(splitArray[2].replaceAll("[^0-63]", ""));
                if (R2Index > 63 || R2Index < 0)
                    throw new Exception("Make sure you entered the correct register number(s)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        short s = (Integer.valueOf(value << 12 | R1Index << 6 | R2Index)).shortValue();
        return s;
    }

    public void parse() {
        short value;
        numOfInstructions = 0;
        try {
            if (numOfInstructions == 1024)
                throw new Exception("Instruction memory full");
            System.out.println("Enter the name of the program file:");
            InputStreamReader readFileName = new InputStreamReader(System.in);
            BufferedReader reader = new BufferedReader(readFileName);
            File x = new File(reader.readLine());
            // File x = new File("test.txt");
            if (!x.exists())
                throw new Exception("Make sure the file name entered is correct");
            Scanner sc = new Scanner(x);
            while (sc.hasNextLine()) {
                String[] splitArray = sc.nextLine().split(" ");
                // System.out.println(Arrays.toString(splitArray));
                switch (splitArray[0].toUpperCase()) {
                    case "ADD":
                        value = InstructionHandler((short) 0, splitArray);
                        instructionMemory[numOfInstructions] = value;
                        numOfInstructions++;
                        break;
                    case "SUB":
                        value = InstructionHandler((short) 1, splitArray);
                        instructionMemory[numOfInstructions] = value;
                        numOfInstructions++;
                        break;
                    case "MUL":
                        value = InstructionHandler((short) 2, splitArray);
                        instructionMemory[numOfInstructions] = value;
                        numOfInstructions++;
                        break;
                    case "MOVI":
                        value = InstructionHandler((short) 3, splitArray);
                        instructionMemory[numOfInstructions] = value;
                        numOfInstructions++;
                        break;
                    case "BEQZ":
                        value = InstructionHandler((short) 4, splitArray);
                        instructionMemory[numOfInstructions] = value;
                        numOfInstructions++;
                        break;
                    case "ANDI":
                        value = InstructionHandler((short) 5, splitArray);
                        instructionMemory[numOfInstructions] = value;
                        numOfInstructions++;
                        break;
                    case "EOR":
                        value = InstructionHandler((short) 6, splitArray);
                        instructionMemory[numOfInstructions] = value;
                        numOfInstructions++;
                        break;
                    case "BR":
                        value = InstructionHandler((short) 7, splitArray);
                        instructionMemory[numOfInstructions] = value;
                        numOfInstructions++;
                        break;
                    case "SAL":
                        value = InstructionHandler((short) 8, splitArray);
                        instructionMemory[numOfInstructions] = value;
                        numOfInstructions++;
                        break;
                    case "SAR":
                        value = InstructionHandler((short) 9, splitArray);
                        instructionMemory[numOfInstructions] = value;
                        numOfInstructions++;
                        break;
                    case "LDR":
                        value = InstructionHandler((short) 10, splitArray);
                        instructionMemory[numOfInstructions] = value;
                        numOfInstructions++;
                        break;
                    case "STR":
                        value = InstructionHandler((short) 11, splitArray);
                        instructionMemory[numOfInstructions] = value;
                        numOfInstructions++;
                        break;
                    default:
                        throw new Exception("Make sure the program contains the proper instruction format");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runWithPipelining() {
        parse();
        int clockCycles = numOfInstructions + 2;
        for (int i = 1; i <= clockCycles; i++) {
            if (i == 1) {
                System.out.println("Cycle Number: " + i);
                System.out.println("Fetching Instruction: " + instructionMemory[i - 1]);
                fetch();
                System.out.println("----------------------------------------------------");
            } else if (i == clockCycles) {
                System.out.println("Cycle Number: " + i);
                System.out.println("Executing Instruction: " + instructionMemory[i - 3]);
                execute();
                System.out.println("----------------------------------------------------");
            } else if (i == 2) {
                System.out.println("Cycle Number: " + i);
                System.out.println("Fetching Instruction: " + instructionMemory[i - 1]);
                System.out.println("Decoding Instruction: " + instructionMemory[i - 2]);
                decode(instructionToDecode);
                fetch();
                System.out.println("----------------------------------------------------");
            } else if (i == clockCycles - 1) {
                System.out.println("Cycle Number: " + i);
                System.out.println("Decoding Instruction: " + instructionMemory[i - 2]);
                System.out.println("Executing Instruction: " + instructionMemory[i - 3]);
                execute();
                decode(instructionToDecode);
                System.out.println("----------------------------------------------------");

            } else {
                System.out.println("Cycle Number: " + i);
                System.out.println("Fetching Instruction: " + instructionMemory[i - 1]);
                System.out.println("Decoding Instruction: " + instructionMemory[i - 2]);
                System.out.println("Executing Instruction: " + instructionMemory[i - 3]);
                execute();
                decode(instructionToDecode);
                fetch();
                System.out.println("----------------------------------------------------");
            }
        }
        System.out.println("PC =" + PC);
        System.out.println("Z flag =" + (sreg & (0b00000001)));
        System.out.println("S flag =" + ((sreg & ((0b00000010))) >> 1));
        System.out.println("N flag =" + ((sreg & ((0b00000100))) >> 2));
        System.out.println("V flag =" + ((sreg & ((0b00001000))) >> 3));
        System.out.println("C flag =" + ((sreg & ((0b00010000))) >> 4));
        System.out.println("----------------------------------------------------");
        for (int i = 0; i < registerFile.length; i++)
            System.out.print("R" + i + " :" + registerFile[i] + " ,");
        System.out.println();
        System.out.println("----------------------------------------------------");
        System.out.println("Data Memory content is :");
        for (int i = 0; i < dataMemory.length; i++)
            System.out.print(dataMemory[i] + " ,");
        System.out.println();
        System.out.println("----------------------------------------------------");
        System.out.println("Instruction Memory content is :");
        for (int i = 0; i < instructionMemory.length; i++)
            System.out.print(instructionMemory[i] + " ,");

    }

    public static void main(String[] args) throws Exception {

        CAProject test = new CAProject();
        test.runWithPipelining();

    }
}
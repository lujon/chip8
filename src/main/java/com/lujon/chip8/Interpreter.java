package com.lujon.chip8;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class Interpreter {
  private static final int INITIAL_PC = 0x200;

  private final Memory memory;
  private final Display display = new Display();
  private final int[] registers = new int[16];
  private int indexRegister = 0;
  private int pc = INITIAL_PC;

  public Interpreter(Memory memory) {
    this.memory = memory;
  }

  public void runFixedCycles(int numCycles) {
    while (numCycles > 0) {
      runCycle();
      numCycles--;
    }
  }

  private void runCycle() {
    int opCode = fetchInstruction();

    int[] opCodeNibble = new int[4];
    opCodeNibble[0] = ((opCode & 0xF000) >> 12);
    opCodeNibble[1] = ((opCode & 0x0F00) >> 8);
    opCodeNibble[2] = ((opCode & 0x00F0) >> 4);
    opCodeNibble[3] = (opCode & 0x000F);

    switch (opCodeNibble[0]) {
      case 0x0:
        if (opCode == 0x00E0) {
          display.clear();
        }
        break;
      case 0x1:
        pc = opCode & 0x0FFF;
        break;
      case 0x2:
        break;
      case 0x3:
        break;
      case 0x4:
        break;
      case 0x5:
        break;
      case 0x6:
        registers[opCodeNibble[1]] = opCode & 0x00FF;
        break;
      case 0x7:
        registers[opCodeNibble[1]] = registers[opCodeNibble[1]] + (opCode & 0x00FF);
        break;
      case 0x8:
        break;
      case 0x9:
        break;
      case 0xA:
        indexRegister = opCode & 0x0FFF;
        break;
      case 0xB:
        break;
      case 0xC:
        break;
      case 0xD:
        int xCoord = (registers[opCodeNibble[1]] & 0xFF) % 64;
        int yCoord = (registers[opCodeNibble[2]] & 0xFF) % 32;

        registers[0xF] = 0;

        for (int pixelRow = 0; pixelRow < opCodeNibble[3]; pixelRow++) {
          byte sprite = memory.getByte(indexRegister + pixelRow);

          boolean[] bits = new boolean[] {
              (sprite & 0b10000000) != 0,
              (sprite & 0b01000000) != 0,
              (sprite & 0b00100000) != 0,
              (sprite & 0b00010000) != 0,
              (sprite & 0b00001000) != 0,
              (sprite & 0b00000100) != 0,
              (sprite & 0b00000010) != 0,
              (sprite & 0b00000001) != 0,
          };

          int displayYCoord = yCoord + pixelRow;

          for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
            int displayXCoord = xCoord + bitIndex;

            boolean previousPixel = display.getPixel(displayXCoord, displayYCoord);
            boolean newPixel = bits[bitIndex];

            display.setPixel(displayXCoord, displayYCoord, !previousPixel && newPixel);

            if(previousPixel && newPixel){
              registers[0xF] = (byte)0x01;
            }

            if (displayXCoord == display.getWidth() - 1) {
              break;
            }
          }

          if (displayYCoord == display.getHeight() - 1) {
            break;
          }
        }

        break;
      case 0xE:
        break;
      case 0xF:
        break;
      default:
        break;
    }
  }

  private int fetchInstruction() {
    int opCode = (memory.getByte(pc) << 8) | (memory.getByte(pc + 1) & 0xFF);
    pc += 2;
    return opCode;
  }


  public Display getDisplay() {
    return display;
  }

  public static void main(String[] args) throws IOException {
    Memory memory = new Memory();
    InputStream ibmLogoFileStream = memory.getClass().getClassLoader()
        .getResourceAsStream("ibm-logo.ch8");
    memory.init(Objects.requireNonNull(Objects.requireNonNull(ibmLogoFileStream).readAllBytes()));

    Interpreter interpreter = new Interpreter(memory);

    interpreter.runFixedCycles(20);

    interpreter.getDisplay().show();
  }
}

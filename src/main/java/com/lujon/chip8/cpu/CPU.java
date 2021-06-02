package com.lujon.chip8.cpu;

import com.lujon.chip8.memory.Memory;
import com.lujon.chip8.screen.Screen;

public class CPU {

  private static final int INITIAL_PC = 0x200;

  private final Memory memory;
  private final Screen screen;
  private final int[] registers = new int[16];
  private int pc = INITIAL_PC;
  private int indexRegister;

  public CPU(Memory memory, Screen screen) {
    this.memory = memory;
    this.screen = screen;
  }

  public void executeCycle() {
    Instruction instruction = fetchInstruction();
    executeInstruction(instruction);
  }

  private Instruction fetchInstruction() {
    int instruction = (memory.getByte(pc) << 8) | (memory.getByte(pc + 1) & 0xFF);
    pc += 2;
    return new Instruction(instruction);
  }

  private void executeInstruction(Instruction instruction) {
    switch (instruction.getOpCode()) {
      case 0x0:
        if (instruction.getNN() == 0x00E0) {
          screen.clear();
        }
        break;
      case 0x1:
        pc = instruction.getNNN();
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
        registers[instruction.getX()] = instruction.getNN();
        break;
      case 0x7:
        registers[instruction.getX()] += instruction.getNN();
        break;
      case 0x8:
        break;
      case 0x9:
        break;
      case 0xA:
        indexRegister = instruction.getNNN();
        break;
      case 0xB:
        break;
      case 0xC:
        break;
      case 0xD:
        int xCoord = (registers[instruction.getX()]) % 64;
        int yCoord = (registers[instruction.getY()]) % 32;

        registers[0xF] = 0;

        for (int pixelRow = 0; pixelRow < instruction.getN(); pixelRow++) {
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

          int screenYCoord = yCoord + pixelRow;

          for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
            int screenXCoord = xCoord + bitIndex;

            boolean previousPixel = screen.getPixel(screenXCoord, screenYCoord);
            boolean newPixel = bits[bitIndex];

            screen.setPixel(screenXCoord, screenYCoord, !previousPixel && newPixel);

            if(previousPixel && newPixel){
              registers[0xF] = (byte)0x01;
            }

            if (screenXCoord == screen.getWidth() - 1) {
              break;
            }
          }

          if (screenYCoord == screen.getHeight() - 1) {
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
}

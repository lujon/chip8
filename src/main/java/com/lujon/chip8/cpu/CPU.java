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
    int instruction = fetchInstruction();
    executeInstruction(instruction);
  }

  private int fetchInstruction() {
    int instruction = (memory.getByte(pc) << 8) | (memory.getByte(pc + 1) & 0xFF);
    pc += 2;
    return instruction;
  }

  private void executeInstruction(int instruction) {

    int[] opCodeNibble = new int[4];
    opCodeNibble[0] = ((instruction & 0xF000) >> 12);
    opCodeNibble[1] = ((instruction & 0x0F00) >> 8);
    opCodeNibble[2] = ((instruction & 0x00F0) >> 4);
    opCodeNibble[3] = (instruction & 0x000F);

    switch (opCodeNibble[0]) {
      case 0x0:
        if (instruction == 0x00E0) {
          screen.clear();
        }
        break;
      case 0x1:
        pc = instruction & 0x0FFF;
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
        registers[opCodeNibble[1]] = instruction & 0x00FF;
        break;
      case 0x7:
        registers[opCodeNibble[1]] = registers[opCodeNibble[1]] + (instruction & 0x00FF);
        break;
      case 0x8:
        break;
      case 0x9:
        break;
      case 0xA:
        indexRegister = instruction & 0x0FFF;
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

package com.lujon.chip8.cpu;

import com.lujon.chip8.memory.Memory;
import com.lujon.chip8.screen.Screen;

public class CPU {

  public static final int INITIAL_PC = 0x200;

  private final Memory memory;
  private final Screen screen;
  private final int[] registers = new int[16];
  private int programCounter = INITIAL_PC;
  private int indexRegister;

  public CPU(Memory memory, Screen screen) {
    this.memory = memory;
    this.screen = screen;
  }

  public void executeInstructionFromMemory() {
    Instruction instruction = fetchInstruction();
    executeInstruction(instruction);
  }

  private Instruction fetchInstruction() {
    int instruction = (memory.getByte(programCounter) << 8) | (memory.getByte(programCounter + 1) & 0xFF);
    programCounter += 2;
    return new Instruction(instruction);
  }

  public void executeInstruction(Instruction instruction) {
    switch (instruction.getOpCode()) {
      case 0x0:
        if (instruction.getNN() == 0xE0) {
          clearScreen();
        }
        break;
      case 0x1:
        jumpToAddress(instruction.getNNN());
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
        setRegister(instruction.getX(), instruction.getNN());
        break;
      case 0x7:
        addToRegister(instruction.getX(), instruction.getNN());
        break;
      case 0x8:
        break;
      case 0x9:
        break;
      case 0xA:
        setIndexRegister(instruction.getNNN());
        break;
      case 0xB:
        break;
      case 0xC:
        break;
      case 0xD:
        drawSprite(instruction.getX(), instruction.getY(), instruction.getN());
        break;
      case 0xE:
        break;
      case 0xF:
        break;
      default:
        break;
    }
  }

  // 00E0 - CLS
  private void clearScreen() {
    screen.clear();
  }

  // 1nnn - JP addr
  private void jumpToAddress(int address) {
    programCounter = address;
  }

  // 6xkk - LD Vx, byte
  private void setRegister(int register, int value) {
    registers[register] = value;
  }

  // 7xkk - ADD Vx, byte
  private void addToRegister(int register, int value) {
    registers[register] += value;
  }

  // Annn - LD I, addr
  private void setIndexRegister(int address) {
    indexRegister = address;
  }

  // Dxyn - DRW Vx, Vy, nibble
  private void drawSprite(int xRegister, int yRegister, int numRows) {
    int x = registers[xRegister] % 64;
    int y = registers[yRegister] % 32;

    registers[0xF] = 0;

    for (int row = 0; row < numRows; row++) {
      byte sprite = memory.getByte(indexRegister + row);

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

      int screenYCoord = y + row;

      for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
        int screenXCoord = x + bitIndex;

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
  }

  public int getProgramCounter() {
    return programCounter;
  }
}

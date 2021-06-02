package com.lujon.chip8.cpu;

import com.lujon.chip8.memory.Memory;
import com.lujon.chip8.screen.Screen;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

public class CPU {

  public static final int INITIAL_PC = 0x200;

  private final Memory memory;
  private final Screen screen;
  private final byte[] registers = new byte[16];
  private int programCounter = INITIAL_PC;
  private int indexRegister;
  private final Deque<Integer> stack = new ArrayDeque<>();

  public CPU(Memory memory, Screen screen) {
    this.memory = memory;
    this.screen = screen;
  }

  public void executeInstructionFromMemory() {
    Instruction instruction = fetchInstruction();
    executeInstruction(instruction);
  }

  private Instruction fetchInstruction() {
    byte firstByte = memory.getByte(programCounter);
    byte secondByte = memory.getByte(programCounter + 1);
    int instruction = (firstByte & 0xFF) << 8 | secondByte & 0xFF;

    programCounter += 2;

    return new Instruction(instruction);
  }

  public void executeInstruction(Instruction instruction) {
    System.out.println("Executing " +  instruction.toString());

    switch (instruction.getOpCode()) {
      case 0x0:
        switch (instruction.getNN()) {
          case 0xE0:
            clearScreen();
            break;
          case 0xEE:
            returnFromSubroutine();
            break;
          default:
            throw new RuntimeException("Not implemented: " + instruction);
        }
        break;
      case 0x1:
        jumpToAddress(instruction.getNNN());
        break;
      case 0x2:
        jumpToSubroutine(instruction.getNNN());
        break;
      case 0x3:
        skipInstructionIfRegisterEqualToValue(instruction.getX(), instruction.getNN());
        break;
      case 0x4:
        skipInstructionIfRegisterNotEqualToValue(instruction.getX(), instruction.getNN());
        break;
      case 0x5:
        skipInstructionIfRegisterEqualToOtherRegister(instruction.getX(), instruction.getY());
        break;
      case 0x6:
        setRegister(instruction.getX(), instruction.getNN());
        break;
      case 0x7:
        addToRegister(instruction.getX(), instruction.getNN());
        break;
      case 0x8:
        switch (instruction.getN()) {
          case 0x0:
            setRegisterToOtherRegister(instruction.getX(), instruction.getY());
            break;
          case 0x1:
            orRegisters(instruction.getX(), instruction.getY());
            break;
          case 0x2:
            andRegisters(instruction.getX(), instruction.getY());
            break;
          case 0x3:
            xorRegisters(instruction.getX(), instruction.getY());
            break;
          default:
            throw new RuntimeException("Not implemented: " + instruction);
        }
        break;
      case 0x9:
        skipInstructionIfRegisterNotEqualToOtherRegister(instruction.getX(), instruction.getY());
        break;
      case 0xA:
        setIndexRegister(instruction.getNNN());
        break;
      case 0xD:
        drawSprite(instruction.getX(), instruction.getY(), instruction.getN());
        break;
      case 0xF:
        switch (instruction.getNN()) {
          case 0x55:
            storeRegistersAtIndex(instruction.getX());
            break;
          case 0x65:
            loadRegistersAtIndex(instruction.getX());
            break;
          default:
            throw new RuntimeException("Not implemented: " + instruction);
        }
        break;
      default:
        throw new RuntimeException("Not implemented: " + instruction);
    }
  }

  // 00E0 - CLS
  private void clearScreen() {
    screen.clear();
  }

  // 00EE - RET
  private void returnFromSubroutine() {
    programCounter = stack.pop();
  }

  // 1nnn - JP addr
  private void jumpToAddress(int address) {
    programCounter = address;
  }

  // 2nnn - CALL addr
  private void jumpToSubroutine(int address) {
    stack.push(programCounter);
    programCounter = address;
  }

  // 3xkk - SE Vx, byte
  private void skipInstructionIfRegisterEqualToValue(int register, int value) {
    if (getRegister(register) == value) {
      programCounter += 2;
    }
  }

  // 4xkk - SNE Vx, byte
  private void skipInstructionIfRegisterNotEqualToValue(int register, int value) {
    if (getRegister(register) != value) {
      programCounter += 2;
    }
  }

  // 5xy0 - SE Vx, Vy
  private void skipInstructionIfRegisterEqualToOtherRegister(int register1, int register2) {
    if (getRegister(register1) == getRegister(register2)) {
      programCounter += 2;
    }
  }

  // 6xkk - LD Vx, byte
  private void setRegister(int register, int value) {
    registers[register] = (byte) value;
  }

  // 7xkk - ADD Vx, byte
  private void addToRegister(int register, int value) {
    registers[register] += value;
  }

  // 8xy0 - LD Vx, Vy
  private void setRegisterToOtherRegister(int toRegister, int fromRegister) {
    registers[toRegister] = registers[fromRegister];
  }

  // 8xy1 - OR Vx, Vy
  private void orRegisters(int register1, int register2) {
    registers[register1] |= registers[register2];
  }

  // 8xy2 - AND Vx, Vy
  private void andRegisters(int register1, int register2) {
    registers[register1] &= registers[register2];
  }

  // 8xy3 - XOR Vx, Vy
  private void xorRegisters(int register1, int register2) {
    registers[register1] ^= registers[register2];
  }

  // 9xy0 - SNE Vx, Vy
  private void skipInstructionIfRegisterNotEqualToOtherRegister(int register1, int register2) {
    if (getRegister(register1) != getRegister(register2)) {
      programCounter += 2;
    }
  }

  // Annn - LD I, addr
  private void setIndexRegister(int address) {
    indexRegister = address;
  }

  // Dxyn - DRW Vx, Vy, nibble
  private void drawSprite(int xRegister, int yRegister, int numRows) {
    int x = registers[xRegister] % 64;
    int y = registers[yRegister] % 32;

    setRegister(0xF, 0);

    for (int rowIndex = 0; rowIndex < numRows; rowIndex++) {
      byte sprite = memory.getByte(indexRegister + rowIndex);

      int screenY = y + rowIndex;

      for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
        int screenX = x + bitIndex;

        boolean previousPixel = screen.getPixel(screenX, screenY);
        boolean newPixel = (sprite >> (7-bitIndex) & 1) == 0x1;

        screen.setPixel(screenX, screenY, !previousPixel && newPixel);

        if(previousPixel && newPixel){
          setRegister(0xF, 0x01);
        }

        if (screenX == screen.getWidth() - 1) {
          break;
        }
      }

      if (screenY == screen.getHeight() - 1) {
        break;
      }
    }
  }

  // Fx55 - LD [I], Vx
  private void storeRegistersAtIndex(int endRegister) {
    for (int i = 0; i <= endRegister; i++) {
      memory.setByte(indexRegister + i, (byte) getRegister(i));
    }
  }

  // Fx65 - LD Vx, [I]
  private void loadRegistersAtIndex(int endRegister) {
    for (int i = 0; i <= endRegister; i++) {
      setRegister(i, memory.getByte(indexRegister + i));
    }
  }

  public int getProgramCounter() {
    return programCounter;
  }

  public int getRegister(int index) {
    return registers[index] & 0xFF;
  }

  public int getIndexRegister() {
    return indexRegister;
  }

  public Deque<Integer> getStack() {
    return stack;
  }
}

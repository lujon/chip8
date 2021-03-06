package com.lujon.chip8.cpu;

import com.lujon.chip8.memory.Memory;
import com.lujon.chip8.screen.Screen;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class CPU {

  public static final int INITIAL_PC = 0x200;

  private final Memory memory;
  private final Screen screen;
  private final Random randomNumberGenerator;
  private final byte[] registers = new byte[16];
  private int programCounter = INITIAL_PC;
  private int indexRegister;
  private int delayTimer;
  private final Deque<Integer> stack = new ArrayDeque<>();

  public CPU(Memory memory, Screen screen) {
    this(memory, screen, new Random());
  }

  public CPU(Memory memory, Screen screen, Random randomNumberGenerator) {
    this.memory = memory;
    this.screen = screen;
    this.randomNumberGenerator = randomNumberGenerator;
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
          case 0x4:
            addRegisters(instruction.getX(), instruction.getY());
            break;
          case 0x5:
            subtractRegisterVYFromVX(instruction.getX(), instruction.getY());
            break;
          case 0x6:
            rightShiftRegister(instruction.getX());
            break;
          case 0x7:
            subtractRegisterVXFromVY(instruction.getX(), instruction.getY());
            break;
          case 0xE:
            leftShiftRegister(instruction.getX());
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
      case 0xB:
        jumpToAddressPlusV0(instruction.getNNN());
        break;
      case 0xC:
        setRegisterToRandomNumber(instruction.getX(), instruction.getNN());
        break;
      case 0xD:
        drawSprite(instruction.getX(), instruction.getY(), instruction.getN());
        break;
      case 0xF:
        switch (instruction.getNN()) {
          case 0x07:
            loadDelayTimer(instruction.getX());
            break;
          case 0x15:
            setDelayTimer(instruction.getX());
            break;
          case 0x1E:
            addRegisterToIndex(instruction.getX());
            break;
          case 0x29:
            setIndexToFontSpriteAddress(instruction.getX());
            break;
          case 0x33:
            storeBCDRepresentationAtIndex(instruction.getX());
            break;
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
    setRegister(register, getRegister(register) + value);
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

  // 8xy4 - ADD Vx, Vy
  private void addRegisters(int register1, int register2) {
    int vx = getRegister(register1);
    int vy = getRegister(register2);

    setRegister(0xF, vx + vy > 0xFF ? 0x01 : 0x00);
    setRegister(register1, vx + vy);
  }

  // 8xy5 - SUB Vx, Vy
  private void subtractRegisterVYFromVX(int register1, int register2) {
    int vx = getRegister(register1);
    int vy = getRegister(register2);

    setRegister(0xF, vx >= vy ? 1 : 0);
    setRegister(register1, vx - vy);
  }

  // 8xy6 - SHR Vx {, Vy}
  private void rightShiftRegister(int register) {
    int registerValue = getRegister(register);

    setRegister(0xF, (registerValue & 0x1) == 0x1 ? 0x01 : 0x00);
    setRegister(register, registerValue >> 1);
  }

  // 8xy7 - SUBN Vx, Vy
  private void subtractRegisterVXFromVY(int register1, int register2) {
    int vx = getRegister(register1);
    int vy = getRegister(register2);

    setRegister(0xF, vy >= vx ? 0x01 : 0x00);
    setRegister(register1, vy - vx);
  }

  // 8xyE - SHL Vx {, Vy}
  private void leftShiftRegister(int register) {
    int registerValue = getRegister(register);

    setRegister(0xF, (registerValue & 0x80) == 0x80 ? 0x01 : 0x00);
    setRegister(register, registerValue << 1);
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

  // Bnnn - JP V0, addr
  private void jumpToAddressPlusV0(int address) {
    programCounter = address + getRegister(0x0);
  }

  // Cxkk - RND Vx, byte
  private void setRegisterToRandomNumber(int register, int andValue) {
    int randomNumber = randomNumberGenerator.nextInt(256) & andValue;
    setRegister(register, randomNumber);
  }

  // Dxyn - DRW Vx, Vy, nibble
  private void drawSprite(int xRegister, int yRegister, int numRows) {
    int x = getRegister(xRegister) % 64;
    int y = getRegister(yRegister) % 32;

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

  // Fx07 - LD Vx, DT
  private void loadDelayTimer(int register) {
    setRegister(register, delayTimer);
  }

  // Fx15 - LD DT, Vx
  private void setDelayTimer(int register) {
    delayTimer = getRegister(register);
  }

  // Fx1E - ADD I, Vx
  private void addRegisterToIndex(int register) {
    indexRegister += getRegister(register);
  }

  // Fx29 - LD F, Vx
  private void setIndexToFontSpriteAddress(int register) {
    int fontSprite = registers[register] & 0xFF;

    indexRegister = Memory.FONT_START_ADDRESS + (5 * fontSprite);
  }

  // Fx33 - LD B, Vx
  private void storeBCDRepresentationAtIndex(int register) {
    int value = getRegister(register);

    int hundreds = (value / 100);
    value -= hundreds * 100;

    int tens = (value / 10);
    value -= tens * 10;

    memory.setByte(indexRegister, (byte) hundreds);
    memory.setByte(indexRegister + 1, (byte) tens);
    memory.setByte(indexRegister + 2, (byte) value);
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

  public int getDelayTimer() {
    return delayTimer;
  }
}

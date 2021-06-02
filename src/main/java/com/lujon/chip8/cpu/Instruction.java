package com.lujon.chip8.cpu;

class Instruction {

  private final int instruction;

  Instruction(int instruction) {
    this.instruction = instruction;
  }

  int getOpCode() {
    return (instruction & 0xF000) >> 12;
  }

  int getX() {
    return (instruction & 0x0F00) >> 8;
  }

  int getY() {
    return (instruction & 0x00F0) >> 4;
  }

  int getN() {
    return instruction & 0x000F;
  }

  int getNN() {
    return instruction & 0x00FF;
  }

  int getNNN() {
    return instruction & 0x0FFF;
  }

  @Override
  public String toString() {
    return Integer.toHexString(instruction);
  }
}

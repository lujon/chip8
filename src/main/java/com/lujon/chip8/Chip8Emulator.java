package com.lujon.chip8;

import com.lujon.chip8.cpu.CPU;
import com.lujon.chip8.memory.Memory;
import com.lujon.chip8.screen.Screen;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class Chip8Emulator {

  private final CPU cpu;

  public Chip8Emulator(Memory memory, Screen screen) {
    this.cpu = new CPU(memory, screen);
  }

  public void runFixedCycles(int numCycles) {
    while (numCycles > 0) {
      cpu.executeInstructionFromMemory();
      numCycles--;
    }
  }

  public static void main(String[] args) throws IOException {
    Memory memory = new Memory();
    InputStream ibmLogoFileStream = memory.getClass().getClassLoader()
        .getResourceAsStream("ibm-logo.ch8");
    memory.init(Objects.requireNonNull(Objects.requireNonNull(ibmLogoFileStream).readAllBytes()));

    Screen screen = new Screen();

    Chip8Emulator chip8Emulator = new Chip8Emulator(memory, screen);

    chip8Emulator.runFixedCycles(20);

    screen.show();
  }
}

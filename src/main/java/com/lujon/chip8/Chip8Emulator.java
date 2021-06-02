package com.lujon.chip8;

import com.lujon.chip8.cpu.CPU;
import com.lujon.chip8.memory.Memory;
import com.lujon.chip8.screen.Screen;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class Chip8Emulator {

  private final CPU cpu;
  private final Screen screen;

  public Chip8Emulator(Memory memory, Screen screen) {
    this.screen = screen;
    this.cpu = new CPU(memory, screen);
  }

  public void run() throws InterruptedException {
    while (true) {
      cpu.executeInstructionFromMemory();
      screen.draw();
      Thread.sleep(100);
    }
  }

  public void runFixedCycles(int numCycles) {
    while (numCycles > 0) {
      cpu.executeInstructionFromMemory();
      numCycles--;
    }
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    Memory memory = new Memory();
    InputStream ibmLogoFileStream = memory.getClass().getClassLoader()
        .getResourceAsStream("test_opcode.ch8");
    memory.init(Objects.requireNonNull(Objects.requireNonNull(ibmLogoFileStream).readAllBytes()));

    Screen screen = new Screen();

    Chip8Emulator chip8Emulator = new Chip8Emulator(memory, screen);

    chip8Emulator.run();
  }
}

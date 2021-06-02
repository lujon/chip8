package com.lujon.chip8.cpu;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.lujon.chip8.memory.Memory;
import com.lujon.chip8.screen.Screen;
import org.junit.Test;

public class CPUTest {

  @Test
  public void testClearScreen() {
    Memory memory = new Memory();
    Screen screen = new Screen();
    CPU cpu = new CPU(memory, screen);

    screen.setPixel(0, 0, true);

    assertTrue(screen.getPixel(0, 0));

    cpu.executeInstruction(new Instruction(0x00E0));

    assertFalse(screen.getPixel(0, 0));
  }
}
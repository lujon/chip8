package com.lujon.chip8.cpu;

import static org.junit.Assert.assertEquals;
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

  @Test
  public void testJumpToAddress() {
    Memory memory = new Memory();
    Screen screen = new Screen();
    CPU cpu = new CPU(memory, screen);

    assertEquals(CPU.INITIAL_PC, cpu.getProgramCounter());

    cpu.executeInstruction(new Instruction(0x100F));

    assertEquals(0x00F, cpu.getProgramCounter());
  }

  @Test
  public void testSetRegister() {
    Memory memory = new Memory();
    Screen screen = new Screen();
    CPU cpu = new CPU(memory, screen);

    assertEquals(0x00, cpu.getRegister(0xA));

    cpu.executeInstruction(new Instruction(0x6A0F));

    assertEquals(0x0F, cpu.getRegister(0xA));
  }

  @Test
  public void testAddToRegister() {
    Memory memory = new Memory();
    Screen screen = new Screen();
    CPU cpu = new CPU(memory, screen);

    assertEquals(0x00, cpu.getRegister(0xA));

    cpu.executeInstruction(new Instruction(0x6A0F));
    cpu.executeInstruction(new Instruction(0x7AF0));

    assertEquals(0xFF, cpu.getRegister(0xA));
  }
}
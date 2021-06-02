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
    Screen screen = new Screen(false);
    CPU cpu = new CPU(new Memory(), screen);

    screen.setPixel(0, 0, true);

    assertTrue(screen.getPixel(0, 0));

    cpu.executeInstruction(new Instruction(0x00E0));

    assertFalse(screen.getPixel(0, 0));
  }

  @Test
  public void testJumpToAddress() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    assertEquals(CPU.INITIAL_PC, cpu.getProgramCounter());

    cpu.executeInstruction(new Instruction(0x100F));

    assertEquals(0x00F, cpu.getProgramCounter());
  }

  @Test
  public void testSetRegister() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    assertEquals(0x00, cpu.getRegister(0xA));

    cpu.executeInstruction(new Instruction(0x6A0F));

    assertEquals(0x0F, cpu.getRegister(0xA));
  }

  @Test
  public void testAddToRegister() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    assertEquals(0x00, cpu.getRegister(0xA));

    // Set register 0xA to 0x0F
    cpu.executeInstruction(new Instruction(0x6A0F));
    // Add 0xF0 to register 0xA
    cpu.executeInstruction(new Instruction(0x7AF0));

    assertEquals(0xFF, cpu.getRegister(0xA));
  }

  @Test
  public void testSetIndexRegister() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    assertEquals(0x000, cpu.getIndexRegister());

    cpu.executeInstruction(new Instruction(0xA0FF));

    assertEquals(0x0FF, cpu.getIndexRegister());
  }

  @Test
  public void testDrawSprite() {
    Memory memory = new Memory();
    Screen screen = new Screen(false);
    CPU cpu = new CPU(memory, screen);

    // Add an 8x8 sprite to memory

    int spriteAddress = 0x200;

    for (int i = 0; i < 8; i++) {
      memory.setByte(spriteAddress + i, (byte) 0xFF);
    }

    // Set index register to sprite address
    cpu.executeInstruction(new Instruction(0xA200));

    // Set register 0 and 1 to xcoord 16 and ycoord 16
    cpu.executeInstruction(new Instruction(0x600F));
    cpu.executeInstruction(new Instruction(0x610F));

    // Draw sprite on screen
    cpu.executeInstruction(new Instruction(0xD018));

    int spriteX = 16;
    int spriteY = 16;

    // All sprite pixels should be set now

    assertTrue(screen.getPixel(spriteX, spriteY));

    // Register 0xF should be set to 0x00 since no overlapping sprites have been drawn
    assertEquals(0x00, cpu.getRegister(0xF));

    // Drawing the same sprite again in the same screen location should cancel out the first one
    cpu.executeInstruction(new Instruction(0xD018));

    // All sprite pixels should be unset now

    assertFalse(screen.getPixel(spriteX, spriteY));

    // Register 0xF should be set to 0x01 overlapping sprites have been drawn
    assertEquals(0x01, cpu.getRegister(0xF));
  }

  @Test
  public void testSkipInstructionIfRegisterEqualToValue() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    // Set register 0xA to 0x0F
    cpu.executeInstruction(new Instruction(0x6A0F));

    // Set register 0xA to 0x0F
    cpu.executeInstruction(new Instruction(0x3A0F));

    assertEquals(CPU.INITIAL_PC + 2, cpu.getProgramCounter());
  }
}
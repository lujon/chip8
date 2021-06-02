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

    cpu.executeInstruction(new Instruction(0x3A0F));

    assertEquals(CPU.INITIAL_PC + 2, cpu.getProgramCounter());
  }

  @Test
  public void testSkipInstructionIfRegisterNotEqualToValue() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    // Set register 0xA to 0x0F
    cpu.executeInstruction(new Instruction(0x6A0F));

    cpu.executeInstruction(new Instruction(0x4A1F));

    assertEquals(CPU.INITIAL_PC + 2, cpu.getProgramCounter());
  }

  @Test
  public void testSkipInstructionIfRegisterEqualToOtherRegister() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    // Set register 0xA to 0x0F
    cpu.executeInstruction(new Instruction(0x6A0F));

    // Set register 0xB to 0x0F
    cpu.executeInstruction(new Instruction(0x6B0F));

    cpu.executeInstruction(new Instruction(0x5AB0));

    assertEquals(CPU.INITIAL_PC + 2, cpu.getProgramCounter());
  }

  @Test
  public void testSkipInstructionIfRegisterNotEqualToOtherRegister() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    // Set register 0xA to 0x0F
    cpu.executeInstruction(new Instruction(0x6A0F));

    // Set register 0xB to 0x1F
    cpu.executeInstruction(new Instruction(0x6B1F));

    cpu.executeInstruction(new Instruction(0x9AB0));

    assertEquals(CPU.INITIAL_PC + 2, cpu.getProgramCounter());
  }

  @Test
  public void testStoreRegistersAtIndex() {
    Memory memory = new Memory();
    CPU cpu = new CPU(memory, new Screen(false));

    // Set registers 0x0-0x3 to values 0x00-0x03
    cpu.executeInstruction(new Instruction(0x6000));
    cpu.executeInstruction(new Instruction(0x6101));
    cpu.executeInstruction(new Instruction(0x6202));
    cpu.executeInstruction(new Instruction(0x6303));

    // Set index register to 0x100
    cpu.executeInstruction(new Instruction(0xA100));

    // Store registers 0-3 at index I
    cpu.executeInstruction(new Instruction(0xF355));

    assertEquals(0x00, memory.getByte(0x100));
    assertEquals(0x01, memory.getByte(0x101));
    assertEquals(0x02, memory.getByte(0x102));
    assertEquals(0x03, memory.getByte(0x103));
  }

  @Test
  public void testLoadRegistersAtIndex() {
    Memory memory = new Memory();
    CPU cpu = new CPU(memory, new Screen(false));

    // Set memory 0x100-0x103 to values 0x00-0x03
    memory.setByte(0x100, (byte) 0x00);
    memory.setByte(0x101, (byte) 0x01);
    memory.setByte(0x102, (byte) 0x02);
    memory.setByte(0x103, (byte) 0x03);

    // Set index register to 0x100
    cpu.executeInstruction(new Instruction(0xA100));

    // Store registers 0-3 at index I
    cpu.executeInstruction(new Instruction(0xF365));

    assertEquals(0x00, cpu.getRegister(0));
    assertEquals(0x01, cpu.getRegister(1));
    assertEquals(0x02, cpu.getRegister(2));
    assertEquals(0x03, cpu.getRegister(3));
  }

  @Test
  public void testJumpToSubRoutine() {
    CPU cpu = new CPU(new Memory(), new Screen(false));
    cpu.executeInstruction(new Instruction(0x2500));

    assertEquals(0x500, cpu.getProgramCounter());
    assertEquals(CPU.INITIAL_PC, (int) cpu.getStack().peek());
  }

  @Test
  public void testReturnFromSubroutine() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    cpu.getStack().push(0x200);

    cpu.executeInstruction(new Instruction(0x00EE));

    assertEquals(0x200, cpu.getProgramCounter());
  }

  @Test
  public void testSetRegisterToOtherRegister() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    // Set register 0 to 0x0F
    cpu.executeInstruction(new Instruction(0x600F));

    // Copy register 0 to register 1
    cpu.executeInstruction(new Instruction(0x8100));

    assertEquals(0x0F, cpu.getRegister(1));
  }

  @Test
  public void testOrRegisters() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    // Set register 0 to 0x0F
    cpu.executeInstruction(new Instruction(0x600F));

    // Set register 1 to 0xF0
    cpu.executeInstruction(new Instruction(0x61F0));

    // V0 |= V1
    cpu.executeInstruction(new Instruction(0x8011));

    assertEquals(0xFF, cpu.getRegister(0));
  }

  @Test
  public void testAndRegisters() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    // Set register 0 to 0x0F
    cpu.executeInstruction(new Instruction(0x600F));

    // Set register 1 to 0xF0
    cpu.executeInstruction(new Instruction(0x61F0));

    // V0 &= V1
    cpu.executeInstruction(new Instruction(0x8012));

    assertEquals(0x00, cpu.getRegister(0));
  }

  @Test
  public void testXorRegisters() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    // Set register 0 to 0x0F
    cpu.executeInstruction(new Instruction(0x600F));

    // Set register 1 to 0xFF
    cpu.executeInstruction(new Instruction(0x61FF));

    // V0 ^= V1
    cpu.executeInstruction(new Instruction(0x8013));

    assertEquals(0xF0, cpu.getRegister(0));
  }

  @Test
  public void testAddRegisters() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    // Set register 0 to 0x01
    cpu.executeInstruction(new Instruction(0x6001));

    // Set register 1 to 0x02
    cpu.executeInstruction(new Instruction(0x6102));

    // V0 += V1
    cpu.executeInstruction(new Instruction(0x8014));

    assertEquals(0x03, cpu.getRegister(0));
  }

  @Test
  public void testAddRegistersWithCarry() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    // Set register 0 to 0xFF
    cpu.executeInstruction(new Instruction(0x60FF));

    // Set register 1 to 0x0F
    cpu.executeInstruction(new Instruction(0x610F));

    // V0 += V1
    cpu.executeInstruction(new Instruction(0x8014));

    assertEquals(0x0E, cpu.getRegister(0));
    assertEquals(0x1, cpu.getRegister(0xF));
  }
}
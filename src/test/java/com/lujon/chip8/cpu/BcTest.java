package com.lujon.chip8.cpu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.lujon.chip8.memory.Memory;
import com.lujon.chip8.screen.Screen;
import org.junit.Test;

/**
 *  Tests translated from the decompilation of
 *  the BestCoder test rom BC_test.ch8
 */

public class BcTest {

  /**
   * v5 := 0xEE
   * if v5 != 0xEE then FAIL
   */

  @Test
  public void test01() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    cpu.executeInstruction(new Instruction(0x65EE));

    assertEquals(0xEE, cpu.getRegister(0x5));
  }

  /**
   * v5 := 0xEE
   * v6 := 0xEE
   * if v5 != v6 then FAIL
   */

  @Test
  public void test02() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    cpu.executeInstruction(new Instruction(0x65EE));
    cpu.executeInstruction(new Instruction(0x66EE));

    assertEquals(0xEE, cpu.getRegister(0x5));
    assertEquals(0xEE, cpu.getRegister(0x6));
  }

  /**
   * v5 := 0xEE
   * if v5 == 0xFD then FAIL
   */

  @Test
  public void test03() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    cpu.executeInstruction(new Instruction(0x65EE));

    assertNotEquals(0xFD, cpu.getRegister(0x5));
  }

  /**
   * v5 := 0xEE
   * v5 += 0x01
   * if v5 != 0xEF then FAIL
   */

  @Test
  public void test04() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    cpu.executeInstruction(new Instruction(0x65EE));
    cpu.executeInstruction(new Instruction(0x7501));

    assertEquals(0xEF, cpu.getRegister(0x5));
  }

  /**
   * vF := 0x01
   * v5 := 0xEE
   * v6 := 0xEF
   * v5 -= v6
   * if vF != 0x00 then FAIL
   */

  @Test
  public void test05() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    cpu.executeInstruction(new Instruction(0x6F01));
    cpu.executeInstruction(new Instruction(0x65EE));
    cpu.executeInstruction(new Instruction(0x66EF));
    cpu.executeInstruction(new Instruction(0x8565));

    assertEquals(0x00, cpu.getRegister(0xF));
  }

  /**
   * vF := 0x00
   * v5 := 0xEF
   * v6 := 0xEE
   * v5 -= v6
   * if vF != 0x01 then FAIL
   */

  @Test
  public void test06() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    cpu.executeInstruction(new Instruction(0x6F00));
    cpu.executeInstruction(new Instruction(0x65EF));
    cpu.executeInstruction(new Instruction(0x66EE));
    cpu.executeInstruction(new Instruction(0x8565));

    assertEquals(0x01, cpu.getRegister(0xF));
  }

  /**
   * vF := 0x00
   * v5 := 0xEE
   * v6 := 0xEF
   * v5 =- v6 # result is always 0x01
   * if vF != 0x01 then FAIL
   */

  @Test
  public void test07() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    cpu.executeInstruction(new Instruction(0x6F00));
    cpu.executeInstruction(new Instruction(0x65EE));
    cpu.executeInstruction(new Instruction(0x66EF));
    cpu.executeInstruction(new Instruction(0x8567));

    assertEquals(0x01, cpu.getRegister(0xF));
  }

  /**
   * vF := 0x01
   * v5 := 0xEF
   * v6 := 0xEE
   * v5 =- v6
   * if vF != 0x00 then FAIL
   */

  @Test
  public void test08() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    cpu.executeInstruction(new Instruction(0x6F01));
    cpu.executeInstruction(new Instruction(0x65EF));
    cpu.executeInstruction(new Instruction(0x66EE));
    cpu.executeInstruction(new Instruction(0x8567));

    assertEquals(0x00, cpu.getRegister(0xF));
  }

  /**
   * v5 := 0xF0
   * v6 := 0x0F
   * v5 |= v6 # result is always 0xFF
   * if v5 != 0xFF then FAIL
   */

  @Test
  public void test09() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    cpu.executeInstruction(new Instruction(0x65F0));
    cpu.executeInstruction(new Instruction(0x660F));
    cpu.executeInstruction(new Instruction(0x8561));

    assertEquals(0xFF, cpu.getRegister(0x5));
  }

  /**
   * v5 := 0xF0
   * v6 := 0x0F
   * v5 &= v6 # result is always 0x00
   * if v5 != 0x00 then FAIL
   */

  @Test
  public void test10() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    cpu.executeInstruction(new Instruction(0x65F0));
    cpu.executeInstruction(new Instruction(0x660F));
    cpu.executeInstruction(new Instruction(0x8562));

    assertEquals(0x00, cpu.getRegister(0x5));
  }

  /**
   * v5 := 0xF0
   * v6 := 0x0F
   * v5 ^= v6
   * if v5 != 0xFF then FAIL
   */

  @Test
  public void test11() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    cpu.executeInstruction(new Instruction(0x65F0));
    cpu.executeInstruction(new Instruction(0x660F));
    cpu.executeInstruction(new Instruction(0x8563));

    assertEquals(0xFF, cpu.getRegister(0x5));
  }

  /**
   * 	vF := 0x00
   * 	v5 := 0x81
   * 	v5 <<= v0
   * 	if vF != 0x01 then FAIL
   */

  @Test
  public void test12() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    cpu.executeInstruction(new Instruction(0x6F00));
    cpu.executeInstruction(new Instruction(0x6581));
    cpu.executeInstruction(new Instruction(0x850E));

    assertEquals(0x01, cpu.getRegister(0xF));
  }

  /**
   * vF := 0x01
   * v5 := 0x47
   * v5 <<= v0
   * if vF != 0x00 then FAIL
   */

  @Test
  public void test13() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    cpu.executeInstruction(new Instruction(0x6F01));
    cpu.executeInstruction(new Instruction(0x6547));
    cpu.executeInstruction(new Instruction(0x850E));

    assertEquals(0x00, cpu.getRegister(0xF));
  }

  /**
   * vF := 0x00
   * v5 := 0x01
   * v5 >>= v0 # result is always 0x00
   * if vF != 0x01 then FAIL
   */

  @Test
  public void test14() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    cpu.executeInstruction(new Instruction(0x6F00));
    cpu.executeInstruction(new Instruction(0x6501));
    cpu.executeInstruction(new Instruction(0x8506));

    assertEquals(0x01, cpu.getRegister(0xF));
  }

  /**
   * vF := 0x01
   * v5 := 0x02
   * v5 >>= v0 # result is always 0x01
   * if vF != 0x00 then FAIL
   */

  @Test
  public void test15() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    cpu.executeInstruction(new Instruction(0x6F01));
    cpu.executeInstruction(new Instruction(0x6502));
    cpu.executeInstruction(new Instruction(0x8506));

    assertEquals(0x00, cpu.getRegister(0xF));
  }

  /**
   * v0 := 0x15
   * v1 := 0x78
   * i := label-8
   * save v1
   * load v1
   * if v0 != 0x15 then FAIL
   * if v1 != 0x78 then FAIL
   */

  @Test
  public void test16() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    cpu.executeInstruction(new Instruction(0x6015));
    cpu.executeInstruction(new Instruction(0x6178));
    cpu.executeInstruction(new Instruction(0xA3D0));
    cpu.executeInstruction(new Instruction(0xF155));
    cpu.executeInstruction(new Instruction(0xF165));

    assertEquals(0x15, cpu.getRegister(0x0));
    assertEquals(0x78, cpu.getRegister(0x1));
  }

  /**
   * v0 := 0x8A
   * i := 0x3D0
   * bcd v0
   * i := 0x3D0
   * load v0
   * if v0 != 0x01 then FAIl
   * v0 := 0x01
   * i += v0
   * load v0
   * if v0 != 0x03 then FAIL
   * v0 := 0x01
   * i += v0
   * load v0
   * if v0 != 0x08 then FAIL
   */

  @Test
  public void test17() {
    CPU cpu = new CPU(new Memory(), new Screen(false));

    cpu.executeInstruction(new Instruction(0x608A));
    cpu.executeInstruction(new Instruction(0xA3D0));
    cpu.executeInstruction(new Instruction(0xF033));
    cpu.executeInstruction(new Instruction(0xA3D0));
    cpu.executeInstruction(new Instruction(0xF065));

    assertEquals(0x01, cpu.getRegister(0x0));

    cpu.executeInstruction(new Instruction(0x6001));
    cpu.executeInstruction(new Instruction(0xF01E));
    cpu.executeInstruction(new Instruction(0xF065));

    assertEquals(0x03, cpu.getRegister(0x0));

    cpu.executeInstruction(new Instruction(0x6001));
    cpu.executeInstruction(new Instruction(0xF01E));
    cpu.executeInstruction(new Instruction(0xF065));

    assertEquals(0x08, cpu.getRegister(0x0));
  }
}

package com.lujon.chip8;

import static org.junit.Assert.assertEquals;

import com.lujon.chip8.memory.Memory;
import com.lujon.chip8.screen.Screen;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import org.junit.Test;

public class Chip8EmulatorTest {

  @Test
  public void testIbmLogoIsRenderedProperly() throws IOException {
    Memory memory = new Memory();
    InputStream ibmLogoFileStream = memory.getClass().getClassLoader()
        .getResourceAsStream("ibm-logo.ch8");
    memory.init(Objects.requireNonNull(Objects.requireNonNull(ibmLogoFileStream).readAllBytes()));

    Screen screen = new Screen();
    Chip8Emulator chip8Emulator = new Chip8Emulator(memory, screen);

    chip8Emulator.runFixedCycles(20);

    assertEquals(1999623313, screen.hashCode());
  }
}
package com.lujon.chip8;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import junit.framework.TestCase;
import org.junit.Test;

public class InterpreterTest extends TestCase {

  @Test
  public void testIbmLogoIsRenderedProperly() throws IOException {
    Memory memory = new Memory();
    InputStream ibmLogoFileStream = memory.getClass().getClassLoader()
        .getResourceAsStream("ibm-logo.ch8");
    memory.init(Objects.requireNonNull(Objects.requireNonNull(ibmLogoFileStream).readAllBytes()));

    Interpreter interpreter = new Interpreter(memory);

    interpreter.runFixedCycles(20);

    assertEquals(1999623313, interpreter.getDisplay().hashCode());
  }
}
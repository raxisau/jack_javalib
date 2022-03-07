package com.jackbooted.tools.os;

import org.junit.Test;

public class ShellTest {

    @Test
    public void testCommand () {
        System.out.println ( "Returns: " + Shell.command ( "ps -awx" ) );
    }

}

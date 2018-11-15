package com.se.npe.androidnote.util;

import org.junit.Test;

import java.util.Random;

public class ReturnValueEaterTest {
    @Test
    public void eatInt() {
        ReturnValueEater.eat(1);
        ReturnValueEater.eat(new Random().nextInt());
        for (int i = 0; i < 100; ++i) {
            ReturnValueEater.eat(i);
        }
    }

    @Test
    public void eatBoolean() {
        ReturnValueEater.eat(true);
        ReturnValueEater.eat(false);
    }
}
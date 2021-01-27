/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.wiringpi.Gpio;

/**
 *
 * @author David.JARDIM
 */
public class LedTest {

    public static void main(String[] args) {
        System.out.println("Test");
        System.out.println("Test");

        GpioController gpio = GpioFactory.getInstance();
        GpioPinDigitalOutput led = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02);

        for (int i = 0; i < 1000; i++) {
            led.setState(i % 2 == 0);
            Gpio.delay(500);
        }
        gpio.shutdown();
    }
}

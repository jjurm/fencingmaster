package com.jjurm.talentum.fencingmaster;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

public class Pins {

	public static final Pin PIN_RIGHT_RED = RaspiPin.GPIO_13;
	public static final Pin PIN_RIGHT_GREEN = RaspiPin.GPIO_26;
	public static final Pin PIN_RIGHT_BLUE = RaspiPin.GPIO_12;
	public static final Pin PIN_UP_RED = RaspiPin.GPIO_28;
	public static final Pin PIN_UP_GREEN = RaspiPin.GPIO_29;
	public static final Pin PIN_UP_BLUE = RaspiPin.GPIO_25;
	public static final Pin PIN_LEFT_RED = RaspiPin.GPIO_24;
	public static final Pin PIN_LEFT_GREEN = RaspiPin.GPIO_27;
	public static final Pin PIN_LEFT_BLUE = RaspiPin.GPIO_23;
	public static final Pin PIN_DOWN_RED = RaspiPin.GPIO_05;
	public static final Pin PIN_DOWN_GREEN = RaspiPin.GPIO_06;
	public static final Pin PIN_DOWN_BLUE = RaspiPin.GPIO_04;

	public static final Pin PIN_BACKBUTTON = RaspiPin.GPIO_03;

	public static final Pin PIN_PLATE_RIGHT = RaspiPin.GPIO_01;
	public static final Pin PIN_PLATE_UP = RaspiPin.GPIO_07;
	public static final Pin PIN_PLATE_LEFT = RaspiPin.GPIO_02;
	public static final Pin PIN_PLATE_DOWN = RaspiPin.GPIO_00;

	public static final Pin PIN_FOOT_FRONT = RaspiPin.GPIO_10;
	public static final Pin PIN_FOOT_BACK = RaspiPin.GPIO_11;
	
	
	public static final Pin[] PINS_LEDS = {
		PIN_RIGHT_RED,
		PIN_RIGHT_GREEN,
		PIN_RIGHT_BLUE,
		PIN_UP_RED,
		PIN_UP_GREEN,
		PIN_UP_BLUE,
		PIN_LEFT_RED,
		PIN_LEFT_GREEN,
		PIN_LEFT_BLUE,
		PIN_DOWN_RED,
		PIN_DOWN_GREEN,
		PIN_DOWN_BLUE
	};
	
	public static final Pin[][] PINS_SIDES_COLORS = {
		{PIN_RIGHT_RED, PIN_RIGHT_GREEN, PIN_RIGHT_BLUE},
		{PIN_UP_RED, PIN_UP_GREEN, PIN_UP_BLUE},
		{PIN_LEFT_RED, PIN_LEFT_GREEN, PIN_LEFT_BLUE},
		{PIN_DOWN_RED, PIN_DOWN_GREEN, PIN_DOWN_BLUE}
	};
	
	public static final Pin[] PINS_PLATES = {
		PIN_PLATE_RIGHT,
		PIN_PLATE_UP,
		PIN_PLATE_LEFT,
		PIN_PLATE_DOWN
	};

	public static final Pin[] PINS_FEET = {
		PIN_FOOT_FRONT,
		PIN_FOOT_BACK
	};
	
}

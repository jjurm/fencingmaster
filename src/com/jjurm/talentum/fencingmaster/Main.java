package com.jjurm.talentum.fencingmaster;

import static com.jjurm.talentum.fencingmaster.enums.Side.DOWN;
import static com.jjurm.talentum.fencingmaster.enums.Side.LEFT;
import static com.jjurm.talentum.fencingmaster.enums.Side.RIGHT;
import static com.jjurm.talentum.fencingmaster.enums.Side.UP;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.jjurm.talentum.fencingmaster.enums.Button;
import com.jjurm.talentum.fencingmaster.enums.Foot;
import com.jjurm.talentum.fencingmaster.enums.Side;
import com.jjurm.talentum.fencingmaster.game.FMState;
import com.jjurm.talentum.fencingmaster.game.Game;
import com.jjurm.talentum.fencingmaster.game.Welcome;
import com.jjurm.talentum.fencingmaster.rgb.RGBController;
import com.jjurm.talentum.fencingmaster.web.WebServer;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;

public class Main {

	public static final Side[] AVAILABLE_SIDES = {RIGHT, UP, LEFT, DOWN};
	
	private static GpioController gpio;
	private static RGBController rgb;
	
	private static Game game;
	private static WebServer webServer;
	
	private static Thread mainThread;
	private static AtomicBoolean exitPrepared = new AtomicBoolean(false);
	
	public static void run(String[] args) {
		
		System.out.println("Started");
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				Main.prepareExit();
			}
		});
		
		mainThread = Thread.currentThread();

		gpio = GpioFactory.getInstance();
		rgb = new RGBController(gpio, Pins.PINS_SIDES_COLORS);
		game = new Game(rgb);
		prepareGpio();
		
		try {
			webServer = new WebServer(game);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		FMState state = new Welcome(game);
		game.switchState(state);
		
		System.out.println("Main thread finished");
		try {
			while (true) {
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			System.out.println("Interrupted");
			//exit();
		}
		System.out.println("Main thread exited");

	}
	
	static void prepareGpio() {
		GpioPinDigitalInput in;
		
		in = gpio.provisionDigitalInputPin(Pins.PIN_BACKBUTTON, PinPullResistance.OFF);
		//in.setDebounce(200);
		in.addListener(new GpioGameListener.ButtonListener(game, Button.BACKBUTTON));
		
		for (Side side : Side.ALL) {
			Pin pin = Pins.PINS_PLATES[side.index()];
			in = new GpioPinDebounceWrapper(gpio.provisionDigitalInputPin(pin, PinPullResistance.OFF));
			/*GpioPinDigitalOutputChangeMonitor monitor = new GpioPinDigitalOutputChangeMonitor(in);
			monitor.addListener(new GpioGameListener.PlateListener(game, side));
			monitor.start();*/
			//in.setDebounce(100);
			in.addListener(new GpioGameListener.PlateListener(game, side));
		}
		
		for (Foot foot : Foot.ALL) {
			Pin pin = Pins.PINS_FEET[foot.index()];
			in = new GpioPinDebounceWrapper(gpio.provisionDigitalInputPin(pin, PinPullResistance.OFF));
			/*GpioPinDigitalOutputChangeMonitor monitor = new GpioPinDigitalOutputChangeMonitor(in);
			monitor.addListener(new GpioGameListener.FootListener(game, foot));
			monitor.start();*/
			//in.setDebounce(100);
			in.addListener(new GpioGameListener.FootListener(game, foot));
		}
		
	}
	
	public static void prepareExit() {
		if (exitPrepared.compareAndSet(false, true)) {
			System.out.println("Shutting down");
			
			/*if (mainThread != null)
				mainThread.interrupt();*/
			
			if (game != null)
				game.shutdown();
			
			if (gpio != null)
				gpio.shutdown();
			
			System.out.println("Exiting");
		}
	}
	
	public static void exit() {
		
		prepareExit();
		System.exit(0);
		
	}

}

package com.jjurm.talentum.fencingmaster;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.jjurm.talentum.fencingmaster.enums.Foot;
import com.jjurm.talentum.fencingmaster.enums.Side;
import com.jjurm.talentum.fencingmaster.enums.UserAction;
import com.jjurm.talentum.fencingmaster.game.FMState;
import com.jjurm.talentum.fencingmaster.game.Game;
import com.jjurm.talentum.fencingmaster.game.fmstate.Welcome;
import com.jjurm.talentum.fencingmaster.rgb.RGBController;
import com.jjurm.talentum.fencingmaster.utils.ObjectHolder;
import com.jjurm.talentum.fencingmaster.web.WebServer;
import com.jjurm.talentum.fencingmaster.web.WebSocketServerImpl;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;

public class Main {

	private static GpioController gpio;
	private static RGBController rgb;
	
	private static Game game;
	private static WebServer webServer;
	private static WebSocketServerImpl webSocketServer;
	
	private static AtomicBoolean exitPrepared = new AtomicBoolean(false);
	
	public static void run(String[] args) {
		
		System.out.println("Started");
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				Main.prepareExit();
			}
		});
		
		gpio = GpioFactory.getInstance();
		rgb = new RGBController(gpio, Pins.PINS_SIDES_COLORS);
		
		//Sound.cacheSounds();
		
		ObjectHolder<Game> gameHolder = new ObjectHolder<Game>();
		
		try {
			webServer = new WebServer(gameHolder);
			webServer.start();
			
			webSocketServer = new WebSocketServerImpl(gameHolder);
			webSocketServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		game = new Game(rgb, webSocketServer);
		gameHolder.set(game);
		prepareGpio();
		
		FMState state = new Welcome(game, true);
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
		in.addListener(new GpioGameListener.UserActionListener(game, UserAction.BACKBUTTON));
		
		for (Side side : Side.ALL) {
			Pin pin = Pins.PINS_PLATES[side.index()];
			//in = new GpioPinDebounceWrapper(gpio.provisionDigitalInputPin(pin, PinPullResistance.OFF));
			in = gpio.provisionDigitalInputPin(pin, PinPullResistance.OFF);
			in.addListener(new GpioGameListener.PlateListener(game, side));
			/*GpioPinDigitalOutputChangeMonitor monitor = new GpioPinDigitalOutputChangeMonitor(in);
			monitor.addListener(new GpioGameListener.PlateListener(game, side));
			monitor.start();*/
		}
		
		for (Foot foot : Foot.ALL) {
			Pin pin = Pins.PINS_FEET[foot.index()];
			in = new GpioPinDebounceWrapper(gpio.provisionDigitalInputPin(pin, PinPullResistance.OFF));
			in.addListener(new GpioGameListener.FootListener(game, foot));
			/*GpioPinDigitalOutputChangeMonitor monitor = new GpioPinDigitalOutputChangeMonitor(in);
			monitor.addListener(new GpioGameListener.FootListener(game, foot));
			monitor.start();*/
		}
		
	}
	
	public static void prepareExit() {
		if (exitPrepared.compareAndSet(false, true)) {
			System.out.println("Shutting down");
			
			/*if (mainThread != null)
				mainThread.interrupt();*/
			
			if (game != null)
				game.terminate();
			
			if (webServer != null)
				webServer.stop();
			
			if (webSocketServer != null) {
				try {
					webSocketServer.stop(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
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

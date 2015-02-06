package com.jjurm.talentum.fencingmaster.control;

import static com.jjurm.talentum.fencingmaster.enums.Side.DOWN;
import static com.jjurm.talentum.fencingmaster.enums.Side.LEFT;
import static com.jjurm.talentum.fencingmaster.enums.Side.RIGHT;
import static com.jjurm.talentum.fencingmaster.enums.Side.UP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.jjurm.talentum.fencingmaster.Main;
import com.jjurm.talentum.fencingmaster.enums.Button;
import com.jjurm.talentum.fencingmaster.enums.Foot;
import com.jjurm.talentum.fencingmaster.enums.State;
import com.jjurm.talentum.fencingmaster.game.Game;

public class Commander {

	public static final String OK_CHARACTER = ".";
	public static final String WARN_CHARACTER = "!";
	public static final String ERR_CHARACTER = "?";

	Game game;

	Map<String, Command> commands = new HashMap<String, Command>();

	public Commander(Game game) {
		this.game = game;
		
		createCommands();
	}

	void createCommands() {
		commands.put("exit", new Command() {
			@Override
			public void process(String[] args, BufferedReader br, PrintWriter pw) throws StreamCloseRequest {
				pw.print("Exit command received");
				Main.exit();
			}
		});
		commands.put("interact", new CommandGroup() {
			{
				commands.put("plate", new CommandGroup() {
					{
						commands.put("right", (args, br, pw) -> game.plateTouched(RIGHT));
						commands.put("up", (args, br, pw) -> game.plateTouched(UP));
						commands.put("left", (args, br, pw) -> game.plateTouched(LEFT));
						commands.put("down", (args, br, pw) -> game.plateTouched(DOWN));
					}
				});
				commands.put("button", new CommandGroup() {
					{
						commands.put("back", new CommandGroup() {
							{
								commands.put("down", (args, br, pw) -> game.buttonPressed(Button.BACKBUTTON, State.INTERACTING));
								commands.put("up", (args, br, pw) -> game.buttonPressed(Button.BACKBUTTON, State.NON_INTERACTING));
							}
						});
					}
				});
				commands.put("foot", new CommandGroup() {
					{
						commands.put("front", new CommandGroup() {
							{
								commands.put("down", (args, br, pw) -> game.footChanged(Foot.FRONT, State.INTERACTING));
								commands.put("up", (args, br, pw) -> game.footChanged(Foot.FRONT, State.NON_INTERACTING));
							}
						});
						commands.put("back", new CommandGroup() {
							{
								commands.put("down", (args, br, pw) -> game.footChanged(Foot.BACK, State.INTERACTING));
								commands.put("up", (args, br, pw) -> game.footChanged(Foot.BACK, State.NON_INTERACTING));
							}
						});
					}
				});
			}
		});
	}

	public void process(BufferedReader br, PrintWriter pw) throws StreamCloseRequest {
		try {

			String line = br.readLine();
			if (line == null) {
				throw new StreamCloseRequest();
			}
			String[] parts = line.trim().split("\\s+");
			String commandName = parts[0].trim();

			Command command = commands.get(commandName);
			if (commandName.length() >= 1 && command != null) {
				String[] args = Arrays.copyOfRange(parts, 1, parts.length);
				command.process(args, br, pw);
			} else {
				pw.println(ERR_CHARACTER);
			}
			pw.println(OK_CHARACTER);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

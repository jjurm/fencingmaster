package com.jjurm.talentum.fencingmaster.history;

import com.jjurm.talentum.fencingmaster.enums.Button;
import com.jjurm.talentum.fencingmaster.enums.Side;
import com.jjurm.talentum.fencingmaster.enums.State;

public abstract class UserAction<T> {

	protected final T spec;
	
	public UserAction(T spec) {
		this.spec = spec;
	}
	
	@Override
	public String toString() {
		return _toString();
	}
	
	protected abstract String _toString();
	
	public abstract static class StateChangedUserAction<T> extends UserAction<T> {

		protected final State state;
		
		public StateChangedUserAction(T spec, State state) {
			super(spec);
			this.state = state;
		}
		
	}
	
	public static class ButtonChanged extends StateChangedUserAction<Button> {

		
		public ButtonChanged(Button button, State state) {
			super(button, state);
		}

		@Override
		protected String _toString() {
			return new StringBuilder("button:")
				.append(spec.toString())
				.append("(")
				.append(state.value() ? "1" : "0")
				.append(")")
				.toString();
		}
		
	}
	
	public static class PlateTouched extends UserAction<Side> {

		public PlateTouched(Side side) {
			super(side);
		}

		@Override
		protected String _toString() {
			return "plate:" + spec.toString();
		}
		
	}
	
	public static class FootChanged extends StateChangedUserAction<Side> {

		public FootChanged(Side side, State state) {
			super(side, state);
		}

		@Override
		protected String _toString() {
			return "plate:" + spec.toString();
		}
		
	}
	
}

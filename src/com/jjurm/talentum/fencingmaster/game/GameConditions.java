package com.jjurm.talentum.fencingmaster.game;

import org.json.JSONWriter;

import com.jjurm.talentum.fencingmaster.Config;
import com.jjurm.talentum.fencingmaster.enums.Condition;
import com.jjurm.talentum.fencingmaster.web.WebObject;

public class GameConditions implements WebObject {

	final int reactionTime;
	final Condition reactionTimeCondition;
	final Condition plateCondition;
	final Condition footCondition;

	final boolean ok;

	private GameConditions(int reactionTime, Condition reactionTimeCondition, Condition plateCondition,
			Condition footCondition, boolean ok) {
		this.reactionTime = reactionTime;
		this.reactionTimeCondition = reactionTimeCondition;
		this.plateCondition = plateCondition;
		this.footCondition = footCondition;

		this.ok = ok;
	}

	public int getReactionTime() {
		return reactionTime;
	}

	public Condition getReactionTimeCondition() {
		return reactionTimeCondition;
	}

	public Condition getPlateCondition() {
		return plateCondition;
	}

	public Condition getFootCondition() {
		return footCondition;
	}

	public boolean isOk() {
		return ok;
	}

	@Override
	public void toJson(JSONWriter json) {
		json.object().key("reactionTime").value(reactionTime).key("reactionTimeCondition").value(reactionTimeCondition)
				.key("plateCondition").value(plateCondition).key("footCondition").value(footCondition).key("ok")
				.value(ok).endObject();
	}

	public static class Builder {

		Integer reactionTime = null;
		Condition reactionTimeCondition = null;
		Condition plateCondition = null;
		Condition footCondition = null;
		Boolean ok = null;

		public Builder() {
		}

		static boolean n(Object o) { // returns true if null
			return o == null;
		}

		public Builder reactionTime(int reactionTime) {
			this.reactionTime = reactionTime;
			return this;
		}

		public Builder reactionTime(Condition reactionTimeCondition) {
			this.reactionTimeCondition = reactionTimeCondition;
			return this;
		}

		public Builder plate(Condition plateCondition) {
			this.plateCondition = plateCondition;
			return this;
		}

		public Builder foot(Condition footCondition) {
			this.footCondition = footCondition;
			return this;
		}

		public Builder ok(boolean ok) {
			this.ok = ok;
			return this;
		}

		static Condition reactionTimeCondition(int reactionTime) {
			if (reactionTime == -1)
				return Condition.NOT_USED;
			else
				return Condition.get(reactionTimeOk(reactionTime));
		}

		static boolean reactionTimeOk(int reactionTime) {
			return (reactionTime < Config.CONST_hit_timeout);
		}

		public GameConditions build() {
			if (n(reactionTime))
				reactionTime = -1;
			if (n(reactionTimeCondition))
				reactionTimeCondition = reactionTimeCondition(reactionTime);
			if (n(plateCondition))
				plateCondition = Condition.NOT_USED;
			if (n(footCondition))
				footCondition = Condition.NOT_USED;
			if (n(ok))
				ok = reactionTimeCondition.isOk() && plateCondition.isOk() && footCondition.isOk();
			return new GameConditions(reactionTime, reactionTimeCondition, plateCondition, footCondition, ok);
		}

	}

}

package com.creature;

import java.lang.IllegalArgumentException;

public class Effect {
	private int turns;
	private int value;
	private AttackType attackType;

	public Effect(int turns, int value, AttackType attackType) {
		this.turns = turns;
		this.value = value;
		this.attackType = attackType;
	}

	public boolean setTurns() {
		turns = turns - 1;
		return turns >= 0;
	}

	public int getTurns() {
		return turns;
	}

	public int getValue() {
		return value;
	}
	
	public String getMessage() {
		switch (attackType) {
			case DAMAGE:
				return "NAME takes an additional " + value + " bleed damage.";
			case HEALING:
				return "NAME heals for " + value + " health.";
			case ATTACK:
				return "NAME's attack is lowered by " + value + " points.";
			case DEFENSE:
				return "NAME's defense is lowered by " + value + " points.";
			case SPEED:
				return "NAME's speed is lowered by " + value + " points.";
			case ACCURACY:
				return "NAME's accuracy is lowered by " + value + " percent.";
			case NOATTACK:
				return "NAME cannot attack this turn.";
			default:  
				throw new IllegalArgumentException("Unknown enum value found.");  
		}
	}

	public AttackType attackType() {
		return attackType;
	}
}
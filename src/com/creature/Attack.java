package com.creature;

public class Attack {
	private String name;
	private String desc;
	private String effectDesc;

	private Target target;

	private int baseDamage;
	private Effect effect;

	public Attack(String name, String desc, String effectDesc, Target target, int baseDamage, Effect effect) {
		this.name = name;
		this.desc = desc;
		this.effectDesc = effectDesc;
		this.target = target;
		this.baseDamage = baseDamage;
		this.effect = effect;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public String getEffectDesc() {
		return effectDesc;
	}

	public Target getTarget() {
		return target;
	}

	public int getBaseDamage() {
		return baseDamage;
	}

	public Effect getEffect() {
		return effect;
	}
}
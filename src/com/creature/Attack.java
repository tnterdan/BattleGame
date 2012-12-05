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

	// Attack name
	public String getName() {
		return name;
	}

	// Attack description
	public String getDesc() {
		return desc;
	}

	// Attack battle message
	public String getEffectDesc() {
		return effectDesc;
	}

	// Attack's target
	public Target getTarget() {
		return target;
	}

	// Attack's base damage
	public int getBaseDamage() {
		return baseDamage;
	}

	// Attack's effect
	public Effect getEffect() {
		return effect;
	}
}
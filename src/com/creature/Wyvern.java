package com.creature;

public class Wyvern extends Creature {
	public Wyvern(int randSeed) {
		super("Wyvern", 200, 35, 50, 20, randSeed);
		attackList.put(
			"Fire Breath",
			new Attack(
				"Fire Breath", 
				"Breathes fire at enemy, dealing 20 damage. Also lowers enemy defense for 2 turns.", 
				"ENEMYNAME has been burned by Wyvern's fire breath!",
				Target.OPPONENT,
				20,
				new Effect(2, 10, AttackType.DEFENSE)
			)
		);
		attackList.put(
			"Claw",
			new Attack(
				"Claw",
				"Slashes at foe with sharp claws to deal 30 damage.",
				"Wyvern slashes at ENEMYNAME with sharp claws!",
				Target.OPPONENT,
				30,
				null
			)
		);
		attackList.put(
			"Bite",
			new Attack(
				"Bite",
				"Bites foe with sharp teeth, dealing 25 damage. May prevent enemy attacks for one turn.",
				"Wyvern bites ENEMYNAME with razor-sharp teeth!",
				Target.OPPONENT,
				25,
				new Effect(1, 30, AttackType.NOATTACK)
			)
		);
	}
}
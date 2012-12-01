package com.creature;

public class Unicorn extends Creature {
	public Unicorn(int randSeed) {
		super("Unicorn", 180, 60, 35, 30, randSeed);
		attackList.put(
			"Horn Charge",
			new Attack(
				"Horn Charge", 
				"Stabs the foe with long horn. Deals 20 damage and causes opponent to bleed for a few turns.",
				"ENEMYNAME has been gored by Unicorn's horn stab! ENEMYNAME is bleeding!",
				Target.OPPONENT,
				20,
				new Effect(3, 5, AttackType.DAMAGE)
			)
		);
		attackList.put(
			"Heal",
			new Attack(
				"Heal",
				"Heals for 50 health.",
				"Unicorn's horn starts glowing! Unicorn is feeling a bit better!",
				Target.SELF,
				-50,
				null
			)
		);
		attackList.put(
			"Hoof Trample",
			new Attack(
				"Hoof Trample",
				"Tramples the enemy with strong hooves, dealing 25 damage. Enemy has a chance to miss attacks for a couple turns.",
				"Unicorn tramples the enemy with its strong hooves! Unicorn is now harder to hit!",
				Target.OPPONENT,
				25,
				new Effect(2, 15, AttackType.ACCURACY)
			)
		);
	}
}
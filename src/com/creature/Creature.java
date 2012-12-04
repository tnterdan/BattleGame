package com.creature;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Iterator;
import java.lang.IllegalArgumentException;

public abstract class Creature {
	// Creature Name
	private String name;

	// Attacks
	protected HashMap<String, Attack> attackList;

	// Effects
	private List<Effect> effectList;
	private boolean canAttack;

	// Random number generator
	Random rand;

	// --------- Stats ---------
	//   Health
	private int maxHealth;
	private int currentHealth;

	//   Base
	private int baseAttack;
	private int baseDefense;
	private int baseSpeed;
	private int baseAccuracy;

	//   Current Stats
	private int attack;
	private int defense;
	private int speed;
	private int accuracy;

	public Creature(String name, int maxHealth, int baseAttack, int baseDefense, int baseSpeed, int randSeed) {
		this.name = name;

		this.attackList = new LinkedHashMap<String, Attack>();
		this.effectList = new ArrayList<Effect>();
		this.canAttack = true;

		this.maxHealth = maxHealth;
		this.currentHealth = maxHealth;

		this.baseAttack = attack = baseAttack;
		this.baseDefense = defense = baseDefense;
		this.baseSpeed = speed = baseSpeed;
		this.baseAccuracy = accuracy = 100;

		rand = new Random(randSeed);
	}

	public Attack getAttack(int index) {
		return (new ArrayList<Attack>(attackList.values())).get(index);
	}

	public Attack getAttack(String name) {
		return attackList.get(name);
	}

	public void addEffect(Effect effect) {
		effectList.add(effect);
	}

	// run this method at end of each turn to remove old effects and apply the damage
	// also to set current defense, attack, accuracy, and speed
	public void applyEffects() {
		Iterator<Effect> i = effectList.iterator();
		while (i.hasNext()) {
			Effect effect = i.next();
			if(!effect.setTurns()) {
				i.remove();
				switch (effect.attackType()) {
					case DAMAGE:
					case HEALING:
						break;
					case ATTACK:
						attack = baseAttack;
						break;
					case DEFENSE:
						defense = baseDefense;
						break;
					case SPEED:
						speed = baseSpeed;
						break;
					case ACCURACY:
						accuracy = baseAccuracy;
						break;
					case NOATTACK:
						canAttack = true;
						break;
					default:  
						throw new IllegalArgumentException("Unknown enum value found.");  
				}
			}
			else {
				System.out.println(effect.getMessage().replace("NAME", getName()));
				switch (effect.attackType()) {
					case DAMAGE:
						setHealth(getHealth() - effect.getValue());
						break;
					case HEALING:
						setHealth(getHealth() + effect.getValue());
						break;
					case ATTACK:
						attack = baseAttack - effect.getValue();
						break;
					case DEFENSE:
						defense = baseDefense - effect.getValue();
						break;
					case SPEED:
						speed = baseSpeed - effect.getValue();
						break;
					case ACCURACY:
						accuracy = baseAccuracy - effect.getValue();
						break;
					case NOATTACK:
						canAttack = rand.nextInt(101) > effect.getValue();
					default:  
						throw new IllegalArgumentException("Unknown enum value found.");  
				}
			}
		}
	}

	public void setHealth(int health) {
		this.currentHealth = health > 0 ? health : 0;
	}

	public void takeDamage(int damage) {
		setHealth((int)(getHealth() - (damage * ((100 - (float)defense) / 100))));
	}

	public String getName() {
		return name;
	}

	public int getBaseAttack() {
		return attack;
	}

	public int getSpeed() {
		return speed;
	}

	public int getAccuracy() {
		return accuracy;
	}
	
	public boolean canAttack() {
		return canAttack;
	}

	public int getHealth() {
		return currentHealth;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public boolean isAlive() {
		return currentHealth > 0;
	}
}
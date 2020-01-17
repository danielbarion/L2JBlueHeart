/*
 * Copyright (C) 2004-2013 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.zone.DragonValley;

import java.util.EnumMap;

import l2r.gameserver.model.actor.L2Attackable;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.L2Playable;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.base.ClassId;
import l2r.gameserver.model.holders.SkillHolder;
import l2r.gameserver.util.Util;
import l2r.util.Rnd;

import ai.npc.AbstractNpcAI;

/**
 * Dragon Valley AI.
 * @author St3eT
 */
public final class DragonValley extends AbstractNpcAI
{
	// NPC
	private static final int GEM_DRAGON = 22830;
	private static final int GEM_DRAGON_HATCHLING = 22837;
	private static final int DRAKOS_ASSASSIN = 22823;
	private static final int[] SUMMON_NPC =
	{
		22824, // Drakos Guardian
		22862, // Drakos Hunter
	};
	private static final int[] SPAWN_ANIMATION =
	{
		22826, // Scorpion Bones
		22823, // Drakos Assassin
		22828, // Parasitic Leech
		
	};
	private static final int[] SPOIL_REACT_MONSTER =
	{
		22822, // Drakos Warrior
		22823, // Drakos Assassin
		22824, // Drakos Guardian
		22825, // Giant Scorpion Bones
		22826, // Scorpion Bones
		22827, // Batwing Drake
		22828, // Parasitic Leech
		22829, // Emerald Drake
		22830, // Gem Dragon
		22831, // Dragon Tracker of the Valley
		22832, // Dragon Scout of the Valley
		22833, // Sand Drake Tracker
		22834, // Dust Dragon Tracker
		22860, // Hungry Parasitic Leech
		22861, // Hard Scorpion Bones
		22862, // Drakos Hunter
	};
	
	// Items
	private static final int GREATER_HERB_OF_MANA = 8604;
	private static final int SUPERIOR_HERB_OF_MANA = 8605;
	
	// Skills
	private static final SkillHolder MORALE_BOOST1 = new SkillHolder(6885, 1);
	private static final SkillHolder MORALE_BOOST2 = new SkillHolder(6885, 2);
	private static final SkillHolder MORALE_BOOST3 = new SkillHolder(6885, 3);
	
	// Misc
	private final int SPAWN_CHANCE = 100; // Retail 100%
	private static final int MIN_DISTANCE = 1500;
	private static final int MIN_MEMBERS = 3;
	private static final int MIN_LVL = 80;
	private static final int CLASS_LVL = 3;
	private static final EnumMap<ClassId, Double> CLASS_POINTS = new EnumMap<>(ClassId.class);
	
	{
		CLASS_POINTS.put(ClassId.adventurer, 0.2);
		CLASS_POINTS.put(ClassId.arcanaLord, 1.5);
		CLASS_POINTS.put(ClassId.archmage, 0.3);
		CLASS_POINTS.put(ClassId.cardinal, -0.6);
		CLASS_POINTS.put(ClassId.dominator, 0.2);
		CLASS_POINTS.put(ClassId.doombringer, 0.2);
		CLASS_POINTS.put(ClassId.doomcryer, 0.1);
		CLASS_POINTS.put(ClassId.dreadnought, 0.7);
		CLASS_POINTS.put(ClassId.duelist, 0.2);
		CLASS_POINTS.put(ClassId.elementalMaster, 1.4);
		CLASS_POINTS.put(ClassId.evaSaint, -0.6);
		CLASS_POINTS.put(ClassId.evaTemplar, 0.8);
		CLASS_POINTS.put(ClassId.femaleSoulhound, 0.4);
		CLASS_POINTS.put(ClassId.fortuneSeeker, 0.9);
		CLASS_POINTS.put(ClassId.ghostHunter, 0.2);
		CLASS_POINTS.put(ClassId.ghostSentinel, 0.2);
		CLASS_POINTS.put(ClassId.grandKhavatari, 0.2);
		CLASS_POINTS.put(ClassId.hellKnight, 0.6);
		CLASS_POINTS.put(ClassId.hierophant, 0.0);
		CLASS_POINTS.put(ClassId.judicator, 0.1);
		CLASS_POINTS.put(ClassId.moonlightSentinel, 0.2);
		CLASS_POINTS.put(ClassId.maestro, 0.7);
		CLASS_POINTS.put(ClassId.maleSoulhound, 0.4);
		CLASS_POINTS.put(ClassId.mysticMuse, 0.3);
		CLASS_POINTS.put(ClassId.phoenixKnight, 0.6);
		CLASS_POINTS.put(ClassId.sagittarius, 0.2);
		CLASS_POINTS.put(ClassId.shillienSaint, -0.6);
		CLASS_POINTS.put(ClassId.shillienTemplar, 0.8);
		CLASS_POINTS.put(ClassId.soultaker, 0.3);
		CLASS_POINTS.put(ClassId.spectralDancer, 0.4);
		CLASS_POINTS.put(ClassId.spectralMaster, 1.4);
		CLASS_POINTS.put(ClassId.stormScreamer, 0.3);
		CLASS_POINTS.put(ClassId.swordMuse, 0.4);
		CLASS_POINTS.put(ClassId.titan, 0.3);
		CLASS_POINTS.put(ClassId.trickster, 0.5);
		CLASS_POINTS.put(ClassId.windRider, 0.2);
	}
	
	public DragonValley()
	{
		super(DragonValley.class.getSimpleName(), "ai/zone/DragonValley");
		addAttackId(SUMMON_NPC);
		addKillId(SPOIL_REACT_MONSTER);
		addKillId(GEM_DRAGON);
		addSpawnId(SPOIL_REACT_MONSTER);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		if ((npc.getCurrentHp() < (npc.getMaxHp() / 2)) && (getRandom(100) < 5) && npc.isScriptValue(0))
		{
			npc.setScriptValue(1);
			final int rnd = getRandom(3, 5);
			for (int i = 0; i < rnd; i++)
			{
				if (Rnd.get(1000) <= (SPAWN_CHANCE * 10))
				{
					final L2Playable playable = isSummon ? attacker.getSummon() : attacker;
					final L2Npc minion = addSpawn(DRAKOS_ASSASSIN, npc.getX(), npc.getY(), npc.getZ() + 10, npc.getHeading(), true, 0, true);
					addAttackDesire(minion, playable);
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		if (npc.getId() == GEM_DRAGON) // Gem Dragon
		{
			if (getRandom(100) < 10)
			{
				final L2Attackable gemHatchling = (L2Attackable) addSpawn(GEM_DRAGON_HATCHLING, npc.getX(), npc.getY(), npc.getZ() + 10, npc.getHeading(), false, 0, true);
				attackPlayer(gemHatchling, killer);
			}
		}
		else if (((L2Attackable) npc).isSweepActive())
		{
			npc.dropItem(killer, getRandom(GREATER_HERB_OF_MANA, SUPERIOR_HERB_OF_MANA), 1);
			manageMoraleBoost(killer, npc);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		((L2Attackable) npc).setOnKillDelay(0);
		if (Util.contains(SPAWN_ANIMATION, npc.getId()))
		{
			npc.setShowSummonAnimation(true);
		}
		return super.onSpawn(npc);
	}
	
	private void manageMoraleBoost(L2PcInstance player, L2Npc npc)
	{
		double points = 0;
		int moraleBoostLv = 0;
		
		if (player.isInParty() && (player.getParty().getMemberCount() >= MIN_MEMBERS) && (npc != null))
		{
			for (L2PcInstance member : player.getParty().getMembers())
			{
				if ((member.getLevel() >= MIN_LVL) && (member.getClassId().level() >= CLASS_LVL) && (Util.calculateDistance(npc, member, true, false) < MIN_DISTANCE))
				{
					points += CLASS_POINTS.get(member.getClassId());
				}
			}
			
			if (points >= 3)
			{
				moraleBoostLv = 3;
			}
			else if (points >= 2)
			{
				moraleBoostLv = 2;
			}
			else if (points >= 1)
			{
				moraleBoostLv = 1;
			}
			
			for (L2PcInstance member : player.getParty().getMembers())
			{
				if (Util.calculateDistance(npc, member, true, false) < MIN_DISTANCE)
				{
					switch (moraleBoostLv)
					{
						case 1:
							MORALE_BOOST1.getSkill().getEffects(member, member);
							break;
						case 2:
							MORALE_BOOST2.getSkill().getEffects(member, member);
							break;
						case 3:
							MORALE_BOOST3.getSkill().getEffects(member, member);
							break;
					}
				}
			}
		}
	}
}
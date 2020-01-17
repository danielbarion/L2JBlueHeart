/*
 * Copyright (C) 2004-2017 L2J DataPack
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

import l2r.gameserver.enums.CtrlIntention;
import l2r.gameserver.model.actor.L2Attackable;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.holders.SkillHolder;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.util.Util;

import ai.npc.AbstractNpcAI;

/**
 * Blackdagger Wing AI.
 * @author Zoey76
 */
public class BlackdaggerWing extends AbstractNpcAI
{
	// NPCs
	private static final int BLACKDAGGER_WING = 25721;
	// Skills
	private static final SkillHolder POWER_STRIKE = new SkillHolder(6833, 1);
	private static final SkillHolder RANGE_MAGIC_ATTACK = new SkillHolder(6834, 1);
	// Variables
	private static final String MID_HP_FLAG = "MID_HP_FLAG";
	private static final String POWER_STRIKE_CAST_COUNT = "POWER_STRIKE_CAST_COUNT";
	// Timers
	private static final String DAMAGE_TIMER = "DAMAGE_TIMER";
	// Misc
	private static final int MAX_CHASE_DIST = 2500;
	private static final double MID_HP_PERCENTAGE = 0.50;
	
	public BlackdaggerWing()
	{
		super(BlackdaggerWing.class.getSimpleName(), "ai/zone/DragonValley");
		addAttackId(BLACKDAGGER_WING);
		addSeeCreatureId(BLACKDAGGER_WING);
		addSpellFinishedId(BLACKDAGGER_WING);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		if (Util.calculateDistance(npc, npc.getSpawn(), false, false) > MAX_CHASE_DIST)
		{
			npc.teleToLocation(npc.getSpawn().getX(), npc.getSpawn().getY(), npc.getSpawn().getZ());
		}
		
		if ((npc.getCurrentHp() < (npc.getMaxHp() * MID_HP_PERCENTAGE)) && !npc.getVariables().getBoolean(MID_HP_FLAG, false))
		{
			npc.getVariables().set(MID_HP_FLAG, true);
			startQuestTimer(DAMAGE_TIMER, 10000, npc, attacker);
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onSeeCreature(L2Npc npc, L2Character creature, boolean isSummon)
	{
		if (npc.getVariables().getBoolean(MID_HP_FLAG, false))
		{
			final L2Character mostHated = ((L2Attackable) npc).getMostHated();
			if ((mostHated != null) && mostHated.isPlayer() && (mostHated != creature))
			{
				if (getRandom(5) < 1)
				{
					addSkillCastDesire(npc, creature, RANGE_MAGIC_ATTACK, 9999900000000000L);
				}
			}
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	@Override
	public String onSpellFinished(L2Npc npc, L2PcInstance player, L2Skill skill)
	{
		if (skill.getId() == POWER_STRIKE.getSkillId())
		{
			npc.getVariables().set(POWER_STRIKE_CAST_COUNT, npc.getVariables().getInt(POWER_STRIKE_CAST_COUNT) + 1);
			if (npc.getVariables().getInt(POWER_STRIKE_CAST_COUNT) > 3)
			{
				addSkillCastDesire(npc, player, RANGE_MAGIC_ATTACK, 9999900000000000L);
				npc.getVariables().set(POWER_STRIKE_CAST_COUNT, 0);
			}
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (DAMAGE_TIMER.equals(event))
		{
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK);
			startQuestTimer(DAMAGE_TIMER, 30000, npc, player);
		}
		return super.onAdvEvent(event, npc, player);
	}
}
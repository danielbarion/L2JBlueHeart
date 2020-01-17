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
package ai.group_template;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2r.gameserver.enums.CtrlIntention;
import l2r.gameserver.model.actor.L2Attackable;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.NpcStringId;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.CreatureSay;

import ai.npc.AbstractNpcAI;

/**
 * Polymorphing on attack monsters AI.
 * @author Slyce
 */
public class PolymorphingOnAttack extends AbstractNpcAI
{
	private static final Map<Integer, List<Integer>> MOBSPAWNS = new HashMap<>();
	
	static
	{
		MOBSPAWNS.put(21258, Arrays.asList(21259, 100, 100, -1)); // Fallen Orc Shaman -> Sharp Talon Tiger (always polymorphs)
		MOBSPAWNS.put(21261, Arrays.asList(21262, 100, 20, 0)); // Ol Mahum Transcender 1st stage
		MOBSPAWNS.put(21262, Arrays.asList(21263, 100, 10, 1)); // Ol Mahum Transcender 2nd stage
		MOBSPAWNS.put(21263, Arrays.asList(21264, 100, 5, 2)); // Ol Mahum Transcender 3rd stage
		MOBSPAWNS.put(21265, Arrays.asList(21271, 100, 33, 0)); // Cave Ant Larva -> Cave Ant
		MOBSPAWNS.put(21266, Arrays.asList(21269, 100, 100, -1)); // Cave Ant Larva -> Cave Ant (always polymorphs)
		MOBSPAWNS.put(21267, Arrays.asList(21270, 100, 100, -1)); // Cave Ant Larva -> Cave Ant Soldier (always polymorphs)
		MOBSPAWNS.put(21271, Arrays.asList(21272, 66, 10, 1)); // Cave Ant -> Cave Ant Soldier
		MOBSPAWNS.put(21272, Arrays.asList(21273, 33, 5, 2)); // Cave Ant Soldier -> Cave Noble Ant
		MOBSPAWNS.put(21521, Arrays.asList(21522, 100, 30, -1)); // Claws of Splendor
		MOBSPAWNS.put(21527, Arrays.asList(21528, 100, 30, -1)); // Anger of Splendor
		MOBSPAWNS.put(21533, Arrays.asList(21534, 100, 30, -1)); // Alliance of Splendor
		MOBSPAWNS.put(21537, Arrays.asList(21538, 100, 30, -1)); // Fang of Splendor
	}
	
	protected static final NpcStringId[][] MOBTEXTS =
	{
		new NpcStringId[]
		{
			NpcStringId.ENOUGH_FOOLING_AROUND_GET_READY_TO_DIE,
			NpcStringId.YOU_IDIOT_IVE_JUST_BEEN_TOYING_WITH_YOU,
			NpcStringId.NOW_THE_FUN_STARTS
		},
		new NpcStringId[]
		{
			NpcStringId.I_MUST_ADMIT_NO_ONE_MAKES_MY_BLOOD_BOIL_QUITE_LIKE_YOU_DO,
			NpcStringId.NOW_THE_BATTLE_BEGINS,
			NpcStringId.WITNESS_MY_TRUE_POWER
		},
		new NpcStringId[]
		{
			NpcStringId.PREPARE_TO_DIE,
			NpcStringId.ILL_DOUBLE_MY_STRENGTH,
			NpcStringId.YOU_HAVE_MORE_SKILL_THAN_I_THOUGHT
		}
	};
	
	public PolymorphingOnAttack()
	{
		super(PolymorphingOnAttack.class.getSimpleName(), "ai/group_template");
		addAttackId(MOBSPAWNS.keySet());
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		if (npc.isVisible() && !npc.isDead())
		{
			final List<Integer> tmp = MOBSPAWNS.get(npc.getId());
			if (tmp != null)
			{
				if ((npc.getCurrentHp() <= ((npc.getMaxHp() * tmp.get(1)) / 100.0)) && (getRandom(100) < tmp.get(2)))
				{
					if (tmp.get(3) >= 0)
					{
						NpcStringId npcString = MOBTEXTS[tmp.get(3)][getRandom(MOBTEXTS[tmp.get(3)].length)];
						npc.broadcastPacket(new CreatureSay(npc.getObjectId(), Say2.NPC_ALL, npc.getName(), npcString));
						
					}
					npc.deleteMe();
					L2Attackable newNpc = (L2Attackable) addSpawn(tmp.get(0), npc.getX(), npc.getY(), npc.getZ() + 10, npc.getHeading(), false, 0, true);
					L2Character originalAttacker = isSummon ? attacker.getSummon() : attacker;
					newNpc.setRunning();
					newNpc.addDamageHate(originalAttacker, 0, 500);
					newNpc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, originalAttacker);
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
}

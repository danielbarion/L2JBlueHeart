/*
 * Copyright (C) 2004-2015 L2J DataPack
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
package hellbound.AI;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2MonsterInstance;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.network.NpcStringId;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.util.MinionList;

import ai.npc.AbstractNpcAI;

/**
 * Ranku's AI.
 * @author GKR
 */
public final class Ranku extends AbstractNpcAI
{
	// NPCs
	private static final int RANKU = 25542;
	private static final int MINION = 32305;
	private static final int MINION_2 = 25543;
	// Misc
	private static final Set<Integer> MY_TRACKING_SET = ConcurrentHashMap.newKeySet();
	
	public Ranku()
	{
		super(Ranku.class.getSimpleName(), "hellbound/AI");
		addAttackId(RANKU);
		addKillId(RANKU, MINION);
	}
	
	@Override
	public final String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("checkup") && (npc.getId() == RANKU) && !npc.isDead())
		{
			for (L2MonsterInstance minion : ((L2MonsterInstance) npc).getMinionList().getSpawnedMinions())
			{
				if ((minion != null) && !minion.isDead() && MY_TRACKING_SET.contains(minion.getObjectId()))
				{
					final L2PcInstance[] players = minion.getKnownList().getKnownPlayers().values().toArray(new L2PcInstance[minion.getKnownList().getKnownPlayers().size()]);
					final L2PcInstance killer = players[getRandom(players.length)];
					minion.reduceCurrentHp(minion.getMaxHp() / 100, killer, null);
				}
			}
			startQuestTimer("checkup", 1000, npc, null);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon, L2Skill skill)
	{
		if (npc.getId() == RANKU)
		{
			for (L2MonsterInstance minion : ((L2MonsterInstance) npc).getMinionList().getSpawnedMinions())
			{
				if ((minion != null) && !minion.isDead() && !MY_TRACKING_SET.contains(minion.getObjectId()))
				{
					broadcastNpcSay(minion, Say2.NPC_ALL, NpcStringId.DONT_KILL_ME_PLEASE_SOMETHINGS_STRANGLING_ME);
					startQuestTimer("checkup", 1000, npc, null);
					MY_TRACKING_SET.add(minion.getObjectId());
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon, skill);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		if (npc.getId() == MINION)
		{
			if (MY_TRACKING_SET.contains(npc.getObjectId()))
			{
				MY_TRACKING_SET.remove(npc.getObjectId());
			}
			
			final L2MonsterInstance master = ((L2MonsterInstance) npc).getLeader();
			if ((master != null) && !master.isDead())
			{
				L2MonsterInstance minion2 = MinionList.spawnMinion(master, MINION_2);
				minion2.teleToLocation(npc.getLocation());
			}
		}
		else if (npc.getId() == RANKU)
		{
			for (L2MonsterInstance minion : ((L2MonsterInstance) npc).getMinionList().getSpawnedMinions())
			{
				if (MY_TRACKING_SET.contains(minion.getObjectId()))
				{
					MY_TRACKING_SET.remove(minion.getObjectId());
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}
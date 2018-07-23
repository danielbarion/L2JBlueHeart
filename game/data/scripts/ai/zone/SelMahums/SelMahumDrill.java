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
package ai.zone.SelMahums;

import l2r.gameserver.data.SpawnTable;
import l2r.gameserver.enums.CtrlIntention;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.L2Spawn;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Attackable;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2MonsterInstance;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.NpcStringId;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.util.Util;

import ai.npc.AbstractNpcAI;

/**
 * Sel Mahum Training Ground AI for drill groups.
 * @author GKR, vGodFather
 */
public final class SelMahumDrill extends AbstractNpcAI
{
	private static final int[] MAHUM_CHIEFS =
	{
		22775, // Sel Mahum Drill Sergeant
		22776, // Sel Mahum Training Officer
		22778, // Sel Mahum Drill Sergeant
	};
	
	private static final int[] MAHUM_SOLDIERS =
	{
		22780, // Sel Mahum Recruit
		22782, // Sel Mahum Recruit
		22783, // Sel Mahum Soldier
		22784, // Sel Mahum Recruit
		22785, // Sel Mahum Soldier
	};
	
	private static final int[] CHIEF_SOCIAL_ACTIONS =
	{
		1,
		4,
		5,
		7
	};
	
	private static final Actions[] SOLDIER_SOCIAL_ACTIONS =
	{
		Actions.SCE_TRAINING_ACTION_A,
		Actions.SCE_TRAINING_ACTION_B,
		Actions.SCE_TRAINING_ACTION_C,
		Actions.SCE_TRAINING_ACTION_D
	};
	
	private static final NpcStringId[] CHIEF_FSTRINGS =
	{
		NpcStringId.HOW_DARE_YOU_ATTACK_MY_RECRUITS,
		NpcStringId.WHO_IS_DISRUPTING_THE_ORDER
	};
	
	private static final NpcStringId[] SOLDIER_FSTRINGS =
	{
		NpcStringId.THE_DRILLMASTER_IS_DEAD,
		NpcStringId.LINE_UP_THE_RANKS
	};
	
	// Chiefs event broadcast range
	private static final int TRAINING_RANGE = 1000;
	
	private static enum Actions
	{
		SCE_TRAINING_ACTION_A(4, -1, 2, 2333),
		SCE_TRAINING_ACTION_B(1, -1, 2, 4333),
		SCE_TRAINING_ACTION_C(6, 5, 4, 1000),
		SCE_TRAINING_ACTION_D(7, -1, 2, 1000);
		
		private final int _socialActionId;
		private final int _altSocialActionId;
		private final int _repeatCount;
		private final int _repeatInterval;
		
		private Actions(int socialActionId, int altSocialActionId, int repeatCount, int repeatInterval)
		{
			_socialActionId = socialActionId;
			_altSocialActionId = altSocialActionId;
			_repeatCount = repeatCount;
			_repeatInterval = repeatInterval;
		}
		
		protected int getSocialActionId()
		{
			return _socialActionId;
		}
		
		protected int getAltSocialActionId()
		{
			return _altSocialActionId;
		}
		
		protected int getRepeatCount()
		{
			return _repeatCount;
		}
		
		protected int getRepeatInterval()
		{
			return _repeatInterval;
		}
	}
	
	public SelMahumDrill()
	{
		super(SelMahumDrill.class.getSimpleName(), "ai/zone/SelMahumDrill");
		
		addAttackId(MAHUM_SOLDIERS);
		addKillId(MAHUM_CHIEFS);
		addEventReceivedId(MAHUM_CHIEFS);
		addEventReceivedId(MAHUM_SOLDIERS);
		addSpawnId(MAHUM_CHIEFS);
		addSpawnId(MAHUM_SOLDIERS);
		// Start global return home timer
		startQuestTimer("return_home", 120000, null, null, true);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "do_social_action":
			{
				if ((npc != null) && !npc.isDead())
				{
					if (Util.contains(MAHUM_CHIEFS, npc.getId()))
					{
						if ((npc.getVariables().getInt("BUSY_STATE") == 0) && (npc.getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE) && npc.staysInSpawnLoc())
						{
							final int idx = getRandom(6);
							if (idx <= (CHIEF_SOCIAL_ACTIONS.length - 1))
							{
								npc.broadcastSocialAction(CHIEF_SOCIAL_ACTIONS[idx]);
								npc.getVariables().set("SOCIAL_ACTION_NEXT_INDEX", idx); // Pass social action index to soldiers via script value
								npc.broadcastEvent("do_social_action", TRAINING_RANGE, null);
							}
						}
						
						startQuestTimer("do_social_action", 15000, npc, null);
					}
					else if (Util.contains(MAHUM_SOLDIERS, npc.getId()))
					{
						handleSocialAction(npc, SOLDIER_SOCIAL_ACTIONS[npc.getVariables().getInt("SOCIAL_ACTION_NEXT_INDEX")], false);
					}
				}
				break;
			}
			case "reset_busy_state":
			{
				if (npc != null)
				{
					npc.getVariables().remove("BUSY_STATE");
					npc.disableCoreAI(false);
				}
				break;
			}
			case "return_home":
			{
				for (int npcId : MAHUM_SOLDIERS)
				{
					for (L2Spawn npcSpawn : SpawnTable.getInstance().getSpawns(npcId))
					{
						final L2Npc soldier = npcSpawn.getLastSpawn();
						if ((soldier != null) && !soldier.isDead() && (npcSpawn.getName() != null) && npcSpawn.getName().startsWith("smtg_drill_group") && !soldier.staysInSpawnLoc() && ((soldier.getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE) || (soldier.getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE)))
						{
							soldier.setHeading(npcSpawn.getHeading());
							soldier.teleToLocation(npcSpawn.getLocation(), false);
						}
					}
				}
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		if (getRandom(10) < 1)
		{
			npc.broadcastEvent("ATTACKED", 1000, null);
		}
		
		switch (npc.getId())
		{
			case 22780:
			case 22782:
			case 22783:
			case 22784:
			case 22785:
			{
				for (L2Spawn spawn : SpawnTable.getInstance().getSpawns(npc.getId()))
				{
					if ((spawn != null) && spawn.getName().equals(npc.getSpawn().getName()))
					{
						for (L2Npc temp : spawn.getSpawnedNpcs())
						{
							if ((temp != null) && !temp.isDead())
							{
								temp.setRunning();
								((L2MonsterInstance) temp).addDamageHate(attacker, 0, 500);
								temp.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
							}
						}
					}
				}
				break;
			}
		}
		
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onEventReceived(String eventName, L2Npc sender, L2Npc receiver, L2Object reference)
	{
		if ((receiver != null) && !receiver.isDead() && receiver.isInMySpawnGroup(sender))
		{
			switch (eventName)
			{
				case "do_social_action":
				{
					if (Util.contains(MAHUM_SOLDIERS, receiver.getId()))
					{
						final int actionIndex = sender.getVariables().getInt("SOCIAL_ACTION_NEXT_INDEX");
						receiver.getVariables().set("SOCIAL_ACTION_NEXT_INDEX", actionIndex);
						handleSocialAction(receiver, SOLDIER_SOCIAL_ACTIONS[actionIndex], true);
					}
					break;
				}
				case "CHIEF_DIED":
				{
					if (Util.contains(MAHUM_SOLDIERS, receiver.getId()))
					{
						if (getRandom(4) < 1)
						{
							broadcastNpcSay(receiver, Say2.NPC_ALL, SOLDIER_FSTRINGS[getRandom(2)]);
						}
						if (receiver.canBeAttacked())
						{
							((L2Attackable) receiver).clearAggroList();
						}
						receiver.disableCoreAI(true);
						receiver.getVariables().set("BUSY_STATE", 1);
						receiver.setIsRunning(true);
						receiver.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location((receiver.getX() + getRandom(-800, 800)), (receiver.getY() + getRandom(-800, 800)), receiver.getZ(), receiver.getHeading()));
						startQuestTimer("reset_busy_state", 5000, receiver, null);
					}
					break;
				}
				case "ATTACKED":
				{
					if (Util.contains(MAHUM_CHIEFS, receiver.getId()))
					{
						broadcastNpcSay(receiver, Say2.NPC_ALL, CHIEF_FSTRINGS[getRandom(2)]);
					}
					break;
				}
			}
		}
		return super.onEventReceived(eventName, sender, receiver, reference);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		npc.broadcastEvent("CHIEF_DIED", TRAINING_RANGE, null);
		return null;
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		if (Util.contains(MAHUM_CHIEFS, npc.getId()))
		{
			startQuestTimer("do_social_action", 15000, npc, null);
		}
		
		else if ((getRandom(18) < 1) && Util.contains(MAHUM_SOLDIERS, npc.getId()))
		{
			npc.getVariables().set("SOCIAL_ACTION_ALT_BEHAVIOR", 1);
		}
		
		// Restore AI handling by core
		npc.disableCoreAI(false);
		return super.onSpawn(npc);
	}
	
	private void handleSocialAction(L2Npc npc, Actions action, boolean firstCall)
	{
		if ((npc.getVariables().getInt("BUSY_STATE") != 0) || (npc.getAI().getIntention() != CtrlIntention.AI_INTENTION_ACTIVE) || !npc.staysInSpawnLoc())
		{
			return;
		}
		
		final int socialActionId = (npc.getVariables().getInt("SOCIAL_ACTION_ALT_BEHAVIOR") == 0) ? action.getSocialActionId() : action.getAltSocialActionId();
		
		if (socialActionId < 0)
		{
			return;
		}
		
		if (firstCall)
		{
			npc.getVariables().set("SOCIAL_ACTION_REMAINED_COUNT", action.getRepeatCount());
		}
		
		npc.broadcastSocialAction(socialActionId);
		
		final int remainedCount = npc.getVariables().getInt("SOCIAL_ACTION_REMAINED_COUNT");
		if (remainedCount > 0)
		{
			npc.getVariables().set("SOCIAL_ACTION_REMAINED_COUNT", (remainedCount - 1));
			startQuestTimer("do_social_action", action.getRepeatInterval(), npc, null);
		}
	}
}
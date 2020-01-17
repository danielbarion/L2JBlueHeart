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
package instances.MithrilMine;

import l2r.gameserver.enums.CtrlIntention;
import l2r.gameserver.instancemanager.InstanceManager;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Attackable;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.holders.SkillHolder;
import l2r.gameserver.model.instancezone.InstanceWorld;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.network.NpcStringId;
import l2r.gameserver.network.clientpackets.Say2;

import instances.AbstractInstance;
import quests.Q10284_AcquisitionOfDivineSword.Q10284_AcquisitionOfDivineSword;

/**
 * Mithril Mine instance zone.
 * @author Adry_85
 */
public final class MithrilMine extends AbstractInstance
{
	protected class MMWorld extends InstanceWorld
	{
		protected int _count = 0;
	}
	
	// NPCs
	private static final int KEGOR = 18846;
	private static final int MITHRIL_MILLIPEDE = 22766;
	private static final int KRUN = 32653;
	private static final int TARUN = 32654;
	// Item
	private static final int COLD_RESISTANCE_POTION = 15514;
	// Skill
	private static SkillHolder BLESS_OF_SWORD = new SkillHolder(6286, 1);
	// Location
	private static final Location START_LOC = new Location(186852, -173492, -3763, 0, 0);
	private static final Location EXIT_LOC = new Location(178823, -184303, -347, 0, 0);
	private static final Location[] MOB_SPAWNS = new Location[]
	{
		new Location(185216, -184112, -3308, -15396),
		new Location(185456, -184240, -3308, -19668),
		new Location(185712, -184384, -3308, -26696),
		new Location(185920, -184544, -3308, -32544),
		new Location(185664, -184720, -3308, 27892)
	};
	// Misc
	private static final int TEMPLATE_ID = 138;
	
	public MithrilMine()
	{
		super(MithrilMine.class.getSimpleName(), "instances");
		addFirstTalkId(KEGOR);
		addKillId(KEGOR, MITHRIL_MILLIPEDE);
		addStartNpc(TARUN, KRUN);
		addTalkId(TARUN, KRUN, KEGOR);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final InstanceWorld world = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		
		switch (event)
		{
			case "BUFF":
			{
				if ((player != null) && npc.isInsideRadius(player, 1000, true, false) && npc.isScriptValue(1) && !player.isDead())
				{
					npc.setTarget(player);
					npc.doCast(BLESS_OF_SWORD.getSkill());
				}
				startQuestTimer("BUFF", 30000, npc, player);
				break;
			}
			case "TIMER":
			{
				if (world instanceof MMWorld)
				{
					for (Location loc : MOB_SPAWNS)
					{
						final L2Attackable spawnedMob = (L2Attackable) addSpawn(MITHRIL_MILLIPEDE, loc, false, 0, false, world.getInstanceId());
						spawnedMob.setScriptValue(1);
						spawnedMob.setIsRunning(true);
						spawnedMob.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, npc);
						spawnedMob.addDamageHate(npc, 0, 999999);
					}
				}
				break;
			}
			case "FINISH":
			{
				for (L2Character knownChar : npc.getKnownList().getKnownCharacters())
				{
					if ((knownChar != null) && (knownChar.getId() == KEGOR))
					{
						final L2Npc kegor = (L2Npc) knownChar;
						kegor.setScriptValue(2);
						kegor.setWalking();
						kegor.setTarget(player);
						kegor.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, player);
						broadcastNpcSay(kegor, Say2.NPC_ALL, NpcStringId.I_CAN_FINALLY_TAKE_A_BREATHER_BY_THE_WAY_WHO_ARE_YOU_HMM_I_THINK_I_KNOW_WHO_SENT_YOU);
					}
				}
				InstanceManager.getInstance().getInstance(world.getInstanceId()).setDuration(3000);
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = player.getQuestState(Q10284_AcquisitionOfDivineSword.class.getSimpleName());
		if ((qs != null))
		{
			if (qs.isMemoState(2))
			{
				return npc.isScriptValue(0) ? "18846.html" : "18846-01.html";
			}
			else if (qs.isMemoState(3))
			{
				final InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(player);
				if ((world != null) && world.isAllowed(player.getObjectId()))
				{
					world.removeAllowed(player.getObjectId());
				}
				player.setInstanceId(0);
				player.teleToLocation(EXIT_LOC, 0);
				giveAdena(player, 296425, true);
				addExpAndSp(player, 921805, 82230);
				qs.exitQuest(false, true);
				return "18846-03.html";
			}
		}
		return super.onFirstTalk(npc, player);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		final InstanceWorld world = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		final MMWorld _world = ((MMWorld) world);
		
		if (_world != null)
		{
			if (npc.getId() == KEGOR)
			{
				broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.HOW_COULD_I_FALL_IN_A_PLACE_LIKE_THIS);
				InstanceManager.getInstance().getInstance(world.getInstanceId()).setDuration(1000);
			}
			else
			{
				checkKill(player, npc, _world);
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		switch (npc.getId())
		{
			case TARUN:
			case KRUN:
			{
				final QuestState qs = talker.getQuestState(Q10284_AcquisitionOfDivineSword.class.getSimpleName());
				if ((qs != null) && qs.isMemoState(2))
				{
					if (!hasQuestItems(talker, COLD_RESISTANCE_POTION))
					{
						giveItems(talker, COLD_RESISTANCE_POTION, 1);
					}
					qs.setCond(4, true);
					enterInstance(talker, new MMWorld(), "MithrilMine.xml", TEMPLATE_ID);
				}
				break;
			}
			case KEGOR:
			{
				final QuestState qs = talker.getQuestState(Q10284_AcquisitionOfDivineSword.class.getSimpleName());
				if ((qs != null) && qs.isMemoState(2) && hasQuestItems(talker, COLD_RESISTANCE_POTION) && npc.isScriptValue(0))
				{
					takeItems(talker, COLD_RESISTANCE_POTION, -1);
					qs.setCond(5, true);
					npc.setScriptValue(1);
					startQuestTimer("TIMER", 3000, npc, talker);
					startQuestTimer("BUFF", 3500, npc, talker);
					return "18846-02.html";
				}
				break;
			}
		}
		return super.onTalk(npc, talker);
	}
	
	@Override
	public void onEnterInstance(L2PcInstance player, InstanceWorld world, boolean firstEntrance)
	{
		if (firstEntrance)
		{
			world.addAllowed(player.getObjectId());
		}
		teleportPlayer(player, START_LOC, world.getInstanceId(), false);
	}
	
	// vGodFather: sync kills
	private synchronized void checkKill(L2PcInstance player, L2Npc npc, MMWorld _world)
	{
		if (npc.isScriptValue(1))
		{
			_world._count++;
		}
		
		if (_world._count >= 5)
		{
			final QuestState qs = player.getQuestState(Q10284_AcquisitionOfDivineSword.class.getSimpleName());
			if ((qs != null) && qs.isMemoState(2))
			{
				cancelQuestTimer("BUFF", npc, player);
				qs.setMemoState(3);
				qs.setCond(6, true);
				startQuestTimer("FINISH", 3000, npc, player);
			}
		}
	}
}
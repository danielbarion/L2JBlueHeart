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
package quests.Q00625_TheFinestIngredientsPart2;

import l2r.gameserver.data.SpawnTable;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.holders.ItemHolder;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.network.NpcStringId;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.NpcSay;
import l2r.gameserver.util.Util;

/**
 * The Finest Ingredients Part - 2 (625)
 * @author Janiko
 */
public final class Q00625_TheFinestIngredientsPart2 extends Quest
{
	// NPCs
	private static final int JEREMY = 31521;
	private static final int YETIS_TABLE = 31542;
	// Monster
	private static final int ICICLE_EMPEROR_BUMBALUMP = 25296;
	// Required Item
	private static final ItemHolder SOY_SOURCE_JAR = new ItemHolder(7205, 1);
	// Quest Items
	private static final ItemHolder FOOD_FOR_BUMBALUMP = new ItemHolder(7209, 1);
	private static final ItemHolder SPECIAL_YETI_MEAT = new ItemHolder(7210, 1);
	// Rewards
	private static final ItemHolder GREATER_DYE_OF_STR_1 = new ItemHolder(4589, 5);
	private static final ItemHolder GREATER_DYE_OF_STR_2 = new ItemHolder(4590, 5);
	private static final ItemHolder GREATER_DYE_OF_CON_1 = new ItemHolder(4591, 5);
	private static final ItemHolder GREATER_DYE_OF_CON_2 = new ItemHolder(4592, 5);
	private static final ItemHolder GREATER_DYE_OF_DEX_1 = new ItemHolder(4593, 5);
	private static final ItemHolder GREATER_DYE_OF_DEX_2 = new ItemHolder(4594, 5);
	// Location
	private static final Location ICICLE_EMPEROR_BUMBALUMP_LOC = new Location(158240, -121536, -2222);
	// Misc
	private static final int MIN_LVL = 73;
	
	public Q00625_TheFinestIngredientsPart2()
	{
		super(625, Q00625_TheFinestIngredientsPart2.class.getSimpleName(), "The Finest Ingredients - Part 2");
		addStartNpc(JEREMY);
		addTalkId(JEREMY, YETIS_TABLE);
		addSpawnId(ICICLE_EMPEROR_BUMBALUMP);
		addKillId(ICICLE_EMPEROR_BUMBALUMP);
		registerQuestItems(FOOD_FOR_BUMBALUMP.getId(), SPECIAL_YETI_MEAT.getId());
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		String htmltext = null;
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "31521-04.htm":
			{
				if (qs.isCreated())
				{
					qs.startQuest();
					takeItem(player, SOY_SOURCE_JAR);
					giveItems(player, FOOD_FOR_BUMBALUMP);
					htmltext = event;
				}
				break;
			}
			case "31521-08.html":
			{
				if (qs.isCond(3))
				{
					if (hasItem(player, SPECIAL_YETI_MEAT))
					{
						int random = getRandom(1000);
						if (random < 167)
						{
							rewardItems(player, GREATER_DYE_OF_STR_1);
							
						}
						else if (random < 334)
						{
							rewardItems(player, GREATER_DYE_OF_STR_2);
						}
						else if (random < 501)
						{
							rewardItems(player, GREATER_DYE_OF_CON_1);
						}
						else if (random < 668)
						{
							rewardItems(player, GREATER_DYE_OF_CON_2);
						}
						else if (random < 835)
						{
							rewardItems(player, GREATER_DYE_OF_DEX_1);
						}
						else if (random < 1000)
						{
							rewardItems(player, GREATER_DYE_OF_DEX_2);
						}
						qs.exitQuest(false, true);
						htmltext = event;
					}
					else
					{
						htmltext = "31521-09.html";
					}
				}
				break;
			}
			case "31542-02.html":
			{
				if (qs.isCond(1))
				{
					if (hasItem(player, FOOD_FOR_BUMBALUMP))
					{
						if (!isBumbalumpSpawned())
						{
							qs.setCond(2, true);
							takeItem(player, FOOD_FOR_BUMBALUMP);
							L2Npc umpaloopa = addSpawn(ICICLE_EMPEROR_BUMBALUMP, ICICLE_EMPEROR_BUMBALUMP_LOC);
							umpaloopa.setSummoner(player);
							htmltext = event;
						}
						else
						{
							htmltext = "31542-03.html";
						}
					}
					else
					{
						htmltext = "31542-04.html";
					}
				}
				break;
			}
			case "NPC_TALK":
			{
				if (isBumbalumpSpawned())
				{
					npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.NPC_ALL, npc.getTemplate().getIdTemplate(), NpcStringId.OOOH));
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		final QuestState qs = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		switch (npc.getId())
		{
			case JEREMY:
			{
				if (qs.isCreated())
				{
					if (talker.getLevel() >= MIN_LVL)
					{
						htmltext = (hasItem(talker, SOY_SOURCE_JAR)) ? "31521-01.htm" : "31521-02.htm";
					}
					else
					{
						htmltext = "31521-03.htm";
					}
				}
				else if (qs.isStarted())
				{
					switch (qs.getCond())
					{
						case 1:
						{
							htmltext = "31521-05.html";
							break;
						}
						case 2:
						{
							htmltext = "31521-06.html";
							break;
						}
						case 3:
						{
							htmltext = "31521-07.html";
							break;
						}
					}
				}
				else if (qs.isCompleted())
				{
					htmltext = getAlreadyCompletedMsg(talker);
				}
				break;
			}
			case YETIS_TABLE:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						if (hasItem(talker, FOOD_FOR_BUMBALUMP))
						{
							htmltext = "31542-01.html";
						}
						break;
					}
					case 2:
					{
						if (!isBumbalumpSpawned())
						{
							L2Npc umpaloopa = addSpawn(ICICLE_EMPEROR_BUMBALUMP, ICICLE_EMPEROR_BUMBALUMP_LOC);
							umpaloopa.setSummoner(talker);
							htmltext = "31542-02.html";
						}
						else
						{
							htmltext = "31542-03.html";
						}
						break;
					}
					case 3:
					{
						htmltext = "31542-05.html";
						break;
					}
					
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		startQuestTimer("NPC_TALK", 1000 * 1200, npc, null);
		npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.NPC_ALL, npc.getTemplate().getIdTemplate(), NpcStringId.I_SMELL_SOMETHING_DELICIOUS));
		return super.onSpawn(npc);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(killer, 1, 2, npc);
		if ((qs != null) && Util.checkIfInRange(1500, npc, killer, true))
		{
			if (npc.getSummoner() == killer)
			{
				qs.setCond(3, true);
				giveItems(qs.getPlayer(), SPECIAL_YETI_MEAT);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	private static boolean isBumbalumpSpawned()
	{
		return SpawnTable.getInstance().findAny(ICICLE_EMPEROR_BUMBALUMP) != null;
	}
}

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
package quests.Q00152_ShardsOfGolem;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;

/**
 * Shards of Golem (152)
 * @author xban1x
 */
public class Q00152_ShardsOfGolem extends Quest
{
	// NPCs
	private static final int HARRYS = 30035;
	private static final int ALTRAN = 30283;
	// Monster
	private static final int STONE_GOLEM = 20016;
	// Items
	private static final int WOODEN_BREASTPLATE = 23;
	private static final int HARRYS_1ST_RECIEPT = 1008;
	private static final int HARRYS_2ND_RECIEPT = 1009;
	private static final int GOLEM_SHARD = 1010;
	private static final int TOOL_BOX = 1011;
	// Misc
	private static final int MIN_LVL = 10;
	
	public Q00152_ShardsOfGolem()
	{
		super(152, Q00152_ShardsOfGolem.class.getSimpleName(), "Shards of Golem");
		addStartNpc(HARRYS);
		addTalkId(HARRYS, ALTRAN);
		addKillId(STONE_GOLEM);
		registerQuestItems(HARRYS_1ST_RECIEPT, HARRYS_2ND_RECIEPT, GOLEM_SHARD, TOOL_BOX);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		String htmltext = null;
		if (st != null)
		{
			switch (event)
			{
				case "30035-03.htm":
				{
					st.startQuest();
					st.giveItems(HARRYS_1ST_RECIEPT, 1);
					htmltext = event;
					break;
				}
				case "30283-02.html":
				{
					if (st.isCond(1) && st.hasQuestItems(HARRYS_1ST_RECIEPT))
					{
						st.takeItems(HARRYS_1ST_RECIEPT, -1);
						st.giveItems(HARRYS_2ND_RECIEPT, 1);
						st.setCond(2, true);
						htmltext = event;
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState st = killer.getQuestState(getName());
		if ((st != null) && st.isCond(2) && (getRandom(100) < 30) && (st.getQuestItemsCount(GOLEM_SHARD) < 5))
		{
			st.giveItems(GOLEM_SHARD, 1);
			if (st.getQuestItemsCount(GOLEM_SHARD) >= 5)
			{
				st.setCond(3, true);
			}
			else
			{
				st.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (st != null)
		{
			switch (npc.getId())
			{
				case HARRYS:
				{
					switch (st.getState())
					{
						case State.CREATED:
						{
							htmltext = player.getLevel() >= MIN_LVL ? "30035-02.htm" : "30035-01.htm";
							break;
						}
						case State.STARTED:
						{
							switch (st.getCond())
							{
								case 1:
								{
									if (st.hasQuestItems(HARRYS_1ST_RECIEPT))
									{
										htmltext = "30035-04a.html";
									}
									break;
								}
								case 2:
								case 3:
								{
									if (st.hasQuestItems(HARRYS_2ND_RECIEPT))
									{
										htmltext = "30035-04.html";
									}
									break;
								}
								case 4:
								{
									if (st.hasQuestItems(HARRYS_2ND_RECIEPT, TOOL_BOX))
									{
										st.giveItems(WOODEN_BREASTPLATE, 1);
										st.addExpAndSp(5000, 0);
										st.exitQuest(false, true);
										htmltext = "30035-05.html";
									}
									break;
								}
							}
							break;
						}
						case State.COMPLETED:
						{
							htmltext = getAlreadyCompletedMsg(player);
							break;
						}
					}
					break;
				}
				case ALTRAN:
				{
					switch (st.getCond())
					{
						case 1:
						{
							if (st.hasQuestItems(HARRYS_1ST_RECIEPT))
							{
								htmltext = "30283-01.html";
							}
							break;
						}
						case 2:
						{
							if (st.hasQuestItems(HARRYS_2ND_RECIEPT) && (st.getQuestItemsCount(GOLEM_SHARD) < 5))
							{
								htmltext = "30283-03.html";
							}
							break;
						}
						case 3:
						{
							if (st.hasQuestItems(HARRYS_2ND_RECIEPT) && (st.getQuestItemsCount(GOLEM_SHARD) >= 5))
							{
								st.takeItems(GOLEM_SHARD, -1);
								st.giveItems(TOOL_BOX, 1);
								st.setCond(4, true);
								htmltext = "30283-04.html";
							}
							break;
						}
						case 4:
						{
							if (st.hasQuestItems(HARRYS_2ND_RECIEPT, TOOL_BOX))
							{
								htmltext = "30283-05.html";
							}
							break;
						}
					}
					break;
				}
			}
		}
		return htmltext;
	}
}

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
package quests.Q00261_CollectorsDream;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;
import l2r.gameserver.model.variables.PlayerVariables;
import l2r.gameserver.network.NpcStringId;
import l2r.gameserver.network.serverpackets.ExShowScreenMessage;
import l2r.gameserver.util.Util;

/**
 * Collector's Dream (261)
 * @author xban1x
 */
public final class Q00261_CollectorsDream extends Quest
{
	// Npc
	private static final int ALSHUPES = 30222;
	// Monsters
	private static final int[] MONSTERS = new int[]
	{
		20308, // Hook Spider
		20460, // Crimson Spider
		20466, // Pincer Spider
	};
	// Item
	private static final int SPIDER_LEG = 1087;
	// Misc
	private static final int MIN_LVL = 15;
	private static final int MAX_LEG_COUNT = 8;
	// Message
	private static final ExShowScreenMessage MESSAGE = new ExShowScreenMessage(NpcStringId.LAST_DUTY_COMPLETE_N_GO_FIND_THE_NEWBIE_GUIDE, 2, 5000);
	
	public Q00261_CollectorsDream()
	{
		super(261, Q00261_CollectorsDream.class.getSimpleName(), "Collector's Dream");
		addStartNpc(ALSHUPES);
		addTalkId(ALSHUPES);
		addKillId(MONSTERS);
		registerQuestItems(SPIDER_LEG);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if ((st != null) && event.equals("30222-03.htm"))
		{
			st.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState st = killer.getQuestState(getName());
		if ((st != null) && st.isCond(1) && Util.checkIfInRange(1500, npc, killer, true))
		{
			if (st.giveItemRandomly(SPIDER_LEG, 1, MAX_LEG_COUNT, 1, true))
			{
				st.setCond(2);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				htmltext = (player.getLevel() >= MIN_LVL) ? "30222-02.htm" : "30222-01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (st.getCond())
				{
					case 1:
					{
						htmltext = "30222-04.html";
						break;
					}
					case 2:
					{
						if (st.getQuestItemsCount(SPIDER_LEG) >= MAX_LEG_COUNT)
						{
							giveNewbieReward(player);
							st.giveAdena(1000, true);
							st.addExpAndSp(2000, 0);
							st.exitQuest(true, true);
							htmltext = "30222-05.html";
						}
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	public static void giveNewbieReward(L2PcInstance player)
	{
		final PlayerVariables vars = player.getVariables();
		if (vars.getString("GUIDE_MISSION", null) == null)
		{
			vars.set("GUIDE_MISSION", 100000);
			player.sendPacket(MESSAGE);
		}
		else if (((vars.getInt("GUIDE_MISSION") % 100000000) / 10000000) != 1)
		{
			vars.set("GUIDE_MISSION", vars.getInt("GUIDE_MISSION") + 10000000);
			player.sendPacket(MESSAGE);
		}
	}
}

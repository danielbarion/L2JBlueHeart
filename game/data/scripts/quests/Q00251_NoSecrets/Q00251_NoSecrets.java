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
package quests.Q00251_NoSecrets;

import l2r.Config;
import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;
import l2r.gameserver.util.Util;

/**
 * No Secrets (251)
 * @author Dumpster
 */
public class Q00251_NoSecrets extends Quest
{
	public static final int PINAPS = 30201;
	public static final int DIARY = 15508;
	public static final int TABLE = 15509;
	
	private static final int[] MOBS =
	{
		22783,
		22785,
		22780,
		22782,
		22784
	};
	
	private static final int[] MOBS2 =
	{
		22775,
		22776,
		22778
	};
	
	public Q00251_NoSecrets()
	{
		super(251, Q00251_NoSecrets.class.getSimpleName(), "No Secrets");
		addStartNpc(PINAPS);
		addTalkId(PINAPS);
		addKillId(MOBS);
		addKillId(MOBS2);
		registerQuestItems(DIARY, TABLE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return getNoQuestMsg(player);
		}
		
		if (event.equals("30201-03.htm"))
		{
			st.startQuest();
		}
		return event;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		final QuestState st = player.getQuestState(getName());
		if ((st != null) && st.isStarted() && st.isCond(1))
		{
			final int npcId = npc.getId();
			
			if (Util.contains(MOBS, npcId) && (getRandom(100) < (10 * Config.RATE_QUEST_DROP)) && (st.getQuestItemsCount(DIARY) < 10))
			{
				st.giveItems(DIARY, 1);
				if ((st.getQuestItemsCount(DIARY) >= 10) && (st.getQuestItemsCount(TABLE) >= 5))
				{
					st.setCond(2, true);
				}
				else
				{
					st.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
			}
			else if (Util.contains(MOBS2, npcId) && (getRandom(100) < (5 * Config.RATE_QUEST_DROP)) && (st.getQuestItemsCount(TABLE) < 5))
			{
				st.giveItems(TABLE, 1);
				if ((st.getQuestItemsCount(DIARY) >= 10) && (st.getQuestItemsCount(TABLE) >= 5))
				{
					st.setCond(2, true);
				}
				else
				{
					st.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case State.CREATED:
				htmltext = (player.getLevel() > 81) ? "30201-01.htm" : "30201-00.htm";
				break;
			case State.STARTED:
				if (st.isCond(1))
				{
					htmltext = "30201-05.htm";
				}
				else if ((st.isCond(2)) && (st.getQuestItemsCount(DIARY) >= 10) && (st.getQuestItemsCount(TABLE) >= 5))
				{
					htmltext = "30201-04.htm";
					st.giveAdena(313355, true);
					st.addExpAndSp(56787, 160578);
					st.exitQuest(false, true);
				}
				break;
			case State.COMPLETED:
				htmltext = "30201-06.htm";
				break;
		}
		return htmltext;
	}
}

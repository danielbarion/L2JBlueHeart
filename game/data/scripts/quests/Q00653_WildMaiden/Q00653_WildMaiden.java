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
package quests.Q00653_WildMaiden;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;

/**
 * Wild Maiden (653)
 * @author malyelfik
 */
public class Q00653_WildMaiden extends Quest
{
	// NPCs
	private static final int GALIBREDO = 30181;
	private static final int SUKI = 32013;
	// Item
	private static final int SOE = 736;
	// Misc
	private static final int MIN_LEVEL = 36;
	
	public Q00653_WildMaiden()
	{
		super(653, Q00653_WildMaiden.class.getSimpleName(), "Wild Maiden");
		addStartNpc(SUKI);
		addTalkId(GALIBREDO, SUKI);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return null;
		}
		
		String htmltext = null;
		if (event.equals("32013-03.html"))
		{
			htmltext = event;
		}
		else if (event.equals("32013-04.htm"))
		{
			if (!st.hasQuestItems(SOE))
			{
				return "32013-05.htm";
			}
			st.startQuest();
			st.takeItems(SOE, 1);
			npc.deleteMe();
			htmltext = (getRandom(2) == 0) ? event : "32013-04a.htm";
		}
		return htmltext;
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
		
		switch (npc.getId())
		{
			case SUKI:
				switch (st.getState())
				{
					case State.CREATED:
						htmltext = (player.getLevel() >= MIN_LEVEL) ? "32013-01.htm" : "32013-01a.htm";
						break;
					case State.STARTED:
						htmltext = "32013-02.htm";
						break;
				}
				break;
			case GALIBREDO:
				if (st.isStarted())
				{
					st.giveAdena(2553, true);
					st.exitQuest(true, true);
					htmltext = "30181-01.html";
				}
				break;
		}
		return htmltext;
	}
}
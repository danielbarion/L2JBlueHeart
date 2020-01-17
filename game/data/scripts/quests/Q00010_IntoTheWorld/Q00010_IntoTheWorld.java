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
package quests.Q00010_IntoTheWorld;

import l2r.gameserver.enums.Race;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;

/**
 * Into the World (10)
 * @author malyelfik
 */
public class Q00010_IntoTheWorld extends Quest
{
	// NPCs
	private static final int REED = 30520;
	private static final int BALANKI = 30533;
	private static final int GERALD = 30650;
	// Items
	private static final int VERY_EXPENSIVE_NECKLACE = 7574;
	private static final int SCROLL_OF_ESCAPE_GIRAN = 7559;
	private static final int MARK_OF_TRAVELER = 7570;
	// Misc
	private static final int MIN_LEVEL = 3;
	
	public Q00010_IntoTheWorld()
	{
		super(10, Q00010_IntoTheWorld.class.getSimpleName(), "Into the World");
		addStartNpc(BALANKI);
		addTalkId(BALANKI, REED, GERALD);
		registerQuestItems(VERY_EXPENSIVE_NECKLACE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return null;
		}
		
		String htmltext = event;
		switch (event)
		{
			case "30533-03.htm":
				st.startQuest();
				break;
			case "30533-06.html":
				st.giveItems(MARK_OF_TRAVELER, 1);
				st.giveItems(SCROLL_OF_ESCAPE_GIRAN, 1);
				st.exitQuest(false, true);
				break;
			case "30520-02.html":
				st.giveItems(VERY_EXPENSIVE_NECKLACE, 1);
				st.setCond(2, true);
				break;
			case "30520-05.html":
				st.setCond(4, true);
				break;
			case "30650-02.html":
				if (!st.hasQuestItems(VERY_EXPENSIVE_NECKLACE))
				{
					return "30650-03.html";
				}
				st.takeItems(VERY_EXPENSIVE_NECKLACE, -1);
				st.setCond(3, true);
				break;
			default:
				htmltext = null;
				break;
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
			case BALANKI:
				switch (st.getState())
				{
					case State.CREATED:
						htmltext = ((player.getLevel() >= MIN_LEVEL) && (player.getRace() == Race.DWARF)) ? "30533-01.htm" : "30533-02.html";
						break;
					case State.STARTED:
						if (st.isCond(1))
						{
							htmltext = "30533-04.html";
						}
						else if (st.isCond(4))
						{
							htmltext = "30533-05.html";
						}
						break;
					case State.COMPLETED:
						htmltext = getAlreadyCompletedMsg(player);
						break;
				}
				break;
			case REED:
				if (st.isStarted())
				{
					switch (st.getCond())
					{
						case 1:
							htmltext = "30520-01.html";
							break;
						case 2:
							htmltext = "30520-03.html";
							break;
						case 3:
							htmltext = "30520-04.html";
							break;
						case 4:
							htmltext = "30520-06.html";
							break;
					}
				}
				break;
			case GERALD:
				if (st.isStarted())
				{
					if (st.isCond(2))
					{
						htmltext = "30650-01.html";
					}
					else if (st.isCond(3))
					{
						htmltext = "30650-04.html";
					}
				}
				break;
		}
		return htmltext;
	}
}

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
package quests.Q00011_SecretMeetingWithKetraOrcs;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;

/**
 * Secret Meeting With Ketra Orcs (11)<br>
 * Original Jython script by Emperorc.
 * @author nonom
 */
public class Q00011_SecretMeetingWithKetraOrcs extends Quest
{
	// NPCs
	private static final int CADMON = 31296;
	private static final int LEON = 31256;
	private static final int WAHKAN = 31371;
	// Item
	private static final int BOX = 7231;
	
	public Q00011_SecretMeetingWithKetraOrcs()
	{
		super(11, Q00011_SecretMeetingWithKetraOrcs.class.getSimpleName(), "Secret Meeting With Ketra Orcs");
		addStartNpc(CADMON);
		addTalkId(CADMON, LEON, WAHKAN);
		registerQuestItems(BOX);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "31296-03.html":
				st.startQuest();
				break;
			case "31256-02.html":
				if (st.isCond(1))
				{
					st.setCond(2, true);
					st.giveItems(BOX, 1);
				}
				break;
			case "31371-02.html":
				if (st.isCond(2) && st.hasQuestItems(BOX))
				{
					st.addExpAndSp(233125, 18142);
					st.exitQuest(false, true);
				}
				else
				{
					htmltext = "31371-03.html";
				}
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		QuestState st = getQuestState(player, true);
		if (st == null)
		{
			return htmltext;
		}
		
		int npcId = npc.getId();
		switch (st.getState())
		{
			case State.COMPLETED:
				htmltext = getAlreadyCompletedMsg(player);
				break;
			case State.CREATED:
				if (npcId == CADMON)
				{
					htmltext = (player.getLevel() >= 74) ? "31296-01.htm" : "31296-02.html";
				}
				break;
			case State.STARTED:
				if ((npcId == CADMON) && st.isCond(1))
				{
					htmltext = "31296-04.html";
				}
				else if (npcId == LEON)
				{
					if (st.isCond(1))
					{
						htmltext = "31256-01.html";
						
					}
					else if (st.isCond(2))
					{
						htmltext = "31256-03.html";
					}
				}
				else if ((npcId == WAHKAN) && st.isCond(2))
				{
					htmltext = "31371-01.html";
				}
				break;
		}
		return htmltext;
	}
}

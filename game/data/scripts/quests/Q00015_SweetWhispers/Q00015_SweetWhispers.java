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
package quests.Q00015_SweetWhispers;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;

/**
 * Sweet Whisper (15)<br>
 * Original jython script by disKret.
 * @author nonom
 */
public class Q00015_SweetWhispers extends Quest
{
	// NPCs
	private static final int VLADIMIR = 31302;
	private static final int HIERARCH = 31517;
	private static final int M_NECROMANCER = 31518;
	
	public Q00015_SweetWhispers()
	{
		super(15, Q00015_SweetWhispers.class.getSimpleName(), "Sweet Whispers");
		addStartNpc(VLADIMIR);
		addTalkId(VLADIMIR, HIERARCH, M_NECROMANCER);
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
			case "31302-01.html":
				st.startQuest();
				break;
			case "31518-01.html":
				if (st.isCond(1))
				{
					st.setCond(2);
				}
				break;
			case "31517-01.html":
				if (st.isCond(2))
				{
					st.addExpAndSp(350531, 28204);
					st.exitQuest(false, true);
				}
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
		
		final int npcId = npc.getId();
		switch (st.getState())
		{
			case State.COMPLETED:
				htmltext = getAlreadyCompletedMsg(player);
				break;
			case State.CREATED:
				if (npcId == VLADIMIR)
				{
					htmltext = (player.getLevel() >= 60) ? "31302-00.htm" : "31302-00a.html";
				}
				break;
			case State.STARTED:
				switch (npcId)
				{
					case VLADIMIR:
						if (st.isCond(1))
						{
							htmltext = "31302-01a.html";
						}
						break;
					case M_NECROMANCER:
						switch (st.getCond())
						{
							case 1:
								htmltext = "31518-00.html";
								break;
							case 2:
								htmltext = "31518-01a.html";
								break;
						}
						break;
					case HIERARCH:
						if (st.isCond(2))
						{
							htmltext = "31517-00.html";
						}
						break;
				}
				break;
		}
		return htmltext;
	}
}

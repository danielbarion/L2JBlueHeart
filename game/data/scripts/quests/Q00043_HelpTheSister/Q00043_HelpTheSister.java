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
package quests.Q00043_HelpTheSister;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;

/**
 * Help The Sister! (43)<br>
 * Original Jython script by zerghase.
 * @author malyelfik
 */
public class Q00043_HelpTheSister extends Quest
{
	// NPCs
	private static final int COOPER = 30829;
	private static final int GALLADUCCI = 30097;
	// Monsters
	private static final int SPECTER = 20171;
	private static final int SORROW_MAIDEN = 20197;
	// Items
	private static final int CRAFTED_DAGGER = 220;
	private static final int MAP_PIECE = 7550;
	private static final int MAP = 7551;
	private static final int PET_TICKET = 7584;
	
	public Q00043_HelpTheSister()
	{
		super(43, Q00043_HelpTheSister.class.getSimpleName(), "Help The Sister!");
		addStartNpc(COOPER);
		addTalkId(COOPER, GALLADUCCI);
		addKillId(SORROW_MAIDEN, SPECTER);
		registerQuestItems(MAP, MAP_PIECE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return getNoQuestMsg(player);
		}
		
		String htmltext = event;
		switch (event)
		{
			case "30829-01.htm":
				st.startQuest();
				break;
			case "30829-03.html":
				if (st.hasQuestItems(CRAFTED_DAGGER))
				{
					st.takeItems(CRAFTED_DAGGER, 1);
					st.setCond(2, true);
				}
				else
				{
					htmltext = getNoQuestMsg(player);
				}
				break;
			case "30829-06.html":
				if (st.getQuestItemsCount(MAP_PIECE) == 30)
				{
					st.takeItems(MAP_PIECE, -1);
					st.giveItems(MAP, 1);
					st.setCond(4, true);
				}
				else
				{
					htmltext = "30829-06a.html";
				}
				break;
			case "30097-02.html":
				if (st.hasQuestItems(MAP))
				{
					st.takeItems(MAP, -1);
					st.setCond(5, true);
				}
				else
				{
					htmltext = "30097-02a.html";
				}
				break;
			case "30829-09.html":
				st.giveItems(PET_TICKET, 1);
				st.exitQuest(false, true);
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		QuestState st = player.getQuestState(getName());
		
		if ((st != null) && st.isCond(2))
		{
			st.giveItems(MAP_PIECE, 1);
			if (st.getQuestItemsCount(MAP_PIECE) == 30)
			{
				st.setCond(3, true);
			}
			else
			{
				st.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
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
		
		switch (npc.getId())
		{
			case COOPER:
				switch (st.getState())
				{
					case State.CREATED:
						htmltext = (player.getLevel() >= 26) ? "30829-00.htm" : "30829-00a.html";
						break;
					case State.STARTED:
						switch (st.getCond())
						{
							case 1:
								htmltext = (st.hasQuestItems(CRAFTED_DAGGER)) ? "30829-02.html" : "30829-02a.html";
								break;
							case 2:
								htmltext = "30829-04.html";
								break;
							case 3:
								htmltext = "30829-05.html";
								break;
							case 4:
								htmltext = "30829-07.html";
								break;
							case 5:
								htmltext = "30829-08.html";
								break;
						}
						break;
					case State.COMPLETED:
						htmltext = getAlreadyCompletedMsg(player);
						break;
				}
				break;
			case GALLADUCCI:
				if (st.isStarted())
				{
					switch (st.getCond())
					{
						case 4:
							htmltext = "30097-01.html";
							break;
						case 5:
							htmltext = "30097-03.html";
							break;
					}
				}
				break;
		}
		return htmltext;
	}
}

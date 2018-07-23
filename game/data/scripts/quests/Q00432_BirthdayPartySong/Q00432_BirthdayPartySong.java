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
package quests.Q00432_BirthdayPartySong;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;

/**
 * Birthday Party Song (432)<br>
 * Original Jython script by CubicVirtuoso.
 * @author malyelfik
 */
public class Q00432_BirthdayPartySong extends Quest
{
	// NPC
	private static final int OCTAVIA = 31043;
	// Monster
	private static final int GOLEM = 21103;
	// Item
	private static final int RED_CRYSTAL = 7541;
	// Reward
	private static final int ECHO_CRYSTAL = 7061;
	
	public Q00432_BirthdayPartySong()
	{
		super(432, Q00432_BirthdayPartySong.class.getSimpleName(), "Birthday Party Song");
		addStartNpc(OCTAVIA);
		addTalkId(OCTAVIA);
		addKillId(GOLEM);
		registerQuestItems(RED_CRYSTAL);
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
			case "31043-02.htm":
				st.startQuest();
				break;
			case "31043-05.html":
				if (st.getQuestItemsCount(RED_CRYSTAL) < 50)
				{
					return "31043-06.html";
				}
				
				st.giveItems(ECHO_CRYSTAL, 25);
				st.exitQuest(true, true);
				break;
			default:
				htmltext = null;
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		final QuestState st = player.getQuestState(getName());
		
		if ((st != null) && st.isCond(1) && getRandomBoolean())
		{
			st.giveItems(RED_CRYSTAL, 1);
			if (st.getQuestItemsCount(RED_CRYSTAL) == 50)
			{
				st.setCond(2, true);
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
		
		switch (st.getState())
		{
			case State.CREATED:
				htmltext = (player.getLevel() >= 31) ? "31043-01.htm" : "31043-00.htm";
				break;
			case State.STARTED:
				htmltext = (st.isCond(1)) ? "31043-03.html" : "31043-04.html";
				break;
		}
		return htmltext;
	}
}
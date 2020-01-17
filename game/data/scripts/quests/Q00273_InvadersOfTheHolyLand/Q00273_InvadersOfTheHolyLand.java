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
package quests.Q00273_InvadersOfTheHolyLand;

import java.util.HashMap;
import java.util.Map;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.enums.Race;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;

import quests.Q00281_HeadForTheHills.Q00281_HeadForTheHills;

/**
 * Invaders of the Holy Land (273)
 * @author xban1x
 */
public final class Q00273_InvadersOfTheHolyLand extends Quest
{
	// NPC
	private static final int VARKEES = 30566;
	// Items
	private static final int BLACK_SOULSTONE = 1475;
	private static final int RED_SOULSTONE = 1476;
	// Monsters
	private static final Map<Integer, Integer> MONSTERS = new HashMap<>();
	
	static
	{
		MONSTERS.put(20311, 90); // Rakeclaw Imp
		MONSTERS.put(20312, 87); // Rakeclaw Imp Hunter
		MONSTERS.put(20313, 77); // Rakeclaw Imp Chieftain
	}
	
	// Misc
	private static final int MIN_LVL = 6;
	
	public Q00273_InvadersOfTheHolyLand()
	{
		super(273, Q00273_InvadersOfTheHolyLand.class.getSimpleName(), "Invaders of the Holy Land");
		addStartNpc(VARKEES);
		addTalkId(VARKEES);
		addKillId(MONSTERS.keySet());
		registerQuestItems(BLACK_SOULSTONE, RED_SOULSTONE);
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
				case "30566-04.htm":
				{
					st.startQuest();
					htmltext = event;
					break;
				}
				case "30566-08.html":
				{
					st.exitQuest(true, true);
					htmltext = event;
					break;
				}
				case "30566-09.html":
				{
					htmltext = event;
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
		if (st != null)
		{
			if (getRandom(100) <= MONSTERS.get(npc.getId()))
			{
				st.giveItems(BLACK_SOULSTONE, 1);
			}
			else
			{
				st.giveItems(RED_SOULSTONE, 1);
			}
			st.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, true);
		String htmltext = null;
		if (st != null)
		{
			switch (st.getState())
			{
				case State.CREATED:
				{
					htmltext = (player.getRace() == Race.ORC) ? (player.getLevel() >= MIN_LVL) ? "30566-03.htm" : "30566-02.htm" : "30566-01.htm";
					break;
				}
				case State.STARTED:
				{
					if (hasAtLeastOneQuestItem(player, BLACK_SOULSTONE, RED_SOULSTONE))
					{
						final long black = st.getQuestItemsCount(BLACK_SOULSTONE);
						final long red = st.getQuestItemsCount(RED_SOULSTONE);
						st.giveAdena((red * 10) + (black * 3) + ((red > 0) ? (((red + black) >= 10) ? 1800 : 0) : ((black >= 10) ? 1500 : 0)), true);
						takeItems(player, -1, BLACK_SOULSTONE, RED_SOULSTONE);
						Q00281_HeadForTheHills.giveNewbieReward(player);
						htmltext = (red > 0) ? "30566-07.html" : "30566-06.html";
					}
					else
					{
						htmltext = "30566-05.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
}

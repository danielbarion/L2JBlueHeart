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
package quests.Q00377_ExplorationOfTheGiantsCavePart2;

import java.util.HashMap;
import java.util.Map;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;

/**
 * Exploration of the Giants' Cave Part 2 (377)<br>
 * Original Jython script by Gnacik.
 * @author nonom
 */
public class Q00377_ExplorationOfTheGiantsCavePart2 extends Quest
{
	// NPC
	private static final int SOBLING = 31147;
	// Items
	private static final int TITAN_ANCIENT_BOOK = 14847;
	private static final int BOOK1 = 14842;
	private static final int BOOK2 = 14843;
	private static final int BOOK3 = 14844;
	private static final int BOOK4 = 14845;
	private static final int BOOK5 = 14846;
	// Mobs
	private static final Map<Integer, Integer> MOBS1 = new HashMap<>();
	private static final Map<Integer, Double> MOBS2 = new HashMap<>();
	
	static
	{
		MOBS1.put(22660, 366); // lesser_giant_re
		MOBS1.put(22661, 424); // lesser_giant_soldier_re
		MOBS1.put(22662, 304); // lesser_giant_shooter_re
		MOBS1.put(22663, 304); // lesser_giant_scout_re
		MOBS1.put(22664, 354); // lesser_giant_mage_re
		MOBS1.put(22665, 324); // lesser_giant_elder_re
		MOBS2.put(22666, 0.276); // barif_re
		MOBS2.put(22667, 0.284); // barif_pet_re
		MOBS2.put(22668, 0.240); // gamlin_re
		MOBS2.put(22669, 0.240); // leogul_re
	}
	
	public Q00377_ExplorationOfTheGiantsCavePart2()
	{
		super(377, Q00377_ExplorationOfTheGiantsCavePart2.class.getSimpleName(), "Exploration of the Giants' Cave - Part 2");
		addStartNpc(SOBLING);
		addTalkId(SOBLING);
		addKillId(MOBS1.keySet());
		addKillId(MOBS2.keySet());
		registerQuestItems(TITAN_ANCIENT_BOOK);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		String htmltext = null;
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "31147-02.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "31147-04.html":
			case "31147-cont.html":
			{
				htmltext = event;
				break;
			}
			case "31147-quit.html":
			{
				qs.exitQuest(true, true);
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(player, -1, 3, npc);
		if (qs != null)
		{
			int npcId = npc.getId();
			if (MOBS1.containsKey(npcId))
			{
				final int itemCount = ((getRandom(1000) < MOBS1.get(npcId)) ? 3 : 2);
				giveItemRandomly(qs.getPlayer(), npc, TITAN_ANCIENT_BOOK, itemCount, 0, 1.0, true);
			}
			else
			{
				giveItemRandomly(qs.getPlayer(), npc, TITAN_ANCIENT_BOOK, 1, 0, MOBS2.get(npcId), true);
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs == null)
		{
			return htmltext;
		}
		
		if (qs.isCreated())
		{
			htmltext = ((player.getLevel() >= 79) ? "31147-01.htm" : "31147-00.html");
		}
		else if (qs.isStarted())
		{
			htmltext = (hasQuestItems(player, BOOK1, BOOK2, BOOK3, BOOK4, BOOK5) ? "31147-03.html" : "31147-02a.html");
		}
		return htmltext;
	}
}

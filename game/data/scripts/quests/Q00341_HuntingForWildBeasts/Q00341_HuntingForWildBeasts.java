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
package quests.Q00341_HuntingForWildBeasts;

import java.util.HashMap;
import java.util.Map;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;

/**
 * Hunting for Wild Beasts (341)
 * @author xban1x
 */
public class Q00341_HuntingForWildBeasts extends Quest
{
	// NPCs
	private static final int PANO = 30078;
	// Monsters
	private static final Map<Integer, Integer> MONSTERS = new HashMap<>();
	
	static
	{
		MONSTERS.put(20203, 99);
		MONSTERS.put(20310, 87);
		MONSTERS.put(20021, 83);
		MONSTERS.put(20335, 87);
	}
	
	// Items
	private static final int BEAR_SKIN = 4259;
	// Misc
	private static final int MIN_LVL = 20;
	private static final int ADENA_COUNT = 3710;
	private static final int REQUIRED_COUNT = 20;
	
	public Q00341_HuntingForWildBeasts()
	{
		super(341, Q00341_HuntingForWildBeasts.class.getSimpleName(), "Hunting for Wild Beasts");
		addStartNpc(PANO);
		addTalkId(PANO);
		addKillId(MONSTERS.keySet());
		registerQuestItems(BEAR_SKIN);
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
				case "30078-03.htm":
				{
					htmltext = event;
					break;
				}
				case "30078-04.htm":
				{
					st.startQuest();
					htmltext = event;
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (st != null)
		{
			switch (st.getState())
			{
				case State.CREATED:
				{
					htmltext = player.getLevel() >= MIN_LVL ? "30078-01.html" : "30078-02.htm";
					break;
				}
				case State.STARTED:
				{
					if (st.isCond(2) && (st.getQuestItemsCount(BEAR_SKIN) >= REQUIRED_COUNT))
					{
						st.giveAdena(ADENA_COUNT, true);
						st.exitQuest(true, true);
						htmltext = "30078-05.html";
					}
					else
					{
						htmltext = "30078-06.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final QuestState st = player.getQuestState(getName());
		if ((st != null) && st.isCond(1))
		{
			long skins = st.getQuestItemsCount(BEAR_SKIN);
			if (skins < REQUIRED_COUNT)
			{
				if (getRandom(100) < MONSTERS.get(npc.getId()))
				{
					st.giveItems(BEAR_SKIN, 1);
					if ((++skins) < REQUIRED_COUNT)
					{
						st.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					else
					{
						st.setCond(2, true);
					}
				}
			}
		}
		return super.onKill(npc, player, isPet);
	}
}

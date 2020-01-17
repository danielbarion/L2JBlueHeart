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
package quests.Q00903_TheCallOfAntharas;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.enums.QuestType;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;
import l2r.gameserver.util.Util;

/**
 * The Call of Antharas (903)
 * @author Zoey76
 */
public class Q00903_TheCallOfAntharas extends Quest
{
	// NPC
	private static final int THEODRIC = 30755;
	// Monsters
	private static final int BEHEMOTH_DRAGON = 29069;
	private static final int TARASK_DRAGON = 29190;
	// Items
	private static final int TARASK_DRAGONS_LEATHER_FRAGMENT = 21991;
	private static final int BEHEMOTH_DRAGON_LEATHER = 21992;
	private static final int SCROLL_ANTHARAS_CALL = 21897;
	private static final int PORTAL_STONE = 3865;
	// Misc
	private static final int MIN_LEVEL = 83;
	
	public Q00903_TheCallOfAntharas()
	{
		super(903, Q00903_TheCallOfAntharas.class.getSimpleName(), "The Call of Antharas");
		addStartNpc(THEODRIC);
		addTalkId(THEODRIC);
		addKillId(BEHEMOTH_DRAGON, TARASK_DRAGON);
		registerQuestItems(TARASK_DRAGONS_LEATHER_FRAGMENT, BEHEMOTH_DRAGON_LEATHER);
	}
	
	@Override
	public void actionForEachPlayer(L2PcInstance player, L2Npc npc, boolean isSummon)
	{
		final QuestState st = player.getQuestState(getName());
		if ((st != null) && Util.checkIfInRange(1500, npc, player, false))
		{
			switch (npc.getId())
			{
				case BEHEMOTH_DRAGON:
				{
					st.giveItems(BEHEMOTH_DRAGON_LEATHER, 1);
					st.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
					break;
				}
				case TARASK_DRAGON:
				{
					st.giveItems(TARASK_DRAGONS_LEATHER_FRAGMENT, 1);
					st.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
					break;
				}
			}
			
			if (st.hasQuestItems(BEHEMOTH_DRAGON_LEATHER) && st.hasQuestItems(TARASK_DRAGONS_LEATHER_FRAGMENT))
			{
				st.setCond(2, true);
			}
		}
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
		if ((player.getLevel() >= MIN_LEVEL) && st.hasQuestItems(PORTAL_STONE))
		{
			switch (event)
			{
				case "30755-05.htm":
				{
					htmltext = event;
					break;
				}
				case "30755-06.html":
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
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		executeForEachPlayer(killer, npc, isSummon, true, false);
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, true);
		if (st == null)
		{
			return getNoQuestMsg(player);
		}
		
		String htmltext = getNoQuestMsg(player);
		switch (st.getState())
		{
			case State.CREATED:
			{
				if (player.getLevel() < MIN_LEVEL)
				{
					htmltext = "30755-03.html";
				}
				else if (!st.hasQuestItems(PORTAL_STONE))
				{
					htmltext = "30755-04.html";
				}
				else
				{
					htmltext = "30755-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (st.getCond())
				{
					case 1:
					{
						htmltext = "30755-07.html";
						break;
					}
					case 2:
					{
						st.giveItems(SCROLL_ANTHARAS_CALL, 1);
						st.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
						st.exitQuest(QuestType.DAILY, true);
						htmltext = "30755-08.html";
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				if (!st.isNowAvailable())
				{
					htmltext = "30755-02.html";
				}
				else
				{
					st.setState(State.CREATED);
					if (player.getLevel() < MIN_LEVEL)
					{
						htmltext = "30755-03.html";
					}
					else if (!st.hasQuestItems(PORTAL_STONE))
					{
						htmltext = "30755-04.html";
					}
					else
					{
						htmltext = "30755-01.htm";
					}
				}
				break;
			}
		}
		return htmltext;
	}
}

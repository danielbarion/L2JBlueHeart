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
package quests.Q10504_JewelOfAntharas;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;
import l2r.gameserver.util.Util;

/**
 * Jewel of Antharas (10504)
 * @author Zoey76
 */
public final class Q10504_JewelOfAntharas extends Quest
{
	// NPC
	private static final int THEODRIC = 30755;
	// Monster
	private static final int ANTHARAS = 29068;
	// Items
	private static final int CLEAR_CRYSTAL = 21905;
	private static final int FILLED_CRYSTAL_ANTHARAS_ENERGY = 21907;
	private static final int JEWEL_OF_ANTHARAS = 21898;
	private static final int PORTAL_STONE = 3865;
	// Misc
	private static final int MIN_LEVEL = 84;
	
	public Q10504_JewelOfAntharas()
	{
		super(10504, Q10504_JewelOfAntharas.class.getSimpleName(), "Jewel of Antharas");
		addStartNpc(THEODRIC);
		addTalkId(THEODRIC);
		addKillId(ANTHARAS);
		registerQuestItems(CLEAR_CRYSTAL, FILLED_CRYSTAL_ANTHARAS_ENERGY);
	}
	
	@Override
	public void actionForEachPlayer(L2PcInstance player, L2Npc npc, boolean isSummon)
	{
		final QuestState st = player.getQuestState(getName());
		if ((st != null) && st.isCond(1) && Util.checkIfInRange(1500, npc, player, false))
		{
			st.takeItems(CLEAR_CRYSTAL, -1);
			st.giveItems(FILLED_CRYSTAL_ANTHARAS_ENERGY, 1);
			st.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
			st.setCond(2, true);
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
				case "30755-06.htm":
				{
					htmltext = event;
					break;
				}
				case "30755-07.html":
				{
					st.startQuest();
					st.giveItems(CLEAR_CRYSTAL, 1);
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
		executeForEachPlayer(killer, npc, isSummon, true, true);
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
					htmltext = "30755-02.html";
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
						if (st.hasQuestItems(CLEAR_CRYSTAL))
						{
							htmltext = "30755-08.html";
						}
						else
						{
							st.giveItems(CLEAR_CRYSTAL, 1);
							htmltext = "30755-09.html";
						}
						break;
					}
					case 2:
					{
						st.giveItems(JEWEL_OF_ANTHARAS, 1);
						st.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
						st.exitQuest(false, true);
						htmltext = "30755-10.html";
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = "30755-03.html";
				break;
			}
		}
		return htmltext;
	}
}

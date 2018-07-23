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
package quests.Q00138_TempleChampionPart2;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;

import quests.Q00137_TempleChampionPart1.Q00137_TempleChampionPart1;

/**
 * Temple Champion - 2 (138)
 * @author nonom
 */
public class Q00138_TempleChampionPart2 extends Quest
{
	// NPCs
	private static final int SYLVAIN = 30070;
	private static final int PUPINA = 30118;
	private static final int ANGUS = 30474;
	private static final int SLA = 30666;
	private static final int MOBS[] =
	{
		20176, // Wyrm
		20550, // Guardian Basilisk
		20551, // Road Scavenger
		20552, // Fettered Soul
	};
	// Items
	private static final int TEMPLE_MANIFESTO = 10341;
	private static final int RELICS_OF_THE_DARK_ELF_TRAINEE = 10342;
	private static final int ANGUS_RECOMMENDATION = 10343;
	private static final int PUPINAS_RECOMMENDATION = 10344;
	
	public Q00138_TempleChampionPart2()
	{
		super(138, Q00138_TempleChampionPart2.class.getSimpleName(), "Temple Champion - 2");
		addStartNpc(SYLVAIN);
		addTalkId(SYLVAIN, PUPINA, ANGUS, SLA);
		addKillId(MOBS);
		registerQuestItems(TEMPLE_MANIFESTO, RELICS_OF_THE_DARK_ELF_TRAINEE, ANGUS_RECOMMENDATION, PUPINAS_RECOMMENDATION);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return getNoQuestMsg(player);
		}
		switch (event)
		{
			case "30070-02.htm":
				st.startQuest();
				st.giveItems(TEMPLE_MANIFESTO, 1);
				break;
			case "30070-05.html":
				st.giveAdena(84593, true);
				if ((player.getLevel() < 42))
				{
					st.addExpAndSp(187062, 11307);
				}
				st.exitQuest(false, true);
				break;
			case "30070-03.html":
				st.setCond(2, true);
				break;
			case "30118-06.html":
				st.setCond(3, true);
				break;
			case "30118-09.html":
				st.setCond(6, true);
				st.giveItems(PUPINAS_RECOMMENDATION, 1);
				break;
			case "30474-02.html":
				st.setCond(4, true);
				break;
			case "30666-02.html":
				if (st.hasQuestItems(PUPINAS_RECOMMENDATION))
				{
					st.set("talk", "1");
					st.takeItems(PUPINAS_RECOMMENDATION, -1);
				}
				break;
			case "30666-03.html":
				if (st.hasQuestItems(TEMPLE_MANIFESTO))
				{
					st.set("talk", "2");
					st.takeItems(TEMPLE_MANIFESTO, -1);
				}
				break;
			case "30666-08.html":
				st.setCond(7, true);
				st.unset("talk");
				break;
		}
		return event;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		final QuestState st = player.getQuestState(getName());
		if ((st != null) && st.isStarted() && st.isCond(4) && (st.getQuestItemsCount(RELICS_OF_THE_DARK_ELF_TRAINEE) < 10))
		{
			st.giveItems(RELICS_OF_THE_DARK_ELF_TRAINEE, 1);
			if (st.getQuestItemsCount(RELICS_OF_THE_DARK_ELF_TRAINEE) >= 10)
			{
				st.playSound(QuestSound.ITEMSOUND_QUEST_MIDDLE);
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
			case SYLVAIN:
				switch (st.getCond())
				{
					case 1:
						htmltext = "30070-02.htm";
						break;
					case 2:
					case 3:
					case 4:
					case 5:
					case 6:
						htmltext = "30070-03.html";
						break;
					case 7:
						htmltext = "30070-04.html";
						break;
					default:
						if (st.isCompleted())
						{
							return getAlreadyCompletedMsg(player);
						}
						final QuestState qs = player.getQuestState(Q00137_TempleChampionPart1.class.getSimpleName());
						htmltext = (player.getLevel() >= 36) ? ((qs != null) && qs.isCompleted()) ? "30070-01.htm" : "30070-00a.htm" : "30070-00.htm";
						break;
				}
				break;
			case PUPINA:
				switch (st.getCond())
				{
					case 2:
						htmltext = "30118-01.html";
						break;
					case 3:
					case 4:
						htmltext = "30118-07.html";
						break;
					case 5:
						htmltext = "30118-08.html";
						if (st.hasQuestItems(ANGUS_RECOMMENDATION))
						{
							st.takeItems(ANGUS_RECOMMENDATION, -1);
						}
						break;
					case 6:
						htmltext = "30118-10.html";
						break;
				}
				break;
			case ANGUS:
				switch (st.getCond())
				{
					case 3:
						htmltext = "30474-01.html";
						break;
					case 4:
						if (st.getQuestItemsCount(RELICS_OF_THE_DARK_ELF_TRAINEE) >= 10)
						{
							st.takeItems(RELICS_OF_THE_DARK_ELF_TRAINEE, -1);
							st.giveItems(ANGUS_RECOMMENDATION, 1);
							st.setCond(5, true);
							htmltext = "30474-04.html";
						}
						else
						{
							htmltext = "30474-03.html";
						}
						break;
					case 5:
						htmltext = "30474-05.html";
						break;
				}
				break;
			case SLA:
				switch (st.getCond())
				{
					case 6:
						switch (st.getInt("talk"))
						{
							case 1:
								htmltext = "30666-02.html";
								break;
							case 2:
								htmltext = "30666-03.html";
								break;
							default:
								htmltext = "30666-01.html";
								break;
						}
						break;
					case 7:
						htmltext = "30666-09.html";
						break;
				}
				break;
		}
		return htmltext;
	}
}

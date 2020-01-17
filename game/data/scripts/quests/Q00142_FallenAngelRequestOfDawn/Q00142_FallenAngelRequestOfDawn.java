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
package quests.Q00142_FallenAngelRequestOfDawn;

import java.util.HashMap;
import java.util.Map;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;

/**
 * Fallen Angel - Request of Dawn (142)
 * @author Nono
 */
public class Q00142_FallenAngelRequestOfDawn extends Quest
{
	// NPCs
	private static final int RAYMOND = 30289;
	private static final int CASIAN = 30612;
	private static final int NATOOLS = 30894;
	private static final int ROCK = 32368;
	// Monsters
	private static final int FALLEN_ANGEL = 27338;
	private static final Map<Integer, Integer> MOBS = new HashMap<>();
	
	static
	{
		MOBS.put(20079, 338); // Ant
		MOBS.put(20080, 363); // Ant Captain
		MOBS.put(20081, 611); // Ant Overseer
		MOBS.put(20082, 371); // Ant Recruit
		MOBS.put(20084, 421); // Ant Patrol
		MOBS.put(20086, 371); // Ant Guard
		MOBS.put(20087, 900); // Ant Soldier
		MOBS.put(20088, 1000);// Ant Warrior Captain
		MOBS.put(20089, 431); // Noble Ant
		MOBS.put(20090, 917); // Noble Ant Leader
	}
	
	// Items
	private static final int CRYPTOGRAM_OF_THE_ANGEL_SEARCH = 10351;
	private static final int PROPHECY_FRAGMENT = 10352;
	private static final int FALLEN_ANGEL_BLOOD = 10353;
	// Misc
	private static final int MAX_REWARD_LEVEL = 43;
	private static final int FRAGMENT_COUNT = 30;
	private boolean isAngelSpawned = false;
	
	public Q00142_FallenAngelRequestOfDawn()
	{
		super(142, Q00142_FallenAngelRequestOfDawn.class.getSimpleName(), "Fallen Angel - Request of Dawn");
		addTalkId(NATOOLS, RAYMOND, CASIAN, ROCK);
		addKillId(MOBS.keySet());
		addKillId(FALLEN_ANGEL);
		registerQuestItems(CRYPTOGRAM_OF_THE_ANGEL_SEARCH, PROPHECY_FRAGMENT, FALLEN_ANGEL_BLOOD);
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
			case "30894-02.html":
			case "30289-03.html":
			case "30289-04.html":
			case "30612-03.html":
			case "30612-04.html":
			case "30612-06.html":
			case "30612-07.html":
				break;
			case "30894-01.html":
				st.startQuest();
				break;
			case "30894-03.html":
				st.giveItems(CRYPTOGRAM_OF_THE_ANGEL_SEARCH, 1);
				st.setCond(2, true);
				break;
			case "30289-05.html":
				st.unset("talk");
				st.setCond(3, true);
				break;
			case "30612-05.html":
				st.set("talk", "2");
				break;
			case "30612-08.html":
				st.unset("talk");
				st.setCond(4, true);
				break;
			case "32368-04.html":
				if (isAngelSpawned)
				{
					return "32368-03.html";
				}
				addSpawn(FALLEN_ANGEL, npc.getX() + 100, npc.getY() + 100, npc.getZ(), 0, false, 120000);
				isAngelSpawned = true;
				startQuestTimer("despawn", 120000, null, player);
				break;
			case "despawn":
				if (isAngelSpawned)
				{
					isAngelSpawned = false;
				}
			default:
				htmltext = null;
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		final QuestState st;
		if ((npc.getId() == FALLEN_ANGEL))
		{
			st = getQuestState(player, false);
			if (st.isCond(5))
			{
				st.giveItems(FALLEN_ANGEL_BLOOD, 1);
				st.setCond(6, true);
				isAngelSpawned = false;
			}
		}
		else
		{
			final L2PcInstance member = getRandomPartyMember(player, 4);
			if (member != null)
			{
				st = member.getQuestState(getName());
				if (getRandom(1000) < MOBS.get(npc.getId()))
				{
					st.giveItems(PROPHECY_FRAGMENT, 1);
					if (st.getQuestItemsCount(PROPHECY_FRAGMENT) >= FRAGMENT_COUNT)
					{
						st.takeItems(PROPHECY_FRAGMENT, -1);
						st.setCond(5, true);
					}
					else
					{
						st.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
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
			case NATOOLS:
				switch (st.getState())
				{
					case State.STARTED:
						switch (st.getCond())
						{
							case 1:
								htmltext = "30894-01.html";
								break;
							default:
								htmltext = "30894-04.html";
								break;
						}
						break;
					case State.COMPLETED:
						htmltext = getAlreadyCompletedMsg(player);
						break;
				}
				break;
			case RAYMOND:
				if (st.isStarted())
				{
					switch (st.getCond())
					{
						case 1:
							htmltext = "30289-01.html";
							break;
						case 2:
							if (st.isSet("talk"))
							{
								htmltext = "30289-03.html";
							}
							else
							{
								st.takeItems(CRYPTOGRAM_OF_THE_ANGEL_SEARCH, -1);
								st.set("talk", "1");
								htmltext = "30289-02.html";
							}
							break;
						case 3:
						case 4:
						case 5:
							htmltext = "30289-06.html";
							break;
						case 6:
							st.giveAdena(92676, true);
							if (player.getLevel() <= MAX_REWARD_LEVEL)
							{
								st.addExpAndSp(223036, 13091);
							}
							st.exitQuest(false, true);
							htmltext = "30289-07.html";
							break;
					}
				}
				break;
			case CASIAN:
				if (st.isStarted())
				{
					switch (st.getCond())
					{
						case 1:
						case 2:
							htmltext = "30612-01.html";
							break;
						case 3:
							if (st.getInt("talk") == 1)
							{
								htmltext = "30612-03.html";
							}
							else if (st.getInt("talk") == 2)
							{
								htmltext = "30612-06.html";
							}
							else
							{
								htmltext = "30612-02.html";
								st.set("talk", "1");
							}
							break;
						case 4:
						case 5:
						case 6:
							htmltext = "30612-09.html";
							break;
					}
				}
				break;
			case ROCK:
				if (st.isStarted())
				{
					switch (st.getCond())
					{
						case 5:
							htmltext = "32368-02.html";
							break;
						case 6:
							htmltext = "32368-05.html";
							break;
						default:
							htmltext = "32368-01.html";
							break;
					}
				}
				break;
		}
		return htmltext;
	}
}
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
package quests.Q00662_AGameOfCards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;
import l2r.gameserver.util.Util;
import l2r.util.Rnd;

/**
 * A Game of Cards (662)
 * @author Zoey76
 */
public final class Q00662_AGameOfCards extends Quest
{
	// NPC
	private static final int KLUMP = 30845;
	// Items
	private static final int RED_GEM = 8765;
	private static final int ZIGGOS_GEMSTONE = 8868;
	// Misc
	private static final int MIN_LEVEL = 61;
	private static final int REQUIRED_CHIP_COUNT = 50;
	// Monsters
	private static final Map<Integer, Integer> MONSTERS = new HashMap<>();
	
	static
	{
		MONSTERS.put(20672, 357); // Trives
		MONSTERS.put(20673, 357); // Falibati
		MONSTERS.put(20674, 583); // Doom Knight
		MONSTERS.put(20677, 435); // Tulben
		MONSTERS.put(20955, 358); // Ghostly Warrior
		MONSTERS.put(20958, 283); // Death Agent
		MONSTERS.put(20959, 455); // Dark Guard
		MONSTERS.put(20961, 365); // Bloody Knight
		MONSTERS.put(20962, 348); // Bloody Priest
		MONSTERS.put(20965, 457); // Chimera Piece
		MONSTERS.put(20966, 493); // Changed Creation
		MONSTERS.put(20968, 418); // Nonexistent Man
		MONSTERS.put(20972, 350); // Shaman of Ancient Times
		MONSTERS.put(20973, 453); // Forgotten Ancient People
		MONSTERS.put(21002, 315); // Doom Scout
		MONSTERS.put(21004, 320); // Dismal Pole
		MONSTERS.put(21006, 335); // Doom Servant
		MONSTERS.put(21008, 462); // Doom Archer
		MONSTERS.put(21010, 397); // Doom Warrior
		MONSTERS.put(21109, 507); // Hames Orc Scout
		MONSTERS.put(21112, 552); // Hames Orc Footman
		MONSTERS.put(21114, 587); // Cursed Guardian
		MONSTERS.put(21116, 812); // Hames Orc Overlord
		MONSTERS.put(21278, 483); // Antelope
		MONSTERS.put(21279, 483); // Antelope
		MONSTERS.put(21280, 483); // Antelope
		MONSTERS.put(21286, 515); // Buffalo
		MONSTERS.put(21287, 515); // Buffalo
		MONSTERS.put(21288, 515); // Buffalo
		MONSTERS.put(21508, 493); // Splinter Stakato
		MONSTERS.put(21510, 527); // Splinter Stakato Soldier
		MONSTERS.put(21513, 562); // Needle Stakato
		MONSTERS.put(21515, 598); // Needle Stakato Soldier
		MONSTERS.put(21520, 458); // Eye of Splendor
		MONSTERS.put(21526, 552); // Wisdom of Splendor
		MONSTERS.put(21530, 488); // Victory of Splendor
		MONSTERS.put(21535, 573); // Signet of Splendor
		MONSTERS.put(18001, 232); // Blood Queen
	}
	
	public Q00662_AGameOfCards()
	{
		super(662, Q00662_AGameOfCards.class.getSimpleName(), "A Game of Cards");
		addStartNpc(KLUMP);
		addTalkId(KLUMP);
		addKillId(MONSTERS.keySet());
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		String htmltext = null;
		if (st == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "30845-03.htm":
			{
				if (player.getLevel() >= MIN_LEVEL)
				{
					st.setState(State.STARTED);
					st.playSound(QuestSound.ITEMSOUND_QUEST_ACCEPT);
					htmltext = event;
				}
				break;
			}
			case "30845-06.html":
			case "30845-08.html":
			case "30845-09.html":
			case "30845-09a.html":
			case "30845-09b.html":
			case "30845-10.html":
			{
				htmltext = event;
				break;
			}
			case "30845-07.html":
			{
				st.exitQuest(true, true);
				htmltext = event;
				break;
			}
			case "return":
			{
				htmltext = st.getQuestItemsCount(RED_GEM) < REQUIRED_CHIP_COUNT ? "30845-04.html" : "30845-05.html";
				break;
			}
			case "30845-11.html":
			{
				if (st.getQuestItemsCount(RED_GEM) >= REQUIRED_CHIP_COUNT)
				{
					int i1 = 0;
					int i2 = 0;
					int i3 = 0;
					int i4 = 0;
					int i5 = 0;
					while ((i1 == i2) || (i1 == i3) || (i1 == i4) || (i1 == i5) || (i2 == i3) || (i2 == i4) || (i2 == i5) || (i3 == i4) || (i3 == i5) || (i4 == i5))
					{
						i1 = getRandom(70) + 1;
						i2 = getRandom(70) + 1;
						i3 = getRandom(70) + 1;
						i4 = getRandom(70) + 1;
						i5 = getRandom(70) + 1;
					}
					if (i1 >= 57)
					{
						i1 = i1 - 56;
					}
					else if (i1 >= 43)
					{
						i1 = i1 - 42;
					}
					else if (i1 >= 29)
					{
						i1 = i1 - 28;
					}
					else if (i1 >= 15)
					{
						i1 = i1 - 14;
					}
					if (i2 >= 57)
					{
						i2 = i2 - 56;
					}
					else if (i2 >= 43)
					{
						i2 = i2 - 42;
					}
					else if (i2 >= 29)
					{
						i2 = i2 - 28;
					}
					else if (i2 >= 15)
					{
						i2 = i2 - 14;
					}
					if (i3 >= 57)
					{
						i3 = i3 - 56;
					}
					else if (i3 >= 43)
					{
						i3 = i3 - 42;
					}
					else if (i3 >= 29)
					{
						i3 = i3 - 28;
					}
					else if (i3 >= 15)
					{
						i3 = i3 - 14;
					}
					if (i4 >= 57)
					{
						i4 = i4 - 56;
					}
					else if (i4 >= 43)
					{
						i4 = i4 - 42;
					}
					else if (i4 >= 29)
					{
						i4 = i4 - 28;
					}
					else if (i4 >= 15)
					{
						i4 = i4 - 14;
					}
					if (i5 >= 57)
					{
						i5 = i5 - 56;
					}
					else if (i5 >= 43)
					{
						i5 = i5 - 42;
					}
					else if (i5 >= 29)
					{
						i5 = i5 - 28;
					}
					else if (i5 >= 15)
					{
						i5 = i5 - 14;
					}
					
					st.setCond((i4 * 1000000) + (i3 * 10000) + (i2 * 100) + i1);
					st.set("ex", i5);
					st.takeItems(RED_GEM, REQUIRED_CHIP_COUNT);
					htmltext = event;
				}
				break;
			}
			case "turncard1":
			case "turncard2":
			case "turncard3":
			case "turncard4":
			case "turncard5":
			{
				final int cond = st.getCond();
				int i1 = st.getInt("ex");
				int i5 = i1 % 100;
				int i9 = i1 / 100;
				i1 = cond % 100;
				int i2 = (cond % 10000) / 100;
				int i3 = (cond % 1000000) / 10000;
				int i4 = (cond % 100000000) / 1000000;
				switch (event)
				{
					case "turncard1":
					{
						if ((i9 % 2) < 1)
						{
							i9 = i9 + 1;
						}
						if ((i9 % 32) < 31)
						{
							st.set("ex", (i9 * 100) + i5);
						}
						break;
					}
					case "turncard2":
					{
						if ((i9 % 4) < 2)
						{
							i9 = i9 + 2;
						}
						if ((i9 % 32) < 31)
						{
							st.set("ex", (i9 * 100) + i5);
						}
						break;
					}
					case "turncard3":
					{
						if ((i9 % 8) < 4)
						{
							i9 = i9 + 4;
						}
						if ((i9 % 32) < 31)
						{
							st.set("ex", (i9 * 100) + i5);
						}
						break;
					}
					case "turncard4":
					{
						if ((i9 % 16) < 8)
						{
							i9 = i9 + 8;
						}
						if ((i9 % 32) < 31)
						{
							st.set("ex", (i9 * 100) + i5);
						}
						break;
					}
					case "turncard5":
					{
						if ((i9 % 32) < 16)
						{
							i9 = i9 + 16;
						}
						if ((i9 % 32) < 31)
						{
							st.set("ex", (i9 * 100) + i5);
						}
						break;
					}
				}
				
				if ((i9 % 32) < 31)
				{
					htmltext = getHtm(player.getHtmlPrefix(), "30845-12.html");
				}
				else if ((i9 % 32) == 31)
				{
					int i6 = 0;
					int i8 = 0;
					if ((i1 >= 1) && (i1 <= 14) && (i2 >= 1) && (i2 <= 14) && (i3 >= 1) && (i3 <= 14) && (i4 >= 1) && (i4 <= 14) && (i5 >= 1) && (i5 <= 14))
					{
						if (i1 == i2)
						{
							i6 = i6 + 10;
							i8 = i8 + 8;
						}
						if (i1 == i3)
						{
							i6 = i6 + 10;
							i8 = i8 + 4;
						}
						if (i1 == i4)
						{
							i6 = i6 + 10;
							i8 = i8 + 2;
						}
						if (i1 == i5)
						{
							i6 = i6 + 10;
							i8 = i8 + 1;
						}
						if ((i6 % 100) < 10)
						{
							if ((i8 % 16) < 8)
							{
								if ((i8 % 8) < 4)
								{
									if (i2 == i3)
									{
										i6 = i6 + 10;
										i8 = i8 + 4;
									}
								}
								if ((i8 % 4) < 2)
								{
									if (i2 == i4)
									{
										i6 = i6 + 10;
										i8 = i8 + 2;
									}
								}
								if ((i8 % 2) < 1)
								{
									if (i2 == i5)
									{
										i6 = i6 + 10;
										i8 = i8 + 1;
									}
								}
							}
						}
						else if ((i6 % 10) == 0)
						{
							if ((i8 % 16) < 8)
							{
								if ((i8 % 8) < 4)
								{
									if (i2 == i3)
									{
										i6 = i6 + 1;
										i8 = i8 + 4;
									}
								}
								if ((i8 % 4) < 2)
								{
									if (i2 == i4)
									{
										i6 = i6 + 1;
										i8 = i8 + 2;
									}
								}
								if ((i8 % 2) < 1)
								{
									if (i2 == i5)
									{
										i6 = i6 + 1;
										i8 = i8 + 1;
									}
								}
							}
						}
						if ((i6 % 100) < 10)
						{
							if ((i8 % 8) < 4)
							{
								if ((i8 % 4) < 2)
								{
									if (i3 == i4)
									{
										i6 = i6 + 10;
										i8 = i8 + 2;
									}
								}
								if ((i8 % 2) < 1)
								{
									if (i3 == i5)
									{
										i6 = i6 + 10;
										i8 = i8 + 1;
									}
								}
							}
						}
						else if ((i6 % 10) == 0)
						{
							if ((i8 % 8) < 4)
							{
								if ((i8 % 4) < 2)
								{
									if (i3 == i4)
									{
										i6 = i6 + 1;
										i8 = i8 + 2;
									}
								}
								if ((i8 % 2) < 1)
								{
									if (i3 == i5)
									{
										i6 = i6 + 1;
										i8 = i8 + 1;
									}
								}
							}
						}
						if ((i6 % 100) < 10)
						{
							if ((i8 % 4) < 2)
							{
								if ((i8 % 2) < 1)
								{
									if (i4 == i5)
									{
										i6 = i6 + 10;
										i8 = i8 + 1;
									}
								}
							}
						}
						else if ((i6 % 10) == 0)
						{
							if ((i8 % 4) < 2)
							{
								if ((i8 % 2) < 1)
								{
									if (i4 == i5)
									{
										i6 = i6 + 1;
										i8 = i8 + 1;
									}
								}
							}
						}
					}
					
					if (i6 == 40)
					{
						st.giveItems(ZIGGOS_GEMSTONE, 43);
						st.giveItems(959, 3);
						st.giveItems(729, 1);
						st.set("ex", 0);
						st.setCond(0);
						htmltext = getHtm(player.getHtmlPrefix(), "30845-13.html");
					}
					else if (i6 == 30)
					{
						st.giveItems(959, 2);
						st.giveItems(951, 2);
						st.set("ex", 0);
						st.setCond(0);
						htmltext = getHtm(player.getHtmlPrefix(), "30845-14.html");
					}
					else if ((i6 == 21) || (i6 == 12))
					{
						st.giveItems(729, 1);
						st.giveItems(947, 2);
						st.giveItems(955, 1);
						st.set("ex", 0);
						st.setCond(0);
						htmltext = getHtm(player.getHtmlPrefix(), "30845-15.html");
					}
					else if (i6 == 20)
					{
						st.giveItems(951, 2);
						st.set("ex", 0);
						st.setCond(0);
						htmltext = getHtm(player.getHtmlPrefix(), "30845-16.html");
					}
					else if (i6 == 11)
					{
						st.giveItems(951, 1);
						st.set("ex", 0);
						st.setCond(0);
						htmltext = getHtm(player.getHtmlPrefix(), "30845-17.html");
					}
					else if (i6 == 10)
					{
						st.giveItems(956, 2);
						st.set("ex", 0);
						st.setCond(0);
						htmltext = getHtm(player.getHtmlPrefix(), "30845-18.html");
					}
					else if (i6 == 0)
					{
						st.set("ex", 0);
						st.setCond(0);
						htmltext = getHtm(player.getHtmlPrefix(), "30845-19.html");
					}
				}
				
				if (htmltext != null)
				{
					if ((i9 % 2) < 1)
					{
						htmltext = htmltext.replaceAll("FontColor1", "FFFF00");
						htmltext = htmltext.replaceAll("Cell1", "?");
					}
					else
					{
						htmltext = htmltext.replaceAll("FontColor1", "FF6F6F");
						htmltext = setHtml(htmltext, i1, "Cell1");
					}
					if ((i9 % 4) < 2)
					{
						htmltext = htmltext.replaceAll("FontColor2", "FFFF00");
						htmltext = htmltext.replaceAll("Cell2", "?");
					}
					else
					{
						htmltext = htmltext.replaceAll("FontColor2", "FF6F6F");
						htmltext = setHtml(htmltext, i2, "Cell2");
					}
					if ((i9 % 8) < 4)
					{
						htmltext = htmltext.replaceAll("FontColor3", "FFFF00");
						htmltext = htmltext.replaceAll("Cell3", "?");
					}
					else
					{
						htmltext = htmltext.replaceAll("FontColor3", "FF6F6F");
						htmltext = setHtml(htmltext, i3, "Cell3");
					}
					if ((i9 % 16) < 8)
					{
						htmltext = htmltext.replaceAll("FontColor4", "FFFF00");
						htmltext = htmltext.replaceAll("Cell4", "?");
					}
					else
					{
						htmltext = htmltext.replaceAll("FontColor4", "FF6F6F");
						htmltext = setHtml(htmltext, i4, "Cell4");
					}
					if ((i9 % 32) < 16)
					{
						htmltext = htmltext.replaceAll("FontColor5", "FFFF00");
						htmltext = htmltext.replaceAll("Cell5", "?");
					}
					else
					{
						htmltext = htmltext.replaceAll("FontColor5", "FF6F6F");
						htmltext = setHtml(htmltext, i5, "Cell5");
					}
				}
				break;
			}
			case "playagain":
			{
				htmltext = st.getQuestItemsCount(RED_GEM) < REQUIRED_CHIP_COUNT ? "30845-21.html" : "30845-20.html";
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				htmltext = player.getLevel() < MIN_LEVEL ? "30845-02.html" : "30845-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (st.getQuestItemsCount(RED_GEM) >= REQUIRED_CHIP_COUNT)
				{
					htmltext = "30845-05.html";
				}
				else if (st.getQuestItemsCount(RED_GEM) < REQUIRED_CHIP_COUNT)
				{
					htmltext = "30845-04.html";
				}
				else if (st.getInt("ex") != 0)
				{
					int i0 = st.getCond();
					int i1 = st.getInt("ex");
					int i5 = i1 % 100;
					int i9 = i1 / 100;
					i1 = i0 % 100;
					int i2 = (i0 % 10000) / 100;
					int i3 = (i0 % 1000000) / 10000;
					int i4 = (i0 % 100000000) / 1000000;
					htmltext = getHtm(player.getHtmlPrefix(), "30845-11a.html");
					
					if ((i9 % 2) < 1)
					{
						htmltext = htmltext.replaceAll("FontColor1", "FFFF00");
						htmltext = htmltext.replaceAll("Cell1", "?");
					}
					else
					{
						htmltext = htmltext.replaceAll("FontColor1", "FF6F6F");
						htmltext = setHtml(htmltext, i1, "Cell1");
					}
					
					if ((i9 % 4) < 2)
					{
						htmltext = htmltext.replaceAll("FontColor2", "FFFF00");
						htmltext = htmltext.replaceAll("Cell2", "?");
					}
					else
					{
						htmltext = htmltext.replaceAll("FontColor2", "FF6F6F");
						htmltext = setHtml(htmltext, i2, "Cell2");
					}
					
					if ((i9 % 8) < 4)
					{
						htmltext = htmltext.replaceAll("FontColor3", "FFFF00");
						htmltext = htmltext.replaceAll("Cell3", "?");
					}
					else
					{
						htmltext = htmltext.replaceAll("FontColor3", "FF6F6F");
						htmltext = setHtml(htmltext, i3, "Cell3");
					}
					if ((i9 % 16) < 8)
					{
						htmltext = htmltext.replaceAll("FontColor4", "FFFF00");
						htmltext = htmltext.replaceAll("Cell4", "?");
					}
					else
					{
						htmltext = htmltext.replaceAll("FontColor4", "FF6F6F");
						htmltext = setHtml(htmltext, i4, "Cell4");
					}
					if ((i9 % 32) < 16)
					{
						htmltext = htmltext.replaceAll("FontColor5", "FFFF00");
						htmltext = htmltext.replaceAll("Cell5", "?");
					}
					else
					{
						htmltext = htmltext.replaceAll("FontColor5", "FF6F6F");
						htmltext = setHtml(htmltext, i5, "Cell5");
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final List<L2PcInstance> players = new ArrayList<>();
		players.add(killer);
		players.add(killer);
		
		if (killer.isInParty())
		{
			for (L2PcInstance member : killer.getParty().getMembers())
			{
				if (member.getQuestState(getName()) != null)
				{
					players.add(member);
				}
			}
		}
		
		final L2PcInstance player = players.get(Rnd.get(players.size()));
		if ((player != null) && Util.checkIfInRange(1500, npc, player, false))
		{
			if (MONSTERS.get(npc.getId()) < getRandom(1000))
			{
				final QuestState st = player.getQuestState(getName());
				if (st != null)
				{
					st.giveItems(RED_GEM, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	private static String setHtml(String htmltext, int var, String regex)
	{
		String replacement = null;
		switch (var)
		{
			case 1:
			{
				replacement = "!";
				break;
			}
			case 2:
			{
				replacement = "=";
				break;
			}
			case 3:
			{
				replacement = "T";
				break;
			}
			case 4:
			{
				replacement = "V";
				break;
			}
			case 5:
			{
				replacement = "O";
				break;
			}
			case 6:
			{
				replacement = "P";
				break;
			}
			case 7:
			{
				replacement = "S";
				break;
			}
			case 8:
			{
				replacement = "E";
				break;
			}
			case 9:
			{
				replacement = "H";
				break;
			}
			case 10:
			{
				replacement = "A";
				break;
			}
			case 11:
			{
				replacement = "R";
				break;
			}
			case 12:
			{
				replacement = "D";
				break;
			}
			case 13:
			{
				replacement = "I";
				break;
			}
			case 14:
			{
				replacement = "N";
				break;
			}
			default:
			{
				replacement = "ERROR";
				break;
			}
		}
		return htmltext.replaceAll(regex, replacement);
	}
}

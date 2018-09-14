/*
 * Copyright (C) 2004-2017 L2J DataPack
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
package quests.Q00384_WarehouseKeepersPastime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.util.Util;

/**
 * Warehouse Keeper's Pastime (384)
 * @author Zealar
 */
public final class Q00384_WarehouseKeepersPastime extends Quest
{
	// NPCs
	private static final int CLIFF = 30182;
	private static final int WAREHOUSE_CHIEF_BAXT = 30685;

	// Monsters
	private static final int DUST_WIND = 20242;
	private static final int INNERSEN = 20950;
	private static final int CONGERER = 20774;
	private static final int CARINKAIN = 20635;
	private static final int CONNABI = 20947;
	private static final int HUNTER_GARGOYLE = 20241;
	private static final int NIGHTMARE_GUIDE = 20942;
	private static final int DRAGON_BEARER_WARRIOR = 20759;
	private static final int DRAGON_BEARER_CHIEF = 20758;
	private static final int DUST_WIND_HOLD = 20281;
	private static final int WEIRD_DRAKE = 20605;
	private static final int THUNDER_WYRM_HOLD = 20282;
	private static final int CADEINE = 20945;
	private static final int CONGERER_LORD = 20773;
	private static final int DRAGON_BEARER_ARCHER = 20760;
	private static final int NIGHTMARE_LORD = 20944;
	private static final int SANHIDRO = 20946;
	private static final int GIANT_MONSTEREYE = 20556;
	private static final int BARTAL = 20948;
	private static final int HUNTER_GARGOYLE_HOLD = 20286;
	private static final int ROT_GOLEM = 20559;
	private static final int GRAVE_GUARD = 20668;
	private static final int TULBEN = 20677;
	private static final int NIGHTMARE_KEEPER = 20943;
	private static final int LUMINUN = 20949;
	private static final int THUNDER_WYRM = 20243;
	// Items
	private static final int Q_IRONGATE_MEDAL = 5964;
	// Reward
	private static final int MOONSTONE_EARING = 852;
	private static final int DRAKE_LEATHER_BOOTS = 2437;
	private static final int DRAKE_LEATHER_MAIL = 401;
	private static final int MOLD_HARDENER = 4041;
	private static final int NECKLACE_OF_MERMAID = 917;
	private static final int SCRL_OF_ENCH_AM_C = 952;
	private static final int BLACKSMITH_S_FRAME = 1892;
	private static final int SCRL_OF_ENCH_WP_C = 951;
	private static final int ORIHARUKON = 1893;
	private static final int SAMURAI_LONGSWORD = 135;
	private static final int AQUASTONE_RING = 883;
	private static final int SYNTHESIS_COKES = 1888;
	private static final int MITHIRL_ALLOY = 1890;
	private static final int GREAT_HELMET = 500;
	private static final int VARNISH_OF_PURITY = 1887;
	private static final int BLESSED_GLOVES = 2463;
	private static final int CRAFTED_LEATHER = 1894;

	public Q00384_WarehouseKeepersPastime()
	{
		super(384, Q00384_WarehouseKeepersPastime.class.getSimpleName(), "Warehouse Keeper's Pastime");
		addStartNpc(CLIFF);
		addTalkId(CLIFF, WAREHOUSE_CHIEF_BAXT);
		addKillId(WAREHOUSE_CHIEF_BAXT, DUST_WIND, INNERSEN, CLIFF, CONGERER, CARINKAIN, CONNABI, HUNTER_GARGOYLE, NIGHTMARE_GUIDE, DRAGON_BEARER_WARRIOR, DRAGON_BEARER_CHIEF, DUST_WIND_HOLD, WEIRD_DRAKE, THUNDER_WYRM_HOLD, CADEINE, CONGERER_LORD, DRAGON_BEARER_ARCHER, NIGHTMARE_LORD, SANHIDRO, GIANT_MONSTEREYE, BARTAL, HUNTER_GARGOYLE_HOLD, ROT_GOLEM, GRAVE_GUARD, TULBEN, NIGHTMARE_KEEPER, LUMINUN, THUNDER_WYRM);
		registerQuestItems(Q_IRONGATE_MEDAL);
	}

	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if ((qs != null))
		{
			switch (npc.getId())
			{
				case CLIFF:
					if (qs.isCreated())
					{
						if (player.getLevel() >= 40)
						{
							return "30182-01.htm";
						}
						return "30182-04.html";
					}
					if (qs.getQuestItemsCount(Q_IRONGATE_MEDAL) < 10)
					{
						return "30182-06.html";
					}
					return "30182-07.html";
				case WAREHOUSE_CHIEF_BAXT:
					if (qs.hasMemoState())
					{
						if (qs.getQuestItemsCount(Q_IRONGATE_MEDAL) < 10)
						{
							return "30685-06.html";
						}
						return "30685-07.html";
					}
			}
		}
		return htmltext;
	}

	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null))
		{
			if (event.contains(".htm"))
			{
				return event;
			}


			switch (npc.getId())
			{
				case CLIFF:
				{
					if (event.equals("QUEST_ACCEPTED"))
					{
						qs.playSound(QuestSound.ITEMSOUND_QUEST_ACCEPT);
						qs.setMemoState(384);
						qs.startQuest();
						qs.showQuestionMark(384);
						qs.playSound(QuestSound.ITEMSOUND_QUEST_MIDDLE);
						return "30182-05.htm";
					}
					final int ask = Integer.parseInt(event);
					switch (ask)
					{
						case 3:
							return "30182-09a.htm";
						case 4:
							return "30182-02.htm";
						case 5:
							if (npc.getId() != CLIFF)
							{
								return getNoQuestMsg(player);
							}
							return "30182-03.htm";
						case 6:
						{
							qs.exitQuest(true);
							return "30182-08.html";
						}
						case 9:
							return "30182-09.html";
						case 7:
						{
							if (qs.getQuestItemsCount(Q_IRONGATE_MEDAL) >= 10)
							{
								qs.takeItems(Q_IRONGATE_MEDAL, 10);
								qs.setMemoState(10);
								createBingoBoard(qs);
								return "30182-10.html";
							}
							return "30182-11.html";
						}
						case 8:
						{
							if (qs.getQuestItemsCount(Q_IRONGATE_MEDAL) >= 100)
							{
								qs.takeItems(Q_IRONGATE_MEDAL, 100);
								qs.setMemoState(20);
								createBingoBoard(qs);
								return "30182-12.html";
							}
							return "30182-11.html";
						}
						case 10:
						case 11:
						case 12:
						case 13:
						case 14:
						case 15:
						case 16:
						case 17:
						case 18:
						{
							selectBingoNumber(qs, (ask - 10) + 1);
							return fillBoard(player, qs, getHtm(player.getHtmlPrefix(), "30182-13.html"));
						}
						case 19:
						case 20:
						case 21:
						case 22:
						case 23:
						case 24:
						case 25:
						case 26:
						case 27:
						{
							return takeHtml(player, qs, (ask - 18), CLIFF);
						}
						case 55:
						case 56:
						case 57:
						case 58:
						case 59:
						case 60:
						case 61:
						case 62:
						case 63:
						{
							return beforeReward(player, qs, (ask - 54), CLIFF);
						}
					}
					break;
				}
				case WAREHOUSE_CHIEF_BAXT:
					final int ask = Integer.parseInt(event);
					switch (ask)
					{
						case 3:
							return "30685-09a.html";
						case 6:
							qs.exitQuest(true);
							return "30685-08.html";
						case 9:
							return "30685-09.html";
						case 7:
							if (qs.getQuestItemsCount(Q_IRONGATE_MEDAL) >= 10)
							{
								qs.takeItems(Q_IRONGATE_MEDAL, 10);
								qs.setMemoState(10);
								createBingoBoard(qs);
								return "30685-10.html";
							}
							return "30685-11.html";
						case 8:
							if (qs.getQuestItemsCount(Q_IRONGATE_MEDAL) >= 100)
							{
								qs.takeItems(Q_IRONGATE_MEDAL, 100);
								qs.setMemoState(20);
								createBingoBoard(qs);
								return "30685-12.html";
							}
							return "30685-11.html";
						case 10:
						case 11:
						case 12:
						case 13:
						case 14:
						case 15:
						case 16:
						case 17:
						case 18:
						{
							selectBingoNumber(qs, (ask - 9));
							return fillBoard(player, qs, getHtm(player.getHtmlPrefix(), "30685-13.html"));
						}
						case 19:
						case 20:
						case 21:
						case 22:
						case 23:
						case 24:
						case 25:
						case 26:
						case 27:
						{
							return takeHtml(player, qs, (ask - 18), WAREHOUSE_CHIEF_BAXT);
						}
						case 55:
						case 56:
						case 57:
						case 58:
						case 59:
						case 60:
						case 61:
						case 62:
						case 63:
						{
							return beforeReward(player, qs, (ask - 54), WAREHOUSE_CHIEF_BAXT);
						}
					}
			}
		}
		return super.onAdvEvent(event, npc, player);
	}

	private String takeHtml(L2PcInstance player, QuestState qs, int num, int npcId)
	{
		String html = null;
		int i3;
		if (!isSelectedBingoNumber(qs, num))
		{
			selectBingoNumber(qs, num);
			i3 = getBingoSelectCount(qs);

			if (i3 == 2)
			{
				html = getHtm(player.getHtmlPrefix(), npcId + "-14.html");
			}
			else if (i3 == 3)
			{
				html = getHtm(player.getHtmlPrefix(), npcId + "-16.html");
			}
			else if (i3 == 4)
			{
				html = getHtm(player.getHtmlPrefix(), npcId + "-18.html");
			}
			else if (i3 == 5)
			{
				html = getHtm(player.getHtmlPrefix(), npcId + "-20.html");
			}
			return fillBoard(player, qs, html);
		}
		i3 = getBingoSelectCount(qs);
		if (i3 == 1)
		{
			html = getHtm(player.getHtmlPrefix(), npcId + "-15.html");
		}
		else if (i3 == 2)
		{
			html = getHtm(player.getHtmlPrefix(), npcId + "-17.html");
		}
		else if (i3 == 3)
		{
			html = getHtm(player.getHtmlPrefix(), npcId + "-19.html");
		}
		else if (i3 == 4)
		{
			html = getHtm(player.getHtmlPrefix(), npcId + "-21.html");
		}
		return fillBoard(player, qs, html);
	}

	private String fillBoard(L2PcInstance player, QuestState qs, String html)
	{
		for (int i0 = 0; i0 < 9; i0 = i0 + 1)
		{
			int i1 = getNumberFromBingoBoard(qs, i0);
			if (isSelectedBingoNumber(qs, i1))
			{
				html = html.replace("<?Cell" + (i0 + 1) + "?>", i1 + "");
			}
			else
			{
				html = html.replace("<?Cell" + (i0 + 1) + "?>", "?");
			}
		}
		return html;
	}

	private String colorBoard(L2PcInstance player, QuestState qs, String html)
	{
		for (int i0 = 0; i0 < 9; i0 = i0 + 1)
		{
			int i1 = getNumberFromBingoBoard(qs, i0);
			html = html.replace("<?FontColor" + (i0 + 1) + "?>", (isSelectedBingoNumber(qs, i1)) ? "ff0000" : "ffffff");
			html = html.replace("<?Cell" + (i0 + 1) + "?>", i1 + "");
		}
		return html;
	}

	private String beforeReward(L2PcInstance player, QuestState qs, int num, int npcId)
	{
		if (!isSelectedBingoNumber(qs, num))
		{
			selectBingoNumber(qs, num);
			int i3 = getMatchedBingoLineCount(qs);
			String html;
			if ((i3 == 3) && ((getBingoSelectCount(qs)) == 6))
			{
				reward(player, qs, i3);
				html = getHtm(player.getHtmlPrefix(), npcId + "-22.html");
			}
			else if ((i3 == 0) && (getBingoSelectCount(qs) == 6))
			{
				reward(player, qs, i3);
				html = getHtm(player.getHtmlPrefix(), npcId + "-24.html");
			}
			else
			{
				html = getHtm(player.getHtmlPrefix(), npcId + "-23.html");
			}
			return colorBoard(player, qs, html);
		}
		return fillBoard(player, qs, getHtm(player.getHtmlPrefix(), npcId + "-25.html"));
	}

	private void reward(L2PcInstance player, QuestState qs, int i3)
	{
		if (i3 == 3)
		{
			if (qs.getMemoState() == 10)
			{
				int random = getRandom(100);
				if (random < 16)
				{
					qs.giveItems(SYNTHESIS_COKES, 1);
				}
				else if (random < 32)
				{
					qs.giveItems(VARNISH_OF_PURITY, 1);
				}
				else if (random < 50)
				{
					qs.giveItems(CRAFTED_LEATHER, 1);
				}
				else if (random < 80)
				{
					qs.giveItems(SCRL_OF_ENCH_AM_C, 1);
				}
				else if (random < 89)
				{
					qs.giveItems(MITHIRL_ALLOY, 1);
				}
				else if (random < 98)
				{
					qs.giveItems(ORIHARUKON, 1);
				}
				else
				{
					qs.giveItems(SCRL_OF_ENCH_WP_C, 1);
				}

			}
			else if (qs.getMemoState() == 20)
			{
				int random = getRandom(100);

				if (random < 50)
				{
					qs.giveItems(AQUASTONE_RING, 1);
				}
				else if (random < 80)
				{
					qs.giveItems(SCRL_OF_ENCH_WP_C, 1);
				}
				else if (random < 98)
				{
					qs.giveItems(MOONSTONE_EARING, 1);
				}
				else
				{
					qs.giveItems(DRAKE_LEATHER_MAIL, 1);
				}
			}
		}
		else if (i3 == 0)
		{
			if (qs.getMemoState() == 10)
			{
				int random = getRandom(100);

				if (random < 50)
				{
					qs.giveItems(MOLD_HARDENER, 1);
				}
				else if (random < 80)
				{
					qs.giveItems(SCRL_OF_ENCH_AM_C, 1);
				}
				else if (random < 98)
				{
					qs.giveItems(BLACKSMITH_S_FRAME, 1);
				}
				else
				{
					qs.giveItems(NECKLACE_OF_MERMAID, 1);
				}
			}
			else if (qs.getMemoState() == 20)
			{
				int random = getRandom(100);

				if (random < 50)
				{
					qs.giveItems(SCRL_OF_ENCH_WP_C, 1);
				}
				else if (random < 80)
				{
					qs.giveItems(GREAT_HELMET, 1);
				}
				else if (random < 98)
				{
					qs.giveItems(DRAKE_LEATHER_BOOTS, 1);
					qs.giveItems(BLESSED_GLOVES, 1);
				}
				else
				{
					qs.giveItems(SAMURAI_LONGSWORD, 1);
				}
			}
		}
	}

	/**
	 * @param qs
	 */
	private void createBingoBoard(QuestState qs)
	{
		//@formatter:off
		Integer[] arr = {1,2,3,4,5,6,7,8,9};
		//@formatter:on
		Collections.shuffle(Arrays.asList(arr));
		qs.set("numbers", Arrays.asList(arr).toString().replaceAll("[^\\d ]", ""));
		qs.set("selected", "? ? ? ? ? ? ? ? ?");
	}

	/**
	 * @param qs
	 * @return
	 */
	private int getMatchedBingoLineCount(QuestState qs)
	{
		String[] q = qs.get("selected").split(" ");
		int found = 0;
		// Horizontal
		if ((q[0] + q[1] + q[2]).matches("\\d+"))
		{
			found++;
		}
		if ((q[3] + q[4] + q[5]).matches("\\d+"))
		{
			found++;
		}
		if ((q[6] + q[7] + q[8]).matches("\\d+"))
		{
			found++;
		}
		// Vertical
		if ((q[0] + q[3] + q[6]).matches("\\d+"))
		{
			found++;
		}
		if ((q[1] + q[4] + q[7]).matches("\\d+"))
		{
			found++;
		}
		if ((q[2] + q[5] + q[8]).matches("\\d+"))
		{
			found++;
		}
		// Diagonal
		if ((q[0] + q[4] + q[8]).matches("\\d+"))
		{
			found++;
		}
		if ((q[2] + q[4] + q[6]).matches("\\d+"))
		{
			found++;
		}
		return found;
	}

	/**
	 * @param qs
	 * @param num
	 */
	private void selectBingoNumber(QuestState qs, int num)
	{
		String[] numbers = qs.get("numbers").split(" ");
		int pos = 0;
		for (int i = 0; i < numbers.length; i++)
		{
			if (Integer.parseInt(numbers[i]) == num)
			{
				pos = i;
				break;
			}
		}
		String[] selected = qs.get("selected").split(" ");
		for (int i = 0; i < selected.length; i++)
		{
			if (i == pos)
			{
				selected[i] = num + "";
				continue;
			}
		}
		String result = selected[0];
		for (int i = 1; i < selected.length; i++)
		{
			result += " " + selected[i];
		}
		qs.set("selected", result);
	}

	/**
	 * @param qs
	 * @param num
	 * @return
	 */
	private boolean isSelectedBingoNumber(QuestState qs, int num)
	{
		return qs.get("selected").contains(num + "");
	}

	/**
	 * @param qs
	 * @param num
	 * @return
	 */
	private int getNumberFromBingoBoard(QuestState qs, int num)
	{
		return Integer.parseInt(qs.get("numbers").split(" ")[num]);
	}

	/**
	 * @param qs
	 * @return
	 */
	private int getBingoSelectCount(QuestState qs)
	{
		String current = qs.get("selected");
		return current.replaceAll("\\D", "").length();
	}

	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getRandomPlayerFromParty(killer, npc);
		if (qs != null)
		{
			switch (npc.getId())
			{
				case HUNTER_GARGOYLE:
					if (getRandom(1000) < 328)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
				case DUST_WIND:
					if (getRandom(100) < 35)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
				case THUNDER_WYRM:
					if (getRandom(1000) < 312)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
				case DUST_WIND_HOLD:
					if (getRandom(100) < 35)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
				case THUNDER_WYRM_HOLD:
					if (getRandom(1000) < 312)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
				case HUNTER_GARGOYLE_HOLD:
					if (getRandom(1000) < 328)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
				case GIANT_MONSTEREYE:
					if (getRandom(1000) < 176)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
				case ROT_GOLEM:
					if (getRandom(1000) < 226)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
				case WEIRD_DRAKE:
					if (getRandom(1000) < 218)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
				case CARINKAIN:
					if (getRandom(1000) < 216)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
				case GRAVE_GUARD:
					if (getRandom(1000) < 312)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
				case TULBEN:
					if (getRandom(1000) < 522)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
				case DRAGON_BEARER_CHIEF:
					if (getRandom(100) < 38)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
				case DRAGON_BEARER_WARRIOR:
					if (getRandom(100) < 39)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
				case DRAGON_BEARER_ARCHER:
					if (getRandom(1000) < 372)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
				case CONGERER_LORD:
					if (getRandom(1000) < 802)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
				case CONGERER:
					if (getRandom(1000) < 844)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
				case NIGHTMARE_GUIDE:
					if (getRandom(1000) < 118)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
				case NIGHTMARE_KEEPER:
					if (getRandom(100) < 17)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
				case NIGHTMARE_LORD:
					if (getRandom(1000) < 144)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
				case CADEINE:
					if (getRandom(1000) < 162)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
				case SANHIDRO:
					if (getRandom(100) < 25)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
				case CONNABI:
					if (getRandom(1000) < 272)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
				case BARTAL:
					if (getRandom(100) < 27)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
				case LUMINUN:
					if (getRandom(100) < 32)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
				case INNERSEN:
					if (getRandom(1000) < 346)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_IRONGATE_MEDAL, 1, 0, 1, true);
					}
					break;
			}

		}
		return super.onKill(npc, killer, isSummon);
	}

	private QuestState getRandomPlayerFromParty(L2PcInstance player, L2Npc npc)
	{
		QuestState qs = getQuestState(player, false);
		final List<QuestState> candidates = new ArrayList<>();

		if ((qs != null) && qs.isStarted())
		{
			candidates.add(qs);
			candidates.add(qs);
		}

		if (player.isInParty())
		{
			player.getParty().getMembers().stream().forEach(pm ->
			{

				QuestState qss = getQuestState(pm, false);
				if ((qss != null) && qss.isStarted() && Util.checkIfInRange(1500, npc, pm, true))
				{
					candidates.add(qss);
				}
			});
		}
		return candidates.isEmpty() ? null : candidates.get(getRandom(candidates.size()));
	}
}

/*
 * Copyright (C) 2004-2016 L2J DataPack
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
package quests.Q00386_StolenDignity;

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
 * Stolen Dignity (386)
 * @author Zealar
 */
public final class Q00386_StolenDignity extends Quest
{
	// NPCs
	private static final int WAREHOUSE_KEEPER_ROMP = 30843;
	
	// Monsters
	private static final int CRIMSON_DRAKE = 20670;
	private static final int KADIOS = 20671;
	private static final int HUNGRY_CORPSE = 20954;
	private static final int PAST_KNIGHT = 20956;
	private static final int BLADE_DEATH = 20958;
	private static final int DARK_GUARD = 20959;
	private static final int BLOODY_GHOST = 20960;
	private static final int BLOODY_LORD = 20963;
	private static final int PAST_CREATURE = 20967;
	private static final int GIANT_SHADOW = 20969;
	private static final int ANCIENTS_SOLDIER = 20970;
	private static final int ANCIENTS_WARRIOR = 20971;
	private static final int SPITE_SOUL_LEADER = 20974;
	private static final int SPITE_SOUL_WIZARD = 20975;
	private static final int WRECKED_ARCHER = 21001;
	private static final int FLOAT_OF_GRAVE = 21003;
	private static final int GRAVE_PREDATOR = 21005;
	private static final int FALLEN_ORC_SHAMAN = 21020;
	private static final int SHARP_TALON_TIGER = 21021;
	private static final int GLOW_WISP = 21108;
	private static final int MARSH_PREDATOR = 21110;
	private static final int HAMES_ORC_SNIPER = 21113;
	private static final int CURSED_GUARDIAN = 21114;
	private static final int HAMES_ORC_CHIEFTAIN = 21116;
	private static final int FALLEN_ORC_SHAMAN_TRANS = 21258;
	private static final int SHARP_TALON_TIGER_TRANS = 21259;
	// Items
	private static final int Q_STOLEN_INF_ORE = 6363;
	// Reward
	private static final int DRAGON_SLAYER_EDGE = 5529;
	private static final int METEOR_SHOWER_HEAD = 5532;
	private static final int ELYSIAN_HEAD = 5533;
	private static final int SOUL_BOW_SHAFT = 5534;
	private static final int CARNIUM_BOW_SHAFT = 5535;
	private static final int BLOODY_ORCHID_HEAD = 5536;
	private static final int SOUL_SEPARATOR_HEAD = 5537;
	private static final int DRAGON_GRINDER_EDGE = 5538;
	private static final int BLOOD_TORNADO_EDGE = 5539;
	private static final int TALLUM_GLAIVE_EDGE = 5541;
	private static final int HALBARD_EDGE = 5542;
	private static final int DASPARIONS_STAFF_HEAD = 5543;
	private static final int WORLDTREES_BRANCH_HEAD = 5544;
	private static final int DARK_LEGIONS_EDGE_EDGE = 5545;
	private static final int SWORD_OF_MIRACLE_EDGE = 5546;
	private static final int ELEMENTAL_SWORD_EDGE = 5547;
	private static final int TALLUM_BLADE_EDGE = 5548;
	private static final int INFERNO_MASTER_BLADE = 8331;
	private static final int EYE_OF_SOUL_PIECE = 8341;
	private static final int DRAGON_FLAME_HEAD_PIECE = 8342;
	private static final int DOOM_CRUSHER_HEAD = 8349;
	private static final int HAMMER_OF_DESTROYER_PIECE = 8346;
	private static final int SIRR_BLADE_BLADE = 8712;
	private static final int SWORD_OF_IPOS_BLADE = 8713;
	private static final int BARAKIEL_AXE_PIECE = 8714;
	private static final int TUNING_FORK_OF_BEHEMOTH_PIECE = 8715;
	private static final int NAGA_STORM_PIECE = 8716;
	private static final int TIPHON_SPEAR_EDGE = 8717;
	private static final int SHYID_BOW_SHAFT = 8718;
	private static final int SOBEKK_HURRICANE_EDGE = 8719;
	private static final int TONGUE_OF_THEMIS_PIECE = 8720;
	private static final int HAND_OF_CABRIO_HEAD = 8721;
	private static final int CRYSTAL_OF_DEAMON_PIECE = 8722;
	
	public Q00386_StolenDignity()
	{
		super(386, Q00386_StolenDignity.class.getSimpleName(), "Stolen Dignity");
		addStartNpc(WAREHOUSE_KEEPER_ROMP);
		addTalkId(WAREHOUSE_KEEPER_ROMP);
		addKillId(CRIMSON_DRAKE, KADIOS, HUNGRY_CORPSE, PAST_KNIGHT, BLADE_DEATH, DARK_GUARD, BLOODY_GHOST, BLOODY_LORD, PAST_CREATURE, GIANT_SHADOW, ANCIENTS_SOLDIER, ANCIENTS_WARRIOR, SPITE_SOUL_LEADER, SPITE_SOUL_WIZARD, WRECKED_ARCHER, FLOAT_OF_GRAVE, GRAVE_PREDATOR, FALLEN_ORC_SHAMAN, SHARP_TALON_TIGER, GLOW_WISP, MARSH_PREDATOR, HAMES_ORC_SNIPER, CURSED_GUARDIAN, HAMES_ORC_CHIEFTAIN, FALLEN_ORC_SHAMAN_TRANS, SHARP_TALON_TIGER_TRANS);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if ((qs != null) && (npc.getId() == WAREHOUSE_KEEPER_ROMP))
		{
			if (qs.isCreated())
			{
				if (player.getLevel() >= 58)
				{
					return "30843-01.htm";
				}
				return "30843-04.html";
			}
			if (qs.getQuestItemsCount(Q_STOLEN_INF_ORE) < 100)
			{
				return "30843-06.html";
			}
			return "30843-07.html";
		}
		return htmltext;
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && (npc.getId() == WAREHOUSE_KEEPER_ROMP))
		{
			if (event.equals("QUEST_ACCEPTED"))
			{
				qs.playSound(QuestSound.ITEMSOUND_QUEST_ACCEPT);
				qs.setMemoState(386);
				qs.startQuest();
				qs.showQuestionMark(386);
				qs.playSound(QuestSound.ITEMSOUND_QUEST_MIDDLE);
				return "30843-05.htm";
			}
			if (event.contains(".html"))
			{
				return event;
			}
			int ask = Integer.parseInt(event);
			switch (ask)
			{
				case 3:
					return "30843-09a.htm";
				case 5:
					return "30843-03.html";
				case 6:
				{
					qs.exitQuest(true);
					return "30843-08.html";
				}
				case 9:
					return "30843-09.html";
				case 8:
				{
					if (qs.getQuestItemsCount(Q_STOLEN_INF_ORE) >= 100)
					{
						qs.takeItems(Q_STOLEN_INF_ORE, 100);
						createBingoBoard(qs);
						return "30843-12.html";
					}
					return "30843-11.html";
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
					selectBingoNumber(qs, (ask - 9));
					return fillBoard(player, qs, getHtm(player.getHtmlPrefix(), "30843-13.html"));
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
					return takeHtml(player, qs, (ask - 18));
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
					return beforeReward(player, qs, (ask - 54));
				}
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	private String takeHtml(L2PcInstance player, QuestState qs, int num)
	{
		String html = null;
		int i3;
		if (!isSelectedBingoNumber(qs, num))
		{
			selectBingoNumber(qs, num);
			i3 = getBingoSelectCount(qs);
			
			if (i3 == 2)
			{
				html = getHtm(player.getHtmlPrefix(), "30843-14.html");
			}
			else if (i3 == 3)
			{
				html = getHtm(player.getHtmlPrefix(), "30843-16.html");
			}
			else if (i3 == 4)
			{
				html = getHtm(player.getHtmlPrefix(), "30843-18.html");
			}
			else if (i3 == 5)
			{
				html = getHtm(player.getHtmlPrefix(), "30843-20.html");
			}
			return fillBoard(player, qs, html);
		}
		i3 = getBingoSelectCount(qs);
		if (i3 == 1)
		{
			html = getHtm(player.getHtmlPrefix(), "30843-15.html");
		}
		else if (i3 == 2)
		{
			html = getHtm(player.getHtmlPrefix(), "30843-17.html");
		}
		else if (i3 == 3)
		{
			html = getHtm(player.getHtmlPrefix(), "30843-19.html");
		}
		else if (i3 == 4)
		{
			html = getHtm(player.getHtmlPrefix(), "30843-21.html");
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
	
	private String beforeReward(L2PcInstance player, QuestState qs, int num)
	{
		if (!isSelectedBingoNumber(qs, num))
		{
			selectBingoNumber(qs, num);
			int i3 = getMatchedBingoLineCount(qs);
			String html;
			if ((i3 == 3) && ((getBingoSelectCount(qs)) == 6))
			{
				reward(player, qs, 4);
				html = getHtm(player.getHtmlPrefix(), "30843-22.html");
			}
			else if ((i3 == 0) && (getBingoSelectCount(qs) == 6))
			{
				reward(player, qs, 10);
				html = getHtm(player.getHtmlPrefix(), "30843-24.html");
			}
			else
			{
				html = getHtm(player.getHtmlPrefix(), "30843-23.html");
			}
			return colorBoard(player, qs, html);
		}
		return fillBoard(player, qs, getHtm(player.getHtmlPrefix(), "30843-25.html"));
	}
	
	private void reward(L2PcInstance player, QuestState qs, int count)
	{
		switch (getRandom(33))
		{
			case 0:
			{
				qs.giveItems(DRAGON_SLAYER_EDGE, count);
				break;
			}
			case 1:
			{
				qs.giveItems(METEOR_SHOWER_HEAD, count);
				break;
			}
			case 2:
			{
				qs.giveItems(ELYSIAN_HEAD, count);
				break;
			}
			case 3:
			{
				qs.giveItems(SOUL_BOW_SHAFT, count);
				break;
			}
			case 4:
			{
				qs.giveItems(CARNIUM_BOW_SHAFT, count);
				break;
			}
			case 5:
			{
				qs.giveItems(BLOODY_ORCHID_HEAD, count);
				break;
			}
			case 6:
			{
				qs.giveItems(SOUL_SEPARATOR_HEAD, count);
				break;
			}
			case 7:
			{
				qs.giveItems(DRAGON_GRINDER_EDGE, count);
				break;
			}
			case 8:
			{
				qs.giveItems(BLOOD_TORNADO_EDGE, count);
				break;
			}
			case 9:
			{
				qs.giveItems(TALLUM_GLAIVE_EDGE, count);
				break;
			}
			case 10:
			{
				qs.giveItems(HALBARD_EDGE, count);
				break;
			}
			case 11:
			{
				qs.giveItems(DASPARIONS_STAFF_HEAD, count);
				break;
			}
			case 12:
			{
				qs.giveItems(WORLDTREES_BRANCH_HEAD, count);
				break;
			}
			case 13:
			{
				qs.giveItems(DARK_LEGIONS_EDGE_EDGE, count);
				break;
			}
			case 14:
			{
				qs.giveItems(SWORD_OF_MIRACLE_EDGE, count);
				break;
			}
			case 15:
			{
				qs.giveItems(ELEMENTAL_SWORD_EDGE, count);
				break;
			}
			case 16:
			{
				qs.giveItems(TALLUM_BLADE_EDGE, count);
				break;
			}
			case 17:
			{
				qs.giveItems(INFERNO_MASTER_BLADE, count);
				break;
			}
			case 18:
			{
				qs.giveItems(EYE_OF_SOUL_PIECE, count);
				break;
			}
			case 19:
			{
				qs.giveItems(DRAGON_FLAME_HEAD_PIECE, count);
				break;
			}
			case 20:
			{
				qs.giveItems(DOOM_CRUSHER_HEAD, count);
				break;
			}
			case 21:
			{
				qs.giveItems(HAMMER_OF_DESTROYER_PIECE, count);
				break;
			}
			case 22:
			{
				qs.giveItems(SIRR_BLADE_BLADE, count);
				break;
			}
			case 23:
			{
				qs.giveItems(SWORD_OF_IPOS_BLADE, count);
				break;
			}
			case 24:
			{
				qs.giveItems(BARAKIEL_AXE_PIECE, count);
				break;
			}
			case 25:
			{
				qs.giveItems(TUNING_FORK_OF_BEHEMOTH_PIECE, count);
				break;
			}
			case 26:
			{
				qs.giveItems(NAGA_STORM_PIECE, count);
				break;
			}
			case 27:
			{
				qs.giveItems(TIPHON_SPEAR_EDGE, count);
				break;
			}
			case 28:
			{
				qs.giveItems(SHYID_BOW_SHAFT, count);
				break;
			}
			case 29:
			{
				qs.giveItems(SOBEKK_HURRICANE_EDGE, count);
				break;
			}
			case 30:
			{
				qs.giveItems(TONGUE_OF_THEMIS_PIECE, count);
				break;
			}
			case 31:
			{
				qs.giveItems(HAND_OF_CABRIO_HEAD, count);
				break;
			}
			case 32:
			{
				qs.giveItems(CRYSTAL_OF_DEAMON_PIECE, count);
				break;
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
				case CRIMSON_DRAKE:
				{
					if (getRandom(1000) < 20.200001)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case KADIOS:
				{
					if (getRandom(1000) < 211)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case HUNGRY_CORPSE:
				{
					if (getRandom(1000) < 184)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case PAST_KNIGHT:
				{
					if (getRandom(1000) < 216)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case BLADE_DEATH:
				{
					if (getRandom(100) < 17)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case DARK_GUARD:
				{
					if (getRandom(1000) < 273)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case BLOODY_GHOST:
				{
					if (getRandom(1000) < 149)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case BLOODY_LORD:
				{
					if (getRandom(1000) < 199)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case PAST_CREATURE:
				{
					if (getRandom(1000) < 257)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case GIANT_SHADOW:
				{
					if (getRandom(1000) < 205)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case ANCIENTS_SOLDIER:
				{
					if (getRandom(1000) < 208)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case ANCIENTS_WARRIOR:
				{
					if (getRandom(1000) < 299)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case SPITE_SOUL_LEADER:
				{
					if (getRandom(100) < 44)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case SPITE_SOUL_WIZARD:
				{
					if (getRandom(100) < 39)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case WRECKED_ARCHER:
				{
					if (getRandom(1000) < 214)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case FLOAT_OF_GRAVE:
				{
					if (getRandom(1000) < 173)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case GRAVE_PREDATOR:
				{
					if (getRandom(1000) < 211)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case FALLEN_ORC_SHAMAN:
				{
					if (getRandom(1000) < 478)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case SHARP_TALON_TIGER:
				{
					if (getRandom(1000) < 234)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case GLOW_WISP:
				{
					if (getRandom(1000) < 245)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case MARSH_PREDATOR:
				{
					if (getRandom(100) < 26)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case HAMES_ORC_SNIPER:
				{
					if (getRandom(100) < 37)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case CURSED_GUARDIAN:
				{
					if (getRandom(1000) < 352)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
				case HAMES_ORC_CHIEFTAIN:
				case FALLEN_ORC_SHAMAN_TRANS:
				case SHARP_TALON_TIGER_TRANS:
				{
					if (getRandom(1000) < 487)
					{
						giveItemRandomly(qs.getPlayer(), npc, Q_STOLEN_INF_ORE, 1, 0, 1, true);
					}
					break;
				}
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

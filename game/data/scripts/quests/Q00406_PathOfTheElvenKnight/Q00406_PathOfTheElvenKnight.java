/*
 * Copyright (C) 2004-2015 L2J DataPack
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
package quests.Q00406_PathOfTheElvenKnight;

import java.util.HashMap;
import java.util.Map;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.base.ClassId;
import l2r.gameserver.model.holders.ItemChanceHolder;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.network.serverpackets.SocialAction;
import l2r.gameserver.util.Util;

/**
 * Path Of The Elven Knight (406)
 * @author ivantotov
 */
public final class Q00406_PathOfTheElvenKnight extends Quest
{
	// NPCs
	private static final int BLACKSMITH_KLUTO = 30317;
	private static final int MASTER_SORIUS = 30327;
	// Items
	private static final int SORIUS_LETTER = 1202;
	private static final int KLUTO_BOX = 1203;
	private static final int TOPAZ_PIECE = 1205;
	private static final int EMERALD_PIECE = 1206;
	private static final int KLUTO_MEMO = 1276;
	// Reward
	private static final int ELVEN_KNIGHT_BROOCH = 1204;
	// Misc
	private static final int MIN_LEVEL = 18;
	// Mobs
	private static final int OL_MAHUM_NOVICE = 20782;
	private static final Map<Integer, ItemChanceHolder> MONSTER_DROPS = new HashMap<>();
	
	static
	{
		MONSTER_DROPS.put(20035, new ItemChanceHolder(TOPAZ_PIECE, 70)); // Tracker Skeleton
		MONSTER_DROPS.put(20042, new ItemChanceHolder(TOPAZ_PIECE, 70)); // Tracker Skeleton Leader
		MONSTER_DROPS.put(20045, new ItemChanceHolder(TOPAZ_PIECE, 70)); // Skeleton Scout
		MONSTER_DROPS.put(20051, new ItemChanceHolder(TOPAZ_PIECE, 70)); // Skeleton Bowman
		MONSTER_DROPS.put(20054, new ItemChanceHolder(TOPAZ_PIECE, 70)); // Ruin Spartoi
		MONSTER_DROPS.put(20060, new ItemChanceHolder(TOPAZ_PIECE, 70)); // Salamander Noble
		MONSTER_DROPS.put(OL_MAHUM_NOVICE, new ItemChanceHolder(EMERALD_PIECE, 50)); // Ol Mahum Novice
	}
	
	public Q00406_PathOfTheElvenKnight()
	{
		super(406, Q00406_PathOfTheElvenKnight.class.getSimpleName(), "Path Of The Elven Knight");
		addStartNpc(MASTER_SORIUS);
		addTalkId(MASTER_SORIUS, BLACKSMITH_KLUTO);
		addKillId(MONSTER_DROPS.keySet());
		registerQuestItems(SORIUS_LETTER, KLUTO_BOX, TOPAZ_PIECE, EMERALD_PIECE, KLUTO_MEMO);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "ACCEPT":
			{
				if (player.getClassId() != ClassId.elvenFighter)
				{
					if (player.getClassId() == ClassId.elvenKnight)
					{
						htmltext = "30327-02a.htm";
					}
					else
					{
						htmltext = "30327-02.htm";
					}
				}
				else if (player.getLevel() < MIN_LEVEL)
				{
					htmltext = "30327-03.htm";
				}
				else if (hasQuestItems(player, ELVEN_KNIGHT_BROOCH))
				{
					htmltext = "30327-04.htm";
				}
				else
				{
					htmltext = "30327-05.htm";
				}
				break;
			}
			case "30327-06.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30317-02.html":
			{
				takeItems(player, SORIUS_LETTER, 1);
				if (!hasQuestItems(player, KLUTO_MEMO))
				{
					giveItems(player, KLUTO_MEMO, 1);
				}
				qs.setCond(4, true);
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = killer.getQuestState(getName());
		final ItemChanceHolder reward = MONSTER_DROPS.get(npc.getId());
		int requiredItemId = KLUTO_BOX;
		int cond = 2;
		boolean check = !hasQuestItems(killer, requiredItemId);
		if (npc.getId() == OL_MAHUM_NOVICE)
		{
			requiredItemId = KLUTO_MEMO;
			cond = 5;
			check = hasQuestItems(killer, requiredItemId);
		}
		
		if ((qs != null) && qs.isStarted() && Util.checkIfInRange(1500, npc, killer, false))
		{
			if (check && (getQuestItemsCount(killer, reward.getId()) < 20) && (getRandom(100) < reward.getChance()))
			{
				giveItems(killer, reward);
				if (getQuestItemsCount(killer, reward.getId()) == 20)
				{
					qs.setCond(cond, true);
				}
				else
				{
					playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated() || qs.isCompleted())
		{
			if (npc.getId() == MASTER_SORIUS)
			{
				htmltext = "30327-01.htm";
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case MASTER_SORIUS:
				{
					if (!hasQuestItems(player, KLUTO_BOX))
					{
						if (!hasQuestItems(player, TOPAZ_PIECE))
						{
							htmltext = "30327-07.html";
						}
						else if (hasQuestItems(player, TOPAZ_PIECE) && (getQuestItemsCount(player, TOPAZ_PIECE) < 20))
						{
							htmltext = "30327-08.html";
						}
						else if (!hasAtLeastOneQuestItem(player, KLUTO_MEMO, SORIUS_LETTER) && (getQuestItemsCount(player, TOPAZ_PIECE) >= 20))
						{
							if (!hasQuestItems(player, SORIUS_LETTER))
							{
								giveItems(player, SORIUS_LETTER, 1);
							}
							qs.setCond(3, true);
							htmltext = "30327-09.html";
						}
						else if ((getQuestItemsCount(player, TOPAZ_PIECE) >= 20) && hasAtLeastOneQuestItem(player, SORIUS_LETTER, KLUTO_MEMO))
						{
							htmltext = "30327-11.html";
						}
					}
					else
					{
						giveAdena(player, 163800, true);
						if (!hasQuestItems(player, ELVEN_KNIGHT_BROOCH))
						{
							giveItems(player, ELVEN_KNIGHT_BROOCH, 1);
						}
						final int level = player.getLevel();
						if (level >= 20)
						{
							addExpAndSp(player, 320534, 23152);
						}
						else if (level == 19)
						{
							addExpAndSp(player, 456128, 29850);
						}
						else
						{
							addExpAndSp(player, 591724, 33328);
						}
						qs.exitQuest(false, true);
						player.sendPacket(new SocialAction(player.getObjectId(), 3));
						qs.saveGlobalQuestVar("1ClassQuestFinished", "1");
						htmltext = "30327-10.html";
					}
					break;
				}
				case BLACKSMITH_KLUTO:
				{
					if (!hasQuestItems(player, KLUTO_BOX))
					{
						if (hasQuestItems(player, SORIUS_LETTER) && (getQuestItemsCount(player, TOPAZ_PIECE) >= 20))
						{
							htmltext = "30317-01.html";
						}
						else if (!hasQuestItems(player, EMERALD_PIECE) && hasQuestItems(player, KLUTO_MEMO) && (getQuestItemsCount(player, TOPAZ_PIECE) >= 20))
						{
							htmltext = "30317-03.html";
						}
						else if (hasQuestItems(player, KLUTO_MEMO, EMERALD_PIECE) && (getQuestItemsCount(player, TOPAZ_PIECE) >= 20) && (getQuestItemsCount(player, EMERALD_PIECE) < 20))
						{
							htmltext = "30317-04.html";
						}
						else if (hasQuestItems(player, KLUTO_MEMO) && (getQuestItemsCount(player, TOPAZ_PIECE) >= 20) && (getQuestItemsCount(player, EMERALD_PIECE) >= 20))
						{
							if (!hasQuestItems(player, KLUTO_BOX))
							{
								giveItems(player, KLUTO_BOX, 1);
							}
							takeItems(player, TOPAZ_PIECE, -1);
							takeItems(player, EMERALD_PIECE, -1);
							takeItems(player, KLUTO_MEMO, 1);
							qs.setCond(6, true);
							htmltext = "30317-05.html";
						}
					}
					else if (hasQuestItems(player, KLUTO_BOX))
					{
						htmltext = "30317-06.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
}
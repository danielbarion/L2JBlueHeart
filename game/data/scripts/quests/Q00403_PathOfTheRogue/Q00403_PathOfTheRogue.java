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
package quests.Q00403_PathOfTheRogue;

import java.util.HashMap;
import java.util.Map;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.base.ClassId;
import l2r.gameserver.model.holders.ItemChanceHolder;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.network.NpcStringId;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.NpcSay;
import l2r.gameserver.network.serverpackets.SocialAction;
import l2r.gameserver.util.Util;

/**
 * Path Of The Rogue (403)
 * @author ivantotov
 */
public final class Q00403_PathOfTheRogue extends Quest
{
	// NPCs
	private static final int CAPTAIN_BEZIQUE = 30379;
	private static final int NETI = 30425;
	// Items
	private static final int BEZIQUES_LETTER = 1180;
	private static final int NETIS_BOW = 1181;
	private static final int NETIS_DAGGER = 1182;
	private static final int SPARTOIS_BONES = 1183;
	private static final int HORSESHOE_OF_LIGHT = 1184;
	private static final int MOST_WANTED_LIST = 1185;
	private static final int STOLEN_JEWELRY = 1186;
	private static final int STOLEN_TOMES = 1187;
	private static final int STOLEN_RING = 1188;
	private static final int STOLEN_NECKLACE = 1189;
	private static final int[] STOLEN_ITEMS =
	{
		STOLEN_JEWELRY,
		STOLEN_TOMES,
		STOLEN_RING,
		STOLEN_NECKLACE
	};
	// Reward
	private static final int BEZIQUES_RECOMMENDATION = 1190;
	// Misc
	private static final int MIN_LEVEL = 18;
	private static final int REQUIRED_ITEM_COUNT = 10;
	// Quest Monster
	private static final int CATS_EYE_BANDIT = 27038;
	// Monster
	private static final Map<Integer, ItemChanceHolder> MONSTER_DROPS = new HashMap<>();
	
	static
	{
		MONSTER_DROPS.put(20035, new ItemChanceHolder(SPARTOIS_BONES, 2)); // Tracker Skeleton
		MONSTER_DROPS.put(20042, new ItemChanceHolder(SPARTOIS_BONES, 3)); // Tracker Skeleton Leader
		MONSTER_DROPS.put(20045, new ItemChanceHolder(SPARTOIS_BONES, 2)); // Skeleton Scout
		MONSTER_DROPS.put(20051, new ItemChanceHolder(SPARTOIS_BONES, 2)); // Skeleton Bowman
		MONSTER_DROPS.put(20054, new ItemChanceHolder(SPARTOIS_BONES, 8)); // Ruin Spartoi
		MONSTER_DROPS.put(20060, new ItemChanceHolder(SPARTOIS_BONES, 8)); // Raging Spartoi
	}
	
	public Q00403_PathOfTheRogue()
	{
		super(403, Q00403_PathOfTheRogue.class.getSimpleName(), "Path Of The Rogue");
		addStartNpc(CAPTAIN_BEZIQUE);
		addTalkId(CAPTAIN_BEZIQUE, NETI);
		addAttackId(MONSTER_DROPS.keySet());
		addAttackId(CATS_EYE_BANDIT);
		addKillId(MONSTER_DROPS.keySet());
		addKillId(CATS_EYE_BANDIT);
		registerQuestItems(BEZIQUES_LETTER, NETIS_BOW, NETIS_DAGGER, SPARTOIS_BONES, HORSESHOE_OF_LIGHT, MOST_WANTED_LIST, STOLEN_JEWELRY, STOLEN_TOMES, STOLEN_RING, STOLEN_NECKLACE);
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
				if (player.getClassId() == ClassId.fighter)
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						if (hasQuestItems(player, BEZIQUES_RECOMMENDATION))
						{
							htmltext = "30379-04.htm";
						}
						else
						{
							htmltext = "30379-05.htm";
						}
					}
					else
					{
						htmltext = "30379-03.htm";
					}
				}
				else if (player.getClassId() == ClassId.rogue)
				{
					htmltext = "30379-02a.htm";
				}
				else
				{
					htmltext = "30379-02.htm";
				}
				break;
			}
			case "30379-06.htm":
			{
				qs.startQuest();
				giveItems(player, BEZIQUES_LETTER, 1);
				htmltext = event;
				break;
			}
			case "30425-02.html":
			case "30425-03.html":
			case "30425-04.html":
			{
				htmltext = event;
				break;
			}
			case "30425-05.html":
			{
				if (hasQuestItems(player, BEZIQUES_LETTER))
				{
					takeItems(player, BEZIQUES_LETTER, 1);
					if (!hasQuestItems(player, NETIS_BOW))
					{
						giveItems(player, NETIS_BOW, 1);
					}
					if (!hasQuestItems(player, NETIS_DAGGER))
					{
						giveItems(player, NETIS_DAGGER, 1);
					}
					qs.setCond(2, true);
				}
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		final QuestState qs = getQuestState(attacker, false);
		if ((qs != null) && qs.isStarted())
		{
			switch (npc.getScriptValue())
			{
				case 0:
				{
					npc.getVariables().set("lastAttacker", attacker.getObjectId());
					if (!checkWeapon(attacker))
					{
						npc.setScriptValue(2);
					}
					else
					{
						if (npc.getId() == CATS_EYE_BANDIT)
						{
							attacker.sendPacket(new NpcSay(npc, Say2.NPC_ALL, NpcStringId.YOU_CHILDISH_FOOL_DO_YOU_THINK_YOU_CAN_CATCH_ME));
						}
						npc.setScriptValue(1);
					}
					break;
				}
				case 1:
				{
					if (!checkWeapon(attacker))
					{
						npc.setScriptValue(2);
					}
					else if (npc.getVariables().getInt("lastAttacker") != attacker.getObjectId())
					{
						npc.setScriptValue(2);
					}
					break;
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isStarted() && npc.isScriptValue(1) && Util.checkIfInRange(1500, npc, killer, true))
		{
			if (npc.getId() == CATS_EYE_BANDIT)
			{
				npc.broadcastPacket(new NpcSay(npc, Say2.NPC_ALL, NpcStringId.I_MUST_DO_SOMETHING_ABOUT_THIS_SHAMEFUL_INCIDENT));
				if (hasQuestItems(killer, MOST_WANTED_LIST))
				{
					int randomItem = STOLEN_ITEMS[getRandom(STOLEN_ITEMS.length)];
					if (!hasQuestItems(killer, randomItem))
					{
						giveItems(killer, randomItem, 1);
						
						if (hasQuestItems(killer, STOLEN_ITEMS))
						{
							qs.setCond(6, true);
						}
						else
						{
							playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						}
					}
				}
			}
			else
			{
				final ItemChanceHolder reward = MONSTER_DROPS.get(npc.getId());
				if ((getQuestItemsCount(killer, reward.getId()) < REQUIRED_ITEM_COUNT) && npc.isScriptValue(1) && (getRandom(REQUIRED_ITEM_COUNT) < reward.getChance()))
				{
					giveItems(killer, reward.getId(), reward.getCount());
					if (getQuestItemsCount(killer, reward.getId()) >= REQUIRED_ITEM_COUNT)
					{
						qs.setCond(3, true);
					}
					else
					{
						playSound(qs.getPlayer(), QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	private boolean checkWeapon(L2PcInstance player)
	{
		L2ItemInstance weapon = player.getActiveWeaponInstance();
		return ((weapon != null) && ((weapon.getId() == NETIS_BOW) || (weapon.getId() == NETIS_DAGGER)));
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated() || qs.isCompleted())
		{
			if (npc.getId() == CAPTAIN_BEZIQUE)
			{
				htmltext = "30379-01.htm";
			}
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case CAPTAIN_BEZIQUE:
				{
					if (hasQuestItems(player, STOLEN_JEWELRY, STOLEN_TOMES, STOLEN_RING, STOLEN_NECKLACE))
					{
						giveAdena(player, 163800, true);
						takeItems(player, NETIS_BOW, 1);
						takeItems(player, NETIS_DAGGER, 1);
						takeItems(player, MOST_WANTED_LIST, 1);
						takeItems(player, STOLEN_JEWELRY, 1);
						takeItems(player, STOLEN_TOMES, 1);
						takeItems(player, STOLEN_RING, 1);
						takeItems(player, STOLEN_NECKLACE, 1);
						giveItems(player, BEZIQUES_RECOMMENDATION, 1);
						final int level = player.getLevel();
						if (level >= 20)
						{
							addExpAndSp(player, 320534, 20232);
						}
						else if (level == 19)
						{
							addExpAndSp(player, 456128, 26930);
						}
						else
						{
							addExpAndSp(player, 591724, 33628);
						}
						qs.exitQuest(false, true);
						player.sendPacket(new SocialAction(player.getObjectId(), 3));
						qs.saveGlobalQuestVar("1ClassQuestFinished", "1");
						htmltext = "30379-09.html";
					}
					else if (!hasQuestItems(player, HORSESHOE_OF_LIGHT) && hasQuestItems(player, BEZIQUES_LETTER))
					{
						htmltext = "30379-07.html";
					}
					else if (hasQuestItems(player, HORSESHOE_OF_LIGHT))
					{
						takeItems(player, HORSESHOE_OF_LIGHT, 1);
						giveItems(player, MOST_WANTED_LIST, 1);
						qs.setCond(5, true);
						htmltext = "30379-08.html";
					}
					else if (hasQuestItems(player, NETIS_BOW, NETIS_DAGGER) && !hasQuestItems(player, MOST_WANTED_LIST))
					{
						htmltext = "30379-10.html";
					}
					else if (hasQuestItems(player, MOST_WANTED_LIST))
					{
						htmltext = "30379-11.html";
					}
					break;
				}
				case NETI:
				{
					if (hasQuestItems(player, BEZIQUES_LETTER))
					{
						htmltext = "30425-01.html";
					}
					else if (!hasAtLeastOneQuestItem(player, HORSESHOE_OF_LIGHT, BEZIQUES_LETTER))
					{
						if (hasQuestItems(player, MOST_WANTED_LIST))
						{
							htmltext = "30425-08.html";
						}
						else if (getQuestItemsCount(player, SPARTOIS_BONES) < REQUIRED_ITEM_COUNT)
						{
							htmltext = "30425-06.html";
						}
						else
						{
							takeItems(player, SPARTOIS_BONES, REQUIRED_ITEM_COUNT);
							giveItems(player, HORSESHOE_OF_LIGHT, 1);
							qs.setCond(4, true);
							htmltext = "30425-07.html";
						}
					}
					else if (hasQuestItems(player, HORSESHOE_OF_LIGHT))
					{
						htmltext = "30425-08.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
}
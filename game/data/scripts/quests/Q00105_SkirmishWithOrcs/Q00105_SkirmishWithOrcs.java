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
package quests.Q00105_SkirmishWithOrcs;

import java.util.HashMap;
import java.util.Map;

import l2r.gameserver.enums.Race;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.holders.ItemHolder;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;
import l2r.gameserver.network.NpcStringId;
import l2r.gameserver.network.serverpackets.SocialAction;
import l2r.gameserver.util.Util;

import quests.Q00281_HeadForTheHills.Q00281_HeadForTheHills;

/**
 * Skimirish with Orcs (105)
 * @author janiko
 */
public final class Q00105_SkirmishWithOrcs extends Quest
{
	// NPC
	private static final int KENDNELL = 30218;
	// Items
	private static final int KENDELLS_1ST_ORDER = 1836;
	private static final int KENDELLS_2ND_ORDER = 1837;
	private static final int KENDELLS_3RD_ORDER = 1838;
	private static final int KENDELLS_4TH_ORDER = 1839;
	private static final int KENDELLS_5TH_ORDER = 1840;
	private static final int KENDELLS_6TH_ORDER = 1841;
	private static final int KENDELLS_7TH_ORDER = 1842;
	private static final int KENDELLS_8TH_ORDER = 1843;
	private static final int KABOO_CHIEFS_1ST_TORQUE = 1844;
	private static final int KABOO_CHIEFS_2ST_TORQUE = 1845;
	private static final Map<Integer, Integer> MONSTER_DROP = new HashMap<>();
	
	static
	{
		MONSTER_DROP.put(27059, KENDELLS_1ST_ORDER); // Uoph (Kaboo Chief)
		MONSTER_DROP.put(27060, KENDELLS_2ND_ORDER); // Kracha (Kaboo Chief)
		MONSTER_DROP.put(27061, KENDELLS_3RD_ORDER); // Batoh (Kaboo Chief)
		MONSTER_DROP.put(27062, KENDELLS_4TH_ORDER); // Tanukia (Kaboo Chief)
		MONSTER_DROP.put(27064, KENDELLS_5TH_ORDER); // Turel (Kaboo Chief)
		MONSTER_DROP.put(27065, KENDELLS_6TH_ORDER); // Roko (Kaboo Chief)
		MONSTER_DROP.put(27067, KENDELLS_7TH_ORDER); // Kamut (Kaboo Chief)
		MONSTER_DROP.put(27068, KENDELLS_8TH_ORDER); // Murtika (Kaboo Chief)
	}
	
	private static final int[] KENDNELLS_ORDERS =
	{
		KENDELLS_1ST_ORDER,
		KENDELLS_2ND_ORDER,
		KENDELLS_3RD_ORDER,
		KENDELLS_4TH_ORDER,
		KENDELLS_5TH_ORDER,
		KENDELLS_6TH_ORDER,
		KENDELLS_7TH_ORDER,
		KENDELLS_8TH_ORDER
	};
	// Misc
	private static final int MIN_LVL = 10;
	// Rewards
	private static final int RED_SUNSET_STAFF = 754;
	private static final int RED_SUNSET_SWORD = 981;
	private static final ItemHolder[] REWARDS =
	{
		new ItemHolder(1060, 100), // Lesser Healing Potion
		new ItemHolder(4412, 10), // Echo Crystal - Theme of Battle
		new ItemHolder(4413, 10), // Echo Crystal - Theme of Love
		new ItemHolder(4414, 10), // Echo Crystal - Theme of Solitude
		new ItemHolder(4415, 10), // Echo Crystal - Theme of Feast
		new ItemHolder(4416, 10), // Echo Crystal - Theme of Celebration
	};
	
	public Q00105_SkirmishWithOrcs()
	{
		super(105, Q00105_SkirmishWithOrcs.class.getSimpleName(), "Skirmish with Orcs");
		addStartNpc(KENDNELL);
		addTalkId(KENDNELL);
		addKillId(MONSTER_DROP.keySet());
		registerQuestItems(KENDNELLS_ORDERS);
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
			case "30218-04.html":
			{
				if (st.isCreated())
				{
					st.startQuest();
					st.giveItems(KENDNELLS_ORDERS[getRandom(0, 3)], 1);
					htmltext = event;
				}
				break;
			}
			case "30218-05.html":
			{
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState st = killer.getQuestState(getName());
		if ((st != null) && Util.checkIfInRange(1500, npc, killer, true))
		{
			switch (npc.getId())
			{
				case 27059:
				case 27060:
				case 27061:
				case 27062:
				{
					if (st.isCond(1) && st.hasQuestItems(MONSTER_DROP.get(npc.getId())))
					{
						st.giveItems(KABOO_CHIEFS_1ST_TORQUE, 1);
						st.setCond(2, true);
					}
					break;
				}
				case 27064:
				case 27065:
				case 27067:
				case 27068:
				{
					if (st.isCond(3) && st.hasQuestItems(MONSTER_DROP.get(npc.getId())))
					{
						st.giveItems(KABOO_CHIEFS_2ST_TORQUE, 1);
						st.setCond(4, true);
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		final QuestState st = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				if (talker.getRace() == Race.ELF)
				{
					htmltext = (talker.getLevel() >= MIN_LVL) ? "30218-03.htm" : "30218-02.htm";
				}
				else
				{
					htmltext = "30218-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (hasAtLeastOneQuestItem(talker, KENDELLS_1ST_ORDER, KENDELLS_2ND_ORDER, KENDELLS_3RD_ORDER, KENDELLS_4TH_ORDER))
				{
					htmltext = "30218-06.html";
				}
				if (st.isCond(2) && st.hasQuestItems(KABOO_CHIEFS_1ST_TORQUE))
				{
					for (int i = 0; i < 4; i++)
					{
						st.takeItems(KENDNELLS_ORDERS[i], -1);
					}
					st.takeItems(KABOO_CHIEFS_1ST_TORQUE, 1);
					st.giveItems(KENDNELLS_ORDERS[getRandom(4, 7)], 1);
					st.setCond(3, true);
					htmltext = "30218-07.html";
				}
				if (hasAtLeastOneQuestItem(talker, KENDELLS_5TH_ORDER, KENDELLS_6TH_ORDER, KENDELLS_7TH_ORDER, KENDELLS_8TH_ORDER))
				{
					htmltext = "30218-08.html";
				}
				if (st.isCond(4) && st.hasQuestItems(KABOO_CHIEFS_2ST_TORQUE))
				{
					Q00281_HeadForTheHills.giveNewbieReward(talker);
					talker.sendPacket(new SocialAction(talker.getObjectId(), 3));
					showOnScreenMsg(talker, NpcStringId.ACQUISITION_OF_RACE_SPECIFIC_WEAPON_COMPLETE_N_GO_FIND_THE_NEWBIE_GUIDE, 2, 5000);
					
					st.addExpAndSp(41478, 3555);
					st.giveAdena(17599, true);
					
					st.giveItems(st.getPlayer().isMageClass() ? RED_SUNSET_STAFF : RED_SUNSET_SWORD, 1);
					
					for (ItemHolder reward : REWARDS)
					{
						giveItems(talker, reward);
					}
					
					st.exitQuest(false, true);
					htmltext = "30218-09.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(talker);
				break;
			}
		}
		return htmltext;
	}
}
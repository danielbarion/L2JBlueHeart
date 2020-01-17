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
package quests.Q00266_PleasOfPixies;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.enums.Race;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.holders.ItemHolder;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;

/**
 * Pleas of Pixies (266)
 * @author xban1x
 */
public final class Q00266_PleasOfPixies extends Quest
{
	// NPC
	private static final int PIXY_MURIKA = 31852;
	// Items
	private static final int PREDATORS_FANG = 1334;
	// Monsters
	private static final Map<Integer, List<ItemHolder>> MONSTERS = new HashMap<>();
	
	static
	{
		MONSTERS.put(20537, Arrays.asList(new ItemHolder(10, 2))); // Elder Red Keltir
		MONSTERS.put(20525, Arrays.asList(new ItemHolder(5, 2), new ItemHolder(10, 3))); // Gray Wolf
		MONSTERS.put(20534, Arrays.asList(new ItemHolder(6, 1))); // Red Keltir
		MONSTERS.put(20530, Arrays.asList(new ItemHolder(8, 1))); // Young Red Keltir
	}
	
	// Rewards
	private static final Map<Integer, List<ItemHolder>> REWARDS = new HashMap<>();
	
	static
	{
		REWARDS.put(0, Arrays.asList(new ItemHolder(1337, 1), new ItemHolder(3032, 1))); // Emerald, Recipe: Spiritshot D
		REWARDS.put(1, Arrays.asList(new ItemHolder(2176, 1), new ItemHolder(1338, 1))); // Recipe: Leather Boots, Blue Onyx
		REWARDS.put(2, Arrays.asList(new ItemHolder(1339, 1), new ItemHolder(1061, 1))); // Onyx, Greater Healing Potion
		REWARDS.put(3, Arrays.asList(new ItemHolder(1336, 1), new ItemHolder(1060, 1))); // Glass Shard, Lesser Healing Potion
	}
	
	// Misc
	private static final int MIN_LVL = 3;
	
	public Q00266_PleasOfPixies()
	{
		super(266, Q00266_PleasOfPixies.class.getSimpleName(), "Pleas of Pixies");
		addStartNpc(PIXY_MURIKA);
		addTalkId(PIXY_MURIKA);
		addKillId(MONSTERS.keySet());
		registerQuestItems(PREDATORS_FANG);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if ((st != null) && event.equals("31852-04.htm"))
		{
			st.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState st = killer.getQuestState(getName());
		if ((st != null) && st.isCond(1))
		{
			final int chance = getRandom(10);
			for (ItemHolder mob : MONSTERS.get(npc.getId()))
			{
				if (chance < mob.getId())
				{
					if (st.giveItemRandomly(npc, PREDATORS_FANG, mob.getCount(), 100, 1.0, true))
					{
						st.setCond(2);
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
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
				if (player.getRace() != Race.ELF)
				{
					htmltext = "31852-01.htm";
				}
				else if (player.getLevel() < MIN_LVL)
				{
					htmltext = "31852-02.htm";
				}
				else
				{
					htmltext = "31852-03.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (st.getCond())
				{
					case 1:
					{
						htmltext = "31852-05.html";
						break;
					}
					case 2:
					{
						if (st.getQuestItemsCount(PREDATORS_FANG) >= 100)
						{
							final int chance = getRandom(100);
							int reward;
							if (chance < 2)
							{
								reward = 0;
								st.playSound(QuestSound.ITEMSOUND_QUEST_JACKPOT);
							}
							else if (chance < 20)
							{
								reward = 1;
							}
							else if (chance < 45)
							{
								reward = 2;
							}
							else
							{
								reward = 3;
							}
							for (ItemHolder item : REWARDS.get(reward))
							{
								st.rewardItems(item);
							}
							st.exitQuest(true, true);
							htmltext = "31852-06.html";
						}
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
}

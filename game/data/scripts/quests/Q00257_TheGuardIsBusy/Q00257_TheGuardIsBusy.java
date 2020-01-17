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
package quests.Q00257_TheGuardIsBusy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.holders.ItemHolder;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;

import quests.Q00281_HeadForTheHills.Q00281_HeadForTheHills;

/**
 * The Guard is Busy (257)
 * @author xban1x
 */
public final class Q00257_TheGuardIsBusy extends Quest
{
	public final static class MobDrop extends ItemHolder
	{
		private final int _chance;
		private final int _random;
		
		public MobDrop(int random, int chance, int id, long count)
		{
			super(id, count);
			_random = random;
			_chance = chance;
		}
		
		public boolean getDrop()
		{
			return (getRandom(_random) < _chance);
		}
	}
	
	// NPC
	private static final int GILBERT = 30039;
	// Misc
	private static final int MIN_LVL = 6;
	// Monsters
	private static final Map<Integer, List<MobDrop>> MONSTERS = new HashMap<>();
	// Items
	private static final int GLUDIO_LORDS_MARK = 1084;
	private static final int ORC_AMULET = 752;
	private static final int ORC_NECKLACE = 1085;
	private static final int WEREWOLF_FANG = 1086;
	
	static
	{
		MONSTERS.put(20006, Arrays.asList(new MobDrop(10, 2, ORC_AMULET, 2), new MobDrop(10, 10, ORC_AMULET, 1))); // Orc Archer
		MONSTERS.put(20093, Arrays.asList(new MobDrop(100, 85, ORC_NECKLACE, 1))); // Orc Fighter
		MONSTERS.put(20096, Arrays.asList(new MobDrop(100, 95, ORC_NECKLACE, 1))); // Orc Fighter Sub Leader
		MONSTERS.put(20098, Arrays.asList(new MobDrop(100, 100, ORC_NECKLACE, 1)));// Orc Fighter Leader
		MONSTERS.put(20130, Arrays.asList(new MobDrop(10, 7, ORC_AMULET, 1))); // Orc
		MONSTERS.put(20131, Arrays.asList(new MobDrop(10, 9, ORC_AMULET, 1))); // Orc Grunt
		MONSTERS.put(20132, Arrays.asList(new MobDrop(10, 7, WEREWOLF_FANG, 1))); // Werewolf
		MONSTERS.put(20342, Arrays.asList(new MobDrop(0, 1, WEREWOLF_FANG, 1))); // Werewolf Chieftain
		MONSTERS.put(20343, Arrays.asList(new MobDrop(100, 85, WEREWOLF_FANG, 1))); // Werewolf Hunter
	}
	
	public Q00257_TheGuardIsBusy()
	{
		super(257, Q00257_TheGuardIsBusy.class.getSimpleName(), "The Guard is Busy");
		addStartNpc(GILBERT);
		addTalkId(GILBERT);
		addKillId(MONSTERS.keySet());
		registerQuestItems(ORC_AMULET, GLUDIO_LORDS_MARK, ORC_NECKLACE, WEREWOLF_FANG);
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
			case "30039-03.htm":
			{
				st.startQuest();
				st.giveItems(GLUDIO_LORDS_MARK, 1);
				htmltext = event;
				break;
			}
			case "30039-05.html":
			{
				st.exitQuest(true, true);
				htmltext = event;
				break;
			}
			case "30039-06.html":
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
		if (st == null)
		{
			return super.onKill(npc, killer, isSummon);
		}
		
		for (MobDrop drop : MONSTERS.get(npc.getId()))
		{
			if (drop.getDrop())
			{
				st.giveItems(drop);
				st.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
				break;
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
				htmltext = (player.getLevel() >= MIN_LVL) ? "30039-02.htm" : "30039-01.html";
				break;
			}
			case State.STARTED:
			{
				if (hasAtLeastOneQuestItem(player, ORC_AMULET, ORC_NECKLACE, WEREWOLF_FANG))
				{
					final long amulets = st.getQuestItemsCount(ORC_AMULET);
					final long common = getQuestItemsCount(player, ORC_NECKLACE, WEREWOLF_FANG);
					st.giveAdena(((amulets * 10) + (common * 20) + (((amulets + common) >= 10) ? 1000 : 0)), true);
					takeItems(player, -1, ORC_AMULET, ORC_NECKLACE, WEREWOLF_FANG);
					Q00281_HeadForTheHills.giveNewbieReward(player);
					htmltext = "30039-07.html";
				}
				else
				{
					htmltext = "30039-04.html";
				}
				break;
			}
		}
		return htmltext;
	}
}

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
package quests.Q00294_CovertBusiness;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2r.gameserver.enums.Race;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.util.Util;

/**
 * Covert Business (294)
 * @author xban1x
 */
public final class Q00294_CovertBusiness extends Quest
{
	// NPC
	private static final int KEEF = 30534;
	// Item
	private static final int BAT_FANG = 1491;
	// Monsters
	private static final Map<Integer, List<Integer>> MONSTER_DROP_CHANCE = new HashMap<>();
	
	static
	{
		MONSTER_DROP_CHANCE.put(20370, Arrays.asList(6, 3, 1, -1));
		MONSTER_DROP_CHANCE.put(20480, Arrays.asList(5, 2, -1));
	}
	
	// Reward
	private static final int RING_OF_RACCOON = 1508;
	// Misc
	private static final int MIN_LVL = 10;
	
	public Q00294_CovertBusiness()
	{
		super(294, Q00294_CovertBusiness.class.getSimpleName(), "Covert Business");
		addStartNpc(KEEF);
		addTalkId(KEEF);
		addKillId(MONSTER_DROP_CHANCE.keySet());
		registerQuestItems(BAT_FANG);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCreated() && event.equals("30534-03.htm"))
		{
			qs.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1) && Util.checkIfInRange(1500, npc, killer, true))
		{
			final int chance = getRandom(10);
			int count = 0;
			for (int i : MONSTER_DROP_CHANCE.get(npc.getId()))
			{
				count++;
				if (chance > i)
				{
					if (giveItemRandomly(killer, npc, BAT_FANG, count, 100, 1.0, true))
					{
						qs.setCond(2);
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
		final QuestState qs = getQuestState(talker, true);
		String html = getNoQuestMsg(talker);
		if (qs.isCreated())
		{
			html = (talker.getRace() == Race.DWARF) ? (talker.getLevel() >= MIN_LVL) ? "30534-02.htm" : "30534-01.htm" : "30534-00.htm";
		}
		else if (qs.isStarted())
		{
			if (qs.isCond(2))
			{
				if (hasQuestItems(talker, RING_OF_RACCOON))
				{
					giveAdena(talker, 2400, true);
					html = "30534-06.html";
				}
				else
				{
					giveItems(talker, RING_OF_RACCOON, 1);
					html = "30534-05.html";
				}
				addExpAndSp(talker, 0, 600);
				qs.exitQuest(true, true);
			}
			else
			{
				html = "30534-04.html";
			}
		}
		return html;
	}
}

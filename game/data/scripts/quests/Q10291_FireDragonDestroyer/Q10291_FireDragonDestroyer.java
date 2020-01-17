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
package quests.Q10291_FireDragonDestroyer;

import java.util.function.Function;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;
import l2r.gameserver.util.Util;

/**
 * Fire Dragon Destroyer (10291)
 * @author malyelfik
 */
public class Q10291_FireDragonDestroyer extends Quest
{
	// NPC
	private static final int KLEIN = 31540;
	// Monster
	private static final int VALAKAS = 29028;
	// Items
	private static final int FLOATING_STONE = 7267;
	private static final int POOR_NECKLACE = 15524;
	private static final int VALOR_NECKLACE = 15525;
	
	private static final int VALAKAS_SLAYER_CIRCLET = 8567;
	
	public Q10291_FireDragonDestroyer()
	{
		super(10291, Q10291_FireDragonDestroyer.class.getSimpleName(), "Fire Dragon Destroyer");
		addStartNpc(KLEIN);
		addTalkId(KLEIN);
		addKillId(VALAKAS);
		registerQuestItems(POOR_NECKLACE, VALOR_NECKLACE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return getNoQuestMsg(player);
		}
		
		if (event.equals("31540-05.htm"))
		{
			st.startQuest();
			st.giveItems(POOR_NECKLACE, 1);
		}
		
		return event;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		if (!player.isInParty())
		{
			return super.onKill(npc, player, isSummon);
		}
		
		Function<L2PcInstance, Boolean> rewardCheck = p ->
		{
			if (Util.checkIfInRange(8000, npc, p, false))
			{
				QuestState st = p.getQuestState(getName());
				
				if ((st != null) && st.isCond(1) && st.hasQuestItems(POOR_NECKLACE))
				{
					st.takeItems(POOR_NECKLACE, -1);
					st.giveItems(VALOR_NECKLACE, 1);
					st.setCond(2, true);
				}
			}
			return true;
		};
		
		// Rewards go only to command channel, not to a single party or player.
		if (player.getParty().isInCommandChannel())
		{
			player.getParty().getCommandChannel().forEachMember(rewardCheck);
		}
		else
		{
			player.getParty().forEachMember(rewardCheck);
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
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				if (player.getLevel() < 83)
				{
					htmltext = "31540-00.htm";
				}
				else
				{
					htmltext = st.hasQuestItems(FLOATING_STONE) ? "31540-02.htm" : "31540-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (st.isCond(1))
				{
					if (st.hasQuestItems(POOR_NECKLACE))
					{
						htmltext = "31540-06.html";
					}
					else
					{
						st.giveItems(POOR_NECKLACE, 1);
						htmltext = "31540-07.html";
					}
				}
				else if (st.isCond(2) && st.hasQuestItems(VALOR_NECKLACE))
				{
					htmltext = "31540-08.html";
					st.giveAdena(126549, true);
					st.addExpAndSp(717291, 77397);
					st.giveItems(VALAKAS_SLAYER_CIRCLET, 1);
					st.exitQuest(false, true);
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = "31540-09.html";
				break;
			}
		}
		
		return htmltext;
	}
}

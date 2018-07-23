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
package quests.Q00151_CureForFever;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;
import l2r.gameserver.network.NpcStringId;

/**
 * Cure for Fever (151)
 * @author malyelfik
 */
public class Q00151_CureForFever extends Quest
{
	// NPCs
	private static final int ELLIAS = 30050;
	private static final int YOHANES = 30032;
	// Monsters
	private static final int[] MOBS =
	{
		20103, // Giant Spider
		20106, // Talon Spider
		20108, // Blade Spider
	};
	// Items
	private static final int ROUND_SHIELD = 102;
	private static final int POISON_SAC = 703;
	private static final int FEVER_MEDICINE = 704;
	// Misc
	private static final int MIN_LEVEL = 15;
	private static final int CHANCE = 0;
	
	public Q00151_CureForFever()
	{
		super(151, Q00151_CureForFever.class.getSimpleName(), "Cure for Fever");
		addStartNpc(ELLIAS);
		addTalkId(ELLIAS, YOHANES);
		addKillId(MOBS);
		registerQuestItems(POISON_SAC, FEVER_MEDICINE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if ((st != null) && event.equalsIgnoreCase("30050-03.htm"))
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
		if ((st != null) && st.isCond(1) && (getRandom(5) == CHANCE))
		{
			st.giveItems(POISON_SAC, 1);
			st.setCond(2, true);
		}
		return super.onKill(npc, killer, isSummon);
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
		
		switch (npc.getId())
		{
			case ELLIAS:
				switch (st.getState())
				{
					case State.CREATED:
						htmltext = (player.getLevel() >= MIN_LEVEL) ? "30050-02.htm" : "30050-01.htm";
						break;
					case State.STARTED:
						if (st.isCond(3) && st.hasQuestItems(FEVER_MEDICINE))
						{
							st.giveItems(ROUND_SHIELD, 1);
							st.addExpAndSp(13106, 613);
							st.exitQuest(false, true);
							showOnScreenMsg(player, NpcStringId.LAST_DUTY_COMPLETE_N_GO_FIND_THE_NEWBIE_GUIDE, 2, 5000); // TODO: Newbie Guide
							htmltext = "30050-06.html";
						}
						else if (st.isCond(2) && st.hasQuestItems(POISON_SAC))
						{
							htmltext = "30050-05.html";
						}
						else
						{
							htmltext = "30050-04.html";
						}
						break;
					case State.COMPLETED:
						htmltext = getAlreadyCompletedMsg(player);
						break;
				}
				break;
			case YOHANES:
				if (st.isStarted())
				{
					if (st.isCond(2) && st.hasQuestItems(POISON_SAC))
					{
						st.setCond(3, true);
						st.takeItems(POISON_SAC, -1);
						st.giveItems(FEVER_MEDICINE, 1);
						htmltext = "30032-01.html";
					}
					else if (st.isCond(3) && st.hasQuestItems(FEVER_MEDICINE))
					{
						htmltext = "30032-02.html";
					}
				}
				break;
		}
		return htmltext;
	}
}

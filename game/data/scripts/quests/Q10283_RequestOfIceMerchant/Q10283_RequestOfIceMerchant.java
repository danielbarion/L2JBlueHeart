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
package quests.Q10283_RequestOfIceMerchant;

import l2r.gameserver.enums.CtrlIntention;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;

import quests.Q00115_TheOtherSideOfTruth.Q00115_TheOtherSideOfTruth;

/**
 * Request of Ice Merchant (10283)
 * @author Gnacik
 * @version 2013-02-07 Updated to High Five
 */
public class Q10283_RequestOfIceMerchant extends Quest
{
	// NPCs
	private static final int RAFFORTY = 32020;
	private static final int KIER = 32022;
	private static final int JINIA = 32760;
	// Location
	private static final Location MOVE_TO_END = new Location(104457, -107010, -3698, 0);
	// Misc
	private boolean _jiniaOnSpawn = false;
	
	public Q10283_RequestOfIceMerchant()
	{
		super(10283, Q10283_RequestOfIceMerchant.class.getSimpleName(), "Request of Ice Merchant");
		addStartNpc(RAFFORTY);
		addTalkId(RAFFORTY, KIER, JINIA);
		addFirstTalkId(JINIA);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return htmltext;
		}
		
		if (npc.getId() == RAFFORTY)
		{
			if (event.equalsIgnoreCase("32020-03.htm"))
			{
				st.startQuest();
			}
			else if (event.equalsIgnoreCase("32020-07.htm"))
			{
				st.setCond(2, true);
			}
		}
		else if ((npc.getId() == KIER) && event.equalsIgnoreCase("spawn"))
		{
			if (_jiniaOnSpawn)
			{
				htmltext = "32022-02.html";
			}
			else
			{
				addSpawn(JINIA, 104473, -107549, -3695, 44954, false, 180000);
				_jiniaOnSpawn = true;
				startQuestTimer("despawn", 180000, npc, player);
				return null;
			}
		}
		else if (event.equalsIgnoreCase("despawn"))
		{
			_jiniaOnSpawn = false;
			return null;
		}
		else if ((npc.getId() == JINIA) && event.equalsIgnoreCase("32760-04.html"))
		{
			st.giveAdena(190000, true);
			st.addExpAndSp(627000, 50300);
			st.exitQuest(false, true);
			npc.setRunning();
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, MOVE_TO_END);
			npc.decayMe();
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (npc.getInstanceId() > 0)
		{
			return "32760-10.html";
		}
		
		final QuestState st = getQuestState(player, false);
		if ((st != null) && st.isCond(2))
		{
			return "32760-01.html";
		}
		return null;
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
			case RAFFORTY:
				switch (st.getState())
				{
					case State.CREATED:
						QuestState _prev = player.getQuestState(Q00115_TheOtherSideOfTruth.class.getSimpleName());
						htmltext = ((_prev != null) && _prev.isCompleted() && (player.getLevel() >= 82)) ? "32020-01.htm" : "32020-00.htm";
						break;
					case State.STARTED:
						if (st.isCond(1))
						{
							htmltext = "32020-04.htm";
						}
						else if (st.isCond(2))
						{
							htmltext = "32020-08.htm";
						}
						break;
					case State.COMPLETED:
						htmltext = "32020-09.htm";
						break;
				}
				break;
			case KIER:
				if (st.isCond(2))
				{
					htmltext = "32022-01.html";
				}
				break;
			case JINIA:
				if (st.isCond(2))
				{
					htmltext = "32760-02.html";
				}
				break;
		}
		return htmltext;
	}
}

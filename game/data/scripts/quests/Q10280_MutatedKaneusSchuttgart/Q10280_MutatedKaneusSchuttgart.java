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
package quests.Q10280_MutatedKaneusSchuttgart;

import java.util.ArrayList;
import java.util.List;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;

/**
 * Mutated Kaneus - Schuttgart (10280)<br>
 * Original Jython script by Gnacik on 2010-06-29.
 * @author nonom
 */
public class Q10280_MutatedKaneusSchuttgart extends Quest
{
	// NPCs
	private static final int VISHOTSKY = 31981;
	private static final int ATRAXIA = 31972;
	private static final int VENOMOUS_STORACE = 18571;
	private static final int KEL_BILETTE = 18573;
	// Items
	private static final int TISSUE_VS = 13838;
	private static final int TISSUE_KB = 13839;
	
	public Q10280_MutatedKaneusSchuttgart()
	{
		super(10280, Q10280_MutatedKaneusSchuttgart.class.getSimpleName(), "Mutated Kaneus - Schuttgart");
		addStartNpc(VISHOTSKY);
		addTalkId(VISHOTSKY, ATRAXIA);
		addKillId(VENOMOUS_STORACE, KEL_BILETTE);
		registerQuestItems(TISSUE_VS, TISSUE_KB);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return getNoQuestMsg(player);
		}
		
		switch (event)
		{
			case "31981-03.htm":
				st.startQuest();
				break;
			case "31972-03.htm":
				st.giveAdena(210000, true);
				st.exitQuest(false, true);
				break;
		}
		return event;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		QuestState st = killer.getQuestState(getName());
		if (st == null)
		{
			return null;
		}
		
		final int npcId = npc.getId();
		if (killer.getParty() != null)
		{
			final List<QuestState> PartyMembers = new ArrayList<>();
			for (L2PcInstance member : killer.getParty().getMembers())
			{
				st = member.getQuestState(getName());
				if ((st != null) && st.isStarted() && (((npcId == VENOMOUS_STORACE) && !st.hasQuestItems(TISSUE_VS)) || ((npcId == KEL_BILETTE) && !st.hasQuestItems(TISSUE_KB))))
				{
					PartyMembers.add(st);
				}
			}
			
			if (!PartyMembers.isEmpty())
			{
				for (QuestState member : PartyMembers)
				{
					rewardItem(npcId, member);
				}
			}
		}
		else if (st.isStarted())
		{
			rewardItem(npcId, st);
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
			case VISHOTSKY:
				switch (st.getState())
				{
					case State.CREATED:
						htmltext = (player.getLevel() > 57) ? "31981-01.htm" : "31981-00.htm";
						break;
					case State.STARTED:
						htmltext = (st.hasQuestItems(TISSUE_VS) && st.hasQuestItems(TISSUE_KB)) ? "31981-05.htm" : "31981-04.htm";
						break;
					case State.COMPLETED:
						htmltext = "31981-06.htm";
						break;
				}
				break;
			case ATRAXIA:
				switch (st.getState())
				{
					case State.STARTED:
						htmltext = (st.hasQuestItems(TISSUE_VS) && st.hasQuestItems(TISSUE_KB)) ? "31972-02.htm" : "31972-01.htm";
						break;
					case State.COMPLETED:
						htmltext = getAlreadyCompletedMsg(player);
						break;
					default:
						break;
				}
				break;
		}
		return htmltext;
	}
	
	/**
	 * @param npcId the ID of the killed monster
	 * @param st the quest state of the killer or party member
	 */
	private final void rewardItem(int npcId, QuestState st)
	{
		if ((npcId == VENOMOUS_STORACE) && !st.hasQuestItems(TISSUE_VS))
		{
			st.giveItems(TISSUE_VS, 1);
			st.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		else if ((npcId == KEL_BILETTE) && !st.hasQuestItems(TISSUE_KB))
		{
			st.giveItems(TISSUE_KB, 1);
			st.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
	}
}

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
package quests.Q00636_TruthBeyond;

import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;
import l2r.gameserver.model.zone.L2ZoneType;

/**
 * The Truth Beyond the Gate (636)<br>
 * Original Jython script by Polo, BiTi and DrLecter.
 * @author DS
 */
public final class Q00636_TruthBeyond extends Quest
{
	private static final int ELIYAH = 31329;
	private static final int FLAURON = 32010;
	private static final int ZONE = 30100;
	private static final int VISITOR_MARK = 8064;
	private static final int FADED_MARK = 8065;
	private static final int MARK = 8067;
	
	public Q00636_TruthBeyond()
	{
		super(636, Q00636_TruthBeyond.class.getSimpleName(), "The Truth Beyond the Gate");
		addStartNpc(ELIYAH);
		addTalkId(ELIYAH, FLAURON);
		addEnterZoneId(ZONE);
	}
	
	@Override
	public final String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return null;
		}
		
		if ("31329-04.htm".equals(event))
		{
			st.startQuest();
		}
		else if ("32010-02.htm".equals(event))
		{
			st.giveItems(VISITOR_MARK, 1);
			st.exitQuest(true, true);
		}
		return event;
	}
	
	@Override
	public final String onEnterZone(L2Character character, L2ZoneType zone)
	{
		// QuestState already null on enter because quest is finished
		if (character.isPlayer())
		{
			if (character.getActingPlayer().destroyItemByItemId("Mark", VISITOR_MARK, 1, character, false))
			{
				character.getActingPlayer().addItem("Mark", FADED_MARK, 1, character, true);
			}
		}
		return null;
	}
	
	@Override
	public final String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, true);
		if (st == null)
		{
			return getNoQuestMsg(player);
		}
		
		if (npc.getId() == ELIYAH)
		{
			if (st.hasQuestItems(VISITOR_MARK) || st.hasQuestItems(FADED_MARK) || st.hasQuestItems(MARK))
			{
				st.exitQuest(true);
				return "31329-mark.htm";
			}
			if (st.getState() == State.CREATED)
			{
				if (player.getLevel() > 72)
				{
					return "31329-02.htm";
				}
				
				st.exitQuest(true);
				return "31329-01.htm";
			}
			else if (st.getState() == State.STARTED)
			{
				return "31329-05.htm";
			}
		}
		else if (st.getState() == State.STARTED) // Flauron only
		{
			if (st.isCond(1))
			{
				return "32010-01.htm";
			}
			st.exitQuest(true);
			return "32010-03.htm";
		}
		return getNoQuestMsg(player);
	}
}

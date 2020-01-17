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
package quests.Q00639_GuardiansOfTheHolyGrail;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;

/**
 * Guardians of the Holy Grail (639)<br>
 * NOTE: This quest is no longer available since Freya(CT2.5)
 * @author corbin12
 */
public final class Q00639_GuardiansOfTheHolyGrail extends Quest
{
	// NPC
	private static final int DOMINIC = 31350;
	
	public Q00639_GuardiansOfTheHolyGrail()
	{
		super(639, Q00639_GuardiansOfTheHolyGrail.class.getSimpleName(), "Guardians of the Holy Grail");
		addStartNpc(DOMINIC);
		addTalkId(DOMINIC);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, true);
		if (st != null)
		{
			st.exitQuest(true);
		}
		return "31350-01.html";
	}
}

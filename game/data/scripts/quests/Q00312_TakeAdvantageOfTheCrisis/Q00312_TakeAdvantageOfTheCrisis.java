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
package quests.Q00312_TakeAdvantageOfTheCrisis;

import java.util.HashMap;
import java.util.Map;

import l2r.gameserver.enums.QuestSound;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;

/**
 * Take Advantage of the Crisis! (312)
 * @author malyelfik
 */
public class Q00312_TakeAdvantageOfTheCrisis extends Quest
{
	// NPC
	private static final int FILAUR = 30535;
	// Monsters
	private static final Map<Integer, Integer> MOBS = new HashMap<>();
	
	static
	{
		MOBS.put(22678, 291); // Grave Robber Summoner (Lunatic)
		MOBS.put(22679, 596); // Grave Robber Magician (Lunatic)
		MOBS.put(22680, 610); // Grave Robber Worker (Lunatic)
		MOBS.put(22681, 626); // Grave Robber Warrior (Lunatic)
		MOBS.put(22682, 692); // Grave Robber Warrior of Light (Lunatic)
		MOBS.put(22683, 650); // Servitor of Darkness
		MOBS.put(22684, 310); // Servitor of Darkness
		MOBS.put(22685, 626); // Servitor of Darkness
		MOBS.put(22686, 626); // Servitor of Darkness
		MOBS.put(22687, 308); // Phantoms of the Mine
		MOBS.put(22688, 416); // Evil Spirits of the Mine
		MOBS.put(22689, 212); // Mine Bug
		MOBS.put(22690, 748); // Earthworm's Descendant
	}
	
	// Item
	private static final int MINERAL_FRAGMENT = 14875;
	// Misc
	private static final int MIN_LEVEL = 80;
	
	public Q00312_TakeAdvantageOfTheCrisis()
	{
		super(312, Q00312_TakeAdvantageOfTheCrisis.class.getSimpleName(), "Take Advantage of the Crisis!");
		addStartNpc(FILAUR);
		addTalkId(FILAUR);
		addKillId(MOBS.keySet());
		registerQuestItems(MINERAL_FRAGMENT);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return null;
		}
		
		String htmltext = event;
		switch (event)
		{
			case "30535-02.html":
			case "30535-03.html":
			case "30535-04.html":
			case "30535-05.htm":
			case "30535-09.html":
			case "30535-10.html":
				break;
			case "30535-06.htm":
				st.startQuest();
				break;
			case "30535-11.html":
				st.exitQuest(true, true);
				break;
			default:
				htmltext = null;
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		final L2PcInstance member = getRandomPartyMember(player, 1);
		if ((member != null) && (getRandom(1000) < MOBS.get(npc.getId())))
		{
			final QuestState st = member.getQuestState(getName());
			st.giveItems(MINERAL_FRAGMENT, 1);
			st.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
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
				htmltext = (player.getLevel() >= MIN_LEVEL) ? "30535-01.htm" : "30535-00.htm";
				break;
			case State.STARTED:
				htmltext = (st.hasQuestItems(MINERAL_FRAGMENT)) ? "30535-08.html" : "30535-07.html";
				break;
		}
		return htmltext;
	}
}

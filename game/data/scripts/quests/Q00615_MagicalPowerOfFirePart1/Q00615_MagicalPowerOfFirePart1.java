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
package quests.Q00615_MagicalPowerOfFirePart1;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.holders.SkillHolder;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;
import l2r.gameserver.network.NpcStringId;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.NpcSay;

/**
 * Magical Power of Fire - Part 1 (615)
 * @author Joxit
 */
public class Q00615_MagicalPowerOfFirePart1 extends Quest
{
	// NPCs
	private static final int NARAN = 31378;
	private static final int UDAN = 31379;
	private static final int ASEFA_BOX = 31559;
	private static final int ASEFA_EYE = 31684;
	// Monsters
	private static final int[] KETRA_MOBS =
	{
		21324, // Ketra Orc Footman
		21325, // Ketra's War Hound
		21327, // Ketra Orc Raider
		21328, // Ketra Orc Scout
		21329, // Ketra Orc Shaman
		21331, // Ketra Orc Warrior
		21332, // Ketra Orc Lieutenant
		21334, // Ketra Orc Medium
		21335, // Ketra Orc Elite Soldier
		21336, // Ketra Orc White Captain
		21338, // Ketra Orc Seer
		21339, // Ketra Orc General
		21340, // Ketra Orc Battalion Commander
		21342, // Ketra Orc Grand Seer
		21343, // Ketra Commander
		21344, // Ketra Elite Guard
		21345, // Ketra's Head Shaman
		21346, // Ketra's Head Guard
		21347, // Ketra Prophet
		21348, // Prophet's Guard
		21349, // Prophet's Aide
	};
	// Items
	private static final int KEY = 1661;
	private static final int STOLEN_RED_TOTEM = 7242;
	private static final int WISDOM_STONE = 7081;
	private static final int RED_TOTEM = 7243;
	private static final int[] VARKA_MARKS =
	{
		7221, // Mark of Varka's Alliance - Level 1
		7222, // Mark of Varka's Alliance - Level 2
		7223, // Mark of Varka's Alliance - Level 3
		7224, // Mark of Varka's Alliance - Level 4
		7225, // Mark of Varka's Alliance - Level 5
	};
	// Skills
	private static SkillHolder GOW = new SkillHolder(4547, 1); // Gaze of Watcher
	private static SkillHolder DISPEL_GOW = new SkillHolder(4548, 1); // Quest - Dispel Watcher Gaze
	// Misc
	private static final int MIN_LEVEL = 74;
	
	public Q00615_MagicalPowerOfFirePart1()
	{
		super(615, Q00615_MagicalPowerOfFirePart1.class.getSimpleName(), "Magical Power of Fire - Part 1");
		addStartNpc(NARAN);
		addTalkId(UDAN, NARAN, ASEFA_BOX);
		addAttackId(KETRA_MOBS);
		registerQuestItems(STOLEN_RED_TOTEM);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return null;
		}
		String htmltext = null;
		switch (event)
		{
			case "31378-02.html":
				st.startQuest();
				htmltext = event;
				break;
			case "open_box":
				if (!st.hasQuestItems(KEY))
				{
					htmltext = "31559-02.html";
				}
				else if (st.isCond(2))
				{
					if (st.isSet("spawned"))
					{
						st.takeItems(KEY, 1);
						htmltext = "31559-04.html";
					}
					else
					{
						st.giveItems(STOLEN_RED_TOTEM, 1);
						st.takeItems(KEY, 1);
						st.setCond(3, true);
						htmltext = "31559-03.html";
					}
				}
				break;
			case "eye_despawn":
				npc.broadcastPacket(new NpcSay(npc, Say2.NPC_ALL, NpcStringId.ASEFA_HAS_ALREADY_SEEN_YOUR_FACE));
				npc.deleteMe();
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		final QuestState st = attacker.getQuestState(getName());
		if ((st != null) && st.isCond(2) && !st.isSet("spawned"))
		{
			st.set("spawned", "1");
			npc.setTarget(attacker);
			npc.doCast(GOW.getSkill());
			final L2Npc eye = addSpawn(ASEFA_EYE, npc);
			eye.broadcastPacket(new NpcSay(eye, Say2.NPC_ALL, NpcStringId.YOU_CANT_AVOID_THE_EYES_OF_ASEFA));
			startQuestTimer("eye_despawn", 10000, eye, attacker);
		}
		return super.onAttack(npc, attacker, damage, isSummon);
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
			case NARAN:
				switch (st.getState())
				{
					case State.CREATED:
						htmltext = (player.getLevel() >= MIN_LEVEL) ? (hasAtLeastOneQuestItem(player, VARKA_MARKS)) ? "31378-01.htm" : "31378-00a.html" : "31378-00b.html";
						break;
					case State.STARTED:
						if (st.isCond(1))
						{
							htmltext = "31378-03.html";
						}
						break;
				}
				break;
			case UDAN:
				if (st.isStarted())
				{
					switch (st.getCond())
					{
						case 1:
							htmltext = "31379-01.html";
							st.setCond(2, true);
							break;
						case 2:
							if (st.isSet("spawned"))
							{
								st.unset("spawned");
								npc.setTarget(player);
								npc.doCast(DISPEL_GOW.getSkill());
								htmltext = "31379-03.html";
							}
							else
							{
								htmltext = "31379-02.html";
							}
							break;
						case 3:
							st.giveItems(RED_TOTEM, 1);
							st.giveItems(WISDOM_STONE, 1);
							st.exitQuest(true, true);
							htmltext = "31379-04.html";
							break;
					}
				}
				break;
			case ASEFA_BOX:
				if (st.isCond(2))
				{
					htmltext = "31559-01.html";
				}
				break;
		}
		return htmltext;
	}
}

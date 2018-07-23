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
package ai.npc.Selina;

import java.util.HashMap;
import java.util.Map;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.holders.SkillHolder;
import l2r.gameserver.model.skills.L2Skill;

import ai.npc.AbstractNpcAI;

/**
 * Mercenary Medic Selina AI.
 * @author Zoey76
 */
public final class Selina extends AbstractNpcAI
{
	// NPC
	private static final int SELINA = 31556;
	// Items
	private static final int GOLDEN_RAM_BADGE_RECRUIT = 7246;
	private static final int GOLDEN_RAM_BADGE_SOLDIER = 7247;
	private static final int GOLDEN_RAM_COIN = 7251;
	// Skills
	private static final Map<String, BuffHolder> BUFFS = new HashMap<>();
	
	static
	{
		BUFFS.put("4359", new BuffHolder(4359, 2, 2)); // Focus
		BUFFS.put("4360", new BuffHolder(4360, 2, 2)); // Death Whisper
		BUFFS.put("4345", new BuffHolder(4345, 3, 3)); // Might
		BUFFS.put("4355", new BuffHolder(4355, 2, 3)); // Acumen
		BUFFS.put("4352", new BuffHolder(4352, 1, 3)); // Berserker Spirit
		BUFFS.put("4354", new BuffHolder(4354, 2, 3)); // Vampiric Rage
		BUFFS.put("4356", new BuffHolder(4356, 1, 6)); // Empower
		BUFFS.put("4357", new BuffHolder(4357, 2, 6)); // Haste
	}
	
	public Selina()
	{
		super(Selina.class.getSimpleName(), "ai/npc");
		addStartNpc(SELINA);
		addTalkId(SELINA);
		addFirstTalkId(SELINA);
		addSpellFinishedId(SELINA);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final BuffHolder buff = BUFFS.get(event);
		if (buff != null)
		{
			if ((getQuestItemsCount(player, GOLDEN_RAM_COIN) >= buff.getCost()))
			{
				castSkill(npc, player, buff);
				return super.onAdvEvent(event, npc, player);
			}
		}
		else
		{
			_log.warn(Selina.class.getSimpleName() + " AI: player " + player + " sent invalid bypass: " + event);
		}
		return "31556-02.html";
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		final String htmltext;
		if (hasQuestItems(player, GOLDEN_RAM_BADGE_SOLDIER))
		{
			htmltext = "31556-08.html";
		}
		else if (hasQuestItems(player, GOLDEN_RAM_BADGE_RECRUIT))
		{
			htmltext = "31556-01.html";
		}
		else
		{
			htmltext = "31556-09.html";
		}
		return htmltext;
	}
	
	@Override
	public String onSpellFinished(L2Npc npc, L2PcInstance player, L2Skill skill)
	{
		final BuffHolder buff = BUFFS.get(Integer.toString(skill.getId()));
		if (buff != null)
		{
			takeItems(player, GOLDEN_RAM_COIN, buff.getCost());
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	private static class BuffHolder extends SkillHolder
	{
		private final int _cost;
		
		public BuffHolder(int skillId, int skillLvl, int cost)
		{
			super(skillId, skillLvl);
			_cost = cost;
		}
		
		public int getCost()
		{
			return _cost;
		}
	}
}

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
package ai.npc.ForgeOfTheGods;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.effects.L2Effect;
import l2r.gameserver.model.holders.SkillHolder;
import l2r.gameserver.model.skills.L2Skill;

import ai.npc.AbstractNpcAI;

/**
 * Tar Beetle AI
 * @author nonom, malyelfik
 */
public final class TarBeetle extends AbstractNpcAI
{
	// NPC
	private static final int TAR_BEETLE = 18804;
	// Skills
	private static final int TAR_SPITE = 6142;
	private static SkillHolder[] SKILLS =
	{
		new SkillHolder(TAR_SPITE, 1),
		new SkillHolder(TAR_SPITE, 2),
		new SkillHolder(TAR_SPITE, 3)
	};
	
	private static final TarBeetleSpawn spawn = new TarBeetleSpawn();
	
	public TarBeetle()
	{
		super(TarBeetle.class.getSimpleName(), "ai/npc");
		addAggroRangeEnterId(TAR_BEETLE);
		addSpellFinishedId(TAR_BEETLE);
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		if (npc.getScriptValue() > 0)
		{
			final L2Effect effect = player.getFirstEffect(TAR_SPITE);
			final int level = (effect != null) ? effect.getSkill().getAbnormalLvl() : 0;
			
			if (level < 3)
			{
				final L2Skill skill = SKILLS[level].getSkill();
				if (!npc.isSkillDisabled(skill))
				{
					npc.setTarget(player);
					npc.doCast(skill);
				}
			}
		}
		return super.onAggroRangeEnter(npc, player, isSummon);
	}
	
	@Override
	public String onSpellFinished(L2Npc npc, L2PcInstance player, L2Skill skill)
	{
		if ((skill != null) && (skill.getId() == TAR_SPITE))
		{
			final int val = npc.getScriptValue() - 1;
			if ((val <= 0) || (SKILLS[0].getSkill().getMpConsume() > npc.getCurrentMp()))
			{
				spawn.removeBeetle(npc);
			}
			else
			{
				npc.setScriptValue(val);
			}
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	@Override
	public boolean unload()
	{
		spawn.unload();
		return super.unload();
	}
}
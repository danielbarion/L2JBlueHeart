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
package ai.group_template;

import l2r.gameserver.model.actor.L2Attackable;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.skills.L2Skill;

import ai.npc.AbstractNpcAI;

/**
 * Frozen Labyrinth AI.
 * @author malyelfik
 */
public final class FrozenLabyrinth extends AbstractNpcAI
{
	// Monsters
	private static final int PRONGHORN_SPIRIT = 22087;
	private static final int PRONGHORN = 22088;
	private static final int LOST_BUFFALO = 22093;
	private static final int FROST_BUFFALO = 22094;
	
	public FrozenLabyrinth()
	{
		super(FrozenLabyrinth.class.getSimpleName(), "ai/group_template");
		addAttackId(PRONGHORN, FROST_BUFFALO);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon, L2Skill skill)
	{
		if (npc.isScriptValue(0) && (skill != null) && !skill.isMagic())
		{
			final int spawnId = (npc.getId() == PRONGHORN) ? PRONGHORN_SPIRIT : LOST_BUFFALO;
			int diff = 0;
			for (int i = 0; i < 6; i++)
			{
				final int x = diff < 60 ? npc.getX() + diff : npc.getX();
				final int y = diff >= 60 ? npc.getY() + (diff - 40) : npc.getY();
				
				final L2Attackable monster = (L2Attackable) addSpawn(spawnId, x, y, npc.getZ(), npc.getHeading(), false, 0);
				attackPlayer(monster, attacker);
				diff += 20;
			}
			npc.setScriptValue(1);
			npc.deleteMe();
		}
		return super.onAttack(npc, attacker, damage, isSummon, skill);
	}
}
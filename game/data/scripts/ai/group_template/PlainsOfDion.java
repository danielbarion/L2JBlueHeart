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

import l2r.gameserver.GeoData;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2MonsterInstance;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.NpcStringId;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.util.Util;

import ai.npc.AbstractNpcAI;

/**
 * AI for mobs in Plains of Dion (near Floran Village).
 * @author Gladicek
 */
public final class PlainsOfDion extends AbstractNpcAI
{
	private static final int DELU_LIZARDMEN[] =
	{
		21104, // Delu Lizardman Supplier
		21105, // Delu Lizardman Special Agent
		21107, // Delu Lizardman Commander
	};
	
	private static final NpcStringId[] MONSTERS_MSG =
	{
		NpcStringId.S1_HOW_DARE_YOU_INTERRUPT_OUR_FIGHT_HEY_GUYS_HELP,
		NpcStringId.S1_HEY_WERE_HAVING_A_DUEL_HERE,
		NpcStringId.THE_DUEL_IS_OVER_ATTACK,
		NpcStringId.FOUL_KILL_THE_COWARD,
		NpcStringId.HOW_DARE_YOU_INTERRUPT_A_SACRED_DUEL_YOU_MUST_BE_TAUGHT_A_LESSON
	};
	
	private static final NpcStringId[] MONSTERS_ASSIST_MSG =
	{
		NpcStringId.DIE_YOU_COWARD,
		NpcStringId.KILL_THE_COWARD,
		NpcStringId.WHAT_ARE_YOU_LOOKING_AT
	};
	
	public PlainsOfDion()
	{
		super(PlainsOfDion.class.getSimpleName(), "ai/group_template");
		addAttackId(DELU_LIZARDMEN);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isSummon)
	{
		if (npc.isScriptValue(0))
		{
			int i = getRandom(5);
			if (i < 2)
			{
				broadcastNpcSay(npc, Say2.NPC_ALL, MONSTERS_MSG[i], player.getName());
			}
			else
			{
				broadcastNpcSay(npc, Say2.NPC_ALL, MONSTERS_MSG[i]);
			}
			
			for (L2Character obj : npc.getKnownList().getKnownCharactersInRadius(npc.getFactionRange()))
			{
				if (obj.isMonster() && Util.contains(DELU_LIZARDMEN, ((L2MonsterInstance) obj).getId()) && !obj.isAttackingNow() && !obj.isDead() && GeoData.getInstance().canSeeTarget(npc, obj))
				{
					final L2MonsterInstance monster = (L2MonsterInstance) obj;
					attackPlayer(monster, player);
					broadcastNpcSay(monster, Say2.NPC_ALL, MONSTERS_ASSIST_MSG[getRandom(3)]);
				}
			}
			npc.setScriptValue(1);
		}
		return super.onAttack(npc, player, damage, isSummon);
	}
}
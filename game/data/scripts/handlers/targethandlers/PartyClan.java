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
package handlers.targethandlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import l2r.gameserver.handler.ITargetTypeHandler;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.model.skills.targets.L2TargetType;

/**
 * @author UnAfraid
 */
public class PartyClan implements ITargetTypeHandler
{
	@Override
	public L2Object[] getTargetList(L2Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		List<L2Character> targetList = new ArrayList<>();
		if (onlyFirst)
		{
			return new L2Character[]
			{
				activeChar
			};
		}
		
		final L2PcInstance player = activeChar.getActingPlayer();
		
		if (player == null)
		{
			return _emptyTargetList;
		}
		
		targetList.add(player);
		
		final int radius = skill.getAffectRange();
		final boolean hasClan = player.getClan() != null;
		final boolean hasParty = player.isInParty();
		
		if (L2Skill.addSummon(activeChar, player, radius, false))
		{
			targetList.add(player.getSummon());
		}
		
		// if player in clan and not in party
		if (!(hasClan || hasParty))
		{
			return targetList.toArray(new L2Character[targetList.size()]);
		}
		
		// Get all visible objects in a spherical area near the L2Character
		final Collection<L2PcInstance> objs = activeChar.getKnownList().getKnownPlayersInRadius(radius);
		int maxTargets = skill.getAffectLimit();
		for (L2PcInstance obj : objs)
		{
			if (obj == null)
			{
				continue;
			}
			
			// olympiad mode - adding only own side
			if (player.isInOlympiadMode())
			{
				if (!obj.isInOlympiadMode())
				{
					continue;
				}
				if (player.getOlympiadGameId() != obj.getOlympiadGameId())
				{
					continue;
				}
				if (player.getOlympiadSide() != obj.getOlympiadSide())
				{
					continue;
				}
			}
			
			if (player.isInDuel())
			{
				if (player.getDuelId() != obj.getDuelId())
				{
					continue;
				}
				
				if (hasParty && obj.isInParty() && (player.getParty().getLeaderObjectId() != obj.getParty().getLeaderObjectId()))
				{
					continue;
				}
			}
			
			if (!((hasClan && (obj.getClanId() == player.getClanId())) || (hasParty && obj.isInParty() && (player.getParty().getLeaderObjectId() == obj.getParty().getLeaderObjectId()))))
			{
				continue;
			}
			
			// Don't add this target if this is a Pc->Pc pvp
			// casting and pvp condition not met
			if (!player.checkPvpSkill(obj, skill))
			{
				continue;
			}
			
			if (!onlyFirst && L2Skill.addSummon(activeChar, obj, radius, false))
			{
				targetList.add(obj.getSummon());
			}
			
			if (!L2Skill.addCharacter(activeChar, obj, radius, false))
			{
				continue;
			}
			
			if (onlyFirst)
			{
				return new L2Character[]
				{
					obj
				};
			}
			
			if ((maxTargets > 0) && (targetList.size() >= maxTargets))
			{
				break;
			}
			
			targetList.add(obj);
		}
		return targetList.toArray(new L2Character[targetList.size()]);
	}
	
	@Override
	public Enum<L2TargetType> getTargetType()
	{
		return L2TargetType.PARTY_CLAN;
	}
}

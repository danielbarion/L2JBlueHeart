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

import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.handler.ITargetTypeHandler;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.model.skills.targets.L2TargetType;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.util.Util;

/**
 * @author UnAfraid
 */
public class AreaCorpseMob implements ITargetTypeHandler
{
	@Override
	public L2Object[] getTargetList(L2Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		List<L2Character> targetList = new ArrayList<>();
		if (target == null)
		{
			activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
			return _emptyTargetList;
		}
		if (!target.isAttackable() || !target.isDead())
		{
			activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
			return _emptyTargetList;
		}
		
		if (onlyFirst)
		{
			return new L2Character[]
			{
				target
			};
		}
		
		targetList.add(target);
		
		final boolean srcInArena = (activeChar.isInsideZone(ZoneIdType.PVP) && !activeChar.isInsideZone(ZoneIdType.SIEGE));
		
		final Collection<L2Character> objs = activeChar.getKnownList().getKnownCharacters();
		for (L2Character obj : objs)
		{
			if (!(obj.isAttackable() || obj.isPlayable()) || !Util.checkIfInRange(skill.getAffectRange(), target, obj, true))
			{
				continue;
			}
			
			if (!L2Skill.checkForAreaOffensiveSkills(activeChar, obj, skill, srcInArena))
			{
				continue;
			}
			
			targetList.add(obj);
		}
		
		if (targetList.isEmpty())
		{
			return _emptyTargetList;
		}
		return targetList.toArray(new L2Character[targetList.size()]);
	}
	
	@Override
	public Enum<L2TargetType> getTargetType()
	{
		return L2TargetType.AREA_CORPSE_MOB;
	}
}

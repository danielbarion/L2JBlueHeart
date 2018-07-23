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
package handlers.effecthandlers;

import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.effects.EffectTemplate;
import l2r.gameserver.model.effects.L2Effect;
import l2r.gameserver.model.stats.Env;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.SystemMessage;

/**
 * Cp Heal Percent effect implementation.
 * @author UnAfraid
 */
public class CpHealPercent extends L2Effect
{
	public CpHealPercent(Env env, EffectTemplate template)
	{
		super(env, template);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public boolean onStart()
	{
		L2Character target = getEffected();
		if ((target == null) || target.isDead() || target.isDoor())
		{
			return false;
		}
		
		double amount = 0;
		double power = calc();
		boolean full = (power == 100.0);
		
		amount = full ? target.getMaxCp() : (target.getMaxCp() * power) / 100.0;
		// Prevents overheal and negative amount
		amount = Math.max(Math.min(amount, target.getMaxRecoverableCp() - target.getCurrentCp()), 0);
		if (amount != 0)
		{
			target.setCurrentCp(amount + target.getCurrentCp());
		}
		SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CP_WILL_BE_RESTORED);
		sm.addInt((int) amount);
		target.sendPacket(sm);
		return true;
	}
}

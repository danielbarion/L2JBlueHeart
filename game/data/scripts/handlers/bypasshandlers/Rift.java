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
package handlers.bypasshandlers;

import l2r.gameserver.handler.IBypassHandler;
import l2r.gameserver.instancemanager.DimensionalRiftManager;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;

public class Rift implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"enterrift",
		"changeriftroom",
		"exitrift"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		if (!target.isNpc())
		{
			return false;
		}
		
		if (command.toLowerCase().startsWith(COMMANDS[0])) // EnterRift
		{
			try
			{
				Byte b1 = Byte.parseByte(command.substring(10)); // Selected Area: Recruit, Soldier etc
				DimensionalRiftManager.getInstance().start(activeChar, b1, (L2Npc) target);
				return true;
			}
			catch (Exception e)
			{
				_log.warn("Exception in " + getClass().getSimpleName(), e);
			}
		}
		else
		{
			final boolean inRift = activeChar.isInParty() && activeChar.getParty().isInDimensionalRift();
			
			if (command.toLowerCase().startsWith(COMMANDS[1])) // ChangeRiftRoom
			{
				if (inRift)
				{
					activeChar.getParty().getDimensionalRift().manualTeleport(activeChar, (L2Npc) target);
				}
				else
				{
					DimensionalRiftManager.getInstance().handleCheat(activeChar, (L2Npc) target);
				}
				
				return true;
			}
			else if (command.toLowerCase().startsWith(COMMANDS[2])) // ExitRift
			{
				if (inRift)
				{
					activeChar.getParty().getDimensionalRift().manualExitRift(activeChar, (L2Npc) target);
				}
				else
				{
					DimensionalRiftManager.getInstance().handleCheat(activeChar, (L2Npc) target);
				}
				
			}
			return true;
		}
		return false;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}

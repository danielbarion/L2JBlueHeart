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

import l2r.gameserver.data.xml.impl.MultisellData;
import l2r.gameserver.handler.IBypassHandler;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;

public class Multisell implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"multisell",
		"exc_multisell"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		if (!target.isNpc())
		{
			return false;
		}
		
		try
		{
			int listId;
			if (command.toLowerCase().startsWith(COMMANDS[0])) // multisell
			{
				listId = Integer.parseInt(command.substring(9).trim());
				MultisellData.getInstance().separateAndSend(listId, activeChar, (L2Npc) target, false);
				return true;
			}
			else if (command.toLowerCase().startsWith(COMMANDS[1])) // exc_multisell
			{
				listId = Integer.parseInt(command.substring(13).trim());
				MultisellData.getInstance().separateAndSend(listId, activeChar, (L2Npc) target, true);
				return true;
			}
			return false;
		}
		catch (Exception e)
		{
			_log.warn("Exception in " + getClass().getSimpleName(), e);
		}
		return false;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}

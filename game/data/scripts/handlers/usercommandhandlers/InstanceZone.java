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
package handlers.usercommandhandlers;

import java.util.Map;

import l2r.gameserver.handler.IUserCommandHandler;
import l2r.gameserver.instancemanager.InstanceManager;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.instancezone.InstanceWorld;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.SystemMessage;

/**
 * Instance Zone user command.
 * @author nille02
 */
public class InstanceZone implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		114
	};
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		if (id != COMMAND_IDS[0])
		{
			return false;
		}
		
		final InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(activeChar);
		if ((world != null) && (world.getTemplateId() >= 0))
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.INSTANT_ZONE_CURRENTLY_INUSE_S1);
			sm.addInstanceName(world.getTemplateId());
			activeChar.sendPacket(sm);
		}
		
		final Map<Integer, Long> instanceTimes = InstanceManager.getInstance().getAllInstanceTimes(activeChar.getObjectId());
		boolean firstMessage = true;
		if (instanceTimes != null)
		{
			for (int instanceId : instanceTimes.keySet())
			{
				long remainingTime = (instanceTimes.get(instanceId) - System.currentTimeMillis()) / 1000;
				if (remainingTime > 60)
				{
					if (firstMessage)
					{
						firstMessage = false;
						activeChar.sendPacket(SystemMessageId.INSTANCE_ZONE_TIME_LIMIT);
					}
					int hours = (int) (remainingTime / 3600);
					int minutes = (int) ((remainingTime % 3600) / 60);
					SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.AVAILABLE_AFTER_S1_S2_HOURS_S3_MINUTES);
					sm.addInstanceName(instanceId);
					sm.addInt(hours);
					sm.addInt(minutes);
					activeChar.sendPacket(sm);
				}
				else
				{
					InstanceManager.getInstance().deleteInstanceTime(activeChar.getObjectId(), instanceId);
				}
			}
		}
		if (firstMessage)
		{
			activeChar.sendPacket(SystemMessageId.NO_INSTANCEZONE_TIME_LIMIT);
		}
		return true;
	}
}

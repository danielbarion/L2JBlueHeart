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
package handlers.usercommandhandlers;

import l2r.gameserver.handler.IUserCommandHandler;
import l2r.gameserver.model.L2Party;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.SystemMessage;

/**
 * Party Info user command.
 * @author Tempy
 */
public class PartyInfo implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		81
	};
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		if (id != COMMAND_IDS[0])
		{
			return false;
		}
		
		activeChar.sendPacket(SystemMessageId.PARTY_INFORMATION);
		if (activeChar.isInParty())
		{
			final L2Party party = activeChar.getParty();
			switch (party.getDistributionType())
			{
				case FINDERS_KEEPERS:
					activeChar.sendPacket(SystemMessageId.LOOTING_FINDERS_KEEPERS);
					break;
				case RANDOM:
					activeChar.sendPacket(SystemMessageId.LOOTING_RANDOM);
					break;
				case RANDOM_INCLUDING_SPOIL:
					activeChar.sendPacket(SystemMessageId.LOOTING_RANDOM_INCLUDE_SPOIL);
					break;
				case BY_TURN:
					activeChar.sendPacket(SystemMessageId.LOOTING_BY_TURN);
					break;
				case BY_TURN_INCLUDING_SPOIL:
					activeChar.sendPacket(SystemMessageId.LOOTING_BY_TURN_INCLUDE_SPOIL);
					break;
			}
			
			if (!party.isLeader(activeChar))
			{
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.PARTY_LEADER_C1);
				sm.addPcName(party.getLeader());
				activeChar.sendPacket(sm);
			}
			activeChar.sendMessage("Members: " + party.getMemberCount() + "/9"); // TODO: Custom?
		}
		activeChar.sendPacket(SystemMessageId.FRIEND_LIST_FOOTER);
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}

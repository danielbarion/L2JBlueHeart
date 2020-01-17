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
package ai.npc.CastleSiegeManager;

import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;

import ai.npc.AbstractNpcAI;

/**
 * Castle Siege Manager AI.
 * @author St3eT
 */
public final class CastleSiegeManager extends AbstractNpcAI
{
	// NPCs
	private static final int[] SIEGE_MANAGER =
	{
		35104, // Gludio Castle
		35146, // Dion Castle
		35188, // Giran Castle
		35232, // Oren Castle
		35278, // Aden Castle
		35320, // Innadril Castle
		35367, // Goddard Castle
		35513, // Rune Castle
		35559, // Schuttgard Castle
		35639, // Fortress of the Dead
		35420, // Devastated Castle
	};
	
	public CastleSiegeManager()
	{
		super(CastleSiegeManager.class.getSimpleName(), "ai/npc");
		addFirstTalkId(SIEGE_MANAGER);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		if (player.isClanLeader() && (player.getClanId() == npc.getCastle().getOwnerId()))
		{
			if (isInSiege(npc))
			{
				htmltext = "CastleSiegeManager.html";
			}
			else
			{
				htmltext = "CastleSiegeManager-01.html";
			}
		}
		else if (isInSiege(npc))
		{
			htmltext = "CastleSiegeManager-02.html";
		}
		else
		{
			if (npc.getConquerableHall() != null)
			{
				npc.getConquerableHall().showSiegeInfo(player);
			}
			else
			{
				npc.getCastle().getSiege().listRegisterClan(player);
			}
		}
		return htmltext;
	}
	
	private boolean isInSiege(L2Npc npc)
	{
		if ((npc.getConquerableHall() != null) && npc.getConquerableHall().isInSiege())
		{
			return true;
		}
		else if (npc.getCastle().getSiege().isInProgress())
		{
			return true;
		}
		return false;
	}
}
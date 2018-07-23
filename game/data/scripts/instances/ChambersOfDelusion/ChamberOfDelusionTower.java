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
package instances.ChambersOfDelusion;

import l2r.gameserver.model.Location;

/**
 * Chamber of Delusion Tower.
 * @author GKR
 */
public final class ChamberOfDelusionTower extends Chamber
{
	// NPC's
	private static final int ENTRANCE_GATEKEEPER = 32663;
	private static final int ROOM_GATEKEEPER_FIRST = 32693;
	private static final int ROOM_GATEKEEPER_LAST = 32701;
	private static final int AENKINEL = 25695;
	private static final int BOX = 18823;
	
	// Misc
	private static final Location[] ENTER_POINTS = new Location[]
	{
		new Location(-108976, -153372, -6688),
		new Location(-108960, -152524, -6688),
		new Location(-107088, -155052, -6688),
		new Location(-107104, -154236, -6688),
		new Location(-108048, -151244, -6688),
		new Location(-107088, -152956, -6688),
		new Location(-108992, -154604, -6688),
		new Location(-108032, -152892, -6688),
		new Location(-108048, -154572, -6688), // Raid room
	};
	private static final int INSTANCEID = 132; // this is the client number
	private static final String INSTANCE_TEMPLATE = "ChamberOfDelusionTower.xml";
	
	public ChamberOfDelusionTower()
	{
		super(ChamberOfDelusionTower.class.getSimpleName(), "instances", INSTANCEID, INSTANCE_TEMPLATE, ENTRANCE_GATEKEEPER, ROOM_GATEKEEPER_FIRST, ROOM_GATEKEEPER_LAST, AENKINEL, BOX);
		ROOM_ENTER_POINTS = ENTER_POINTS;
	}
}
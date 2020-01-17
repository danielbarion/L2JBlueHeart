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
package vehicles;

import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.enums.audio.Sound;
import l2r.gameserver.instancemanager.BoatManager;
import l2r.gameserver.model.VehiclePathPoint;
import l2r.gameserver.model.actor.instance.L2BoatInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.CreatureSay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author DS
 */
public class BoatInnadrilTour implements Runnable
{
	private static final Logger _log = LoggerFactory.getLogger(BoatInnadrilTour.class);
	
	// Time: 1867s
	private static final VehiclePathPoint[] TOUR =
	{
		new VehiclePathPoint(105129, 226240, -3610, 150, 800),
		new VehiclePathPoint(90604, 238797, -3610, 150, 800),
		new VehiclePathPoint(74853, 237943, -3610, 150, 800),
		new VehiclePathPoint(68207, 235399, -3610, 150, 800),
		new VehiclePathPoint(63226, 230487, -3610, 150, 800),
		new VehiclePathPoint(61843, 224797, -3610, 150, 800),
		new VehiclePathPoint(61822, 203066, -3610, 150, 800),
		new VehiclePathPoint(59051, 197685, -3610, 150, 800),
		new VehiclePathPoint(54048, 195298, -3610, 150, 800),
		new VehiclePathPoint(41609, 195687, -3610, 150, 800),
		new VehiclePathPoint(35821, 200284, -3610, 150, 800),
		new VehiclePathPoint(35567, 205265, -3610, 150, 800),
		new VehiclePathPoint(35617, 222471, -3610, 150, 800),
		new VehiclePathPoint(37932, 226588, -3610, 150, 800),
		new VehiclePathPoint(42932, 229394, -3610, 150, 800),
		new VehiclePathPoint(74324, 245231, -3610, 150, 800),
		new VehiclePathPoint(81872, 250314, -3610, 150, 800),
		new VehiclePathPoint(101692, 249882, -3610, 150, 800),
		new VehiclePathPoint(107907, 256073, -3610, 150, 800),
		new VehiclePathPoint(112317, 257133, -3610, 150, 800),
		new VehiclePathPoint(126273, 255313, -3610, 150, 800),
		new VehiclePathPoint(128067, 250961, -3610, 150, 800),
		new VehiclePathPoint(128520, 238249, -3610, 150, 800),
		new VehiclePathPoint(126428, 235072, -3610, 150, 800),
		new VehiclePathPoint(121843, 234656, -3610, 150, 800),
		new VehiclePathPoint(120096, 234268, -3610, 150, 800),
		new VehiclePathPoint(118572, 233046, -3610, 150, 800),
		new VehiclePathPoint(117671, 228951, -3610, 150, 800),
		new VehiclePathPoint(115936, 226540, -3610, 150, 800),
		new VehiclePathPoint(113628, 226240, -3610, 150, 800),
		new VehiclePathPoint(111300, 226240, -3610, 150, 800),
		new VehiclePathPoint(111264, 226240, -3610, 150, 800)
	};
	
	private static final VehiclePathPoint DOCK = TOUR[TOUR.length - 1];
	
	private final L2BoatInstance _boat;
	private int _cycle = 0;
	
	private final CreatureSay ARRIVED_AT_INNADRIL;
	private final CreatureSay LEAVE_INNADRIL5;
	private final CreatureSay LEAVE_INNADRIL1;
	private final CreatureSay LEAVE_INNADRIL0;
	private final CreatureSay LEAVING_INNADRIL;
	
	private final CreatureSay ARRIVAL20;
	private final CreatureSay ARRIVAL15;
	private final CreatureSay ARRIVAL10;
	private final CreatureSay ARRIVAL5;
	private final CreatureSay ARRIVAL1;
	
	public BoatInnadrilTour(L2BoatInstance boat)
	{
		_boat = boat;
		
		ARRIVED_AT_INNADRIL = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.INNADRIL_BOAT_ANCHOR_10_MINUTES);
		LEAVE_INNADRIL5 = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.INNADRIL_BOAT_LEAVE_IN_5_MINUTES);
		LEAVE_INNADRIL1 = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.INNADRIL_BOAT_LEAVE_IN_1_MINUTE);
		LEAVE_INNADRIL0 = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.INNADRIL_BOAT_LEAVE_SOON);
		LEAVING_INNADRIL = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.INNADRIL_BOAT_LEAVING);
		
		ARRIVAL20 = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.INNADRIL_BOAT_ARRIVE_20_MINUTES);
		ARRIVAL15 = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.INNADRIL_BOAT_ARRIVE_15_MINUTES);
		ARRIVAL10 = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.INNADRIL_BOAT_ARRIVE_10_MINUTES);
		ARRIVAL5 = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.INNADRIL_BOAT_ARRIVE_5_MINUTES);
		ARRIVAL1 = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.INNADRIL_BOAT_ARRIVE_1_MINUTE);
	}
	
	@Override
	public void run()
	{
		try
		{
			switch (_cycle)
			{
				case 0:
					BoatManager.getInstance().broadcastPacket(DOCK, DOCK, LEAVE_INNADRIL5);
					ThreadPoolManager.getInstance().scheduleGeneral(this, 240000);
					break;
				case 1:
					BoatManager.getInstance().broadcastPacket(DOCK, DOCK, LEAVE_INNADRIL1);
					ThreadPoolManager.getInstance().scheduleGeneral(this, 40000);
					break;
				case 2:
					BoatManager.getInstance().broadcastPacket(DOCK, DOCK, LEAVE_INNADRIL0);
					ThreadPoolManager.getInstance().scheduleGeneral(this, 20000);
					break;
				case 3:
					BoatManager.getInstance().broadcastPackets(DOCK, DOCK, LEAVING_INNADRIL, Sound.ITEMSOUND_SHIP_ARRIVAL_DEPARTURE.withObject(_boat));
					_boat.payForRide(0, 1, 107092, 219098, -3952);
					_boat.executePath(TOUR);
					ThreadPoolManager.getInstance().scheduleGeneral(this, 650000);
					break;
				case 4:
					BoatManager.getInstance().broadcastPacket(DOCK, DOCK, ARRIVAL20);
					ThreadPoolManager.getInstance().scheduleGeneral(this, 300000);
					break;
				case 5:
					BoatManager.getInstance().broadcastPacket(DOCK, DOCK, ARRIVAL15);
					ThreadPoolManager.getInstance().scheduleGeneral(this, 300000);
					break;
				case 6:
					BoatManager.getInstance().broadcastPacket(DOCK, DOCK, ARRIVAL10);
					ThreadPoolManager.getInstance().scheduleGeneral(this, 300000);
					break;
				case 7:
					BoatManager.getInstance().broadcastPacket(DOCK, DOCK, ARRIVAL5);
					ThreadPoolManager.getInstance().scheduleGeneral(this, 240000);
					break;
				case 8:
					BoatManager.getInstance().broadcastPacket(DOCK, DOCK, ARRIVAL1);
					break;
				case 9:
					BoatManager.getInstance().broadcastPackets(DOCK, DOCK, ARRIVED_AT_INNADRIL, Sound.ITEMSOUND_SHIP_ARRIVAL_DEPARTURE.withObject(_boat));
					ThreadPoolManager.getInstance().scheduleGeneral(this, 300000);
					break;
			}
			_cycle++;
			if (_cycle > 9)
			{
				_cycle = 0;
			}
		}
		catch (Exception e)
		{
			_log.warn(e.getMessage());
		}
	}
	
	public static void main(String[] args)
	{
		final L2BoatInstance boat = BoatManager.getInstance().getNewBoat(4, 111264, 226240, -3610, 32768);
		if (boat != null)
		{
			boat.registerEngine(new BoatInnadrilTour(boat));
			boat.runEngine(180000);
		}
	}
}

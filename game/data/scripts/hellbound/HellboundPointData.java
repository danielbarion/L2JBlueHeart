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
package hellbound;

import java.util.HashMap;
import java.util.Map;

import l2r.util.data.xml.IXmlReader.IXmlReader;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Point data parser.
 * @author Zoey76
 */
public final class HellboundPointData implements IXmlReader
{
	private final Map<Integer, int[]> _pointsInfo = new HashMap<>();
	
	public HellboundPointData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_pointsInfo.clear();
		parseDatapackFile("data/scripts/hellbound/hellboundTrustPoints.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _pointsInfo.size() + " trust point reward data.");
	}
	
	@Override
	public void parseDocument(Document doc)
	{
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equals(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					parsePoint(d);
				}
			}
		}
	}
	
	/**
	 * Parses the point.
	 * @param d the node to parse
	 */
	private void parsePoint(Node d)
	{
		if ("npc".equals(d.getNodeName()))
		{
			NamedNodeMap attrs = d.getAttributes();
			Node att = attrs.getNamedItem("id");
			if (att == null)
			{
				LOGGER.error(getClass().getSimpleName() + ": Missing NPC ID, skipping record!");
				return;
			}
			
			int npcId = Integer.parseInt(att.getNodeValue());
			
			att = attrs.getNamedItem("points");
			if (att == null)
			{
				LOGGER.error("[Hellbound Trust Points Info] Missing reward point info for NPC ID " + npcId + ", skipping record");
				return;
			}
			int points = Integer.parseInt(att.getNodeValue());
			
			att = attrs.getNamedItem("minHellboundLvl");
			if (att == null)
			{
				LOGGER.error("[Hellbound Trust Points Info] Missing minHellboundLvl info for NPC ID " + npcId + ", skipping record");
				return;
			}
			int minHbLvl = Integer.parseInt(att.getNodeValue());
			
			att = attrs.getNamedItem("maxHellboundLvl");
			if (att == null)
			{
				LOGGER.error("[Hellbound Trust Points Info] Missing maxHellboundLvl info for NPC ID " + npcId + ", skipping record");
				return;
			}
			int maxHbLvl = Integer.parseInt(att.getNodeValue());
			
			att = attrs.getNamedItem("lowestTrustLimit");
			final int lowestTrustLimit = (att == null) ? 0 : Integer.parseInt(att.getNodeValue());
			
			_pointsInfo.put(npcId, new int[]
			{
				points,
				minHbLvl,
				maxHbLvl,
				lowestTrustLimit
			});
		}
	}
	
	/**
	 * Gets all the points data.
	 * @return the points data
	 */
	public Map<Integer, int[]> getPointsInfo()
	{
		return _pointsInfo;
	}
	
	/**
	 * Gets the points amount for an specific NPC ID.
	 * @param npcId the NPC ID
	 * @return the points for an specific NPC ID
	 */
	public int getPointsAmount(int npcId)
	{
		return _pointsInfo.get(npcId)[0];
	}
	
	/**
	 * Get the minimum Hellbound level for the given NPC ID.
	 * @param npcId the NPC ID
	 * @return the minimum Hellbound level for the given NPC ID
	 */
	public int getMinHbLvl(int npcId)
	{
		return _pointsInfo.get(npcId)[1];
	}
	
	/**
	 * Get the maximum Hellbound level for the given NPC ID.
	 * @param npcId the NPC ID
	 * @return the maximum Hellbound level for the given NPC ID
	 */
	public int getMaxHbLvl(int npcId)
	{
		return _pointsInfo.get(npcId)[2];
	}
	
	/**
	 * Get the lowest trust limit for the given NPC ID.
	 * @param npcId the NPC ID
	 * @return the lowest trust limit for the given NPC ID
	 */
	public int getLowestTrustLimit(int npcId)
	{
		return _pointsInfo.get(npcId)[3];
	}
	
	public static HellboundPointData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final HellboundPointData INSTANCE = new HellboundPointData();
	}
}

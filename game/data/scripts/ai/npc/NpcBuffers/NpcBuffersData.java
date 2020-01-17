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
package ai.npc.NpcBuffers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import l2r.gameserver.model.StatsSet;
import l2r.util.data.xml.IXmlReader.IXmlReader;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author UnAfraid
 */
public class NpcBuffersData implements IXmlReader
{
	private final Map<Integer, NpcBufferData> _npcBuffers = new HashMap<>();
	
	protected NpcBuffersData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDatapackFile("data/scripts/ai/npc/NpcBuffers/NpcBuffersData.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded: " + _npcBuffers.size() + " buffers data.");
	}
	
	@Override
	public void parseDocument(Document doc)
	{
		StatsSet set;
		Node attr;
		NamedNodeMap attrs;
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("npc".equalsIgnoreCase(d.getNodeName()))
					{
						attrs = d.getAttributes();
						final int npcId = parseInteger(attrs, "id");
						final NpcBufferData npc = new NpcBufferData(npcId);
						for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling())
						{
							switch (c.getNodeName())
							{
								case "skill":
								{
									attrs = c.getAttributes();
									set = new StatsSet();
									for (int i = 0; i < attrs.getLength(); i++)
									{
										attr = attrs.item(i);
										set.set(attr.getNodeName(), attr.getNodeValue());
									}
									npc.addSkill(new NpcBufferSkillData(set));
									break;
								}
							}
						}
						_npcBuffers.put(npcId, npc);
					}
				}
			}
		}
	}
	
	public NpcBufferData getNpcBuffer(int npcId)
	{
		return _npcBuffers.get(npcId);
	}
	
	public Collection<NpcBufferData> getNpcBuffers()
	{
		return _npcBuffers.values();
	}
	
	public Set<Integer> getNpcBufferIds()
	{
		return _npcBuffers.keySet();
	}
}

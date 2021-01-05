package stocktales.factsheet.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.factsheet.interfaces.IFactSheetBufferSrv;
import stocktales.scripsEngine.uploadEngine.scDataContainer.DAO.types.scDataContainer;
import stocktales.scripsEngine.uploadEngine.scDataContainer.services.interfaces.ISCDataContainerSrv;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.ISCExistsDB_Srv;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.types.ScripSector;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class FactSheetBufferSrv implements IFactSheetBufferSrv
{
	@Autowired
	private ISCExistsDB_Srv scExSrv;
	
	@Autowired
	private ISCDataContainerSrv scDCSrv;
	
	@Autowired
	private MessageSource msgSrc;
	
	@Value("${NO_PEER}")
	private String noPeer_error;
	
	private List<scDataContainer> scripDataContainers;
	private List<String>          peerScripNames = new ArrayList<String>();
	private String                refScripName;
	private boolean               modeSolo;                                //No Peer Comparison needed
	
	@Override
	public scDataContainer getDataContainerforScrip(
	        String scCode
	)
	{
		scDataContainer scDC = null;
		
		if (this.scripDataContainers != null)
		{
			if (this.scripDataContainers.size() > 0)
			{
				Optional<scDataContainer> scDCO = scripDataContainers.stream()
				        .filter(x -> x.getSCRoot().getSCCode().equals(scCode)).findFirst();
				if (scDCO.isPresent())
				{
					scDC = scDCO.get();
				}
			}
		}
		
		return scDC;
	}
	
	@Override
	public void Initialize(
	        String scCode, boolean no_peers
	) throws Exception
	{
		if (scCode != null)
		{
			if (scCode.trim().length() > 0)
			{
				//Load the Containers
				load(scCode, null, false, no_peers);
			}
		}
		
	}
	
	@Override
	public void Initialize(
	        String scCode, List<String> peers
	) throws Exception
	{
		//Load the Containers
		load(scCode, peers, false, false);
		
	}
	
	@Override
	public void InitializeAndExclude(
	        String scCode, List<String> peersToExclude
	) throws Exception
	{
		//Load the Containers
		load(scCode, peersToExclude, true, false);
		
	}
	
	/*
	 * -----------------------------------------------------------------------------------------------
	 *                                  PRIVATE SECTION
	 * -----------------------------------------------------------------------------------------------                                  
	 */
	
	private void clear(
	)
	{
		this.setPeerScripNames(new ArrayList<String>());
		this.setScripDataContainers(null);
		this.setRefScripName(null);
	}
	
	private void load(
	        String scCode, List<String> peers, Boolean excludepeers, boolean no_peers
	) throws Exception
	{
		if (scExSrv != null)
		{
			//clear the Buffer
			clear();
			boolean loadAllPeers = false;
			
			//First Get the Sector of All Scrips
			List<ScripSector> scSecList    = scExSrv.getAllScripSectors();
			List<ScripSector> filscSecList = null;
			
			//Get the Sector for Current Scrip
			String sectorBaseScrip = this.getSectorforSCCode(scCode, scSecList);
			
			//Filter Scrip Sector List to Base Scrip Sector Only
			filscSecList = scSecList.stream().filter(x -> x.getSector().equals(sectorBaseScrip))
			        .collect(Collectors.toList());
			
			if (peers == null)
			{
				loadAllPeers = true;
			} else
			{
				if (peers.size() == 0)
				{
					loadAllPeers = true;
				} else
				{
					//Validate Peers 
					for (String peer : peers)
					{
						Optional<ScripSector> SSO = filscSecList.stream().filter(x -> x.getScCode().equals(peer))
						        .findFirst();
						if (!SSO.isPresent())
						{
							//Scrip Peer to Compare {0} does not belong to Base Scrip Sector {1}! - message.properties
							throw new Exception(msgSrc.getMessage("INVALID_PEER", new Object[]
							{ peer, sectorBaseScrip }, Locale.ENGLISH));
						}
					}
				}
			}
			
			// Peers Validated
			if (loadAllPeers == true)
			{
				this.setRefScripName(scCode);
				if (no_peers == true)
				{
					this.setPeerScripNames(null);
					
				} else
				{
					this.setPeerScripNames(peers);
				}
				loadDC();
				
			} else
			{
				if (excludepeers == true)
				{
					//Check for No Peer
					//-1 for Base Scrip Exclusion from Count
					if ((filscSecList.size() - 1) == peers.size())
					{
						throw new Exception(noPeer_error);
					}
					
					//REmove Peers ought to be excluded
					//Also Remove current Scrip - Add to Peers
					peers.add(scCode);
					for (String peer : peers)
					{
						filscSecList.removeIf(x -> x.getScCode().equals(peer));
					}
					
					for (ScripSector scripSector : filscSecList)
					{
						this.getPeerScripNames().add(scripSector.getScCode());
					}
					this.setRefScripName(scCode);
					
				} else //No Exclusion; Infact Only include these Scrips as Peers
				{
					this.setRefScripName(scCode);
					this.setPeerScripNames(peers);
				}
				
				loadDC();
			}
			
		}
		
	}
	
	private String getSectorforSCCode(
	        String scCode, List<ScripSector> scSecList
	)
	{
		String                sectorBaseScrip = null;
		Optional<ScripSector> scsector        = scSecList.stream().filter(x -> x.getScCode().equals(scCode))
		        .findFirst();
		if (scsector.isPresent())
		{
			sectorBaseScrip = scsector.get().getSector();
		}
		return sectorBaseScrip;
	}
	
	private void loadDC(
	) throws Exception
	{
		this.scripDataContainers = new ArrayList<scDataContainer>();
		scDCSrv.load(this.getRefScripName());
		this.scripDataContainers.add(scDCSrv.getScDC());
		
		if (this.getPeerScripNames() != null)
		{
			for (String peer : this.getPeerScripNames())
			{
				scDCSrv.load(peer);
				this.scripDataContainers.add(scDCSrv.getScDC());
			}
		}
	}
	
}

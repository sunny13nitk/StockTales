package stocktales.dataBook.scjournal.srv;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import stocktales.dataBook.enums.EnumInterval;
import stocktales.dataBook.helperPojo.scjournal.dbproc.NumandLastEntry;
import stocktales.dataBook.helperPojo.scjournal.dbproc.PlaceHolderLongPJ;
import stocktales.dataBook.helperPojo.scjournal.dbproc.intf.SCJID;
import stocktales.dataBook.helperPojo.scjournal.dbproc.intf.Tag;
import stocktales.dataBook.model.entity.scjournal.ScripJournal;
import stocktales.dataBook.model.pojo.PJIntervalSimple;
import stocktales.dataBook.model.pojo.ScripJournalPoJo;
import stocktales.dataBook.model.repo.scjournal.RepoScJournal;
import stocktales.dataBook.scjournal.intf.IScJournalSrv;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ScJournalSrv implements IScJournalSrv
{
	@Autowired
	private RepoScJournal repoScJournal;
	
	private ScripJournalPoJo scJournal;
	
	@Override
	public ScripJournalPoJo createNewJournalforScrip(
	        String scCode
	)
	{
		this.clearBuffer();
		this.scJournal = new ScripJournalPoJo();
		scJournal.setSccode(scCode);
		
		return scJournal;
	}
	
	@Override
	public ScripJournalPoJo createNewJournalforScripandInterval(
	        String scCode, PJIntervalSimple intervalPoJo
	)
	{
		this.scJournal = new ScripJournalPoJo();
		scJournal.setSccode(scCode);
		
		scJournal.setInterval(intervalPoJo.getInterval());
		scJournal.setValm(intervalPoJo.getValm());
		scJournal.setVald(intervalPoJo.getVald());
		return scJournal;
	}
	
	@Override
	public void uploadImage(
	        MultipartFile file
	)
	{
		Byte[] byteObjects = null;
		
		try
		{
			//Get byte length of file
			byteObjects = new Byte[file.getBytes().length];
			
			int i = 0;
			
			for (byte b : file.getBytes())
			{
				byteObjects[i++] = b;
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		scJournal.setImage(byteObjects);
		
	}
	
	@Override
	public ScripJournal saveJournal(
	)
	{
		ScripJournal sj = null;
		if (this.scJournal != null)
		{
			//Either of Notes or Image Maintained
			if (scJournal.getNotes() != null || scJournal.getImage() != null)
			{
				ScripJournal scJ = new ScripJournal();
				
				scJ.setCategory(scJournal.getCategory());
				scJ.setEffect(scJournal.getEffect());
				scJ.setImage(scJournal.getImage());
				scJ.setInterval(scJournal.getInterval());
				scJ.setValm(scJournal.getValm());
				scJ.setVald(scJournal.getVald());
				scJ.setNotes(scJournal.getNotes());
				scJ.setSccode(scJournal.getSccode());
				scJ.setSource(scJournal.getSource());
				scJ.setTag(scJournal.getTag());
				
				scJ.setUrl(scJournal.getUrl());
				scJ.setDateofentry(new java.sql.Date(System.currentTimeMillis()));
				
				sj = repoScJournal.save(scJ);
			}
			
		}
		
		return sj;
	}
	
	@Override
	public void clearBuffer(
	)
	{
		scJournal = null;
		
	}
	
	@Override
	public void refreshScJournal(
	        ScripJournalPoJo scJP
	)
	{
		//Refresh Everything but Scrip Code and Image in Buffer Attribute
		
		if (scJP != null && scJournal != null)
		{
			scJournal.setCategory(scJP.getCategory());
			scJournal.setEffect(scJP.getEffect());
			
			scJournal.setInterval(scJP.getInterval());
			if (scJournal.getInterval() != EnumInterval.NA)
			{
				scJournal.setValm(scJP.getValm());
				scJournal.setVald(scJP.getVald());
				
			}
			
			scJournal.setTag(scJP.getTag());
			
			scJournal.setNotes(scJP.getNotes());
			
			scJournal.setSource(scJP.getSource());
		}
		
	}
	
	@Override
	public NumandLastEntry getQStats_New_ByScrip(
	        String scCode
	)
	{
		NumandLastEntry snippet = null;
		
		if (repoScJournal != null && scCode != null)
		{
			List<Object[]> vals = repoScJournal.findByAsArray(scCode);
			if (vals != null)
			{
				if (vals.size() > 0)
				{
					
					snippet = new NumandLastEntry();
					snippet.setLastEntryDate((Date) vals.get(0)[0]);
					snippet.setNumEntries((Long) vals.get(0)[1]);
				}
			}
			
		}
		
		return snippet;
	}
	
	@Override
	public List<PlaceHolderLongPJ> getTagDetailsOvw(
	        String scCode
	)
	{
		
		List<String>            tagsInd    = new ArrayList<String>();
		List<String>            tagsUnique = null;
		List<PlaceHolderLongPJ> tagDetails = null;
		
		//1. Get the Tags as Such First from DB for the Scrip
		
		List<Tag> tagsDB = repoScJournal.findAllBySccode(scCode);
		if (tagsDB != null)
		{
			//2. Get Tags Names separating the comma from each tag
			for (Tag tag : tagsDB)
			{
				String[] arrOfTags = tag.getTag().split(",");
				for (String tagVal : arrOfTags)
				{
					tagsInd.add(tagVal);
				}
			}
			
			//3. Collect Unique Tags in a List
			tagsUnique = tagsInd.stream().distinct().collect(Collectors.toList());
			
			if (tagsUnique != null)
			{
				//Get Journal Entries that contain each tag in Loop
				
				for (String uqtag : tagsUnique)
				{
					List<SCJID> scJournalsFound = repoScJournal.findAllBySccodeAndTagContaining(scCode, uqtag);
					if (scJournalsFound != null)
					{
						PlaceHolderLongPJ phL = new PlaceHolderLongPJ(uqtag, (long) scJournalsFound.size());
						
						if (tagDetails == null)
						{
							tagDetails = new ArrayList<PlaceHolderLongPJ>();
						}
						tagDetails.add(phL);
					}
				}
			}
		}
		
		return tagDetails;
	}
	
}

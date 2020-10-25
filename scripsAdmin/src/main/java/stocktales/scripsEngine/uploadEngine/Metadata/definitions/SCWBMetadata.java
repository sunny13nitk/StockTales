package stocktales.scripsEngine.uploadEngine.Metadata.definitions;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "WorkBookMetadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class SCWBMetadata
{
	private ArrayList<SCSheetMetadata> sheetMetadata;

	public ArrayList<SCSheetMetadata> getSheetMetadata()
	{
		return sheetMetadata;
	}

	public void setSheetMetadata(ArrayList<SCSheetMetadata> sheetMetadata)
	{
		this.sheetMetadata = sheetMetadata;
	}

	public SCWBMetadata(ArrayList<SCSheetMetadata> sheetMetadata)
	{
		super();
		this.sheetMetadata = sheetMetadata;
	}

	public SCWBMetadata()
	{
		super();
		this.sheetMetadata = new ArrayList<SCSheetMetadata>();
	}

}

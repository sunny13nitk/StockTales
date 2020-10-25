package stocktales.scripsEngine.uploadEngine.exceptions;

import stocktales.scripsEngine.uploadEngine.interfaces.IPropertyAwareMessage;

@SuppressWarnings("serial")
public class EX_ScripMetadata extends Exception implements IPropertyAwareMessage
{
	private String Property_ID;
	
	private Object[] Args;
	
	@Override
	public void setProperty_ID(
	        String propertyID
	)
	{
		this.Property_ID = propertyID;
	}
	
	@Override
	public String getProperty_ID(
	)
	{
		// TODO Auto-generated method stub
		return Property_ID;
	}
	
	@Override
	public Object[] getArguments(
	)
	{
		// TODO Auto-generated method stub
		return Args;
	}
	
	@Override
	public boolean IS_Exception(
	)
	{
		// TODO Auto-generated method stub
		return true;
	}
	
	public EX_ScripMetadata(
	        Object[] args
	)
	{
		this.Args = args;
		this.setProperty_ID("SCRIPMDTERROR_BASEKEY");
	}
	
	public EX_ScripMetadata(
	        String propID, Object[] args
	)
	{
		this.Args = args;
		this.setProperty_ID(propID);
	}
}

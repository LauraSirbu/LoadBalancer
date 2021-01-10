package net.codejava;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Provider {
	private UUID id;
	private List<String> requests = new ArrayList<String>();
	private boolean isActive;
	private double remainingLifeTimeSeconds;
	private int heartbeatCounter = 0;
	
	public Provider(UUID id, double remainingLifeTimeSeconds)
	{
		this.id = id;
		this.isActive = true;
		this.remainingLifeTimeSeconds = remainingLifeTimeSeconds;
	}
	
	public UUID Id()
	{
		return id;
	}
	
	public List<String> Requests()
	{
		return requests;
	}
	
	public boolean IsActive()
	{
		return isActive;
	}
	
	public void SetIsActive(boolean isActive)
	{
		this.isActive = isActive;
		heartbeatCounter = 0;
	}
	
	public double RemainingLifeTimeSeconds()
	{
		return remainingLifeTimeSeconds;
	}
	
	public void SetRemainingLifeTimeSeconds(double remainingLifeTimeSeconds)
	{
		this.remainingLifeTimeSeconds = remainingLifeTimeSeconds;
	}
	
	public void CheckIsExpired(int interval)
	{
		remainingLifeTimeSeconds -= interval;
		if(remainingLifeTimeSeconds < 0)
		{
			SetIsActive(false);
		}
	}
	
	public void CheckIfStillInactive()
	{
		heartbeatCounter++;
		if(heartbeatCounter == 2)
		{
			SetIsActive(true);
		}
	}
	
	public void AddRequest(String ipAddress)
	{
		requests.add(ipAddress);
	}
}

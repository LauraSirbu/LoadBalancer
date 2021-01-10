package net.codejava;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import net.codejava.ILogger.Severity;

public abstract class LoadBalancer implements ILoadBalancer
{
	protected UUID id;
	private ILogger logger;
	
	private int MAX_NO_PROVIDERS = 10;
	private int MAX_NO_PROVIDER_REQUESTS = 10;
	private final ReentrantLock lock;
	
	List<Provider> providers = new ArrayList<Provider>();
	List<Provider> activeProviders = new ArrayList<Provider>();

    public LoadBalancer(ILogger logger) 
    {
		this.logger = logger;
    	lock = new ReentrantLock();
    	this.id = UUID.randomUUID();
    	InitializeProviders(MAX_NO_PROVIDERS);
    }
    
    public void HandleRequest(String ipAddress)
    {
    	int maxLoadBalancerRequests =  activeProviders.size() * MAX_NO_PROVIDER_REQUESTS;
    	int noActiveProvidersRequests = CalculateActiveProvidersRequests();
    	
    	if(noActiveProvidersRequests < maxLoadBalancerRequests)
    	{
    		Provider provider = GetAvailableProvider();
    		provider.AddRequest(ipAddress);
    		
    		logger.Log("Request " + ipAddress + " was added to provider " +  provider.Id() + ".", Severity.Info);
    	}
    	else
    	{
    		logger.Log("Request " + ipAddress + " could not be added because balancer " + name() + " cannot accept any further requests.", Severity.Warning);
    	}
    }
    
    public void AddProvider(UUID id, double remainingLifeTimeSeconds)
    {
    	if(!IsProviderListFull())
    	{
    		Provider newProvider = new Provider(id, remainingLifeTimeSeconds);
    		providers.add(newProvider);
    		SetActiveProviders();
    		logger.Log("Provider " + newProvider.Id() + " was added.", Severity.Info);
    	}
    	else
    	{
    		logger.Log("Provider " + id + " list is full.", Severity.Warning);
    	}
    }
    
    public void SetProviderInactive(Provider provider)
    {
    	for(Provider p : providers)
    	{
    		if(p.Id() == provider.Id())
    		{
    			p.SetIsActive(false);
    			logger.Log("Provider " + provider.Id() + " was inactivated.", Severity.Info);
    			return;
    		}
    	}
    	
    	logger.Log("Provider " + provider.Id() + " was not found.", Severity.Warning);
    }
    
    public void InitializeProviders(int noProviders)
    {
    	for(int i=0; i < Math.min(noProviders, MAX_NO_PROVIDERS); i++)
    	{
    		AddProvider(UUID.randomUUID(), GetRandomBetween(5, 15));
    	}
    }
    
    public void Check(int interval)
    { 
    	for(Provider p : providers)
    	{
    		if(!p.IsActive())
    		{
    			p.CheckIfStillInactive();
    			
    			if(p.IsActive())
        		{
        			SetActiveProviders();
        			logger.Log("Provider " + p.Id() + " was activated (heartbeat).", Severity.Info);
        		}	
    		}
    		else
    		{
    			p.CheckIsExpired(interval);
        		
        		if(!p.IsActive())
        		{
        			SetActiveProviders();
        			logger.Log("Provider " + p.Id() + " was inactivated (heartbeat).", Severity.Info);
        		}	
    		}
    	}
    }
    
    private boolean IsProviderListFull()
    {
    	if(activeProviders == null || activeProviders.size() < MAX_NO_PROVIDERS)
    	{
    		return false;
    	}
    	return true;
    }
    
    private void SetActiveProviders()
    {
    	lock.lock();
        try 
        {
        	List<Provider> activeProviders = new ArrayList<>();
        	
            for(Provider provider : providers)
            {
            	if(provider.IsActive())
            	{
            		activeProviders.add(provider);
            	}
            }
            
            this.activeProviders = activeProviders;
        } 
        finally 
        {
            lock.unlock();
        }
    }
    
    private int CalculateActiveProvidersRequests()
    {
    	int noActiveProvidersRequests = 0;
    	for(Provider p : activeProviders)
    	{
    		noActiveProvidersRequests += p.Requests().size();
    	}
    	
    	return noActiveProvidersRequests;
    }
    
    private double GetRandomBetween(double min, double max)
    {
    	Random random = new Random();
		return random.nextDouble() * (max - min) + min;
    }
    
    private Provider GetAvailableProvider()
    {
		Provider provider = get();
		int noProviderRequests = provider.Requests().size();

		while(noProviderRequests >= MAX_NO_PROVIDER_REQUESTS)
		{
			logger.Log("Provider " + provider.Id() + " cannot accept any further requests. Trying other provider...", Severity.Warning);			
			provider = get();
    		noProviderRequests = provider.Requests().size();
		}
		
		return provider;
    }
    
    abstract Provider get();
    
    abstract String name();
}

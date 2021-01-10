package net.codejava;

import java.util.UUID;

public interface ILoadBalancer {
	public void HandleRequest(String ipAddress);
	
	public void AddProvider(UUID id, double remainingLifeTimeSeconds);
	
	public void SetProviderInactive(Provider provider);
	
	public void InitializeProviders(int noProviders);
	
	public void Check(int interval);
}
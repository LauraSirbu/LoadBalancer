package net.codejava;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Solution {

	public static void main(String[] args) {

		int numOfRequests = 105;
		MockLogger logger = new MockLogger();
		
		System.out.println("--------------------------Random------------------------------------");
        LoadBalancer randomLoadBalancer = new RandomLoadBalancer(logger);
        
		String ipAddress = "192.168.0.";           
		for(int i = 1; i <= numOfRequests; i++)
		{
            randomLoadBalancer.HandleRequest(ipAddress + String.valueOf(i));
		}
		
		//Read providers(maximum of 10)
		for(Provider p : randomLoadBalancer.activeProviders)
		{			
			System.out.println("Provider: " + p.Id() + " is active " + p.IsActive() + " for "  + p.RemainingLifeTimeSeconds() + " seconds.");
			
			//Exclude provider(set to inactive)
			randomLoadBalancer.SetProviderInactive(p);
			System.out.println("Provider: " + p.Id() + " is active " + p.IsActive() + " for "  + p.RemainingLifeTimeSeconds() + " seconds.");
		}
		
		//Heartbeat check
		ScheduledExecutorService executorServiceRandom = Executors.newSingleThreadScheduledExecutor();
		executorServiceRandom.scheduleWithFixedDelay(new Runnable() {
	        @Override
	        public void run() {
	        	int waitAttempts = 0;
	        	while(waitAttempts < 3)
	        	{
		        	randomLoadBalancer.Check(20);
		        	waitAttempts++;
	        	}
	        	executorServiceRandom.shutdown();
	        }
	    }, 0, 50, TimeUnit.SECONDS);
		
		System.out.println("--------------------------RoundRobin------------------------------------");
        LoadBalancer roundRobinLoadBalancer = new RoundRobinLoadBalancer(logger);
        
		String ipAddressRoundRobin = "193.168.0.";           
		for(int i = 1; i <= numOfRequests; i++)
		{
			roundRobinLoadBalancer.HandleRequest(ipAddressRoundRobin + String.valueOf(i));
		}
		
		//Read providers(maximum of 10)
		for(Provider p : roundRobinLoadBalancer.activeProviders)
		{
			System.out.println("Provider: " + p.Id() + " is active " + p.IsActive() + " for "  + p.RemainingLifeTimeSeconds() + " seconds.");
		}
	}
}

package net.codejava;

import java.util.Random;

public class RandomLoadBalancer extends LoadBalancer
{
	public RandomLoadBalancer(ILogger logger) {
		super(logger);
    }
	
	@Override
	public String name()
	{
		return "Random(" + id + ")";
	}
	
	// Retrieve random provider.
    @Override
    public Provider get() {
        Random random = new Random();
        return activeProviders.get(random.nextInt(activeProviders.size()));
    }
}

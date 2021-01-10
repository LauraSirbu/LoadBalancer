package net.codejava;

import java.util.concurrent.locks.ReentrantLock;

public class RoundRobinLoadBalancer extends LoadBalancer 
{
    private int counter = 0;
    private final ReentrantLock lock;

    public RoundRobinLoadBalancer(ILogger logger) {
        super(logger);
        lock = new ReentrantLock();
    }
    
	@Override
	public String name()
	{
		return "RoundRobin(" + id + ")";
	}

	// Retrieve next provider.
    @Override
    public Provider get() 
    {
        lock.lock();
        try 
        {
            Provider p = activeProviders.get(counter);
            counter += 1;
            if (counter == activeProviders.size()) 
            {
                counter = 0;
            }
            return p;
        } 
        finally 
        {
            lock.unlock();
        }
    }
}

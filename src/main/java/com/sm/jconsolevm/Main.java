package com.sm.jconsolevm;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.sun.management.OperatingSystemMXBean;

@SuppressWarnings("restriction")
public class Main {

    public static void main(String[] args) throws Exception {
        // Create an RMI connector client and
        // connect it to the RMI connector server
        //
        echo("\nCreate an RMI connector client and " +
             "connect it to the RMI connector server");
        JMXServiceURL url =
            new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:" + args[0] + "/jmxrmi");
        JMXConnector jmxc = JMXConnectorFactory.connect(url, null);

        // Create listener
        //
        // Get an MBeanServerConnection
        //
        echo("\nGet an MBeanServerConnection");
        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

        // Get domains from MBeanServer
        //
        echo("\nDomains:");
        String domains[] = mbsc.getDomains();
        Arrays.sort(domains);
        for (String domain : domains) {
            echo("\tDomain = " + domain);
        }
        // Get MBeanServer's default domain
        //
        echo("\nMBeanServer default domain = " + mbsc.getDefaultDomain());

        // Get MBean count
        //
        echo("\nMBean count = " + mbsc.getMBeanCount());

        // Query MBean names
        //
        echo("\nQuery MBeanServer MBeans:");
        Set<ObjectName> names =
            new TreeSet<ObjectName>(mbsc.queryNames(null, null));
        for (ObjectName name : names) {
            echo("\tObjectName = " + name);
        }
        
        OperatingSystemMXBean osbean =
                JMX.newMXBeanProxy(mbsc, ObjectName.getInstance(ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME), OperatingSystemMXBean.class);
        RuntimeMXBean runbean = JMX.newMXBeanProxy(mbsc, ObjectName.getInstance(ManagementFactory.RUNTIME_MXBEAN_NAME), RuntimeMXBean.class);
        
        int nCPUs = osbean.getAvailableProcessors();
        while (nCPUs < 10) {
        	nCPUs = osbean.getAvailableProcessors();
    		long prevUpTime = runbean.getUptime();
    		long prevProcessCpuTime = osbean.getProcessCpuTime();
    		try {
    			Thread.sleep(500);
    		} catch (Exception e) {
    		}

    		osbean = JMX.newMXBeanProxy(mbsc, ObjectName.getInstance(ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME), OperatingSystemMXBean.class);
    		long upTime = runbean.getUptime();
    		long processCpuTime = osbean.getProcessCpuTime();
    		double javacpu;
    		if (prevUpTime > 0L && upTime > prevUpTime) {
    			long elapsedCpu = processCpuTime - prevProcessCpuTime;
    			long elapsedTime = upTime - prevUpTime;
    			javacpu = Math
    					.min(99F, elapsedCpu / (elapsedTime * 10000F * nCPUs));
    		} else {
    			javacpu = 0.001;
    		}

    		int max = osbean.getAvailableProcessors() * 100;
    		echo(" CPU used : " + javacpu + " %  ");
    		Long percentage = (long) ((javacpu * 100) / max);
    		echo(" Available Processors : "
    				+ osbean.getAvailableProcessors());
    		echo(" CPU used : " + percentage + " %  ");
        }
        echo("\nClose the connection to the server");
        jmxc.close();
        echo("\nBye! Bye!");
    }

    private static void echo(String msg) {
        System.out.println(msg);
    }

	
}

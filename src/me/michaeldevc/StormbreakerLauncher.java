package me.michaeldevc;


import me.michaeldevc.option.Options;

public class StormbreakerLauncher {
    public StormbreakerLauncher() {
    }

    public static void main(String... args) {
        Stormbreaker breaker = new Stormbreaker(Options.Builder.of(args));
        breaker.launch();
    }
}

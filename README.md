# WanderingTrades ![WanderingTrades](https://github.com/jmanpenilla/WanderingTrades/workflows/WanderingTrades/badge.svg?branch=master)
* [bStats](https://bstats.org/plugin/bukkit/WanderingTrades/7597)
* [Spigot Resource Page](https://www.spigotmc.org/resources/wanderingtrades.79068/)
* [Discord](https://discord.gg/g7CZdxt)


## Summary

* WanderingTrades is a plugin for [Spigot](https://www.spigotmc.org/) Minecraft Servers (also works on [Paper](https://www.papermc.io/))
* Adds trades defined in config files to wandering traders. Config files are located in ```plugins/WanderingTraders/trades``` in your server. You may create as many as you like. If you have no configs, an ```example.yml``` config file will be created for you.
* Randomization options
* Supports enchantments, and custom player heads
* Commands: `````/wanderingtrades`````, `````/wanderingtrades reload````` (requires ```wanderingtrades.reload``` permission)
* [VanillaTweaks](https://vanillatweaks.net) microblocks config: [microblocks.yml](https://gist.github.com/jmanpenilla/56120245992a7c4099c13b798c94b5e0)  (Place in ```plugins/WanderingTrades/trades```)
* Default config file [config.yml](https://github.com/jmanpenilla/WanderingTrades/blob/master/src/main/resources/config.yml)
* See the Spigot Resource Page for a more up to date overview


## How to compile

1. Clone and cd into this repository ```git clone https://github.com/jmanpenilla/WanderingTrades.git && cd WanderingTrades```
2. Run ```mvn clean install```


## Credits

* Apache for Maven, IntelliJ for IDEA
* Thank you to the Bukkit Spigot, and Paper communities for providing a better Minecraft server wrapper
* Thanks to [Deanveloper SkullCreator](https://github.com/deanveloper/SkullCreator), [Aikar Annotation Command Framework](https://github.com/aikar/commands), and [WesJD AnvilGUI](https://github.com/WesJD/AnvilGUI)

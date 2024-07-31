package nl.teamdiopside.separatedleaves.forge;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import nl.teamdiopside.separatedleaves.Reload;
import nl.teamdiopside.separatedleaves.SeparatedLeaves;

import java.util.function.Consumer;

@Mod(SeparatedLeaves.MOD_ID)
public class SeparatedLeavesForge {
    public SeparatedLeavesForge() {
//        EventBuses.registerModEventBus(SeparatedLeaves.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        SeparatedLeaves.init();

        Consumer<TagsUpdatedEvent> tags = tagsUpdatedEvent -> {
            if (tagsUpdatedEvent.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD) {
                reload();
            }
        };

        Consumer<ServerStartingEvent> serverStarting = serverStartingEvent -> {
            SeparatedLeaves.minecraftServer = serverStartingEvent.getServer();
            reload();
        };

        Consumer<ServerStoppingEvent> serverStopping = serverStoppingEvent -> SeparatedLeaves.minecraftServer = serverStoppingEvent.getServer();

        NeoForge.EVENT_BUS.addListener(tags);
        NeoForge.EVENT_BUS.addListener(serverStarting);
        NeoForge.EVENT_BUS.addListener(serverStopping);
    }

    public static void reload() {
        if (SeparatedLeaves.minecraftServer != null) {
            Reload.reload(SeparatedLeaves.minecraftServer.getResourceManager());
        }
    }
}

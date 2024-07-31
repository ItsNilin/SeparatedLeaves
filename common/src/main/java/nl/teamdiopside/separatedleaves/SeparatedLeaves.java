package nl.teamdiopside.separatedleaves;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeparatedLeaves {
    public static final String MOD_ID = "separatedleaves";
    public static final Logger LOGGER = LoggerFactory.getLogger("Separated Leaves");
    public static final TagKey<Structure> ALLOW_MISMATCHED_LEAVES_STRUCTURES = TagKey.create(Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(MOD_ID, "allow_mismatched_leaves"));

    public static MinecraftServer minecraftServer = null;
    
    public static void init() {
        LOGGER.debug("unilock is the real MVP");
    }
}

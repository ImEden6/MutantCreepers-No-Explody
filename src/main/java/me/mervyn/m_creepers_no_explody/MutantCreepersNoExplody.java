package me.mervyn.m_creepers_no_explody;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MutantCreepersNoExplody implements ModInitializer {
    public static final String MOD_ID = "mutant_creepers_no_explody";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Mutant Creepers No Explody!");
    }
}

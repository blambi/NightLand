package com.chebab.nightland;

import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class NightLandBlockListener extends BlockListener {
    private final NightLand plugin;

    public NightLandBlockListener( NightLand instance) {
        plugin = instance;
    }

    @Override
    public void onBlockPlace( BlockPlaceEvent event ) {
        if( plugin.isWorldNightLand( event.getPlayer().getWorld().getName() ) )
        {
            System.out.println( event.getPlayer().getDisplayName() + " placed " + event.getBlockPlaced().getType() + " in a nightland!" );
        }
    }
}
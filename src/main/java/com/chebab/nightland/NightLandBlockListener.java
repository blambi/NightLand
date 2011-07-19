package com.chebab.nightland;

import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.Material;

public class NightLandBlockListener extends BlockListener {
    private final NightLand plugin;

    public NightLandBlockListener( NightLand instance) {
        plugin = instance;
    }

    @Override
    public void onBlockPlace( BlockPlaceEvent event ) {
        if( plugin.isWorldNightLand( event.getPlayer().getWorld().getName() ) )
        {
            // block in a nightland
            Block block = event.getBlockPlaced();
            Block block_near;
            BlockFace[] dirs = { BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH };
            
            if( block.getType() == Material.BED_BLOCK )
            {
                block.setType( Material.AIR );
                
                for( BlockFace bf: dirs )
                {
                    block_near = block.getRelative( bf );
                    event.getPlayer().sendBlockChange( block_near.getLocation(),
                                                       block_near.getType(),
                                                       block_near.getData() );
                }

                event.getPlayer().sendRawMessage( plugin.getBedPlacingMessage() );
                event.setBuild( false );
                event.setCancelled( true );
            }
        }
    }
}
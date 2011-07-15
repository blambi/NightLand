package com.chebab.nightland;

import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
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
                    /*if( block.getFace( bf ).getType() == Material.AIR )
                      block.getFace( bf ).setType( Material.AIR );*/
                    block_near = block.getFace( bf );
                    event.getPlayer().sendBlockChange( block_near.getLocation(),
                                                       block_near.getType(),
                                                       block_near.getData() );
                }
                
                //block.setType( Material.AIR );
                
                event.setBuild( false );
                event.setCancelled( true );
            }
            
            System.out.println( event.getPlayer().getDisplayName() + " placed " + event.getBlockPlaced().getType() + " in a nightland!" );
        }
    }
}
package com.chebab.nightland;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.util.config.Configuration;

public class NightLand extends JavaPlugin
{
    private int moonwatcher_id = -1;
    private int stormwatcher_id = -1;
    private Configuration conf;
    private Random rnd;
    
    public void onDisable() {
        if( moonwatcher_id != -1 )
            getServer().getScheduler().cancelTask( moonwatcher_id );

        System.out.println( "[NightLand] unloaded" );
    }


    public void onLoad() {
        // Load config
        // make dir if not there.
        new File( "plugins" + File.separator + "NightLand" ).mkdir();
        File raw_conf = new File( "plugins" + File.separator + "NightLand" +
                                  File.separator + "config.yml" );

        // create default if there isn't any config yet
        if( ! raw_conf.exists() )
        {
            System.err.println( "[NightLand] Creating a default config file" );

            try
            {
                raw_conf.createNewFile();
                conf = new Configuration( raw_conf );
                
                String[] def_world = {
                    getServer().getWorlds().get( 0 ).getName() };
                conf.setProperty( "worlds", def_world  );
                conf.setProperty( "extraStorms", false );
                conf.setProperty( "stormProbability", 6 );
                conf.setProperty( "stormDurationMin", 2500 );
                conf.setProperty( "stormDurationMax", 10000 );
                conf.save();
            }
            catch( IOException e )
            {
                System.err.println( "[NightLand] oooh couldn't save..." );
                e.printStackTrace();
            }
        }
        else
        {
            conf = new Configuration( raw_conf );
            
        }
        conf.load();
    }
    
    public void onEnable() {
        rnd = new Random();

        // Watching the moon.. or well the time but yea..
        moonwatcher_id = getServer().getScheduler().scheduleSyncRepeatingTask(
            this, new Runnable() {
                    public void run() {
                        List<String> worlds = conf.getStringList( "worlds", null );
                        for( String world_name: worlds )
                        {
                            World w = getServer().getWorld( world_name );
                            int storm_min = conf.getInt( "stormDurationMin", 2500 );
                            int storm_max = conf.getInt( "stormDurationMax", 10000 ) - storm_min;

                            // Extra Storms with thundering death!
                            if( ! w.isThundering() && // FIXME: move to another runnable
                                conf.getBoolean( "extraStorms", false ) )
                            {
                                if( rnd.nextInt( 10 ) < conf.getInt( "stormProbability", 6 ) )
                                {
                                    System.out.print( "extraStorm!!!" );
                                    w.setThundering( true );
                                    w.setStorm( true );
                                    w.setThunderDuration( storm_min + rnd.nextInt( storm_max ) );
                                    System.out.print( w.getThunderDuration() );
                                }
                                
                            }
                            
                            // Check time reset if needed
                            if( w.getTime() < (long)13672 ||
                                w.getTime() > (long)21000 )
                            {
                                w.setTime( (long)13672 );
                            }
                        }
                    }
                },
            (long)0, (long)1000 );

        System.out.print( "[NightLand] loaded, will check:" );

        List<String> worlds = conf.getStringList( "worlds", null );
        for( String world_name: worlds )
            System.out.print( " " + world_name );
        System.out.print( "\n" );
    }

}
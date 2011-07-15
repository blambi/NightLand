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
    private int weather_time = -1;
    private Configuration conf;
    private Random rnd;
    
    public void onDisable() {
        if( moonwatcher_id != -1 )
            getServer().getScheduler().cancelTask( moonwatcher_id );

        if( stormwatcher_id != -1 )
            getServer().getScheduler().cancelTask( stormwatcher_id );
        
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
                conf.setProperty( "stormDurationMin", 2500 );
                conf.setProperty( "stormDurationMax", 10000 );
                conf.setProperty( "niceWeatherMin", 500 );
                conf.setProperty( "niceWeatherMax", 5000 );

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

        // Weather gunk
        if( conf.getBoolean( "extraStorms", false ) )
        {
            weather_time = Math.min(
                conf.getInt( "stormDurationMin", 2500 ),
                conf.getInt( "niceWeatherMin", 500 ) );

            stormwatcher_id = getServer().getScheduler().scheduleSyncRepeatingTask( this, new Runnable() {
                    public void run() {
                        List<String> worlds = conf.getStringList( "worlds", null );
                        for( String world_name: worlds )
                        {
                            World w = getServer().getWorld( world_name );
                            int storm_min = conf.getInt( "stormDurationMin",
                                                         2500 );
                            int storm_max = conf.getInt( "stormDurationMax",
                                                         10000 ) - storm_min;
                            int nice_min = conf.getInt( "niceWeatherMin",
                                                         500 );
                            int nice_max = conf.getInt( "niceWeatherMax",
                                                         5000 ) - nice_min;
                            int duration = w.getWeatherDuration();
                            
                            if( duration > Math.max( storm_max, nice_max ) )
                                duration = 0; // Outside our bounds.
                            else if( duration > weather_time )
                                continue; // Nothing to do for this one yet.
                            
                            if( w.isThundering() )
                            {
                                // Yay sunshine.. wait... no okey no rain then.
                                w.setThundering( false );
                                w.setStorm( false );
                                w.setThunderDuration( 0 );
                                duration = storm_min + rnd.nextInt( storm_max );
                            }
                            else
                            {
                                // Okey lets make some baaad weather then
                                w.setThundering( true );
                                w.setStorm( true );
                                w.setThunderDuration(
                                    storm_min + rnd.nextInt( storm_max ) );

                                duration = w.getThunderDuration();
                                
                            }

                            w.setWeatherDuration( duration );
                        }
                    }
                }, (long)0, (long)weather_time );
        }

        
        System.out.print( "[NightLand] loaded, will check:" );

        List<String> worlds = conf.getStringList( "worlds", null );
        for( String world_name: worlds )
            System.out.print( " " + world_name );
        System.out.print( "\n" );
    }

}
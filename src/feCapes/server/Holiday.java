package feCapes.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimerTask;

import com.ForgeEssentials.util.tasks.TaskRegistry;

import feCapes.FeCapes;

public class Holiday extends TimerTask
{
    public static Holiday                 current   = null;

    // SEPERATOR = "\|"
    private static final String           SEPERATOR = new String(new byte[] { 92, 124 });
    private static final SimpleDateFormat SDF       = new SimpleDateFormat("dd-MM");

    public String[]                       dateString;
    public Calendar                       date;
    public String                         name;
    public int                            before;
    public int                            after;
    public String                         url;

    public Holiday()
    {
        // Used for timer
    }

    public Holiday(String date, String name, String before, String after, String URL)
    {
        try
        {
            dateString = date.split("-");

            this.date = Calendar.getInstance();
            this.date.set(Calendar.getInstance().get(Calendar.YEAR), Integer.parseInt(dateString[1]) - 1, Integer.parseInt(dateString[0]));
        }
        catch (NumberFormatException e)
        {
            throw new RuntimeException("Unable to parse holiday dates. Please make sure your holiday dates are formatted properly. Proper formatting: dd-mm", e);
        }

        this.name = name;
        this.before = -1 * Integer.parseInt(before);
        this.after = Integer.parseInt(after);
        url = URL;
    }

    public boolean isRelevant()
    {
        int t = daysToHoliday();

        if (t < 0)
            return t >= before;
        else if (t > 0)
            return t <= after;
        else
            return true;
    }

    public int daysToHoliday()
    {
        long diff = Calendar.getInstance().getTimeInMillis() - date.getTimeInMillis();
        return (int) (diff / (24 * 60 * 60 * 1000));
    }

    public long timeToHoliday()
    {
        return Calendar.getInstance().getTimeInMillis() - date.getTimeInMillis();
    }

    @Override
    public String toString()
    {
        return "[" + SDF.format(date.getTime()) + ", " + name + ", " + before + ", " + after + ", " + url + ", " + daysToHoliday() + ", " + isRelevant() + "]";
    }

    /*
     * Holiday capes!
     */
    private static ArrayList<Holiday> list = new ArrayList<Holiday>();

    private static void getHolidays()
    {
        File file = new File(FeCapes.dir, "holidays.txt");

        if (file.exists())
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null)
                    if (!(line.isEmpty() || line.startsWith("#")) && line.contains("|"))
                    {
                        String[] split = line.split(SEPERATOR);
                        if (split.length == 5) if (split[0].contains("-")) list.add(new Holiday(split[0], split[1], split[2], split[3], split[4]));
                    }
                br.close();
            }
            catch (Exception e)
            {
                // No holidays for you!
            }
        else
            try
            {
                PrintWriter pw = new PrintWriter(file);
                pw.println("# Holidays database");
                pw.println("# Once made, this file wil never get updated!");
                pw.println("# If 2 holidays overlap, the colosest one to the actual date will be taken.");
                pw.println("# Holidays get checked every hour, and on server start.");
                pw.println("# The seperator is \"|\"");
                pw.println("# Proper line syntax:");
                pw.println("# dd-mm|Holiday Name|days before|days after|cape URL");
                pw.println("# Example:");
                pw.println("# 01-05|Dries007's birthday|0|0|" + FeCapes.DEFSERVER + "theCakeIsALie.png");
                pw.println("# ");
                pw.println("25-12|Christmas|7|7|" + FeCapes.DEFSERVER + "christmas.png");
                pw.println("01-1|New Years|2|2|" + FeCapes.DEFSERVER + "newyear.png");

                pw.close();

                getHolidays();
            }
            catch (Exception e)
            {
                // No holidays for you!
            }
    }

    private static void getMostCurrentHoliday()
    {
        Holiday lastCurrent = current;
        for (Holiday h : list)
            if (h.isRelevant()) if (current == null)
                current = h;
            else if (Math.abs(current.timeToHoliday()) > Math.abs(h.timeToHoliday())) current = h;

        if (lastCurrent != current) if (Holiday.current != null)
            FeCapes.logger.info("Current Holiday: " + Holiday.current);
        else
            FeCapes.logger.info("No Holiday :(");
    }

    @Override
    public void run()
    {
        getMostCurrentHoliday();
    }

    public static void init()
    {
        getHolidays();
        TaskRegistry.registerRecurringTask(new Holiday(), 0, 0, 0, 0, 1, 0, 0, 0);
    }
}

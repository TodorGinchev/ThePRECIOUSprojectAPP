package precious_rule_system.journeyview_new;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import precious_rule_system.journeyview_new.assets.JourneyAssets;
import precious_rule_system.journeyview_new.grid.JourneyGridAdapterDelegate;
import precious_rule_system.journeyview_new.utilities.Layout;
import precious_rule_system.rewardsystem.entities.RewardEvent;
import rules.helpers.Tuple;

/**
 * Created by christopher on 01.09.16.
 */

public class DataManager implements JourneyGridAdapterDelegate {

    JourneyAssets assets;
    Layout layout;

    private final int pointsPerPage = 150;

    private ArrayList<RewardEvent> dummyData = new ArrayList<>();
    private int dummyTotalPoints = 0;

    private ArrayList<DataPage> pages = new ArrayList<>();
    private int totalPoints = 0;

    public class DataPage {
        ArrayList<Tuple<RewardEvent, Float>> events = new ArrayList<>();
        boolean hasPlayer = false;
        float playerPosition = 0.0f;
        ArrayList<Tuple<JourneyAssets.Size, double[]>> assets;
        JourneyAssets.Landscape landscape;
    }

    public DataManager(JourneyAssets assets, Layout layout) {
        this.assets = assets;
        this.layout = layout;
        this.reload();
    }

    private void buildDummyData() {

        dummyData.clear();
        dummyTotalPoints = 0;
        int n = 50;
        long startDate = new Date().getTime();

        for(int i=0; i<=n; i++) {
            float p = (float) i / (float) n;
            RewardEvent e;
            int points = 0;
            if (Math.random() > 0.7) {
                points = 20;
                e = RewardEvent.getMilestone("event", "reason", points, startDate);
            } else {
                points = 10;
                e = RewardEvent.getEvent("event", points, startDate);
            }
            dummyTotalPoints += points;
            startDate += Math.random() * 3600000 + 1800000;
            dummyData.add(e);
        }

    }

    public void reload() {
        buildDummyData();
        buildPages(dummyData, dummyTotalPoints);
    }

    private JourneyAssets.Landscape getLandscape(int page) {
        return JourneyAssets.Landscape.MOUNTAIN;
    }

    private void buildPages(ArrayList<RewardEvent> sortedEvents, int totalPoints) {

        this.totalPoints = totalPoints;
        pages.clear();
        int numPages = (int) Math.ceil((float)totalPoints/(float)pointsPerPage);
        numPages = Math.max(numPages, 1);

        // convert points to per page points
        ArrayList<Float> positions = new ArrayList<>();
        float currentPoints = 0.0f;
        for(RewardEvent e : sortedEvents) {

            if (currentPoints > (float) pointsPerPage) {
                currentPoints = 0.0f;
            }

            currentPoints += (float) e.getPoints();

            float percentage = currentPoints/(float) pointsPerPage;
            positions.add(percentage);

        }

        // get each page
        int currentIndex = 0;

        for (int n = 0; n<numPages; n++) {

            DataPage p = new DataPage();
            p.assets = layout.calculateRandomSizes(assets.assetSizes,assets.path.offset,assets);
            p.landscape = getLandscape(n);

            int currentPagePoints = 0;
            for(int i=currentIndex; i<sortedEvents.size(); i++) {

                if (i == sortedEvents.size()-1) {
                    p.hasPlayer = true;
                    p.playerPosition = positions.get(i);
                }

                RewardEvent e = sortedEvents.get(i);
                currentPagePoints += e.getPoints();

                if (currentPagePoints > pointsPerPage) {
                    currentIndex = i;
                    currentPagePoints = 0;
                    break;
                }



                p.events.add(new Tuple<RewardEvent, Float>(e, positions.get(i)));
            }

            pages.add(p);

        }
    }


    public int getNumberOfItems() {
        return pages.size();
    }

    public ArrayList<Tuple<RewardEvent, Float>> getEvents(int item) {
        return pages.get(item).events;
    }

    public ArrayList<Tuple<JourneyAssets.Size, double[]>> getAssets(int item) {
        return pages.get(item).assets;
    }

    public float getPlayer(int item) {
        DataPage p = pages.get(item);
        if (p.hasPlayer) return p.playerPosition;
        else return -1;
    }

    public int getPlayerItem() {
        for(int i=0; i<pages.size(); i++) {
            if (pages.get(i).hasPlayer) return i;
        }
        return 0;
    }

    public String getLabelForItem(int item) {
        return pages.get(item).landscape.getNiceText();
    }

    public int getNumberOfPoints() {
        return totalPoints;
    }


}

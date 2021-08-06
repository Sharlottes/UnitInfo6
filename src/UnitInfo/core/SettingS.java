package UnitInfo.core;

import mindustry.ui.dialogs.*;

import static mindustry.Vars.*;

public class SettingS {
    public void addGraphicCheckSetting(String key, boolean def){
        ui.settings.graphics.checkPref(key, def);
    }
    public void addGraphicSlideSetting(String key, int def, int min, int max, int step, SettingsMenuDialog.StringProcessor s){
        ui.settings.graphics.sliderPref(key, def, min, max, step, s);
    }
    public void init(){
        addGraphicCheckSetting("pastwave", false);
        addGraphicCheckSetting("emptywave", true);
        addGraphicSlideSetting("wavemax", 50, 0, 200, 1, s -> s + "waves");
        addGraphicCheckSetting("scan", false);
        addGraphicSlideSetting("rangemax", 10, 0, 100, 1, s -> s + "tiles");
        addGraphicCheckSetting("coreRange", false);
        addGraphicCheckSetting("rangeNearby", true);
        addGraphicCheckSetting("allTeamRange", false);
        addGraphicCheckSetting("allTargetRange", false);
        addGraphicCheckSetting("unitRange", false);
        addGraphicCheckSetting("softRangeDrawing", true);
        addGraphicSlideSetting("softRangeOpacity", 10, 0, 25, 1, s -> s + "%");
        addGraphicSlideSetting("rangeRadius", 15, 0, 20, 1, s -> s + "tiles");
        addGraphicSlideSetting("selectopacity", 50, 0, 100, 5, s -> s + "%");
        addGraphicSlideSetting("baropacity", 50, 0, 100, 5, s -> s + "%");
        addGraphicSlideSetting("uiopacity", 50, 0, 100, 5, s -> s + "%");

        addGraphicCheckSetting("autoShooting", false);
        addGraphicCheckSetting("infoui", true);
        addGraphicCheckSetting("weaponui", true);
        addGraphicCheckSetting("select", true);
        addGraphicCheckSetting("unithealthui", true);
        addGraphicCheckSetting("ssim", false);
        addGraphicCheckSetting("shar", false);
        addGraphicCheckSetting("shar1", false);
        addGraphicCheckSetting("shar2", false);
        addGraphicCheckSetting("shar3", false);
        addGraphicCheckSetting("gaycursor", false);
        addGraphicCheckSetting("allTeam", false);
        addGraphicCheckSetting("deadTarget", false);
        addGraphicCheckSetting("linkedMass", true);
        addGraphicCheckSetting("linkedNode", false);
        addGraphicCheckSetting("distanceLine", true);
    }
}

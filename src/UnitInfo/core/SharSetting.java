package UnitInfo.core;

import arc.Core;
import arc.scene.ui.layout.Table;
import arc.util.OS;
import mindustry.ui.dialogs.SettingsMenuDialog;

import static arc.Core.bundle;

public abstract class SharSetting extends SettingsMenuDialog.SettingsTable.Setting {
    public String description;

    public SharSetting(String name) {
        super();

        this.name = name;
        String winkey = "setting." + name + ".name.windows";
        title = OS.isWindows && bundle.has(winkey) ? bundle.get(winkey) : bundle.get("setting." + name + ".name");
        description = bundle.get("setting." + name + ".description");
    }

    public SharSetting(String name, Object def) {
        this(name);
        Core.settings.defaults(name, def);
    }

    public void add(Table table) {

    }

    @Override
    public void add(SettingsMenuDialog.SettingsTable table) {

    }
}

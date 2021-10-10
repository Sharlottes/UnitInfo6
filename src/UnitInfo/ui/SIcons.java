package UnitInfo.ui;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.Style;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.TextButton;
import mindustry.ui.Fonts;

import static mindustry.gen.Tex.pane;
import static mindustry.ui.Styles.*;

public class SIcons {
    public static TextureRegion health = Core.atlas.find("unitinfo6-health");
    public static TextureRegion shield = Core.atlas.find("unitinfo6-shield");
    public static TextureRegion item = Core.atlas.find("unitinfo6-item");
    public static TextureRegion liquid = Core.atlas.find("unitinfo6-liquid");
    public static TextureRegion power = Core.atlas.find("unitinfo6-power");
    public static TextureRegion ammo = Core.atlas.find("unitinfo6-ammo");
    public static TextureRegion reload = Core.atlas.find("unitinfo6-reload");

    public static ScrollPane.ScrollPaneStyle nonePane = new ScrollPane.ScrollPaneStyle();
    public static TextButton.TextButtonStyle squareTogglet = new TextButton.TextButtonStyle(){{
        font = Fonts.def;
        fontColor = Color.white;
        checked = flatOver;
        down = flatOver;
        up = pane;
        over = flatOver;
        disabled = black;
        disabledFontColor = Color.gray;
    }};
}

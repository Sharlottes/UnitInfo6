package UnitInfo.core;

import UnitInfo.SUtils;
import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.math.geom.Vec2;
import arc.scene.Element;
import arc.scene.Group;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.scene.ui.layout.Stack;
import arc.struct.*;
import arc.util.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class SettingS {
    public SettingsMenuDialog.SettingsTable sharset;
    
    public void addDesc(Element elem, String description){
        if(description == null) return;
        elem.addListener(new Tooltip(t -> t.background(Styles.black8).margin(4f).add(description).color(Color.lightGray)));
    }
    
    public void addGraphicCheckSetting(String key, boolean def, Seq<SharSetting> list){
        list.add(new SharSetting(key, def) {

            @Override
            public void add(Table table) {
                CheckBox box = new CheckBox(title);
                box.update(() -> box.setChecked(settings.getBool(name)));
                box.changed(() -> settings.put(name, box.isChecked()));

                box.left();
                addDesc(table.add(box).left().padTop(3f).get(), description);
                table.row();
            }
        });
    }

    public void addGraphicSlideSetting(String key, int def, int min, int max, int step, SettingsMenuDialog.StringProcessor sp, Seq<SharSetting> list){
        list.add(new SharSetting(key, def) {

            @Override
            public void add(Table table){
                Slider slider = new Slider(min, max, step, false);

                slider.setValue(settings.getInt(name));

                Label value = new Label("", Styles.outlineLabel);
                Table content = new Table();
                content.add(title, Styles.outlineLabel).left().growX().wrap();
                content.add(value).padLeft(10f).right();
                content.margin(3f, 33f, 3f, 33f);
                content.touchable = Touchable.disabled;

                slider.changed(() -> {
                    settings.put(name, (int)slider.getValue());
                    value.setText(sp.get((int)slider.getValue()));
                });

                slider.change();

                addDesc(table.stack(slider, content).width(Math.min(Core.graphics.getWidth() / 1.2f, 460f)).left().padTop(4f).get(), description);
                table.row();
            }
        });
    }

    public void addGraphicTypeSetting(String key, float min, float max, int def, boolean integer, Boolp condition, Func<String, String> h, Seq<SharSetting> list){
        list.add(new SharSetting(key, def) {

            @Override
            public void add(Table table) {
                final String[] str = {""};
                Table table1 = new Table(t -> {
                    final float[] value = {def};
                    t.add(new Label(title + ": ")).left().padRight(5)
                            .update(a -> a.setColor(condition.get() ? Color.white : Color.gray));

                    t.field((integer ? String.valueOf(value[0]).split("[.]")[0] : value[0]) + str[0], s -> {
                                str[0] = h.get(s);
                                value[0] = s.isEmpty() ? def : Strings.parseFloat(s);

                                if(integer) settings.put(key, (int)value[0]);
                                else settings.put(key, value[0]);

                            }).update(a -> a.setDisabled(!condition.get()))
                            .valid(f -> Strings.canParsePositiveFloat(f) && Strings.parseFloat(f) >= min && Strings.parseFloat(f) <= max).width(120f).left();
                });

                addDesc(table.add(table1).left().padTop(4f).get(), description);
                table.row();
            }
        });
    }

    public void init(){
        BaseDialog dialog = new BaseDialog("UnitInfo Setting");
        dialog.addCloseButton();
        sharset = new SettingsMenuDialog.SettingsTable();
        dialog.cont.center().add(new Table(t -> t.pane(sharset).grow().row()));
        ui.settings.shown(() -> {
            Table settingUi = (Table)((Group)((Group)(ui.settings.getChildren().get(1))).getChildren().get(0)).getChildren().get(0); //This looks so stupid lol - lmfao
            settingUi.row();
            settingUi.button(bundle.get("setting.shar-title"), Styles.cleart, dialog::show);
        });


        Seq<Seq<SharSetting>> settingSeq = new Seq<>();
        Seq<SharSetting> tapSeq = new Seq<>();
        addGraphicSlideSetting("barstyle", 0, 0, 5, 1, s -> s == 0 ? "default bar" : s + "th bar", tapSeq);
        addGraphicSlideSetting("infoUiScale", mobile ? 50 : 75, 50, 100, 5, s -> s + "%", tapSeq);
        addGraphicSlideSetting("coreItemCheckRate", 60, 6, 180, 6, s -> Strings.fixed(s/60f,1) + "sec", tapSeq);
        addGraphicTypeSetting("wavemax", 0, 200,100, true, () -> true, s -> s + "waves", tapSeq);
        addGraphicCheckSetting("infoui", true, tapSeq);
        addGraphicCheckSetting("waveui", true, tapSeq);
        addGraphicCheckSetting("pastwave", false, tapSeq);
        addGraphicCheckSetting("emptywave", true, tapSeq);

        Seq<SharSetting> rangeSeq = new Seq<>();
        addGraphicTypeSetting("rangeRadius", 0, 500, 70, true, () -> true, s -> s + "tiles", rangeSeq);
        addGraphicCheckSetting("rangeNearby", true, rangeSeq);
        addGraphicCheckSetting("allTeamRange", false, rangeSeq);
        addGraphicCheckSetting("allTargetRange", false, rangeSeq);
        addGraphicCheckSetting("coreRange", false, rangeSeq);
        addGraphicCheckSetting("unitRange", false, rangeSeq);
        addGraphicCheckSetting("RangeShader", true, rangeSeq);

        Seq<SharSetting> opacitySeq = new Seq<>();
        addGraphicSlideSetting("selectopacity", 50, 0, 100, 5, s -> s + "%", opacitySeq);
        addGraphicSlideSetting("baropacity", 50, 0, 100, 5, s -> s + "%", opacitySeq);
        addGraphicSlideSetting("uiopacity", 50, 0, 100, 5, s -> s + "%", opacitySeq);
        addGraphicSlideSetting("softRangeOpacity", 60, 0, 100, 10, s -> s + "%", opacitySeq);

        Seq<SharSetting> drawSeq = new Seq<>();
        addGraphicTypeSetting("pathlinelimit", 0, 5000, 50, true, () -> true, s -> s + "lines", drawSeq);
        addGraphicTypeSetting("unitlinelimit", 0, 5000, 50, true, () -> true, s -> s + "lines", drawSeq);
        addGraphicTypeSetting("logiclinelimit", 0, 5000, 50, true, () -> true, s -> s + "lines", drawSeq);
        addGraphicTypeSetting("spawnarrowlimit", 0, 5000, 50, true, () -> true, s -> s + "arrows", drawSeq);
        addGraphicCheckSetting("gaycursor", false, drawSeq);
        addGraphicCheckSetting("unithealthui", true, drawSeq);
        addGraphicCheckSetting("linkedMass", true, drawSeq);
        addGraphicCheckSetting("linkedNode", false, drawSeq);
        addGraphicCheckSetting("select", true, drawSeq);
        addGraphicCheckSetting("deadTarget", false, drawSeq);
        addGraphicCheckSetting("distanceLine", true, drawSeq);
        addGraphicCheckSetting("spawnerarrow", true, drawSeq);

        Seq<SharSetting> etcSeq = new Seq<>();
        addGraphicCheckSetting("autoShooting", false, etcSeq);

        settingSeq.add(tapSeq, rangeSeq, opacitySeq);
        settingSeq.add(drawSeq, etcSeq);

        sharset.table(t -> {
            Seq<TextButton> buttons = new Seq<>();
            buttons.add(new TextButton(bundle.get("setting.shar-ui"), Styles.clearToggleMenut));
            buttons.add(new TextButton(bundle.get("setting.shar-range"), Styles.clearToggleMenut));
            buttons.add(new TextButton(bundle.get("setting.shar-opacity"), Styles.clearToggleMenut));
            buttons.add(new TextButton(bundle.get("setting.shar-draw"), Styles.clearToggleMenut));
            buttons.add(new TextButton(bundle.get("setting.shar-etc"), Styles.clearToggleMenut));
            buttons.each(b -> b.clicked(() -> buttons.each(b1 -> b1.setChecked(b1 == b))));
            t.table(Styles.black8, bt -> {
                bt.top().align(Align.top);
                buttons.each(b -> {
                    b.getLabel().setFontScale(0.85f);
                    bt.add(b).minHeight(60f * 0.85f).minWidth(150f * 0.85f).top();
                });
            }).grow().row();

            Stack stack = new Stack();
            for(int i = 0; i < settingSeq.size; i++){
                int finalI = i;
                stack.add(new Table(st -> {
                    for(SharSetting setting : settingSeq.get(finalI))
                        st.table(setting::add).left().row();

                    st.button(Core.bundle.get("settings.reset", "Reset to Defaults"), () -> {
                        settingSeq.get(finalI).each(s -> Core.settings.put(s.name, Core.settings.getDefault(s.name)));
                    }).margin(14.0f).width(240.0f).pad(6.0f);
                    st.visibility = () -> buttons.get(finalI).isChecked();
                    st.pack();
                }));
            }
            t.add(stack);
            t.fillParent = true;
        });
    }
}

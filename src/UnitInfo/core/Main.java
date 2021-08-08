package UnitInfo.core;

import UnitInfo.ui.*;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.*;
import mindustry.ai.types.BuilderAI;
import mindustry.ai.types.MinerAI;
import mindustry.content.*;
import mindustry.entities.units.AIController;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.mod.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;

import static UnitInfo.SVars.*;
import static arc.Core.*;
import static mindustry.Vars.*;

public class Main extends Mod {
    @Override
    public void init(){
        Events.on(ClientLoadEvent.class, e -> {
            hud = new HudUi();
            settingAdder.init();
            hud.addCoreTable();
            hud.addWaveTable();
            hud.addUnitTable();
            hud.addTileTable();
            hud.addTable();
            hud.setEvent();
            playerinfo.createFile();
            playerinfo.setEvent();
        });

        Events.on(WorldLoadEvent.class, e -> {
            hud = new HudUi();
            hud.addWaveTable();
            hud.addTileTable();
        });

        Events.on(WaveEvent.class, e -> {
            Vars.ui.hudGroup.removeChild(hud.waveTable);
            hud = new HudUi();
            hud.addWaveTable();
        });

        Events.run(Trigger.draw, () -> {
            if(settings.getBool("blockstatus")) Groups.build.each(build -> {
                if(Vars.player != null && Vars.player.team() == build.team) return;

                Block block = build.block;
                if(block.enableDrawStatus && block.consumes.any()){
                    float multiplier = block.size > 1 ? 1 : 0.64f;
                    float brcx = build.x + (block.size * tilesize / 2f) - (tilesize * multiplier / 2f);
                    float brcy = build.y - (block.size * tilesize / 2f) + (tilesize * multiplier / 2f);

                    Draw.z(Layer.power + 1);
                    Draw.color(Pal.gray);
                    Fill.square(brcx, brcy, 2.5f * multiplier, 45);
                    Draw.color(build.status().color);
                    Fill.square(brcx, brcy, 1.5f * multiplier, 45);
                    Draw.color();
                }
            });
            if(Core.settings.getBool("unithealthui"))
                Groups.unit.each(unit -> new FreeBar().draw(unit));

            if(Core.settings.getBool("scan")){
                float range = settings.getInt("rangemax") * 8f;
                for(Team team : Team.all)
                    indexer.eachBlock(team, Core.input.mouseWorldX(), Core.input.mouseWorldY(), range, b -> true, b -> new FreeBar().draw(b));
                Draw.color(Tmp.c1.set(Pal.accent).a(0.75f + Mathf.absin(3, 0.25f)));
                for(int i = 0; i < 4; i++)
                    Lines.swirl(Core.input.mouseWorldX(), Core.input.mouseWorldY(), range, 0.15f, 90 * i + Time.time % 360);
                Draw.reset();
            }

            if(!mobile && !Vars.state.isPaused() && settings.getBool("gaycursor"))
                Fx.mine.at(Core.input.mouseWorldX(), Core.input.mouseWorldY(), Tmp.c2.set(Color.red).shiftHue(Time.time * 1.5f));

            if(!renderer.pixelator.enabled()) Groups.unit.each(unit -> unit.item() != null && unit.itemTime > 0.01f, unit -> {
                Fonts.outline.draw(unit.stack.amount + "",
                    unit.x + Angles.trnsx(unit.rotation + 180f, unit.type.itemOffsetY),
                    unit.y + Angles.trnsy(unit.rotation + 180f, unit.type.itemOffsetY) - 3,
                    Pal.accent, 0.25f * unit.itemTime / Scl.scl(1f), false, Align.center);
                Draw.reset();
            });

            // Turret Ranges
            if(settings.getBool("rangeNearby") && player != null) {
                Team team = player.team();
                Unit unit = player.unit();
                Groups.build.each(e -> {
                    if(!settings.getBool("allTeamRange") && e.team == team) return; // Don't draw own turrets
                    if(!(e instanceof BaseTurret.BaseTurretBuild)) return; // Not a turret
                    if((e instanceof Turret.TurretBuild t && !t.hasAmmo()) || !e.cons.valid()) return; // No ammo

                    boolean canHit = e.block instanceof Turret t ? unit.isFlying() ? t.targetAir : t.targetGround :
                        e.block instanceof TractorBeamTurret tu && (unit.isFlying() ? tu.targetAir : tu.targetGround);
                    float range = ((BaseTurret.BaseTurretBuild) e).range();
                    float max = range + settings.getInt("rangeRadius") * tilesize + e.block.offset;
                    float dst = Mathf.dst(control.input.getMouseX(), control.input.getMouseY(), e.x, e.y);

                    if(control.input.block != null && dst <= max) canHit = e.block instanceof Turret t && t.targetGround;
                    if(player.dst(e) <= max || (control.input.block != null && dst <= max)) {
                        if(canHit || settings.getBool("allTargetRange")){
                            if(settings.getBool("softRangeDrawing")){
                                Lines.stroke(Scl.scl(), Tmp.c1.set(canHit ? e.team.color : Team.derelict.color).a(0.5f));
                                Lines.poly(e.x, e.y, Lines.circleVertices(range), range);
                                Fill.light(e.x, e.y, Lines.circleVertices(range), range, Color.clear, Tmp.c1.a(Mathf.clamp(1-((control.input.block != null && dst <= max ? dst : player.dst(e))/max), 0, settings.getInt("softRangeOpacity")/100f)));
                            }
                            else Drawf.dashCircle(e.x, e.y, range, canHit ? e.team.color : Team.derelict.color);
                        }
                    }
                });

                // Unit Ranges (Only works when turret ranges are enabled)
                if(settings.getBool("unitRange") || (settings.getBool("allTeamRange") && player.unit() != null)) {
                    Groups.unit.each(u -> {
                        if(!settings.getBool("unitRange") && settings.getBool("allTeamRange") && player.unit() != u) return; //player unit rule
                        if(!settings.getBool("allTeamRange") && u.team == team) return; // Don't draw own units
                        if(u.controller() instanceof AIController ai && (ai instanceof BuilderAI || ai instanceof MinerAI)) return; //don't draw poly and mono
                        boolean canHit = unit.isFlying() ? u.type.targetAir : u.type.targetGround;
                        float range = u.range();
                        float max = range + settings.getInt("rangeRadius") * tilesize;

                        if(Vars.player.dst(u) <= max) { // TODO: Store value of rangeRadius as an int, should increase performance
                            if (canHit || settings.getBool("allTargetRange")) // Same as above
                                if(settings.getBool("softRangeDrawing")){
                                    Lines.stroke(Scl.scl(), Tmp.c1.set(canHit ? u.team.color : Team.derelict.color).a(0.5f));
                                    Lines.poly(u.x, u.y, Lines.circleVertices(range), range);
                                    Fill.light(u.x, u.y, Lines.circleVertices(range), range, Color.clear, Tmp.c1.a(Math.min(settings.getInt("softRangeOpacity")/100f, 1-Vars.player.dst(u)/max)));
                                }
                                else Drawf.dashCircle(u.x, u.y, range, canHit ? u.team.color : Team.derelict.color);
                        }
                    });
                }
            }
            if(settings.getBool("coreRange") && player != null){
                state.teams.eachEnemyCore(player.team(), core -> {
                    if(Core.camera.bounds(Tmp.r1).overlaps(Tmp.r2.setCentered(core.x, core.y, state.rules.enemyCoreBuildRadius * 2f))){
                        Draw.color(Color.darkGray);
                        Lines.circle(core.x, core.y - 2, state.rules.enemyCoreBuildRadius);
                        Draw.color(Pal.accent, core.team.color, 0.5f + Mathf.absin(Time.time, 10f, 0.5f));
                        Lines.circle(core.x, core.y, state.rules.enemyCoreBuildRadius);
                    }
                });
            }
        });
    }
}

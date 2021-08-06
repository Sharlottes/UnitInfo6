package UnitInfo.ui;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.*;
import arc.scene.style.*;
import arc.scene.ui.layout.*;
import arc.util.pooling.*;
import mindustry.graphics.*;
import mindustry.ui.*;

public class SBar extends Element{
    static final Rect scissor = new Rect();

    Floatp fraction;
    String name = "";
    float value, lastValue, blink;
    final Color blinkColor = new Color();
    NinePatchDrawable bar, top;
    float spriteWidth;

    public SBar(Prov<String> name, Prov<Color> color, Floatp fraction){
        this.fraction = fraction;
        try{
            lastValue = value = Mathf.clamp(fraction.get());
        }catch(Exception e){ //getting the fraction may involve referring to invalid data
            lastValue = value = 0f;
        }
        update(() -> {
            try{
                this.name = name.get();
                this.blinkColor.set(color.get());
                setColor(color.get());
            }catch(Exception e){ //getting the fraction may involve referring to invalid data
                this.name = "";
            }
        });
        init();
    }


    public Drawable drawable(String name, int left, int right, int top, int bottom){
        Drawable out;

        TextureAtlas.AtlasRegion region = Core.atlas.find(name);

        int[] splits = {left, right, top, bottom};
        NinePatch patch = new NinePatch(region, splits[0], splits[1], splits[2], splits[3]);
        int[] pads = region.pads;
        if(pads != null) patch.setPadding(pads[0], pads[1], pads[2], pads[3]);
        out = new ScaledNinePatchDrawable(patch, 1f);

        return out;
    }

    public void init(){
        boolean ssim = Core.settings.getBool("ssim");
        boolean shar = Core.settings.getBool("shar");
        boolean shar1 = Core.settings.getBool("shar1");
        boolean shar2 = Core.settings.getBool("shar2");
        boolean shar3 = Core.settings.getBool("shar3");

        bar = (NinePatchDrawable) drawable("unitinfo6-barS", 10, 10, 9, 9);
        top = (NinePatchDrawable) drawable("unitinfo6-barS-top", 10, 10, 9, 9);
        spriteWidth = Core.atlas.find("unitinfo6-barS").width;
        if(ssim){
            bar = (NinePatchDrawable) drawable("unitinfo6-barSS", 14, 14, 19, 19);
            top = (NinePatchDrawable) drawable("unitinfo6-barSS-top", 14, 14, 19, 19);
            spriteWidth = Core.atlas.find("unitinfo6-barSS").width;
        }
        else if(shar){
            bar = (NinePatchDrawable) drawable("unitinfo6-barSSS", 25, 25, 17, 17);
            top = (NinePatchDrawable) drawable("unitinfo6-barSSS-top", 25, 25, 17, 17);
            spriteWidth = Core.atlas.find("unitinfo6-barSSS").width;
        }
        else if(shar1){
            bar = (NinePatchDrawable) drawable("unitinfo6-barSSSS", 25, 25, 17, 17);
            top = (NinePatchDrawable) drawable("unitinfo6-barSSSS-top", 25, 25, 17, 17);
            spriteWidth = Core.atlas.find("unitinfo6-barSSSS").width;
        }
        else if(shar2){
            bar = (NinePatchDrawable) drawable("unitinfo6-barSSSSS", 27, 27, 16, 16);
            top = (NinePatchDrawable) drawable("unitinfo6-barSSSSS-top", 27, 27, 16, 16);
            spriteWidth = Core.atlas.find("unitinfo6-barSSSSS").width;
        }
        else if(shar3){
            bar = (NinePatchDrawable) drawable("unitinfo6-barSSSSSS", 32, 32, 16, 16);
            top = (NinePatchDrawable) drawable("unitinfo6-barSSSSSS-top", 32, 32, 16, 16);
            spriteWidth = Core.atlas.find("unitinfo6-barSSSSSS").width;
        }
    }

    @Override
    public void draw(){
        if(fraction == null) return;
        float computed;
        try{
            computed = Mathf.clamp(fraction.get());
        }catch(Exception e){ //getting the fraction may involve referring to invalid data
            computed = 0f;
        }

        if(lastValue > computed){
            blink = 1f;
            lastValue = computed;
        }

        if(Float.isNaN(lastValue)) lastValue = 0;
        if(Float.isInfinite(lastValue)) lastValue = 1f;
        if(Float.isNaN(value)) value = 0;
        if(Float.isInfinite(value)) value = 1f;
        if(Float.isNaN(computed)) computed = 0;
        if(Float.isInfinite(computed)) computed = 1f;

        blink = Mathf.lerpDelta(blink, 0f, 0.2f);
        value = Mathf.lerpDelta(value, computed, 0.05f);

        Draw.colorl(0.1f);
        bar.draw(x, y, width, height);


        Draw.color(color.cpy().mul(Pal.lightishGray), blinkColor, blink);
        float topWidth = width * value;
        if(topWidth > spriteWidth){
            top.draw(x, y, topWidth, height);
        }else{
            if(ScissorStack.push(scissor.set(x, y, topWidth, height))){
                top.draw(x, y, spriteWidth, height);
                ScissorStack.pop();
            }
        }

        Draw.color(color, blinkColor, blink);
        float topWidthReal = width * (Math.min(value, computed));
        if(topWidthReal > spriteWidth){
            top.draw(x, y, topWidthReal, height);
        }else{
            if(ScissorStack.push(scissor.set(x, y, topWidthReal, height))){
                top.draw(x, y, spriteWidth, height);
                ScissorStack.pop();
            }
        }
        Draw.color();

        Font font = Fonts.outline;
        GlyphLayout lay = Pools.obtain(GlyphLayout.class, GlyphLayout::new);
        font.getData().setScale(Scl.scl());
        lay.setText(font, name);
        font.setColor(Color.white);
        font.draw(name, x + width / 2f - lay.width / 2f, y + height / 2f + lay.height / 2f + 1);
        font.getData().setScale(Scl.scl());

        Pools.free(lay);
        Draw.reset();
    }
}

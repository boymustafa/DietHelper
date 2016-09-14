package adapters;

import android.support.annotation.DrawableRes;

/**
 * Created by Boy Mustafa on 08/09/16.
 */
public class SpinnerItem {

    private final String text;

    @DrawableRes
    private final int drawable;

    private boolean isHint;

    public SpinnerItem(String text){
        this(text,0);
    }

    public SpinnerItem(String text, @DrawableRes int drawable){
        this(text,drawable,false);
    }

    public SpinnerItem(String text, @DrawableRes int drawable, boolean isHint) {
        this.text = text;
        this.drawable = drawable;
        this.isHint = isHint;
    }

    public String getText() {
        return text;
    }

    public int getDrawable() {
        return drawable;
    }

    public boolean isHint() {
        return isHint;
    }

    public void setHint(){
        isHint=true;
    }
}

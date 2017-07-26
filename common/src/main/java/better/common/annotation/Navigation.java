package better.common.annotation;

import android.support.annotation.ColorInt;
import android.support.annotation.MenuRes;
import android.support.annotation.StringRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * toolbar注解
 * Created by zhaoyu1 on 2017/7/26.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Navigation {
    /**
     * 标题
     *
     * @return
     */
    @StringRes int title() default -1;

    boolean back() default true;

    /**
     * 背景资源
     *
     * @return
     */
    int backgroundRes() default -1;

    /**
     * 标题文字颜色
     *
     * @return
     */
    @ColorInt int titleColor() default -1;

    /**
     * 菜单项
     *
     * @return
     */
    @MenuRes int menu() default -1;
}
